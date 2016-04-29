package com.intelligent.custom.handlers;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.intelligent.custom.util.ProgressBar;
import com.intelligent.custom.util.Util;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.officeliveservices.ExcelExportOption;
import com.teamcenter.rac.officeliveservices.InterfaceExcelExportable;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class ExportERP extends AbstractHandler {
	private final String DEFAULT_EXPORT_DIR = "\\\\192.168.16.17\\file\\PDM&ERP\\ERP BOM\\";
	private final String TEMPLATE_FILE = "/resource/ERPBOMTemplate.xls";
	private InterfaceAIFComponent[] allTargets; // 获得所有展开的Component
	private Map<String, Integer> BOMId; // BOMId
	private Map<String, Integer> seqLineNum; // 子件行号
	private Map<String, String> seqNum; // 工序行号
	private Map<String, String> itemId; // 母件编码
	private final int ROW_NUM = 92;
	private int number;
	private AbstractAIFApplication app;
	private TCComponentBOMLine rootBomLine;
	private TCSession session;
	private File file;
	private AIFDesktop deskTop;

	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		BOMId = new HashMap<String, Integer>();
		seqLineNum = new HashMap<String, Integer>();
		seqNum = new HashMap<String, String>();
		itemId = new HashMap<String, String>();
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) this.app.getSession();
		((AbstractPSEApplication) app).expandBelow();
		this.rootBomLine = ((AbstractPSEApplication) app).getBOMPanel()
				.getTreeTable().getRoot();
		number = 1;
		new Thread() {
			public void run() {
				try {
					JFileChooser jfc = new JFileChooser();
					jfc.setDialogTitle("选择导出文件夹路径");
					jfc.setApproveButtonText("导出");
					jfc.setMultiSelectionEnabled(true);
					jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnvalue = jfc.showOpenDialog(deskTop);
					if (returnvalue == 0) {
						file = jfc.getSelectedFile();
						Thread thread = new Thread() {
							public void run() {
								if (file != null) {
									insertExcel(TEMPLATE_FILE, file.getPath());
								} else {
									insertExcel(TEMPLATE_FILE,
											DEFAULT_EXPORT_DIR);
								}
							}
						};
						ProgressBar.show((Frame) null, thread, "正在导出ERP,请稍候……",
								"导出成功", "导出失败，请检查数据是否正确！");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		return null;
	}

	public boolean insertExcel(String modelFilePathName, String filePath) {
		String bomTopItemID = null;
		String bomTopItemName = null;
		try {
			bomTopItemID = rootBomLine.getProperty("bl_item_item_id");
			bomTopItemName = rootBomLine.getProperty("bl_item_object_name");
			if (bomTopItemName.contains("/")) {
				throw new NumberFormatException();
			}
			/**
			 * 加载excel
			 */
			HSSFWorkbook wb = new HSSFWorkbook(this.getClass()
					.getResourceAsStream(modelFilePathName));
			/**
			 * 获得第一个文件中的第一个表单
			 */
			HSSFSheet sheet = wb.getSheetAt(0);
			getALLBOMLine(sheet);
		

			FileOutputStream os = new FileOutputStream(
					filePath
						+ "\\"
						+ session.getUser().toString().split(" ")[0]
						+" "
						+ session.getGroup().toString().split(" ")[0]
						+" "
						+"ERP BOM"
						+ bomTopItemID
						+ "."
						+ rootBomLine.getProperty("bl_rev_item_revision_id")
						+" "
						+ bomTopItemName
						+ " "
						+ (new SimpleDateFormat("yyyy-MM-dd HH-mm-ss")).format(new Date().getTime())
						+ ".xls");
			wb.write(os);
			os.close();
		} catch (NumberFormatException numberFormatException) {
			JOptionPane.showMessageDialog(null, "文件名：" + bomTopItemName
					+ "不能包含斜线", "错误", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (Exception e) {
			if(e.toString().contains("SOA")){
				JOptionPane.showMessageDialog(null, "网络异常，请重新登录！", "网络错误", JOptionPane.ERROR_MESSAGE);
			}
			if (e.toString().contains("FileNotFoundException")) {
				JOptionPane.showMessageDialog(null, "系统找不到指定的路径,请添加公共盘！", "错误",
						JOptionPane.ERROR_MESSAGE);
			}
			e.printStackTrace();
		}
		return true;
	}

	@SuppressWarnings("unused")
	public boolean getALLBOMLine(HSSFSheet sheet) throws Exception {
		JPanel fd = ((AbstractAIFUIApplication) app).getApplicationPanel();
		allTargets = ((InterfaceExcelExportable) fd)
				.getComponentsToExport(ExcelExportOption.AllinView);
		String bomLevel="";
		TCComponentBOMLine bomLine_date=(TCComponentBOMLine)allTargets[0];
		String date_re2	=bomLine_date.getItemRevision().getProperty("creation_date");
		
		String pItem=bomLine_date.getItemRevision().getProperty("item_id");
		String pItemRs=bomLine_date.getItemRevision().getProperty("item_revision_id");
		//存储发放状态信息日志
		StringBuffer sb=new StringBuffer();
		for (int i = 1; i < allTargets.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) allTargets[i];
			if(bomLine==null){
				continue;
			}
			TCComponentItemRevision itemRevision = bomLine.getItemRevision();
			String rev_Release_Status = bomLine.getProperty("bl_rev_release_status_list"); // 获取发放状态
			Object date_re = Util.getNotNull(itemRevision.getProperty("date_released"));// 获取发放日期
//			 if ("废弃".contains(rev_Release_Status)) {
//				 System.out.println(bomLine+"已废弃！");
//	                continue;
//	         }
			String date_re1 = Util.getNotNull(bomLine.getItemRevision().getProperty("date_released"));
//			String creation_date = Util.getNotNull(bomLine.getItemRevision().getProperty("creation_date"));
			// 获得Iten类型
			String itemType = bomLine.getItemRevision().getProperty("object_type");
			
			String log="\r\n"+itemRevision.getProperty("item_id")+"的发放时间："+date_re+"----;发放状态为:"+rev_Release_Status;//
			sb.append(log);
			if (itemType.contains("A2AYTL_") || itemType.contains("A2BYTL_")) {
				continue;
			}
			
			 //判断父类没有发放，则其下子零件都不导出
			if(!bomLevel.isEmpty()){
				int level = Integer.parseInt(bomLevel);
				if(Integer.parseInt(bomLine.getProperty("bl_level_starting_0"))>level){
					continue;
				}else{
					bomLevel="";
				}
			}
			
			String parItemId = null;
			String parItemrevid = null;
			Row row = sheet.createRow(number);
			row = createCell(row);
			TCComponentBOMLine parentBOMLine = bomLine.getCachedParent();
			
			double num = Double
					.parseDouble(bomLine.getProperty("bl_quantity") == "" ? "1"
							: bomLine.getProperty("bl_quantity"));
			num=ExportBOM.testNum(bomLine, num);
			parItemId = parentBOMLine.getProperty("bl_item_item_id");
			parItemrevid = parentBOMLine.getProperty("bl_rev_item_revision_id");
			int revisionId = Integer.parseInt(parentBOMLine.getProperty("bl_rev_item_revision_id")); // 版本
			//判断是否发放，导出
			if (!itemType.contains("CPBM")&&date_re1.isEmpty() ) {
					bomLevel=bomLine.getProperty("bl_level_starting_0");
					continue;
				}
			if(itemType.contains("ZZBCP")||itemType.contains("PCBABCP")){
				continue;
			}
			row.getCell(0).setCellValue(getBOMID(parItemId)); // 子父级别相同
			row.getCell(1).setCellValue(1);
			row.getCell(2).setCellValue(pItemRs);// 版本代号
			row.getCell(3).setCellValue(revisionId);
//			if (parentBOMLine.getProperty("date_released").isEmpty()&&parentBOMLine.toString().contains("")) {
//				System.out.println(parentBOMLine+"母件编码未发放");
//				continue;
//			}
			row.getCell(4).setCellValue(date_re2.split(" ")[0]); // 版本日期
			row.getCell(7).setCellValue(pItem); // 母件编码
			row.getCell(8).setCellValue(0);
			row.getCell(9).setCellValue(3);
			row.getCell(11).setCellValue(i); // BOM子件Id
			row.getCell(12).setCellValue(getSeqLineNum(parItemId) * 10); // 子键id
			row.getCell(13).setCellValue(getSeqNum(parItemId,bomLine.getProperty("bl_item_item_id"))); // 工序行号
			//判断第一级如果是成品编码不管发放与否都导出
			if(date_re1.isEmpty()&&parentBOMLine.getItemRevision().getProperty("object_type").contains("CPBM")){
				row.getCell(14).setCellValue("");
			}else{
				if (itemType.contains("CPBM")|| itemType.contains("BJBM")|| itemType.contains("KCL")|| itemType.contains("库存类")|| itemType.contains("成品编码")) {
					row.getCell(14).setCellValue(bomLine.getProperty("bl_item_item_id"));// 子键编码
				} else {
					row.getCell(14).setCellValue(bomLine.getProperty("bl_item_item_id")+ "."+ bomLine.getProperty("bl_rev_item_revision_id"));// 子键编码
				}
			}
			
			
			if (date_re1.isEmpty()) {
				System.out.println(bomLine.getProperty("bl_item_item_id")+"子件编码未发放！");
				continue;
			}
			row.getCell(15).setCellValue(date_re2.split(" ")[0]); // 生效日期
			row.getCell(16).setCellValue("2099-12-31"); // 2099/12/31
			row.getCell(18).setCellValue(1);
			if(num<1){
				num=1;
			}
			row.getCell(19).setCellValue(num + "");
			row.getCell(20).setCellValue("1");
			row.getCell(22).setCellValue(i); // BOM子件选项Id
			row.getCell(23).setCellValue(0);
			row.getCell(26).setCellValue(3); // 供应类型
			row.getCell(27).setCellValue(1);
			row.getCell(28).setCellValue(0);
			row.getCell(29).setCellValue(2);
			row.getCell(30).setCellValue(100); // 计划比例
			row.getCell(32).setCellValue(1);
			row.getCell(33).setCellValue(1);
			row.getCell(34).setCellValue(0);
			row.getCell(89).setCellValue(
					bomLine.getItemRevision().getProperty("creation_date")
							.split(" ")[0]); // 录入日期
			row.getCell(90).setCellValue(
					bomLine.getItemRevision().getProperty("owning_user")
							.split(" ")[0]); // 录入员
			number++;
			if (!itemId.containsKey(parItemId)) {
				itemId.put(parItemId, parItemId);
			}
		}

		/*
		 * //替换子键编码 for (int i = 1; i <number; i++) { String cellValue =
		 * sheet.getRow(i).getCell(14).getStringCellValue();
		 * if(!"".equals(cellValue)){
		 * if(itemId.containsKey(Util.getItemId(cellValue))){
		 * sheet.getRow(i).getCell(14).setCellValue(Util.getItemId(cellValue));
		 * } } }
		 */
	      //输出发放状态日志
	      Util.getOutStringInText("ERP BOM "+rootBomLine.getProperty("bl_item_item_id"), sb);
		return true;
	}

	public Row createCell(Row row) {
		for (int i = 0; i < ROW_NUM; i++) {
			row.createCell(i);
		}
		return row;
	}

	public int getBOMID(String id) {
		if (!BOMId.containsKey(id)) {
			if (BOMId.size() == 0) {
				BOMId.put(id, 1);
			} else {
				Object[] objects = BOMId.values().toArray();
				Arrays.sort(objects);
				BOMId.put(id, ((int) objects[objects.length - 1]) + 1);
			}
		}
		return BOMId.get(id);
	}

	/**
	 * 子件行号
	 * 
	 * @param id
	 * @return
	 */
	public int getSeqLineNum(String id) {
		if (seqLineNum.containsKey(id)) {
			int value = seqLineNum.get(id);
			seqLineNum.put(id, value + 1);
		} else {
			seqLineNum.put(id, 1);
		}
		return seqLineNum.get(id);
	}

	/**
	 * 工序行号
	 * 
	 * @param id
	 * @return
	 */
	public String getSeqNum(String parentId, String id) {
		if (seqNum.containsKey(parentId + "@" + id)) {
			int num = Integer.parseInt(seqNum.get(parentId + "@" + id)) + 1;
			seqNum.put(parentId + "@" + id, Util.getStringDDDD(num));
		} else {
			seqNum.put(parentId + "@" + id, "0000");
		}
		return seqNum.get(parentId + "@" + id);
	}
}
