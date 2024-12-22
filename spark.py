from pyspark.sql import SparkSession
from pyspark.sql.types import StructType, StructField, FloatType

# SparkSession başlatmak ve Kafka bağımlılığını yüklemek
spark = SparkSession.builder \
    .appName("RealTimeAnomalyDetection") \
    .config("spark.jars.packages", "org.apache.spark:spark-sql-kafka-0-10_2.12:3.5.3") \
    .getOrCreate()

# Schema tanımlaması (dataframe'in doğru formatta olması için)
schema = StructType([
    StructField("V1", FloatType()),
    StructField("V6", FloatType()),
    StructField("V8", FloatType()),
    StructField("V13", FloatType()),
    StructField("V15", FloatType()),
    StructField("V19", FloatType()),
    StructField("V20", FloatType()),
    StructField("V21", FloatType()),
    StructField("V23", FloatType()),
    StructField("V24", FloatType()),
    StructField("V25", FloatType()),
    StructField("V26", FloatType()),
    StructField("V27", FloatType()),
    StructField("V28", FloatType()),
    StructField("log_amount", FloatType()),
    StructField("Class", FloatType())
])

# Kafka'dan veri okuma
input_stream = spark.readStream \
    .format("kafka") \
    .option("kafka.bootstrap.servers", "localhost:9092") \
    .option("subscribe", "input_topic") \
    .load()

# Kafka verisini çözümle (value'yı STRING formatında almak)
json_data = input_stream.selectExpr("CAST(value AS STRING) as value")

# JSON verisini çözümleme
from pyspark.sql.functions import from_json
parsed_data = json_data.select(from_json("value", schema).alias("data"))

# Veriyi düzleştir (data sütunundaki her bir alt sütunu alıyoruz)
final_data = parsed_data.select("data.*")

# Anomali sınıflandırma işlemi veya diğer işlemler burada yapılabilir
final_data.show()  # Bu örnek, veriyi yalnızca göstermek için
