# 🚀 Nom du projet

TASK MANAGER by Antoine Bourgeolet

## 🧰 Stack technique

- **Backend** : Java 21 · Spring Boot 4 · Maven · H2
- **Frontend** : Angular · TypeScript
- **API** : REST (Spring Web)

---
## 📡SONARQUBE CLOUD 
- https://sonarcloud.io/project/overview?id=AntoineBourgeolet_task-manager

---

## ▶️ Démarrage rapide

### Backend (Spring – Maven)
- API : http://localhost:8080
- H2 Console : http://localhost:8080/h2-console
  - JDBC URL : `jdbc:h2:mem:testdb`

### Frontend (Angular)

UI : http://localhost:4200/dashboard

---

## ⚙️ Configuration

### Backend — `application.yml`

### Variables d'environnement

| Variable | Profil | Description | Valeur par défaut (dev) |
|---|---|---|---|
| `ADMIN_INITIAL_PASSWORD` | `dev` | Mot de passe initial du compte `admin` créé au démarrage. Hashé via BCrypt avant stockage. | `admin_dev_pass` *(fallback local — ne jamais utiliser en prod)* |

#### Setup local (profil `dev`)

```powershell
# PowerShell — définir le mot de passe avant de lancer le backend
$env:ADMIN_INITIAL_PASSWORD = "monMotDePasseLocal"
cd backend
./mvnw.cmd -pl application spring-boot:run -Dspring-boot.run.profiles=dev
```

> **Note :** si `ADMIN_INITIAL_PASSWORD` n'est pas défini, le fallback `admin_dev_pass` est utilisé automatiquement.  
> **Ne jamais commiter de mot de passe réel dans `application-dev.yml`.**

Un compte `admin / admin@dev.local` est créé automatiquement au premier démarrage sur base vide.  
Les démarrages suivants sont idempotents (aucune création si l'utilisateur existe déjà).

## TODO

## 📦 Scripts utiles

### Backend

## TODO

### Frontend

```bash
ng serve
```

---

## 📁 Structure du projet

```
backend/   → Spring Boot (Java 21)
frontend/  → Angular
docs/      → Documentation
README.md
```

---
