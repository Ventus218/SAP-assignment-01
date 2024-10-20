# Report

## Requisiti

L'utente:
- deve potersi registrare
- deve poter fare login
- deve poter selezionare una bici e iniziare / terminare una corsa.

![User frontend](./img/user-frontend.png)

L'amministratore:
- deve poter vedere la posizione di tutte le bici
- deve poter aggiungere nuove bici al sistema

![Admin frontend](./img/admin-frontend.png)

## Architettura del sistema completo

![Architettura del sistema](./img/client-server-diagram.png)

Il sistema sarà composto da un server e due client:
- admin: pannello di controllo dell'amministratore
- user: applicazione dell'utente

Si è scelto di utilizzare una API REST HTTP per implementare la comunicazione tra client e server. Non ci saranno quindi dipendenze nel codice in nessuna direzione tra client e server, questo viene [testato](../src/test/java/sap/ass01/solution/ClientServerArchTests.java) con ArchUnit.

## Architettura dei client

![Architettura dei client](./img/client-diagram.png)

Entrambi i client sono molto semplici e implementano un'architettura del tipo MVVM (dove il ruolo di Model viene giocato dal server.)

## Architettura del server

### Layered

![Architettura del server (layered)](./img/backend-layered-diagram.png)

Si è scelto di impostare l'architettura su 4 layer.

Ogni layer può interagire solo con il layer sottostante e non può saltare layer. Inoltre ogni layer ha le proprie strutture dati che vengono convertite tra di loro durante le interazioni.

Tutte queste caratteristiche vengono [testate](../src/test/java/sap/ass01/solution/backend/layered/LayeredArchTests.java) con ArchUnit.

|Layer|Descrizione|
|-----|-----------|
|Presentation| Il layer di presentation è implementato attraverso un server http (Vertx) che espone un'API REST e non fa altro che girare le richieste alla buisness logic effettuando marshaling e unmarshaling dei messaggi.|
|BusinessLogic| Il layer di business logic espone tutte le operazioni che possono essere eseguite sul dominio, e sfrutta il livello inferiore per effettuare la persistenza dei dati.
|Persistence| Questo layer espone un'interfaccia semplice ovvero quella di persistenza di collezioni di elementi (CollectionStorage). <br> Sono fornite due implementazioni di questo layer, una che si basa su un layer database che sfrutta il file system ([FileSystemDatabase.java](../src/main/java/sap/ass01/solution/backend/layered/database/FileSystemDatabase.java)) e una che si basa su un layer database che sfrutta un dizionario in memoria ([InMemoryMapDatabase.java](../src/main/java/sap/ass01/solution/backend/layered/database/InMemoryMapDatabase.java)).
|Database| Questo layer espone due interfacce ed implementazioni differenti, una che sul file system ([FileSystemDatabase.java](../src/main/java/sap/ass01/solution/backend/layered/database/FileSystemDatabase.java)) e che quindi realizza persistenza anche oltre la chiusura del programma. <br>  E una che si basa su un dizionario in memoria ([InMemoryMapDatabase.java](../src/main/java/sap/ass01/solution/backend/layered/database/InMemoryMapDatabase.java)) e che quindi perde i dati una volta riavviato il programma.


Il motivo per cui si sono scritte due implementazioni dei layer Database e Persistence è proprio per dimostrare che è possibile scambiare queste senza intaccare minimamente i livelli superiori (Business Logic e Presentation).
In questo caso quindi il layer Persistence gioca il ruolo di un'interfaccia / adattatore per il layer Database.
