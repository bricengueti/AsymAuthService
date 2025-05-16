
# 🔐 AsymAuthService

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)](https://spring.io/projects/spring-boot)  
[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)  
[![JWT](https://img.shields.io/badge/JWT-Secure-blue.svg)](https://jwt.io)  
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)

**AsymAuthService** est un service d'authentification moderne utilisant des JWT signés par une paire de clés RSA générée dynamiquement au démarrage. La vérification des tokens est déléguée à une API Gateway grâce à une clé publique exposée via un endpoint JWKS.

---

## 📋 Table des matières

- [📐 Architecture](#-architecture)
- [🧰 Technologies](#-technologies)
- [🚀 Démarrage rapide](#-démarrage-rapide)
- [🔐 API Auth](#-api-auth)
- [📘 Endpoint JWKS](#-endpoint-jwks)
- [📦 Structure du projet](#-structure-du-projet)
- [✨ Fonctionnalités](#-fonctionnalités)

---

## 📐 Architecture

- 🛡 **Auth Service** : gère l'inscription, la connexion, les tokens JWT et l'exposition de la clé publique
- 🌐 **API Gateway** : filtre les requêtes entrantes et valide les tokens grâce à la clé publique fournie
- 🔑 **JWT asymétriques** : les tokens sont signés avec une clé privée et validés par la clé publique exposée

---

## 🧰 Technologies

- Java 17
- Spring Boot 3.2+
- Spring Security
- JWT (avec RSA)
- Maven
- Docker (optionnel)

---

## 🚀 Démarrage rapide

### Prérequis

- Java 17
- Maven 3.6+
- Docker (si déploiement containerisé)

### Lancement local

```bash
git clone https://github.com/ton-utilisateur/AuthCrypto.git
cd AuthCrypto
mvn clean install
mvn spring-boot:run
🔐 API Auth
🧾 Enregistrement
http
Copier
Modifier
POST /auth/register
Content-Type: application/json
json
Copier
Modifier
{
  "username": "johndoe",
  "email": "john@doe.com",
  "password": "123456",
  "firstName": "John",
  "lastName": "Doe"
}
🔓 Connexion
http
Copier
Modifier
POST /auth/login
Content-Type: application/json
json
Copier
Modifier
{
  "username": "johndoe",
  "password": "123456"
}
Réponse :

json
Copier
Modifier
{
  "accessToken": "eyJhbGciOiJSUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIs...",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
🔄 Rafraîchir un token
http
Copier
Modifier
POST /auth/refresh-token
Content-Type: application/json
json
Copier
Modifier
{
  "refreshToken": "..."
}
📘 Endpoint JWKS
L'API Gateway peut récupérer la clé publique pour valider les tokens en interrogeant :

http
Copier
Modifier
GET /.well-known/jwks
Réponse :

json
Copier
Modifier
{
  "keys": [
    {
      "kty": "RSA",
      "kid": "auth-key",
      "alg": "RS256",
      "use": "sig",
      "n": "...",
      "e": "AQAB"
    }
  ]
}
📦 Structure du projet
bash
Copier
Modifier
AuthCrypto/
├── config/                 # Configuration de sécurité
├── controller/             # Endpoints REST
├── service/                # Services métier
├── model/                  # Entités / DTO
├── repository/             # Accès base de données
└── util/                   # Génération de clé RSA, JWT, etc.
✨ Fonctionnalités
🔐 Authentification par JWT avec RSA

🔄 Rafraîchissement sécurisé des tokens

📤 Clé publique exposée en JWKS pour les services externes

🧼 Révocation automatique des tokens invalides

🧩 Architecture prête pour microservices avec API Gateway

🌍 CORS configurable et sécurité stateless

🛠️ Extensible pour OAuth2, LDAP, MFA...

📜 Licence
Distribué sous licence MIT. Voir LICENSE pour plus d’informations.

perl
Copier
Modifier

Souhaite-tu que je t’ajoute un schéma d’architecture ou un `docker-compose.yml` ?







