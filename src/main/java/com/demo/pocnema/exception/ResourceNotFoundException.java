package com.demo.pocnema.exception;


import com.demo.pocnema.service.ResourceNotFoundExceptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException implements ResourceNotFoundExceptionService {

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s : '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public Object getFieldValue() {
        return fieldValue;
    }
}
