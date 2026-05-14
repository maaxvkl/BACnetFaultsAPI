package faults.filehandling;

import faults.model.Fault;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
@AllArgsConstructor
public class ParseExcelFile {

    public List<Fault> parseFile(MultipartFile file) throws IOException {
        List<Fault> faults = new ArrayList<>();
        final int FAULTS = 0;
        Cell cell = null;
        DataFormatter formatter = new DataFormatter();
        InputStream inputStream = file.getInputStream();
        XSSFWorkbook wb = new XSSFWorkbook(inputStream);

        if (wb.getSheetAt(FAULTS).getSheetName().equals("Faults")) {
            XSSFSheet worksheet = wb.getSheetAt(FAULTS);
            for (Row row : worksheet) {
                if (row.getRowNum() == 0) continue;
                Map<String, String> properties = new HashMap<>();
                String deviceIP = formatter.formatCellValue(row.getCell(0)).trim();
                String objectType = formatter.formatCellValue(row.getCell(1)).trim();
                String objectName = formatter.formatCellValue(row.getCell(2)).trim();
                String objectDescription = formatter.formatCellValue(row.getCell(3)).trim();
                String eventState = formatter.formatCellValue(row.getCell(4)).trim();
                if (row.getLastCellNum() > 5) {
                    for (int i = 5; i < row.getLastCellNum(); i++) {
                        cell = row.getCell(i);
                        if (cell == null) continue;
                        String headerCell = worksheet.getRow(0).getCell(i).getStringCellValue();
                        String cellValue = cell.getStringCellValue();
                        properties.put(headerCell, cellValue);
                        Fault fault = new Fault(deviceIP, objectType, objectName, objectDescription, eventState, properties);
                        faults.add(fault);
                    }
                } else {
                    Fault fault = new Fault(deviceIP, objectType, objectName, objectDescription, eventState, Collections.emptyMap());
                    faults.add(fault);
                }
            }
        }
        return faults;
    }
}

