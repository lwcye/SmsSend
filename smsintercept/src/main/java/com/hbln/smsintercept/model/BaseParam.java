package com.hbln.smsintercept.model;

/**
 * Created by Administrator on 2018/4/7.
 */

public class BaseParam {
    int int0;
    int int1;
    String arg0;
    String arg1;

    public BaseParam(int int0, int int1, String arg0, String arg1) {
        this.int0 = int0;
        this.int1 = int1;
        this.arg0 = arg0;
        this.arg1 = arg1;
    }

    public BaseParam(int int0, int int1, String arg0) {
        this.int0 = int0;
        this.int1 = int1;
        this.arg0 = arg0;
    }

    public BaseParam(int int0, String arg0, String arg1) {
        this.int0 = int0;
        this.arg0 = arg0;
        this.arg1 = arg1;
    }

    public BaseParam(int int0, String arg0) {
        this.int0 = int0;
        this.arg0 = arg0;
    }
}
