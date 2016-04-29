package com.intelligent.custom.handlers;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
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
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentChangeItemRevision;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.ics.ICSFormat;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.officeliveservices.ExcelExportOption;
import com.teamcenter.rac.officeliveservices.InterfaceExcelExportable;
import com.teamcenter.rac.pse.AbstractPSEApplication;
import com.teamcenter.services.rac.core.DataManagementService;
import com.teamcenter.services.rac.core._2007_06.DataManagement.RelationAndTypesFilter;
import com.teamcenter.services.rac.core._2007_09.DataManagement.ExpandGRMRelationsOutput2;
import com.teamcenter.services.rac.core._2007_09.DataManagement.ExpandGRMRelationsPref2;
import com.teamcenter.services.rac.core._2007_09.DataManagement.ExpandGRMRelationsResponse2;
import com.teamcenter.services.rac.core._2007_09.DataManagement.ExpandGRMRelationship;

public class ExportBOMForNotStandard_NO_hierarchy extends AbstractHandler {
	private final String DEFAULT_EXPORT_DIR = "\\\\192.168.16.17\\file\\PDM&ERP\\工程 BOM\\";
	private final String TEMPLATE_FILE = "/resource/BOMTemplateForNotStandard_NO_hierarchy.xls";
	private final int CHANGE_START_ROW = 4;
	private final int ITEM_START_ROW = 6;
	private final int EXCEL_TOTAL_CELL = 15;
	private InterfaceAIFComponent[] allTargets; // 获得所有展开的Component
	private List<InterfaceAIFComponent> groupTargets;
	private static final String BOM_TYPE = "Pricing+Library Path+Library Ref+Footprint Path+责任人+Footprint Ref+ComponentLink1Description+ComponentLink1URL+旧料号+是否有3D";
	private static final String DELETE_PRONAME = "分类封装类型引脚数目功能描述类型材料备注IA型号接口类型外观颜色连接角度快断/慢断"
			+ "是否带螺柱插座角度引脚类型是否带螺母/通孔是否自锁/是否带灯形状/颜色是否是否自锁叠层数量是否带刹车是否带蜂鸣器是否屏蔽"
			+ "封装/外壳是否柔性线芯数是否带触发规格描述尺寸功率种类类别是否带灯表面处理工艺图号螺牙";// 添加后表示导出不带分类属性名称
	private AbstractAIFApplication app;
	private TCComponentBOMLine rootBomLine;
	private TCSession session;
	private File file;
	private AIFDesktop deskTop;

	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		app = AIFUtility.getCurrentApplication();
		groupTargets = new ArrayList<InterfaceAIFComponent>();
		session = (TCSession) this.app.getSession();
		((AbstractPSEApplication) app).expandBelow();
		this.rootBomLine = ((AbstractPSEApplication) app).getBOMPanel()
				.getTreeTable().getRoot();
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
							String exportFileName = rootBomLine
									.getProperty("bl_item_item_id");

							public void run() {
								if (file != null) {
									insertExcel(TEMPLATE_FILE, file.getPath(),
											exportFileName);
								} else {
									insertExcel(TEMPLATE_FILE,
											DEFAULT_EXPORT_DIR, exportFileName);
								}
							}
						};
						ProgressBar.show((Frame) null, thread,
								"正在导出...不分层BOM...,请稍候……", "不分层BOM导出成功",
								"导出失败，请检查数据是否正确！");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		return null;
	}

	@SuppressWarnings("rawtypes")
	public boolean insertExcel(String modelFilePathName, String filePath,
			String rootId) {
		String proName = "";
		String proID = "";
		try {
			proName = ((TCComponent) rootBomLine)
					.getProperty("bl_item_object_name");
			proID = rootBomLine.getProperty("bl_item_item_id");
			if (proName.contains("/")) {
				throw new NumberFormatException();
			}
			/**
			 * 加载excel
			 */
			HSSFWorkbook wb = new HSSFWorkbook(this.getClass()
					.getResourceAsStream(modelFilePathName));
//			/**
//			 * 获得第一个文件中的第一个表单
//			 */
//			HSSFSheet sheet = wb.getSheetAt(0);
//			Row row = sheet.getRow(3);
//			row.getCell(2).setCellValue(rootId); // 母件编码
//			row.getCell(6).setCellValue(proName);

			/**
			 * 获得第二个汇总表单
			 */
			HSSFSheet sheet = wb.getSheetAt(0);
			Row row = sheet.getRow(3);

			row.getCell(2).setCellValue(rootId); // 母件编码
			row.getCell(6).setCellValue(proName);

			// 变更记录
			Map localHashMap = getChanges(rootBomLine);

//			insertRow(sheet, localHashMap);
//
			String proRevision_ID = rootBomLine
					.getProperty("bl_rev_item_revision_id");

			int proRevision_ID_int = 0;
			if (!proRevision_ID.isEmpty()) {
				proRevision_ID_int = Integer.parseInt(proRevision_ID);
			}

			int bomStartRow = ITEM_START_ROW + proRevision_ID_int;
//			boolean writeExcel = getALLBOMLine(wb, sheet, bomStartRow, true);
//			// 分组
//			if (writeExcel)
//				groupRow(sheet, bomStartRow);

			/**
			 * 第二个汇总表的变更
			 */
			insertRow(wb,sheet, localHashMap);
			getALLBOMLine(wb, sheet, bomStartRow, false);

			FileOutputStream os = new FileOutputStream(
					filePath
					+ "\\"
					+ session.getUser().toString().split(" ")[0]
					+" "
					+ session.getGroup().toString().split(" ")[0]
					+" "
					+"不分层BOM"
					+ proID
					+ "."
					+ rootBomLine.getProperty("bl_rev_item_revision_id")
					+ " "
					+ proName
					+ " "
					+ (new SimpleDateFormat("yyyy-MM-dd HH-mm-ss")).format(new Date().getTime())
					+ ".xls");
			wb.write(os);
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
	private Double  testNum(TCComponentBOMLine tcBOMLine,Double number1) {
		Double parent_Item_Num=1.0;
		TCComponentBOMLine pBomLine = tcBOMLine.getCachedParent();
		try {
			if(pBomLine==null){
				return 1.0;
			}else{
				parent_Item_Num = Double
						.parseDouble(pBomLine
								.getProperty("bl_quantity") == "" ? "1.0"
										: pBomLine
										.getProperty("bl_quantity"));
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (TCException e) {
			e.printStackTrace();
		}
		return testNum(pBomLine,parent_Item_Num)*number1;
	}
	public boolean getALLBOMLine(HSSFWorkbook wb, HSSFSheet sheet,
			int bomStartRow, boolean isSheet) throws Exception {

		JPanel fd = ((AbstractAIFUIApplication) this.app).getApplicationPanel();
		allTargets = ((InterfaceExcelExportable) fd)
				.getComponentsToExport(ExcelExportOption.AllinView);
		int index = 0;
		List<List<String>> item_infos = new ArrayList<List<String>>();
		for (int i = 0; i < allTargets.length; i++) {
			System.out.println("已完成："+i+"/"+allTargets.length);
			TCComponentBOMLine bomLine = (TCComponentBOMLine) allTargets[i];
			TCComponentItemRevision itemRevision = bomLine.getItemRevision();
			if (itemRevision == null) {
				continue;
			}
			AIFComponentContext[] revForms = itemRevision
					.getChildren("IMAN_master_form_rev");
			String item_id = bomLine.getProperty("bl_item_item_id");
			String bom_ItemRev= bomLine.getProperty("bl_rev_item_revision_id");
			String itemType = itemRevision.getProperty("object_type");
			String num = bomLine.getProperty("bl_quantity") == "" ? "1.0"
					: bomLine.getProperty("bl_quantity");
			boolean isRepeat = false;
			if (!(("A2AYTL_KHLJRevision".equals(itemType)
					|| "零组件版本".equals(itemType) || ("A2BYTL_BZJZLJRevision"
						.equals(itemType))))) {

				/**
				 * 控制sheet1不显示组件和总装配
				 */
				List<String> item_info = new ArrayList<String>();
				if (!isSheet) {
					if ("A2YTL_ZZBCPRevision".equals(itemType)|| "A2YTL_CPBMRevision".equals(itemType)) {
						continue;
					} else {
						/*
						 * 根据父项的数量，来确定子项的数量进行相乘，并且在导出的时候判断不在同一父项下相同零件进行相加
						 */
//						TCComponentBOMLine parentBOMLine = bomLine.getCachedParent();
//						if(parentBOMLine==null){
//							continue;
//						}
//						double parent_Item_Num = Double.parseDouble(parentBOMLine.getProperty("bl_quantity") == "" ? "1.0"
//								: parentBOMLine.getProperty("bl_quantity"));
//						if(parent_Item_Num > 1){
//							num = Double.parseDouble(num)*parent_Item_Num+"";
//						}
						num=testNum(bomLine, Double.parseDouble(num))+"";
						for (int j = 0; j < item_infos.size(); j++) {
							if ((item_id+"."+bom_ItemRev).equals(item_infos.get(j).get(1))) {
								double item_num = Double.parseDouble(item_infos
										.get(j).get(2))
										+ Double.parseDouble(num);
								item_infos.get(j).set(2, item_num + "");
								sheet.getRow(
										Integer.parseInt(item_infos.get(j).get(
												0))).getCell(8)
										.setCellValue(item_infos.get(j).get(2));
								isRepeat = true;
							}
						}
						if (isRepeat) {
							continue;
						} else {
							item_info.add((bomStartRow + index+1) + "");
							item_info.add(item_id+"."+bom_ItemRev);
							item_info.add(num);
							item_infos.add(item_info);
						}

					}

				}
				Row row = sheet.createRow(bomStartRow + index+1);
				row = createCell(row);
				// if(i-1>=0){
				// int parentNum =
				// Integer.parseInt(bomLine.getCachedParent().getProperty("新建属性")
				// == "" ? "1": bomLine.getCachedParent().getProperty("新建属性"));
				// TCProperty tcProperty = bomLine.getTCProperty("新建属性");
				// tcProperty.setStringValueData(parentNum*num+"");
				// bomLine.setTCProperty(tcProperty);
				// num = Integer.parseInt(bomLine.getProperty("新建属性") == "" ?
				// "1": bomLine.getProperty("新建属性"));
				// }
				row.getCell(0).setCellValue(index + 1);
				row.getCell(1).setCellValue(
						bomLine.getProperty("bl_level_starting_0"));
				String item_type = bomLine.getProperty("bl_item_object_type");
				row.getCell(2).setCellValue(
						"EDA组件".equals(item_type) ? "电子类" : item_type);
				String rev_Release_Status = bomLine.getProperty("bl_rev_release_status_list");
				if(item_type.contains("KCL")|| item_type.contains("库存") || item_type.contains("成品编码") ||item_type.contains("CPBM")){
					row.getCell(3).setCellValue(item_id);
				}else{
					row.getCell(3).setCellValue(item_id+"."+bom_ItemRev);
				}

				row.getCell(4).setCellValue(
						bomLine.getProperty("bl_item_object_name"));
//				// 创建合并
//				sheet.addMergedRegion(new CellRangeAddress(bomStartRow + index,
//						bomStartRow + index, 5, 7));

				Object spe_Des = "";
				String brand = "";
				String model = "";
				String luoYa = "";
				String length = "";
				String changdu = "";
				Object material = "";
				Object biaoMian = "";
				Object reChuLi = "";
				Object pingPai = "";
				Object miaoShu = "";
				// 获得分类信息
				Map<String, String> map = itemRevision
						.getClassificationAttributes();
				TCComponentICO[] tcico = itemRevision
						.getClassificationObjects();
				if (tcico.length != 0) {
					ICSPropertyDescription[] icsPropertyDescriptions = tcico[0]
							.getICSPropertyDescriptors();
					for (int j = 0; j < icsPropertyDescriptions.length; j++) {
						if(icsPropertyDescriptions[j].isHidden()){	//判断此属性是否为隐藏状态，如果隐藏则跳过
							continue;
						}
						String name = (icsPropertyDescriptions[j]).getName();
						String unit = Util.getUnit((icsPropertyDescriptions[j])
								.getUnit());
						ICSFormat icsFormat = icsPropertyDescriptions[j]
								.getFormat();
						Object icsValue = "";
						String brand1 = "";
						if (icsFormat.getType() == -1) {
							brand1 = Util.getNotNull(icsFormat.getKeyLov()
									.getValueOfKey(map.get(name)));
							icsValue = Util.replaceValue(brand1);
						} else {
							brand1 = Util.getNotNull(map.get(name));
							icsValue = Util.formtString(Util
									.replaceValue(brand1));
						}
						Object value = "".equals(icsValue) ? "" : icsValue
								+ unit;
						if ("品牌".equals(name)) {
							if (brand1.contains("SEA&LAND")) {
								brand += "SEA&LAND";
							} else {
								brand += value; //
							}
						} else if ("型号".equals(name)||"图号".equals(name)) {
							model += value;
						} else if ("螺牙".equals(name)) {
							luoYa += value;
						} else if ("长度".equals(name)) {
							 length+= value;
							 changdu=name;
						} else {
							if (!"".equals(value) && !BOM_TYPE.contains(name)) {
								if (!DELETE_PRONAME.contains(name)) {
									spe_Des += name  + value + ",";
								} else {
									spe_Des +=  value + ",";
								}
							}
						}
					}
				}
				// 机加件类通用属性
				TCComponentForm form = (TCComponentForm) revForms[0].getComponent();
				String beizhuPro = bomLine.getProperty("A2BZ"); // 工程师备注
				if ("A2YTL_JJJLRevision".equals(itemType)||"A2YTL_JJBZJRevision".equals(itemType)||"A2YTL_ZZBCPRevision".equals(itemType)) {
                    TCProperty materialPro = itemRevision.getTCProperty("a2CL"); // 材料
                    TCProperty biaoMianPro = itemRevision.getTCProperty("a2BMCL"); // 表面处理
                    TCProperty reChuLiPro = itemRevision.getTCProperty("a2RCL"); // 热处理
                    TCProperty pingpaiPro = itemRevision.getTCProperty("a2PP"); // 品牌	
					TCProperty miaoshuPro = itemRevision.getTCProperty("a2MS"); // 描述
					
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
							spe_Des += pingPai + ",";
						}
					}
					if (miaoshuPro != null) {
						miaoShu = miaoshuPro.getStringValue();
						if (!"".equals(miaoShu)) {
							spe_Des += miaoShu + ",";
						}
					}
				}
				String spe_Des_Re = "".equals(length) ? length : ("长度" + length+",");
                String spe_Des_Re1 = "".equals(length) ? length : ("*" + length+",");
				if(itemType.contains("DZL")||itemType.contains("DQL")||itemType.contains("QDL")||itemType.contains("JGL")||itemType.contains("BaoCai")||itemType.contains("FZL")|| itemType.contains("电子类") || itemType.contains("电气类") || itemType.contains("气动类") || itemType.contains("机构类") || itemType.contains("包材类") ){
					if(itemRevision.getProperty("object_string").contains("螺丝")){
						row.getCell(5).setCellValue(Util.deleteProName(Util.trimValue(spe_Des+luoYa +spe_Des_Re1)));
					}else{
						row.getCell(5).setCellValue(Util.deleteProName(Util.trimValue(spe_Des+luoYa + spe_Des_Re)));
					}
				}else{
					row.getCell(5).setCellValue(Util.deleteProName(Util.trimValue(spe_Des+luoYa +spe_Des_Re))+itemRevision.getProperty("object_desc"));
				}
				row.getCell(6).setCellValue(brand+pingPai);// 品牌
//				sheet.addMergedRegion(new CellRangeAddress(bomStartRow + index,
//						bomStartRow + index, 9, 10));
				row.getCell(7).setCellValue(model);// 型号
				row.getCell(8).setCellValue(num);
				String BOMUnit = bomLine.getProperty("bl_uom");
				row.getCell(9).setCellValue(
						("每个".equals(BOMUnit) ? "PCS" : BOMUnit));// 单位
				row.getCell(10).setCellValue("");// 元件位置
				row.getCell(11).setCellValue(beizhuPro);
				// price
				TCProperty pricePro = form.getFormTCProperty("a2price");
				double price = 0.0;
				if (pricePro != null) {
					price = pricePro.getDoubleValue();
				}
				row.getCell(12).setCellValue(price);

				// 半成品表格添加黄色
				// if (itemType.contains("BCP")) {
				// // 设置BOM样式
				// HSSFCellStyle cellStyle2 = wb.createCellStyle();
				// cellStyle2.setFillPattern(HSSFCellStyle.FINE_DOTS);
				// cellStyle2.setFillForegroundColor(new HSSFColor.YELLOW()
				// .getIndex());
				// cellStyle2.setFillBackgroundColor(new HSSFColor.YELLOW()
				// .getIndex());
				// row.getCell(0).setCellStyle(cellStyle2);
				// row.getCell(1).setCellStyle(cellStyle2);
				// row.getCell(2).setCellStyle(cellStyle2);
				// row.getCell(3).setCellStyle(cellStyle2);
				// row.getCell(4).setCellStyle(cellStyle2);
				// row.getCell(5).setCellStyle(cellStyle2);
				// row.getCell(8).setCellStyle(cellStyle2);
				// row.getCell(9).setCellStyle(cellStyle2);
				// row.getCell(11).setCellStyle(cellStyle2);
				// row.getCell(12).setCellStyle(cellStyle2);
				// row.getCell(13).setCellStyle(cellStyle2);
				// row.getCell(14).setCellStyle(cellStyle2);
				// }
				if (itemType.contains("ZZBCP")) {
					HSSFCellStyle cellStyle3 = wb.createCellStyle();
					cellStyle3.setFillPattern(HSSFCellStyle.FINE_DOTS);
					cellStyle3.setFillForegroundColor(new HSSFColor.LIME()
							.getIndex());
					cellStyle3.setFillBackgroundColor(new HSSFColor.LIME()
							.getIndex());
					row.getCell(0).setCellStyle(cellStyle3);
					row.getCell(1).setCellStyle(cellStyle3);
					row.getCell(2).setCellStyle(cellStyle3);
					row.getCell(3).setCellStyle(cellStyle3);
					row.getCell(4).setCellStyle(cellStyle3);
					row.getCell(5).setCellStyle(cellStyle3);
					row.getCell(6).setCellStyle(cellStyle3);
					row.getCell(7).setCellStyle(cellStyle3);
					row.getCell(8).setCellStyle(cellStyle3);
					row.getCell(9).setCellStyle(cellStyle3);
					row.getCell(10).setCellStyle(cellStyle3);
					row.getCell(11).setCellStyle(cellStyle3);
					row.getCell(12).setCellStyle(cellStyle3);
				}

				if ("废弃".equals(rev_Release_Status)) {
					HSSFCellStyle cellStyle = wb.createCellStyle();
					Font font = wb.createFont();
					font.setColor(HSSFColor.RED.index); // 红字
					cellStyle.setFont(font);
					row.getCell(0).setCellStyle(cellStyle);
					row.getCell(1).setCellStyle(cellStyle);
					row.getCell(2).setCellStyle(cellStyle);
					row.getCell(3).setCellStyle(cellStyle);
					row.getCell(4).setCellStyle(cellStyle);
					row.getCell(5).setCellStyle(cellStyle);
					row.getCell(6).setCellStyle(cellStyle);
					row.getCell(7).setCellStyle(cellStyle);
					row.getCell(8).setCellStyle(cellStyle);
					row.getCell(9).setCellStyle(cellStyle);
					row.getCell(10).setCellStyle(cellStyle);
					row.getCell(11).setCellStyle(cellStyle);
					row.getCell(12).setCellStyle(cellStyle);
				}
				groupTargets.add(bomLine);
				index += 1;
			}
		}
		return true;
	}

	public void groupRow(HSSFSheet sheet, int bomStartRow) throws Exception {
		TreeSet<String> set = new TreeSet<String>();// 去重复使用TreeSet
		for (int i = 1; i < groupTargets.size(); i++) {
			TCComponentBOMLine bomLine = (TCComponentBOMLine) groupTargets
					.get(i);
			set.add(bomLine.getProperty("bl_level_starting_0"));
		}
		for (String string : set) {
			for (int i = 1; i < groupTargets.size(); i++) {
				TCComponentBOMLine bomLine = (TCComponentBOMLine) groupTargets
						.get(i);
				int level = Integer.parseInt(bomLine
						.getProperty("bl_level_starting_0"));
				if (level >= Integer.parseInt(string)) {
					int rowNum = Integer.parseInt(bomStartRow + i + "");
					sheet.groupRow(rowNum, rowNum);
				}
			}
		}
	}

	public static class EntryComparator implements
			Comparator<Map.Entry<String, Integer>> {// value列表顺序的比较器

		public int compare(Map.Entry<String, Integer> map1,
				Map.Entry<String, Integer> map2) {// 重写compare方法
			return map1.getValue() - map2.getValue(); // 升序排列
			// return map2.getValue() - map1.getValue();// 降序排列
		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void insertRow(HSSFWorkbook wb,HSSFSheet sheet, Map localHashMap) throws IOException {
		try {

			String targetBOMRevision_id = rootBomLine
					.getProperty("bl_rev_item_revision_id");
			int targetBOMRevision_id_int = 0;
			if (!targetBOMRevision_id.isEmpty()) {
				targetBOMRevision_id_int = Integer
						.parseInt(targetBOMRevision_id);
			}
			sheet.shiftRows(CHANGE_START_ROW + 1, sheet.getLastRowNum(),
					targetBOMRevision_id_int, true, false);
			for (int i = 0; i < localHashMap.size(); i++) {
				Row sourceRow = null;
				Row targetRow = null;
				TCComponentItemRevision itemRevision = (TCComponentItemRevision) localHashMap
						.keySet().toArray()[i];
				String revisionId = itemRevision
						.getProperty("item_revision_id");
				int revisionId_int = 0;
				if (!revisionId.isEmpty()) {
					revisionId_int = Integer.parseInt(revisionId);
				}
				if (targetBOMRevision_id_int < revisionId_int) {
					continue;
				}
				String changeDesc = "";
				String creation_date = "";
				String owning_user = "";
				String owning_group = "";
				TCComponentItemRevision ir = null;
				String jibie="";
				// List<TCComponentChangeItemRevision> ecnList =
				// (ArrayList<TCComponentChangeItemRevision>)
				// localHashMap.get(itemRevision);
				List<TCComponentChangeItemRevision> ecnList = (List<TCComponentChangeItemRevision>) localHashMap
						.get(itemRevision);
				// Collections.sort(ecnList);

				// ArrayList.sort(ecnList);
				// System.out.println(ecnList);
				if (ecnList.size() != 0) {
					for (TCComponentChangeItemRevision changeItemRevision : ecnList) {
						for (TCComponent tcComponent : changeItemRevision
								.getRelatedComponents("CMHasProblemItem"))
							changeDesc = "问题零组件："
									+ tcComponent.getProperty("object_string")
									+ ",";
						creation_date = changeItemRevision
								.getProperty("creation_date");
						owning_user = itemRevision.getProperty("owning_user");
						owning_group = itemRevision.getProperty("owning_group");
					}
				} else if (!revisionId.isEmpty() && !revisionId.equals("01")
						&& !revisionId.equals("001")) {
					changeDesc = itemRevision.getProperty("object_desc");
					creation_date = itemRevision.getProperty("creation_date");
					owning_user = itemRevision.getProperty("owning_user");
					owning_group = itemRevision.getProperty("owning_group");
				} else {
					//获取半成品属性作为变更记录
					if (changeDesc.isEmpty() || changeDesc.equals("")) {
					    changeDesc = "第一次下发";
					    creation_date = itemRevision.getProperty("creation_date");
					    owning_user = itemRevision.getProperty("owning_user");
					    owning_group = itemRevision.getProperty("owning_group");
					}
				}

				int j = 0;
				if (!revisionId.isEmpty()) {
					j = Integer.parseInt(revisionId);
				}
				sourceRow = sheet.getRow(CHANGE_START_ROW);
				targetRow = sheet.createRow(CHANGE_START_ROW + j);
				targetRow.setHeight(sourceRow.getHeight());
				targetRow = createCell(targetRow);
				sheet.addMergedRegion(new CellRangeAddress(
						CHANGE_START_ROW + j, CHANGE_START_ROW + j, 0, 1));
				sheet.addMergedRegion(new CellRangeAddress(
						CHANGE_START_ROW + j, CHANGE_START_ROW + j, 2, 7));
				sheet.addMergedRegion(new CellRangeAddress(
						CHANGE_START_ROW + j, CHANGE_START_ROW + j, 8, 9));
				sheet.addMergedRegion(new CellRangeAddress(
						CHANGE_START_ROW + j, CHANGE_START_ROW + j, 10, 12));
				HSSFCellStyle cellStyle=wb.createCellStyle();
				cellStyle.setWrapText(true); //换行
				targetRow.getCell(2).setCellStyle(cellStyle);
				targetRow.getCell(0).setCellValue(revisionId);
				targetRow.getCell(2).setCellValue(changeDesc);
				targetRow.getCell(8).setCellValue(creation_date);
				targetRow.getCell(10).setCellValue(owning_user+"--"+owning_group);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map getChanges(TCComponentBOMLine parentLine) {
		Map localHashMap = new HashMap();
		ExpandGRMRelationsResponse2 localExpandGRMRelationsResponse2 = null;
		try {
			TCComponent[] tcComponents = parentLine.getItem()
					.getReferenceListProperty("revision_list");
			RelationAndTypesFilter localRelationAndTypesFilter = new RelationAndTypesFilter();
			localRelationAndTypesFilter.relationTypeName = "CMHasSolutionItem";
			ExpandGRMRelationsPref2 localExpandGRMRelationsPref2 = new ExpandGRMRelationsPref2();
			localExpandGRMRelationsPref2.expItemRev = false;
			localExpandGRMRelationsPref2.returnRelations = false;
			localExpandGRMRelationsPref2.info = new RelationAndTypesFilter[] { localRelationAndTypesFilter };
			DataManagementService localDataManagementService = DataManagementService
					.getService(session);
			localExpandGRMRelationsResponse2 = localDataManagementService
					.expandGRMRelationsForSecondary(tcComponents,
							localExpandGRMRelationsPref2);
			for (ExpandGRMRelationsOutput2 out2 : localExpandGRMRelationsResponse2.output) {
				List localArrayList = new ArrayList();
				if (out2.relationshipData.length != 0)
					for (ExpandGRMRelationship localExpandGRMRelationship : out2.relationshipData[0].relationshipObjects) {
						if (((TCComponentItemRevision) localExpandGRMRelationship.otherSideObject)
								.getItem().getLatestItemRevision() != localExpandGRMRelationship.otherSideObject)
							continue;
						// 获得问题零组件
						// localExpandGRMRelationship.otherSideObject.getRelatedComponents("CMHasProblemItem");
						// localArrayList.add(localExpandGRMRelationship);
						localArrayList
								.add(localExpandGRMRelationship.otherSideObject);
						if (localExpandGRMRelationship.relation == null)
							continue;
						localExpandGRMRelationship.relation.refresh();
					}
				localHashMap.put(out2.inputObject, localArrayList);
			}

		} catch (TCException e) {
			e.printStackTrace();
		}
		return localHashMap;
	}

	public Row createCell(Row row) {
		for (int i = 0; i <= EXCEL_TOTAL_CELL; i++) {
			row.createCell(i);
		}
		return row;
	}
}
