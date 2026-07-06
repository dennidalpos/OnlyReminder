# 17 — WhatsApp Business API Advanced Mode

## Obiettivo

Implementare modalità avanzata WhatsApp Business API con credenziali fornite dal cliente, archiviate
localmente cifrate, coda locale, delay, retry e fallback manuale.

## Scope

Feature avanzata. Deve essere implementata solo dopo WhatsApp Manual Mode e backup.

## Vincoli

- Nessun backend proprietario.
- Nessun token hardcoded.
- Credenziali inserite dall'utente.
- Token cifrato localmente.
- L'utente è responsabile dell'uso lecito della WhatsApp Business Platform.
- L'invio parte solo dopo azione esplicita dell'utente.
- Birthday scan non invia mai direttamente.

## Campi configurazione

- WhatsApp Business Account ID.
- Phone Number ID.
- Access Token.
- Default approved template name.
- Language code template.
- Test connection.

## Avviso credenziali EN

```text
Your WhatsApp API credentials are stored only on this device and encrypted locally. You are responsible for their security and for the lawful use of the WhatsApp Business Platform.
```

## Avviso credenziali IT

```text
Le tue credenziali WhatsApp API vengono salvate solo su questo dispositivo e cifrate localmente. Sei responsabile della loro sicurezza e dell'uso corretto della WhatsApp Business Platform.
```

## Stati coda

- pending;
- sending;
- api_accepted;
- failed;
- skipped;
- retry_scheduled.

Nota: non usare `delivered` o `read` se non gestiti tramite webhook. Senza backend/webhook, l'app
può sapere solo se la richiesta API è stata accettata o rifiutata.

## Flusso invio

1. Utente seleziona contatti.
2. Utente preme `Send selected automatically`.
3. App mostra conferma.
4. App crea coda locale.
5. App invia un messaggio ogni `birthdayApiBatchDelaySeconds`.
6. Default delay: 3 secondi.
7. App mostra progresso.
8. App registra log.
9. App permette pausa.
10. App permette stop.
11. App permette retry failed.
12. App permette fallback manuale.

## Schermata progresso

Mostrare:

- totale;
- api accepted;
- failed;
- pending;
- skipped;
- contatto corrente;
- ultimo errore;
- pause;
- stop;
- retry failed;
- export report.

## Errori

Gestire:

- token mancante;
- token scaduto;
- phone number id errato;
- template non approvato;
- rate limit;
- rete assente;
- risposta API non valida;
- telefono contatto invalido.

## Privacy/compliance

Aggiungere filtro:

```text
Send only to contacts with marketing consent
```

Aggiungere warning se template potenzialmente commerciale.

Consigliato:

- supportare `doNotContact`;
- non inviare a contatti con opt-out se il campo esiste.

## Checklist implementazione

- [ ] Settings credenziali API implementate.
- [ ] Token salvato cifrato.
- [ ] Masking token implementato.
- [ ] Test connection implementato.
- [ ] Client Retrofit/OkHttp implementato.
- [ ] Coda locale implementata.
- [ ] Delay default 3 secondi implementato.
- [ ] Delay configurabile implementato.
- [ ] Progress screen implementata.
- [ ] Pause implementato.
- [ ] Stop implementato.
- [ ] Retry failed implementato.
- [ ] Fallback manuale implementato.
- [ ] Error handling credenziali implementato.
- [ ] MessageLog API implementato.
- [ ] Export report predisposto o implementato.
- [ ] Nessun invio automatico da birthday scan.
- [ ] UI EN/IT completata.
- [ ] Build debug compila.

## Criteri di accettazione

- L'utente può salvare credenziali cifrate.
- L'utente può testare configurazione.
- L'invio API parte solo dopo azione esplicita.
- La coda rispetta delay configurato.
- Gli errori sono chiari.
- Retry failed funziona.
- Fallback manuale è disponibile.
- Nessun token appare in log o UI non mascherata.

## Prompt da passare all'agente

```text
Implementa lo step 17 WhatsApp Business API Advanced Mode.

Aggiungi configurazione credenziali BYO cifrate, test connection, client API, coda locale, delay 3s configurabile, progress screen, pause/stop/retry failed, log locale e fallback manuale. Non usare backend proprietario. Non inviare mai automaticamente dopo birthday scan. Usa stati api_accepted/failed, non delivered/read senza webhook.
```
