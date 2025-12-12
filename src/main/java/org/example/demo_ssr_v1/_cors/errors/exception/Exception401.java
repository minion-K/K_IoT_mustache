package org.example.demo_ssr_v1._cors.errors.exception;

/*
* 401 unAuthorized 인증처리 오류
* */
public class Exception401 extends RuntimeException {
    public Exception401(String msg) {
        super(msg);
    }

}
