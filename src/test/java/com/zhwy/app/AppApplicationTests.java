package com.zhwy.app;

import com.zhwy.app.common.LonAndLat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@SpringBootTest
@PropertySource("classpath:address.properties")
class AppApplicationTests {
    @Value("${textPath}")
    String textPath;

    @Autowired
    LonAndLat lonAndLat;


    @Test
    void contextLoads() {
        makeTXTFile("F://application//lon.txt");
        lonAndLat.GaussToBLToGauss(115.39084,41.06792);
        //38616900.0  4549051.0

    }

    public void makeTXTFile(String saveFile) {
        File file = new File(saveFile);
        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        Reader reader = null;
        String enCode="UTF-8";
        FileInputStream fis=null;
        Scanner sc = null;
        StringBuilder sb=new StringBuilder();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            fos = new FileOutputStream(file);
            osw = new OutputStreamWriter(fos, "utf-8");
            fis = new FileInputStream(textPath);
            sc = new Scanner(fis, enCode);
            while (sc.hasNextLine()) {
                String[] lonLat=sc.nextLine().split(" ");
                double lonStr=Double.parseDouble(lonLat[0].trim());
                double latStr=Double.parseDouble(lonLat[2].trim());
                //String []result=lonAndLat.GaussToBL(lonStr,latStr);
               /* osw.write(result[0]+"  "+result[1]); //写入内容*/
                osw.write("\r\n");  //换行
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {   //关闭流
            try {
                if (osw != null) {
                    osw.close();
                }
                if (sc != null) {
                    sc.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
