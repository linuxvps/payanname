package org.knn.src.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@Data
public class PredictionAndPlotResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -674419265506455671L;
    private List<CashPrediction> predictions;
    private String plotImage;



}
