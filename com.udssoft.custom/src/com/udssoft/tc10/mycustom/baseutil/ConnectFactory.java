package com.udssoft.tc10.mycustom.baseutil;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.log4j.chainsaw.Main;


public class ConnectFactory {

	private static String url;
	private static String user;
	private static String password;

	public static Connection getConnect() {
		Connection con = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Properties properties = new Properties();
			InputStream is = ConnectFactory.class.getResourceAsStream("/com/udssoft/tc10/mycustom/baseutil/oracle.properties");
			properties.load(is);
			url = properties.getProperty("url");
			user = properties.getProperty("user");
			password = properties.getProperty("password");
			con = DriverManager.getConnection(url, user, password);
			is.close();
		} catch (Exception e) {
		}
		return con;
	}
	public String[] getFirmByNum(String firmNum,String firmName,String itemType1){
		if(itemType1.equals("EDAComp")){
			itemType1="A2YTL_DZL";
		}
		StringBuffer sb=new StringBuffer();
		sb.append("select t.value, t.name from ");
		sb.append(itemType1+" t ");
		if(!StringUtil.isEmpty(firmNum)){
			sb.append(" where value = '"+firmNum+"'");
		}
		if(!StringUtil.isEmpty(firmName)){
			 firmName=firmName.toUpperCase();
			sb.append(" where upper(t.name) = '"+firmName+"' ");
		}
		sb.append(" order by t.value ");
		String[] assNum = new String[2];
		String sql=sb.toString();
		try{
			Statement stmt=ConnectFactory.getConnect().createStatement();
			ResultSet rs=stmt.executeQuery(sql);
			   while(rs.next())
			   {
			      assNum[0]=rs.getString(1);
			      assNum[1]=rs.getString(2);
			   }
			   if(!StringUtil.isEmpty(firmName)&&StringUtil.isEmpty(assNum[0])){
				   if(Pattern.compile("^[a-z,A-Z]*$").matcher(firmName).matches()){
					   firmName=firmName.toUpperCase();
				   }
					sql="select t.value, t.name from AAUDSSOFT_NUM t where  upper(t.name) like '%"+firmName+"%' order by t.name ";
					 stmt=ConnectFactory.getConnect().createStatement();
					 rs=stmt.executeQuery(sql);
					   while(rs.next())
					   {
					      assNum[0]=rs.getString(1);
					      assNum[1]=rs.getString(2);
					   }
				}
		}
		
		catch(Exception e){
		}
		return assNum;
	}
//	/**
//	 * 查询列数据
//	 * @param sql
//	 * @param column
//	 * @return
//	 */
//	public String queryColumn(String sql,String column){
//		String s=null;
//		try {
//			Statement stmt=ConnectFactory.getConnect().createStatement();
//			ResultSet rs=stmt.executeQuery(sql);
//			while(rs.next())
//			   {
//			      s=rs.getString(column);
//			      System.out.println(s+"-----");
//			   }
//		} catch (Exception e) {
//		}
//		return s;
//	}
	
	public void updateAssNum(String itemType,String num){
		String sql = "UPDATE AAUDSSOFT_NUM SET "+itemType+" = "+num+" where 1=1 ";
		try {
			Statement stmt=ConnectFactory.getConnect().createStatement();
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		System.out.println("色带-1！@#￥%……&*（）AAaa".toUpperCase());
	}
}
