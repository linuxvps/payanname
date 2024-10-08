package org.knn.controller;

import org.knn.entity.Terminal;
import org.knn.service.ExcelToDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class TerminalExcel {

    @Autowired
    private ExcelToDatabaseService excelToDatabaseService;

    @GetMapping("/excel")
    public void uploadExcel() {
        try {
            excelToDatabaseService.readExcelAndInsertData("C:\\Users\\NIMA\\Desktop\\arshad\\payanname\\code\\knn\\src\\main\\resources\\CD_TERMINAL.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/show")
    public List<Terminal> showTerminal() {
        List<Terminal> terminals = excelToDatabaseService.showTerminal();
        return terminals;
    }

}
