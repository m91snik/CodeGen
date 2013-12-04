/**
 * Copyright m91snik
 * Created on: 9/21/13
 * Created by: m91snik
 */
package com.m91snik.code_gen.test.generator.protocol;

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
