package com.zhwy.app.common;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class LonAndLat {

    //  由高斯投影坐标反算成经纬度
    public List<String[]> GaussToBL(String[] XStr, String[] YStr)//, double *longitude, double *latitude)

    {
        List<String[]> list=new ArrayList<>();
        int ProjNo; int ZoneWide;
        String[] lonArr = new String[XStr.length];
        String [] latArr=new String[XStr.length];
        double longitude1,latitude1, longitude0, X0,Y0, xval,yval;//latitude0,
        double e1,e2,f,a, ee, NN, T,C, M, D,R,u,fai, iPI;
        iPI = 0.0174532925199433;
        //a = 6378245.0; f = 1.0/298.3; //54年北京坐标系参数
        a=6378140.0; f=1/298.257; //80年西安坐标系参数
        ZoneWide = 3;
        double X=0.0;
        double Y=0.0;
        for (int i=0;i<XStr.length;i++){
            X=Double.parseDouble(XStr[i]);
            Y=Double.parseDouble(YStr[i]);
            ProjNo = (int)(X/1000000L) ; //查找带号
            //longitude0 = (ProjNo-1) * ZoneWide + ZoneWide / 2;6度带算法
            longitude0=ProjNo*ZoneWide;//3度带算法
            longitude0 = longitude0 * iPI ; //中央经线


            X0 = ProjNo*1000000L+500000L;
            Y0 = 0;
            xval = X-X0; yval = Y-Y0; //带内大地坐标
            e2 = 2*f-f*f;
            e1 = (1.0-Math.sqrt(1-e2))/(1.0+Math.sqrt(1-e2));
            ee = e2/(1-e2);
            M = yval;
            u = M/(a*(1-e2/4-3*e2*e2/64-5*e2*e2*e2/256));
            fai = u+(3*e1/2-27*e1*e1*e1/32)*Math.sin(2*u)+(21*e1*e1/16-55*e1*e1*e1*e1/32)*Math.sin(
                    4*u)
                    +(151*e1*e1*e1/96)*Math.sin(6*u)+(1097*e1*e1*e1*e1/512)*Math.sin(8*u);
            C = ee*Math.cos(fai)*Math.cos(fai);
            T = Math.tan(fai)*Math.tan(fai);
            NN = a/Math.sqrt(1.0-e2*Math.sin(fai)*Math.sin(fai));
            R = a*(1-e2)/Math.sqrt((1-e2*Math.sin(fai)*Math.sin(fai))*(1-e2*Math.sin(fai)*Math.sin(fai))*(1-e2*Math.sin
                    (fai)*Math.sin(fai)));
            D = xval/NN;
            //计算经度(Longitude) 纬度(Latitude)
            longitude1 = longitude0+(D-(1+2*T+C)*D*D*D/6+(5-2*C+28*T-3*C*C+8*ee+24*T*T)*D
                    *D*D*D*D/120)/Math.cos(fai);
            latitude1 = fai -(NN*Math.tan(fai)/R)*(D*D/2-(5+3*T+10*C-4*C*C-9*ee)*D*D*D*D/24
                    +(61+90*T+298*C+45*T*T-256*ee-3*C*C)*D*D*D*D*D*D/720);
            //转换为度 DD
            lonArr[i]=String.format("%.4f", longitude1 / iPI);
            latArr[i]=String.format("%.4f", latitude1 / iPI);
        }
        list.add(lonArr);
        list.add(latArr);
        return list;
    }

    public  int [] GaussToBLToGauss(double longitude, double latitude)
    {
        int[] Arr=new int[2];
        int ProjNo=0; int ZoneWide;
        double longitude1,latitude1, longitude0,latitude0, X0,Y0, xval,yval;
        double a,f, e2,ee, NN, T,C,A, M, iPI;
        iPI = 0.0174532925199433;
        ZoneWide = 3;

        a=6378140.0; f=1/298.257; //80年西安坐标系参数
        ProjNo = (int)(longitude / ZoneWide) ;
        //longitude0 = ProjNo * ZoneWide + ZoneWide / 2;6度带算法
        longitude0=ProjNo*ZoneWide;//3度带算法
        longitude0 = longitude0 * iPI ;
        latitude0 = 0;
        System.out.println(latitude0);
        longitude1 = longitude * iPI ; //经度转换为弧度
        latitude1 = latitude * iPI ; //纬度转换为弧度
        e2=2*f-f*f;
        ee=e2*(1.0-e2);
        NN=a/Math.sqrt(1.0-e2*Math.sin(latitude1)*Math.sin(latitude1));
        T=Math.tan(latitude1)*Math.tan(latitude1);
        C=ee*Math.cos(latitude1)*Math.cos(latitude1);
        A=(longitude1-longitude0)*Math.cos(latitude1);
        M=a*((1-e2/4-3*e2*e2/64-5*e2*e2*e2/256)*latitude1-(3*e2/8+3*e2*e2/32+45*e2*e2
                *e2/1024)*Math.sin(2*latitude1)
                +(15*e2*e2/256+45*e2*e2*e2/1024)*Math.sin(4*latitude1)-(35*e2*e2*e2/3072)*Math.sin(6*latitude1));
        xval = NN*(A+(1-T+C)*A*A*A/6+(5-18*T+T*T+72*C-58*ee)*A*A*A*A*A/120);
        yval = M+NN*Math.tan(latitude1)*(A*A/2+(5-T+9*C+4*C*C)*A*A*A*A/24
                +(61-58*T+T*T+600*C-330*ee)*A*A*A*A*A*A/720);
        X0 = 1000000L*ProjNo+500000L;
        Y0 = 0;
        xval = xval+X0; yval = yval+Y0;
        Arr[0]=(int)xval;
        Arr[1]=(int)yval;

        //*X = xval;
        //*Y = yval;
        System.out.println("x："+Arr[0]);
        System.out.println("y："+Arr[1]);
        return Arr;
    }

    //  由高斯投影坐标反算成经纬度
    public static String[] GaussToBL(String XStr, String YStr)//, double *longitude, double *latitude)

    {
        String[] Arr=new String[2];
        int ProjNo; int ZoneWide;
        double longitude1,latitude1, longitude0, X0,Y0, xval,yval;//latitude0,
        double e1,e2,f,a, ee, NN, T,C, M, D,R,u,fai, iPI;
        iPI = 0.0174532925199433;
        a=6378140.0; f=1/298.257; //80年西安坐标系参数
        ZoneWide = 3;
        double X;
        double Y;
            X=Double.parseDouble(XStr);
            Y=Double.parseDouble(YStr);
            ProjNo = (int)(X/1000000L) ; //查找带号
            //longitude0 = (ProjNo-1) * ZoneWide + ZoneWide / 2;6度带算法
            longitude0=ProjNo*ZoneWide;//3度带算法
            longitude0 = longitude0 * iPI ; //中央经线
            X0 = ProjNo*1000000L+500000L;
            Y0 = 0;
            xval = X-X0; yval = Y-Y0; //带内大地坐标
            e2 = 2*f-f*f;
            e1 = (1.0-Math.sqrt(1-e2))/(1.0+Math.sqrt(1-e2));
            ee = e2/(1-e2);
            M = yval;
            u = M/(a*(1-e2/4-3*e2*e2/64-5*e2*e2*e2/256));
            fai = u+(3*e1/2-27*e1*e1*e1/32)*Math.sin(2*u)+(21*e1*e1/16-55*e1*e1*e1*e1/32)*Math.sin(
                    4*u)
                    +(151*e1*e1*e1/96)*Math.sin(6*u)+(1097*e1*e1*e1*e1/512)*Math.sin(8*u);
            C = ee*Math.cos(fai)*Math.cos(fai);
            T = Math.tan(fai)*Math.tan(fai);
            NN = a/Math.sqrt(1.0-e2*Math.sin(fai)*Math.sin(fai));
            R = a*(1-e2)/Math.sqrt((1-e2*Math.sin(fai)*Math.sin(fai))*(1-e2*Math.sin(fai)*Math.sin(fai))*(1-e2*Math.sin
                    (fai)*Math.sin(fai)));
            D = xval/NN;
            //计算经度(Longitude) 纬度(Latitude)
            longitude1 = longitude0+(D-(1+2*T+C)*D*D*D/6+(5-2*C+28*T-3*C*C+8*ee+24*T*T)*D
                    *D*D*D*D/120)/Math.cos(fai);
            latitude1 = fai -(NN*Math.tan(fai)/R)*(D*D/2-(5+3*T+10*C-4*C*C-9*ee)*D*D*D*D/24
                    +(61+90*T+298*C+45*T*T-256*ee-3*C*C)*D*D*D*D*D*D/720);
            //转换为度 DD
        Arr[0]=String.format("%.4f", longitude1 / iPI);
        Arr[1]=String.format("%.4f", latitude1 / iPI);
        System.out.println("lon"+Arr[0]);
        System.out.println("lat"+Arr[1]);
        return Arr;
    }
    public static void main(String[] args) {
        //115.38937	41.06611
      /*  GaussToBLToGauss(115.38937,41.06611);
        GaussToBL("38616900","4549051");*/


        //38616900.0  4549051.0
    }
}
