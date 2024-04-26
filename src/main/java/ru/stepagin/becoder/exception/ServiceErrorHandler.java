package ru.stepagin.becoder.exception;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;
import java.util.Objects;

@Slf4j
@ControllerAdvice
@Hidden
public class ServiceErrorHandler {

    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "Пользователь не авторизован")
    @ExceptionHandler(AccessDeniedException.class)
    public String handleException(final AccessDeniedException e, Model model, HttpServletRequest request) {
        log.error("AccessDeniedException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", "Доступ запрещен");
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleException(final MethodArgumentNotValidException e, Model model, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", Arrays.stream(Objects.requireNonNull(e.getDetailMessageArguments())).toList().get(1));
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidIdSuppliedException.class)
    public String handleException(final InvalidIdSuppliedException e, Model model, HttpServletRequest request) {
        log.error("InvalidIdSuppliedException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", e.getMessage());
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(LegalAccountNotFoundException.class)
    public String handleException(final LegalAccountNotFoundException e, Model model, HttpServletRequest request) {
        log.error("LegalAccountNotFoundException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", e.getMessage());
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleException(final IllegalArgumentException e, Model model, HttpServletRequest request) {
        log.error("IllegalArgumentException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", e.getMessage());
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleException(final DataIntegrityViolationException e, Model model, HttpServletRequest request) {
        log.error("DataIntegrityViolationException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", "Переданы некорректные данные: " + e.getMessage());
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ValidationException.class)
    public String handleException(final ValidationException e, Model model, HttpServletRequest request) {
        log.error("ValidationException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", e.getMessage());
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleException(final NoResourceFoundException e, Model model, HttpServletRequest request) {
        log.error("NoResourceFoundException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", e.getMessage());
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String handleException(final HttpRequestMethodNotSupportedException e, Model model, HttpServletRequest request) {
        log.error("HttpRequestMethodNotSupportedException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", "Данный тип запроса не поддерживается.");
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public String handleException(final HttpMessageNotReadableException e, Model model, HttpServletRequest request) {
        log.error("HttpMessageNotReadableException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", "Переданы некорректные данные: " + e.getMessage());
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(UnsupportedOperationException.class)
    public String handleException(final UnsupportedOperationException e, Model model, HttpServletRequest request) {
        log.error("UnsupportedOperationException: {}", e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", e.getMessage());
        model.addAttribute("revertAddress", referer);

        return "error";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public String handleException(final RuntimeException e, Model model, HttpServletRequest request) {
        log.error("Runtime Exception ({}): {}", e.getClass(), e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", "Произошла ошибка на стороне сервера");
        model.addAttribute("revertAddress", referer);
        return "error";
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(final Exception e, Model model, HttpServletRequest request) {
        log.error("Common Exception ({}): {}", e.getClass(), e.getMessage());
        String referer = request.getHeader("Referer");
        model.addAttribute("errorText", "Идеального сервера не существует. Где-то спряталась ошибка.");
        model.addAttribute("revertAddress", referer);
        return "error";
    }

}

