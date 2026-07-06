# 04 — Security and Encryption

## Obiettivo

Implementare le fondamenta di sicurezza locale: gestione chiavi, storage sicuro, app lock, segreti e
policy anti-leak.

## Scope

Questo step prepara sicurezza locale. Il database cifrato definitivo viene collegato nello step 05.

## Requisiti

- Android Keystore per chiavi locali.
- Storage sicuro per preferenze sensibili.
- App lock con PIN.
- Biometria se disponibile.
- Auto-lock dopo inattività.
- Mascheramento token/segreti.
- Nessun log sensibile in chiaro.
- Wipe locale predisposto.

## Threat model minimo

Proteggere contro:

- accesso casuale al telefono;
- copia file locali;
- backup letto da terzi;
- perdita telefono bloccato.

Non promettere protezione totale contro:

- dispositivo rootato;
- malware con privilegi elevati;
- utente che esporta dati non cifrati;
- token WhatsApp compromesso da ambiente esterno.

## App Lock

Default consigliato:

- enabled.

Opzioni timeout:

- immediately;
- 1 minute;
- 5 minutes;
- 15 minutes;
- never.

Default:

```text
5 minutes
```

## PIN

Implementare:

- setup PIN;
- verifica PIN;
- cambio PIN;
- disattivazione solo dopo verifica;
- limite tentativi ragionevole;
- nessun PIN salvato in chiaro;
- hash con salt.

## Biometria

Implementare se disponibile:

- prompt biometrico;
- fallback PIN;
- gestione dispositivo non supportato.

## Segreti

Preparare funzioni per:

- salvare token cifrati;
- leggere token cifrati;
- cancellare token;
- mostrare token mascherato.

## Backup key

Non usare solo Android Keystore per backup cross-device. Preparare interfaccia per backup cifrato
con password/recovery key nello step 14.

## Anti-leak

- Non loggare numeri completi quando non necessario.
- Non loggare access token.
- Non loggare payload WhatsApp completo.
- Valutare `FLAG_SECURE` per schermate con segreti.

## Checklist implementazione

- [ ] Android Keystore wrapper creato.
- [ ] Secure preferences/storage predisposto.
- [ ] PIN setup implementato.
- [ ] PIN verify implementato.
- [ ] Biometria integrata dove disponibile.
- [ ] Fallback PIN funzionante.
- [ ] Auto-lock implementato.
- [ ] Secret masking implementato.
- [ ] Wipe locale predisposto.
- [ ] Policy no sensitive logs applicata.
- [ ] Build debug compila.

## Criteri di accettazione

- L'app può essere protetta da PIN.
- Biometria funziona se supportata.
- Dispositivi senza biometria usano PIN.
- Nessun segreto viene salvato in chiaro.
- Nessun log sensibile evidente.
- L'app resta usabile se app lock è disabilitato.

## Prompt da passare all'agente

```text
Implementa lo step 04 Security and Encryption.

Crea le fondamenta di sicurezza locale: Android Keystore wrapper, secure storage, PIN lock, biometria con fallback PIN, auto-lock, masking segreti e policy no sensitive logs. Non implementare ancora database completo, import o WhatsApp.

A fine lavoro restituisci implementazione, file modificati, test eseguiti e limiti noti.
```
