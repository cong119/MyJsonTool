package com.cong.my_json;

import static org.junit.Assert.*;

import com.cong.my_json.util.MyJson;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

public class MyJsonTest {

    private Person person;

    @Before
    public void initPerson() throws Exception  {
        person = new Person();
        person.setName("张\"聪");
        person.setAge(23);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        person.setBirthday(sdf.parse("2018-12-20"));

        person.setMajorSchool(new LearnSchool("西电", "软件工程"));

        List<LearnSchool> school = new ArrayList<>();
        school.add(new LearnSchool("西电", "软件工程"));
        school.add(new LearnSchool("西电", "CS"));
        person.setSchools(school);

        List<Integer> types = new ArrayList<>();
        types.add(1);
        types.add(2);
        types.add(3);
        person.setTypes(types);

        List<List<Date>> dateGroups = new ArrayList<>();
        List<Date> dateGroup1 = new ArrayList<>();
        dateGroup1.add(sdf.parse("2018-01-01"));
        dateGroup1.add(sdf.parse("2018-01-20"));
        dateGroup1.add(sdf.parse("2018-01-21"));
        dateGroups.add(dateGroup1);

        List<Date> dateGroup2 = new ArrayList<>();
        dateGroup2.add(sdf.parse("2018-02-02"));
        dateGroup2.add(sdf.parse("2018-02-20"));
        dateGroup2.add(sdf.parse("2018-02-21"));
        dateGroups.add(dateGroup2);

        person.setDateGroups(dateGroups);

        Map<Integer, Date> dateMap = new HashMap<>();
        dateMap.put(1, sdf.parse("2018-01-01"));
        dateMap.put(2, sdf.parse("2018-02-02"));
        dateMap.put(3, sdf.parse("2018-03-03"));
        person.setDateMap(dateMap);

        Map<Integer, LearnSchool> schoolsMap = new HashMap<>();
        schoolsMap.put(1, new LearnSchool("西电", "软件工程"));
        schoolsMap.put(2, new LearnSchool("西电", "CS"));
        person.setSchoolsMap(schoolsMap);
    }

    @Test
    public void objectToString() throws Exception {

        String jsonStr = MyJson.objectToString(person);
        System.out.println(jsonStr);

        assertEquals("{\"name\":\"张\\\"聪\",\"age\":23,\"birthday\":\"2018-12-20\",\"majorSchool\":{\"schoolName\":\"西电\",\"major\":\"软件工程\"},\"schools\":[{\"schoolName\":\"西电\",\"major\":\"软件工程\"},{\"schoolName\":\"西电\",\"major\":\"CS\"}],\"types\":[1,2,3],\"dateGroups\":[[\"2018-01-01 00:00:00\",\"2018-01-20 00:00:00\",\"2018-01-21 00:00:00\"],[\"2018-02-02 00:00:00\",\"2018-02-20 00:00:00\",\"2018-02-21 00:00:00\"]],\"dateMap\":{\"1\":\"2018-01-01 00:00:00\",\"2\":\"2018-02-02 00:00:00\",\"3\":\"2018-03-03 00:00:00\"},\"schoolsMap\":{\"1\":{\"schoolName\":\"西电\",\"major\":\"软件工程\"},\"2\":{\"schoolName\":\"西电\",\"major\":\"CS\"}}}", jsonStr);
    }

    @Test
    public void stringToObject() throws Exception {

        String jsonStr = MyJson.objectToString(person);
        System.out.println(jsonStr);

        Person newPerson = MyJson.stringToObject(jsonStr, Person.class);

        assertEquals(person, newPerson);
    }

}