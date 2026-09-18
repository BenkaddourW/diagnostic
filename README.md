# Diagnostic médical — test technique

## Présentation du projet

Cette application Java associe un index de santé à des unités médicales selon des
règles de divisibilité, puis retourne leurs noms dans une chaîne de caractères.
Elle a été développée dans le cadre d'un test technique et expose également ce
calcul via une API REST.

Les règles métier ci-dessous sont issues du sujet du test technique. Les décisions
non spécifiées par le sujet sont présentées dans la section Hypothèses de conception.

## Règles métier

| Condition | Unité retournée |
|---|---|
| Index multiple de 3 | `Cardiologie` |
| Index multiple de 5 | `Traumatologie` |
| Index multiple de 3 et de 5 | `Cardiologie, Traumatologie` |

Exemples : `33` → `Cardiologie`, `55` → `Traumatologie`,
`15` → `Cardiologie, Traumatologie`.

Le séparateur est une virgule suivie d'une espace (`", "`). Un index positif ne
correspondant à aucune règle, par exemple `7`, produit une chaîne vide.

## Choix techniques

| Technologie | Usage dans le projet |
|---|---|
| Java 21 | Typage statique, enum pour les unités et record immuable pour les règles |
| Spring Boot 4.1.1 / Spring MVC | Démarrage de l'application, injection du service, routage HTTP et gestion des erreurs |
| Maven 3.9.16 via le wrapper | Compilation, exécution des tests et production du JAR exécutable |
| JUnit Jupiter | Tests unitaires, tests MVC et tests paramétrés |
| AssertJ | Assertions lisibles sur les résultats métier et les exceptions |
| MockMvc | Vérification des réponses HTTP et de la spécification générée, sans serveur réseau |
| springdoc-openapi 3.1.1 / Swagger UI | Génération OpenAPI et consultation interactive de l'API |

Les versions des bibliothèques de test sont gérées par le parent Spring Boot.
La validation de positivité est réalisée explicitement dans le service.

## Architecture

```text
DiagnosticController
    → DiagnosticService
        → DiagnosticRuleCatalog
            → DiagnosticRule / MedicalUnit
```

| Composant | Responsabilité |
|---|---|
| `DiagnosticController` | Exposer `GET /api/v1/diagnostics/{healthIndex}` et documenter le contrat HTTP |
| `DiagnosticService` | Valider la positivité, évaluer les règles, dédupliquer les unités et produire la chaîne |
| `DiagnosticRuleCatalog` | Fournir la liste immuable et ordonnée des règles actuelles |
| `DiagnosticRule` | Associer un diviseur strictement positif à une unité non nulle et évaluer la divisibilité |
| `MedicalUnit` | Définir les unités disponibles et leurs libellés français |
| `InvalidHealthIndexException` | Signaler le rejet d'un index non positif par le service |
| `DiagnosticExceptionHandler` | Convertir les erreurs de positivité et de conversion en HTTP 400 avec `ProblemDetail` |
| `OpenApiConfig` | Définir les métadonnées de la documentation de l'API |
| `DiagnosticApplication` | Démarrer Spring Boot |

Les packages de production, sous `src/main/java/com/softwaymedical/diagnostic`,
sont `controller`, `service`, `domain`, `exception` et `config`.
Les tests reprennent cette organisation sous `src/test/java`.

Le domaine ne dépend ni de Spring ni de HTTP. Le catalogue statique est un choix
de simplicité pour des règles fixes : il n'est pas configurable à l'exécution.
Le service retourne directement la chaîne demandée, sans objet résultat
intermédiaire. Aucune base de données ni aucun service externe n'est nécessaire.

## Extensibilité

Pour ajouter ultérieurement une règle de divisibilité :

1. Ajouter une constante à `MedicalUnit` seulement si l'unité n'existe pas encore.
2. Ajouter un `DiagnosticRule` dans la liste de `DiagnosticRuleCatalog`, à la
   position correspondant à l'ordre de sortie souhaité.
3. Actualiser les tests du catalogue, les tests fonctionnels et la documentation.

L'algorithme du service et le routage du contrôleur n'ont pas à changer. Plusieurs
règles peuvent cibler la même unité : `distinct()` assure qu'elle n'apparaît qu'une
fois, à la position de sa première règle correspondante. La sortie suit l'ordre
du catalogue, et non l'ordre de déclaration de l'enum.

Cette extensibilité concerne les règles de divisibilité. Des règles d'une autre
nature nécessiteraient de réexaminer le modèle. Aucune nouvelle règle n'est
implémentée au-delà des deux règles décrites plus haut.

## Prérequis

- Un **JDK 21**, avec `JAVA_HOME` pointant vers ce JDK et son dossier `bin` dans
  `PATH`. Un JRE seul ne suffit pas pour compiler ou générer la JavaDoc.
- Un accès aux dépôts Maven lors du premier téléchargement des outils et dépendances.
- Un port `8080` disponible pour le lancement par défaut.
- Pour les exemples HTTP en ligne de commande : `curl.exe` sous Windows ou `curl`
  sous Linux/macOS.

Le Maven Wrapper est inclus : `mvnw.cmd` pour Windows, `mvnw` pour Linux/macOS.
Il utilise la distribution Maven 3.9.16 définie dans
`.mvn/wrapper/maven-wrapper.properties` ; aucune installation globale de Maven
n'est nécessaire.

## Installation et lancement

Après récupération du projet depuis
[GitHub](https://github.com/BenkaddourW/diagnostic) ou extraction de ses sources,
ouvrir un terminal dans le dossier contenant `pom.xml` et les scripts du wrapper.

### Windows — PowerShell

Vérifier le JDK effectivement utilisé par Maven :

```powershell
.\mvnw.cmd -version
```

Construire et lancer le JAR :

```powershell
.\mvnw.cmd -B -ntp clean verify
java -jar .\target\diagnostic-0.0.1-SNAPSHOT.jar
```

Autre possibilité pour le développement :

```powershell
.\mvnw.cmd -B -ntp spring-boot:run
```

Arrêter l'application avec `Ctrl+C`. Les deux modes de lancement sont des
alternatives ; ne pas les démarrer simultanément sur le même port.

Si `8080` est déjà occupé, choisir par exemple `18080` :

```powershell
java -jar .\target\diagnostic-0.0.1-SNAPSHOT.jar --server.port=18080
```

Ou avec le plugin Maven :

```powershell
.\mvnw.cmd -B -ntp spring-boot:run "-Dspring-boot.run.arguments=--server.port=18080"
```

Remplacer alors `8080` par `18080` dans toutes les URL de ce document. Les
vérifications de lancement et des exemples HTTP sous Windows ont utilisé ce
port alternatif, car `8080` était occupé dans l'environnement de vérification.

### Linux/macOS — shell POSIX

Le workflow GitHub Actions a réussi sur son environnement Ubuntu en exécutant
`./mvnw -B -ntp clean verify`. Les autres commandes ci-dessous n'ont pas été
vérifiées sous Linux ; aucune commande macOS n'a été exécutée.

```sh
chmod +x mvnw
./mvnw -version
./mvnw -B -ntp clean verify
java -jar target/diagnostic-0.0.1-SNAPSHOT.jar
```

Ou, pour le développement :

```sh
./mvnw -B -ntp spring-boot:run
```

## Tests

| Action | Windows PowerShell | Linux/macOS |
|---|---|---|
| Compiler et exécuter les tests | `.\mvnw.cmd -B -ntp test` | `./mvnw -B -ntp test` |
| Reconstruire, tester et produire le JAR | `.\mvnw.cmd -B -ntp clean verify` | `./mvnw -B -ntp clean verify` |

Les tests présents couvrent trois niveaux :

- **Unitaires**, sans contexte Spring : `DiagnosticServiceTest` (6 cas),
  `DiagnosticRuleTest` (5 cas) et `DiagnosticRuleCatalogTest` (1 cas).
- **Tranche MVC** : `DiagnosticControllerTest` (14 cas exécutés), avec
  `@WebMvcTest` et import du vrai service et de l'advice. Les assertions portent
  sur les chaînes, statuts HTTP, médias et champs des erreurs, y compris avec
  `Accept: text/plain`.
- **Intégration du contexte Spring** : `DiagnosticApplicationTest` (2 cas), avec
  `@SpringBootTest` et MockMvc, vérifie la spécification produite par `/v3/api-docs`.

La suite actuelle comporte **28 cas exécutés**, incluant les invocations du test
paramétré. Les rapports sont générés dans `target/surefire-reports`.
Ces tests n'ouvrent pas de serveur HTTP réseau. JaCoCo mesure leur couverture
du code de production ; les rapports sont décrits ci-dessous.

Mockito est fourni par les dépendances de test Spring Boot. Le listener Spring
de remise à zéro des mocks initialise son moteur même sans mock explicite.
Surefire charge donc Mockito comme agent au démarrage, avec le chemin résolu
par `maven-dependency-plugin:properties`. La version reste gérée par Spring Boot.
La configuration `@{argLine}` conserve aussi l'agent JaCoCo. Aucune option de
masquage du chargement dynamique n'est utilisée. Pour obtenir cette configuration
depuis un IDE, déléguer l'exécution des tests à Maven.

L'avertissement JVM « Sharing is only supported for boot loader classes because
bootstrap classpath has been appended » peut subsister avec l'instrumentation.
Il est distinct du chargement dynamique de l'agent Mockito, désormais évité.

Pour générer la JavaDoc sous Windows :

```powershell
.\mvnw.cmd -B -ntp javadoc:javadoc
```

Sous Linux/macOS, utiliser `./mvnw -B -ntp javadoc:javadoc`.
La documentation HTML est générée dans `target/reports/apidocs/index.html`.

La génération conserve six avertissements de documentation manquante : les
constructeurs implicites de `DiagnosticApplication`, `DiagnosticService`,
`DiagnosticExceptionHandler` et `OpenApiConfig`, ainsi que les deux constantes
de `MedicalUnit`. Aucun constructeur explicite ni commentaire répétant un nom
n'est ajouté uniquement pour supprimer ces avertissements. Le constructeur
d'injection de `DiagnosticController` documente, lui, la répartition des
responsabilités et son paramètre. Les contrôles JavaDoc restent actifs.

## Qualité automatique

Le cycle `verify` contrôle le formatage et produit les rapports de couverture.
La configuration est centralisée dans `pom.xml`.

| Outil | Version | Rôle |
|---|---|---|
| Spotless | 3.10.2 | Vérification et correction du formatage Java |
| Google Java Format | 1.30.0 | Style Google déterministe, sans fichier de règles supplémentaire |
| JaCoCo | 0.8.15 | Instrumentation des tests et rapports par classe et package |

Spotless couvre `src/main/java` et `src/test/java`, avec des fins de ligne LF,
également fixées par `.gitattributes`. Le contrôle échoue si le formatage diffère ;
seule la commande `apply` réécrit les sources. Checkstyle n'est pas ajouté pour
éviter un second ensemble de règles de style.

```powershell
# Vérifier le formatage sans modifier les fichiers
.\mvnw.cmd -B -ntp spotless:check
# Corriger le formatage
.\mvnw.cmd -B -ntp spotless:apply
# Recompiler, exécuter les tests, contrôler le style et générer la couverture
.\mvnw.cmd -B -ntp clean verify
# Régénérer les rapports depuis les données du dernier passage de tests
.\mvnw.cmd -B -ntp jacoco:report
```

Sous Linux/macOS, remplacer `.\mvnw.cmd` par `./mvnw`.

JaCoCo écrit les données dans `target/jacoco.exec` et les rapports dans
`target/site/jacoco/index.html` (HTML) et `target/site/jacoco/jacoco.xml` (XML).
La commande `jacoco:report` seule n'exécute pas les tests et requiert les données
et classes correspondantes ; privilégier `clean verify` pour une mesure fraîche.
Aucune exclusion de classe et aucun seuil bloquant ne sont configurés. Les
filtres internes de JaCoCo pour le code généré par le compilateur restent actifs.
La couverture doit être lue avec les scénarios de test : elle ne prouve pas à
elle seule la qualité des assertions.

Mesure de référence du 18 septembre 2026, après `clean verify` avec les 28 cas
existants, sans exclusion configurée :

| Métrique | Couvert / total | Pourcentage |
|---|---|---|
| Instructions | 164 / 169 | 97,04 % |
| Lignes | 46 / 48 | 95,83 % |
| Branches | 6 / 6 | 100 % |
| Méthodes | 21 / 22 | 95,45 % |
| Classes | 9 / 9 | 100 % |

Les deux lignes non couvertes sont celles de `DiagnosticApplication.main` : le
contexte est chargé par les tests Spring sans appel direct au point d'entrée.
Aucune branche comptabilisée par JaCoCo n'est manquante : quatre sont mesurées
dans `DiagnosticRule` et deux dans `DiagnosticService`. Cela ne signifie pas que
toutes les combinaisons métier sont testées ; par exemple, le catalogue actuel
ne permet pas d'exercer deux règles correspondant à une même unité.
Ces chiffres constituent un instantané : les rapports régénérés font foi après
toute modification. Aucun seuil n'est imposé à partir de cette seule mesure.

Versions des plugins d'appui gérées par Spring Boot 4.1.1 : Surefire 3.5.6,
Maven Dependency Plugin 3.10.0 et Maven Javadoc Plugin 3.12.0.

## Intégration continue — GitHub Actions

Le workflow `.github/workflows/ci.yml`, nommé **CI**, s'exécute sur les événements
`push` et `pull_request` (ouverture, synchronisation et réouverture). Il utilise
Ubuntu 24.04, un JDK 21 Eclipse Temurin et le Maven Wrapper du projet.

Après récupération des sources et restauration du cache Maven, le workflow rend
`mvnw` exécutable, puis lance une seule commande :

```sh
./mvnw -B -ntp clean verify
```

Cette commande compile le projet, exécute une seule fois la suite existante
(tests unitaires, tranche Spring MVC et intégration OpenAPI), vérifie Spotless
et génère les rapports JaCoCo grâce aux liaisons déjà présentes dans le POM.
La JavaDoc n'est pas incluse dans cette CI ; elle conserve sa commande dédiée.
L'équivalent local sous Windows est :

```powershell
.\mvnw.cmd -B -ntp clean verify
```

Le cache Maven est fourni par `actions/setup-java`, avec une clé dépendant du
POM et des propriétés du wrapper. Les actions officielles sont figées par SHA,
avec leur version indiquée en commentaire : `actions/checkout` 7.0.1,
`actions/setup-java` 6.0.1 et `actions/upload-artifact` 7.0.1. Le workflow accorde
uniquement `contents: read`, ne conserve pas les identifiants Git dans le checkout
et n'effectue aucun déploiement ni publication de paquet. Aucun secret à fournir
manuellement n'est nécessaire.

Sur GitHub, ouvrir **Actions → CI → une exécution → Java 21 - Maven verify** pour
consulter les journaux. Si l'archivage a abouti, la section **Artifacts** propose
l'archive `test-and-coverage-reports`, conservée 14 jours, avec les fichiers
disponibles :

- rapports Surefire XML et texte ;
- rapport JaCoCo HTML, ses ressources et le XML ;
- données brutes `jacoco.exec`.

L'étape d'archivage utilise `always()` pour être tentée même après un échec du
build. Elle conserve les rapports déjà produits sans relancer les tests.
Si Maven s'arrête avant `verify`, le rapport JaCoCo HTML/XML peut ne pas exister ;
les rapports Surefire et les données brutes sont conservés s'ils sont présents.
Une absence totale de fichiers produit un avertissement, sans masquer le statut
d'échec initial.

Le projet est publié sur
[GitHub](https://github.com/BenkaddourW/diagnostic). Le premier push sur `main`
a déclenché le workflow CI, dont la première exécution affichait un statut de
réussite sur Ubuntu. Le détail des artefacts et le déclenchement sur Pull Request
restent à vérifier. Aucun badge n'est ajouté.

## API REST

Adresse de base par défaut : `http://localhost:8080`.

```http
GET /api/v1/diagnostics/{healthIndex}
```

`healthIndex` est un paramètre de chemin obligatoire, converti en `int` Java.
L'implémentation accepte les valeurs de `1` à `2147483647` incluses.

### Succès

HTTP **200**, `Content-Type: text/plain; charset=UTF-8`.
Le corps contient le libellé français, la liste séparée par `", "`, ou aucun
caractère si aucune règle ne correspond. Il ne s'agit pas d'une chaîne JSON :
aucun guillemet n'est ajouté autour du corps.

Dans un second terminal PowerShell, après démarrage de l'application :

```powershell
curl.exe -i -H "Accept: text/plain" http://localhost:8080/api/v1/diagnostics/33
curl.exe -i -H "Accept: text/plain" http://localhost:8080/api/v1/diagnostics/55
curl.exe -i -H "Accept: text/plain" http://localhost:8080/api/v1/diagnostics/15
curl.exe -i -H "Accept: text/plain" http://localhost:8080/api/v1/diagnostics/7
```

Les corps attendus sont respectivement `Cardiologie`, `Traumatologie`,
`Cardiologie, Traumatologie` et une chaîne vide. Sous Linux/macOS, remplacer
`curl.exe` par `curl`.

### Entrées invalides

HTTP **400**, `Content-Type: application/problem+json`, pour les erreurs de
positivité ou de conversion du paramètre. Le titre est `Invalid health index`.

| Entrées | Champ `detail` |
|---|---|
| `0`, `-15` | `Health index must be a positive integer.` |
| `abc`, `1.5`, `2147483648` | `Health index must be a whole number between 1 and 2147483647.` |

Exemple :

```powershell
curl.exe -i -H "Accept: text/plain, application/problem+json" http://localhost:8080/api/v1/diagnostics/abc
```

Corps d'erreur représentatif, indépendamment de l'ordre des propriétés JSON :

```json
{
  "status": 400,
  "title": "Invalid health index",
  "detail": "Health index must be a whole number between 1 and 2147483647.",
  "instance": "/api/v1/diagnostics/abc"
}
```

`instance` identifie le chemin de la requête. Les erreurs restent en
`application/problem+json` même avec `Accept: text/plain`.
Un client peut annoncer les deux médias comme dans cet exemple. Un en-tête
`Accept` exclusivement JSON ne correspond pas au contrat de succès en texte.
Les autres erreurs de routage ou de méthode HTTP restent gérées par Spring ;
leur représentation n'est pas uniformisée par l'advice métier.

## OpenAPI et Swagger

Après lancement sur le port par défaut :

- [Swagger UI](http://localhost:8080/swagger-ui.html) : documentation interactive.
- [Spécification JSON OpenAPI](http://localhost:8080/v3/api-docs).

La spécification décrit le paramètre, les règles, les exemples de réponses,
le succès `text/plain` et l'erreur `application/problem+json` avec le schéma
`ProblemDetail`. Elle est produite à partir des annotations du contrôleur et
des métadonnées de `OpenApiConfig`.

La version documentaire de l'API est `1.0.0`, le chemin est versionné `/api/v1`,
et la version de l'artefact Maven est `0.0.1-SNAPSHOT`. Ces valeurs décrivent des
éléments distincts ; la version documentaire ne signifie pas que l'artefact est
une release publiée.

## Hypothèses de conception

Le besoin communiqué porte sur les associations par divisibilité et le retour
d'une chaîne. Les décisions suivantes complètent ce besoin :

| Choix | Comportement et compromis |
|---|---|
| Positivité stricte | Le service rejette zéro et les négatifs. Il s'agit d'une hypothèse ajoutée, pas d'une exigence confirmée ; mathématiquement, zéro est divisible par 3 et 5. |
| Chaîne vide | Une absence de correspondance retourne `""` et HTTP 200, sans unité par défaut. |
| Type `int` | L'index a une capacité bornée ; toute valeur non convertible est rejetée en HTTP 400. |
| Ordre déterministe | L'ordre du catalogue garantit actuellement Cardiologie avant Traumatologie ; chaque unité apparaît au plus une fois. |
| API REST | L'exposition HTTP complète le calcul demandé ; GET convient à ce calcul sans modification d'état. |

## Limites et évolutions possibles

Les règles et libellés sont définis dans le code : les modifier nécessite une
nouvelle compilation et livraison. Le catalogue statique simplifie le projet,
mais ne permet pas de fournir facilement un catalogue alternatif dans un test.
Une injection de règles ne serait à envisager qu'en présence de ce besoin.

Le résultat textuel respecte le besoin communiqué. Il est moins structuré pour
un consommateur souhaitant exploiter des identifiants d'unités ; toute évolution
de ce contrat demanderait une décision explicite de compatibilité.

Il n'y a ni persistance, ni configuration dynamique des règles, ni règles autres
que la divisibilité par 3 et 5. Le workflow CI a réussi lors du premier push sur
`main` ; ses artefacts et son déclenchement sur Pull Request restent à vérifier.
Le formatage et la couverture sont également disponibles localement via Maven.
