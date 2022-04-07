package com.satisfactory.production.models;

import lombok.Data;

@Data
public class Error {
    private int status;
    private String message;
}
