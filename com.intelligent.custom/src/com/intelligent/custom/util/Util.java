package com.intelligent.custom.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;

public class Util {

	public static String getNotNull(String value) {
		return (value == null ? "" : value);
	}

	public static String getNotFirstZero(String value) {
			return value.replaceFirst("^0*", "");
	}

	public static String getItemId(String value) {
		return value.substring(0, value.lastIndexOf("."));
	}

	public static String getUnit(String unit) {
		return unit.contains("Length") ? unit.split("_")[1] : unit;
	}

	public static String getDateYYYYMMDD(Date date) {
		return (new SimpleDateFormat("yyyy-MM-dd")).format(date);
	}

	public static String getDateYYYYMMDDHHMMSS(Date date) {
		return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date);
	}

	public static String getDateYYYYMMDD(String string) {
		return (string.split(" ")[0]).replace("-", "\\");
	}
	
	public static String replaceValue(String string) {
		return string.replace("&", "");
	}
	
	public static String trimValue(String string) {
		if(string.endsWith(",")){
			string = string.substring(0,string.lastIndexOf(","));
		}
		return string;
	}

	public static String getStringDDDD(int i) {
		return String.format("%1$04d", i);
	}
	
	public static String  judgePurchase(String itemId){
		if(itemId.startsWith("001")||itemId.startsWith("002")||itemId.startsWith("003")||
				itemId.startsWith("1")||itemId.startsWith("2")||itemId.startsWith("3")||
				itemId.startsWith("4")||itemId.startsWith("5")){
			return "1";
		}
		return "";
	}
	
	public static String  judgeConsume(String itemId){
		if(itemId.startsWith("001")||itemId.startsWith("002")||itemId.startsWith("003")||
				itemId.startsWith("1")||itemId.startsWith("2")||itemId.startsWith("3")||
				itemId.startsWith("4")||itemId.startsWith("5")||itemId.startsWith("051")
				||itemId.startsWith("9")){
			return "1";
		}
		return "";
	}
	
	public static String  judgeHomeMade(String itemId){
		if(itemId.startsWith("IA")||itemId.startsWith("051")
				||itemId.startsWith("9")){
			return "1";
		}
		return "";
	}
	
	//格式化字符串      柱长20mm+外牙长5mm
	public static String deleteProName(String s){
		if(s.contains("+")){
			return s.replace(",+", "+");
		}
		return s;
	}
	
	//用正则表达式判断是否是正常数
	public static boolean isNumeric(String str){ 
	    return str.matches("^(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");   
	}

	public static Object formtString(String str) {
		Object object = null;
		if ((!isNumeric(str))||"".equals(str)) {
			object = str;
		}else{
			Pattern pat = Pattern.compile("^\\d+\\.\\d+$");
			Matcher isMinNum = pat.matcher(str);
			if(isMinNum.matches()){
				object = Double.parseDouble(str);
			}else{
				object = Long.parseLong(str);
			}
		}
		return object;
	}

	public static String getDateAgoYYYYMMDD(String str, int i) {
		GregorianCalendar gc = null;
		try {
			gc = new GregorianCalendar();
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date myDate = formatter.parse(str);
			gc.setTime(myDate);
			gc.add(5, -i); // 向前i天
		} catch (Exception e) {
			e.printStackTrace();
		}
		return (new SimpleDateFormat("yyyy-MM-dd")).format(gc.getTime());
	}
	
	/**
	 * 
	 * @param modelFilePathName
	 * @param filePath
	 * @return
	 */
	public static String getUnitNumber(String unit){
		String UnitNum = "";
		if("M".equals(unit)){
			UnitNum = "003";
		}else if("KG".equals(unit)){
			UnitNum = "004";
		}else if("^2".equals(unit)){
			UnitNum = "021";
		}else if("L".equals(unit)){
			UnitNum = "022";
		}else if("ft.".equals(unit)){
			UnitNum = "029";
		}else{
			UnitNum = "001";
		}
		return UnitNum;
	}
	
	public  boolean insertExcel(String modelFilePathName, String filePath) {
		try {
			/**
			 * 加载excel
			 */
			HSSFWorkbook wb = new HSSFWorkbook(this.getClass()
					.getResourceAsStream(modelFilePathName));
			/**
			 * 获得第一个文件中的第一个表单
			 */
			HSSFSheet sheet = wb.getSheetAt(0);
			FileOutputStream os = new FileOutputStream(filePath + "ERP_"
					+ new Date().getTime() + ".xls");
			System.out.println("#######3");
			Row row = sheet.createRow(1);
			row.createCell(0).setCellValue("aaaaaaaaa");
			wb.write(os);
			os.close();
		} catch (Exception e) {
			if(e.toString().contains("FileNotFoundException")){
				JOptionPane.showMessageDialog(null,
						"系统找不到指定的路径!", "错误", JOptionPane.ERROR_MESSAGE);
			}
			e.printStackTrace();
		}
		return true;
	}

	
	
	
	public static void main(String[] args) {
		// Date date = new Date();
		// System.out.println(Util.getDateYYYYMMDD(date));

		// int value = 100;
		// System.out.println(Util.getStringDDDD(value));

		// String str = "2015-3-01";
		// System.out.println(Util.getDateAgoYYYYMMDD(str,2));
		// String[] ss = {"PCBA半成品","包材类","包材类1","客户零件","库存 气动类",
		// "库存包材类","库存机构类","库存电子类","库存电气类","库存辅助类","成品编码","文档编码",
		// "机加件类","机构类","气动类","电子类","电气类","组装半成品","辅助类"};
		// String ITEM_TYPE = "PCBA半成品包材类包材类1客户零件库存 气动类" +
		// "库存包材类库存机构类库存电子类库存电气类库存辅助类成品编码文档编码" +
		// "机加件类机构类气动类电子类电气类组装半成品辅助类";
		//
		// for (int i = 0; i < ss.length; i++) {
		// System.out.println(ITEM_TYPE.contains(ss[i])+ " " +i);
		// }

//		 System.out.println(Util.getItemId("3230.01"));
//		System.out.println(Util.formtString("004300"));
		String DEFAULT_EXPORT_DIR = "\\\\192.168.16.17\\file\\PDM&ERP\\原材料\\";
		String TEMPLATE_FILE = "/resource/ERPBOMTemplate.xls";
		Util util = new Util();
		util.insertExcel(TEMPLATE_FILE,DEFAULT_EXPORT_DIR);
//		String s = "单通,材料黄铜,螺纹M3,柱长10mm,外牙长3.5mm,";
//		System.out.println(Util.trimValue(s));
//		String s = "00426448481";
//		System.out.println(Util.judgePurchase(s));
//		System.out.println(Double.parseDouble("2.00"));
	}
	//导出excel的时，File名称
	/**
	 * 输出sb到e:/Siemens/test目录下以fileName+日期+毫秒命名的txt文本下
	 */
	public static void getOutStringInText(String fileName,StringBuffer sb){
		Date dt=new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String fileName1=fileName+dateFormat.format(dt)+dt.getTime();
		File file = new File("e:/Siemens/"+fileName1+".txt");
		//将信息写入到文件
		 try (FileOutputStream fop = new FileOutputStream(file)) {

			   // 如果文件不存在，创建
			   if (!file.exists()) {
			    file.createNewFile();
			   }

			   
			   byte[] contentInBytes = sb.toString().getBytes();
			   fop.write(contentInBytes);
			   fop.flush();
			   fop.close();
			   
			  } catch (IOException e) {
			   e.printStackTrace();
			  }
	}
}
