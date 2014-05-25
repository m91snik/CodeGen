/**
 * Copyright m91snik
 * Created on: 9/23/13
 * Created by: m91snik
 */
package com.m91snik.code_gen.test.generator.component.impl;

import com.m91snik.code_gen.test.generator.component.Calculator;
import org.springframework.stereotype.Service;

@Service
public class CalculatorImpl implements Calculator {

    @Override
    public int sum(int a, int b) {
        return a + b;
    }
}
