# 16 — Release, Signing and Checksums

## Obiettivo

Configurare build release, firma APK, checksum, changelog e documentazione release.

## Scope

Distribuzione APK firmata tramite landing statica.

## Requisiti

- Build debug.
- Build release.
- APK firmato.
- Keystore configurabile.
- Keystore reale non committato.
- File checksum.
- Changelog.
- Versionamento semantico.

## Nomi richiesti

APK:

```text
OnlyReminder-v1.0.0-release.apk
```

Keystore:

```text
onlyreminder-release-key.jks
```

Example properties:

```text
onlyreminder-release-key.example.properties
```

## Documentazione release

Aggiornare:

```text
android-app/docs/release-guide.md
```

Deve spiegare:

- come generare keystore;
- come configurare signing;
- come produrre APK release;
- come calcolare checksum;
- dove copiare APK nella landing;
- come aggiornare changelog;
- come verificare firma APK.

## Checksum

Creare:

```text
landing/assets/apk/checksums.txt
```

Contenuto minimo:

```text
SHA256  OnlyReminder-v1.0.0-release.apk  <hash>
```

## Changelog

Creare/aggiornare:

```text
landing/assets/changelog/changelog.md
```

Versione iniziale:

```text
1.0.0
```

## Checklist implementazione

- [ ] Signing config Gradle predisposta.
- [ ] Keystore reale escluso da git.
- [ ] Example properties creato.
- [ ] Release guide aggiornata.
- [ ] Comando assembleRelease documentato.
- [ ] APK release generabile.
- [ ] APK rinominato correttamente.
- [ ] APK copiabile in landing assets.
- [ ] Checksum SHA-256 generato.
- [ ] Changelog aggiornato.
- [ ] Istruzioni verifica firma aggiunte.
- [ ] Build debug ancora funzionante.
- [ ] Build release funzionante.

## Criteri di accettazione

- È possibile generare APK release firmato.
- Il keystore reale non è nel repository.
- Il checksum è generato.
- La landing ha file APK e checksum.
- La release guide è sufficiente per ripetere la procedura.

## Prompt da passare all'agente

```text
Implementa lo step 16 Release, Signing and Checksums.

Configura signing release con keystore esterno, documenta generazione keystore, build release, checksum SHA-256, copia APK nella landing e changelog. Non committare segreti. Verifica build debug e release.
```
