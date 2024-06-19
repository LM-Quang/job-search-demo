package com.helper;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class Helper {
    public static final int STATUS_NOT_CONFIRMED = 0;
    public static final int STATUS_CONFIRMING = 1;
    public static final int STATUS_CONFIRMED = 2;
    public static final int STATUS_ACTIVE = 1;

    public static final int ROLE_APPLICANT_ID = 1;
    public static final int ROLE_HR_ID = 2;
    public static final String ROLE_APPLICANT = "Ứng viên";
    public static final String ROLE_HR = "Nhà tuyển dụng";

    public static final String EMAIL_EXISTED = "Email đã tồn tại.";
    public static final String EMAIL_NOT_CORRECT = "Email không chính xác.";
    public static final String PASSWORD_NOT_CORRECT = "Mật khẩu không chính xác.";


    public static final int DEFAULT_PAGE = 1;
    public static final int RECRUITMENT_PER_PAGE = 5;
    public static final int APPLY_POST_PER_PAGE = 3;
    public static final int SAVE_JOB_PER_PAGE = 2;
    public static final int FOLLOW_COMPANY_PER_PAGE = 2;

    public static final String EMPTY = "empty";
    public static final String SEARCH_BY_NAME = "searchByName";
    public static final String SEARCH_BY_COMPANY = "searchByCompany";

    public static final String APPLY_WITH_EXISTED_CV = "1";

    public static final String PAGE_HOME = "home";
    public static final String PAGE_RECRUITMENT = "recruitment";

    public static int getNumOfPages(int numOfItems, int itemPerPage) {
        if (numOfItems % itemPerPage == 0) {
            return numOfItems / itemPerPage;
        }
        return numOfItems / itemPerPage + 1;
    }

    public static String removeAccent(String str) {
        String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("")
                .replaceAll("Đ","D")
                .replace("đ", "d");
    }
}
