package com.example.shaw.myvisitshop.bean;

import java.util.List;

/**
 * Created by Shaw on 2017/7/29.
 */

public class SelectShop {

    /**
     * code : 0
     * msg : 店面信息查询成功
     * shoplists : []
     */

    private int code;
    private String msg;
    private List<ShopLists> shoplists;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
    public List<ShopLists> getShoplists() {
        return shoplists;
    }

    public void setShoplists(List<ShopLists> shoplists) {
        this.shoplists = shoplists;
    }

    @Override
    public String toString() {
        return "SelectShop{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", shoplists=" + shoplists +
                '}';
    }

    public class ShopLists {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "ShopLists{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
