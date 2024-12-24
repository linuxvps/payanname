package org.knn.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


@AllArgsConstructor
@Data
public class CashPrediction implements Serializable {
    @Serial
    private static final long serialVersionUID = 4595018540952157928L;
    private double actualCash;
    private double predictedCash;
}
