package com.dubbohelper.admin.util;


import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;


/**
 * @Author Mr.zhang  2018-11-26 16:33
 */
public class XMLBase {

	public static Object xml2obj(Class cls,String xml)throws Exception{
		HashMap map = xml2map(xml);
		return map2obj(cls,map);
	}
	
	public static Object map2obj(Class cls,HashMap map)throws Exception{
		try {
			Object o = cls.newInstance(),ov;
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				String fieldName = field.getName();
				ov = map.get(fieldName);
				field.setAccessible(true);
				field.set(o,ov);
			}
			return o;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	public static HashMap xml2map(String xml){
		HashMap map = new HashMap();
		try{
			if(xml==null||xml.length()<1) {
				return map;
			}
			String ss,se;
			int si,ei,eii,xl=xml.length();
			for(int i=0;i<=xl;i++){
				ei = xml.indexOf("</", i);
				if(ei<0) break;
				i=ei;
				si = -1; eii = -1;
				for(int ii=ei;ii>=0;ii--){
					if(xml.charAt(ii)=='>'){
						si = ii;
						break;
					}
				}
				for(int ii=ei;ii<=xl;ii++){
					if(xml.charAt(ii)=='>'){
						eii = ii;
						break;
					}
				}
				if(si<0||eii<0) {
					break;
				}
				ss = xml.substring(ei+2,eii);
				se = xml.substring(si+1,ei);
				i = eii;
				map.put(ss, se.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	public static void main(String [] args){
		String filePath = "d:/test1/test2/test3/";
		File fp = new File(filePath);
		// 创建目录
		if (!fp.exists()) {
			fp.mkdirs();// 目录不存在的情况下，创建目录。
		}
		System.out.println("执行结束"+filePath);
	}
}
