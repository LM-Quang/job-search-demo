package com.dao;

import com.entity.SaveJob;

import java.util.List;

public interface SaveJobDAO {
    void addSaveJob(SaveJob newSaveJob);
    void deleteSaveJob(int id);
    void deleteSaveJob(int userId, int recruitmentId);
    boolean isExisted(int userId, int recruitmentId);
    long getNumOfSaveJobs(int userId);
    List<SaveJob> getSaveJobsByUserId(int userId, int pageNum);
}
