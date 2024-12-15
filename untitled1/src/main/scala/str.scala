import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka010._
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.classification.RandomForestClassificationModel

object str {

  def main(args: Array[String]): Unit = {

    // SparkSession başlatma
    val spark = SparkSession.builder
      .appName("CreditCardFraudDetectionStreaming")
      .config("spark.master", "local[*]")
      .getOrCreate()

    import spark.implicits._

    // Spark StreamingContext başlatma
    val ssc = new StreamingContext(spark.sparkContext, Seconds(10))

    // Kafka yapılandırması
    val kafkaParams = Map(
      "bootstrap.servers" -> "localhost:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "credit-card-group",
      "auto.offset.reset" -> "latest"
    )

    // Kafka'dan okunan topic
    val topics = Array("credit_card_data")

    // Kafka Stream oluşturma
    val stream = KafkaUtils.createDirectStream[String, String](
      ssc,
      LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](topics, kafkaParams)
    )

    // Eğitilmiş Random Forest modelini yükleme
    val modelPath = "C:\\Users\\Helen\\Desktop\\python\\big data\\random_forest_model" // Eğitilmiş model yolu
    val rfModel = RandomForestClassificationModel.load(modelPath)

    // Giriş özelliklerini birleştiren assembler
    val featureColumns = Array("V1", "V6", "V8", "V13", "V15", "V19", "V20", "V21",
      "V23", "V24", "V25", "V26", "V27", "V28", "year",
      "month", "day", "hour", "weekday", "log_amount")

    val assembler = new VectorAssembler()
      .setInputCols(featureColumns)
      .setOutputCol("features")

    // Kafka'dan gelen veriyi işleme
    stream.foreachRDD { rdd =>
      if (!rdd.isEmpty()) {
        // JSON formatında Kafka verisini DataFrame'e dönüştürme
        val kafkaData = rdd.map(_.value).toDF("value")
        val jsonData = spark.read.json(kafkaData.select("value").rdd.map(_.getString(0)))

        // Veriyi işleme
        if (jsonData.columns.contains("Class")) {
          val processedData = assembler.transform(jsonData)

          // Model ile tahmin yapma
          val predictions: DataFrame = rfModel.transform(processedData)

          // Anomali tespiti sonuçlarını gösterme
          println("Tahmin Sonuçları:")
          predictions.select("Class", "prediction").show(false)
        } else {
          println("Uygun formatta veri bulunamadı.")
        }
      }
    }

    // Streaming başlatma
    ssc.start()
    ssc.awaitTermination()
  }
}
