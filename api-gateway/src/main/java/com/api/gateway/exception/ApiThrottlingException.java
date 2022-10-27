package com.api.gateway.exception;

import lombok.experimental.StandardException;

@StandardException
public class ApiThrottlingException extends RuntimeException {}
