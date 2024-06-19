package com.dao;

import com.entity.ApplyPost;
import com.entity.Recruitment;

import java.util.List;

public interface ApplyPostDAO {
    void deleteApplyPost(int id);
    long getApplyPosts(int userId);
    long getApplyPosts(List<Integer> recruitmentIds);
    boolean isRecruitmentApplied(int recruitmentId);
    boolean addApplyPost(ApplyPost newApplyPost);
    boolean isApplyPostExisted(int userId, int recruitmentId);
    Recruitment getRecruitmentWithMostApply();
    List<ApplyPost> getApplyPostByUserId(int userId, int pageNum);
    List<ApplyPost> getApplyPostsByRecruitmentId(int recruitmentId);
    List<ApplyPost> getApplyPostsFromRecruitmentIds(List<Integer> recruitmentIds, int pageNum);
}
