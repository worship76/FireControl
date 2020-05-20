package com.nuc.server.bean;

import java.util.Arrays;

public class Data {

    private Integer id;
    private String sid;
    private String smoke;
    private String temperature;
    private String date;
    private String time;
    private int concentration[] = new int[20];

    public Data(){}

    public Data(Integer id, String sid, String smoke, String temperature, String date, String time, int[] concentration) {
        this.id = id;
        this.sid = sid;
        this.smoke = smoke;
        this.temperature = temperature;
        this.date = date;
        this.time = time;
        this.concentration = concentration;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", sid='" + sid + '\'' +
                ", smoke='" + smoke + '\'' +
                ", temperature='" + temperature + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", concentration=" + Arrays.toString(concentration) +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getSmoke() {
        return smoke;
    }

    public void setSmoke(String smoke) {
        this.smoke = smoke;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int[] getConcentration() {
        return concentration;
    }

    public void setConcentration(int[] concentration) {
        this.concentration = concentration;
    }
}
