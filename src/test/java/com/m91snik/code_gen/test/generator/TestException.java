/**
 * Copyright m91snik
 * Created on: 9/21/13
 * Created by: m91snik
 */
package com.m91snik.code_gen.test.generator;

public class TestException extends Exception {

    public TestException() {
    }

    public TestException(String message) {
        super(message);
    }

    public TestException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestException(Throwable cause) {
        super(cause);
    }
}
