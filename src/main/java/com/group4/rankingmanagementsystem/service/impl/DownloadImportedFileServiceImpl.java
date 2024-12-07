package com.group4.rankingmanagementsystem.service.impl;

import com.group4.rankingmanagementsystem.entity.RankingHistory;
import com.group4.rankingmanagementsystem.repository.RankingHistoryRepository;
import com.group4.rankingmanagementsystem.service.DownloadImportedFileService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class DownloadImportedFileServiceImpl implements DownloadImportedFileService {

    private final RankingHistoryRepository rankingHistoryRepository;

    @Autowired
    public DownloadImportedFileServiceImpl(RankingHistoryRepository rankingHistoryRepository) {
        this.rankingHistoryRepository = rankingHistoryRepository;
    }

    @Override
    public File getImportedFileById(Long rankingHistoryId) {
        RankingHistory rankingHistory = rankingHistoryRepository
                .findById(rankingHistoryId)
                .orElseThrow(()->new EntityNotFoundException("Ranking History not found."));
        return new File(rankingHistory.getFilePath());
    }
}
