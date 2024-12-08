package org.example;

import org.example.job.SQLrequest;
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
    private final SQLrequest sqLrequest;
    public JSController(SQLrequest sqLrequest) {
        this.sqLrequest = sqLrequest;
    }
    // Получение всех отзывов
    @GetMapping("/review")
    public ResponseEntity<List<Map<String, Object>>> getAllReviews() {
        return ResponseEntity.ok(sqLrequest.getReview());
    }
    @DeleteMapping("/review/{id}")
    public ResponseEntity<Map<String, String>> deleteReview(@PathVariable int id) {
        Map<String, String> response = new HashMap<>();
        sqLrequest.deleteReview(id);

        response.put("message", "Гнусный комментарий удален");
        return ResponseEntity.ok(response);
    }
}
