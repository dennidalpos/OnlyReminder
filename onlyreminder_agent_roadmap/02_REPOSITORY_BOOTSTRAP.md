# 02 — Repository Bootstrap

## Obiettivo

Creare la struttura iniziale del repository OnlyReminder con progetto Android, landing, media assets
e documentazione base.

## Scope

Implementare solo bootstrap repository e progetto Android base compilabile.

## Struttura richiesta

```text
OnlyReminder/
  android-app/
    app/
    core/
    data/
    domain/
    features/
    keystore/
    docs/
  landing/
    en/
    it/
    assets/
      css/
      js/
      images/
      apk/
      changelog/
  media-assets/
    logo/
    icons/
    splash/
    screenshots/
    mockups/
    social/
    banners/
    store/
    video/
  README.md
  PROJECT_PLAN.md
```

## Android

Configurare:

- Gradle Kotlin DSL;
- Kotlin;
- Jetpack Compose;
- Material 3;
- package `com.onlyreminder.app`;
- app name `OnlyReminder`;
- versione iniziale `1.0.0`;
- minSdk ragionevole per app moderna;
- targetSdk aggiornato;
- build debug funzionante.

## File base da creare

```text
README.md
PROJECT_PLAN.md
android-app/docs/android-build-guide.md
android-app/docs/release-guide.md
android-app/docs/gdpr-notes.md
android-app/docs/import-format-guide.md
android-app/docs/backup-restore-guide.md
android-app/docs/whatsapp-api-guide.md
android-app/keystore/README.md
android-app/keystore/onlyreminder-release-key.example.properties
landing/assets/changelog/changelog.md
landing/assets/apk/.gitkeep
media-assets/video/demo-placeholder.md
```

## Regole

- Non implementare ancora funzionalità app.
- Non creare backend.
- Non aggiungere analytics.
- Non aggiungere SDK non richiesti.
- Non inserire keystore reale nel repository.
- Il keystore reale deve restare fuori repo.

## Checklist implementazione

- [ ] Repository creato con struttura base.
- [ ] Progetto Android apribile in Android Studio.
- [ ] Gradle Kotlin DSL configurato.
- [ ] Compose attivo.
- [ ] Material 3 disponibile.
- [ ] Package impostato a `com.onlyreminder.app`.
- [ ] Nome app visibile `OnlyReminder`.
- [ ] Versione iniziale `1.0.0`.
- [ ] Cartelle landing create.
- [ ] Cartelle media assets create.
- [ ] Documentazione placeholder creata.
- [ ] Build debug compila.

## Criteri di accettazione

- `./gradlew assembleDebug` funziona.
- L'app installata mostra una schermata base.
- Il repository non contiene segreti.
- La struttura è coerente con gli step futuri.

## Prompt da passare all'agente

```text
Implementa lo step 02 Repository Bootstrap per OnlyReminder.

Crea la struttura repository, il progetto Android nativo Kotlin/Compose con package com.onlyreminder.app, versione 1.0.0, landing folder, media-assets folder e documentazione base. Non implementare feature funzionali. Non aggiungere backend, analytics, cloud sync o SDK non richiesti.

A fine lavoro restituisci:
- cosa hai implementato;
- file modificati/creati;
- comando build eseguito;
- eventuali limiti;
- conferma se pronto per step 03.
```
