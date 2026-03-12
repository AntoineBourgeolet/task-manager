# AGENTS.md

## Portee et contexte
- Monorepo `task-manager` avec backend Spring Boot multi-modules (`backend/`) + frontend Angular standalone (`frontend/`).
- Conventions AI detectees via recherche globale: uniquement `README.md` (pas de regles Copilot/Claude/Cursor dediees).

## Architecture essentielle (a comprendre avant de coder)
- Backend OpenAPI-first: les contrats sont dans `backend/contracts/*.yaml` et generent les interfaces/DTO dans `backend/api-account`, `backend/api-tag`, `backend/api-task` (voir `openapi-generator-maven-plugin` dans chaque `pom.xml`).
- Le module runnable est `backend/application`: il depend des modules `api-*` et implemente les interfaces generees via `*ApiImpl` (`TaskApiImpl`, `TagApiImpl`, `AccountApiImpl`).
- Couche backend typique: `controller -> service -> repository -> entity`, avec mappers explicites (`backend/application/src/main/java/com/bourgeolet/task_manager/mapper`).
- Flux metier critique: toute mutation de ticket emet un evenement audit en outbox (`OutboxService`), puis publication Kafka asynchrone (`OutboxProducer`, topic `outbox.events`, cron `PT2S`).

## Workflows dev utiles
- Backend build multi-module (gen OpenAPI + tests):
  - `cd backend`
  - `./mvnw.cmd clean verify`
- Lancer l'app backend en dev:
  - `cd backend`
  - `./mvnw.cmd -pl application spring-boot:run -Dspring-boot.run.profiles=dev`
- Frontend local avec proxy API:
  - `cd frontend`
  - `npm install`
  - `npm run start`
- Qualite frontend:
  - `npm run format`
  - `npm run format:check`

## Conventions projet specifiques
- Ne pas modifier le code genere sous `backend/*/target/generated-sources/**`; modifier les contrats `backend/contracts/*.yaml` puis regenerer via Maven.
- Les endpoints de creation/update backend renvoient souvent `202 Accepted` (ex: `createTask`, `patchTask`, `createTag`), pas `200/201`.
- Les DTO de mutation imposent un `actor` (audit fonctionnel): respecter ce champ cote frontend et backend.
- PATCH task = semantique "presence-aware": `TaskApiImpl` lit le JSON brut (`JsonNode.has(...)`) pour distinguer champ absent vs null, puis construit `TaskPatchCommand`.
- Les statuts doivent rester alignes entre backend (`TaskStatus` dans contrat OpenAPI) et frontend (`columnsTemplate` dans `frontend/src/environments/const.ts`).

## Points d'integration a ne pas casser
- Frontend appelle `/api/*` (`apiBaseUrl`), puis `frontend/proxy.conf.json` re-ecrit `^/api` vers backend `http://localhost:8080`.
- Si vous changez un path contractuel (ex `task/getTaskById/{taskId}`), mettez a jour simultanement:
  - contrat OpenAPI,
  - implementation `*ApiImpl`,
  - service frontend correspondant (`task.api.ts`, `tag.api.ts`, `user.api.ts`).
- Profil `dev`: Swagger + H2 console actives (`application-dev.yml`). Profil `prod`: Swagger/H2 coupes (`application-prod.yml`).

## Tests et verification
- Backend: tests unitaires JUnit5 + Mockito dans `backend/application/src/test/java` (ex: `TaskServiceTest`, `TaskApiImplTest`).
- Frontend: Vitest configure (`tsconfig.spec.json`) mais peu/pas de specs presentes; eviter d'annoncer une couverture frontend sans verification.
- Couverture/CI: `sonar-project.properties` exclut notamment `dto`, `mapper`, `config`, `generated-sources`, `kafka/outbox`.
