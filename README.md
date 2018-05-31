# wicket-participate
Teilnahmemanager für den Kammerchor Wernigerode e.V.

## Einrichtung
Es wird ein MySQL Server und eine Datenbank benötigt

* Windows: XAMPP
* Linux: mysql-server

Beispieldateien umbenennen und der Datenbank anpassen

* liquibase.properties (liquibase.sample.properties)
* application.properties (application.sample.properties)

Datenbankstruktur mit ```mvn resources:resources liquibase:update``` einrichten

Zum Ausführen, einfach die _ParticipateApplication.class_ ausführen oder mit ```mvn spring-boot:run``` ausführen 