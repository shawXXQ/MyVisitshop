package com.example.shaw.myvisitshop.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Shaw on 2017/7/29.
 */

public class ShopList extends DataSupport implements Serializable {
    private int id;
    private String imgname;//图片名称
    private String name;//店面名称
    private String shopid;//店面id
    private String shoplocation;//店面位置
    private String shoplevel;//店面评分
    private String userid;//用户id
    private String visitdate;//巡店时间
    private String feedback;//店面评语
    private Boolean IsSelect;//是否是选择的店面

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgname() {
        return imgname;
    }

    public void setImgname(String imgname) {
        this.imgname = imgname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopid() {
        return shopid;
    }

    public void setShopid(String shopid) {
        this.shopid = shopid;
    }

    public String getShoplocation() {
        return shoplocation;
    }

    public void setShoplocation(String shoplocation) {
        this.shoplocation = shoplocation;
    }

    public String getShoplevel() {
        return shoplevel;
    }

    public void setShoplevel(String shoplevel) {
        this.shoplevel = shoplevel;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getVisitdate() {
        return visitdate;
    }

    public void setVisitdate(String visitdate) {
        this.visitdate = visitdate;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public Boolean getSelect() {
        return IsSelect;
    }

    public void setSelect(Boolean select) {
        IsSelect = select;
    }

    @Override
    public String toString() {
        return "ShopList{" +
                "id=" + id +
                ", imgname='" + imgname + '\'' +
                ", name='" + name + '\'' +
                ", shopid='" + shopid + '\'' +
                ", shoplocation='" + shoplocation + '\'' +
                ", shoplevel='" + shoplevel + '\'' +
                ", userid='" + userid + '\'' +
                ", visitdate='" + visitdate + '\'' +
                ", feedback='" + feedback + '\'' +
                ", IsSelect=" + IsSelect +
                '}';
    }
}
