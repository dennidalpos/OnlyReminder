# 03 — Android Base Architecture

## Obiettivo

Impostare l'architettura Android di base, navigation, theme, dependency injection e separazione
pragmatica delle aree funzionali.

## Scope

Questo step crea fondamenta architetturali, non feature complete.

## Architettura raccomandata

Struttura pragmatica iniziale:

```text
android-app/
  app/
  core/
    common/
    security/
    database/
    notifications/
    storage/
    ui/
  data/
    contacts/
    groups/
    templates/
    tasks/
    birthday/
    whatsapp/
    backup/
    settings/
  domain/
    contacts/
    groups/
    templates/
    tasks/
    birthday/
    whatsapp/
    backup/
    settings/
  features/
    onboarding/
    contacts/
    import/
    groups/
    tasks/
    templates/
    birthday/
    whatsapp/
    backup/
    settings/
    security/
```

Nota: non creare troppi moduli Gradle se rallenta il progetto. Usare package interni ordinati.
Separare in moduli Gradle solo dove già previsto e sostenibile.

## Componenti da predisporre

- `MainActivity`.
- Compose root.
- Navigation graph.
- Theme Material 3 blu/nero/bianco.
- Screen placeholder:
    - Onboarding;
    - Home;
    - Contacts;
    - Import;
    - Groups;
    - Tasks;
    - Templates;
    - Birthday Review;
    - WhatsApp;
    - Backup;
    - Settings.
- Common UI components:
    - app scaffold;
    - top bar;
    - empty state;
    - loading state;
    - error state;
    - confirmation dialog;
    - destructive confirmation dialog.

## Dependency Injection

Usare una soluzione semplice e coerente:

- Hilt, se già configurato bene;
- oppure manual DI se il progetto deve restare leggero.

La scelta deve essere motivata nel README tecnico.

## Regole

- Nessuna logica business pesante in UI.
- ViewModel per stato schermate.
- Repository per accesso dati.
- Use case nel domain per regole non banali.
- Nessun networking in questo step.
- Nessun invio WhatsApp.
- Nessun database definitivo ancora, salvo placeholder.

## Checklist implementazione

- [ ] Navigation graph creato.
- [ ] Theme Material 3 impostato.
- [ ] Palette blu/nero/bianco configurata.
- [ ] Placeholder screen principali creati.
- [ ] Struttura package coerente.
- [ ] Common UI components minimi creati.
- [ ] DI scelta e configurata.
- [ ] Home placeholder raggiungibile.
- [ ] Build debug compila.

## Criteri di accettazione

- L'app si apre senza crash.
- Si può navigare tra schermate placeholder.
- La struttura supporta gli step successivi.
- Nessuna feature fuori scope è stata implementata.

## Prompt da passare all'agente

```text
Implementa lo step 03 Android Base Architecture.

Configura architettura base, navigation Compose, theme Material 3, palette blu/nero/bianco, screen placeholder e common UI components. Mantieni separazione data/domain/features/core. Non implementare ancora database reale, import, backup, WhatsApp o reminder.

A fine lavoro restituisci implementazione, file modificati, controlli eseguiti e conferma readiness per step 04.
```
