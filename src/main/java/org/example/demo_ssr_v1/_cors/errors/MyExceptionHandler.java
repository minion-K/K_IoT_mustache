package org.example.demo_ssr_v1._cors.errors;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.demo_ssr_v1._cors.errors.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

// @ControllerAdvice
// - 모든 컨트롤러에서 발생하는 예외를 해당 클래스에서 중앙 집중화 시킴
// @RestControllerAdvice
// - @ControllerAdvice + @ResponseBody
@ControllerAdvice
@Slf4j
public class MyExceptionHandler {
    // 지켜볼 예외를 명시하면 ControllerAdvice가 가지고와 처리함

    // 400 Bad Request
    @ExceptionHandler(Exception400.class)
    public String ex400(Exception400 e, HttpServletRequest request) {
        log.warn("=== 400 Bad Request ===");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        request.setAttribute("msg", e.getMessage());

        return "err/400";
    }

    // 401 UnAuthrized
//    @ExceptionHandler(Exception401.class)
//    public String ex401(Exception401 e, HttpServletRequest request) {
//        log.warn("=== 401 UnAuthorized ===");
//        log.warn("요청 URL: {}", request.getRequestURL());
//        log.warn("에러 메시지: {}", e.getMessage());
//        log.warn("예외 클래스: {}", e.getClass().getSimpleName());
//
//        request.setAttribute("msg", e.getMessage());
//
//        return "err/401";
//    }

    // 401 UnAuthrized
    @ExceptionHandler(Exception401.class)
    public ResponseEntity<String> ex401(Exception401 e, HttpServletRequest request) {
        String script = "<script>" +
                "if(confirm('"+e.getMessage()+"')) {" +
                "location.href = '/login';}" +
                "else { history.back()}"+
                "</script>";

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 403 Forbidden
//    @ExceptionHandler(Exception403.class)
//    public String ex403(Exception403 e, HttpServletRequest request) {
//        log.warn("=== 403 Forbidden ===");
//        log.warn("요청 URL: {}", request.getRequestURL());
//        log.warn("에러 메시지: {}", e.getMessage());
//        log.warn("예외 클래스: {}", e.getClass().getSimpleName());
//
//        request.setAttribute("msg", e.getMessage());
//
//        return "err/403";
//    }
    @ExceptionHandler(Exception403.class)
    @ResponseBody
    public ResponseEntity<String> ex403(Exception403 e, HttpServletRequest request) {
        String script = "<script>" +
                "alert('"+e.getMessage()+"');" +
                "history.back();"+
                "</script>";

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.TEXT_HTML)
                .body(script);
    }

    // 404 Not found
    @ExceptionHandler(Exception404.class)
    public String ex404(Exception404 e, HttpServletRequest request) {
        log.warn("=== 404 Not Found ===");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        request.setAttribute("msg", e.getMessage());

        return "err/404";
    }

    // 500 Internal Server
    @ExceptionHandler(Exception500.class)
    public String ex500(Exception500 e, HttpServletRequest request) {
        log.warn("=== 500 Internal Server Error ===");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        request.setAttribute("msg", e.getMessage());

        return "err/500";
    }

    // 기타 모든 실행 시점 오류 처리
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(Exception e, HttpServletRequest request) {
        log.warn("=== Runtime Exception");
        log.warn("요청 URL: {}", request.getRequestURL());
        log.warn("에러 메시지: {}", e.getMessage());
        log.warn("예외 클래스: {}", e.getClass().getSimpleName());

        request.setAttribute("msg", e.getMessage());

        return "err/500";
    }
}
