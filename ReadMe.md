
# TARDIS Archive – Java og MySQL

## Introduktion

Vi skal bygge en Java-applikation, der kan administrere forskellige inkarnationer af Doctoren fra Doctor Who.

Applikationen skal gemme oplysninger i en MySQL-database.

I skal bruge det, I allerede har lært om:

- Klasser og objekter
- Metoder og konstruktører
- ArrayList og List
- Exception handling
- Lagdelt arkitektur

Derudover skal I arbejde med JDBC, PreparedStatement og SQL.

**Målet er ikke kun at få programmet til at virke. I skal også kunne forklare, hvordan koden er organiseret, og hvorfor de forskellige klasser har hvert deres ansvar.**

## 1. Arkitektur – forstå projektet

Vi bruger tre lag:

```text
Bruger
  |
  v
CLI / DoctorCliApp
  |
  v
Service / DoctorService
  |
  v
DAL / DoctorRepository
  |
  v
MySQL / tardis_archive
```

CLI håndterer brugerinput og beskeder.

Service indeholder validering og regler.

Repository indeholder SQL og JDBC.

### Opgave 1 – forstå arkitekturen (skal laves)

Find klasserne i projektet.

1. Hvilken klasse læser brugerens input?
2. Hvor bliver SQL-kommandoerne udført?
3. Hvilken klasse kontrollerer, at navnet ikke er tomt?
4. Hvor bliver et Doctor-objekt oprettet?
5. Hvad er fordelen ved at holde SQL ude af CLI?

**Hjælp:** Find en metode i `DoctorCliApp`, og følg metodekaldene videre gennem Service og Repository.

Diskutér svarene med din gruppe.

## 2. Opgave – få forbindelse til MySQL (skal laves)

Start med at kontrollere, at databasen `tardis_archive` findes.

Kør følgende i MySQL Workbench:

```sql
USE tardis_archive;

SELECT * FROM doctors;
```

Undersøg derefter klassen `DatabaseConnection`.

### Test forbindelsen

Prøv at oprette en forbindelse:

```java
try (Connection connection =
        DatabaseConnection.getConnection()) {

    System.out.println("Forbindelsen virker!");

} catch (SQLException e) {

    System.out.println(e.getMessage());
}
```

### Eksperiment

Prøv én ting ad gangen:

1. Stop MySQL-serveren.
2. Skriv et forkert databasenavn.
3. Brug et forkert password.

Hvilke fejl får I?

Ret derefter fejlene, så forbindelsen virker igen.

**Refleksion:** Hvorfor skal vi håndtere databasefejl med exception handling?

## 3. Opgave – forstå READ (skal laves)

Åbn `DoctorRepository`.

Find metoden:

```java
public List<Doctor> findAll() throws SQLException
```

Undersøg især:

```java
String sql =
    "SELECT id, name, actor FROM doctors";
```

og:

```java
while (result.next()) {
    // Hent data fra den næste række
}
```

### Jeres opgave

1. Find ud af, hvordan `ResultSet` fungerer.
2. Forklar, hvordan en række bliver til et Doctor-objekt.
3. Find den ArrayList, som objekterne bliver gemt i.
4. Undersøg, hvordan listen kommer tilbage til CLI.

### Eksperiment

Ændr SQL-kommandoen, så den kun henter Doctors med et ID større end 2.

**Hint:**

```sql
SELECT id, name, actor
FROM doctors
WHERE id > 2;
```

Test ændringen.

Hvad sker der, hvis tabellen er tom?

## 4. Opgave – implementér CREATE (skal laves)

I skal kunne oprette en ny Doctor.

I `DoctorRepository` skal I bruge:

```sql
INSERT INTO doctors (name, actor)
VALUES (?, ?);
```

### Hjælp

Et PreparedStatement kan modtage værdier:

```java
statement.setString(1, doctor.getName());
statement.setString(2, doctor.getActor());
```

Derefter skal SQL-kommandoen udføres.

### Tænk over

Hvorfor bruger vi spørgsmålstegn i SQL i stedet for at indsætte brugerens tekst direkte?

### Test

- Opret en Doctor.
- Kontrollér, at den findes i MySQL.
- Luk og genstart Java-programmet.
- Kontrollér, at Doctoren stadig eksisterer.

## 5. Opgave – implementér DELETE (skal laves)

Nu skal I selv lave en metode, der kan slette en Doctor.

Metoden skal have følgende signatur:

```java
public boolean deleteById(int id)
        throws SQLException
```

Brug denne SQL:

```sql
DELETE FROM doctors WHERE id = ?;
```

### Hjælp

I skal bruge:

```java
statement.setInt(1, id);
```

Når I udfører SQL-kommandoen, får I antallet af påvirkede rækker:

```java
int affectedRows = statement.executeUpdate();
```

Metoden skal returnere `true`, hvis mindst én række blev slettet.

### Tænk over

1. Hvad returnerer `executeUpdate()`, hvis ID'et ikke findes?
2. Hvad skal brugeren have at vide i denne situation?
3. Skal SQL-koden være i CLI, Service eller Repository?

### Udvidelsen

Når repository-metoden virker:

1. Lav en tilsvarende metode i `DoctorService`.
2. Tilføj et menupunkt i `DoctorCliApp`.
3. Test med både eksisterende og ikke-eksisterende ID'er.

## 6. Opgave – implementér UPDATE (skal laves)

Nu skal brugeren kunne ændre skuespilleren for en Doctor.

Lav en metode:

```java
public boolean updateActor(int id, String actor)
        throws SQLException
```

### SQL-hjælp

```sql
UPDATE doctors
SET actor = ?
WHERE id = ?;
```

Husk, at der er to spørgsmålstegn.

### Jeres opgave

1. Opret metoden i Repository.
2. Brug PreparedStatement.
3. Returnér `true`, hvis opdateringen påvirker en række.
4. Tilføj metoden i Service.
5. Lav et menupunkt i CLI.

### Refleksion

Hvorfor skal Service kontrollere, at `actor` ikke er tom?

Hvad sker der, hvis vi glemmer `WHERE` i SQL-kommandoen?

## 7. Opgave – søg efter en Doctor (skal laves)

Brugeren skal kunne søge efter en Doctor ud fra navnet.

Eksempel:

```text
Søg efter navn: Tenth
```

Programmet skal vise alle Doctors, der matcher søgningen.

### Hjælp

I MySQL kan I bruge `LIKE`:

```sql
SELECT id, name, actor
FROM doctors
WHERE name LIKE ?;
```

I Java kan søgeteksten sættes ind med:

```java
statement.setString(1, "%" + search + "%");
```

`%` betyder, at der må stå andre tegn før eller efter søgeteksten.

### Jeres opgave

Lav metoden:

```java
public List<Doctor> searchByName(String search)
        throws SQLException
```

Husk at returnere en liste, også når ingen Doctors matcher.

### Test

Søg efter:

- `Doctor`
- `Tenth`
- `First`
- Et navn, der ikke findes

Hvad sker der, hvis brugeren søger efter en tom tekst?

## 8. Opgave – exception handling (skal laves)

Vi skal sikre, at programmet håndterer fejl fornuftigt.

Der er forskel på:

- Ugyldigt brugerinput
- En Doctor, der ikke findes
- En fejl ved databasen

### Jeres opgave

Undersøg, hvordan disse fejl bliver håndteret i programmet.

Prøv at:

1. Oprette en Doctor uden navn.
2. Indtaste tekst i stedet for et ID.
3. Slette en Doctor med ID 99999.
4. Stoppe MySQL-serveren og forsøge at hente data.

### Refleksion

Hvilke fejl skal håndteres i Service?

Hvilke fejl kommer fra JDBC?

Hvilket lag skal vise fejlbeskeder til brugeren?

**Husk:** Vi skal ikke bare fange alle exceptions og ignorere dem.

## 9. Opgave – forbedr arkitekturen (ekstra)

Se på `DoctorRepository`.

Metoderne `findAll()` og `searchByName()` indeholder næsten den samme kode til at oprette Doctor-objekter.

Kan I undgå denne gentagelse?

### Hjælp

Overvej en privat hjælpemetode:

```java
private Doctor mapDoctor(ResultSet result)
        throws SQLException
```

Metoden skal omdanne én række fra databasen til et Doctor-objekt.

Diskutér, hvorfor dette kan gøre koden lettere at vedligeholde.

## 10. Opgave – egne exceptions (ekstra)

I har tidligere arbejdet med egne exceptions.

Lav en exception:

```java
InvalidDoctorException
```

Brug den, hvis nogen forsøger at oprette en Doctor uden navn.

### Hjælp

Overvej følgende:

- Hvilken klasse skal kaste exceptionen?
- Hvilken klasse skal fange den?
- Hvilken besked skal brugeren se?

Undersøg også forskellen mellem checked og unchecked exceptions.

## 11. Opgave – databasekonfiguration (ekstra)

Undersøg klassen `DatabaseConnection`.

I dag bruger vi miljøvariabler til brugernavn og password.

### Jeres opgave

Undersøg, hvordan en `.properties`-fil også kan bruges til databasekonfiguration.

Overvej:

1. Hvorfor skal passwords ikke ligge direkte i kildekoden?
2. Hvorfor skal vi undgå at lægge adgangskoder på GitHub?
3. Hvad er fordelen ved at samle databasekonfigurationen ét sted?

## 12. Test jeres program

Gennemgå følgende testscenarier:

| Test | Forventet resultat |
|---|---|
| Opret Doctor | Gemmes i databasen |
| Vis alle Doctors | Alle vises |
| Opdater Doctor | Oplysninger ændres |
| Slet Doctor | Rækken fjernes |
| Slet ukendt ID | Programmet giver en besked |
| Søg efter navn | Matchende Doctors vises |
| Indtast tomt navn | Afvises |
| Stop MySQL | Databasefejl håndteres |
| Genstart programmet | Data er stadig gemt |

Prøv også at bruge IntelliJs debugger til at følge programmet gennem de forskellige lag.

## 13. Afsluttende refleksion

I skal kunne forklare følgende til hinanden:

1. Hvad er JDBC?
2. Hvad betyder CRUD?
3. Hvorfor anvender vi PreparedStatement?
4. Hvad er forskellen på `executeQuery()` og `executeUpdate()`?
5. Hvorfor bruger vi try-with-resources?
6. Hvad er repositoryets ansvar?
7. Hvad er servicelagets ansvar?
8. Hvorfor skal CLI ikke indeholde SQL?
9. Hvordan hænger Java-objekter og databasetabeller sammen?
10. Hvad sker der med data, når programmet afsluttes?

## 14. Hvornår er I færdige?

I er færdige med de obligatoriske opgaver, når:

- Java-programmet kan forbinde til MySQL.
- I har implementeret CRUD.
- I kan søge efter Doctors.
- I har grundlæggende inputvalidering.
- I håndterer databasefejl.
- I har opdelt programmet i CLI, Service og Repository.
- I kan forklare, hvordan et metodekald bevæger sig gennem lagene.

**Det vigtigste er ikke, hvor hurtigt I bliver færdige, men at I kan forstå, teste og forklare jeres egen kode.**

Når I er færdige, kan I arbejde videre med dagens opgaver: esport-hold, zoologisk have eller julefrokostplanlægger.
