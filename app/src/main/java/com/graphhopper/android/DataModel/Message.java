package com.graphhopper.android.DataModel;

import org.json.JSONObject;

/**
 * Created by aliparsa on 10/25/2014.
 */
public class Message {
    private int id;
    private String X1;
    private String X2;
    private String X3;
    private String X4;
    private String X5;
    private String X6;
    private String X7;
    private String X8;
    private String X9;
    private String X10;


    public Message(String x1, String x2, String x3, String x4, String x5, String x6, String x7, String x8, String x9, String x10) {
        X10 = x10;
        X9 = x9;
        X8 = x8;
        X7 = x7;
        X6 = x6;
        X5 = x5;
        X4 = x4;
        X3 = x3;
        X2 = x2;
        X1 = x1;
    }


    public void setX10(String x10) {
        X10 = x10;
    }

    public void setX9(String x9) {
        X9 = x9;
    }

    public void setX8(String x8) {
        X8 = x8;
    }

    public void setX7(String x7) {
        X7 = x7;
    }

    public void setX6(String x6) {
        X6 = x6;
    }

    public void setX5(String x5) {
        X5 = x5;
    }

    public void setX4(String x4) {
        X4 = x4;
    }

    public void setX3(String x3) {
        X3 = x3;
    }

    public void setX2(String x2) {
        X2 = x2;
    }

    public void setX1(String x1) {
        X1 = x1;
    }

    public String getX10() {
        return X10;
    }

    public String getX9() {
        return X9;
    }

    public String getX8() {
        return X8;
    }

    public String getX7() {
        return X7;
    }

    public String getX6() {
        return X6;
    }

    public String getX5() {
        return X5;
    }

    public String getX4() {
        return X4;
    }

    public String getX3() {
        return X3;
    }

    public String getX2() {
        return X2;
    }

    public String getX1() {
        return X1;
    }


    public static Message getObjectFromJSON(JSONObject obj) {
        try {
            return new Message(obj.getString("x1"),
                    obj.getString("x2"),
                    obj.getString("x3"),
                    obj.getString("x4"),
                    obj.getString("x5"),
                    obj.getString("x6"),
                    obj.getString("x7"),
                    obj.getString("x8"),
                    obj.getString("x9"),
                    obj.getString("x10"));
        }catch (Exception e){

        }
        return  null;
    }
}
