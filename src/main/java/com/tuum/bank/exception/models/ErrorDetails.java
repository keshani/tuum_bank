package com.tuum.bank.exception.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetails {
    private Instant timestamp;
    private String message;
    private String details;
}
