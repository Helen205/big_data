import org.apache.kafka.clients.consumer.{KafkaConsumer, ConsumerConfig}
import java.util.Properties
import scala.collection.JavaConverters._

object consumer {

  def main(args: Array[String]): Unit = {
    // Kafka Consumer yapılandırması
    val props = new Properties()
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Kafka Broker adresi
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "credit-card-group")       // Consumer Group ID
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer")

    // Kafka Consumer nesnesi oluşturma
    val consumer = new KafkaConsumer[String, String](props)
    consumer.subscribe(List("credit_card_data").asJava) // Kafka topic'e abone olma

    println("Kafka Consumer çalışıyor...")

    try {
      while (true) {
        val records = consumer.poll(1000) // Her 1 saniyede veri kontrolü yap
        for (record <- records.asScala) {
          // Gelen veriyi işleme
          println(s"Key: ${record.key()}, Value: ${record.value()}, Partition: ${record.partition()}, Offset: ${record.offset()}")
        }
      }
    } catch {
      case ex: Exception => println(s"Hata oluştu: ${ex.getMessage}")
    } finally {
      consumer.close() // Consumer'ı güvenli bir şekilde kapatma
      println("Kafka Consumer kapatıldı.")
    }
  }
}
