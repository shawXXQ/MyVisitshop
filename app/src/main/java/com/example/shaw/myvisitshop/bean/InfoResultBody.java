package com.example.shaw.myvisitshop.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Shaw on 2017/7/24.
 */

public class InfoResultBody extends DataSupport {
    private int id;
    private String title;
    private String summary;
    private String imgurl;
    private String detail;

    public InfoResultBody() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "InfoResultBody{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", imgurl='" + imgurl + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
