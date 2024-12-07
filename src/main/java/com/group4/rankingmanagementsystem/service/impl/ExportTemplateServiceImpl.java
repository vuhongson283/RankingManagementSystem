package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.*;
import com.group4.rankingmanagementsystem.repository.EmployeeRepository;
import com.group4.rankingmanagementsystem.repository.GroupRepository;
import com.group4.rankingmanagementsystem.service.ExportTemplateService;
import com.group4.rankingmanagementsystem.service.RankTitleService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.poi.hssf.usermodel.HSSFDataValidationHelper;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExportTemplateServiceImpl implements ExportTemplateService {

    private final RankTitleService rankTitleService;
    private final GroupRepository groupRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public ExportTemplateServiceImpl(RankTitleService rankTitleService, GroupRepository groupRepository,
                                     EmployeeRepository employeeRepository) {
        this.rankTitleService = rankTitleService;
        this.groupRepository = groupRepository;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public String export(Long groupId, List<Long> employeeIds) throws InvalidFormatException {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found."));
        if(group.getCurrentRankingDecision() == null){
            throw new InvalidFormatException("Group doesn't have Current Ranking Decision.");
        }

        List<Employee> employees = employeeRepository.findAllById(employeeIds);

        String filePath = null;
        try {
            filePath = generateTemplate(employees, group.getCurrentRankingDecision());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePath;
    }

    @Override
    public String generateTemplate(List<Employee> employees, RankingDecision rankingDecision) throws IOException {
        String excelFilePath = "D:\\Bulk_Assess_" + System.currentTimeMillis() + ".xlsx";
        Workbook workbook = getWorkbook(excelFilePath);
        Sheet sheet = workbook.createSheet("Bulk Assess");

        //write header
        int rowIndex = 0;
        writeHeader(sheet, rowIndex, rankingDecision);

        //write data
        rowIndex++;
        if (employees.isEmpty()) {
            Row row = sheet.createRow(rowIndex++);
            writeTemplateFormat(rankingDecision, row);
        }else{
            for (Employee employee : employees
            ) {
                Row row = sheet.createRow(rowIndex++);
                writeData(employee, rankingDecision, row);
            }
        }

        //footer

        //format
        int numberOfColumn = sheet.getRow(0).getPhysicalNumberOfCells();
        autoSizeColumn(sheet, numberOfColumn);

        createOutputFile(workbook, excelFilePath);

        return excelFilePath;
    }

    @Override
    public void writeHeader(Sheet sheet, int rowIndex, RankingDecision rankingDecision) {
        // create CellStyle
        CellStyle cellStyle = createStyleForHeader(sheet);

        Row row = sheet.createRow(rowIndex);

        int cellIndex = 0;

        Cell cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Employee ID");

        cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Employee Name");

        cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Current Ranking Decision");

        cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Current Rank");

        cell = row.createCell(cellIndex++);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Assessment Rank");

        for (RankingDecisionCriteria rdc :
                rankingDecision.getRankingDecisionCriterias()
        ) {
            cell = row.createCell(cellIndex++);
            cell.setCellStyle(cellStyle);
            cell.setCellValue(rdc.getRankingDecisionCriteriaId().getCriteria().getName());
        }

        cell = row.createCell(cellIndex);
        cell.setCellStyle(cellStyle);
        cell.setCellValue("Total");
    }

    @Override
    public void writeData(Employee employee, RankingDecision rankingDecision, Row row) {
        CellStyle cellStyleFormatNumber = null;
        // Format number
        short format = (short) BuiltinFormats.getBuiltinFormat("#,##0");

        //Create CellStyle
        Workbook workbook = row.getSheet().getWorkbook();
        cellStyleFormatNumber = workbook.createCellStyle();
        cellStyleFormatNumber.setDataFormat(format);

        int cellIndex = 0;
        //id
        Cell cell = row.createCell(cellIndex++);
        cell.setCellValue(employee.getId());
        //name
        cell = row.createCell(cellIndex++);
        cell.setCellValue(employee.getName());
        //current ranking
        cell = row.createCell(cellIndex++);
        cell.setCellValue(rankingDecision.getName());
        //rank title
        cell = row.createCell(cellIndex++);
        cell.setCellValue(employee.getRankTitle().getName());
        //assessment rank
        cell = row.createCell(cellIndex++);
        cell.setCellValue(generateAssessmentFormula(rankingDecision, employee.getRankTitle()));
        //criteria list
        for (RankingDecisionCriteria rdc : rankingDecision.getRankingDecisionCriterias()
        ) {
            createDropdown(row.getSheet(), row, cellIndex, rdc.getRankingDecisionCriteriaId().getCriteria());
            cell = row.createCell(cellIndex++);
            cell.setCellValue(employee.getRankTitle().getOptions().stream()
                    .filter(opt -> opt.getCriteria().equals(rdc.getRankingDecisionCriteriaId().getCriteria()))
                    .collect(Collectors.toList()).get(0).getName());
        }

        //total
        cell = row.createCell(cellIndex);
        cell.setCellValue(rankTitleService.calculateRankScore(rankingDecision, employee.getRankTitle()));
        cell.setCellStyle(cellStyleFormatNumber);
    }

    public void writeTemplateFormat(RankingDecision rankingDecision, Row row) {
        CellStyle cellStyleFormatNumber = null;
        // Format number
        short format = (short) BuiltinFormats.getBuiltinFormat("#,##0");

        //Create CellStyle
        Workbook workbook = row.getSheet().getWorkbook();
        cellStyleFormatNumber = workbook.createCellStyle();
        cellStyleFormatNumber.setDataFormat(format);

        int cellIndex = 0;
        //id
        Cell cell = row.createCell(cellIndex++);
        cell.setCellValue(1);
        //name
        cell = row.createCell(cellIndex++);
        cell.setCellValue("Nguyen Van A");
        //current ranking
        cell = row.createCell(cellIndex++);
        cell.setCellValue("NULL");
        //rank title
        cell = row.createCell(cellIndex++);
        cell.setCellValue("NULL");
        //assessment rank
        cell = row.createCell(cellIndex++);
        cell.setCellValue("");
        //criteria list
        for (RankingDecisionCriteria rdc : rankingDecision.getRankingDecisionCriterias()
        ) {
            createDropdown(row.getSheet(), row, cellIndex, rdc.getRankingDecisionCriteriaId().getCriteria());
            cell = row.createCell(cellIndex++);
            cell.setCellValue("");
        }

        //total
        cell = row.createCell(cellIndex);
        cell.setCellValue("");
        cell.setCellStyle(cellStyleFormatNumber);
    }

    @Override
    public CellStyle createStyleForHeader(Sheet sheet) {
        // Create font
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 12); // font size
        font.setColor(IndexedColors.WHITE.getIndex()); // text color

        // Create CellStyle
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setFillForegroundColor(IndexedColors.AQUA.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        return cellStyle;
    }

    @Override
    public void autoSizeColumn(Sheet sheet, int lastColumn) {
        for (int columnIndex = 0; columnIndex < lastColumn; columnIndex++) {
            sheet.autoSizeColumn(columnIndex);
        }
    }

    @Override
    public void createOutputFile(Workbook workbook, String excelFilePath) throws IOException {
        try (OutputStream os = new FileOutputStream(excelFilePath)) {
            workbook.write(os);
        }
    }

    private Workbook getWorkbook(String excelFilePath) {
        Workbook workbook = null;

        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook();
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook();
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }
        return workbook;
    }

    private String generateAssessmentFormula(RankingDecision rankingDecision, RankTitle rankTitle) {
        String formula = "";
        for (Option option : rankTitle.getOptions()
        ) {
            Criteria criteria = option.getCriteria();
            int weight = criteria.getRankingDecisionCriterias().stream()
                    .filter(rdc -> rdc.getRankingDecisionCriteriaId().getRankingDecision().equals(rankingDecision))
                    .collect(Collectors.toList()).get(0).getWeight();
            int maxScore = criteria.getOptions().stream().findFirst()
                    .orElseThrow(() -> new EntityNotFoundException("Not found any Option in Criteria!")).getScore();
            formula += "(" + option.getScore() + " * " + weight + " / " + maxScore + ")";
        }
        return formula;
    }

    private void createDropdown(Sheet sheet, Row row, int cellIndex, Criteria criteria) {
        DataValidation dataValidation = null;
        DataValidationConstraint constraint = null;
        DataValidationHelper validationHelper = null;

        if (sheet instanceof XSSFSheet) {
            validationHelper = new XSSFDataValidationHelper((XSSFSheet) sheet);
        } else {
            validationHelper = new HSSFDataValidationHelper((HSSFSheet) sheet);
        }
        CellRangeAddressList addressList =
                new CellRangeAddressList(row.getRowNum(), row.getRowNum(), cellIndex, cellIndex);
        constraint = validationHelper.createExplicitListConstraint(
                criteria.getOptions().stream().map(Option::getName).toArray(String[]::new)
        );
        dataValidation = validationHelper.createValidation(constraint, addressList);
        dataValidation.setSuppressDropDownArrow(true);
        sheet.addValidationData(dataValidation);
    }
}
