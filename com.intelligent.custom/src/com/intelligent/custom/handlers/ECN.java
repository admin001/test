package com.intelligent.custom.handlers;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;

import com.intelligent.custom.util.ProgressBar;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class ECN extends AbstractHandler {
	String DEFAULT_EXPORT_DIR = "D:\\ExportedBOM";
	String TEMPLATE_FILE = "/resource/ECN-comp.xls";
	TCComponentBOMLine rootBomLine;
	List<InterfaceAIFComponent> groupTargets;
	AbstractAIFApplication app;
	AIFDesktop deskTop;
	TCSession session;
	File file;
	
	
	@Override
	public Object execute(ExecutionEvent arg0) {
		
		app = AIFUtility.getCurrentApplication();
		deskTop = AIFUtility.getCurrentApplication().getDesktop();
		session = (TCSession) this.app.getSession();
		((AbstractPSEApplication) app).expandBelow();
		groupTargets = new ArrayList<InterfaceAIFComponent>();
		this.rootBomLine = ((AbstractPSEApplication) app).getBOMPanel().getTreeTable().getRoot();
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
							String exportFileName = rootBomLine.getProperty("bl_item_item_id");

							public void run() {
								if (file != null) {
									insertExcel(TEMPLATE_FILE, file.getPath(),
											exportFileName);
									// thread.exit = true;
								}else{
									insertExcel(TEMPLATE_FILE,DEFAULT_EXPORT_DIR, exportFileName);
								}
							}
						};
						ProgressBar.show((Frame) null, thread,
								"正在导出...工程变更单...,请稍候……", "工程变更单导出成功",
								"导出失败，请检查数据是否正确！");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
			
		return null;
	}

	public boolean insertExcel( String modelFilePathName,String filePath,String rootId) {
		try {
			String proID = rootBomLine.getProperty("bl_item_item_id");//ID
			String proName = rootBomLine.getProperty("bl_item_object_name");//名称
			String proRevision = rootBomLine.getProperty("bl_rev_item_revision_id");//版本
			Date applyDate=new Date();//申请日期
			
			HSSFWorkbook wb = new HSSFWorkbook(this.getClass().getResourceAsStream(modelFilePathName));
		    HSSFSheet sheet = wb.getSheetAt(0); // 获得第1个工作薄
		        
		        // 填充上面的表格,数据需要从数据库查询
		        HSSFRow row4 = sheet.getRow(3); // 获得工作薄的第4行
		        HSSFRow row5 = sheet.getRow(4); // 获得工作薄的第5行
		        row4.getCell(2).setCellValue(proID);// 给单元格赋值
		        row4.getCell(7).setCellValue(proRevision);// 给单元格赋值
		        row5.getCell(2).setCellValue(proName);// 给单元格赋值
		        row5.getCell(7).setCellValue(applyDate);// 给单元格赋值
			if (proName.contains("/")) {
				throw new NumberFormatException();
			}
			FileOutputStream os = new FileOutputStream(
					filePath
							+ "\\"
							+ session.getUser().toString().split(" ")[0]
							+ " "
							+ session.getGroup().toString().split(" ")[0]
							+ " "
							+ "工程变更单"
							+ proID
							+ "."
							+ proRevision
							+ " "
							+ proName
							+ " "
							+ (new SimpleDateFormat("yyyy-MM-dd HH-mm-ss")).format(new Date().getTime()) + ".xls");
			wb.write(os);
			os.close();
		} catch (NumberFormatException numberFormatException) {
			JOptionPane.showMessageDialog(null, "文件名或ID不能包含斜线",
					"错误", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch (Exception e) {
			if (e.toString().contains("SOA")) {
				JOptionPane.showMessageDialog(null, "网络异常，请重新登录！", "网络错误",
						JOptionPane.ERROR_MESSAGE);
			}
			if (e.toString().contains("FileNotFoundException")) {
				JOptionPane.showMessageDialog(null, "系统找不到指定的路径,请添加公共盘！", "错误",
						JOptionPane.ERROR_MESSAGE);
			}
			e.printStackTrace();
		}
		return true;
	}
	
	
}
