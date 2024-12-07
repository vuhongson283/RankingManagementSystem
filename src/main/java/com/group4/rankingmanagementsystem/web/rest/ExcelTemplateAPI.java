package com.group4.rankingmanagementsystem.web.rest;

import com.group4.rankingmanagementsystem.service.DownloadImportedFileService;
import com.group4.rankingmanagementsystem.service.ExportTemplateService;
import com.group4.rankingmanagementsystem.service.ImportTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/excel-template")
public class ExcelTemplateAPI {

    private final ExportTemplateService exportTemplateService;
    private final ImportTemplateService importTemplateService;
    private final DownloadImportedFileService downloadImportedFileService;

    @Autowired
    public ExcelTemplateAPI(ExportTemplateService exportTemplateService, ImportTemplateService importTemplateService,
                            DownloadImportedFileService downloadImportedFileService) {
        this.exportTemplateService = exportTemplateService;
        this.importTemplateService = importTemplateService;
        this.downloadImportedFileService = downloadImportedFileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> exportFile(@PathVariable("id") Long groupId,
                                             @RequestParam(required = false) Long[] employeeIds){
        String filePath;
        File file;
        InputStreamResource resource;
        List<Long> employeeIdList = new ArrayList<>();
        if(employeeIds != null){
            employeeIdList = Arrays.asList(employeeIds);
        }
        try{
            filePath =exportTemplateService.export(groupId, employeeIdList);
            file = new File(filePath);
            resource = new InputStreamResource(new FileInputStream(file));
        }catch (Exception e){
            return new ResponseEntity<>("Error occurred exporting file. Please try again.", HttpStatus.BAD_REQUEST);
        }
        HttpHeaders header = new HttpHeaders();
        header.add("Content-Disposition", String.format("attachment; filename=\"%s\"",file.getName()));
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma","no-cache");
        header.add("Expires","0");

        ResponseEntity<Object> responseEntity;
        responseEntity = ResponseEntity.ok().headers(header).contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/txt")).body(resource);

        return responseEntity;
    }

    @PostMapping(value="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Object> importFile(@PathVariable("id") Long groupId,
                                             @RequestPart("file") MultipartFile file){
        try{
            importTemplateService.bulkRanking(groupId, file);
        }catch (Exception e){
            return new ResponseEntity<>("Error occurred importing file. Please try again.",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("File successfully imported.",HttpStatus.OK);
    }

    @GetMapping("/download-imported-file/{id}")
    public ResponseEntity<Object> downloadImportedFile(@PathVariable("id") Long rankingHistoryId){

        InputStreamResource resource;
        File importedFile;
        try{
            importedFile = downloadImportedFileService.getImportedFileById(rankingHistoryId);
            resource = new InputStreamResource(new FileInputStream(importedFile));
        }catch (Exception e){
            return new ResponseEntity<>("Error occurred exporting file. Please try again.", HttpStatus.BAD_REQUEST);
        }

        HttpHeaders header = new HttpHeaders();
        header.add("Content-Disposition", String.format("attachment; filename=\"%s\"",importedFile.getName()));
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma","no-cache");
        header.add("Expires","0");

        ResponseEntity<Object> responseEntity;
        responseEntity = ResponseEntity.ok().headers(header).contentLength(importedFile.length())
                .contentType(MediaType.parseMediaType("application/txt")).body(resource);

        return responseEntity;
    }
}
