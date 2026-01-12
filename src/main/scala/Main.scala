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

    // Charger les 3 fichiers CSV avec schéma inféré
    println("=" * 60)
    println("TRANSACTIONS DATA (CSV)")
    println("=" * 60)
    val dfTransactions = readCsv(spark, "data/transactions_data.csv")
    dfTransactions.printSchema()
    dfTransactions.show(10, truncate = false)

    println("=" * 60)
    println("CARDS DATA (CSV)")
    println("=" * 60)
    val dfCards = readCsv(spark, "data/cards_data.csv")
    dfCards.printSchema()
    dfCards.show(10, truncate = false)

    println("=" * 60)
    println("USERS DATA (CSV)")
    println("=" * 60)
    val dfUsers = readCsv(spark, "data/users_data.csv")
    dfUsers.printSchema()
    dfUsers.show(10, truncate = false)

    // Charger les 2 fichiers JSON
    println("=" * 60)
    println("MCC CODES (JSON)")
    println("=" * 60)
    val dfMccCodes = readJsonMultiLine(spark, "data/mcc_codes.json")
    dfMccCodes.printSchema()
    dfMccCodes.show(10, truncate = false)

    println("=" * 60)
    println("TRAIN FRAUD LABELS (JSON)")
    println("=" * 60)
    val dfFraudLabels = readJsonLines(spark, "data/train_fraud_labels.json")
    dfFraudLabels.printSchema()
    dfFraudLabels.show(10, truncate = false)

    spark.stop()
  }

  def readCsv(spark: SparkSession, path: String, limit: Int = 10): DataFrame = {
    return spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(path).limit(limit)
  }

  def readJsonLines(spark: SparkSession, path: String, limit: Int = 10): DataFrame = {
    return spark.read
      .option("multiline", "false")
      .option("inferSchema", "true")
      .option("mode", "DROPMALFORMED")
      .json(path).limit(limit)
  }

  def readJsonMultiLine(spark: SparkSession, path: String, limit: Int = 10): DataFrame = {
    return spark.read
      .option("multiline", "true") 
      .option("inferSchema", "true")
      .option("mode", "DROPMALFORMED")
      .json(path).limit(limit)
  }

  // Méthode générique pour compatibilité (défaut: multi-ligne)
  def readJson(spark: SparkSession, path: String, limit: Int = 10): DataFrame = {
    return readJsonMultiLine(spark, path, limit)
  }

}
