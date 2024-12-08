package org.example;

import org.example.job.SQLrequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.util.*;

import static org.example.job.checker.isAdmin;
import static org.example.job.checker.isAuth;

@Controller
public class MyPostControlleer {
    @Autowired
    private final SQLrequest sqLrequest;
    public MyPostControlleer(SQLrequest sqLrequest) {
        this.sqLrequest = sqLrequest;
    }
    @PostMapping("/register")
    public String postRegisterPage(Model model, Authentication authentication,
           @RequestParam String username, @RequestParam String password,
           @RequestParam String confirmPassword) {
        model.addAttribute("showLogout", isAuth(authentication));
        if(isAuth(authentication)) {
            model.addAttribute("name", authentication.getName());
        }
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Пароли не совпадают");
            return "register";
        }
        try {
            sqLrequest.putNewClient(username, password);
            return "redirect:/login";
        }
        catch (Exception e) {
            model.addAttribute("error", "Пользователь с таким именем уже существует");
            return "register";
        }
    }
    @PostMapping("/addToCart")
    public String addToCart(HttpServletRequest request, @RequestParam("dish_id") int dishId,
            @RequestParam("action") String action) {
        HttpSession session = request.getSession();
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");

        if (cart == null) {
            cart = new HashMap<>();
            session.setAttribute("cart", cart);
        }

        if (cart.containsKey(dishId)) {
            int count = cart.get(dishId);
            System.out.println(count);
            if (action.equals("inc")) {
                cart.put(dishId, count + 1);
                System.out.println("inc1");
            }
            else if (action.equals("dec") && count > 1){
                cart.put(dishId, count - 1);
                System.out.println("dec2");
            }
            else if (action.equals("dec") && count == 1) {
                cart.remove(dishId);
                System.out.println("dec1");
            }
        }
        else if (!action.equals("dec")){
            cart.put(dishId, 1);
        }

        return "redirect:/"; // Возврат на страницу меню
    }
    @PostMapping("/makeAnOrder")
    public String postMakeAnOrder(@RequestParam String address, Authentication authentication, HttpServletRequest request) {
        HttpSession session = request.getSession();
        Map<Integer, Integer> cart = (Map<Integer, Integer>) session.getAttribute("cart");
        sqLrequest.addNewOrder(cart, address, authentication);
        session.removeAttribute("cart");
        return "redirect:/";
    }
    @GetMapping("/filter")
    public String getEmpPage(@RequestParam String specialization, @RequestParam int minYears,
                             Model model, Authentication authentication) {
        List emps = sqLrequest.getEmpWithFilter(minYears, specialization);
        model.addAttribute("ADMIN", isAdmin(authentication));
        model.addAttribute("showLogout", isAuth(authentication));
        model.addAttribute("emps", emps);
        if(isAuth(authentication)) {
            model.addAttribute("name", authentication.getName());
        }
        return "emp";
    }
    @PostMapping("/changeStatus")
    public String changeOrderStatus(
            @RequestParam("orderId") Long orderId,
            @RequestParam("status") String status) {
        sqLrequest.updateOrderStatus(orderId, status);
        return "redirect:/order";
    }
    @PostMapping("/makeReview")
    public String makeReview(
            @RequestParam String comment, Authentication authentication) {
        sqLrequest.makeComment(comment, authentication);
        return "redirect:/review";
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
}