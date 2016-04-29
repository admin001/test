package com.intelligent.custom.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class AccessUtil {
	private Connection connection;
	private Statement statement;
	// 空白mdb文件路径. 直接保存在src/cn/iwoo/dataexport/common/下.
	private final String blankMdbFilePath = "/resource/";
	// 空白mdb文件名
	private final String blankMdbFileName = "ERPTemplate.mdb";
	// 新mdb文件路径
	public static final String defaultSavedMdbFilePath = "C:\\Users\\Administrator\\Desktop\\";
	// 新mdb文件名
	public static final String defaultSavedMdbFileName = "IA_EEDatabase.mdb";
	// 需要保存到的新的mdb文件路径和名
	private String savedMdbFilePathAndName = defaultSavedMdbFilePath
			+ defaultSavedMdbFileName;
	// mdb文件后缀
	public static final String defaultSavedMdbFileExtension = ".mdb";
	// 标准的单件模式
	private static AccessUtil instance = new AccessUtil();

	private AccessUtil() {
	}

	public static AccessUtil getInstance() {
		return instance;
	}

	/**
	 * 
	 Description: 设置待保存的新的mdb文件路径和名
	 */
	public void setSavedFilePathAndName(String newFilePathAndName) {
		this.savedMdbFilePathAndName = newFilePathAndName;
	}

	/**
	 * 
	 Description: 删除已经存在的mdb文件
	 */
	public void deleteOldMdbFile() throws Exception {
		File oldTargetFile = new File(savedMdbFilePathAndName);
		if (oldTargetFile.exists()) {
			oldTargetFile.delete();
		}
	}

	/**
	 * 
	 Description: 将空白mdb文件拷贝到特定目录
	 */
	public void copyBlankMdbFile() throws Exception {
		InputStream is = this.getClass().getResourceAsStream(blankMdbFilePath + blankMdbFileName);
		OutputStream out = new FileOutputStream(savedMdbFilePathAndName);
		byte[] buffer = new byte[1024];
		int numRead;
		while ((numRead = is.read(buffer)) != -1) {
			out.write(buffer, 0, numRead);
		}
		is.close();
		out.close();
	}

	/**
	 * 
	 Description: 打开对mdb文件的jdbc-odbc连接
	 */
	public void connetAccessDB() throws Exception {
		Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
		String url = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ="
				+ savedMdbFilePathAndName.trim();
		connection = DriverManager.getConnection(url);
		statement = connection.createStatement();
	}

	/**
	 * 
	 Description: 执行特定sql语句
	 */
	public void executeSql(String sql) throws Exception {
		statement.execute(sql);
	}

	/**
	 * 
	 Description: 关闭连接
	 */
	public void closeConnection() throws Exception {
		statement.close();
		connection.close();
	}
	
	public static void main(String[] args) {
		
		try {
//			AccessUtil au = AccessUtil.getInstance();
//			au.copyBlankMdbFile();
//			au.connetAccessDB();
//			System.out.println(au.statement);
//			String insertSql = "insert into Transistor values ('001GR-00001-0/01','三极管','Y','SMT NPN SOT-23 20V 2.3A 0.5W INFINEON BSS806N        ','','','','','','','','','','','','','','','','','','','INFINEON','BSS806N','','','','','','','','','','')";
//			au.executeSql(insertSql);
//			au.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}