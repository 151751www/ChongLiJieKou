package com.zhwy.app.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 关于文件的基本操作
 * @author Lisa
 * 
 * 
 * 2019-04-15 
 *   1、添加获取文件编码格式的方法
 *   2、指定编码格式写入文件
 *   3、读取指定的十进制文件（自动检测文件的编码格式）
 */
@Component
public class FileTool {
	private static Logger logger = LoggerFactory.getLogger(FileTool.class);

	@Autowired
	LonAndLat lonAndLat;
	/**
	 * 特别的：
		1、不管有没有出现异常，finally块中代码都会执行；
		2、当try和catch中有return时，finally仍然会执行；
		3、finally是在return后面的表达式运算后执行的（此时并没有返回运算后的值，而是先把要返回的值保存起来，不管finally中的代码怎么样，返回的值都不会改变，任然是之前保存的值），所以函数返回值是在finally执行前确定的；
		4、finally中最好不要包含return，否则程序会提前退出，返回值不是try或catch中保存的返回值。
	 */
	/**
	 * 获得文件的编码格式
	 * @param fileName 文件全路径
	 * @return UTF-8、Unicode、UTF-16BE、GBK
	 */
	public  String getEncodingType(String fileName){
		FileInputStream fis=null;
		BufferedInputStream bin=null;
        String code="Unicode";
		try {
			fis = new FileInputStream(fileName);
			bin = new BufferedInputStream(fis);
			int p = (bin.read() << 8) + bin.read();
			bin.close();
			switch (p) {
			case 0xefbb:
			    code = "UTF-8";
			    break;
			case 0xfffe:
			    code = "Unicode";
			    break;
			case 0xfeff:
			    code = "UTF-16BE";
			    break;
			default:
			    code = "GBK";
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getLocalizedMessage());
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		}finally{
			try {
				if(bin!=null){
					bin.close();
				}
				if(fis!=null){
					fis.close();
				}
			} catch (IOException ex) {
				logger.error(ex.getLocalizedMessage());
			}
		}
        return code;
    }

	/**
	 * 写入数据
	 * @param map 数据内容
	 * @param filePath 保存文件全路径
	 * @return 保存成功true;否则返回false  \r\n  换行;
	 * java多种写入的方法，如果是小文件（几M的文件）写入时，使用常规的io输入就行，最优选择是BufferedInportStream
	 */
	public  boolean writeDataToFile(Map<String,Object>map,String filePath,String enCode) throws Exception {
		boolean success=false;
		FileOutputStream fos=null;
		BufferedOutputStream bos=null;

		int length=((String[])map.get("X"))[0].length();
		fos=new FileOutputStream(filePath);
		bos=new BufferedOutputStream(fos);
		bos.write(map.get("TITLE").toString().getBytes(enCode));
		bos.write(map.get("VARIABLES").toString().getBytes(enCode));
		bos.write(map.get("zone").toString().getBytes(enCode));
		String [] obavArr= (String[]) map.get("VARIABLESARR");
		for (int i=0;i<obavArr.length;i++ ){
			String []obavDataArr= (String[]) map.get(obavArr[i]);
			String content="";
			for (int j=0;j<obavDataArr.length;j++){
				content+=formatStr(obavDataArr[j],length);
				if(j+1%10==0||j==obavDataArr.length-1){
					bos.write(content.getBytes(enCode));
					content="";
				}
			}
			bos.write("\\r\\n".getBytes(enCode));
		}
		if(bos!=null){
			bos.close();
		}
		if(fos!=null)
		{
			fos.close();
		}
		success=true;

		return success;
	}

	public void makeAfterTXTFile(String saveFile,String beforFile) {
		File file = new File(saveFile);
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		String enCode="UTF-8";
		FileInputStream fis=null;
		Scanner sc = null;
		double lonStr=0.0;
		double latStr=0.0;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, "utf-8");
			fis = new FileInputStream(beforFile);
			sc = new Scanner(fis, enCode);
			String []result;
			while (sc.hasNextLine()) {
				String[] lonLat=sc.nextLine().split("	");
				if(!lonLat[0].trim().equals("")&&!lonLat[1].trim().equals("")){
					lonStr=Double.parseDouble(lonLat[0].trim());
					latStr=Double.parseDouble(lonLat[1].trim());
					result=lonAndLat.GaussToBL(String.valueOf(lonStr),String.valueOf(latStr));
					osw.write(result[0]+"	"+result[1]); //写入内容
					osw.write("\r\n");  //换行
				}
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
	/**
	 * 读取指定的十进制的文本编辑器可打开的文件
	 * 非写入内存的方法，所以支持大文件操作
	 * @return 返回以separator分割的字符串
	 */
	public  String[][][] readDataFromFile(String path,int x,int y,int z) throws IOException {
		Reader reader = null;
		FileInputStream inputStream=null;
		Scanner sc=null;
		String cont="";
		String [][][] arraM=new String [x][y][z];
		String [] Arr;
		int i=0;
		int j=0;
		int k=0;
		int ai=0;
		try {
			 inputStream = new FileInputStream(new File(path));
			 sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				cont=sc.nextLine();
				if(!cont.equals("")&&!cont.trim().equals("")){
					Arr=cont.trim().split("\\s{1,}");
					if(z>Arr.length){//30>10
						out:for (int ii=i;ii<arraM.length;ii++){
							for (int jj=j;jj<arraM[ii].length;jj++){
								for (int kk=k;kk<arraM[ii][jj].length;kk++){
									arraM[ii][jj][kk]=Arr[ai];
									ai++;
									k++;
									if(z==k){
										if(ai==Arr.length){
											ai=0;
										}
										j++;
										k=0;
										if(j==arraM[ii].length){
											i++;
											j=0;k=0;
										}
										break out;
									}
									if(ai==Arr.length){
										ai=0;
										break out;
									}
								}
							}
						}
					}else if(z<Arr.length){

					}
				}
		    }
		} catch (FileNotFoundException e) {
			logger.error(e.getLocalizedMessage());
		} finally {
			if(reader!=null){
				reader.close();
			}
			if(inputStream!=null){
				inputStream.close();
			}
			if(sc!=null){
				sc.close();
			}
		}
		return arraM;
	}

	/**
	 * 读取指定的十进制的文本编辑器可打开的文件
	 * 非写入内存的方法，所以支持大文件操作
	 * @return 返回以separator分割的字符串
	 */
	public  Map<String,Object> readOneNumFile(String filepath) throws IOException {
		Reader reader = null;
		String cont="";
		Map<String,Object> map=new HashMap<String,Object>();
		String [] VARIABLES=null;
		String [][][] arraM=null;
		String [] Arr;
		int  iInt=0;
		int  jInt=0;
		int  kInt=0;
		int num=0;
		int i=0;
		int j=0;
		int k=0;
		int ai=0;
		try {
			File file=new File(filepath);
			FileInputStream inputStream = new FileInputStream(file);
			Scanner sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				cont=sc.nextLine();
				if(cont.startsWith("VARIABLES")){
					map.put("VARIABLES",cont);
					cont=cont.replace("VARIABLES","").replace("=","").replace("\"","").replace(" ","");
					VARIABLES=cont.split(",");
					map.put("VARIABLESARR",VARIABLES);

				}else if(cont.startsWith("TITLE")){
					map.put("TITLE",cont);
				}else if(cont.startsWith("zone")){
					map.put("zone",cont);
					String zoneCon=cont.replace(" ","");
					String []zoneArr=zoneCon.split(",");
					if(zoneArr.length==3){
						String iStr=zoneArr[0].split("=")[1];
						String jStr=zoneArr[1].split("=")[1];
						String kStr=zoneArr[2].split("=")[1].replace("F","");
						if(iStr!=null&&!iStr.equals("")&&jStr!=null&&!jStr.equals("")&&kStr!=null&&!kStr.equals("")){
							iInt=Integer.parseInt(iStr);
							jInt=Integer.parseInt(jStr);
							kInt=Integer.parseInt(kStr);
							map.put("iInt",iInt);
							map.put("jInt",jInt);
							map.put("kInt",kInt);
						}
					}
				}
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getLocalizedMessage());
		} finally {
			if(reader!=null){
				reader.close();
			}
		}
		return map;
	}

	public void getLineCon(MultipartFile multipartFile,int line){
		File file=transferToFile(multipartFile);
		try {
			readLineVarFile(file.getAbsolutePath(), line);
		}catch (Exception  e){

		}


	}

	public void makeFile(String[] lon,String [] lat,String saveFile) {
		File file = new File(saveFile);
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		Scanner sc = null;
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, "utf-8");
			for(int i=0;i<lon.length;i++){
				osw.write(lon[i]+"	"+lat[i]);  //换行
				osw.write("\r\n");
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

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private File transferToFile(MultipartFile multipartFile) {
//        选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
		File file = null;
		try {
			String originalFilename =multipartFile.getOriginalFilename();
			String[] filename = originalFilename.split("\\.");
			file=File.createTempFile(filename[0], filename[1]);
			multipartFile.transferTo(file);
			file.deleteOnExit();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	public String formatStr(String str, int length) {
		str = "   "+str;
		int strLen = str.getBytes().length;
		if (strLen < length) {
			int temp = length - strLen;
			for (int i = 0; i < temp; i++) {
				str = " "+str;
			}
		}
		return str ;
	}

	public String readLineVarFile(String fileName, int lineNumber) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName))); //使用缓冲区的方法将数据读入到缓冲区中
		String line = reader.readLine(); //定义行数
		if (lineNumber < 0 || lineNumber > getTotalLines(fileName)) //确定输入的行数是否有内容
		{
			System.out.println("不在文件的行数范围之内。");
		}
		int num = 0;
		while (line != null) 	//当行数不为空时，输出该行内容及字符数
		{
			if (lineNumber == ++num)
			{
				line = reader.readLine();
				break;
				//System.out.println("第" + lineNumber + "行: " + line+"     字符数为："+line.length());
			}

		}
		reader.close();
		return line;
	}

	// 文件内容的总行数
	public int getTotalLines(String fileName) throws IOException
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));
		LineNumberReader reader = new LineNumberReader(br);
		String s = reader.readLine(); //定义行数
		int lines = 0;
		while (s != null) //确定行数
		{
			lines++;
			s = reader.readLine();
		}
		reader.close();
		br.close();
		return lines; //返回行数
	}

	public int  chaijie(MultipartFile multipartFile,String savepath) {
		int fileNo = 1;
		try {
			String cont;
			FileWriter fw = new FileWriter(savepath+"/"+fileNo +".txt");
			File file=transferToFile(multipartFile);
			FileInputStream inputStream = new FileInputStream(file);
			Scanner sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				cont=sc.nextLine();
				fw.append(cont + "\r\n");
				if(cont.equals("")||cont.trim().equals("")){
					fw.close();
					fileNo ++ ;
					fw = new FileWriter(savepath+"/"+fileNo +".txt");
				}
			}
			fw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileNo;
	}

	public static void main(String[] args) {

	}


}
