package com.intelligent.custom.handlers;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.intelligent.custom.util.ProgressBar;
import com.intelligent.custom.util.Util;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.ics.ICSFormat;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.officeliveservices.ExcelExportOption;
import com.teamcenter.rac.officeliveservices.InterfaceExcelExportable;
import com.teamcenter.rac.pse.AbstractPSEApplication;

/**
 * ERP 原材料
 * @author infodba
 *
 */
public class ExportRawMateOfERPBOM extends AbstractHandler {
	private final String DEFAULT_EXPORT_DIR = "\\\\192.168.16.17\\file\\PDM&ERP\\原材料\\";
	private final String TEMPLATE_FILE = "/resource/RawMaterialOfERPTemplate.xls";
	private final int ROW_NUM = 51;
	private AbstractAIFApplication app;
	private InterfaceAIFComponent[] allTargets; // 获得所有展开的Component
	private int number;
	private TCSession session;
	private TCComponentBOMLine rootBomLine;
	private File file;
	private AIFDesktop deskTop;
	private static final String BOM_TYPE = "Pricing+Library Path+Library Ref+Footprint Path+责任人+Footprint Ref+ComponentLink1Description+ComponentLink1URL";
	private static final String DELETE_PRONAME = "分类封装类型引脚数目功能类型材料备注IA型号接口类型外观颜色连接角度快断/慢断" 
			+ "是否带螺柱插座角度引脚类型是否带螺母/通孔是否自锁/是否带灯形状/颜色是否是否自锁叠层数量是否带刹车是否带蜂鸣器是否屏蔽" 
			+ "封装/外壳是否柔性线芯数是否带触发规格描述尺寸功率种类类别是否带灯表面处理工艺螺牙";// 添加后表示导出不带分类属性名称
			

	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		app = AIFUtility.getCurrentApplication();
		session = (TCSession) this.app.getSession();
		((AbstractPSEApplication) app).expandBelow();
		this.rootBomLine = ((AbstractPSEApplication) app).getBOMPanel()
				.getTreeTable().getRoot();
		number = 1;
		// 选择导出路径
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
						ProgressBar.show((Frame) null, thread,
								"正在导出...ERP原材料BOM...,请稍候……", "ERP原材料BOM导出成功",
								"导出失败，请检查数据是否正确！");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		return null;
	}

	public boolean insertExcel(String modelFilePathName, String filePath) {
		String bomTopItemID =null;
		String bomTopItemName = null;
		try {
			/**
			 * 加载excel
			 */
			InputStream is = this.getClass().getResourceAsStream(
					modelFilePathName);
			HSSFWorkbook wb = new HSSFWorkbook(is);
			/**
			 * 获得第一个文件中的第一个表单
			 */
			HSSFSheet sheet = wb.getSheetAt(0);
			HSSFSheet sheet1 = wb.getSheetAt(1);
			HSSFSheet sheet2 = wb.getSheetAt(3);
			HSSFSheet sheet6 = wb.getSheetAt(6);
			HSSFSheet sheet4 = wb.getSheetAt(7);
			HSSFSheet sheet5 = wb.getSheetAt(2);
			// 获得所有的itemId
			bomTopItemID = rootBomLine.getProperty("bl_item_item_id");
			bomTopItemName = rootBomLine.getProperty("bl_item_object_name");
			if (bomTopItemName.contains("/")) {
				throw new NumberFormatException();
			}
			FileOutputStream os = new FileOutputStream(
					filePath
						+ "\\"
						+ session.getUser().toString().split(" ")[0]
						+" "
						+ session.getGroup().toString().split(" ")[0]
						+" "
						+"ERP原材料"
					    + bomTopItemID
						+ "."
						+ rootBomLine.getProperty("bl_rev_item_revision_id")
						+" "
						+ bomTopItemName
						+ " "
						+ (new SimpleDateFormat("yyyy-MM-dd HH-mm-ss")).format(new Date().getTime())
						+ ".xls");
			getALLBOMLine(sheet, sheet1, sheet2, sheet4, sheet5,sheet6);
			wb.write(os);
			is.close();
			os.close();
		} catch (NumberFormatException numberFormatException) {
			JOptionPane.showMessageDialog(null, "文件名或ID不能包含斜线",
					"错误", JOptionPane.ERROR_MESSAGE);
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

	public boolean getALLBOMLine(HSSFSheet sheet, HSSFSheet sheet1,
			HSSFSheet sheet2, HSSFSheet sheet3, HSSFSheet sheet4, HSSFSheet sheet6)
			throws Exception {
		JPanel fd = ((AbstractAIFUIApplication) this.app).getApplicationPanel();
		allTargets = ((InterfaceExcelExportable) fd)
				.getComponentsToExport(ExcelExportOption.AllinView);
		// 获得所有的已导出的itemId
		//存储发放状态信息日志
		StringBuffer sb=new StringBuffer();
		// Map<String, String> itemMap = ConnectFactory.getItemId();
		for (int i = 0; i < allTargets.length; i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) allTargets[i];
			TCComponentItemRevision itemRevision = bomLine.getItemRevision();
			String itemId = itemRevision.getProperty("item_id");
			String rev_Release_Status = bomLine.getProperty("bl_rev_release_status_list"); // 获取发放状态
			Object date_re = Util.getNotNull(itemRevision.getProperty("date_released"));// 获取发放日期
			System.out.println(Util.getDateYYYYMMDDHHMMSS(new Date())+itemId+"--发放状态："+rev_Release_Status+"--发放日期："+date_re);
//			String beizhuPro = bomLine.getProperty("A2BZ"); // 工程师备注
			AIFComponentContext[] revForms = itemRevision
					.getChildren("IMAN_master_form_rev");
			String itemType = itemRevision.getProperty("object_type");
			String jibie=bomLine.getProperty("bl_level_starting_0");
			String log="\r\n"+itemRevision.getProperty("item_id")+"的发放时间："+date_re+"----;发放状态为:"+rev_Release_Status;//
			sb.append(log);
			if (itemType.contains("A2AYTL_") || itemType.contains("A2BYTL_")||rev_Release_Status.contains("废弃")) {
				continue;
			}
			if(!jibie.equals("0")&&itemType.contains("组装半成品")||!jibie.equals("0")&&itemType.contains("ZZBCP")){
				continue;
			}
			if (itemType.contains("ZZBCP")||itemType.contains("CPBM")||!"".equals(date_re)) {
				Row row = sheet.createRow(number);
				row = createCell(row);
				Row row1 = sheet1.createRow(number);
				row1 = createCell(row1);
				Row row2 = sheet2.createRow(number);
				row2 = createCell(row2);
				Row row3 = sheet3.createRow(number);
				row3 = createCell(row3);
				Row row4 = sheet4.createRow(number);
				row4 = createCell(row4);
				Row row6 = sheet6.createRow(number);
				row6 = createCell(row6);

				
				String itemRevisionId = itemRevision
						.getProperty("item_revision_id");
				if ((itemType.contains("库存类") ||itemType.contains("KCL") || itemType.contains("CPBM")|| itemType.contains("BJBM"))) {
					row.getCell(0).setCellValue(itemId); // 存货编码
					row1.getCell(0).setCellValue(itemId); // 存货编码
					row2.getCell(0).setCellValue(itemId); // 存货编码
					row3.getCell(0).setCellValue(itemId); // 存货编码
					row4.getCell(0).setCellValue(itemId); // 存货编码
					row6.getCell(0).setCellValue(itemId); // 存货编码
				} else {
					row.getCell(0).setCellValue(itemId + "." + itemRevisionId); // 存货编码
					row1.getCell(0).setCellValue(itemId + "." + itemRevisionId); // 存货编码
					row2.getCell(0).setCellValue(itemId + "." + itemRevisionId); // 存货编码
					row3.getCell(0).setCellValue(itemId + "." + itemRevisionId); // 存货编码
					row4.getCell(0).setCellValue(itemId + "." + itemRevisionId); // 存货编码
					row6.getCell(0).setCellValue(itemId + "." + itemRevisionId); // 存货编码
				}
				/*
				 * if ("".equals(itemRevision.getProperty("ps_children"))) { //
				 * if (itemMap.containsKey(itemId + "." + itemRevisionId)) { //
				 * continue; // } row.getCell(0).setCellValue(itemId + "." +
				 * itemRevisionId); // 存货编码 row1.getCell(0).setCellValue(itemId
				 * + "." + itemRevisionId); // 存货编码
				 * row2.getCell(0).setCellValue(itemId + "." + itemRevisionId);
				 * // 存货编码 row3.getCell(0).setCellValue(itemId + "." +
				 * itemRevisionId); // 存货编码 row4.getCell(0).setCellValue(itemId
				 * + "." + itemRevisionId); // 存货编码 //往数据库插入数据 //
				 * ConnectFactory.insert(itemId + "." + itemRevisionId); //
				 * itemMap.put(itemId + "." + itemRevisionId, itemId + "." // +
				 * itemRevisionId); } else { // if (itemMap.containsKey(itemId))
				 * { // continue; // } row.getCell(0).setCellValue(itemId); //
				 * 存货编码 row1.getCell(0).setCellValue(itemId); // 存货编码
				 * row2.getCell(0).setCellValue(itemId); // 存货编码
				 * row3.getCell(0).setCellValue(itemId); // 存货编码
				 * row4.getCell(0).setCellValue(itemId); // 存货编码 //
				 * ConnectFactory.insert(itemId); // itemMap.put(itemId,
				 * itemId); }
				 */

				Object spe_Des = "";
				String brand = "";
				String model = "";
				String luoYa = "";
				String length = "";
				String changdu = "";
				String miaoshu = "";
				String JLH = "";
				Map.Entry<String, String> entry =null;
				
				// 获得分类信息
				Map<String, String> map = itemRevision.getClassificationAttributes();
				TCComponentICO[] tcico = itemRevision.getClassificationObjects();
				if (tcico.length != 0) {
					ICSPropertyDescription[] icsPropertyDescriptions = tcico[0].getICSPropertyDescriptors();
					for (int j = 0; j < icsPropertyDescriptions.length; j++) {
						if (icsPropertyDescriptions[j].isHidden()) { // 判断此属性是否为隐藏状态，如果隐藏则跳过
							continue;
						}
						String name = (icsPropertyDescriptions[j]).getName(); // 获取分类属性
						String unit = Util.getUnit((icsPropertyDescriptions[j]).getUnit());
						ICSFormat icsFormat = icsPropertyDescriptions[j].getFormat();// 获取分类属性
						
						Object icsValue = "";
						String brand1 = "";
						if (icsFormat.getType() == -1) {
							brand1 = Util.getNotNull(icsFormat.getKeyLov()
									.getValueOfKey(map.get(name)));
							icsValue = Util.replaceValue(brand1);
						} else {
							brand1 = Util.getNotNull(map.get(name));
							// System.out.println(brand1+"");
							icsValue = Util.formtString(Util
									.replaceValue(brand1));
						}
						Object value = "".equals(icsValue) ? "" : icsValue+ unit;
						// System.out.println("value:" + value);
						if ("品牌".equals(name)) {
							if (brand1.contains("SEA&LAND")) {
								brand += "SEA&LAND";
							} else {
								brand += value; //
							}
						} else if ("型号".equals(name) || "图号".equals(name)) {
							model += value;
						} else if ("螺牙".equals(name)) {
							luoYa += value;
						} else if ("长度".equals(name)) {
							 length+= value;
							 changdu=name;
						} else {
							if (!"".equals(value) && !BOM_TYPE.contains(name)) {
								if (!DELETE_PRONAME.contains(name)) {
									spe_Des += name + value + ",";
								} else {
									spe_Des += value + ",";
								}
							}
						}
					}
				}
//				String beizhuPro = bomLine.getProperty("A2BZ"); // 工程师备注
				
//				if (beizhuPro != null) {
//					beiZhu2 = beizhuPro.getStringValue();
//				}
				// 机加件类通用属性
				if ("A2YTL_JJJLRevision".equals(itemType)||"A2YTL_JJBZJRevision".equals(itemType)||"A2YTL_ZZBCPRevision".equals(itemType)) {
                    TCProperty materialPro = itemRevision.getTCProperty("a2CL"); // 材料
                    TCProperty biaoMianPro = itemRevision.getTCProperty("a2BMCL"); // 表面处理
                    TCProperty reChuLiPro = itemRevision.getTCProperty("a2RCL"); // 热处理
                    TCProperty pingpaiPro = itemRevision.getTCProperty("a2PP"); // 品牌	
                    TCProperty miaoshuPro = itemRevision.getTCProperty("a2MS"); // 描述
					
					Object material = "";
					Object biaoMian = "";
					Object reChuLi = "";
					Object pingPai = "";
					Object miaoShu = "";
					if (materialPro != null&&!materialPro.getStringValue().contains("fromparent")) {
						material = materialPro.getStringValue();
						if (!"".equals(material)) {
							spe_Des += material + ",";
						}
					}
					if (biaoMianPro != null&&!biaoMianPro.getStringValue().contains("fromparent")) {
						biaoMian = biaoMianPro.getStringValue();
						if (!"".equals(biaoMian)) {
							spe_Des += biaoMian + ",";
						}
					}
					if (reChuLiPro != null&&!reChuLiPro.getStringValue().contains("fromparent")) {
						reChuLi = reChuLiPro.getStringValue();
						if (!"".equals(reChuLi)) {
							spe_Des += reChuLi + ",";
						}
					}
					if (pingpaiPro != null) {
						pingPai = pingpaiPro.getStringValue();
						if (!"".equals(pingPai)) {
							brand+=pingPai;
//							spe_Des += pingPai + ",";
						}
					}

					if (miaoshuPro != null) {
					    miaoShu = miaoshuPro.getStringValue();
					    if (!"".equals(miaoShu)) {
					        spe_Des += miaoShu + ",";
					    }
					}
					
				}
				row.getCell(1).setCellValue(
						itemRevision.getProperty("object_name")); // 存货名称
				if (itemType.equals("A2YTL_CPBMRevision")) {
					row.getCell(2).setCellValue("ICMAA");
				} else if (itemType.equals("A2YTL_ZZBCPRevision")||itemType.equals("A2YTL_DQBCP")) {
					row.getCell(2).setCellValue("ICMAB");
				} else if (itemType.equals("A2YTL_JJJLRevision")) {
					row.getCell(2).setCellValue("ICMAC");
				}else if (itemType.equals("A2YTL_JJBZJRevision")) {
					row.getCell(2).setCellValue("ICMAD");
				}else if (itemType.equals("A2YTL_KCLRevision")) {
					row.getCell(2).setCellValue("ICMAE");
				}else if (itemType.equals("A2YTL_BJBMRevision")) {
					row.getCell(2).setCellValue("ICMAF");
				}else if (itemType.equals("A2YTL_PCBABCPRevision")) {
					row.getCell(2).setCellValue("ICMAG");
				}else {
					row.getCell(2).setCellValue(
							itemRevision.getClassificationClass()); // 存货大类编码
				}
				row.getCell(3).setCellValue("01"); // 计量单位组编码
				String unit = bomLine.getProperty("bl_uom");
				row.getCell(4).setCellValue(Util.getUnitNumber(unit)); // 主计量单位编码
				row.getCell(7).setCellValue(JLH);// 旧料号
				
				String spe_Des_Re = "".equals(length) ? length : ("长度" + length+",");
                String spe_Des_Re1 = "".equals(length) ? length : ("*" + length+",");
				if(itemType.contains("DZL")||itemType.contains("DQL")||itemType.contains("QDL")||itemType.contains("JGL")||itemType.contains("BaoCai")||itemType.contains("FZL")|| itemType.contains("电子类") || itemType.contains("电气类") || itemType.contains("气动类") || itemType.contains("机构类") || itemType.contains("包材类") ){
					if(itemRevision.getProperty("object_string").contains("螺丝")){
						row.getCell(8).setCellValue(Util.deleteProName(Util.trimValue(spe_Des+luoYa +spe_Des_Re1))+ itemRevision.getProperty("object_desc")); // 规格型号
					}else{
						row.getCell(8).setCellValue(Util.deleteProName(Util.trimValue(spe_Des+luoYa +spe_Des_Re))+ itemRevision.getProperty("object_desc")); // 规格型号
					}
				}else{
					row.getCell(8).setCellValue(Util.deleteProName(Util.trimValue(spe_Des+luoYa +spe_Des_Re))+ itemRevision.getProperty("object_desc")); // 规格型号
				}
				row.getCell(23).setCellValue("1"); // 是否内销
				row.getCell(24).setCellValue("1"); // 是否外销
				row.getCell(25).setCellValue("1"); // 是否采购//row.getCell(25).setCellValue(Util.judgePurchase(itemId));
													// // 是否采购
				row.getCell(26).setCellValue("1"); // 是否生产耗用//row.getCell(26).setCellValue(Util.judgeConsume(itemId));
													// // 是否生产耗用
				row.getCell(28).setCellValue(Util.judgeHomeMade(itemId)); // 是否自制
				row.getCell(49).setCellValue(
						itemRevision.getProperty("creation_date")
								.split(" ")[0]); // 录入日期
				row.getCell(50).setCellValue(
						itemRevision.getProperty("owning_user")
								.split(" ")[0]); // 录入员

				// sheet1
				row1.getCell(29).setCellValue("1"); // 领料是否切除尾数
				row1.getCell(30).setCellValue("0"); // 是否出库跟踪入库

				TCComponentForm form = (TCComponentForm) revForms[0]
						.getComponent();
				TCProperty tcProperty = form
						.getFormTCProperty("a2outgoingtype");
				String outgoingtype = "";
				if (tcProperty != null) {
					outgoingtype = tcProperty.getStringValue();
				}
				Cell cell = row1.getCell(48);
				insertCell(cell, "0");
				row1.getCell(49).setCellValue("0");
				// sheet2
				row2.getCell(11).setCellValue("L"); // 计划方法
				row2.getCell(12).setCellValue("PE");
				row2.getCell(13).setCellValue("1");
				row2.getCell(15).setCellValue("1");
				row2.getCell(17).setCellValue("0");
				row2.getCell(18).setCellValue("1");
				row2.getCell(19).setCellValue("1");
				row2.getCell(20).setCellValue("0");
				row2.getCell(21).setCellValue("1");// 是否切除尾数
				row2.getCell(25).setCellValue("1");
				row2.getCell(26).setCellValue("1");
				row2.getCell(27).setCellValue("1");
				row2.getCell(28).setCellValue("1");
				// sheet3
				row3.getCell(6).setCellValue(brand); // 制造品牌
				row3.getCell(7).setCellValue(model); // 制造型号
				// sheet5
				row6.getCell(21).setCellValue(itemRevision.getProperty("owning_user").split(" ")[0]); // 建档人
				row6.getCell(22).setCellValue(itemRevision.getProperty("creation_date").split(" ")[0]); // 建档日期

				// sheet4
				row4.getCell(1).setCellValue("移动平均法"); // 计价方式
				number++;
			}else{
				System.out.println("物料未发放："+itemRevision.getProperty("item_id"));
			}
			
		}
		//输出发放状态日志
		Util.getOutStringInText("ERP原材料 "+rootBomLine.getProperty("bl_item_item_id"), sb);
		return true;
	}

	public Row createCell(Row row) {
		for (int i = 0; i < ROW_NUM; i++) {
			row.createCell(i);
		}
		return row;
	}

	public void insertCell(Cell cell, String outgoingtype) {
		if ("1".equals(outgoingtype)) {
			cell.setCellValue("入库倒冲");
		} else if ("2".equals(outgoingtype)) {
			cell.setCellValue("工序倒冲");
		} else if ("3".equals(outgoingtype)) {
			cell.setCellValue("虚拟件");
		} else if ("4".equals(outgoingtype)) {
			cell.setCellValue("直接供应");
		} else {
			cell.setCellValue("领用");
		}

	}
}
