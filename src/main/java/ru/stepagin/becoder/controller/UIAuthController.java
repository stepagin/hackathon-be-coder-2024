package ru.stepagin.becoder.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.stepagin.becoder.DTO.RegistrationDTO;

import static org.springframework.http.HttpStatus.OK;

@Controller
@CrossOrigin
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UIAuthController {

    private final AuthController authController;

    @GetMapping("/start")
    public String start(Model model) {
        model.addAttribute("registerDTO", new RegistrationDTO());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Validated @ModelAttribute RegistrationDTO data, HttpServletRequest request, Model model) {
        ResponseEntity<?> responseEntity = authController.register(data);
        if (responseEntity.getStatusCode().equals(OK)) {
            return "success_registration";
        } else {
            String referer = request.getHeader("Referer");
            model.addAttribute("errorMessage", "Не удалось создать аккаунт: " + responseEntity.getBody());
            return "redirect:" + referer;
        }
    }
}
