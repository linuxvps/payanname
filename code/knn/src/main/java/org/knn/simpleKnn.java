package org.knn;

import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import java.util.ArrayList;

public class simpleKnn {
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
        double[] atm1 = {35.6892, 51.3890, 50000, 200000};
        double[] atm2 = {35.6824, 51.4258, 30000, 300000};
        double[] atm3 = {35.7036, 51.3311, 20000, 100000};

        dataset.add(new DenseInstance(1.0, atm1));
        dataset.add(new DenseInstance(1.0, atm2));
        dataset.add(new DenseInstance(1.0, atm3));

        // ساخت و آموزش مدل KNN با استفاده از IBk (K-Nearest Neighbors)
        IBk knn = new IBk();

        // تنظیم مقدار K (مثلاً K = 3)
        knn.setKNN(3);

        // آموزش مدل KNN
        knn.buildClassifier(dataset);

        // داده‌های جدید برای پیش‌بینی
        double[] newAtmData = {35.7000, 51.4000, 25000, 0};  // مقدار 0 برای "Current_Cash" که باید پیش‌بینی شود
        Instance newATM = new DenseInstance(1.0, newAtmData);
        newATM.setDataset(dataset);

        // پیش‌بینی مقدار نقدینگی لازم برای خودپرداز جدید
        double predictedCash = knn.classifyInstance(newATM);
        System.out.println("Predicted Cash for new ATM: " + predictedCash);
    }
}
