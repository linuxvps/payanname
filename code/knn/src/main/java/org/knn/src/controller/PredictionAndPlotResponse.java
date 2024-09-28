package org.knn.src.controller;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class PredictionAndPlotResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -674419265506455671L;
    private List<CashPrediction> predictions;
    private String plotImage;

    public PredictionAndPlotResponse(List<CashPrediction> predictions, String plotImage) {
        this.predictions = predictions;
        this.plotImage = plotImage;
    }

    public List<CashPrediction> getPredictions() {
        return predictions;
    }

    public void setPredictions(List<CashPrediction> predictions) {
        this.predictions = predictions;
    }

    public String getPlotImage() {
        return plotImage;
    }

    public void setPlotImage(String plotImage) {
        this.plotImage = plotImage;
    }
}
