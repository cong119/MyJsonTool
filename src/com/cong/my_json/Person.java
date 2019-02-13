package com.cong.my_json;

import com.cong.my_json.util.DateJsonAnnotation;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class Person {

    private String name;

    private int age;

    @DateJsonAnnotation(value = "yyyy-MM-dd")
    private Date birthday;

    LearnSchool majorSchool;

    private List<LearnSchool> schools;

    private List<Integer> types;

    private List<List<Date>> dateGroups;

    private Map<Integer, Date> dateMap;

    private Map<Integer, LearnSchool> schoolsMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public LearnSchool getMajorSchool() {
        return majorSchool;
    }

    public void setMajorSchool(LearnSchool majorSchool) {
        this.majorSchool = majorSchool;
    }

    public List<LearnSchool> getSchools() {
        return schools;
    }

    public List<Integer> getTypes() {
        return types;
    }

    public void setTypes(List<Integer> types) {
        this.types = types;
    }

    public void setSchools(List<LearnSchool> schools) {
        this.schools = schools;
    }

    public List<List<Date>> getDateGroups() {
        return dateGroups;
    }

    public void setDateGroups(List<List<Date>> dateGroups) {
        this.dateGroups = dateGroups;
    }

    public Map<Integer, Date> getDateMap() {
        return dateMap;
    }

    public void setDateMap(Map<Integer, Date> dateMap) {
        this.dateMap = dateMap;
    }

    public Map<Integer, LearnSchool> getSchoolsMap() {
        return schoolsMap;
    }

    public void setSchoolsMap(Map<Integer, LearnSchool> schoolsMap) {
        this.schoolsMap = schoolsMap;
    }
}
