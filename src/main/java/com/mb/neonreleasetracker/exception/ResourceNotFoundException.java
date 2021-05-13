package com.mb.neonreleasetracker.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(final String id) {
        super("No release with id [" + id + "] found");
    }
}
