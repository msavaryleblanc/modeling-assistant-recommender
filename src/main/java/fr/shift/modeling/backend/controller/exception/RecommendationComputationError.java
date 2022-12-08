package fr.shift.modeling.backend.controller.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class RecommendationComputationError extends IllegalStateException{
    public RecommendationComputationError(String s) {
        super(s);
    }
}