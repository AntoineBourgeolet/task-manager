# Authentification et Autorisations Backend (V1)

## Matrice des accès par endpoint

| Endpoint | Roles autorises |
|---|---|
| `POST /task` | `USER`, `ADMIN` |
| `GET /task` | `ALL` |
| `DELETE /task/{id}` | `ADMIN` |
| `PATCH /task/{id}` | `USER`, `ADMIN` |
| `GET /task/allByStatus` | `ALL` |
| `GET /task/getTaskById/{taskId}` | `ALL` |
| `POST /tag` | `USER`, `ADMIN` |
| `GET /tag` | `ALL` |
| `DELETE /tag` | `USER`, `ADMIN` |
| `PATCH /tag` | `USER`, `ADMIN` |
| `POST /account` | `ADMIN` |
| `GET /account` | `ALL` |
| `GET /account/{username}` | `ALL` |

## Contraintes de base JWT

### Expiration du token
- Type de token: access token JWT uniquement (V1).
- Duree de vie recommandee: `8h`.
- Pas de refresh token en V1.

### Claims minimaux
- `sub`: identifiant utilisateur (username ou account id).
- `roles`: liste des roles Spring Security (ex: `ROLE_ADMIN`, `ROLE_USER`, `ROLE_OBSERVATEUR`).
- `iat`: date d'emission.
- `exp`: date d'expiration.

### Bonnes pratiques minimales
- Signature obligatoire du JWT avec une cle secrete forte (env var).
- Aucune information sensible dans les claims (pas de mot de passe, pas de secret).
- Horloge serveur et clients synchronisee pour limiter les faux expirations.

## Endpoints publics (non authentifies)

### Publics en V1
- `POST /auth/login`

### Publics en dev uniquement
- `/swagger-ui.html`
- `/v3/api-docs`
- `/h2-console/*`

### Proteges par defaut
- Tous les autres endpoints sont authentifies, puis filtres par role selon la matrice ci-dessus.

## Notes d'implementation
- Le role `ALL` signifie: utilisateur authentifie quel que soit son role (`ADMIN`, `USER`, `OBSERVATEUR`).
- Les routes publiques de dev doivent etre desactivees en production.
