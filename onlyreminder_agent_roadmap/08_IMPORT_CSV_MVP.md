# 08 — CSV Import MVP

## Obiettivo

Implementare import contatti da CSV con mapping guidato, preview, normalizzazione telefono,
deduplica e backup pre-import.

## Scope

Solo CSV in questo step. XLSX/JSON/XML sono step successivo.

## Flusso import

1. Utente seleziona file CSV.
2. App rileva separatore ed encoding base.
3. App mostra anteprima righe.
4. App propone mapping colonne.
5. Utente conferma o modifica mapping.
6. App normalizza telefoni.
7. App valida dati.
8. App rileva duplicati.
9. App mostra report.
10. App crea backup cifrato prima di salvare se backup disponibile.
11. App salva contatti nel DB cifrato.

## Campi importabili MVP

- firstName;
- lastName;
- displayName;
- phone;
- email;
- company;
- birthday;
- tags;
- group;
- source;
- notes;
- lastContactDate;
- marketingConsent;
- privacyConsent;
- status.

Custom fields possono essere rimandati se rallentano il CSV MVP, ma il mapping deve essere
predisposto.

## Normalizzazione telefono

Default:

```text
+39
```

Regole:

- rimuovere spazi;
- rimuovere trattini;
- rimuovere parentesi;
- convertire `00` iniziale in `+`;
- se manca prefisso internazionale, aggiungere default country code;
- segnalare numeri sospetti;
- consentire correzione manuale.

Esempi:

```text
333 1234567 -> +393331234567
0039 333 1234567 -> +393331234567
+39 333 1234567 -> +393331234567
```

## Deduplicazione

Criteri:

1. normalizedPhone se presente;
2. email se presente;
3. firstName + lastName + company come debole.

Scelte utente:

- skip duplicate;
- update existing;
- create new anyway.

## Report import

Mostrare:

- righe lette;
- contatti validi;
- contatti con warning;
- duplicati;
- numeri sospetti;
- righe saltate;
- errori.

## Limiti MVP da dichiarare

- CSV max iniziale ragionevole, ad esempio 5.000 contatti.
- Separatore supportato: virgola, punto e virgola, tab.
- Encoding: UTF-8 e fallback ragionevole.

## Checklist implementazione

- [x] File picker CSV implementato.
- [x] Parser CSV implementato.
- [x] Rilevamento separatore implementato.
- [x] Preview righe implementata.
- [x] Mapping colonne implementata.
- [x] Normalizzazione telefono implementata.
- [x] Validazione dati implementata.
- [x] Deduplica implementata.
- [x] Scelte gestione duplicati implementate.
- [x] Report import implementato.
- [x] Backup pre-import chiamato se disponibile.
- [x] Salvataggio DB implementato.
- [x] Error handling implementato.
- [x] Build debug compila.

## Criteri di accettazione

- Import CSV funziona con dati reali.
- L'utente vede preview prima del salvataggio.
- L'utente può mappare colonne.
- I numeri italiani vengono normalizzati.
- I duplicati vengono rilevati.
- L'utente sceglie come gestire duplicati.
- L'app non salva nulla prima della conferma finale.
- Prima di import massivo viene creato backup se la funzione backup è disponibile.

## Prompt da passare all'agente

```text
Implementa lo step 08 CSV Import MVP.

Crea import CSV con file picker, parser, preview, mapping colonne, normalizzazione telefono +39, validazione, deduplica, report e salvataggio nel database cifrato dopo conferma. Se la funzione backup è già disponibile, crea backup pre-import; altrimenti predisponi hook chiaro per step 14.

Non implementare XLSX, JSON o XML in questo step.
```
