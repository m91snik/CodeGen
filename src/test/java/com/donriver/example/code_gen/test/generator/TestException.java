/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 9/21/13
 * Created by: Nikolay Garbuzov
 */
package com.donriver.example.code_gen.test.generator;

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
