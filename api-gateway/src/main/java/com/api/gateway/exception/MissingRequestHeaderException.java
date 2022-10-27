package com.api.gateway.exception;

import lombok.experimental.StandardException;

@StandardException
public class MissingRequestHeaderException extends RuntimeException {}
