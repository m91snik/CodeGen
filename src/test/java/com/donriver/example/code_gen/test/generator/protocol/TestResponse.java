/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 9/21/13
 * Created by: Nikolay Garbuzov
 */
package com.donriver.example.code_gen.test.generator.protocol;

public class TestResponse {
    public int anInt;

    public TestResponse() {
    }

    public TestResponse(int anInt) {
        this.anInt = anInt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestResponse that = (TestResponse) o;

        if (anInt != that.anInt) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return anInt;
    }
}
