# CSV-Upload-Example

**EN:** This repository provides a minimal example project that shows how to load data from a CSV file into a PostgreSQL database via a REST endpoint. The project is implemented with Spring Boot and demonstrates how to efficiently process CSV files and store them in a relational database. It is suitable as a starting point for developers who want to implement similar data integration solutions.

**DE:** Dieses Repository bietet ein minimales Beispielprojekt, das zeigt, wie man Daten aus einer CSV-Datei über einen REST-Endpunkt in eine PostgreSQL-Datenbank lädt. 
Das Projekt ist mit Spring Boot implementiert und demonstriert, wie man CSV-Dateien effizient verarbeitet und in einer relationalen Datenbank speichert. 
Es eignet sich als Ausgangspunkt für Entwickler, die ähnliche Datenintegrationslösungen umsetzen möchten.

**EN:** The `.csv` file used in this repo contains the following cars data:<br>
**DE:** Die in diesem Repo verwendete `.csv`-Datei enthält die folgende Autodaten:
```
brand, model, color, year_of_production, price_in_euro
BMW, 3er, black, 2018, 25.000
Audi, A4, white, 2020, 30.000
Volkswagen, Golf, blue, 2017, 18.000
Mercedes-Benz, C-Klasse, green, 2019, 32.000
Toyota, Corolla, black, 2016, 15.000
Ford, Focus, silver, 2015, 12.000
Opel, Astra, grey, 2021, 20.000
Renault, Clio, black, 2018, 14.500
Honda, Civic, black, 2019, 22.000
Peugeot, 308, blue, 2020, 19.500
Skoda, Octavia, silver, 2017, 16.500
Mazda, 3, red, 2016, 14.000
Hyundai, i30, green,  2019, 17.000
Kia, Ceed, black, 2018, 15.500
Nissan, Qashqai, white, 2021, 25.000
Seat, Leon, red, 2018, 18.500
Citroën, C4, blue, 2017, 13.000
Fiat, 500, white, 2020, 16.000
Volvo, XC60, silver, 2019, 35.000
Subaru, Impreza, green, 2020, 24.000
```

**EN:** After the CSV file has been successfully uploaded, the table in the PostgreSQL database looks like this:<br>
**DE:** Nachdem die CSV-Datei erfolgreich hochgeladen wurde, sieht die Tabelle in der PostgreSQL-Datenbank wie folgt aus:
<img width="1086" alt="postgreSQL-csv-upload" src="https://github.com/user-attachments/assets/5913136c-85bb-43e3-8a3d-2a745a63303f">

### Maven-Dependency
Used Dependency: https://mvnrepository.com/artifact/com.opencsv/opencsv
```xml
<!-- CSV dependency -->
<dependency>  
    <groupId>com.opencsv</groupId>  
    <artifactId>opencsv</artifactId>  
    <version>5.9</version>  
</dependency>
```

### PostgreSQL-Database
**EN:** In this example, the PostgreSQL database was started using Docker via the following `docker-compose.yml` file:<br>
**DE:** In diesem Beispiel wurde die PostgreSQL-Datenbank mittels Docker über die folgende `docker-compose.yml`-Datei gestartet:
```yml
services:
  postgres_db:
    container_name: postgres_db
    image: postgres
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres
      POSTGRES_DB: csv_car
      PGDATA: /var/lib/postgresql/data
    ports:
      - 5432:5432
```

### application.yml
**EN:** The following properties have been defined to establish a connection to the database:<br>
**DE:** Um eine Verbindung zur Datenbank herzustellen wurden folgende Properties definiert:
```yml
spring:
  application:
    name: csv-upload

  datasource:
    url: jdbc:postgresql://localhost:5432/csv_car
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: create-drop
    database: postgresql
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: false
```

### REST Endpunkt
**EN:** The following controller class defines the REST endpoint via which the CSV file is received:<br>
**DE:** Die nachfolgende Controller-Klasse definiert den REST Endpunkt, über den die CSV-Datei entgegen genommen wird:
```java
@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarsController {

    private final CarsService carsService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<Integer> uploadCars(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(this.carsService.uploadCars(file));
    }
}
```

### CarsService.data
**EN:** This class contains the core business logic.<br>
**DE:** Diese Klasse beinhaltet die eigentliche Businesslogik.
```java
@Service
@RequiredArgsConstructor
public class CarsService {

    private final CarsRepository carsRepository;

    public Integer uploadCars(MultipartFile file) throws IOException {
        Set<Car> cars = parseCsv(file);
        this.carsRepository.saveAll(cars);
        return cars.size();
    }

    private Set<Car> parseCsv(MultipartFile file) throws IOException {
        // try with resources to read file
        try(Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            // Create Strategy how to read csv file
            /*  Maps data to objects using the column names in the first row of the CSV file as reference.
                This way the column order does not matter */
            HeaderColumnNameMappingStrategy<CarCsvRepresentation> strategy =
                    new HeaderColumnNameMappingStrategy<>();

             /*  Sets the class type that is being mapped.
                Also initializes the mapping between column names and bean fields.
                Attempts to create one example bean to be certain there are no fundamental problems with creation. */
            strategy.setType(CarCsvRepresentation.class);

            // Transform csv to bean
            /* CsvToBean converts CSV data to objects via parse() method */
            CsvToBean<CarCsvRepresentation> csvToBean = new CsvToBeanBuilder<CarCsvRepresentation>(reader)
                    .withMappingStrategy(strategy)
                    .withIgnoreEmptyLine(true)
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            /* Parses the input based on parameters already set through other methods. */
            return csvToBean.parse()
                    .stream()
                    .map(
                            csvLine -> Car.builder()
                                    .brand(csvLine.getBrand())
                                    .model(csvLine.getModel())
                                    .color(csvLine.getColor())
                                    .yearOfProduction(csvLine.getYearOfProduction())
                                    .priceInEuro(csvLine.getPriceInEuro())
                                    .build()
                    ).collect(Collectors.toSet());
        }
    }
}
```

### CarCsvRepresentation.java
**EN:** This class assigns the data to the objects and uses the column names in the first line of the CSV file as a reference.<br>
**DE:** Diese Klasse ordnet die Daten den Objekten zu und verwendet dabei die Spaltennamen in der ersten Zeile der CSV-Datei als Referenz.
```java
@Getter  
@Setter  
@Builder  
@AllArgsConstructor  
@NoArgsConstructor  
public class CarCsvRepresentation {  
  
    @CsvBindByName(column = "brand")  
    private String brand;  
    @CsvBindByName(column = "model")  
    private String model;  
    @CsvBindByName(column = "color")  
    private String color;  
    @CsvBindByName(column = "year_of_production")  
    private Short yearOfProduction;  
    @CsvBindByName(column = "price_in_euro")  
    private String priceInEuro;  
  
}
```

### Postman
**EN:** The CSV upload can be tested using the following request. The number of entries (rows) is returned as the result.<br>
**DE:** Über den folgenden Request kann der CSV-Upload getestet werden. Als Ergebnis wird die Anzahl der Einträge (Zeilen) zurück gegeben.
![postman-csv-upload](https://github.com/user-attachments/assets/b464b62a-c1f2-44c6-8b60-24426f2a3f66)
