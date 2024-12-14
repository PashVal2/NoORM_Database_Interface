package org.example.job;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SQLrequest {
    @PersistenceContext
    private EntityManager entityManager;
    private final PasswordEncoder passwordEncoder;
    public SQLrequest(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    @Transactional
    public List getDish(String condition, boolean isMenuNeed) {
        String sql = "SELECT dish_id, dish_price, dish_name, category, menu_id FROM dish WHERE " + condition;
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> dishes = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> dish = new HashMap<>();
            dish.put("dish_id", row[0]);
            dish.put("dish_price", row[1]);
            dish.put("dish_name", row[2]);
            dish.put("category", row[3]);

            if (isMenuNeed) {
                String sql1 = "Select menu_name, menu_time from menu WHERE menu_id = :param";
                Query query1  = entityManager.createNativeQuery(sql1);
                query1.setParameter("param", row[4]);
                Object[] resObj = (Object[]) query1.getSingleResult();
                dish.put("menu_name", resObj[0]);
                dish.put("menu_time", resObj[1]);
            }

            dishes.add(dish);
        }
        return dishes;
    }
    @Transactional
    public List<Map<String, Object>> getForOrderPage(String condition) {
        String sql = "SELECT order_id, employee_id, order_state, order_total_amount, address from orders WHERE " + condition;
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> orders = new ArrayList<>();

        for (Object[] row: results) {
            Map<String, Object> order = new HashMap<>();
            order.put("order_id", row[0]);
            order.put("order_state", row[2]);
            order.put("order_total_amount", row[3]);
            order.put("address", row[4]);

            String sql1 = "Select employee_full_name from employee WHERE employee_id = :param";
            Query query1  = entityManager.createNativeQuery(sql1);
            query1.setParameter("param",  row[1]);
            String empName = (String) query1.getSingleResult();
            order.put("empName", empName);

            orders.add(order);
        }
        return orders;
    }
    @Transactional
    public void putNewClient(String username, String password) {
        String sql = "call InsertClient(:name, :password, :role)";
        String encryptedPassword = passwordEncoder.encode(password);

        entityManager.createNativeQuery(sql)
                .setParameter("name", username)
                .setParameter("password", encryptedPassword)
                .setParameter("role", "USER")
                .executeUpdate();
    }
    @Transactional
    public List<Map<String, Object>> getEmp() {
        String sql = "SELECT employee_id, employee_full_name, employee_date, years, specialization FROM employee";
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> emps = new ArrayList<>();

        for (Object[] row: results) {
            Map<String, Object> order = new HashMap<>();
            order.put("employee_id", row[0]);
            order.put("employee_full_name", row[1]);
            order.put("employee_date", row[2]);
            order.put("years", row[3]);
            order.put("specialization", row[4]);

            emps.add(order);
        }
        return emps;
    }
    @Transactional
    public List<Map<String, Object>> getEmpWithFilter(int years, String specialization) {
        StringBuilder sql = new StringBuilder("SELECT employee_id, employee_full_name, employee_date, years, specialization FROM employee Where ");
        sql.append(" years >= :minYears");
        sql.append(" AND specialization = :specialization");
        Query query = entityManager.createNativeQuery(sql.toString());
        query.setParameter("minYears", years)
            .setParameter("specialization", specialization);

        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> emps = new ArrayList<>();

        for (Object[] row: results) {
            Map<String, Object> order = new HashMap<>();
            order.put("employee_id", row[0]);
            order.put("employee_full_name", row[1]);
            order.put("employee_date", row[2]);
            order.put("years", row[3]);
            order.put("specialization", row[4]);

            emps.add(order);
        }
        return emps;
    }
    @Transactional
    public void addNewOrder(Map<Integer, Integer> dishCount, String address, Authentication authentication) {
        // Получаем employee_id курьера
        String sql = "SELECT employee_id FROM employee WHERE specialization = 'courier' ORDER BY RAND() LIMIT 1";
        Query query = entityManager.createNativeQuery(sql);
        Object emp = query.getSingleResult();

        if (emp == null) {
            throw new RuntimeException("Нет курьеров");
        }

        // Преобразуем в Long для корректной работы с BIGINT
        int employeeId = (int) emp;

        // Получаем client_id, если пользователь аутентифицирован
        int clientId = 0;
        if (authentication != null && authentication.isAuthenticated()) {
            String sql3 = "SELECT client_id FROM client WHERE client_full_name = :param";
            Query query1 = entityManager.createNativeQuery(sql3);
            query1.setParameter("param", authentication.getName());

            // Преобразуем результат в Long
            Object result = query1.getSingleResult();
            if (result != null) {
                clientId = (int) result;
            }
        }

        // Вставляем новый заказ
        String sql2 = "INSERT INTO orders (employee_id, order_state, order_total_amount, address, client_id) VALUES (?, ?, ?, ?, ?)";
        Query insertOrderQuery = entityManager.createNativeQuery(sql2)
                .setParameter(1, employeeId)
                .setParameter(2, "pending")
                .setParameter(3, 0)
                .setParameter(5, null)
                .setParameter(4, address);

        // Если client_id не null, передаем его в запрос, иначе передаем null
        if (clientId != 0) {
            insertOrderQuery.setParameter(5, clientId);
        }

        insertOrderQuery.executeUpdate();

        // Получаем последний вставленный order_id
        Query lastInsertIdQuery = entityManager.createNativeQuery("SELECT LAST_INSERT_ID()");
        Long orderId = ((Number) lastInsertIdQuery.getSingleResult()).longValue();

        // Вставляем данные в таблицу order_dish
        String sqlOrderDish = "INSERT INTO order_dish (order_id, dish_id, count) VALUES (?, ?, ?)";
        for (Map.Entry<Integer, Integer> map : dishCount.entrySet()) {
            entityManager.createNativeQuery(sqlOrderDish)
                    .setParameter(1, orderId)
                    .setParameter(2, map.getKey()) // dish_id
                    .setParameter(3, map.getValue()) // dish_count
                    .executeUpdate();
        }
    }
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        String sql = "UPDATE orders SET order_state = :status WHERE order_id = :param";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("param", orderId)
            .setParameter("status", status);
        query.executeUpdate();
    }
    @Transactional
    public List<Map<String, Object>> getReview() {
        System.out.println("fdasdasdasdasdasdas");
        String sql = "SELECT review_id, comment, client_id FROM review";
        Query query = entityManager.createNativeQuery(sql);

        List<Object[]> results = query.getResultList();
        List<Map<String, Object>> reviews = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> review = new HashMap<>();
            review.put("review_id", row[0]);
            review.put("comment", row[1]);
            review.put("client_id", row[2]);
            reviews.add(review);
        }

        return reviews;
    }
    @Transactional
    public void deleteReview(int id) {
        String sql = "delete from review where review_id = :param";
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("param", id);
        query.executeUpdate();
    }
    @Transactional
    public void makeComment(String comment, Authentication authentication) {
        String sql3 = "SELECT client_id FROM client WHERE client_full_name = :param";
        Query query1 = entityManager.createNativeQuery(sql3);
        query1.setParameter("param", authentication.getName());

        int clientId = (int) query1.getSingleResult();

        // Вставляем новый заказ
        String sql2 = "INSERT INTO review (comment, client_id) VALUES (?, ?)";
        Query insertOrderQuery = entityManager.createNativeQuery(sql2)
                .setParameter(1, comment)
                .setParameter(2, clientId);
        insertOrderQuery.executeUpdate();
    }
}
