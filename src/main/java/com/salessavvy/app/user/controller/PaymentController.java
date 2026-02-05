package com.salessavvy.app.user.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.razorpay.RazorpayException;
import com.salessavvy.app.common.entity.OrderItem;
import com.salessavvy.app.common.entity.User;
import com.salessavvy.app.user.service.PaymentServiceContract;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentServiceContract paymentService;

    public PaymentController(PaymentServiceContract paymentService) {
        this.paymentService = paymentService;
    }

    // ================= CREATE PAYMENT =================
    @PostMapping("/create")
    public ResponseEntity<String> createPaymentOrder(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {

        try {
            User user = (User) request.getAttribute("authenticatedUser");
            if (user == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> cartItemsRaw =
                    (List<Map<String, Object>>) requestBody.get("cartItems");

            List<OrderItem> cartItems = cartItemsRaw.stream().map(item -> {
                OrderItem oi = new OrderItem();

                int qty = Integer.parseInt(item.get("quantity").toString());
                BigDecimal price =
                        new BigDecimal(item.get("price").toString());

                oi.setProductId(Integer.parseInt(item.get("productId").toString()));
                oi.setQuantity(qty);
                oi.setPricePerUnit(price);
                oi.setTotalPrice(price.multiply(BigDecimal.valueOf(qty)));
                return oi;
            }).collect(Collectors.toList());

            // frontend total is ignored for security
            String razorpayOrderId =
                    paymentService.createOrder(
                            user.getUserId(), BigDecimal.ZERO, cartItems);

            return ResponseEntity.ok(razorpayOrderId);

        } catch (RazorpayException e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Razorpay error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid payment request: " + e.getMessage());
        }
    }

    // ================= VERIFY PAYMENT =================
    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(
            @RequestBody Map<String, Object> requestBody,
            HttpServletRequest request) {

        try {
            User user = (User) request.getAttribute("authenticatedUser");
            if (user == null) {
                return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body("User not authenticated");
            }

            boolean verified =
                    paymentService.verifyPayment(
                            requestBody.get("razorpayOrderId").toString(),
                            requestBody.get("razorpayPaymentId").toString(),
                            requestBody.get("razorpaySignature").toString(),
                            user.getUserId());

            return verified
                    ? ResponseEntity.ok("Payment verified successfully")
                    : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Payment verification failed");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error verifying payment: " + e.getMessage());
        }
    }
}