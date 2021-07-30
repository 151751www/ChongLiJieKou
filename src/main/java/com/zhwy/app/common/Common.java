package com.zhwy.app.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Common {

    @Autowired (required=false)
    HttpServletResponse response;

    //跨域
    public void getCrossOrigin(){
        response.setHeader("Access-Control-Allow-Origin","*");
        response.setHeader("Access-Control-Allow-Headers","*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Methods","PUT,POST,GET,DELETE,OPTIONS");
        response.setHeader("Content-Type", "application/json;charset=utf-8");
    }


    public String StrNull(Object object){
        String  str="";
        if(object==null||object.toString().equals("")){
            str="";
        }else{
            str=object.toString();
        }
        return str;
    }



}
