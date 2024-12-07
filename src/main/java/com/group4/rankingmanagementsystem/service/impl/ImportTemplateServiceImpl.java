package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.*;
import com.group4.rankingmanagementsystem.repository.*;
import com.group4.rankingmanagementsystem.service.ImportTemplateService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.sql.Timestamp;
import java.util.*;

@Service
public class ImportTemplateServiceImpl implements ImportTemplateService {

    private final RankTitleRepository rankTitleRepository;
    private final GroupRepository groupRepository;
    private final EmployeeRepository employeeRepository;
    private final CriteriaRepository criteriaRepository;
    private final OptionRepository optionRepository;
    private final RankingHistoryRepository rankingHistoryRepository;

    @Autowired
    public ImportTemplateServiceImpl(RankTitleRepository rankTitleRepository, GroupRepository groupRepository,
                                     EmployeeRepository employeeRepository, CriteriaRepository criteriaRepository,
                                     OptionRepository optionRepository,
                                     RankingHistoryRepository rankingHistoryRepository) {
        this.rankTitleRepository = rankTitleRepository;
        this.groupRepository = groupRepository;
        this.employeeRepository = employeeRepository;
        this.criteriaRepository = criteriaRepository;
        this.optionRepository = optionRepository;
        this.rankingHistoryRepository = rankingHistoryRepository;
    }

    public Workbook getWorkbook(InputStream inputStream, String excelFilePath) throws IOException {
        Workbook workbook = null;
        if (excelFilePath.endsWith("xlsx")) {
            workbook = new XSSFWorkbook(inputStream);
        } else if (excelFilePath.endsWith("xls")) {
            workbook = new HSSFWorkbook(inputStream);
        } else {
            throw new IllegalArgumentException("The specified file is not Excel file");
        }

        return workbook;
    }

    // Get cell value
    @Override
    public Object getCellValue(Cell cell) {
        CellType cellType = cell.getCellTypeEnum();
        Object cellValue = null;
        switch (cellType) {
            case BOOLEAN:
                cellValue = cell.getBooleanCellValue();
                break;
            case FORMULA:
                Workbook workbook = cell.getSheet().getWorkbook();
                FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                cellValue = evaluator.evaluate(cell).getNumberValue();
                break;
            case NUMERIC:
                cellValue = cell.getNumericCellValue();
                break;
            case STRING:
                cellValue = cell.getStringCellValue();
                break;
            case _NONE:
            case BLANK:
            case ERROR:
            default:
                break;
        }
        return cellValue;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class, Throwable.class}, isolation = Isolation.SERIALIZABLE)
    public void readTemplate(String excelFilePath, Group group) throws IOException, InvalidFormatException {
        List<Employee> employees = new ArrayList<>();
        // Get file
        InputStream inputStream = new FileInputStream(new File(excelFilePath));
        // Get workbook
        Workbook workbook = getWorkbook(inputStream, excelFilePath);
        //Get Sheet
        Sheet sheet = workbook.getSheetAt(0);

        Map<String, Integer> neededColumn = new HashMap<>();
        neededColumn.put("Employee ID", -1);
        neededColumn.put("Employee Name", -1);
        for (RankingDecisionCriteria rdc : group.getCurrentRankingDecision().getRankingDecisionCriterias()
        ) {
            neededColumn.put(rdc.getRankingDecisionCriteriaId().getCriteria().getName(),-1);
        }

        //Get All Rows
        Iterator<Row> iterator = sheet.iterator();
        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            // Get all cells
            Iterator<Cell> cellIterator = nextRow.cellIterator();

            //read header
            if (nextRow.getRowNum() == 0) {
                //read and mark column number of headers cell
                readHeaderCell(neededColumn, cellIterator);
                continue;
            }

            //read other cells
            Employee employee = new Employee();
            Map<String, String> criteriaOption = new HashMap<>();  //key:criteriaName - value:optionName
            while(cellIterator.hasNext()){
                Cell cell = cellIterator.next();
                Object cellValue = getCellValue(cell);
                int columnIndex = cell.getColumnIndex();

                if(columnIndex == neededColumn.get("Employee ID")){
                    employee.setId(((Double)cellValue).longValue());
                }else if(columnIndex == neededColumn.get("Employee Name")){
                    employee.setName((String)cellValue);
                }else{
                    String criteriaName = sheet.getRow(0).getCell(columnIndex).getStringCellValue();
                    if(neededColumn.containsKey(criteriaName)){//check if this is an criteria column
                        if (cellValue == null || cellValue.toString().isEmpty()) {
                            throw new InvalidFormatException("Wrong value input for criteria options. Update and try again.");
                        }
                        String optionName = (String)cellValue;
                        criteriaOption.put(criteriaName, optionName);
                    }
                }
            }
            //find ranktitle
            Employee actualEmployee = employeeRepository.findById(employee.getId())
                    .orElseThrow(()->new
                            EntityNotFoundException("Some employees do not exist in system. Update and try again."));
            if(!actualEmployee.getName().equals(employee.getName())){
                throw new EntityNotFoundException("Some employees do not exist in system. Update and try again.");
            }
            RankTitle rankTitle = getRankTitleByOptions(criteriaOption, group.getCurrentRankingDecision());
            if(!actualEmployee.getRankTitle().equals(rankTitle)){
                actualEmployee.setRankTitle(rankTitle);
                employeeRepository.save(actualEmployee);
            }
        }
    }

    @Override
    public void bulkRanking(Long groupId, MultipartFile excelFile) throws IOException, InvalidFormatException {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new EntityNotFoundException("Group not found."));
        if(group.getCurrentRankingDecision() == null){
            throw new InvalidFormatException("Group doesn't have Current Ranking Decision.");
        }

        String excelFilePath = "D:\\"+excelFile.getOriginalFilename();
        File convertFile = new File(excelFilePath);
        convertFile.createNewFile();
        FileOutputStream fOut = new FileOutputStream(convertFile);
        fOut.write(excelFile.getBytes());
        fOut.close();

        RankingHistory rankingHistory = new RankingHistory();
        rankingHistory.setRankingDecision(group.getCurrentRankingDecision().getName());
        rankingHistory.setFilePath(excelFilePath);
        rankingHistory.setFileName(excelFile.getOriginalFilename());
        rankingHistory.setUploadedAt(new Timestamp(System.currentTimeMillis()));
        rankingHistory.setGroup(group);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserDetails){
            UserDetails user = (UserDetails) authentication.getPrincipal();
            rankingHistory.setUploadedBy(user.getUsername());
        }

        try{
            readTemplate(excelFilePath, group);
            rankingHistory.setStatus(true);
        }catch (EntityNotFoundException | InvalidFormatException e){
            rankingHistory.setStatus(false);
            rankingHistory.setNote(e.getMessage());
        }catch (Exception e){
            System.out.println(e.getMessage());
            rankingHistory.setStatus(false);
            rankingHistory.setNote("Unknown error. Try again or contact dev team.");
        }
        rankingHistoryRepository.save(rankingHistory);
    }

    public void readHeaderCell(Map<String, Integer> neededColumn, Iterator<Cell> cellIterator) throws InvalidFormatException{
        while(cellIterator.hasNext()){
            Cell cell = cellIterator.next();
            String headerName = cell.getStringCellValue().trim();
            if(neededColumn.containsKey(headerName)){
                neededColumn.put(headerName, cell.getColumnIndex());
            }
        }

        if(neededColumn.values().contains(-1)){
            throw new InvalidFormatException("Wrong Template. Re-download latest template and try again.");
        }
    }

    public RankTitle getRankTitleByOptions(Map<String, String> criteriaOption, RankingDecision rankingDecision) throws EntityNotFoundException{
        List<Option> options = new ArrayList<>();
        for (Map.Entry en: criteriaOption.entrySet()
             ) {
            String criteriaName = (String)en.getKey();
            String optionName = (String)en.getValue();

            Criteria criteria = criteriaRepository.findByName(criteriaName)
                    .orElseThrow(()-> new
                            EntityNotFoundException("Wrong value input for criteria options. Update and try again."));
            Option option = optionRepository.findByNameAndCriteria(optionName, criteria)
                    .orElseThrow(()-> new
                            EntityNotFoundException("Wrong value input for criteria options. Update and try again."));
            options.add(option);
        }

        //Trong 1 Ranking Decision, từng option sẽ có list các rank title,
        //rank title chung của các list rank title của từng option là rank title cần tìm
        for (RankTitle rt:options.get(0).getRankTitles()
             ) {
            if(!rt.getRankingDecision().getId().equals(rankingDecision.getId())){
                continue;
            }
            boolean flag = true;
            //duyệt list options
            for (int i = 1; i < options.size(); i++) {
                // nếu option có liên kết với rank title thì tiếp túc
                if(options.get(i).getRankTitles().contains(rt)){
                    continue;
                }
                else{
                    flag = false;
                    break;
                }
            }
            if(flag){
                return rt;
            }
        }
        throw new EntityNotFoundException("Rank Title not found.");
    }
}
