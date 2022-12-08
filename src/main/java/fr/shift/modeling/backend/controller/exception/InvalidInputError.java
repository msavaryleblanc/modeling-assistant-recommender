package fr.shift.modeling.backend.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidInputError extends IllegalStateException{
    public InvalidInputError(String s) {
        super(s);
    }
}