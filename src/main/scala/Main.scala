import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.DataFrame
import scala.io.StdIn.readLine
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.StringType 

object Main {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Spark Scala Demo")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    // 1. Chargement des données
    val df = readCsv(spark, "data/transactions_data.csv")
    df.printSchema()

    // val df2 = readCsv(spark, "data/cards_data.csv")

    // val df3 = readCsv(spark, "data/users_data.csv")

    val df4 = readJsonMultiLine(spark, "data/mcc_codes.json")

    // val df5 = readJsonLines(spark, "data/train_fraud_labels.json")

    // 2. Analyse de volumétrie 
    
    // println("Nombre de transactions: " + df.count())
    // println("Nombre de clients uniques: " + df.select("client_id").distinct().count())
    // println("Nombre de cartes uniques: " + df.select("card_id").distinct().count())
    // println("Nombre de commercants uniques: " + df.select("merchant_id").distinct().count())

    //3. Qualité des données

    // val nullCounts = df.columns.map { colName =>
    //   (colName, df.filter(col(colName).isNull).count())
    // }.filter(_._2 > 0)

    // println("Colonnes avec valeurs nulles:")
    // nullCounts.foreach { case (name, count) =>
    //   println(s"  $name: $count valeurs nulles")
    // }

    // println("Transactions avec montant ≤ 0: " + df.filter(col("amount").cast("double") <= 0).count())

    //   df.select("amount").show(10)
    //   df.select(min("amount"), max("amount")).show()

    // println("Transactions avec montant ≤ 0: " + 
    // df.filter(
    //   regexp_replace(col("amount"), "[\\$ ]", "").cast("double") <= 0
    // ).count())

    // println("Transactions sans mcc: " + df.filter(col("mcc").isNull or (col("mcc") === 0)).count())

    // println("Transactions avec erreurs: " + df.filter(col("errors").isNotNull).count())


    // Tableau récapitulatif des valeurs manquantes

    // val totalRows = df.count()
    // val missingStats = df.columns.map { colName =>
    //   val nullCount = df.filter(col(colName).isNull).count()
    //   val percentage = if (totalRows > 0) (nullCount.toDouble / totalRows * 100) else 0.0
    //   (colName, nullCount, percentage)
    // }
    
    // println("\n" + "="*80)
    // println("TABLEAU RÉCAPITULATIF DES VALEURS MANQUANTES")
    // println("="*80)
    // println(f"${"Colonne"}%-30s ${"Nombre"}%15s ${"Pourcentage (%)"}%20s")
    // println("-"*80)
    // missingStats.foreach { case (colName, count, pct) =>
    //   println(f"${colName}%-30s ${count}%15d ${pct}%19.2f%%")
    // }
    // println("="*80)
    // println(s"Total de lignes: $totalRows")
    // println()




    // PARTIE 2 – Analyse des montants & comportements

  // //4. Analyse des montants
  //    df.select(
  //       sum(regexp_replace(col("amount"), "[\\$ ]", "").cast("double")).as("somme_totale"),
  //       avg(regexp_replace(col("amount"), "[\\$ ]", "").cast("double")).as("moyenne"),
  //       min(regexp_replace(col("amount"), "[\\$ ]", "").cast("double")).as("minimum"),
  //       max(regexp_replace(col("amount"), "[\\$ ]", "").cast("double")).as("maximum"),
  //       count("amount").as("nombre_transactions")
  //     ).show()


    //5. Analyse temporelle

    // val dfWithDate = df.withColumn("date_timestamp", to_timestamp(col("date"), "yyyy-MM-dd HH:mm:ss"))
    
    // // Extraire l'heure, le jour et le mois
    // val dfWithExtracted = dfWithDate
    //   .withColumn("heure", hour(col("date_timestamp")))
    //   .withColumn("jour", dayofmonth(col("date_timestamp")))
    //   .withColumn("mois", month(col("date_timestamp")))
    //   .withColumn("jour_semaine", date_format(col("date_timestamp"), "EEEE")) // Nom du jour en français
    //   .withColumn("jour_semaine_num", dayofweek(col("date_timestamp"))) // 1=Dimanche, 2=Lundi, etc.
    
    // // Afficher quelques exemples avec les colonnes extraites
    // println("\n=== Exemples avec date extraite ===")
    // dfWithExtracted.select("date", "heure", "jour", "mois", "jour_semaine", "jour_semaine_num").show(10)
    
    // // Nombre de transactions par heure
    // println("\n=== Nombre de transactions par heure ===")
    // dfWithExtracted
    //   .groupBy("heure")
    //   .agg(count("*").as("nombre_transactions"))
    //   .orderBy("heure")
    //   .show(24)
    
    // // Nombre de transactions par jour de la semaine
    // println("\n=== Nombre de transactions par jour de la semaine ===")
    // dfWithExtracted
    //   .groupBy("jour_semaine", "jour_semaine_num")
    //   .agg(count("*").as("nombre_transactions"))
    //   .orderBy("jour_semaine_num")
    //   .show()

  //6. Jointure avec les MCC

    val mccExploded = df4.columns.map { mccCode =>
      (mccCode, df4.select(col(s"`$mccCode`")).first().getString(0))
    }.toSeq.toDF("mcc_code", "merchant_category")

    val jointure = df.join(mccExploded, df.col("mcc").cast("string") === mccExploded.col("mcc_code"), "left")
      .drop("mcc_code")

    jointure.select("mcc", "merchant_category").show(10)

    // Top 10 des catégories par volume
    println("\n=== Top 10 catégories par volume ===")
    jointure
      .groupBy("merchant_category")
      .agg(count("*").as("nombre_transactions"))
      .orderBy(desc("nombre_transactions"))
      .limit(10)
      .show(false)

    // Montant moyen par catégorie
    println("\n=== Montant moyen par catégorie ===")
    jointure
      .withColumn("amount_clean", regexp_replace(col("amount"), "[\\$ ]", "").cast("double"))
      .groupBy("merchant_category")
      .agg(
        round(avg("amount_clean"), 2).as("montant_moyen"),
        count("*").as("nombre_transactions")
      )
      .orderBy(desc("montant_moyen"))
      .show(false)



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
      .option("multiline", "true")
      .option("inferSchema", "true")
      .option("samplingRatio", "0.01") 
      .option("mode", "PERMISSIVE")
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
