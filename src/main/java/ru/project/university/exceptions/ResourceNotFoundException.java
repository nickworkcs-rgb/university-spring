package ru.project.university.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public static ResourceNotFoundException forEntity(String entityName, Long id) {
        return new ResourceNotFoundException(entityName + " with id " + id + " not found");
    }
}
