# 09 — Advanced Import Formats: XLSX, JSON, XML

## Obiettivo

Aggiungere import da XLSX, JSON e XML riutilizzando il flusso CSV già implementato.

## Scope

Estensione import. Non cambiare logica contatti se non necessario.

## Regola principale

Tutti i formati devono convergere nello stesso modello intermedio:

```text
RawImportedContactRow
```

Il flusso successivo deve restare identico:

- preview;
- mapping;
- validazione;
- normalizzazione;
- deduplica;
- report;
- conferma;
- backup;
- salvataggio.

## XLSX

Requisiti:

- leggere primo foglio di default;
- permettere scelta foglio se pratico;
- riconoscere header;
- gestire celle vuote;
- convertire date Excel correttamente;
- limite righe dichiarato.

## JSON

Non supportare JSON arbitrario illimitato senza regole.

Definire schema supportato:

```json
[
  {
    "firstName": "Mario",
    "lastName": "Rossi",
    "displayName": "Mario Rossi",
    "phone": "3331234567",
    "email": "mario@example.com",
    "company": "ACME",
    "birthday": "1980-07-06",
    "tags": ["client", "vip"],
    "group": "Customers",
    "notes": "Example"
  }
]
```

## XML

Non supportare XML arbitrario illimitato senza regole.

Definire schema supportato:

```xml
<contacts>
  <contact>
    <firstName>Mario</firstName>
    <lastName>Rossi</lastName>
    <phone>3331234567</phone>
    <email>mario@example.com</email>
  </contact>
</contacts>
```

## Checklist implementazione

- [ ] Modello intermedio import condiviso creato.
- [ ] XLSX parser implementato.
- [ ] JSON parser implementato con schema documentato.
- [ ] XML parser implementato con schema documentato.
- [ ] Preview riutilizzata.
- [ ] Mapping riutilizzato.
- [ ] Validazione riutilizzata.
- [ ] Deduplica riutilizzata.
- [ ] Report riutilizzato.
- [ ] Documentazione formati aggiornata.
- [ ] Build debug compila.

## Criteri di accettazione

- XLSX importa correttamente dati semplici.
- JSON importa correttamente schema documentato.
- XML importa correttamente schema documentato.
- Errori di formato vengono mostrati chiaramente.
- Non vengono salvati dati prima della conferma.
- Il CSV esistente non regredisce.

## Prompt da passare all'agente

```text
Implementa lo step 09 Advanced Import Formats.

Aggiungi import XLSX, JSON e XML usando lo stesso flusso già creato per CSV. Definisci e documenta schema JSON/XML supportato. Non tentare di supportare qualsiasi struttura arbitraria. Mantieni preview, mapping, validazione, deduplica e report condivisi.
```
