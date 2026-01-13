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
    // RÉPONSE Q1 - Nombre de colonnes par fichier:
    //   - transactions_data.csv: 12 colonnes
    //   - cards_data.csv: 9 colonnes
    //   - users_data.csv: 11 colonnes
    //   - mcc_codes.json: ~300 colonnes (1 colonne par code MCC)
    //   - train_fraud_labels.json: 2 colonnes (id, is_fraud)
    //
    // Types suspects:
    //   - transactions: amount (String avec "$"), zip (Double au lieu de String)
    //   - cards: credit_limit (String avec "$"), acct_open_date (String au lieu de Date)
    //   - users: yearly_income/total_debt/per_capita_income (String avec "$")
    //   - mcc_codes: structure inversée (codes en colonnes au lieu de lignes)
    println("\n" + "="*80)
    println("QUESTION 1 : CHARGEMENT DES DONNÉES")
    println("Questions : Combien de colonnes par fichier ? Quels types de données semblent incorrects ou suspects ?")
    println("="*80)

    // Chargement des 3 CSV
    val df = readCsv(spark, "data/transactions_data.csv")
    val df2 = readCsv(spark, "data/cards_data.csv")
    val df3 = readCsv(spark, "data/users_data.csv")

    // Chargement des 2 JSON
    val df4 = readJsonMultiLine(spark, "data/mcc_codes.json")
    val df5 = readJsonLines(spark, "data/train_fraud_labels.json")

    // Affichage schéma et 10 premières lignes pour chaque fichier
    println("\n--- transactions_data.csv (" + df.columns.length + " colonnes) ---")
    df.printSchema()
    df.show(10, truncate = false)

    println("\n--- cards_data.csv (" + df2.columns.length + " colonnes) ---")
    df2.printSchema()
    df2.show(10, truncate = false)

    println("\n--- users_data.csv (" + df3.columns.length + " colonnes) ---")
    df3.printSchema()
    df3.show(10, truncate = false)

    println("\n--- mcc_codes.json (" + df4.columns.length + " colonnes) ---")
    df4.printSchema()
    df4.show(10, truncate = false)

    println("\n--- train_fraud_labels.json (" + df5.columns.length + " colonnes) ---")
    df5.printSchema()
    df5.show(10, truncate = false)

    // 2. Analyse de volumétrie
    // RÉPONSE Q2: Les commerçants génèrent le plus de lignes (74 831 uniques)
    // Ratio: ~10 923 transactions/client, ~3 268 tx/carte, ~178 tx/commerçant
    // Interprétation: Chaque client possède plusieurs cartes, chaque carte fait plusieurs achats chez différents commerçants
    println("\n" + "="*80)
    println("="*80)
    println("Nombre de transactions: " + df.count())
    println("Nombre de clients uniques: " + df.select("client_id").distinct().count())
    println("Nombre de cartes uniques: " + df.select("card_id").distinct().count())
    println("Nombre de commercants uniques: " + df.select("merchant_id").distinct().count())

    //3. Qualité des données
    // RÉPONSE Q3:
    // - Colonnes avec nulls: merchant_state (11.75%), zip (12.42%), errors (98.41% = normal, signifie pas d'erreur)
    // - Transactions montant ≤ 0: 670 688 (5%) - incluant des montants négatifs (remboursements probablement je pense)
    // - Transactions sans MCC: 0 (toutes ont un code MCC peut etre inexacte je reviendrai dessus)
    // - Transactions avec erreurs: 211 393 (1.59%)
    println("\n" + "="*80)
    println("="*80)
    val nullCounts = df.columns.map { colName =>
      (colName, df.filter(col(colName).isNull).count())
    }.filter(_._2 > 0)

    println("Colonnes avec valeurs nulles:")
    nullCounts.foreach { case (name, count) =>
      println(s"  $name: $count valeurs nulles")
    }

    println("Transactions avec montant ≤ 0: " + df.filter(col("amount").cast("double") <= 0).count())

      df.select("amount").show(10)
      df.select(min("amount"), max("amount")).show()

    println("Transactions avec montant ≤ 0: " + 
    df.filter(
      regexp_replace(col("amount"), "[\\$ ]", "").cast("double") <= 0
    ).count())

    println("Transactions sans mcc: " + df.filter(col("mcc").isNull or (col("mcc") === 0)).count())

    println("Transactions avec erreurs: " + df.filter(col("errors").isNotNull).count())


    // Tableau récapitulatif des valeurs manquantes

    val totalRows = df.count()
    val missingStats = df.columns.map { colName =>
      val nullCount = df.filter(col(colName).isNull).count()
      val percentage = if (totalRows > 0) (nullCount.toDouble / totalRows * 100) else 0.0
      (colName, nullCount, percentage)
    }
    
    println("\n" + "="*80)
    println("TABLEAU RÉCAPITULATIF DES VALEURS MANQUANTES")
    println("="*80)
    println(f"${"Colonne"}%-30s ${"Nombre"}%15s ${"Pourcentage (%)"}%20s")
    println("-"*80)
    missingStats.foreach { case (colName, count, pct) =>
      println(f"${colName}%-30s ${count}%15d ${pct}%19.2f%%")
    }
    println("="*80)
    println(s"Total de lignes: $totalRows")
    println()

    // PARTIE 2 – Analyse des montants & comportements

    //4. Analyse des montants
    // RÉPONSE Q4: Les montants élevés sont RARES
    // Moyenne: 42.98$ | Min: -500$ | Max: 6820.20$
    // La moyenne basse (43$) indique que la majorité des transactions sont de petits montants
    // Les montants >1000$ sont exceptionnels, probablement des achats importants ou fraudes
    println("\n" + "="*80)
    println("="*80)
      df.select(
          sum(regexp_replace(col("amount"), "[\\$ ]", "").cast("double")).as("somme_totale"),
          avg(regexp_replace(col("amount"), "[\\$ ]", "").cast("double")).as("moyenne"),
          min(regexp_replace(col("amount"), "[\\$ ]", "").cast("double")).as("minimum"),
          max(regexp_replace(col("amount"), "[\\$ ]", "").cast("double")).as("maximum"),
          count("amount").as("nombre_transactions")
        ).show()


    //5. Analyse temporelle
    // RÉPONSE Q5: Oui, distribution horaire NON uniforme
    // Heures creuses: 1h-5h (110-180k tx) - nuit, activité faible = normal
    // Pic d'activité: 11h-12h (950k tx) - heure du déjeuner
    // Heures de pointe: 6h-16h (750k-950k tx) - journée de travail
    // Par jour: Distribution quasi-uniforme (1.9M/jour) - pas d'anomalie
    // Conclusion: Pas d'heures "anormalement" actives, pattern cohérent avec comportement humain
    println("\n" + "="*80)
    println("="*80)
    val dfWithDate = df.withColumn("date_timestamp", to_timestamp(col("date"), "yyyy-MM-dd HH:mm:ss"))
    
    // Extraire l'heure, le jour et le mois
    val dfWithExtracted = dfWithDate
      .withColumn("heure", hour(col("date_timestamp")))
      .withColumn("jour", dayofmonth(col("date_timestamp")))
      .withColumn("mois", month(col("date_timestamp")))
      .withColumn("jour_semaine", date_format(col("date_timestamp"), "EEEE")) // Nom du jour en français
      .withColumn("jour_semaine_num", dayofweek(col("date_timestamp"))) // 1=Dimanche, 2=Lundi, etc.
    
    // Afficher quelques exemples avec les colonnes extraites
    println("\n=== Exemples avec date extraite ===")
    dfWithExtracted.select("date", "heure", "jour", "mois", "jour_semaine", "jour_semaine_num").show(10)
    
    // Nombre de transactions par heure
    println("\n=== Nombre de transactions par heure ===")
    dfWithExtracted
      .groupBy("heure")
      .agg(count("*").as("nombre_transactions"))
      .orderBy("heure")
      .show(24)
    
    // Nombre de transactions par jour de la semaine
    println("\n=== Nombre de transactions par jour de la semaine ===")
    dfWithExtracted
      .groupBy("jour_semaine", "jour_semaine_num")
      .agg(count("*").as("nombre_transactions"))
      .orderBy("jour_semaine_num")
      .show()

  //6. Jointure avec les MCC
    // RÉPONSE Q6: Oui, certaines catégories sont potentiellement plus risquées
    // Catégories à haut risque (montant moyen élevé + faible volume):
    //   - Cruise Lines (1551$), Steel/Metal products (750-800$) → Montants élevés = cible de fraude
    //   - Money Transfer (589k tx) → Volume élevé + transferts = risque de blanchiment
    // Catégories courantes (faible risque individuel, haut volume):
    //   - Grocery/Supermarkets, Gas Stations, Restaurants → Transactions quotidiennes normales
    println("\n" + "="*80)
    println("="*80)
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


    // 7. Analyse des erreurs
    // RÉPONSE Q7: Oui, un client avec beaucoup d'erreurs EST suspect
    // Sur 13M transactions, seulement 211k (1.6%) ont des erreurs
    // Un client avec un ratio d'erreurs élevé (>10%) est anormal
    // Les erreurs répétées peuvent indiquer: cartes volées testées, tentatives de fraude, comportement bot
    // À croiser avec: nombre de villes, montants, fréquence pour confirmer la suspicion
    println("\n" + "="*80)
    println("="*80)

    // 8. Création d'indicateurs
    // RÉPONSE Q8: 4 indicateurs créés pour détecter les anomalies
    // 1. Tx/carte/jour: Max 29 tx/jour (carte 2408) - seuil >10 = suspect
    // 2. Montant total/jour: Max 6820$ (carte 5165) - seuil >1000$ = suspect
    // 3. Villes distinctes: Max 359 villes (carte 3239) - seuil >3 = suspect
    // 4. Ratio erreurs: Max 14.96% (carte 2220) - ratio élevé = suspect
    println("\n" + "="*80)
    println("="*80)

    // Préparer les données avec montant nettoyé et date extraite
    val dfClean = df
      .withColumn("amount_clean", regexp_replace(col("amount"), "[\\$ ]", "").cast("double"))
      .withColumn("date_jour", to_date(col("date")))
      .withColumn("has_error", when(col("errors").isNotNull && col("errors") =!= "", 1).otherwise(0))

    // Nombre de transactions par carte et par jour
    println("\n=== Transactions par carte et par jour ===")
    val txParCarteJour = dfClean
      .groupBy("card_id", "date_jour")
      .agg(count("*").as("nb_transactions"))
      .orderBy(desc("nb_transactions"))
    txParCarteJour.show(10)

    // Montant total par carte et par jour
    println("\n=== Montant total par carte et par jour ===")
    val montantParCarteJour = dfClean
      .groupBy("card_id", "date_jour")
      .agg(round(sum("amount_clean"), 2).as("montant_total"))
      .orderBy(desc("montant_total"))
    montantParCarteJour.show(10)

    // Nombre de villes différentes utilisées par carte
    println("\n=== Nombre de villes différentes par carte ===")
    val villesParCarte = dfClean
      .groupBy("card_id")
      .agg(countDistinct("merchant_city").as("nb_villes_distinctes"))
      .orderBy(desc("nb_villes_distinctes"))
    villesParCarte.show(10)

    // Ratio de transactions avec erreur par carte
    println("\n=== Ratio de transactions avec erreur par carte ===")
    val ratioErreurs = dfClean
      .groupBy("card_id")
      .agg(
        count("*").as("total_transactions"),
        sum("has_error").as("transactions_erreur"),
        round(sum("has_error") / count("*") * 100, 2).as("ratio_erreur_pct")
      )
      .orderBy(desc("ratio_erreur_pct"))
    ratioErreurs.show(10)

    // 9. Détection des cartes suspectes
    // Amélioration: Augmenter seuils ou combiner critères (ET au lieu de OU) pour réduire faux positifs
    println("\n" + "="*80)
    println("="*80)

    // Seuils configurables
    val seuilTxParJour = 10        // Plus de X transactions par jour
    val seuilVilles = 3            // Plus de 3 villes différentes
    val seuilMontantJournalier = 1000.0  // Montant total journalier élevé

    // Cartes avec trop de transactions par jour
    val cartesHighTx = txParCarteJour
      .filter(col("nb_transactions") > seuilTxParJour)
      .select("card_id")
      .distinct()

    // Cartes utilisées dans plus de 3 villes
    val cartesMultiVilles = villesParCarte
      .filter(col("nb_villes_distinctes") > seuilVilles)
      .select("card_id")

    // Cartes avec montant journalier élevé
    val cartesHighMontant = montantParCarteJour
      .filter(col("montant_total") > seuilMontantJournalier)
      .select("card_id")
      .distinct()

    // Union des cartes suspectes avec les raisons
    val suspiciousCards = cartesHighTx.withColumn("raison", lit("high_tx_count"))
      .union(cartesMultiVilles.withColumn("raison", lit("multi_cities")))
      .union(cartesHighMontant.withColumn("raison", lit("high_daily_amount")))
      .groupBy("card_id")
      .agg(collect_set("raison").as("raisons"))

    suspiciousCards.show(20, false)
    println(s"Nombre de cartes suspectes: ${suspiciousCards.count()}")

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
