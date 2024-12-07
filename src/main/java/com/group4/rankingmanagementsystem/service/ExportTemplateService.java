package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.Employee;
import com.group4.rankingmanagementsystem.entity.RankingDecision;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.springframework.core.io.InputStreamResource;

import java.io.IOException;
import java.util.List;

public interface ExportTemplateService {
    String export(Long groupId, List<Long> employeeIds) throws InvalidFormatException;
    String generateTemplate(List<Employee> employees, RankingDecision rankingDecision)throws IOException;
    void writeHeader(Sheet sheet, int rowIndex, RankingDecision rankingDecision);
    void writeData(Employee employee, RankingDecision rankingDecision, Row row);
    CellStyle createStyleForHeader(Sheet sheet);
    void autoSizeColumn(Sheet sheet, int lastColumn);
    void createOutputFile(Workbook workbook, String excelFilePath) throws IOException;
}
