# Service d'Authentification & RBAC (Auth Service)

Ce projet est un microservice d'authentification robuste, sécurisé et prêt pour la production. Il implémente une gestion complète des identités avec **JWT (JSON Web Tokens)** et un contrôle d'accès basé sur les rôles (**RBAC**) et les permissions granulaires.

Le projet est construit selon les principes de l'**Architecture Hexagonale (Ports & Adapters)** pour assurer une séparation stricte entre la logique métier (Domaine) et les détails techniques (Framework, Base de données, API).

## 🚀 Fonctionnalités Clés

- **Authentification Flexible** : Connexion via nom d'utilisateur, email ou numéro de téléphone.
- **Sécurité JWT** :
  - **Access Token** : Durée de vie courte (signé HS256).
  - **Refresh Token** : Durée de vie longue, stocké en base de données.
  - **Rotation des Refresh Tokens** : Sécurité accrue (un refresh token ne peut être utilisé qu'une seule fois).
- **RBAC & Permissions** : Gestion fine des droits (ex: `ROLE_ADMIN` possède `user:create`, `report:read`).
- **Architecture Hexagonale** : Le cœur du métier est isolé de Spring Boot et de la BDD.
- **Migrations de Base de Données** : Versionnage du schéma SQL via **Liquibase**.
- **Documentation API** : Intégration native de **Swagger UI / OpenAPI**.
- **Docker Ready** : Conteneurisation optimisée pour le déploiement cloud.

---

## 🛠 Stack Technique

- **Langage** : Java 21
- **Framework** : Spring Boot 3.5.8
- **Sécurité** : Spring Security 6, JJWT (Java JWT)
- **Base de données** :  17
- **ORM** : Spring Data JPA / Hibernate
- **Migration** : Liquibase
- **Documentation** : SpringDoc (Swagger UI)
- **Build** : Maven

---

## 🏗 Architecture du Projet

Le projet suit une structure de dossiers reflétant l'architecture hexagonale :

```text
src/main/java/com/tramasys/auth
├── adapters
│   ├── in/web          # Contrôleurs REST (Points d'entrée)
│   └── out/persistence # Implémentation des Repositories (JPA/SQL)
├── application
│   ├── dto             # Objets de transfert de données (Request/Response)
│   └── service         # Orchestration des cas d'utilisation (Logique applicative)
├── config              # Configuration Spring (Security, Cors, Swagger)
├── domain
│   ├── exception       # Exceptions métier
│   ├── model           # Entités pures du domaine (User, Role, Token)
│   └── port            # Interfaces (Ports In/Out) définissant les contrats
└── util                # Utilitaires (JwtUtil, UserContext)
```

## 📖 Documentation API (Swagger)

Toute la documentation détaillée des endpoints (paramètres, corps JSON, codes de réponse) est générée automatiquement et accessible via une interface web interactive.
Une fois l'application lancée, accédez à :

```bash
http://localhost:8080/swagger-ui.html
```

### Comment tester dans Swagger ?

- Utilisez l'endpoint POST /auth/login pour obtenir un token.
- Copiez l'accessToken de la réponse.
- Cliquez sur le bouton Authorize (cadenas) en haut de la page Swagger.
- Entrez le token sous la forme : Bearer VOTRE_TOKEN_ICI.
- Vous pouvez maintenant tester les routes protégées (ex: /api/auth/me).

## ⚙️ Installation et Démarrage

### Prérequis

- Java 21 (JDK)
- PostgreSQL (Local ou Docker)
- Maven (Le wrapper mvnw est inclus)

### 1. Configuration de la Base de Données

Assurez-vous d'avoir une instance PostgreSQL qui tourne.
Par défaut, l'application attend :
DB Name : authdb
User : auth_user
Password : auth_pass
Pour lancer une BDD rapidement via Docker :

```bash
docker run --name auth-db -e POSTGRES_USER=auth_user -e POSTGRES_PASSWORD=auth_pass -e POSTGRES_DB=authdb -p 5432:5432 -d postgres:alpine
```

### 2. Lancement en local

Clonez le projet et lancez-le via le wrapper Maven :

```bash
git clone https://github.com/votre-utilisateur/auth_service.git
cd auth_service
```

# Compiler et lancer

```bash
./mvnw spring-boot:run
```

L'application sera accessible sur http://localhost:8080.

## 🐳 Déploiement avec Docker

Le projet inclut un Dockerfile optimisé (Multi-stage build).

### 1. Construire l'image

```bash
docker build -t auth-service .
```

### 2. Lancer le conteneur

Vous pouvez surcharger les variables d'environnement au démarrage pour la production :

```bash
docker run -d -p 8080:8080 \
  -e DB_USER=mon_user_prod \
  -e DB_PASSWORD=mon_password_prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://mon-serveur-db:5432/authdb \
  -e AUTH_JWT_SECRET=MA_CLE_SECRETE_TRES_LONGUE_ET_SECURISEE \
  --name auth-service-container \
  auth-service
```

## 🔑 Variables d'Environnement

Voici les variables clés à configurer, notamment pour le déploiement (DigitalOcean, AWS, etc.) :
|Variable | Description | Valeur par défaut (Dev) |
|---------|--------------|-------------------------|
SPRING_DATASOURCE_URL | URL de connexion JDBC | jdbc:postgresql://localhost:5432/authdb |
DB_USER | Utilisateur BDD | ROOT_USERNAME ("postgres" par défaut) |
DB_PASSWORD | Mot de passe BDD | ROOT_PASSWORD |
AUTH_JWT_SECRET | Clé secrète de signature (min 32 chars) | (Une clé par défaut est fournie pour le dev) |
SERVER_PORT | Port d'écoute du serveur | 8080 |

## 🛡 Sécurité

Mots de passe : Hachés avec BCrypt avant stockage.
Stateless : Aucune session HTTP n'est stockée côté serveur.
Protection CSRF : Désactivée (inutile pour une API REST stateless).
CORS : Configurable via CorsConfig (par défaut permissif pour le développement, à restreindre en production).

## 👤 Auteurs

Projet développé par Brayan KUATE pour TraMaSys.
