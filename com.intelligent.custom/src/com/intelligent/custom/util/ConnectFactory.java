package com.intelligent.custom.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class ConnectFactory {

	private static String url;
	private static String user;
	private static String password;
	private static Map<String, String> itemMap;
	private static Connection con;
	private static ResultSet rs;
	private static PreparedStatement stmt;
	private static final String SELECT_SQL = "select * from rawMate";
	private static final String INSERT_SQL = "insert into rawMate(itemId,exportTime) values(?,?)";

	public static Connection getConnect() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Properties properties = new Properties();
			InputStream is = ConnectFactory.class
					.getResourceAsStream("/com/intelligent/custom/util/oracle.properties");
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
	
	public static Map<String, String> getItemId() {
		itemMap = new HashMap<String,String>();
		try {
			con = ConnectFactory.getConnect();
			stmt = con.prepareStatement(SELECT_SQL);
			rs = stmt.executeQuery();
			while (rs.next()) {
				itemMap.put(rs.getString("itemId"), rs.getString("itemId"));
			}
		} catch (Exception e) {
		} finally {
			ConnectFactory.closeAll(rs, stmt, con);
		}
		return itemMap;
	}
	
	public static void insert(String item_id) {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dates = sdf.format(date);
		try {
			con = ConnectFactory.getConnect();
			PreparedStatement stmt = con.prepareStatement(INSERT_SQL);
			stmt.setString(1, item_id);
			stmt.setString(2, dates);
			stmt.executeUpdate();
		} catch (Exception e) {
		} finally {
			ConnectFactory.closeAll(rs, stmt, con);
		}
	}
	
	/**
	 * 关闭ResultSet
	 * 
	 * @param rs
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭Statement
	 * 
	 * @param st
	 */
	public static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭Connection
	 * 
	 * @param conn
	 */
	public static void closeConnection(Connection conn) {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭全部
	 * 
	 * @param rs
	 * @param sta
	 * @param conn
	 */
	public static void closeAll(ResultSet rs, Statement sta, Connection conn) {
		closeResultSet(rs);
		closeStatement(sta);
		closeConnection(conn);
	}
	
	

	public static void main(String[] args) {
		 ConnectFactory.insert("234234");
		 System.out.println(ConnectFactory.getConnect());
		 System.out.println(ConnectFactory.getItemId());
		// ConnectFactory.getInstance().insert("aaaa");
//		ConnectFactory.insert("aafd");
//		ConnectFactory.getItemId();
	}
}
