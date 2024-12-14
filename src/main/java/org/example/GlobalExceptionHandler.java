package org.example;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)  // Обрабатываем все исключения
    public String handleException(Exception ex, Model model) {
        // Логируем исключение, если нужно
        model.addAttribute("error", ex.getMessage());  // Добавляем ошибку в модель
        return "error";  // Переадресовываем на страницу error.html
    }
}