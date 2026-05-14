package faults.service;


import faults.filehandling.ParseExcelFile;
import faults.model.Fault;
import faults.repository.FaultsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@AllArgsConstructor
public class FaultsService {

    private final ParseExcelFile parseExcelFile;
    private final FaultsRepository faultsRepository;

    public int readAndSaveExcelFile(MultipartFile file) throws IOException {
        List<Fault> faultList = parseExcelFile.parseFile(file);
        int importedRows = 0;
        for (Fault fault : faultList) {
            if (fault == null) continue;
            importedRows += faultsRepository.addFault(fault);
        }
        return importedRows;
    }

    public List<String> findDeviceIps() {
        return faultsRepository.findAllDeviceIps();
    }

    public List<String> findByDataType(String dataType) {
        return faultsRepository.findByDataType(dataType);
    }

    public int deleteAllData() {
        return faultsRepository.deleteAll();
    }
}
