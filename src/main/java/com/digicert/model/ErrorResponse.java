package com.digicert.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private int statusCode;
    private String message;
    private String field;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.field="NA";
    }

    public void addError(String field, String message) {
        this.statusCode = 400;
        this.field = field;
        this.message = message;
    }
}
