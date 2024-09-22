package org.knn.src;

import weka.classifiers.lazy.IBk;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.instance.Randomize;
import weka.filters.unsupervised.instance.RemovePercentage;

import java.util.ArrayList;

public class trianTestKnn {
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
        double[] atm4 = {35.7000, 51.3500, 40000, 250000};
        double[] atm5 = {35.7200, 51.3900, 10000, 80000};

        dataset.add(new DenseInstance(1.0, atm1));
        dataset.add(new DenseInstance(1.0, atm2));
        dataset.add(new DenseInstance(1.0, atm3));
        dataset.add(new DenseInstance(1.0, atm4));
        dataset.add(new DenseInstance(1.0, atm5));

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
        for (int i = 0; i < testSet.numInstances(); i++) {
            Instance testInstance = testSet.instance(i);
            double predictedCash = knn.classifyInstance(testInstance);
            double actualCash = testInstance.classValue();
            System.out.println("Actual Cash: " + actualCash + ", Predicted Cash: " + predictedCash);
        }
    }
}
