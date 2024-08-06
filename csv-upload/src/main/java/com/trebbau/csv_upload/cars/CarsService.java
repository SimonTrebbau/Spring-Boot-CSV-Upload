package com.trebbau.csv_upload.cars;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Set;
import java.util.stream.Collectors;

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
