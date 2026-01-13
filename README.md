# Spark Scala Demo - Analyse de Transactions Bancaires

Projet d'analyse de données de transactions bancaires avec Apache Spark et Scala. Le programme effectue une analyse complète incluant la qualité des données, l'analyse temporelle, et la détection de comportements suspects.

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
