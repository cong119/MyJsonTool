package com.cong.my_json;

import java.util.Objects;

public class LearnSchool {

    private String schoolName;

    private String major;

    public LearnSchool() {

    }

    public LearnSchool(String schoolName, String major) {
        this.schoolName = schoolName;
        this.major = major;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

}
