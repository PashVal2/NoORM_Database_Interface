package org.example.service;

import org.example.job.SQLrequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {
    @Autowired
    private SQLrequest sqLrequest;
    public void putNewClient(String username, String password) {
        sqLrequest.putNewClient(username, password);
    }
}
