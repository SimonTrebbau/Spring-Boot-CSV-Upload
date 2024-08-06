package com.trebbau.csv_upload.cars;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/cars")
@RequiredArgsConstructor
public class CarsController {

    private final CarsService carsService;

    @PostMapping(value = "/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<Integer> uploadCars(
            @RequestParam("file") MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(this.carsService.uploadCars(file));
    }
}
