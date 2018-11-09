package translate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;




public class main {
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		int result1 = 0;
		File file = null;
		String path = null;
		JFileChooser fileChooser = new JFileChooser();
		FileSystemView fsv = FileSystemView.getFileSystemView();  //注意了，这里重要的一句
		System.out.println(fsv.getHomeDirectory());                //得到桌面路径
		fileChooser.setCurrentDirectory(fsv.getHomeDirectory());
		fileChooser.setDialogTitle("请选择文件...");
		fileChooser.setApproveButtonText("确定");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		result1 = fileChooser.showOpenDialog(null);
		if (JFileChooser.APPROVE_OPTION == result1) {
		    	   path=fileChooser.getSelectedFile().getPath();
		    	   System.out.println("path: "+path);
		}
		ArrayList<myType> list = new ArrayList<myType>();
		try {
				FileInputStream fis = new FileInputStream(path); 
		        InputStreamReader isr = new InputStreamReader(fis, "UTF-8"); 
		        BufferedReader br = new BufferedReader(isr); 
		        String line = null;
		     
		        int count = 0;
		        int hand = 0;
		        while ((line = br.readLine()) != null) {
//		        	System.out.println(line);
		        	myType thisLine = new myType();
		        	String []a = line.split(",");
		        	if(a.length != 3) {
		        		System.out.println("Error:" + hand + "行数据缺失");
		        	}
		        	else {
		        		thisLine.name = a[0];
		        		thisLine.lat = a[2];
		        		thisLine.lng = a[1];
		        		list.add(thisLine);
		        		count++;
		        	}
//		        	thisLine.name = line.split(",")[0];
		        	hand++;
		        	
		        	
		        	
//		            FileContent += line; 
//		            FileContent += "\r\n"; // 补上换行符 
		        }
		        System.out.println("一共有"+count+"条有效数据");

			 } catch (IOException e) {
			            e.printStackTrace();
			 }


		
	/**
	 * 组合数据
	 */
		
		int length = list.size();
		String request = "";
		String[]names = null;
		int hand = 0;
		int j = 0;
		while(length > 0) {
			if(length <= 10) {
				names = new String[length];
				j = 0;
				for(int i = hand;i<hand+length;i++) {
					request += list.get(i).lat + "," + list.get(i).lng + ";";
					names[j] = list.get(i).name;
					j++;
				}
				if(!request.equals("")) {
					request = request.substring(0,request.length()-1);
					translate(request,names);
				}
				hand += length;
				length = 0;
			}
			else {
				length = length - 10;
				names = new String[10];
				j = 0;
				for(int i = hand;i<hand+10;i++) {
					request += list.get(i).lat + "," + list.get(i).lng + ";";
					names[j] = list.get(i).name;
					j++;
				}
				if(!request.equals("")) {
					request = request.substring(0,request.length()-1);
					translate(request,names);
					request = "";
				}
				hand += 10;
				
			}
		}
		

		
			
		
		
	}
	public static void translate(String list,String[]names) {
//		System.out.println(list);
		 HttpURLConnection connection = null;
		 InputStream is = null;
		 BufferedReader br = null;
		 String result = null;// 返回结果字符串
		 try {
			JsonParser jsonParser = new JsonParser();
			URL url = new URL("https://apis.map.qq.com/ws/coord/v1/translate?locations="+ list +"&type=3&key=腾讯地图API");
			connection = (HttpURLConnection) url.openConnection();
	            // 设置连接方式：get
	        connection.setRequestMethod("GET");
	            // 设置连接主机服务器的超时时间：15000毫秒
	        connection.setConnectTimeout(15000);
	            // 设置读取远程返回的数据时间：60000毫秒
	        connection.setReadTimeout(60000);
	            // 发送请求
	        connection.connect();
	            // 通过connection连接，获取输入流
	            if (connection.getResponseCode() == 200) {
	                is = connection.getInputStream();
	                // 封装输入流is，并指定字符集
	                br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	                // 存放数据
	                StringBuffer sbf = new StringBuffer();
	                String temp = null;
	                while ((temp = br.readLine()) != null) {
	                    sbf.append(temp);
	                    sbf.append("\r\n");
	                }
	                result = sbf.toString();
	                JsonObject object=(JsonObject) jsonParser.parse(result); 
	                /**
	                 * 	lat	number	是	纬度
	                 * lng	number	是	经度
	                 */
//	                System.out.println(result);
	                JsonArray points = object.get("locations").getAsJsonArray();
//	                System.out.println("这个Array长度为" + points.size());
	                
	                for(int i = 0;i<points.size();i++) {
//	                	System.out.println(points.get(i));
	                	float lat = points.get(i).getAsJsonObject().get("lat").getAsFloat();
	                	float lng = points.get(i).getAsJsonObject().get("lng").getAsFloat();
	                	System.out.println(names[i]+","+lat+","+lng);
	                }
			
	            }
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
//			System.out.print(result);
	}

}
