package com.group4.rankingmanagementsystem.service;

import java.io.File;

public interface DownloadImportedFileService {
    File getImportedFileById(Long rankingHistoryId);
}
