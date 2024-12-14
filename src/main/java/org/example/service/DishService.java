package org.example.service;

import org.example.job.SQLrequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DishService {
    @Autowired
    private SQLrequest sqLrequest;

    public List<Map<String, Object>> getDish(String dishIdIsNotNull, boolean isMenuNeed) {
        return  sqLrequest.getDish(dishIdIsNotNull, isMenuNeed);
    }
}
