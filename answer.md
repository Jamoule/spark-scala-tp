# PARTIE 5 – RESTITUTION : Synthèse Finale

## Analyse des Transactions Bancaires - Détection de Fraude

En premier lieu j'ai fait quelques recherches sur scala et le fonctionnement de Apache Spark et avec l'aide de la documentation et d'exemples fournis j'ai pu initialiser ce projet et réaliser les differents exercices

---

## 1. Patterns Principaux Observés

### Volumétrie
- Le dataset contient un grand nombre de transactions réparties sur plusieurs clients et cartes
- Ratio moyen d'environ plusieurs centaines de transactions par carte, indiquant une utilisation régulière

### Comportements Temporels
- Distribution des transactions non uniforme selon les heures de la journée
- Certaines heures (nuit, tôt le matin) présentent moins d'activité - des transactions à ces heures peuvent être suspectes

### Comportements Géographiques
- Certaines cartes sont utilisées dans de nombreuses villes différentes
- Une utilisation multi-villes sur une courte période est un signal fort de fraude (impossibilité physique)

### Patterns de Montants
- Présence de transactions avec montants négatifs (remboursements ou erreurs)
- Distribution des montants avec une majorité de petites transactions et quelques montants élevés

### Erreurs Transactionnelles
- Certaines cartes présentent un taux d'erreur anormalement élevé
- Un ratio d'erreurs élevé peut indiquer des tentatives de fraude répétées

---

## 2. Indicateurs Utiles pour un Futur Modèle ML

### Features de Vélocité (comportement temporel)
| Indicateur | Description | Pertinence Fraude |
|------------|-------------|-------------------|
| `nb_transactions_par_jour` | Nombre de TX par carte/jour | Détecte les pics d'activité anormaux |
| `nb_transactions_par_heure` | Distribution horaire | Identifie les heures inhabituelles |
| `delta_temps_entre_tx` | Temps entre 2 transactions | Détecte les rafales suspectes |

### Features Géographiques
| Indicateur | Description | Pertinence Fraude |
|------------|-------------|-------------------|
| `nb_villes_distinctes` | Villes utilisées par carte | **Signal fort** - mobilité anormale |
| `distance_entre_tx` | Distance géographique | Impossible si trop grande en peu de temps |
| `ville_inhabituelle` | TX hors zone habituelle | Changement de comportement |

### Features de Montant
| Indicateur | Description | Pertinence Fraude |
|------------|-------------|-------------------|
| `montant_total_journalier` | Somme des TX par jour | Dépenses anormalement élevées |
| `montant_moyen_carte` | Moyenne historique | Comparaison avec baseline |
| `ecart_type_montant` | Variabilité | Comportement erratique |

### Features d'Erreur
| Indicateur | Description | Pertinence Fraude |
|------------|-------------|-------------------|
| `ratio_erreurs_carte` | % TX avec erreur | Cartes avec beaucoup d'échecs |
| `type_erreur_frequent` | Type d'erreur dominant | Certains types = tentatives fraude |

### Features Catégorielles (MCC)
| Indicateur | Description | Pertinence Fraude |
|------------|-------------|-------------------|
| `categorie_risquee` | MCC à haut risque | Certaines catégories plus frauduleuses |
| `changement_categorie` | Écart vs historique | Achat inhabituel pour le client |

---

## 3. Limites des Données

### Données Manquantes
- **Colonne `errors`** : souvent vide ou nulle, limitant l'analyse des erreurs
- **Mapping MCC incomplet** : certains codes MCC non présents dans `mcc_codes.json`
- **Informations géographiques** : code postal (`zip`) parfois manquant ou mal formaté

### Biais Potentiels
- **Anonymisation** : perte d'information contextuelle (nom du commerçant, etc.)
- **Absence de canal** : pas d'information sur transaction en ligne vs physique
- **Pas de label de fraude** : impossible de valider les détections sans ground truth

### Limitations Techniques
- **Format du montant** : stocké en string avec `$`, nécessite nettoyage systématique
- **Granularité temporelle** : précision à la minute, pas à la seconde
- **Pas d'historique client** : ancienneté, première transaction, comportement baseline absent

### Améliorations Suggérées
1. **Intégrer `train_fraud_labels.json`** pour avoir des labels de fraude confirmée
2. **Enrichir avec `cards_data.csv`** pour les caractéristiques des cartes (type, plafond, etc.)
3. **Croiser avec `users_data.csv`** pour le profil client (âge, ancienneté, etc.)
4. **Ajouter coordonnées GPS** pour calculer les distances réelles entre transactions

---

## 4. Résumé des Critères de Détection Utilisés

```
Carte suspecte si :
├── Plus de 10 transactions par jour (vélocité anormale)
├── Transactions dans plus de 3 villes (mobilité suspecte)
└── Montant total journalier > 1000$ (dépense excessive)
```

Ces règles simples permettent d'identifier des comportements à risque sans modèle ML, mais présentent des limites :
- Seuils arbitraires (10 TX, 3 villes, 1000$)
- Pas de personnalisation par profil client
- Risque de faux positifs (clients légitimes voyageant beaucoup)

Un modèle ML supervisé permettrait d'affiner ces détections en apprenant des patterns plus subtils à partir des labels de fraude confirmée.

---

*Analyse réalisée avec Apache Spark en Scala*
