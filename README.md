# Spark Scala Demo - Analyse de Transactions Bancaires

Projet d'analyse de données de transactions bancaires avec Apache Spark et Scala. Le programme effectue une analyse complète incluant la qualité des données, l'analyse temporelle, et la détection de comportements suspects.


## Synthèse de l'analyse

### Patterns principaux observés

1. **Distribution temporelle cohérente** : Les transactions suivent un cycle journalier classique avec pic entre 11h-12h (déjeuner) et creux la nuit (1h-5h). Aucune anomalie temporelle majeure détectée.

2. **Montants majoritairement faibles** : Moyenne de 43$ avec médiane probablement plus basse. Les montants >1000$ sont rares et représentent des outliers (achats importants ou potentielles fraudes).

3. **Concentration des volumes** : Les supermarchés, stations-service et restaurants dominent en volume. Les catégories à montant élevé (croisières, métallurgie) ont un faible volume mais un risque unitaire plus élevé.

4. **Faible taux d'erreurs global** : Seulement 1.6% des transactions présentent des erreurs, mais certaines cartes atteignent 15% de ratio d'erreurs (comportement anormal).

5. **Multi-localisation fréquente** : La majorité des cartes sont utilisées dans plus de 3 villes sur la période analysée (2010-2019), ce qui est normal sur 10 ans.

### Indicateurs utiles pour un futur modèle

| Indicateur | Description | Seuil suggéré |
|------------|-------------|---------------|
| `nb_transactions_jour` | Nombre de transactions par carte/jour | > 10 = suspect |
| `montant_total_jour` | Somme des montants par carte/jour | > 1000$ = suspect |
| `nb_villes_distinctes` | Nombre de villes différentes par carte (sur période courte) | > 3/jour = suspect |
| `ratio_erreur_pct` | Pourcentage de transactions avec erreur | > 10% = suspect |
| `montant_moyen_categorie` | Écart par rapport au montant moyen de la catégorie MCC | > 3 écarts-types |
| `heure_transaction` | Transactions nocturnes (1h-5h) | À pondérer |
| `categorie_risque` | Catégories sensibles (Money Transfer, Cruise Lines) | Flag binaire |

### Limites des données

1. **Données monétaires en String** : Les montants contiennent le symbole "$" et nécessitent un nettoyage. Risque d'erreurs de parsing.

2. **Valeurs manquantes significatives** :
   - `merchant_state` : 11.75% de nulls
   - `zip` : 12.42% de nulls
   - Impact sur l'analyse géographique

3. **Code postal en Double** : Perte des zéros initiaux (ex: "01234" devient 1234.0). Problème pour l'analyse par région.

4. **Structure MCC inversée** : Le fichier JSON a les codes en colonnes au lieu de lignes, nécessitant une transformation.

5. **Période longue (2010-2019)** : Les seuils de détection doivent être adaptés (ex: multi-villes normal sur 10 ans, anormal sur 1 jour, très dependant de beaucoups de facteurs).

6. **Déséquilibre probable des labels** : Les fraudes représentent généralement <1% des transactions, nécessitant des techniques de rééquilibrage pour l'entraînement.

7. **Absence de contexte client** : Pas de jointure exploitée avec `users_data.csv` et `cards_data.csv` pour enrichir le profil de risque.


## Prérequis

### Java Development Kit (JDK 17)

**macOS (Homebrew)**
```bash
brew install openjdk@17
sudo ln -sfn $(brew --prefix)/opt/openjdk@17/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-17.jdk
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

**Ubuntu/Debian**
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

**Vérification**
```bash
java -version
# Doit afficher: openjdk version "17.x.x"
```

### SBT (Scala Build Tool)

**macOS (Homebrew)**
```bash
brew install sbt
```

**Ubuntu/Debian**
```bash
echo "deb https://repo.scala-sbt.org/scalasbt/debian all main" | sudo tee /etc/apt/sources.list.d/sbt.list
curl -sL "https://keyserver.ubuntu.com/pks/lookup?op=get&search=0x99E82A75642AC823" | sudo apt-key add
sudo apt update
sudo apt install sbt
```

**Vérification**
```bash
sbt --version
```

### Configuration mémoire recommandée

Le projet traite ~13 millions de transactions. Assurez-vous d'avoir au moins **8 Go de RAM** disponibles.

## Structure du projet

```
scala/
├── build.sbt                          # Configuration SBT et dépendances
├── src/
│   └── main/
│       └── scala/
│           └── Main.scala             # Code principal d'analyse
├── data/                              # Données (à fournir)
│   ├── transactions_data.csv          # Transactions bancaires
│   ├── cards_data.csv                 # Informations cartes
│   ├── users_data.csv                 # Informations clients
│   ├── mcc_codes.json                 # Codes catégories marchands
│   └── train_fraud_labels.json        # Labels de fraude
└── README.md
```

## Installation

1. **Cloner le projet**
```bash
git clone <url-du-repo>
cd scala
```

2. **Vérifier les prérequis**
```bash
java -version   # JDK 17 requis
sbt --version   # SBT 1.x requis
```

3. **Placer les fichiers de données**

Créez le dossier `data/` et placez-y les fichiers suivants :
```bash
mkdir -p data
```

| Fichier | Description |
|---------|-------------|
| `transactions_data.csv` | Transactions bancaires (~13M lignes) |
| `cards_data.csv` | Informations sur les cartes |
| `users_data.csv` | Informations sur les clients |
| `mcc_codes.json` | Codes catégories marchands (MCC) |
| `train_fraud_labels.json` | Labels de fraude pour entraînement |

Structure attendue :
```
data/
├── transactions_data.csv
├── cards_data.csv
├── users_data.csv
├── mcc_codes.json
└── train_fraud_labels.json
```

**Important** : Sans ces fichiers, le programme ne pourra pas s'exécuter.

## Lancement

```bash
# Depuis la racine du projet
sbt run
```

Le programme s'exécute en ~4-5 minutes selon votre machine.

## Analyses effectuées

| Question | Description |
|----------|-------------|
| Q1 | Chargement et inspection des schémas de données |
| Q2 | Analyse de volumétrie (clients, cartes, commerçants) |
| Q3 | Qualité des données (valeurs nulles, montants invalides, erreurs) |
| Q4 | Statistiques des montants (min, max, moyenne) |
| Q5 | Analyse temporelle (distribution par heure et jour) |
| Q6 | Jointure avec codes MCC et analyse par catégorie |
| Q7 | Analyse des erreurs de transaction |
| Q8 | Création d'indicateurs (tx/jour, montant total, villes, ratio erreurs) |
| Q9 | Détection de comportements suspects |

## Dépendances

| Technologie | Version |
|-------------|---------|
| Scala | 2.13.18 |
| Apache Spark | 3.5.1 |
| JDK | 17 |
| SBT | 1.12.0+ |

## Résolution de problèmes

### Erreur de mémoire (OutOfMemoryError)

Augmentez la mémoire dans `build.sbt` :
```scala
"-Xmx16g"  // Passer à 16 Go si disponible
```

### Erreur JDK (Illegal reflective access)

Les options `--add-exports` et `--add-opens` sont déjà configurées dans `build.sbt` pour JDK 17.

### Fichiers de données manquants

Vérifiez que tous les fichiers sont présents dans `data/` :
```bash
ls -la data/
```

## Auteur

Projet réalisé dans le cadre d'une analyse de données bancaires avec Spark.
