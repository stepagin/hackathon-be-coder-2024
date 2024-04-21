package ru.stepagin.becoder.exception;

public class LegalAccountNotFoundException extends RuntimeException {
    public LegalAccountNotFoundException(final String message) {
        super(message);
    }
}
