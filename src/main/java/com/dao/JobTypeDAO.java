package com.dao;

import com.entity.JobType;

import java.util.List;

public interface JobTypeDAO {
    JobType getJobTypeById(int id);
    List<JobType> getJobTypes();
}
