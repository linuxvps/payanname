package org.knn;

import java.awt.Color;
import java.util.ArrayList;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

class TrainTestPlotKnnOOP {
    public static void main(String[] args) throws Exception {
        ATMDataProcessor dataProcessor = new ATMDataProcessor();
        Instances dataset = dataProcessor.createDataset();
        dataProcessor.addSimulatedData(dataset, 100);
        dataProcessor.randomizeData(dataset);

        Instances[] splitData = dataProcessor.splitData(dataset, 80);
        Instances trainSet = splitData[0];
        Instances testSet = splitData[1];

        KNNModel knnModel = new KNNModel(3);
        knnModel.train(trainSet);

        Plotter plotter = new Plotter();
        plotter.plotResults(knnModel, testSet);
    }
}

class ATMDataProcessor {
    public Instances createDataset() {
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Location_Lat"));
        attributes.add(new Attribute("Location_Long"));
        attributes.add(new Attribute("Daily_Transactions"));
        attributes.add(new Attribute("Current_Cash"));

        Instances dataset = new Instances("ATM_Data", attributes, 0);
        dataset.setClassIndex(3);
        return dataset;
    }

    public void addSimulatedData(Instances dataset, int numInstances) {
        for (int i = 0; i < numInstances; i++) {
            double[] atm = new double[4];
            atm[0] = 35.68 + Math.random() * 0.02;
            atm[1] = 51.38 + Math.random() * 0.02;
            atm[2] = 10000 + Math.random() * 50000;
            atm[3] = 50000 + Math.random() * 200000;
            dataset.add(new DenseInstance(1.0, atm));
        }
    }

    public void randomizeData(Instances dataset) throws Exception {
        Randomize randomize = new Randomize();
        randomize.setInputFormat(dataset);
        Filter.useFilter(dataset, randomize);
    }

    public Instances[] splitData(Instances dataset, double percentage) throws Exception {
        RemovePercentage removePercentage = new RemovePercentage();
        removePercentage.setPercentage(percentage);
        removePercentage.setInputFormat(dataset);
        Instances testSet = Filter.useFilter(dataset, removePercentage);

        removePercentage.setInvertSelection(true);
        Instances trainSet = Filter.useFilter(dataset, removePercentage);

        return new Instances[]{trainSet, testSet};
    }
}

class KNNModel {
    private IBk knn;

    public KNNModel(int k) {
        this.knn = new IBk();
        this.knn.setKNN(k);
    }

    public void train(Instances trainSet) throws Exception {
        knn.buildClassifier(trainSet);
    }

    public double classifyInstance(Instance instance) throws Exception {
        return knn.classifyInstance(instance);
    }
}

class Plotter {
    public void plotResults(KNNModel knnModel, Instances testSet) throws Exception {
        DefaultCategoryDataset datasetForGraph = new DefaultCategoryDataset();
        for (int i = 0; i < testSet.numInstances(); i++) {
            Instance testInstance = testSet.instance(i);
            double predictedCash = knnModel.classifyInstance(testInstance);
            double actualCash = testInstance.classValue();
            System.out.println("Actual Cash: " + actualCash + ", Predicted Cash: " + predictedCash);
            datasetForGraph.addValue(actualCash, "Actual Cash", "Instance " + (i + 1));
            datasetForGraph.addValue(predictedCash, "Predicted Cash", "Instance " + (i + 1));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Actual vs Predicted Cash",
                "Instance",
                "Cash",
                datasetForGraph,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE);
        renderer.setSeriesPaint(1, Color.RED);
        plot.setRenderer(renderer);

        ChartFrame frame = new ChartFrame("Actual vs Predicted Cash", chart);
        frame.pack();
        frame.setVisible(true);
    }
}
