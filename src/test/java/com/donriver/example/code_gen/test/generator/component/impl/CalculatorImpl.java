/**
 * Copyright DonRiver Inc. All Rights Reserved.
 * Created on: 9/23/13
 * Created by: Nikolay Garbuzov
 */
package com.donriver.example.code_gen.test.generator.component.impl;

import com.donriver.example.code_gen.test.generator.component.Calculator;

import javax.inject.Named;

@Named("calculator")
public class CalculatorImpl implements Calculator {

    @Override
    public int sum(int a, int b) {
        return a + b;
    }
}
