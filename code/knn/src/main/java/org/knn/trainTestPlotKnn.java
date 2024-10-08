package org.knn;

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

import java.awt.*;
import java.util.ArrayList;

public class trainTestPlotKnn {
    public static void main(String[] args) throws Exception {
        // تعریف ویژگی‌ها (Attributes)
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Location_Lat"));     // Latitude
        attributes.add(new Attribute("Location_Long"));    // Longitude
        attributes.add(new Attribute("Daily_Transactions"));  // تعداد تراکنش‌های روزانه
        attributes.add(new Attribute("Current_Cash"));     // مقدار نقدینگی فعلی

        // ایجاد دیتاست با ویژگی‌ها
        Instances dataset = new Instances("ATM_Data", attributes, 0);
        dataset.setClassIndex(3);  // مشخص کردن ستون هدف (Current_Cash)

        // افزودن داده‌های شبیه‌سازی‌شده برای خودپردازها
        int numInstances = 100;
        for (int i = 0; i < numInstances; i++) {
            double[] atm = new double[4];
            atm[0] = 35.68 + Math.random() * 0.02; // Random latitude
            atm[1] = 51.38 + Math.random() * 0.02; // Random longitude
            atm[2] = 10000 + Math.random() * 50000; // Random daily transactions
            atm[3] = 50000 + Math.random() * 200000; // Random current cash
            dataset.add(new DenseInstance(1.0, atm));
        }

        // مرحله 1: مخلوط کردن داده‌ها به‌صورت تصادفی (اختیاری اما توصیه می‌شود)
        Randomize randomize = new Randomize();
        randomize.setInputFormat(dataset);
        dataset = Filter.useFilter(dataset, randomize);

        // مرحله 2: تقسیم داده‌ها به 80% آموزش و 20% تست
        RemovePercentage removePercentage = new RemovePercentage();
        removePercentage.setPercentage(80);
        removePercentage.setInputFormat(dataset);

        // 20% داده‌ها برای تست
        Instances testSet = Filter.useFilter(dataset, removePercentage);

        // برعکس کردن برای گرفتن 80% داده‌ها برای آموزش
        removePercentage.setInvertSelection(true);
        Instances trainSet = Filter.useFilter(dataset, removePercentage);

        // ساخت و آموزش مدل KNN با استفاده از IBk
        IBk knn = new IBk();
        knn.setKNN(3);  // تنظیم مقدار K

        // آموزش مدل KNN
        knn.buildClassifier(trainSet);

        // ارزیابی مدل بر روی داده‌های تست
        DefaultCategoryDataset datasetForGraph = new DefaultCategoryDataset();
        for (int i = 0; i < testSet.numInstances(); i++) {
            Instance testInstance = testSet.instance(i);
            double predictedCash = knn.classifyInstance(testInstance);
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

        // Get the CategoryPlot instance
        CategoryPlot plot = (CategoryPlot) chart.getPlot();

        // Customize the chart appearance
        plot.setBackgroundPaint(Color.WHITE); // Set background color to white
        plot.setOutlineVisible(false); // Remove border

        // Create a custom renderer to display different colors for actual and predicted cash
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.BLUE); // Actual cash (blue)
        renderer.setSeriesPaint(1, Color.RED); // Predicted cash (red)
        plot.setRenderer(renderer);

        // نمایش نمودار
        ChartFrame frame = new ChartFrame("Actual vs Predicted Cash", chart);
        frame.pack();
        frame.setVisible(true);
    }
}