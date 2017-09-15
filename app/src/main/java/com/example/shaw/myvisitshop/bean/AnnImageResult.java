package com.example.shaw.myvisitshop.bean;

import com.example.shaw.myvisitshop.bean.AnnImgs;

import java.util.List;

public class AnnImageResult {
    private int code;//请求返回码，0为成功
    private String msg;//请求放回信息
    private List<AnnImgs> body;//请求数据数组

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

    public List<AnnImgs> getBody() {
        return body;
    }

    public void setBody(List<AnnImgs> body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "AnnImageResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", body=" + body +
                '}';
    }
}
