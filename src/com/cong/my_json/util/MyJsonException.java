package com.cong.my_json.util;

public class MyJsonException extends Exception {

    private static final long serialVersionUID = 1L;

    public MyJsonException(Exception e) {
        super(e);
    }

    public MyJsonException(String msg) {
        super(msg);
    }

    public MyJsonException(String msg, Exception e) {
        super(msg, e);
    }

}
