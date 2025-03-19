package com.ezequiel.itacademy.blackjack.exception.error;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private int status;
    private String message;

    public ErrorMessage(HttpStatus status, String message) {
        this.status = status.value();
        this.message = message;
    }
}