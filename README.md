# 🤖 Classification de Textes Arabes avec Naive Bayes

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![SAFAR](https://img.shields.io/badge/SAFAR-v2-blue.svg)](http://arabic.emi.ac.ma/safar/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Application web complète de classification automatique de textes arabes utilisant l'algorithme **Naive Bayes** et la bibliothèque **SAFAR** pour le traitement du langage naturel arabe.

![Screenshot](docs/screenshot.png)

---

## 📋 Table des Matières

- [Fonctionnalités](#-fonctionnalités)
- [Technologies](#-technologies)
- [Architecture](#-architecture)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Utilisation](#-utilisation)
- [API Endpoints](#-api-endpoints)
- [Algorithme](#-algorithme-naive-bayes)
- [Métriques d'Évaluation](#-métriques-dévaluation)
- [Structure du Projet](#-structure-du-projet)
- [Dépannage](#-dépannage)
- [Contribuer](#-contribuer)
- [Licence](#-licence)
- [Auteurs](#-auteurs)

---

## ✨ Fonctionnalités

### 🎯 Classification de Textes
- **Upload de fichiers** .txt arabes
- **Saisie directe** de texte dans l'interface
- **Classification automatique** en catégories prédéfinies
- **Calcul des probabilités** pour chaque catégorie
- **Affichage visuel** des résultats avec barres de progression

### 📊 Entraînement et Évaluation
- **Entraînement automatique** à partir de fichiers
- **Validation croisée** (train/test split 80/20)
- **Métriques complètes**: Accuracy, Precision, Recall, F1-Score
- **Matrice de confusion** pour analyse détaillée

### 🔤 Traitement du Langage Naturel
- **Tokenization** avec SAFAR Tokenizer
- **Stemming** avec ISRI Stemmer
- **Filtrage des stop words** arabes
- **Support UTF-8** natif pour l'arabe

### 🖥️ Interface Web
- **Interface moderne et responsive**
- **Drag & Drop** pour upload de fichiers
- **Prévisualisation** du contenu
- **Animations fluides** et feedback visuel
- **Export des résultats** en JSON
- **Mode impression** optimisé

---

## 🛠️ Technologies

### Backend
- **Java 17** - Langage de programmation
- **Spring Boot 3.2.0** - Framework web
- **Spring MVC** - Architecture MVC
- **Thymeleaf** - Moteur de templates
- **Maven** - Gestion des dépendances

### NLP & Machine Learning
- **SAFAR v2** - Bibliothèque NLP arabe
  - SAFARTokenizer pour la tokenization
  - ISRI Stemmer pour le stemming
- **Naive Bayes** - Algorithme de classification
- **Laplace Smoothing** - Lissage des probabilités

### Frontend
- **HTML5** - Structure
- **CSS3** - Stylisation moderne
- **JavaScript ES6** - Interactivité
- **Thymeleaf** - Templating côté serveur

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────┐
│         Interface Web (Thymeleaf)       │
│  (Upload, Saisie, Affichage Résultats)  │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│          WebController (Spring)         │
│  (Gestion des requêtes HTTP)            │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         Services (Logique Métier)       │
├─────────────────────────────────────────┤
│  • NaiveBayesService                    │
│  • TextPreprocessingService             │
│  • StopWordsService                     │
│  • TrainingService                      │
│  • MetricsCalculator                    │
└──────────────────┬──────────────────────┘
                   │
┌──────────────────▼──────────────────────┐
│         SAFAR Library (NLP)             │
│  • Tokenization                         │
│  • Stemming (ISRI)                      │
└─────────────────────────────────────────┘
```

---

## 🚀 Installation

### Prérequis

- **Java JDK 17** ou supérieur
- **Maven 3.6+**
- **Git** (optionnel)
- **Bibliothèques SAFAR** (SAFAR_v2.jar, jdom2-2.0.6.1.jar)

### Étape 1: Cloner le Projet

```bash
git clone https://github.com/yourusername/naive-bayes-arabic-classifier.git
cd naive-bayes-arabic-classifier
```

Ou téléchargez et extrayez le ZIP du projet.

### Étape 2: Ajouter les Bibliothèques SAFAR

Créez le dossier `lib/` et ajoutez les JARs:

```bash
mkdir lib
# Copiez SAFAR_v2.jar et jdom2-2.0.6.1.jar dans lib/
```

Structure attendue:
```
lib/
├── SAFAR_v2.jar
└── jdom2-2.0.6.1.jar
```

### Étape 3: Préparer les Données d'Entraînement

Créez le dossier `Data/` et ajoutez vos fichiers texte:

```bash
mkdir Data
```

**Format des fichiers:**
- Un fichier `.txt` par catégorie
- Nom du fichier = nom de la catégorie
- Encodage: **UTF-8**
- Contenu: Texte arabe

**Exemple:**
```
Data/
├── economie.txt      (textes économiques)
├── politique.txt     (textes politiques)
└── sports.txt        (textes sportifs)
```

### Étape 4: Compiler le Projet

```bash
mvn clean install
```

### Étape 5: Lancer l'Application

```bash
mvn spring-boot:run
```

L'application démarre sur **http://localhost:8082**

---

## ⚙️ Configuration

### application.properties

Configuration principale dans `src/main/resources/application.properties`:

```properties
# Port du serveur
server.port=8082

# Chemin des données d'entraînement
training.data.path=Data

# Taille maximale des fichiers uploadés
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Niveau de logs
logging.level.com.example.naive_bayesniene_text=DEBUG
```

### Stop Words

Personnalisez les mots vides arabes dans:
```
src/main/resources/stopwords/arabic_stopwords.txt
```

Format: un mot par ligne, lignes commençant par `#` = commentaires.

---

## 📖 Utilisation

### 1. Démarrer l'Application

```bash
mvn spring-boot:run
```

Accédez à: **http://localhost:8082**

### 2. Entraîner le Modèle

Sur la page d'accueil, cliquez sur:
- **"🎯 Entraîner le Modèle"** - Entraînement simple
- **"📊 Entraîner et Évaluer"** - Entraînement + métriques (80/20 split)

Le modèle sera entraîné sur tous les fichiers `.txt` du dossier `Data/`.

### 3. Classifier un Document

#### Option A: Upload de Fichier

1. Cliquez sur **"📄 Aller à la Classification"**
2. Sélectionnez un fichier `.txt` (ou drag & drop)
3. Cliquez sur **"🚀 Classifier le Fichier"**

#### Option B: Saisie de Texte

1. Allez à la page de classification
2. Saisissez votre texte arabe dans la zone de texte
3. Cliquez sur **"🚀 Classifier le Texte"**

### 4. Interpréter les Résultats

La page de résultats affiche:

```
┌─────────────────────────────────────┐
│ 🎯 Catégorie Prédite: economie     │
│ Confiance: 92.5%                    │
├─────────────────────────────────────┤
│ 📊 Probabilités:                    │
│ economie    ████████████ 92.5%     │
│ politique   ██░░░░░░░░░░  5.2%     │
│ sports      █░░░░░░░░░░░  2.3%     │
├─────────────────────────────────────┤
│ 📈 Statistiques:                    │
│ Tokens totaux: 150                  │
│ Tokens uniques: 87                  │
└─────────────────────────────────────┘
```

---

## 🔌 API Endpoints

### Interface Web (HTML)

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/` | Page d'accueil |
| GET | `/upload` | Page de classification |
| POST | `/train` | Entraîner le modèle |
| POST | `/train-evaluate` | Entraîner et évaluer |
| POST | `/classify-file` | Classifier un fichier |
| POST | `/classify-text` | Classifier du texte |

### Exemples de Requêtes

#### Entraîner le Modèle
```bash
curl -X POST http://localhost:8082/train
```

#### Classifier du Texte (via formulaire)
```bash
curl -X POST http://localhost:8082/classify-text \
  -d "text=النص العربي للتصنيف"
```

---

## 🧮 Algorithme Naive Bayes

### Principe

L'algorithme Naive Bayes calcule la probabilité qu'un document appartienne à chaque catégorie en utilisant le théorème de Bayes:

```
P(C|D) = P(D|C) × P(C) / P(D)
```

Où:
- **P(C|D)** = Probabilité de la catégorie C sachant le document D
- **P(D|C)** = Vraisemblance du document sachant la catégorie
- **P(C)** = Probabilité a priori de la catégorie
- **P(D)** = Probabilité du document (constante)

### Implémentation

```java
// 1. Probabilité a priori
P(C) = nombre_docs_categorie_C / total_documents

// 2. Probabilité de chaque mot
P(w|C) = (count(w, C) + 1) / (total_mots_C + taille_vocabulaire)
         └─ Laplace smoothing

// 3. Probabilité du document
log P(C|D) = log P(C) + Σ log P(w|C) pour chaque mot w
```

### Laplace Smoothing

Pour éviter les probabilités nulles:
```
P(w|C) = (count(w, C) + 1) / (total_mots_C + |V|)
```

---

## 📊 Métriques d'Évaluation

### Accuracy (Exactitude)
Pourcentage de prédictions correctes:
```
Accuracy = (TP + TN) / Total
```

### Precision (Précision)
Parmi les documents classés dans une catégorie, combien sont corrects:
```
Precision = TP / (TP + FP)
```

### Recall (Rappel)
Parmi les documents d'une catégorie, combien sont trouvés:
```
Recall = TP / (TP + FN)
```

### F1-Score
Moyenne harmonique de Precision et Recall:
```
F1 = 2 × (Precision × Recall) / (Precision + Recall)
```

### Matrice de Confusion

```
                Prédictions
              ┌─────┬─────┬─────┐
              │ Eco │ Pol │ Spt │
        ┌─────┼─────┼─────┼─────┤
Réel    │ Eco │  45 │  2  │  1  │
        │ Pol │  1  │  38 │  3  │
        │ Spt │  0  │  2  │  42 │
        └─────┴─────┴─────┴─────┘
```

---

## 📁 Structure du Projet

```
naive-bayesniene-text/
├── src/
│   ├── main/
│   │   ├── java/com/example/naive_bayesniene_text/
│   │   │   ├── NaiveBayesApplication.java          # Main
│   │   │   ├── controller/
│   │   │   │   └── WebController.java              # Contrôleur web
│   │   │   ├── service/
│   │   │   │   ├── NaiveBayesService.java          # Algorithme NB
│   │   │   │   ├── SimpleTextPreprocessingService.java  # Prétraitement
│   │   │   │   ├── StopWordsService.java           # Stop words
│   │   │   │   └── TrainingService.java            # Gestion données
│   │   │   ├── model/
│   │   │   │   ├── ClassificationResult.java       # Résultat
│   │   │   │   ├── TrainingDocument.java           # Document
│   │   │   │   └── EvaluationMetrics.java          # Métriques
│   │   │   └── utils/
│   │   │       └── MetricsCalculator.java          # Calcul métriques
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── index.html                      # Page accueil
│   │       │   ├── upload.html                     # Page upload
│   │       │   └── result.html                     # Page résultats
│   │       ├── static/
│   │       │   ├── css/style.css                   # Styles
│   │       │   └── js/script.js                    # JavaScript
│   │       ├── stopwords/
│   │       │   └── arabic_stopwords.txt            # Mots vides
│   │       └── application.properties              # Configuration
│   └── test/
├── Data/                                           # Données entraînement
│   ├── economie.txt
│   ├── politique.txt
│   └── sports.txt
├── lib/                                            # Bibliothèques
│   ├── SAFAR_v2.jar
│   └── jdom2-2.0.6.1.jar
├── pom.xml                                         # Configuration Maven
└── README.md                                       # Ce fichier
```

---

## 🔧 Dépannage

### Problème: Erreur de compilation SAFAR

**Symptôme:**
```
package safar.util.tokenization.impl does not exist
```

**Solution:**
1. Vérifiez que `lib/SAFAR_v2.jar` existe
2. Vérifiez le chemin dans `pom.xml`:
```xml
<systemPath>${project.basedir}/lib/SAFAR_v2.jar</systemPath>
```
3. Nettoyez et recompilez:
```bash
mvn clean install
```

### Problème: Modèle non entraîné

**Symptôme:**
```
Model not trained yet!
```

**Solution:**
1. Vérifiez que `Data/` contient des fichiers `.txt`
2. Cliquez sur "🎯 Entraîner le Modèle"
3. Attendez le message de succès

### Problème: Fichier non accepté

**Symptôme:**
Fichier rejeté lors de l'upload

**Solution:**
- Format accepté: `.txt` uniquement
- Encodage requis: **UTF-8**
- Taille max: 10 MB
- Contenu: Texte arabe non vide

### Problème: Port 8082 déjà utilisé

**Symptôme:**
```
Port 8082 is already in use
```

**Solution:**
Changez le port dans `application.properties`:
```properties
server.port=8083
```

### Problème: Caractères arabes mal affichés

**Solution:**
1. Vérifiez l'encodage UTF-8 de vos fichiers
2. Dans votre éditeur, sauvegardez en UTF-8 **sans BOM**
3. Vérifiez que `application.properties` contient:
```properties
spring.http.encoding.charset=UTF-8
spring.http.encoding.force=true
```

---

## 🎓 Exemple Complet

### 1. Préparer les Données

**Data/economie.txt:**
```
الاقتصاد المغربي ينمو بشكل مستمر في السنوات الأخيرة.
البنك المركزي يعلن عن خفض معدلات الفائدة.
الاستثمار الأجنبي في المغرب يشهد ارتفاعا ملحوظا.
```

**Data/politique.txt:**
```
البرلمان يصوت على قانون جديد للانتخابات.
الحكومة تعلن عن إصلاحات سياسية جديدة.
الأحزاب السياسية تناقش التعديلات الدستورية.
```

**Data/sports.txt:**
```
المنتخب الوطني يفوز في مباراة مثيرة.
البطولة الوطنية تشهد منافسة قوية هذا العام.
اللاعب المغربي يتألق في الدوري الأوروبي.
```

### 2. Entraîner

```bash
mvn spring-boot:run
# Ouvrir http://localhost:8082
# Cliquer sur "Entraîner le Modèle"
```

**Résultat:**
```
✅ Modèle entraîné avec succès!
Documents: 9
Vocabulaire: 45
Catégories: economie, politique, sports
```

### 3. Tester

**Texte à classifier:**
```
البنك يعلن عن ارتفاع الاستثمارات في القطاع المالي
```

**Résultat attendu:**
```
Catégorie: economie (89.3%)
Probabilités:
  - economie: 89.3%
  - politique: 7.2%
  - sports: 3.5%
```

---

## 📚 Ressources

### Documentation

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [SAFAR Documentation](http://arabic.emi.ac.ma/safar/)
- [Naive Bayes Classifier](https://en.wikipedia.org/wiki/Naive_Bayes_classifier)
- [Cours Text Mining PDF](./docs/Introduction_TextMining.pdf)
- [Métriques d'Évaluation PDF](./docs/Metriques_Evaluation_modele1.pdf)

### Corpora Arabes Recommandés

1. **OSAC (Open Source Arabic Corpus)**
   - 22,000+ documents
   - URL: http://www.osac.com

2. **Arabic Wikipedia Dump**
   - Millions d'articles
   - URL: https://dumps.wikimedia.org/arwiki/

3. **Tashkeela Corpus**
   - Textes classiques arabes
   - Complètement diacritisés

4. **KALIMAT Corpus**
   - 20,000+ articles de presse

### Outils

- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - IDE recommandé
- [Postman](https://www.postman.com/) - Test des APIs
- [Git](https://git-scm.com/) - Contrôle de version

---

## 🤝 Contribuer

Les contributions sont les bienvenues! Voici comment contribuer:

### 1. Fork le projet

```bash
git clone https://github.com/yourusername/naive-bayes-arabic-classifier.git
```

### 2. Créer une branche

```bash
git checkout -b feature/AmazingFeature
```

### 3. Commit vos changements

```bash
git commit -m 'Add some AmazingFeature'
```

### 4. Push vers la branche

```bash
git push origin feature/AmazingFeature
```

### 5. Ouvrir une Pull Request

---

## 📝 Licence

Ce projet est sous licence MIT. Voir le fichier [LICENSE](LICENSE) pour plus de détails.

```
MIT License

Copyright (c) 2025 [Votre Nom]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction...
```

---

## 👥 Auteurs

- **Votre Nom** - *Travail initial* - [YourGitHub](https://github.com/yourusername)

### Contributeurs

Merci à tous ceux qui ont contribué à ce projet!

---

## 📧 Contact

- **Email**: your.email@example.com
- **GitHub**: [@yourusername](https://github.com/yourusername)
- **LinkedIn**: [Votre Profil](https://linkedin.com/in/yourprofile)

---

## 🙏 Remerciements

- **Équipe SAFAR** - Pour la bibliothèque NLP arabe
- **Spring Boot Community** - Pour le framework excellent
- **Université Sidi Mohamed Ben Abdellah** - Pour le cours de Text Mining
- **Prof. Chakir LOQMAN** - Pour l'enseignement du Text Mining

---

## 📊 Statistiques du Projet

![GitHub stars](https://img.shields.io/github/stars/yourusername/naive-bayes-arabic-classifier)
![GitHub forks](https://img.shields.io/github/forks/yourusername/naive-bayes-arabic-classifier)
![GitHub issues](https://img.shields.io/github/issues/yourusername/naive-bayes-arabic-classifier)
![GitHub pull requests](https://img.shields.io/github/issues-pr/yourusername/naive-bayes-arabic-classifier)

---

## 🗺️ Roadmap

- [x] Implémentation Naive Bayes de base
- [x] Interface web avec Thymeleaf
- [x] Support SAFAR pour NLP arabe
- [x] Métriques d'évaluation
- [ ] API REST JSON
- [ ] Support de plusieurs langues
- [ ] Amélioration des algorithmes de stemming
- [ ] Déploiement Docker
- [ ] Tests unitaires complets
- [ ] Documentation API Swagger

---

## 📖 Changelog

### Version 1.0.0 (2025-01-XX)

**Ajouté:**
- ✅ Classification Naive Bayes
- ✅ Interface web complète
- ✅ Support SAFAR (tokenization, stemming)
- ✅ Filtrage stop words arabes
- ✅ Métriques d'évaluation (Precision, Recall, F1)
- ✅ Upload de fichiers
- ✅ Saisie de texte directe
- ✅ Visualisation des résultats

---

**⭐ Si ce projet vous a été utile, n'oubliez pas de lui donner une étoile sur GitHub!**

**🚀 Bon codage avec la classification de textes arabes!**

---

*Dernière mise à jour: Janvier 2025*
