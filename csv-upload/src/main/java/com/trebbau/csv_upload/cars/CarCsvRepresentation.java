package com.trebbau.csv_upload.cars;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

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
