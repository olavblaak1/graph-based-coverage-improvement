package com.kuleuven.MissingTestIdentification;

import com.kuleuven.TestMinimization.MinimizationMethod;

public class MissingTestIdentifier {

    WeightingMethod weightingMethod;

    public MissingTestIdentifier(MinimizationMethod method) {
        switch (method) {
            case STANDARD:
                break;
            default:
                break;
        }
    }
}
