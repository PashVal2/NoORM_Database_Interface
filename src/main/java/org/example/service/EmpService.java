package org.example.service;

import org.example.job.SQLrequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpService {
    @Autowired
    private SQLrequest sqLrequest;
    public List getEmpWithFilter(int minYears, String specialization) {
        return sqLrequest.getEmpWithFilter(minYears, specialization);
    }

    public List getEmp() {
        return sqLrequest.getEmp();
    }
}
