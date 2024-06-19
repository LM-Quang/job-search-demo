package com.dao;

import com.entity.CV;

public interface CVDAO {
    void addCV(CV newCv);
    boolean updateCV (int id, String newName);
    void deleteCV (int id);
    CV getCVById(int id);
}
