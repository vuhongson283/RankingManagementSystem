package com.group4.rankingmanagementsystem.service;

import com.group4.rankingmanagementsystem.entity.Group;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImportTemplateService {
    Object getCellValue(Cell cell);
    void readTemplate(String excelFilePath, Group group) throws IOException, InvalidFormatException;
    void bulkRanking(Long groupId, MultipartFile excelFile) throws IOException, InvalidFormatException;
}
