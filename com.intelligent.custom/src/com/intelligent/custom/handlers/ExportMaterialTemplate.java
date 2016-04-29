package com.intelligent.custom.handlers;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentChangeItemRevision;
import com.teamcenter.rac.kernel.TCComponentDataset;
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
/**
 * 物料模板
 * @author infodba
 *
 */
public class ExportMaterialTemplate extends AbstractHandler {
    private File file;
    private TCComponentBOMLine rootBomLine;
    private final String DEFAULT_EXPORT_DIR = "D:\\ExportedBOM";
    private final String TEMPLATE_FILE = "/resource/ExportMaterialTemplate.xls";
    private final int CHANGE_START_ROW = 4;
    //	private final int ITEM_START_ROW = 0;
    private final int EXCEL_TOTAL_CELL = 14;
    private InterfaceAIFComponent[] allTargets; // 获得所有展开的Component
    private List < InterfaceAIFComponent > groupTargets;
    private AbstractAIFApplication app;
    private AIFDesktop deskTop;
    private TCSession session;
    private static final String BOM_TYPE = "Pricing+Library Path+Library Ref+Footprint Path+责任人+Footprint Ref+ComponentLink1Description+ComponentLink1URL";
    private static final String DELETE_PRONAME = "分类封装类型引脚数目功能描述类型材料备注IA型号接口类型外观颜色连接角度快断/慢断" 
			+ "是否带螺柱插座角度引脚类型是否带螺母/通孔是否自锁/是否带灯形状/颜色是否是否自锁叠层数量是否带刹车是否带蜂鸣器是否屏蔽" 
			+ "封装/外壳是否柔性线芯数是否带触发规格描述尺寸功率种类类别是否带灯表面处理工艺螺牙"; // 添加后表示导出不带分类属性名称
    public Object execute(ExecutionEvent arg0) throws ExecutionException {
        app = AIFUtility.getCurrentApplication();
        deskTop = AIFUtility.getCurrentApplication().getDesktop();
        session = (TCSession) this.app.getSession(); ((AbstractPSEApplication) app).expandBelow();
        groupTargets = new ArrayList < InterfaceAIFComponent > ();

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
                            String s = rootBomLine.getProperty("bl_item_item_id");

                            public void run() {
                                if (file != null) {
                                    insertExcel(TEMPLATE_FILE, file.getPath(), s);
                                } else {
                                    insertExcel(TEMPLATE_FILE, DEFAULT_EXPORT_DIR, s);
                                }
                            }
                        };
                        ProgressBar.show((Frame) null, thread, "正在导出...PDM物料模板...,请稍候……", "PDM物料模板导出成功", "导出失败，请检查数据是否正确！");
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return null;
    }

    public boolean insertExcel(String modelFilePathName, String filePath, String rootId) {
        String proName = "";
        String proID = "";
        try {
            proName = rootBomLine.getProperty("bl_item_object_name");
            proID = rootBomLine.getProperty("bl_item_item_id");
            if (proName.contains("/")) {
				throw new NumberFormatException();
			}
            /**
			 * 加载excel
			 */
            HSSFWorkbook wb = new HSSFWorkbook(this.getClass().getResourceAsStream(modelFilePathName));
            /**
			 * 获得第一个文件中的第一个表单
			 */
            HSSFSheet sheet = wb.getSheetAt(0);
            //			Row row = sheet.getRow(0);
            //			row.getCell(2).setCellValue(rootId); // 母件编码
            //			row.getCell(8).setCellValue(proName);
            //			HSSFSheet sheet1 = wb.getSheetAt(1);
            //			Row row1 = sheet1.getRow(3);
            //			row1.getCell(2).setCellValue(rootId); // 母件编码
            //			row1.getCell(8).setCellValue(proName);
            // 变更记录
            //			Map localHashMap = getChanges(rootBomLine);
            //			insertRow(sheet, localHashMap);
            //			int bomStartRow = ITEM_START_ROW + localHashMap.size();
            int bomStartRow = 1;
            boolean writeExcel = getALLBOMLine(wb, sheet, bomStartRow);
            // 分组
            if (writeExcel) groupRow(sheet, bomStartRow);
            FileOutputStream os = new FileOutputStream(filePath 
            		+ "\\"
					+ session.getUser().toString().split(" ")[0]
					+" "
					+ session.getGroup().toString().split(" ")[0]
					+" "
					+"物料模板"
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
		} catch(Exception e) {
            if (e.toString().contains("FileNotFoundException")) {
                JOptionPane.showMessageDialog(null, "系统找不到指定的路径,请添加公共盘！", "错误", JOptionPane.ERROR_MESSAGE);
            }
            e.printStackTrace();
        }
        return true;
    }

    @SuppressWarnings("unused")
	public boolean getALLBOMLine(HSSFWorkbook wb, HSSFSheet sheet, int bomStartRow) throws Exception {
        JPanel fd = ((AbstractAIFUIApplication) this.app).getApplicationPanel();
        allTargets = ((InterfaceExcelExportable) fd).getComponentsToExport(ExcelExportOption.AllinView);
        int index = 0;
		//存储发放状态信息日志
		StringBuffer sb=new StringBuffer();
        for (int i = 0; i < allTargets.length; i++) {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) allTargets[i];
            TCComponentItemRevision itemRevision = bomLine.getItemRevision();
            String itemType = itemRevision.getProperty("object_type");
            String creation_date = null;
            String datasetType="";
            String displayTypeName="";
            String rev_Release_Status = bomLine.getProperty("bl_rev_release_status_list"); // 获取发放状态
			Object date_re = Util.getNotNull(itemRevision.getProperty("date_released"));// 获取发放日期
            String beizhuPro = bomLine.getProperty("A2BZ"); // 工程师备注
            // if(!(rev_Release_Status==null ||
            // rev_Release_Status.equals(""))){//判断状态不为空||rev_Release_Status.equals("TCM 已发放")
            // System.out.println(" ### 审核者："+itemRevision.getProperty("fnd0EventTypeName"));
            /**
             * 先获得结构管理器最顶层的Item，然后下面的数据不就也可以获得了嘛
             * 只是把谟胜写的代码样式去掉，一个没有层级的BOM不就出来了嘛
             * 把半成品屏蔽一下，不就没半成品了嘛
             */
            String log="\r\n"+itemRevision.getProperty("item_id")+"的发放时间："+date_re+"----;发放状态为:"+rev_Release_Status;//
			sb.append(log);
            //导出提示物料废弃
            if ("废弃".equals(rev_Release_Status)||itemType.contains("A2YTL_JJJL")) {
                continue;
            }
            if (! ("A2AYTL_KHLJRevision".equals(itemType) || "零组件版本".equals(itemType) || itemType.equals("A2BYTL_BZJZLJRevision"))) {
                Row row = sheet.createRow(bomStartRow + index);
                row = createCell(row);

                Object spe_Des = "";
                String brand = "";
                String model = "";
                String luoYa = "";
                String length = "";
                String changdu = "";
                String JLH = "";
                String if3D = "";
                String zerenren = "";
                Object material = "";
				Object biaoMian = "";
				Object reChuLi = "";
				Object pingPai = "";
				Object miaoShu = "";
                // 创建合并
                //				sheet.addMergedRegion(new CellRangeAddress(bomStartRow + index, bomStartRow + index, 5, 7));
                //				sheet.addMergedRegion(new CellRangeAddress(bomStartRow + index, bomStartRow + index, 9, 10));
                String item_type = bomLine.getProperty("bl_item_object_type");
                String bom_ItemId = bomLine.getProperty("bl_item_item_id");//bomLine.getProperty("bl_rev_item_revision_id")
                String num = bomLine.getProperty("bl_quantity") == "" ? "1.0": bomLine.getProperty("bl_quantity");
//                String spe_Des_Re = "".equals(length) ? length: ("*" + length);
                String BOMUnit = bomLine.getProperty("bl_uom");
                //判断是否有3D
                AIFComponentContext[] revDatasets = itemRevision.getChildren("IMAN_specification");
                if(revDatasets.length>0){
	                for(AIFComponentContext getDataset : revDatasets){
	                	if(getDataset.getComponent() instanceof TCComponentDataset){
		                	TCComponentDataset dataset = (TCComponentDataset)getDataset.getComponent();
		                	datasetType += dataset.getType();
		                	displayTypeName += dataset.getDisplayType();
		                	displayTypeName+=",";
		                	creation_date = dataset.getProperty("creation_date");
		                	if(dataset.getType().equals("SWAsm")||dataset.getType().equals("SWPrt")){//CAEAnalysisDS  STP文件
			                	if3D = "是";
			                	if(dataset.getType().equals("CAEAnalysisDS")||dataset.getType().equals("SWDrw")||dataset.getType().equals("PDF")){
				                	if3D+="";
			                	}
			                	//datasetType+=displayTypeName;
		                	}
		                	
	                	}
	                }
                }
                
                
                Map < String,String > map = itemRevision.getClassificationAttributes();
                TCComponentICO[] tcico = itemRevision.getClassificationObjects();

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
                if (tcico.length != 0) {
                    ICSPropertyDescription[] icsPropertyDescriptions = tcico[0].getICSPropertyDescriptors();
                    for (int j = 0; j < icsPropertyDescriptions.length; j++) {
                    	if(icsPropertyDescriptions[j].isHidden()){	//判断此属性是否为隐藏状态，如果隐藏则跳过
							continue;
						}
                        String name = (icsPropertyDescriptions[j]).getName(); // 获取分类属性
                        String unit = Util.getUnit((icsPropertyDescriptions[j]).getUnit());
                        ICSFormat icsFormat = icsPropertyDescriptions[j].getFormat(); // 获取分类属性
                        Object icsValue = "";
                        String brand1 = "";
                        if (icsFormat.getType() == -1) {
                            brand1 = Util.getNotNull(icsFormat.getKeyLov().getValueOfKey(map.get(name)));
                            icsValue = Util.replaceValue(brand1);
                        } else {
                            brand1 = Util.getNotNull(map.get(name));
                            icsValue = Util.formtString(Util.replaceValue(brand1));
                        }
                        String value = "".equals(icsValue) ? "": icsValue + unit;
                        if ("品牌".equals(name)) {
                            if (brand1.contains("SEA&LAND")) {
                                brand += "SEA&LAND";
                            } else {
                                brand += value; //
                            }
                        }else if ("旧料号".equals(name)) {
                            JLH += value;
                        } else if ("型号".equals(name)) {
                        	model += value; //
                        } else if ("螺牙".equals(name)) {
							luoYa += value;
						} else if ("长度".equals(name)) {
							 length+= value;
							 changdu=name;
						} else if ("责任人".equals(name)) {
							zerenren = value;
                        }else {
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
                // 机加件类通用属性
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

                // 半成品表格添加黄色
                //                if (itemType.contains("BCP")) {
                //                    HSSFCellStyle cellStyle2 = wb.createCellStyle();
                //                    cellStyle2.setFillPattern(HSSFCellStyle.FINE_DOTS);
                //                    cellStyle2.setFillForegroundColor(new HSSFColor.YELLOW().getIndex());
                //                    cellStyle2.setFillBackgroundColor(new HSSFColor.YELLOW().getIndex());
                //                    row.getCell(0).setCellStyle(cellStyle2);
                //                    row.getCell(1).setCellStyle(cellStyle2);
                //                    row.getCell(2).setCellStyle(cellStyle2);
                //                    row.getCell(3).setCellStyle(cellStyle2);
                //                    row.getCell(4).setCellStyle(cellStyle2);
                //                    row.getCell(5).setCellStyle(cellStyle2);
                //                    row.getCell(8).setCellStyle(cellStyle2);
                //                    row.getCell(9).setCellStyle(cellStyle2);
                //                    row.getCell(11).setCellStyle(cellStyle2);
                //                    row.getCell(12).setCellStyle(cellStyle2);
                //                    row.getCell(13).setCellStyle(cellStyle2);
                //                    row.getCell(14).setCellStyle(cellStyle2);
                //                }
                if (itemType.contains("ZZBCP")) {
                    HSSFCellStyle cellStyle3 = wb.createCellStyle();
                    cellStyle3.setFillPattern(HSSFCellStyle.FINE_DOTS);
                    cellStyle3.setFillForegroundColor(new HSSFColor.LIME().getIndex());
                    cellStyle3.setFillBackgroundColor(new HSSFColor.LIME().getIndex());
                    cellStyle3.setBorderLeft((short) 1);
                    cellStyle3.setBorderRight((short) 1);
                    cellStyle3.setBorderBottom((short) 1);
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
                    row.getCell(13).setCellStyle(cellStyle3);

                }

                //				// 废弃物料字体红色
                //				if ("废弃".equals(rev_Release_Status)) {
                //					// 设置BOM样式
                //					HSSFCellStyle cellStyle = wb.createCellStyle();
                //					Font font = wb.createFont();
                //					font.setColor(HSSFColor.RED.index);
                //					cellStyle.setFont(font);
                //					row.getCell(0).setCellStyle(cellStyle);
                //					row.getCell(1).setCellStyle(cellStyle);
                //					row.getCell(2).setCellStyle(cellStyle);
                //					row.getCell(3).setCellStyle(cellStyle);
                //					row.getCell(4).setCellStyle(cellStyle);
                //					row.getCell(5).setCellStyle(cellStyle);
                //					row.getCell(6).setCellStyle(cellStyle);
                //					row.getCell(7).setCellStyle(cellStyle);
                //					row.getCell(8).setCellStyle(cellStyle);
                //				}
                
                row.getCell(0).setCellValue(index + 1);
                row.getCell(1).setCellValue("EDA组件".equals(item_type) ? "电子类": item_type);
                if(item_type.contains("KCL")|| item_type.contains("库存") || item_type.contains("成品编码") ||item_type.contains("CPBM")){
                	row.getCell(2).setCellValue(bom_ItemId);
				}else{
					row.getCell(2).setCellValue(bom_ItemId+"."+bomLine.getProperty("bl_rev_item_revision_id"));
				}
                row.getCell(3).setCellValue(bomLine.getProperty("bl_rev_item_revision_id"));
                row.getCell(4).setCellValue(bomLine.getProperty("bl_item_object_name"));
                row.getCell(5).setCellValue(brand+pingPai); // 品牌
                row.getCell(6).setCellValue(model); // 型号
                row.getCell(7).setCellValue(if3D);//是否有图纸
                String spe_Des_Re = "".equals(length) ? length : ("长度" + length+",");
                String spe_Des_Re1 = "".equals(length) ? length : ("*" + length+",");
                if(itemType.contains("DZL")||itemType.contains("DQL")||itemType.contains("QDL")||itemType.contains("JGL")||itemType.contains("BaoCai")||itemType.contains("FZL")|| itemType.contains("电子类") || itemType.contains("电气类") || itemType.contains("气动类") || itemType.contains("机构类") || itemType.contains("包材类") ){
					if(itemRevision.getProperty("object_string").contains("螺丝")){
						row.getCell(8).setCellValue(Util.deleteProName(Util.trimValue(itemRevision.getProperty("object_desc")+spe_Des+luoYa +spe_Des_Re1))+beizhuPro);
					}else{
						row.getCell(8).setCellValue(Util.deleteProName(Util.trimValue(itemRevision.getProperty("object_desc")+spe_Des+luoYa +spe_Des_Re))+beizhuPro);
					}
				}else{
                	row.getCell(8).setCellValue(Util.deleteProName(Util.trimValue(itemRevision.getProperty("object_desc")+spe_Des+luoYa +spe_Des_Re))+beizhuPro);
                }
                row.getCell(9).setCellValue(JLH); // 助记码
                row.getCell(10).setCellValue(rev_Release_Status);
                if(if3D=="是"){
                	row.getCell(11).setCellValue(creation_date);
                }
                row.getCell(12).setCellValue(displayTypeName);
                String unit=bomLine.getItem().getProperty("uom_tag");;
                if(!bomLine.isRoot()){
                	unit=bomLine.getCachedParent().getItem().getProperty("uom_tag");
                }
                row.getCell(13).setCellValue(unit);//uom_tag  items_tag 
                row.getCell(14).setCellValue(zerenren);//uom_tag  items_tag 
           
                groupTargets.add(bomLine);
                index += 1;
            }
        }
      //输出发放状态日志
      Util.getOutStringInText("物料模版 "+rootBomLine.getProperty("bl_item_item_id"), sb);
        return true;
    }

    public void groupRow(HSSFSheet sheet, int bomStartRow) throws Exception {
        TreeSet < String > set = new TreeSet < String > (); // 去重复使用TreeSet
        for (int i = 1; i < groupTargets.size(); i++) {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) groupTargets.get(i);
            set.add(bomLine.getProperty("bl_level_starting_0"));
        }
        for (String string: set) {
            for (int i = 1; i < groupTargets.size(); i++) {
                TCComponentBOMLine bomLine = (TCComponentBOMLine) groupTargets.get(i);
                int level = Integer.parseInt(bomLine.getProperty("bl_level_starting_0"));
                if (level >= Integer.parseInt(string)) {
                    int rowNum = Integer.parseInt(bomStartRow + i + "");
                    sheet.groupRow(rowNum, rowNum);
                }
            }
        }
    }
/*
    public void insertRow(HSSFSheet sheet, Map localHashMap) throws IOException {
        try {
            sheet.shiftRows(CHANGE_START_ROW + 1, sheet.getLastRowNum(), localHashMap.size(), true, false);
            for (int i = 0; i < localHashMap.size(); i++) {
                Row sourceRow = null;
                Row targetRow = null;
                sourceRow = sheet.getRow(CHANGE_START_ROW);
                targetRow = sheet.createRow(CHANGE_START_ROW + 1 + i);
                targetRow.setHeight(sourceRow.getHeight());
                TCComponentItemRevision itemRevision = (TCComponentItemRevision) localHashMap.keySet().toArray()[i];
                String revisionId = itemRevision.getProperty("item_revision_id");
                String changeDesc = "";
                String creation_date = "";
                String owning_user = "";
                List < TCComponentChangeItemRevision > ecnList = (List < TCComponentChangeItemRevision > ) localHashMap.get(itemRevision);

                if (ecnList.size() != 0) {
                    for (TCComponentChangeItemRevision changeItemRevision: ecnList) {
                        for (TCComponent tcComponent: changeItemRevision.getRelatedComponents("CMHasProblemItem")) changeDesc = "问题零组件：" + tcComponent.getProperty("object_string") + ",";
                        creation_date = changeItemRevision.getProperty("creation_date");
                        owning_user = itemRevision.getProperty("owning_user");
                    }
                } else {
                    changeDesc = "第一次下发";
                    creation_date = itemRevision.getProperty("creation_date");
                    owning_user = itemRevision.getProperty("owning_user");
                }
                				targetRow = createCell(targetRow);
                				sheet.addMergedRegion(new CellRangeAddress(CHANGE_START_ROW + 1 + i, CHANGE_START_ROW + 1 + i, 0, 1));
                				sheet.addMergedRegion(new CellRangeAddress(CHANGE_START_ROW + 1 + i, CHANGE_START_ROW + 1 + i, 2, 8));
                				sheet.addMergedRegion(new CellRangeAddress(CHANGE_START_ROW + 1 + i, CHANGE_START_ROW + 1 + i, 9, 12));
                				sheet.addMergedRegion(new CellRangeAddress(CHANGE_START_ROW + 1 + i, CHANGE_START_ROW + 1 + i, 13, 14));
                				targetRow.getCell(0).setCellValue(revisionId);
                				targetRow.getCell(2).setCellValue(changeDesc);
                				targetRow.getCell(9).setCellValue(creation_date);
                				targetRow.getCell(13).setCellValue(owning_user);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
*/
    public Map getChanges(TCComponentBOMLine parentLine) {
        Map localHashMap = new HashMap();
        ExpandGRMRelationsResponse2 localExpandGRMRelationsResponse2 = null;
        try {
            TCComponent[] tcComponents = parentLine.getItem().getReferenceListProperty("revision_list");
            RelationAndTypesFilter localRelationAndTypesFilter = new RelationAndTypesFilter();
            localRelationAndTypesFilter.relationTypeName = "CMHasSolutionItem";
            ExpandGRMRelationsPref2 localExpandGRMRelationsPref2 = new ExpandGRMRelationsPref2();
            localExpandGRMRelationsPref2.expItemRev = false;
            localExpandGRMRelationsPref2.returnRelations = false;
            localExpandGRMRelationsPref2.info = new RelationAndTypesFilter[] {
                localRelationAndTypesFilter
            };
            DataManagementService localDataManagementService = DataManagementService.getService(session);
            localExpandGRMRelationsResponse2 = localDataManagementService.expandGRMRelationsForSecondary(tcComponents, localExpandGRMRelationsPref2);

            for (ExpandGRMRelationsOutput2 out2: localExpandGRMRelationsResponse2.output) {
                List localArrayList = new ArrayList();
                if (out2.relationshipData.length != 0) for (ExpandGRMRelationship localExpandGRMRelationship: out2.relationshipData[0].relationshipObjects) {
                    if (((TCComponentItemRevision) localExpandGRMRelationship.otherSideObject).getItem().getLatestItemRevision() != localExpandGRMRelationship.otherSideObject) continue;
                    // 获得问题零组件
                    // localExpandGRMRelationship.otherSideObject.getRelatedComponents("CMHasProblemItem");
                    // localArrayList.add(localExpandGRMRelationship);
                    localArrayList.add(localExpandGRMRelationship.otherSideObject);
                    if (localExpandGRMRelationship.relation == null) continue;
                    localExpandGRMRelationship.relation.refresh();
                }
                localHashMap.put(out2.inputObject, localArrayList);
            }

        } catch(TCException e) {
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