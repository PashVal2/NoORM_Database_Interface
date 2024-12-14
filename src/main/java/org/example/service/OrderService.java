package org.example.service;

import org.example.job.SQLrequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    @Autowired
    private SQLrequest sqLrequest;
    public void updateOrderStatus(Long orderId, String status) {
        sqLrequest.updateOrderStatus(orderId, status);
    }

    public void addNewOrder(Map<Integer, Integer> cart, String address, Authentication authentication) {
        sqLrequest.addNewOrder(cart, address, authentication);
    }

    public List getForOrderPage(String orderIdIsNotNull) {
        return sqLrequest.getForOrderPage(orderIdIsNotNull);
    }
}
