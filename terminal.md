jipei@vps-4afa48bf:~/projects/scala$ sbt run
[info] welcome to sbt 1.12.0 (Ubuntu Java 17.0.17)
[info] loading project definition from /home/jipei/projects/scala/project
[info] loading settings for project root from build.sbt...
[info] set current project to spark-scala-demo (in build file:/home/jipei/projects/scala/)
[warn] there's a key that's not used by any other settings/tasks:
[warn]  
[warn] * ThisBuild / classLoaderLayeringStrategy
[warn]   +- /home/jipei/projects/scala/build.sbt:3
[warn]  
[warn] note: a setting might still be used by a command; to exclude a key from this `lintUnused` check
[warn] either append it to `Global / excludeLintKeys` or call .withRank(KeyRanks.Invisible) on the key
[info] compiling 1 Scala source to /home/jipei/projects/scala/target/scala-2.13/classes ...
[info] running (fork) Main 
[info] ================================================================================
[info] QUESTION 1 : CHARGEMENT DES DONNÉES
[info] Questions : Combien de colonnes par fichier ? Quels types de données semblent incorrects ou suspects ?
[info] ================================================================================
[info] root
[info]  |-- id: integer (nullable = true)
[info]  |-- date: timestamp (nullable = true)
[info]  |-- client_id: integer (nullable = true)
[info]  |-- card_id: integer (nullable = true)
[info]  |-- amount: string (nullable = true)
[info]  |-- use_chip: string (nullable = true)
[info]  |-- merchant_id: integer (nullable = true)
[info]  |-- merchant_city: string (nullable = true)
[info]  |-- merchant_state: string (nullable = true)
[info]  |-- zip: double (nullable = true)
[info]  |-- mcc: integer (nullable = true)
[info]  |-- errors: string (nullable = true)
[info] ================================================================================
[info] QUESTION 2 : ANALYSE DE VOLUMÉTRIE
[info] Interprétation attendue : Qui génère le plus de lignes ?
[info] ================================================================================
[info] Nombre de transactions: 13305915
[info] Nombre de clients uniques: 1219
[info] Nombre de cartes uniques: 4071
[info] Nombre de commercants uniques: 74831
[info] ================================================================================
[info] QUESTION 3 : QUALITÉ DES DONNÉES
[info] Identifier les colonnes avec valeurs nulles, transactions avec montant ≤ 0, sans MCC et avec erreurs
[info] ================================================================================
[info] Colonnes avec valeurs nulles:
[info]   merchant_state: 1563700 valeurs nulles
[info]   zip: 1652706 valeurs nulles
[info]   errors: 13094522 valeurs nulles
[info] Transactions avec montant ≤ 0: 0
[info] +-------+
[info] | amount|
[info] +-------+
[info] |$-77.00|
[info] | $14.57|
[info] | $80.00|
[info] |$200.00|
[info] | $46.41|
[info] |  $4.81|
[info] | $77.00|
[info] | $26.46|
[info] |$261.58|
[info] | $10.74|
[info] +-------+
[info] only showing top 10 rows
[info] +-----------+-----------+
[info] |min(amount)|max(amount)|
[info] +-----------+-----------+
[info] |     $-0.00|    $999.97|
[info] +-----------+-----------+
[info] Transactions avec montant ≤ 0: 670688
[info] Transactions sans mcc: 0
[info] Transactions avec erreurs: 211393
[info] ================================================================================
[info] TABLEAU RÉCAPITULATIF DES VALEURS MANQUANTES
[info] ================================================================================
[info] Colonne                                 Nombre      Pourcentage (%)
[info] --------------------------------------------------------------------------------
[info] id                                           0                0.00%
[info] date                                         0                0.00%
[info] client_id                                    0                0.00%
[info] card_id                                      0                0.00%
[info] amount                                       0                0.00%
[info] use_chip                                     0                0.00%
[info] merchant_id                                  0                0.00%
[info] merchant_city                                0                0.00%
[info] merchant_state                         1563700               11.75%
[info] zip                                    1652706               12.42%
[info] mcc                                          0                0.00%
[info] errors                                13094522               98.41%
[info] ================================================================================
[info] Total de lignes: 13305915
[info] ================================================================================
[info] QUESTION 4 : ANALYSE DES MONTANTS
[info] Question métier : Les montants élevés sont-ils rares ou fréquents ?
[info] ================================================================================
[info] +-------------------+-----------------+-------+-------+-------------------+
[info] |       somme_totale|          moyenne|minimum|maximum|nombre_transactions|
[info] +-------------------+-----------------+-------+-------+-------------------+
[info] |5.718355222800052E8|42.97603902324682| -500.0| 6820.2|           13305915|
[info] +-------------------+-----------------+-------+-------+-------------------+
[info] ================================================================================
[info] QUESTION 5 : ANALYSE TEMPORELLE
[info] Interprétation : Existe-t-il des heures anormalement actives ?
[info] ================================================================================
[info] === Exemples avec date extraite ===
[info] +-------------------+-----+----+----+------------+----------------+
[info] |               date|heure|jour|mois|jour_semaine|jour_semaine_num|
[info] +-------------------+-----+----+----+------------+----------------+
[info] |2010-01-01 00:01:00|    0|   1|   1|      Friday|               6|
[info] |2010-01-01 00:02:00|    0|   1|   1|      Friday|               6|
[info] |2010-01-01 00:02:00|    0|   1|   1|      Friday|               6|
[info] |2010-01-01 00:05:00|    0|   1|   1|      Friday|               6|
[info] |2010-01-01 00:06:00|    0|   1|   1|      Friday|               6|
[info] |2010-01-01 00:07:00|    0|   1|   1|      Friday|               6|
[info] |2010-01-01 00:09:00|    0|   1|   1|      Friday|               6|
[info] |2010-01-01 00:14:00|    0|   1|   1|      Friday|               6|
[info] |2010-01-01 00:21:00|    0|   1|   1|      Friday|               6|
[info] |2010-01-01 00:21:00|    0|   1|   1|      Friday|               6|
[info] +-------------------+-----+----+----+------------+----------------+
[info] only showing top 10 rows
[info] === Nombre de transactions par heure ===
[info] +-----+-------------------+
[info] |heure|nombre_transactions|
[info] +-----+-------------------+
[info] |    0|             140582|
[info] |    1|             115586|
[info] |    2|             112787|
[info] |    3|             103478|
[info] |    4|             114985|
[info] |    5|             182965|
[info] |    6|             758856|
[info] |    7|             901756|
[info] |    8|             880501|
[info] |    9|             876423|
[info] |   10|             871512|
[info] |   11|             943671|
[info] |   12|             953498|
[info] |   13|             900703|
[info] |   14|             887776|
[info] |   15|             858022|
[info] |   16|             864678|
[info] |   17|             482230|
[info] |   18|             472559|
[info] |   19|             457434|
[info] |   20|             423636|
[info] |   21|             424523|
[info] |   22|             418877|
[info] |   23|             158877|
[info] +-----+-------------------+
[info] === Nombre de transactions par jour de la semaine ===
[info] +------------+----------------+-------------------+
[info] |jour_semaine|jour_semaine_num|nombre_transactions|
[info] +------------+----------------+-------------------+
[info] |      Sunday|               1|            1899044|
[info] |      Monday|               2|            1896914|
[info] |     Tuesday|               3|            1897678|
[info] |   Wednesday|               4|            1895871|
[info] |    Thursday|               5|            1918666|
[info] |      Friday|               6|            1895372|
[info] |    Saturday|               7|            1902370|
[info] +------------+----------------+-------------------+
[info] ================================================================================
[info] QUESTION 6 : JOINTURE AVEC LES MCC
[info] Question : Certaines catégories sont-elles plus risquées ?
[info] ================================================================================
[info] +----+--------------------+
[info] | mcc|   merchant_category|
[info] +----+--------------------+
[info] |5499|Miscellaneous Foo...|
[info] |5311|   Department Stores|
[info] |4829|      Money Transfer|
[info] |4829|      Money Transfer|
[info] |5813|Drinking Places (...|
[info] |5942|         Book Stores|
[info] |5499|Miscellaneous Foo...|
[info] |4784|Tolls and Bridge ...|
[info] |7801|Athletic Fields, ...|
[info] |5813|Drinking Places (...|
[info] +----+--------------------+
[info] only showing top 10 rows
[info] === Top 10 catégories par volume ===
[info] +-----------------------------+-------------------+
[info] |merchant_category            |nombre_transactions|
[info] +-----------------------------+-------------------+
[info] |Grocery Stores, Supermarkets |1592584            |
[info] |Miscellaneous Food Stores    |1460875            |
[info] |Service Stations             |1424711            |
[info] |Eating Places and Restaurants|999738             |
[info] |Drug Stores and Pharmacies   |772913             |
[info] |Tolls and Bridge Fees        |674135             |
[info] |Wholesale Clubs              |601942             |
[info] |Money Transfer               |589140             |
[info] |Taxicabs and Limousines      |500662             |
[info] |Fast Food Restaurants        |499659             |
[info] +-----------------------------+-------------------+
[info] === Montant moyen par catégorie ===
[info] +---------------------------------------+-------------+-------------------+
[info] |merchant_category                      |montant_moyen|nombre_transactions|
[info] +---------------------------------------+-------------+-------------------+
[info] |Cruise Lines                           |1551.42      |428                |
[info] |Steel Drums and Barrels                |797.89       |384                |
[info] |Fabricated Structural Metal Products   |786.94       |408                |
[info] |Miscellaneous Fabricated Metal Products|786.78       |351                |
[info] |Coated and Laminated Products          |785.08       |381                |
[info] |Bolt, Nut, Screw, Rivet Manufacturing  |761.1        |337                |
[info] |Floor Covering Stores                  |758.88       |334                |
[info] |Miscellaneous Metal Fabrication        |757.54       |391                |
[info] |Tools, Parts, Supplies Manufacturing   |734.31       |3084               |
[info] |Leather Goods                          |733.86       |2822               |
[info] |Steel Products Manufacturing           |732.64       |3112               |
[info] |Airlines                               |729.83       |2861               |
[info] |Steelworks                             |729.08       |3065               |
[info] |Hospitals                              |726.08       |3468               |
[info] |Miscellaneous Metals                   |722.72       |2714               |
[info] |Pottery and Ceramics                   |719.24       |2809               |
[info] |Upholstery and Drapery Stores          |718.03       |2805               |
[info] |Brick, Stone, and Related Materials    |716.37       |2794               |
[info] |Legal Services and Attorneys           |535.69       |7095               |
[info] |Music Stores - Musical Instruments     |465.65       |319                |
[info] +---------------------------------------+-------------+-------------------+
[info] only showing top 20 rows
[info] ================================================================================
[info] QUESTION 7 : ANALYSE DES ERREURS
[info] Indice : Un client avec beaucoup d'erreurs est-il suspect ?
[info] ================================================================================
[info] ================================================================================
[info] QUESTION 8 : CRÉATION D'INDICATEURS
[info] Créer les indicateurs : transactions par carte/jour, montant total, villes distinctes, ratio erreurs
[info] ================================================================================
[info] === Transactions par carte et par jour ===
[info] +-------+----------+---------------+
[info] |card_id| date_jour|nb_transactions|
[info] +-------+----------+---------------+
[info] |   2408|2011-06-12|             29|
[info] |   2408|2015-11-06|             28|
[info] |   2408|2016-04-11|             26|
[info] |   2408|2013-08-11|             24|
[info] |   4938|2015-04-19|             24|
[info] |   3239|2018-08-29|             23|
[info] |   2408|2011-10-29|             23|
[info] |   4938|2012-06-24|             23|
[info] |   4938|2015-02-19|             23|
[info] |   2408|2012-04-01|             23|
[info] +-------+----------+---------------+
[info] only showing top 10 rows
[info] === Montant total par carte et par jour ===
[info] +-------+----------+-------------+
[info] |card_id| date_jour|montant_total|
[info] +-------+----------+-------------+
[info] |   5165|2010-09-22|       6820.2|
[info] |   5757|2017-05-30|      6646.75|
[info] |   3427|2019-01-27|      6613.44|
[info] |   2204|2010-09-05|      6089.18|
[info] |   5406|2012-04-10|      5913.37|
[info] |   4946|2013-05-22|      5813.78|
[info] |   5619|2014-10-24|      5802.38|
[info] |   3399|2010-12-11|      5703.94|
[info] |    175|2018-05-09|      5682.22|
[info] |   5406|2014-11-13|       5654.5|
[info] +-------+----------+-------------+
[info] only showing top 10 rows
[info] === Nombre de villes différentes par carte ===
[info] +-------+--------------------+
[info] |card_id|nb_villes_distinctes|
[info] +-------+--------------------+
[info] |   3239|                 359|
[info] |   1254|                 324|
[info] |   3530|                 315|
[info] |   5359|                 293|
[info] |   1195|                 292|
[info] |   1246|                 292|
[info] |   4237|                 281|
[info] |   4993|                 279|
[info] |   3738|                 279|
[info] |   1247|                 273|
[info] +-------+--------------------+
[info] only showing top 10 rows
[info] === Ratio de transactions avec erreur par carte ===
[info] +-------+------------------+-------------------+----------------+
[info] |card_id|total_transactions|transactions_erreur|ratio_erreur_pct|
[info] +-------+------------------+-------------------+----------------+
[info] |   2220|              6726|               1006|           14.96|
[info] |   2644|              6530|                954|           14.61|
[info] |   5586|              6600|                951|           14.41|
[info] |   4416|               191|                 24|           12.57|
[info] |   5364|              4277|                443|           10.36|
[info] |    164|              2764|                274|            9.91|
[info] |   4081|                58|                  5|            8.62|
[info] |   5596|                88|                  7|            7.95|
[info] |   4376|                76|                  6|            7.89|
[info] |   3184|               144|                 11|            7.64|
[info] +-------+------------------+-------------------+----------------+
[info] only showing top 10 rows
[info] ================================================================================
[info] QUESTION 9 : DÉTECTION DE COMPORTEMENTS SUSPECTS
[info] Identifier les cartes avec comportements suspects (trop de transactions, multi-villes, montants élevés)
[info] ================================================================================
[info] +-------+------------------------------------------------+
[info] |card_id|raisons                                         |
[info] +-------+------------------------------------------------+
[info] |0      |[multi_cities, high_daily_amount]               |
[info] |1      |[multi_cities, high_daily_amount]               |
[info] |2      |[multi_cities, high_daily_amount]               |
[info] |3      |[multi_cities, high_daily_amount]               |
[info] |4      |[multi_cities]                                  |
[info] |5      |[multi_cities]                                  |
[info] |8      |[multi_cities]                                  |
[info] |9      |[multi_cities, high_daily_amount, high_tx_count]|
[info] |10     |[multi_cities]                                  |
[info] |11     |[multi_cities]                                  |
[info] |12     |[multi_cities, high_daily_amount]               |
[info] |13     |[multi_cities, high_tx_count]                   |
[info] |14     |[multi_cities]                                  |
[info] |15     |[multi_cities, high_daily_amount]               |
[info] |16     |[multi_cities, high_daily_amount]               |
[info] |17     |[multi_cities]                                  |
[info] |19     |[multi_cities, high_daily_amount]               |
[info] |20     |[multi_cities]                                  |
[info] |21     |[multi_cities]                                  |
[info] |22     |[multi_cities, high_tx_count]                   |
[info] +-------+------------------------------------------------+
[info] only showing top 20 rows
[info] Nombre de cartes suspectes: 4061
[success] Total time: 266 s (0:04:26.0), completed Jan 13, 2026, 12:59:16 PM
jipei@vps-4afa48bf:~/projects/scala$ 
