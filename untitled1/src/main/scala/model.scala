import org.apache.spark.sql.SparkSession
import org.apache.spark.ml.classification.RandomForestClassifier
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator

object model {

  def main(args: Array[String]): Unit = {
    // Spark oturumunu başlat
    val spark = SparkSession.builder
      .appName("Credit Card Fraud Detection")
      .config("spark.master", "local[*]") // Spark local modda çalışıyor
      .getOrCreate()

    // Veri yükleme
    val dataPath = "C:\\Users\\Helen\\Desktop\\python\\big data\\standardized_data.csv" // Veri setinin yolu
    val data = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(dataPath)

    // Veri setini gözlemleme
    println("Veri setinin ilk 5 satırı:")
    data.show(5)

    // Giriş özelliklerini (features) birleştirme
    val featureColumns = Array("V1", "V6", "V8", "V13", "V15", "V19", "V20", "V21", "V23",
      "V24", "V25", "V26", "V27", "V28", "log_amount")

    val assembler = new VectorAssembler()
      .setInputCols(featureColumns)
      .setOutputCol("features")

    val assembledData = assembler.transform(data)

    // Veriyi eğitim ve test setlerine ayırma
    val Array(trainingData, testData) = assembledData.randomSplit(Array(0.8, 0.2), seed = 12345)

    // Random Forest sınıflandırıcısını tanımlama
    val rf = new RandomForestClassifier()
      .setLabelCol("Class") // Sınıf sütunu hedef değişken
      .setFeaturesCol("features") // Özellik sütunu
      .setNumTrees(100) // Ağaç sayısını belirleme
      .setMaxDepth(10) // Maksimum derinlik
      .setSeed(12345) // Rastgelelik için sabit seed

    // Modeli eğitim verisi üzerinde eğitme
    val rfModel = rf.fit(trainingData)

    //val modelPath = "C:/Users/Helen/Desktop/python/big data/random_forest_model"
    //rfModel.save(modelPath) // Modeli belirtilen dizine kaydet
    //println(s"Model başarıyla kaydedildi: $modelPath")
    // Test verisi üzerinde tahmin yapma
    val predictions = rfModel.transform(testData)

    // Model performansını değerlendirme
    val evaluator = new MulticlassClassificationEvaluator()
      .setLabelCol("Class")
      .setPredictionCol("prediction")
      .setMetricName("accuracy")


    val accuracy = evaluator.evaluate(predictions)

    println(s"Model Accuracy: $accuracy")
    val tp = predictions.filter("Class == 1 AND prediction == 1").count()
    val fp = predictions.filter("Class == 0 AND prediction == 1").count()
    val tn = predictions.filter("Class == 0 AND prediction == 0").count()
    val fn = predictions.filter("Class == 1 AND prediction == 0").count()

    val precision = tp.toDouble / (tp + fp).toDouble
    val recall = tp.toDouble / (tp + fn).toDouble
    val f1Score = 2 * (precision * recall) / (precision + recall)

    println(s"Precision: $precision")
    println(s"Recall: $recall")
    println(s"F1 Score: $f1Score")

    // Tahmin sonuçlarını gözlemleme
    println("Tahmin Sonuçları:")
    predictions.select("Class", "prediction", "features").show(10)


    // Yeni veri setini kaydetme
    val predictionsPath = "C:/Users/Helen/Desktop/python/big data/predictions.csv"
    predictions.select("Class", "prediction", "features").write
      .option("header", "true")
      .mode("overwrite") // Mevcut dosyanın üzerine yaz
      .csv(predictionsPath)  // Yeni tahmin edilen veriyi CSV olarak kaydet
    println(s"Yeni veri seti başarıyla kaydedildi: $predictionsPath")

    // Spark oturumunu kapatma
    spark.stop()
  }
}
