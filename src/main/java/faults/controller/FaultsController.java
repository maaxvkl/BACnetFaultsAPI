package faults.controller;

import faults.service.FaultsService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("faults/")
public class FaultsController {

    private final FaultsService faultsService;

    @PostMapping("saveAll")
    public ResponseEntity<String> saveFaults(MultipartFile file) throws IOException, IllegalArgumentException {
        int importedRows = faultsService.readAndSaveExcelFile(file);
        String stringValue = String.valueOf(importedRows);
        return new ResponseEntity<>("Inserted " + stringValue + " rows", HttpStatus.OK);
    }

    @GetMapping("getDeviceIps")
    public ResponseEntity<List<String>> getDeviceIps() {
        List<String> deviceIps = faultsService.findDeviceIps();
        return new ResponseEntity<>(deviceIps, HttpStatus.OK);
    }

    @GetMapping("findBy/{dataType}")
    public ResponseEntity<List<String>> getByDataType(@PathVariable String dataType) throws IllegalArgumentException {
        List<String> deviceIps = faultsService.findByDataType(dataType);
        return new ResponseEntity<>(deviceIps, HttpStatus.OK);
    }

    @DeleteMapping("deleteAll")
    public ResponseEntity<String> deleteAll() throws IllegalArgumentException {
        int deletedData = faultsService.deleteAllData();
        String stringValue = String.valueOf(deletedData);
        return new ResponseEntity<>("Deleted " + stringValue + " rows", HttpStatus.OK);
    }
}


