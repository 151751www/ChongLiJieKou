package com.zhwy.app.controller;

import com.zhwy.app.common.Common;
import com.zhwy.app.common.FileTool;
import com.zhwy.app.common.JsonResult;
import com.zhwy.app.common.LonAndLat;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(position = 9,tags = "崇礼精细化--dat文件经纬度转换")
@RestController
@SessionAttributes
@PropertySource("classpath:address.properties")
@RequestMapping("/DatFile")
public class CFDFileZhuanHuan {
    private static Logger logger = LoggerFactory.getLogger(CFDFileZhuanHuan.class);

    @Autowired
    Common common;
    @Autowired
    FileTool fileTool;
    @Autowired
    LonAndLat lonAndLat;

    @Value("${filePath}")
    String filePath;

    @ApiOperation(value = "Dat文件经纬度转换")
    @PostMapping("/getDatFile")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file",value = "原始文件",paramType = "formData",required = true,dataType = "file"),
            @ApiImplicitParam(name = "strlon",value = "起始经度",paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "endlon",value = "终止经度",paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "strlat",value = "起始纬度",paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "endlat",value = "终止纬度",paramType = "query",required = true,dataType = "String"),
            @ApiImplicitParam(name = "jingdu",value = "精度",paramType = "query",required = true,dataType = "String"),


    })
    public String getDatFile(MultipartFile file,String strlon,String endlon,String strlat,String endlat,String jingdu) {
        common.getCrossOrigin();
        JsonResult jsonResult=new JsonResult();
        try {
            String lons[]={"115.3830","115.3835","115.3840","115.3845","115.3850","115.3855","115.3860","115.3865","115.3870","115.3875"};
            String lat[]={"40.8780","40.8780","40.8780","40.8780","40.8780","40.8780","40.8780","40.8780","40.8780","40.8780","40.8780"};
            int filenum=fileTool.chaijie(file,filePath);
            Map<String,Object>map=fileTool.readOneNumFile(filePath+"/1.txt");
            int I= (int) map.get("iInt");
            int J=(int) map.get("jInt");
            int K=(int) map.get("kInt");
            String [] VARIABLESARR= (String[]) map.get("VARIABLESARR");
            Map<String,String[][][]>rsumap=new HashMap<>();
            String [][][] Arr=null;
            int fileKLinenum=fileTool.getTotalLines(filePath+"/"+2+".txt");
            String xstr="";
            String ystr="";
            String []xArr;
            String []yArr;
            int minX=0;
            int minY=0;
            double minV=0.0;
            for (int l=0;l<lons.length;l++){
                int [] latLon=lonAndLat.GaussToBLToGauss(Double.parseDouble(lons[l]),Double.parseDouble(lat[l]));
                for (int i=0;i<fileKLinenum;i++){
                    xstr=fileTool.readLineVarFile(filePath+"/"+2+".txt",i+1);
                    ystr=fileTool.readLineVarFile(filePath+"/"+3+".txt",i+1);
                    if(xstr!=null&&!xstr.equals("")&&!xstr.trim().equals("")){
                        xArr=xstr.trim().split("\\s{1,}");
                        yArr=ystr.trim().split("\\s{1,}");
                        for(int j=0;j<xArr.length;j++){
                            double chazhi=Math.pow((Double.parseDouble(xArr[j])-latLon[0]),2)
                                    +Math.pow((Double.parseDouble(yArr[j])-latLon[1]),2);
                            if(i==0&&j==0){
                                minV=chazhi;
                            }else{
                                if(chazhi<minV){
                                    minV=chazhi;
                                    minX=i;
                                    minY=j;
                                }
                            }
                        }

                    }
                }
                System.out.println(latLon[0]+" :"+latLon[1]+"minval:"+minV+",minX:"+minX+",miny:"+minY);
            }

            //fileTool.getLineCon(file,1087);
           /* String lon[]=null;
            String lat[]=null;
            for (Map.Entry<String,Object> entry:map.entrySet()){
                if(entry.getKey().indexOf("X")!=-1){
                    lon= (String[]) map.get("X");
                }else if(entry.getKey().indexOf("Y")!=-1){
                    lat=(String[]) map.get("Y");
                }
            }
            fileTool.makeFile(lon,lat,"F://application//befor.txt");
            fileTool.makeAfterTXTFile("F://application//After.txt","F://application//befor.txt");
            *///List<String[]> list=lonAndLat.GaussToBL(lon,lat);

            /*Arrays.sort(list.get(0));
            Arrays.sort(list.get(1));
            map.put("X",list.get(0));
            map.put("Y",list.get(1));
            boolean result=fileTool.writeDataToFile(map,filePath+file.getOriginalFilename(),"UTF-8");
            jsonResult.setData(result);*/
            jsonResult.setStatus("0");
            jsonResult.setErrorMessage("");
        }catch (Exception e){
            e.printStackTrace();
            logger.error(e.getMessage());
            jsonResult.setData("");
            jsonResult.setStatus("1");
            jsonResult.setErrorMessage(e.getMessage());
        }
        return jsonResult.toJsonResut();
    }

}
