package com.meli.challenge.exception;

/**
 * Base exception for MercadoLibre API related errors.
 */
public class MeliApiException extends RuntimeException {
    private final int statusCode;

    public MeliApiException(String message, int statusCode) {
        super(message + " (Status: " + statusCode + ")");
        this.statusCode = statusCode;
    }

    public MeliApiException(String message, int statusCode, Throwable cause) {
        super(message + " (Status: " + statusCode + ")", cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
