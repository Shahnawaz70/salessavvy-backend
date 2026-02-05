package com.salessavvy.app.user.service.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.salessavvy.app.common.entity.Order;
import com.salessavvy.app.common.entity.OrderItem;
import com.salessavvy.app.user.repository.OrderItemRepository;
import com.salessavvy.app.user.repository.OrderRepository;
import com.salessavvy.app.user.repository.ProductRepository;
import com.salessavvy.app.user.service.AdminBusinessServiceContract;

@Service
public class AdminBusinessService implements AdminBusinessServiceContract {
	private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    
	
	public AdminBusinessService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
			ProductRepository productRepository) {
		super();
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.productRepository = productRepository;
	}

	@Override
	public Map<String, Object> calculateMonthlyBusiness(int month, int year) {
		List<Order> successfulOrders = orderRepository.findSuccessfulOrdersByMonthAndYear(month, year);
        return calculateBusinessMetrics(successfulOrders);
	}

	@Override
	public Map<String, Object> calculateDailyBusiness(LocalDate date) {
		List<Order> successfulOrders = orderRepository.findSuccessfulOrdersByDate(date);
        return calculateBusinessMetrics(successfulOrders);
	}

	@Override
	public Map<String, Object> calculateYearlyBusiness(int year) {
		List<Order> successfulOrders = orderRepository.findSuccessfulOrdersByYear(year);
        return calculateBusinessMetrics(successfulOrders);
	}

	@Override
	public Map<String, Object> calculateOverallBusiness() {
		List<Order> successfulOrders = orderRepository.findAllByStatusForOverAllBusiness();
		return calculateBusinessMetrics(successfulOrders);
	}
	
	private Map<String, Object> calculateBusinessMetrics(List<Order> orders) {
        double totalRevenue = 0.0;
        Map<String, Integer> categorySales = new HashMap<>();

        for (Order order : orders) {
            totalRevenue += order.getTotalAmount().doubleValue();

            List<OrderItem> items = orderItemRepository.findByOrderId(order.getOrderId());
            for (OrderItem item : items) {
                String categoryName = productRepository.findCategoryNameByProductId(item.getProductId());
                categorySales.put(categoryName, categorySales.getOrDefault(categoryName, 0) + item.getQuantity());
            }
        }

        Map<String, Object> metrics = new HashMap<>();
        metrics.put("totalRevenue", totalRevenue);
        metrics.put("categorySales", categorySales);
        return metrics;
    }

}
