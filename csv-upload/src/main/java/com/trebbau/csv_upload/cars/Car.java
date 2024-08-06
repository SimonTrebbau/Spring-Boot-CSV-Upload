package com.trebbau.csv_upload.cars;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Car {

    @Id
    @GeneratedValue
    private Integer id;
    private String brand;
    private String model;
    private String color;
    private Short yearOfProduction;
    private String priceInEuro;

}
