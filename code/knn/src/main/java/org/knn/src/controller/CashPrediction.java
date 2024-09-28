package org.knn.src.controller;

import java.io.Serial;
import java.io.Serializable;

public class CashPrediction implements Serializable {
    @Serial
    private static final long serialVersionUID = 4595018540952157928L;
    private double actualCash;
    private double predictedCash;

    public CashPrediction(double actualCash, double predictedCash) {
        this.actualCash = actualCash;
        this.predictedCash = predictedCash;
    }

    public double getActualCash() {
        return actualCash;
    }

    public double getPredictedCash() {
        return predictedCash;
    }
}
