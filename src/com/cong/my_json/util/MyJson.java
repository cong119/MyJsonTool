package com.cong.my_json.util;

import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MyJson {

    /**
     * 将对象转化成字符串
     *
     * @param obj
     * @return
     */
    public static String objectToString(Object obj) {
        try {

            StringBuffer jsonSb = new StringBuffer("{");

            Class clazz = obj.getClass();

            Field[] fields = clazz.getDeclaredFields();

            if (fields != null && fields.length > 0) {
                for (Field field : fields) {
                    String key = field.getName();

                    String getKeyMethodName = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
                    Method method = clazz.getDeclaredMethod(getKeyMethodName);

                    Object valueObj = method.invoke(obj);
                    String value = "";
                    if (valueObj instanceof List || valueObj instanceof Map) {
                        value = changeCollectionObjToStr(valueObj, field);
                    } else {
                        value = changeSimpleObjToStr(valueObj, field);
                    }

                    key = "\"" + key + "\"";
                    jsonSb.append(key).append(":").append(value).append(",");
                }
            }

            return jsonSb.toString().substring(0, jsonSb.length() - 1) + "}";
        } catch (NoSuchMethodException nsex) {
            nsex.printStackTrace();
        } catch (InvocationTargetException itex) {
            itex.printStackTrace();
        } catch (IllegalAccessException ilex) {
            ilex.printStackTrace();
        }

        return null;
    }

    private static String changeSimpleObjToStr(Object valueObj, Field field) {

        String value = "";

        if (valueObj instanceof Byte || valueObj instanceof Short || valueObj instanceof Integer
                || valueObj instanceof Long || valueObj instanceof Boolean
                || valueObj instanceof Float || valueObj instanceof Double) {

            value = String.valueOf(valueObj);
        } else if(valueObj instanceof Date) {

            Annotation fieldAnnotation = field.getAnnotation(DateJsonAnnotation.class);

            SimpleDateFormat sdf = null;
            if(fieldAnnotation != null) {
                sdf = new SimpleDateFormat(((DateJsonAnnotation) fieldAnnotation).value());
            } else {
                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            }

            value = "\"" + sdf.format(valueObj) + "\"";
        } else if(valueObj instanceof String) {
            value = "\"" + transferQuot(String.valueOf(valueObj)) + "\"";
        } else {
            value = objectToString(valueObj);
        }

        return value;
    }

    private static String changeCollectionEleObjToStr(Object obj, Field field) {
        String eleStr = "";

        if (isSimpleType(obj)) {
            eleStr = changeSimpleObjToStr(obj, field);
        } else if (obj instanceof List || obj instanceof Map) {
            eleStr = changeCollectionObjToStr(obj, field);
        } else {
            eleStr = objectToString(obj);
        }

        if(obj instanceof Date) {
            eleStr = "\"" + eleStr.substring(1, eleStr.length() - 1) + "\"";
        }

        return eleStr;
    }

    private static String changeCollectionObjToStr(Object obj, Field field) {

        String value = "";

        if(obj instanceof List) {
            value = value + "[";

            List valueList = (ArrayList) obj;

            if(valueList != null && valueList.size() >= 0) {
                for(int i = 0 ; i < valueList.size(); i++) {
                    String eleStr = changeCollectionEleObjToStr(valueList.get(i), field);

                    value = value + eleStr + ",";
                }

                value = value.substring(0, value.length() - 1) + "]";
            }
        } else if(obj instanceof Map) {
            value = value + "{";

            Map valueMap = (HashMap) obj;

            if(valueMap != null && valueMap.size() > 0) {

                Iterator<Map.Entry> iterator = valueMap.entrySet().iterator();

                String eleKey = null;
                String eleValue = null;
                while(iterator.hasNext()) {
                    Map.Entry entry = iterator.next();

                    eleKey = changeCollectionEleObjToStr(entry.getKey(), field);
                    eleValue = changeCollectionEleObjToStr(entry.getValue(), field);

                    value = value + "\"" + eleKey + "\":" + eleValue + ",";
                }

                value = value.substring(0, value.length() - 1);
            }

            value = value + "}";
        }

        return value;
    }

    public static boolean isSimpleType(Object valueObj) {

        if(valueObj instanceof Byte || valueObj instanceof Short || valueObj instanceof Integer
                || valueObj instanceof Long || valueObj instanceof Boolean
                || valueObj instanceof Float || valueObj instanceof Double
                || valueObj instanceof Date || valueObj instanceof String) {
            return true;
        }

        return false;
    }

    private static String transferQuot(String str) {
        StringBuffer sb = new StringBuffer();

        for(int i = 0; i < str.length(); i++) {
            char currentCh = str.charAt(i);
            if('"' == currentCh) {
                sb.append("\\").append("\"");
            } else {
                sb.append(currentCh);
            }
        }

        return sb.toString();
    }

    public static <T> T stringToObject(String jsonStr, Class<T> clazz) {

        String fieldNotFoundSetMathod = "";
        try {
            T obj = clazz.newInstance();

            Map<String, String> parseJsonToMap = parseJsonString(jsonStr);

            Field[] fields = clazz.getDeclaredFields();

            if(fields != null && fields.length > 0) {
                for(Field field : fields) {
                    String fieldName = field.getName();
                    Class fieldType = field.getType();

                    String setFieldMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
                    fieldNotFoundSetMathod = fieldName;
                    Method method = clazz.getDeclaredMethod(setFieldMethodName, fieldType);

                    String value = parseJsonToMap.get(fieldName);

                    String StringName = fieldType.getName();

                    switch (fieldType.getName()) {
                        case "java.lang.String" : method.invoke(obj, value); break;
                        case "java.lang.Boolean" : case "boolean" : method.invoke(obj, Boolean.valueOf(value)); break;
                        case "java.lang.Byte" : case "byte" : method.invoke(obj, Byte.valueOf(value)); break;
                        case "java.lang.Short" : case "short" : method.invoke(obj, Short.valueOf(value)); break;
                        case "java.lang.Integer" : case "int" : method.invoke(obj, Integer.valueOf(value)); break;
                        case "java.lang.Long" : case "long" : method.invoke(obj, Long.valueOf(value)); break;
                        case "java.lang.Float" : case "float" : method.invoke(obj, Float.valueOf(value)); break;
                        case "java.lang.Double" : case "double" : method.invoke(obj, Double.valueOf(value)); break;
                        case "java.util.Date" : {
                            Annotation fieldAnnotation = field.getAnnotation(DateJsonAnnotation.class);

                            SimpleDateFormat sdf = null;
                            if(fieldAnnotation != null) {
                                sdf = new SimpleDateFormat(((DateJsonAnnotation) fieldAnnotation).value());
                            } else {
                                sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            }

                            method.invoke(obj, sdf.parse(value));
                            break;
                        }
                        case "java.util.List" : { //String To Object暂不支持List内部嵌套List/Map等情况
                            List parseList = parseValueToList(value, field);
                            method.invoke(obj, parseList);
                            break;
                        }
                        case "java.util.Map" : { //String To Object暂不支持Map内部嵌套Map/List等情况
                            Map parseMap = parseValueToMap(value, field);
                            method.invoke(obj, parseMap);
                            break;
                        }
                        default : {
                            method.invoke(obj, stringToObject(value, fieldType));
                        }
                    }

                }

            }
            return obj;
        } catch(MyJsonException mjex) {
            System.out.println(mjex);
        } catch(NoSuchMethodException nsex) {
            System.out.println(fieldNotFoundSetMathod + " not found setter method in class" + clazz.getName());
        } catch(IllegalAccessException iaex) {

        } catch(InvocationTargetException itex) {

        } catch (InstantiationException iex) {

        } catch (ClassNotFoundException cex) {

        } catch (ParseException pex) {

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static <T> List<T> parseValueToList(String value, Field field) throws MyJsonException, ParseException, Exception {

        List<T> parseList = new ArrayList<>();

        ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();

        Class<T> listGenericClass = null;
        Type[] listGenericTypeArr = listGenericType.getActualTypeArguments();
        if(listGenericTypeArr[0] instanceof ParameterizedTypeImpl) {  //嵌套数组
//            //todo 暂不支持
//            Class innerClass = (Class<T>) ((ParameterizedType) listGenericTypeArr[0]).getActualTypeArguments()[0];
//            listGenericClass = innerClass.getComponentType();
//
//            List<String> eleParseList = parseValueToList(value, field);
//
//            for(String eleParse : eleParseList) {
//                parseList.add(parseValueToList(eleParse, field));
//            }
        } else if (listGenericTypeArr[0] instanceof Class) {  //数组内部仅包含简单类型
            listGenericClass = (Class) listGenericType.getActualTypeArguments()[0];

            parseList = parseSimpleValueToList(value, listGenericClass, field);
        } else {
            throw new MyJsonException(field.getName() + "format is error");
        }

        return parseList;
    }

    private static <T> List<T> parseSimpleValueToList(String value, Class<T> listGenericClass, Field field) throws Exception{
        List<T> parseList = new ArrayList<>();

        List<String> eleStrList = parseValueToList(value, field.getName());

        for(String eleStr : eleStrList) {
            switch (listGenericClass.getName()) {
                case "java.lang.String" :
                case "java.lang.Boolean" : case "boolean" :
                case "java.lang.Byte" : case "byte" :
                case "java.lang.Short" : case "short" :
                case "java.lang.Integer" : case "int" :
                case "java.lang.Long" : case "long" :
                case "java.lang.Float" : case "float" :
                case "java.lang.Double" : case "double" :
                    parseList.add((T)eleStr); break;
                case "java.util.Date" : {
                    Annotation fieldAnnotation = field.getAnnotation(DateJsonAnnotation.class);

                    SimpleDateFormat sdf = null;
                    if(fieldAnnotation != null) {
                        sdf = new SimpleDateFormat(((DateJsonAnnotation) fieldAnnotation).value());
                    } else {
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }

                    parseList.add((T)sdf.parse(eleStr));
                    break;
                }
                default :
                    T obj = stringToObject(eleStr, listGenericClass);
                    parseList.add(obj);
            }
        }

        return parseList;
    }

    private static List<String> parseValueToList(String value, String fieldName) throws MyJsonException {

        List<String> eleList = new ArrayList<>();

        StringBuffer sb = new StringBuffer();

        Stack<Character> symbolStack = new Stack<>();

        for(int i = 1; i < value.length() - 1; i++) { //去掉字符串前后的[]
            char currentChar = value.charAt(i);

            sb.append(currentChar);

            if(currentChar == '[' || currentChar == '{') {
                symbolStack.push(currentChar);
            }

            if(currentChar == '}') {
                if(symbolStack.isEmpty()) {
                    throw new MyJsonException("Symbol of " + fieldName + " List is not match");
                } else {
                    symbolStack.pop();
                }
            }

            if(currentChar == ',' && symbolStack.isEmpty()) {
                eleList.add(sb.toString().substring(0, sb.length() - 1));
                sb = new StringBuffer();
            }

            if(currentChar == '}' && i == value.length() - 2) {
                eleList.add(sb.toString());
            }

            if(i == value.length() - 2 && symbolStack.isEmpty()) {
                eleList.add(sb.toString());
            }

        }

        return eleList;

    }

    private static <K, V> Map<K, V> parseValueToMap(String value, Field field) throws MyJsonException, ParseException {

        Map<K, V> parseMap = new HashMap<>();

        ParameterizedType listGenericType = (ParameterizedType) field.getGenericType();

        Class<K> mapGenericClassOfKey = null;
        Class<V> mapGenericClassOfValue = null;
        Type[] listGenericTypeArr = listGenericType.getActualTypeArguments();
        if(listGenericTypeArr[0] instanceof ParameterizedType
                || listGenericTypeArr[1] instanceof ParameterizedType) {  //嵌套Map
//            //todo 暂不支持
//            Class innerClass = (Class<T>) ((ParameterizedType) listGenericTypeArr[0]).getActualTypeArguments()[0];
//            listGenericClass = innerClass.getComponentType();
//
//            List<String> eleParseList = parseValueToList(value, field);
//
//            for(String eleParse : eleParseList) {
//                parseList.add(parseValueToList(eleParse, field));
//            }
        } else if (listGenericTypeArr[0] instanceof Class && listGenericTypeArr[1] instanceof Class) {  //数组内部仅包含简单类型
            mapGenericClassOfKey = (Class) listGenericType.getActualTypeArguments()[0];
            mapGenericClassOfValue = (Class) listGenericType.getActualTypeArguments()[1];

            parseMap = parseSimpleValueToMap(value, mapGenericClassOfKey, mapGenericClassOfValue, field);
        } else {
            throw new MyJsonException(field.getName() + "format is error");
        }

        return parseMap;

    }

    private static <K, V> Map<K, V> parseSimpleValueToMap(String value, Class<K> mapGenericClassOfKey, Class<V> mapGenericClassOfValue, Field field) throws MyJsonException, ParseException {
        Map<K, V> parseMap = new HashMap<>();

        Map<String, String> eleStrMap = parseJsonString(value);

        for(String eleStrKey : eleStrMap.keySet()) {
            K mapKey = null;
            switch (mapGenericClassOfKey.getName()) {
                case "java.lang.String" :
                case "java.lang.Boolean" : case "boolean" :
                case "java.lang.Byte" : case "byte" :
                case "java.lang.Short" : case "short" :
                case "java.lang.Integer" : case "int" :
                case "java.lang.Long" : case "long" :
                case "java.lang.Float" : case "float" :
                case "java.lang.Double" : case "double" :
                    mapKey = (K) eleStrKey; break;
                case "java.util.Date" : {
                    Annotation fieldAnnotation = field.getAnnotation(DateJsonAnnotation.class);

                    SimpleDateFormat sdf = null;
                    if(fieldAnnotation != null) {
                        sdf = new SimpleDateFormat(((DateJsonAnnotation) fieldAnnotation).value());
                    } else {
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }

                    mapKey = (K) sdf.parse(eleStrKey);
                    break;
                }
                default :
                    mapKey = stringToObject(eleStrKey, mapGenericClassOfKey);
            }

            V mapValue = null;
            switch (mapGenericClassOfValue.getName()) {
                case "java.lang.String" :
                case "java.lang.Boolean" : case "boolean" :
                case "java.lang.Byte" : case "byte" :
                case "java.lang.Short" : case "short" :
                case "java.lang.Integer" : case "int" :
                case "java.lang.Long" : case "long" :
                case "java.lang.Float" : case "float" :
                case "java.lang.Double" : case "double" :
                    mapValue = (V) eleStrMap.get(eleStrKey); break;
                case "java.util.Date" : {
                    Annotation fieldAnnotation = field.getAnnotation(DateJsonAnnotation.class);

                    SimpleDateFormat sdf = null;
                    if(fieldAnnotation != null) {
                        sdf = new SimpleDateFormat(((DateJsonAnnotation) fieldAnnotation).value());
                    } else {
                        sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    }

                    mapValue = (V) sdf.parse(eleStrMap.get(eleStrKey));
                    break;
                }
                default :
                    mapValue = stringToObject(eleStrMap.get(eleStrKey), mapGenericClassOfValue);
            }

            parseMap.put(mapKey, mapValue);
        }

        return parseMap;
    }

    private static Map<String, String> parseJsonString(String jsonStr) throws MyJsonException {

        try {

            if(jsonStr == null || jsonStr.length() <= 2) {
                throw new MyJsonException("Length of json is not valid");
            }

            if(jsonStr.charAt(0) != '{') {
                throw new MyJsonException("Location 0 expect \"{\"，but actural is " + jsonStr.charAt(0));
            }

            if(jsonStr.charAt(jsonStr.length() - 1) != '}') {
                throw new MyJsonException("Location " + (jsonStr.length() - 1) + " expect \"}\"，but actural is " + jsonStr.charAt(jsonStr.length() - 1));
            }

            Map<String, String> jsonMap = new HashMap<>();

            for(int startLoc = 1; startLoc < jsonStr.length() - 1; ) {
                StringBuffer key = new StringBuffer();
                StringBuffer value = new StringBuffer();

                int slideLoc = startLoc;
                char currentChar = jsonStr.charAt(slideLoc);

                //找到当前key-value在字符串中的开始位置
                while((currentChar == ' ' || currentChar == '\n') && slideLoc < jsonStr.length() - 1) {
                    slideLoc = slideLoc + 1;
                    currentChar = jsonStr.charAt(slideLoc);
                }

                //找到开始位置了，作为一个key，要求该位置以"开头
                if(currentChar != '"') {
                    throw new MyJsonException("Location " + slideLoc + " expect \"\"\"，but actural is " + currentChar);
                }

                slideLoc = slideLoc + 1;
                currentChar = jsonStr.charAt(slideLoc);
                char lastChar = jsonStr.charAt(slideLoc - 1);

                //寻找key的下一个结束字符
                while((currentChar != '"' || (currentChar == '"' && lastChar == '\\')) && slideLoc < jsonStr.length() - 1) {
                    key.append(currentChar);
                    slideLoc = slideLoc + 1;

                    currentChar = jsonStr.charAt(slideLoc);
                    lastChar = jsonStr.charAt(slideLoc - 1);
                }
                //如果此循环结束，代表key找完了

                slideLoc = slideLoc + 1;
                currentChar = jsonStr.charAt(slideLoc);

                //找到当前key-value在字符串中分割符:的位置
                while((currentChar == ' ' || currentChar == '\n') && slideLoc < jsonStr.length() - 1) {
                    slideLoc = slideLoc + 1;
                    currentChar = jsonStr.charAt(slideLoc);
                }

                if(currentChar != ':') {
                    throw new MyJsonException("Location " + slideLoc + " expect \":\"，but actural is " + currentChar);
                }

                //找到当前key-value在字符串的value开始位置
                while((currentChar == ' ' || currentChar == '\n') && slideLoc < jsonStr.length() - 1) {
                    slideLoc = slideLoc + 1;
                    currentChar = jsonStr.charAt(slideLoc);
                }

                slideLoc = slideLoc + 1;
                currentChar = jsonStr.charAt(slideLoc);
                lastChar = jsonStr.charAt(slideLoc - 1);

                if(currentChar == '"') {  //value是一个字符串
                    //寻找value的下一个结束字符
                    slideLoc = slideLoc + 1;
                    currentChar = jsonStr.charAt(slideLoc);
                    lastChar = jsonStr.charAt(slideLoc - 1);

                    while(currentChar != '"' || (currentChar == '"' && lastChar == '\\')) {
                        value.append(currentChar);
                        slideLoc = slideLoc + 1;

                        currentChar = jsonStr.charAt(slideLoc);
                        lastChar = jsonStr.charAt(slideLoc - 1);
                    }
                    //如果此循环结束，代表value找完了
                    slideLoc = slideLoc + 1;
                } else if(currentChar >= '0' && currentChar <= '9') { //value是一个数字
                    //寻找value数字的结束位置，即,为位置
                    while(currentChar != ',') {
                        if(currentChar >= '0' && currentChar <= '9') {
                            value.append(currentChar);

                            slideLoc = slideLoc + 1;
                            currentChar = jsonStr.charAt(slideLoc);
                        } else {
                            throw new MyJsonException("Location " + slideLoc + " expect \"[0-9]\"，but actural is " + currentChar);
                        }
                    }
                    //如果此循环结束，代表value数字找完了
                } else if(currentChar == '[') { //value是一个数组
                    //需要找到与之匹配的]的位置
                    String arrStr = findValidSymbol(jsonStr, '[', ']', slideLoc);
                    value.append(arrStr);
                    slideLoc = slideLoc + arrStr.length();
                } else if(currentChar == '{') { //value是一个key-value组合
                    //需要找到与之匹配的}的位置
                    String mapStr = findValidSymbol(jsonStr, '{', '}', slideLoc);
                    value.append(mapStr);
                    slideLoc = slideLoc + mapStr.length();
                } else {
                    throw new MyJsonException("Location " + slideLoc + " expect \"\",[0-9],[,{\"，but actural is " + currentChar);
                }

                jsonMap.put(key.toString(), value.toString());


                currentChar = jsonStr.charAt(slideLoc);

                if(slideLoc < jsonStr.length() - 1 && currentChar != ',') {
                    throw new MyJsonException("Location " + slideLoc + " expect \",\"，but actural is " + currentChar);
                }

                startLoc = slideLoc + 1;
            }

            return jsonMap;
        } catch (MyJsonException mjex) {
            throw  mjex;
        }

    }

    private static String findValidSymbol(String str, char symbol, char find, int startLoc) throws MyJsonException {

        StringBuffer validStr = new StringBuffer();

        Stack<Character> symbolStack = new Stack<>();

        int index = startLoc;
        while(index < str.length() - 1) {
            char currentChar = str.charAt(index);

            validStr.append(currentChar);

            if(currentChar == symbol) {
                symbolStack.push(currentChar);
            } else if(currentChar == find) {
                symbolStack.pop();

                if(symbolStack.isEmpty()) {
                    return validStr.toString();
                }
            }

            index++;
        }

        if(str.charAt(index) != find) {
            throw new MyJsonException("After location " + startLoc + " expect \"" + find + "\"，but not found");
        } else {
            return validStr.toString();
        }
    }

}
