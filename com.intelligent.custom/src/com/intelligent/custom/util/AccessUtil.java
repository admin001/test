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
	// �հ�mdb�ļ�·��. ֱ�ӱ�����src/cn/iwoo/dataexport/common/��.
	private final String blankMdbFilePath = "/resource/";
	// �հ�mdb�ļ���
	private final String blankMdbFileName = "ERPTemplate.mdb";
	// ��mdb�ļ�·��
	public static final String defaultSavedMdbFilePath = "C:\\Users\\Administrator\\Desktop\\";
	// ��mdb�ļ���
	public static final String defaultSavedMdbFileName = "IA_EEDatabase.mdb";
	// ��Ҫ���浽���µ�mdb�ļ�·������
	private String savedMdbFilePathAndName = defaultSavedMdbFilePath
			+ defaultSavedMdbFileName;
	// mdb�ļ���׺
	public static final String defaultSavedMdbFileExtension = ".mdb";
	// ��׼�ĵ���ģʽ
	private static AccessUtil instance = new AccessUtil();

	private AccessUtil() {
	}

	public static AccessUtil getInstance() {
		return instance;
	}

	/**
	 * 
	 Description: ���ô�������µ�mdb�ļ�·������
	 */
	public void setSavedFilePathAndName(String newFilePathAndName) {
		this.savedMdbFilePathAndName = newFilePathAndName;
	}

	/**
	 * 
	 Description: ɾ���Ѿ����ڵ�mdb�ļ�
	 */
	public void deleteOldMdbFile() throws Exception {
		File oldTargetFile = new File(savedMdbFilePathAndName);
		if (oldTargetFile.exists()) {
			oldTargetFile.delete();
		}
	}

	/**
	 * 
	 Description: ���հ�mdb�ļ��������ض�Ŀ¼
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
	 Description: �򿪶�mdb�ļ���jdbc-odbc����
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
	 Description: ִ���ض�sql���
	 */
	public void executeSql(String sql) throws Exception {
		statement.execute(sql);
	}

	/**
	 * 
	 Description: �ر�����
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
//			String insertSql = "insert into Transistor values ('001GR-00001-0/01','������','Y','SMT NPN SOT-23 20V 2.3A 0.5W INFINEON BSS806N        ','','','','','','','','','','','','','','','','','','','INFINEON','BSS806N','','','','','','','','','','')";
//			au.executeSql(insertSql);
//			au.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}