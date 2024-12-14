package org.example;

import org.example.job.SQLrequest;
import org.example.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

import static org.example.job.checker.isAdmin;
import static org.example.job.checker.isAuth;

@Controller
public class IndexController {
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
    @GetMapping("/")
    public String getIndexPage(Model model, Authentication authentication, HttpServletRequest request) {
        List<Map<String, Object>> dishes = dishService.getDish("dish_id is not null", true);
        model.addAttribute("showLogout", isAuth(authentication));
        model.addAttribute("ADMIN", isAdmin(authentication));
        model.addAttribute("dishes", dishes);
        if(isAuth(authentication)) {
            model.addAttribute("name", authentication.getName());
        }
        HttpSession session = request.getSession();
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart == null) {
            cart = new HashMap<>();
        }
        model.addAttribute("cart", cart);
        return "index";
    }

    @GetMapping("/order")
    public String getOrderPage(Model model, Authentication authentication) {
        List orders = orderService.getForOrderPage("order_id is not null");
        model.addAttribute("ADMIN", isAdmin(authentication));
        model.addAttribute("showLogout", isAuth(authentication));
        model.addAttribute("orders", orders);
        if(isAuth(authentication)) {
            model.addAttribute("name", authentication.getName());
        }
        return "order";
    }
    @GetMapping("/emp")
    public String getEmpPage(Model model, Authentication authentication) {

        List emps = empService.getEmp();
        model.addAttribute("ADMIN", isAdmin(authentication));
        model.addAttribute("showLogout", isAuth(authentication));
        model.addAttribute("emps", emps);
        if(isAuth(authentication)) {
            model.addAttribute("name", authentication.getName());
        }
        return "emp";
    }
    @GetMapping("/register")
    public String getRegisterPage(Model model, Authentication authentication) {
        model.addAttribute("ADMIN", isAdmin(authentication));
        model.addAttribute("showLogout", isAuth(authentication));
        if(isAuth(authentication)) {
            model.addAttribute("name", authentication.getName());
        }
        return "register";
    }
    @GetMapping("/login")
    public String getLoginPage(Model model, Authentication authentication) {
        model.addAttribute("showLogout", isAuth(authentication));
        model.addAttribute("ADMIN", isAdmin(authentication));
        if(isAuth(authentication)) {
            model.addAttribute("name", authentication.getName());
        }
        return "login";
    }
    @GetMapping("/cart")
    public String viewCart(HttpServletRequest request, Model model, Authentication authentication) {
        model.addAttribute("ADMIN", isAdmin(authentication));
        model.addAttribute("showLogout", isAuth(authentication));
        if(isAuth(authentication)) {
            model.addAttribute("name", authentication.getName());
        }

        HttpSession session = request.getSession();
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            model.addAttribute("message", "Корзина пуста.");
            return "cart";
        }

        StringBuilder condition = new StringBuilder("dish_id IN (");
        int i = 0;
        for (Integer key : cart.keySet()) {
            condition.append(key);
            if (i < cart.size() - 1) {
                condition.append(", ");
            }
            i++;
        }
        condition.append(")");

        List<Map<String, Object>> dishes = dishService.getDish(condition.toString(), false);
        model.addAttribute("dishes", dishes);
        model.addAttribute("cart", cart);
        return "cart";
    }
    @GetMapping("/review")
    public String reviewsPage(Model model, Authentication authentication) {
        model.addAttribute("ADMIN", isAdmin(authentication));
        model.addAttribute("showLogout", isAuth(authentication));
        if(isAuth(authentication)) {
            model.addAttribute("name", authentication.getName());
        }
        return "review";
    }
    @GetMapping("/filter")
    public String getEmpPage(@RequestParam String specialization, @RequestParam int minYears,
                             Model model, Authentication authentication) {
        List emps = empService.getEmpWithFilter(minYears, specialization);
        model.addAttribute("ADMIN", isAdmin(authentication));
        model.addAttribute("showLogout", isAuth(authentication));
        model.addAttribute("emps", emps);
        if(isAuth(authentication)) {
            model.addAttribute("name", authentication.getName());
        }
        return "emp";
    }
}

