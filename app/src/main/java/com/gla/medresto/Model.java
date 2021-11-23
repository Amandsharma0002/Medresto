package com.gla.medresto;

//model class is used to set and get the data from database

public class Model implements Comparable {
    String title, date, time;

    public Model() {
    }

    public Model(String title, String date, String time) {
        this.title = title;
        this.date = date;
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    @Override
    public int compareTo(Object o) {
        Model model = (Model) o;
        if (model.getTitle().equals(this.title) && model.getDate().equals(this.date) && model.getTime().equals(this.time)) {
            return 0;
        }
        return 1;
    }
}
