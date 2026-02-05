package com.salessavvy.app.user.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.salessavvy.app.common.entity.Cart_Items;
import com.salessavvy.app.common.entity.Order;
import com.salessavvy.app.common.entity.OrderItem;
import com.salessavvy.app.common.entity.OrderStatus;
import com.salessavvy.app.user.repository.CartRepository;
import com.salessavvy.app.user.repository.OrderItemRepository;
import com.salessavvy.app.user.repository.OrderRepository;
import com.salessavvy.app.user.service.PaymentServiceContract;

import jakarta.transaction.Transactional;

@Service
public class PaymentService implements PaymentServiceContract {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;

    private static final BigDecimal SHIPPING_CHARGE = BigDecimal.valueOf(50);

    public PaymentService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
    }

    // ================= CREATE ORDER =================
    @Override
    @Transactional
    public String createOrder(int userId, BigDecimal frontendTotal, List<OrderItem> cartItems)
            throws RazorpayException {

       
        BigDecimal subtotal = cartItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //Final total = subtotal + shipping
        BigDecimal finalTotal = subtotal.add(SHIPPING_CHARGE);

        //Convert to paise
        int paiseAmount = finalTotal
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .intValueExact();

        RazorpayClient razorpayClient =
                new RazorpayClient(razorpayKeyId, razorpayKeySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", paiseAmount);
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());

        com.razorpay.Order razorpayOrder =
                razorpayClient.orders.create(orderRequest);

        // Save order
        Order order = new Order();
        order.setOrderId(razorpayOrder.get("id"));
        order.setUserId(userId);
        order.setTotalAmount(finalTotal);
        order.setStatus(OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        orderRepository.save(order);

        return razorpayOrder.get("id");
    }

    // ================= VERIFY PAYMENT =================
    @Override
    @Transactional
    public boolean verifyPayment(
            String razorpayOrderId,
            String razorpayPaymentId,
            String razorpaySignature,
            int userId) {

        try {
            JSONObject attributes = new JSONObject();
            attributes.put("razorpay_order_id", razorpayOrderId);
            attributes.put("razorpay_payment_id", razorpayPaymentId);
            attributes.put("razorpay_signature", razorpaySignature);

            boolean isValid =
                    com.razorpay.Utils.verifyPaymentSignature(
                            attributes, razorpayKeySecret);

            if (!isValid) return false;

            Order order = orderRepository.findById(razorpayOrderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));

            order.setStatus(OrderStatus.SUCCESS);
            order.setUpdatedAt(LocalDateTime.now());
            orderRepository.save(order);

            List<Cart_Items> cartItems =
                    cartRepository.findCartItemsWithProductDetails(userId);

            for (Cart_Items cartItem : cartItems) {
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProductId(cartItem.getProduct().getProductId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPricePerUnit(cartItem.getProduct().getPrice());
                orderItem.setTotalPrice(
                        cartItem.getProduct().getPrice()
                                .multiply(BigDecimal.valueOf(cartItem.getQuantity()))
                );
                orderItemRepository.save(orderItem);
            }

            cartRepository.deleteAllCartItemsByUserId(userId);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}