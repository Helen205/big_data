import org.apache.kafka.clients.producer.{KafkaProducer, ProducerConfig, ProducerRecord}
import java.util.Properties
import scala.io.Source

object producer {

  def main(args: Array[String]): Unit = {
    // Kafka Producer için yapılandırma
    val props = new Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092") // Kafka Broker Adresi
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer")

    // Kafka Producer nesnesi oluşturma
    val producer = new KafkaProducer[String, String](props)

    val topic = "credit_card_data" // Kafka topic adı
    val filePath = "C:\\Users\\Helen\\Desktop\\python\\big data\\standardized_data.csv" // Göndermek istediğiniz dosyanın yolu

    try {
      // Dosyayı satır satır okuma ve Kafka'ya gönderme
      val bufferedSource = Source.fromFile(filePath)
      for (line <- bufferedSource.getLines) {
        val record = new ProducerRecord[String, String](topic, "key", line)
        producer.send(record)
        println(s"Sent: $line")
      }
      bufferedSource.close()
    } catch {
      case ex: Exception => println(s"Error occurred: ${ex.getMessage}")
    } finally {
      producer.close() // Kafka producer'ı güvenli kapatma
      println("Producer closed.")
    }
  }
}
