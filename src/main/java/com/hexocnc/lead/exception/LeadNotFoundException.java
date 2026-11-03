package com.hexocnc.lead.exception;

public class LeadNotFoundException extends RuntimeException {

    public LeadNotFoundException(final String message) {
        super(message);
    }
}
