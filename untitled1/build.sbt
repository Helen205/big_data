ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.19"

lazy val root = (project in file("."))
  .settings(
    name := "untitled1"
  )
// https://mvnrepository.com/artifact/org.apache.spark/spark-core
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.3.0"
// https://mvnrepository.com/artifact/org.apache.spark/spark-sql
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.0"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming" % "3.3.0",
  "org.apache.kafka" %% "kafka" % "3.3.0",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.3.0",
  "org.apache.spark" %% "spark-mllib" % "3.3.0"
)
// https://mvnrepository.com/artifact/org.apache.spark/spark-streaming
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.3.0"
name := "CreditCardFraudDetection"

version := "1.0"

scalaVersion := "2.12.15"
