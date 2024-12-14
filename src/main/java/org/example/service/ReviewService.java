package org.example.service;

import org.example.job.SQLrequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ReviewService {
    @Autowired
    private SQLrequest sqLrequest;
    public void makeComment(String comment, Authentication authentication) {
        sqLrequest.makeComment(comment, authentication);
    }

    public List<Map<String, Object>> getReview() {
        return sqLrequest.getReview();
    }

    public void deleteReview(int id) {
        sqLrequest.deleteReview(id);
    }
}
