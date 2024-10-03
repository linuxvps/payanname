package org.knn.src.controller;

import org.apache.tomcat.util.codec.binary.Base64;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class trainTestPlotKnnController {

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @PostMapping("/predictions")
        public ResponseEntity<List<CashPrediction>> getPredictions(@RequestBody int numInstances) throws Exception {
        // لیستی برای نگهداری پیش‌بینی‌ها
        List<CashPrediction> predictions = new ArrayList<>();
        DefaultCategoryDataset datasetForGraph = new DefaultCategoryDataset();

        // تعریف ویژگی‌ها
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Location_Lat"));
        attributes.add(new Attribute("Location_Long"));
        attributes.add(new Attribute("Daily_Transactions"));
        attributes.add(new Attribute("Current_Cash"));

        Instances dataset = new Instances("ATM_Data", attributes, 0);
        dataset.setClassIndex(3);

        for (int i = 0; i < numInstances; i++) {
            double[] atm = new double[4];
            atm[0] = 35.68 + Math.random() * 0.02;
            atm[1] = 51.38 + Math.random() * 0.02;
            atm[2] = 10000 + Math.random() * 50000;
            atm[3] = 50000 + Math.random() * 200000;
            dataset.add(new DenseInstance(1.0, atm));
        }

        Randomize randomize = new Randomize();
        randomize.setInputFormat(dataset);
        dataset = Filter.useFilter(dataset, randomize);

        RemovePercentage removePercentage = new RemovePercentage();
        removePercentage.setPercentage(80);
        removePercentage.setInputFormat(dataset);

        Instances testSet = Filter.useFilter(dataset, removePercentage);
        removePercentage.setInvertSelection(true);
        Instances trainSet = Filter.useFilter(dataset, removePercentage);

        IBk knn = new IBk();
        knn.setKNN(3);
        knn.buildClassifier(trainSet);

        for (int i = 0; i < testSet.numInstances(); i++) {
            Instance testInstance = testSet.instance(i);
            double predictedCash = Math.ceil(knn.classifyInstance(testInstance));
            double actualCash = Math.ceil(testInstance.classValue());
            predictions.add(new CashPrediction(actualCash, predictedCash));

            // اضافه کردن داده به دیتاست نمودار
            datasetForGraph.addValue(actualCash, "Actual Cash", "Instance " + (i + 1));
            datasetForGraph.addValue(predictedCash, "Predicted Cash", "Instance " + (i + 1));
        }

        // ایجاد نمودار
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

        // تبدیل نمودار به تصویر Base64
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedImage chartImage = chart.createBufferedImage(800, 600);
        ImageIO.write(chartImage, "png", byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String base64Image = Base64.encodeBase64String(imageBytes);

        // بازگشت داده‌ها و نمودار به صورت Base64
        // return new PredictionAndPlotResponse(predictions, base64Image);
        predictions.forEach(System.out::println);
        return ResponseEntity.status(HttpStatus.OK).body(predictions);
    }


    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/predictions/chart")
    public ResponseEntity<StreamingResponseBody> getPredictionsChart() throws Exception {
        System.out.println("Generating chart...");

        DefaultCategoryDataset datasetForGraph = new DefaultCategoryDataset();

        // Assuming predictions are fetched in a similar manner
        List<CashPrediction> predictions = new ArrayList<>();
        for (int i = 0; i < predictions.size(); i++) {
            CashPrediction prediction = predictions.get(i);
            datasetForGraph.addValue(prediction.getActualCash(), "Actual Cash", "Instance " + (i + 1));
            datasetForGraph.addValue(prediction.getPredictedCash(), "Predicted Cash", "Instance " + (i + 1));
        }

        // Create chart
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

        // Prepare StreamingResponseBody to stream the image
        StreamingResponseBody stream = outputStream -> {
            BufferedImage chartImage = chart.createBufferedImage(800, 600);
            ImageIO.write(chartImage, "png", outputStream);
            outputStream.flush();
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.set(HttpHeaders.TRANSFER_ENCODING, "chunked");  // Explicitly setting chunked transfer encoding

        System.out.println("Streaming chart...");
        return new ResponseEntity<>(stream, headers, HttpStatus.OK);
    }

    @GetMapping("hi")
    public String sayhi() {
        return "hi";
    }

}
