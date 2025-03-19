package com.ezequiel.itacademy.blackjack.exception;

import com.ezequiel.itacademy.blackjack.exception.error.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    //! Maneja GameNotFoundExcepction
    @ExceptionHandler(GameNotFoundExcepction.class)
    public ResponseEntity<ErrorMessage> handleGameNotFoundExcepction(GameNotFoundExcepction ex) {
        ErrorMessage error = new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    //! Maneja PlayerNotFoundExcepction
    @ExceptionHandler(PlayerNotFoundException.class)
    public ResponseEntity<ErrorMessage> handlePlayerNotFoundExcepction(PlayerNotFoundException ex) {
        ErrorMessage error = new ErrorMessage(HttpStatus.NOT_FOUND, ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    //! Maneja excepciones generales
    @ExceptionHandler(org.springframework.dao.DataAccessException.class)
    public ResponseEntity<ErrorMessage> handleDatabaseException(org.springframework.dao.DataAccessException ex) {
        ErrorMessage error = new ErrorMessage(HttpStatus.INTERNAL_SERVER_ERROR, "Error de base de datos: " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }
}