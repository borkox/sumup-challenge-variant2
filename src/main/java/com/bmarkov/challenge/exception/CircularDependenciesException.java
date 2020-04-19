package com.bmarkov.challenge.exception;

public class CircularDependenciesException extends JobException {
    public CircularDependenciesException(String message) {
        super(message);
    }

}
