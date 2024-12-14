package org.example;

import org.example.job.SQLrequest;
import org.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class JSController {
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private EmpService empService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private DishService dishService;
    // Получение всех отзывов
    @GetMapping("/review")
    public ResponseEntity<List<Map<String, Object>>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getReview());
    }
    @DeleteMapping("/review/{id}")
    public ResponseEntity<Map<String, String>> deleteReview(@PathVariable int id) {
        Map<String, String> response = new HashMap<>();
        reviewService.deleteReview(id);

        response.put("message", "Гнусный комментарий удален");
        return ResponseEntity.ok(response);
    }
}
