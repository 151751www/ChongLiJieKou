package com.zhwy.app.common;

import com.alibaba.fastjson.JSONObject;

public class JsonResult {
    private String status;
    private String errorMessage;
    private Object data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String toJsonResut(){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("status",status);
        jsonObject.put("errorMessage",errorMessage);
        jsonObject.put("data",data);
        return jsonObject.toJSONString();
    }
}
