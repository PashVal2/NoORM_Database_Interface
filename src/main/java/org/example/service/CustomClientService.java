package org.example.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomClientService implements UserDetailsService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            Map<String, Object> client = getUserByName(username);
            if (client.isEmpty()) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            return User.builder()
                    .username((String) client.get("client_full_name"))
                    .password((String) client.get("client_password"))
                    .roles((String) client.get("client_role"))
                    .build();
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
    public Map<String, Object> getUserByName(String name) {
        String sql = "SELECT client_id, client_full_name, client_password, client_role from client where client_full_name = :param";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("param", name);

        Object[] row = (Object[]) query.getSingleResult();
        Map<String, Object> client = new HashMap<>();
        client.put("client_id", row[0]);
        client.put("client_full_name", row[1]);
        client.put("client_password", row[2]);
        client.put("client_role", row[3]);
        return client;
    }
}
