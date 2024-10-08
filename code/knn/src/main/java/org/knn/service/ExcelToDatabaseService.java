package org.knn.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.knn.entity.Terminal;
import org.knn.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

@Service
public class ExcelToDatabaseService {

    @Autowired
    private TerminalRepository terminalRepository;

    public void readExcelAndInsertData(String excelFilePath) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(excelFilePath);
        Workbook workbook = new XSSFWorkbook(fileInputStream);

        Sheet sheet = workbook.getSheetAt(0); // Assuming the first sheet contains the data

        for (Row row : sheet) {
            if (row.getRowNum() == 0) {
                // Skip header row
                continue;
            }

            Terminal terminal = new Terminal();
            terminal.setAssetNumber(getCellValue(row.getCell(0)));
            terminal.setMacKey(getCellValue(row.getCell(1)));
            terminal.setManufacturer(getCellValue(row.getCell(2)));
            terminal.setPinKey(getCellValue(row.getCell(3)));
            terminal.setSerialNo(getCellValue(row.getCell(4)));
            terminal.setTerminalModel(getCellValue(row.getCell(5)));
            terminal.setTerminalNumber(getLongCellValue(row.getCell(6)));
            terminal.setTerminalStatus(getCellValue(row.getCell(7)));
            terminal.setTerminalType(getCellValue(row.getCell(8)));
            terminal.setAcquirer(getLongCellValue(row.getCell(9)));
            terminal.setRowUpdSeq(getLongCellValue(row.getCell(10)));
            terminal.setInsertSysdate(new Date());
            terminal.setUpdateSysdate(new Date());
            terminal.setUpdateUser(getCellValue(row.getCell(11)));
            terminal.setInsertUser(getCellValue(row.getCell(12)));
            terminal.setBatchId(getLongCellValue(row.getCell(13)));
            terminal.setIpAddress(getCellValue(row.getCell(14)));
            terminal.setMasterKey(getCellValue(row.getCell(15)));
            terminal.setPersianTitle(getCellValue(row.getCell(16)));
            terminal.setEnglishTitle(getCellValue(row.getCell(17)));
            terminal.setTerminalGroupId(getLongCellValue(row.getCell(18)));
            terminal.setTraceId(getLongCellValue(row.getCell(19)));
            terminal.setLatitude(getDoubleCellValue(row.getCell(20)));
            terminal.setLongitude(getDoubleCellValue(row.getCell(21)));

            terminalRepository.save(terminal);
        }

        workbook.close();
        fileInputStream.close();
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }
        return cell.getCellType() == CellType.STRING ? cell.getStringCellValue() : String.valueOf((int) cell.getNumericCellValue());
    }

    private Long getLongCellValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        return (long) cell.getNumericCellValue();
    }

    private Double getDoubleCellValue(Cell cell) {
        if (cell == null || cell.getCellType() != CellType.NUMERIC) {
            return null;
        }
        return cell.getNumericCellValue();
    }
}