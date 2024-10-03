package org.knn.src.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*", allowedHeaders = "*", maxAge = 3600)
public class trainTestPlotKnnController {

    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping("/predictions")
        public ResponseEntity<List<CashPrediction>> getPredictions() throws Exception {

        List<CashPrediction> predictions = new ArrayList<>();
        // تعریف ویژگی‌ها
        ArrayList<Attribute> attributes = new ArrayList<>();
        attributes.add(new Attribute("Location_Lat"));
        attributes.add(new Attribute("Location_Long"));
        attributes.add(new Attribute("Daily_Transactions"));
        attributes.add(new Attribute("Current_Cash"));

        Instances dataset = new Instances("ATM_Data", attributes, 0);
        dataset.setClassIndex(3);

        for (int i = 0; i < 100; i++) {
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
        }
        predictions.forEach(System.out::println);
        return ResponseEntity.status(HttpStatus.OK).body(predictions);
    }

    @GetMapping("hi")
    public String sayhi() {
        return "hi";
    }

}
