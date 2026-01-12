import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame
import scala.io.StdIn.readLine

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Spark Scala Demo")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val df = readCsv(spark, "data/transactions_data.csv")

    val df2 = readCsv(spark, "data/cards_data.csv")

    val df3 = readCsv(spark, "data/users_data.csv")

    val df4 = readJsonMultiLine(spark, "data/mcc_codes.json")

    // val df5 = readJsonLines(spark, "data/train_fraud_labels.json")

    // df5.show()


    // spark.stop()
  }

  def readCsv(spark: SparkSession, path: String): DataFrame = {
    spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(path)
  }

  def readJsonLines(spark: SparkSession, path: String): DataFrame = {
    spark.read
      .option("multiline", "false")
      .option("inferSchema", "true")
      .option("mode", "DROPMALFORMED")
      .json(path)
  }

  def readJsonMultiLine(spark: SparkSession, path: String): DataFrame = {
    spark.read
      .option("multiline", "true") 
      .option("inferSchema", "true")
      .option("mode", "DROPMALFORMED")
      .json(path)
  }

  // Méthode générique pour compatibilité (défaut: multi-ligne)
  def readJson(spark: SparkSession, path: String): DataFrame = {
    readJsonMultiLine(spark, path)
  }

}
