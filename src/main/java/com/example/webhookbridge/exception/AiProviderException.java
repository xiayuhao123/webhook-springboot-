package com.example.webhookbridge.exception;

public class AiProviderException extends RuntimeException {

    private final int statusCode;
    private final String provider;

    public AiProviderException(String message, Throwable cause, int statusCode, String provider) {
        super(message, cause);
        this.statusCode = statusCode;
        this.provider = provider;
    }

    public AiProviderException(String message, int statusCode, String provider) {
        super(message);
        this.statusCode = statusCode;
        this.provider = provider;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getProvider() {
        return provider;
    }
}
