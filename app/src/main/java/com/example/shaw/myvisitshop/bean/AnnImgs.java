package com.example.shaw.myvisitshop.bean;

import org.litepal.crud.DataSupport;

/**
 * Created by Shaw on 2017/7/23.
 */

public class AnnImgs extends DataSupport {
    private int id;
    private String imgUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "AnnImgs{" +
                "id=" + id +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
