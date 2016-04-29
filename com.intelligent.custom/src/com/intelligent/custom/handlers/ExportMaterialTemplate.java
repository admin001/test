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
 * ����ģ��
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
    private InterfaceAIFComponent[] allTargets; // �������չ����Component
    private List < InterfaceAIFComponent > groupTargets;
    private AbstractAIFApplication app;
    private AIFDesktop deskTop;
    private TCSession session;
    private static final String BOM_TYPE = "Pricing+Library Path+Library Ref+Footprint Path+������+Footprint Ref+ComponentLink1Description+ComponentLink1URL";
    private static final String DELETE_PRONAME = "�����װ����������Ŀ�����������Ͳ��ϱ�עIA�ͺŽӿ����������ɫ���ӽǶȿ��/����" 
			+ "�Ƿ�����������Ƕ����������Ƿ����ĸ/ͨ���Ƿ�����/�Ƿ������״/��ɫ�Ƿ��Ƿ��������������Ƿ��ɲ���Ƿ���������Ƿ�����" 
			+ "��װ/����Ƿ�������о���Ƿ��������������ߴ繦����������Ƿ���Ʊ��洦��������"; // ��Ӻ��ʾ��������������������
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
                    jfc.setDialogTitle("ѡ�񵼳��ļ���·��");
                    jfc.setApproveButtonText("����");
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
                        ProgressBar.show((Frame) null, thread, "���ڵ���...PDM����ģ��...,���Ժ򡭡�", "PDM����ģ�嵼���ɹ�", "����ʧ�ܣ����������Ƿ���ȷ��");
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
			 * ����excel
			 */
            HSSFWorkbook wb = new HSSFWorkbook(this.getClass().getResourceAsStream(modelFilePathName));
            /**
			 * ��õ�һ���ļ��еĵ�һ����
			 */
            HSSFSheet sheet = wb.getSheetAt(0);
            //			Row row = sheet.getRow(0);
            //			row.getCell(2).setCellValue(rootId); // ĸ������
            //			row.getCell(8).setCellValue(proName);
            //			HSSFSheet sheet1 = wb.getSheetAt(1);
            //			Row row1 = sheet1.getRow(3);
            //			row1.getCell(2).setCellValue(rootId); // ĸ������
            //			row1.getCell(8).setCellValue(proName);
            // �����¼
            //			Map localHashMap = getChanges(rootBomLine);
            //			insertRow(sheet, localHashMap);
            //			int bomStartRow = ITEM_START_ROW + localHashMap.size();
            int bomStartRow = 1;
            boolean writeExcel = getALLBOMLine(wb, sheet, bomStartRow);
            // ����
            if (writeExcel) groupRow(sheet, bomStartRow);
            FileOutputStream os = new FileOutputStream(filePath 
            		+ "\\"
					+ session.getUser().toString().split(" ")[0]
					+" "
					+ session.getGroup().toString().split(" ")[0]
					+" "
					+"����ģ��"
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
			JOptionPane.showMessageDialog(null, "�ļ�����ID���ܰ���б��",
					"����", JOptionPane.ERROR_MESSAGE);
			return false;
		} catch(Exception e) {
            if (e.toString().contains("FileNotFoundException")) {
                JOptionPane.showMessageDialog(null, "ϵͳ�Ҳ���ָ����·��,����ӹ����̣�", "����", JOptionPane.ERROR_MESSAGE);
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
		//�洢����״̬��Ϣ��־
		StringBuffer sb=new StringBuffer();
        for (int i = 0; i < allTargets.length; i++) {
            TCComponentBOMLine bomLine = (TCComponentBOMLine) allTargets[i];
            TCComponentItemRevision itemRevision = bomLine.getItemRevision();
            String itemType = itemRevision.getProperty("object_type");
            String creation_date = null;
            String datasetType="";
            String displayTypeName="";
            String rev_Release_Status = bomLine.getProperty("bl_rev_release_status_list"); // ��ȡ����״̬
			Object date_re = Util.getNotNull(itemRevision.getProperty("date_released"));// ��ȡ��������
            String beizhuPro = bomLine.getProperty("A2BZ"); // ����ʦ��ע
            // if(!(rev_Release_Status==null ||
            // rev_Release_Status.equals(""))){//�ж�״̬��Ϊ��||rev_Release_Status.equals("TCM �ѷ���")
            // System.out.println(" ### ����ߣ�"+itemRevision.getProperty("fnd0EventTypeName"));
            /**
             * �Ȼ�ýṹ����������Item��Ȼ����������ݲ���Ҳ���Ի������
             * ֻ�ǰ���ʤд�Ĵ�����ʽȥ����һ��û�в㼶��BOM���ͳ�������
             * �Ѱ��Ʒ����һ�£�����û���Ʒ����
             */
            String log="\r\n"+itemRevision.getProperty("item_id")+"�ķ���ʱ�䣺"+date_re+"----;����״̬Ϊ:"+rev_Release_Status;//
			sb.append(log);
            //������ʾ���Ϸ���
            if ("����".equals(rev_Release_Status)||itemType.contains("A2YTL_JJJL")) {
                continue;
            }
            if (! ("A2AYTL_KHLJRevision".equals(itemType) || "������汾".equals(itemType) || itemType.equals("A2BYTL_BZJZLJRevision"))) {
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
                // �����ϲ�
                //				sheet.addMergedRegion(new CellRangeAddress(bomStartRow + index, bomStartRow + index, 5, 7));
                //				sheet.addMergedRegion(new CellRangeAddress(bomStartRow + index, bomStartRow + index, 9, 10));
                String item_type = bomLine.getProperty("bl_item_object_type");
                String bom_ItemId = bomLine.getProperty("bl_item_item_id");//bomLine.getProperty("bl_rev_item_revision_id")
                String num = bomLine.getProperty("bl_quantity") == "" ? "1.0": bomLine.getProperty("bl_quantity");
//                String spe_Des_Re = "".equals(length) ? length: ("*" + length);
                String BOMUnit = bomLine.getProperty("bl_uom");
                //�ж��Ƿ���3D
                AIFComponentContext[] revDatasets = itemRevision.getChildren("IMAN_specification");
                if(revDatasets.length>0){
	                for(AIFComponentContext getDataset : revDatasets){
	                	if(getDataset.getComponent() instanceof TCComponentDataset){
		                	TCComponentDataset dataset = (TCComponentDataset)getDataset.getComponent();
		                	datasetType += dataset.getType();
		                	displayTypeName += dataset.getDisplayType();
		                	displayTypeName+=",";
		                	creation_date = dataset.getProperty("creation_date");
		                	if(dataset.getType().equals("SWAsm")||dataset.getType().equals("SWPrt")){//CAEAnalysisDS  STP�ļ�
			                	if3D = "��";
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
                // Integer.parseInt(bomLine.getCachedParent().getProperty("�½�����")
                // == "" ? "1": bomLine.getCachedParent().getProperty("�½�����"));
                // TCProperty tcProperty = bomLine.getTCProperty("�½�����");
                // tcProperty.setStringValueData(parentNum*num+"");
                // bomLine.setTCProperty(tcProperty);
                // num = Integer.parseInt(bomLine.getProperty("�½�����") == "" ?
                // "1": bomLine.getProperty("�½�����"));
                // }
                if (tcico.length != 0) {
                    ICSPropertyDescription[] icsPropertyDescriptions = tcico[0].getICSPropertyDescriptors();
                    for (int j = 0; j < icsPropertyDescriptions.length; j++) {
                    	if(icsPropertyDescriptions[j].isHidden()){	//�жϴ������Ƿ�Ϊ����״̬���������������
							continue;
						}
                        String name = (icsPropertyDescriptions[j]).getName(); // ��ȡ��������
                        String unit = Util.getUnit((icsPropertyDescriptions[j]).getUnit());
                        ICSFormat icsFormat = icsPropertyDescriptions[j].getFormat(); // ��ȡ��������
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
                        if ("Ʒ��".equals(name)) {
                            if (brand1.contains("SEA&LAND")) {
                                brand += "SEA&LAND";
                            } else {
                                brand += value; //
                            }
                        }else if ("���Ϻ�".equals(name)) {
                            JLH += value;
                        } else if ("�ͺ�".equals(name)) {
                        	model += value; //
                        } else if ("����".equals(name)) {
							luoYa += value;
						} else if ("����".equals(name)) {
							 length+= value;
							 changdu=name;
						} else if ("������".equals(name)) {
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
                // ���Ӽ���ͨ������
                if ("A2YTL_JJJLRevision".equals(itemType)||"A2YTL_JJBZJRevision".equals(itemType)||"A2YTL_ZZBCPRevision".equals(itemType)) {
                    TCProperty materialPro = itemRevision.getTCProperty("a2CL"); // ����
                    TCProperty biaoMianPro = itemRevision.getTCProperty("a2BMCL"); // ���洦��
                    TCProperty reChuLiPro = itemRevision.getTCProperty("a2RCL"); // �ȴ���
                    TCProperty pingpaiPro = itemRevision.getTCProperty("a2PP"); // Ʒ��	
					TCProperty miaoshuPro = itemRevision.getTCProperty("a2MS"); // ����
					
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

                // ���Ʒ�����ӻ�ɫ
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

                //				// �������������ɫ
                //				if ("����".equals(rev_Release_Status)) {
                //					// ����BOM��ʽ
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
                row.getCell(1).setCellValue("EDA���".equals(item_type) ? "������": item_type);
                if(item_type.contains("KCL")|| item_type.contains("���") || item_type.contains("��Ʒ����") ||item_type.contains("CPBM")){
                	row.getCell(2).setCellValue(bom_ItemId);
				}else{
					row.getCell(2).setCellValue(bom_ItemId+"."+bomLine.getProperty("bl_rev_item_revision_id"));
				}
                row.getCell(3).setCellValue(bomLine.getProperty("bl_rev_item_revision_id"));
                row.getCell(4).setCellValue(bomLine.getProperty("bl_item_object_name"));
                row.getCell(5).setCellValue(brand+pingPai); // Ʒ��
                row.getCell(6).setCellValue(model); // �ͺ�
                row.getCell(7).setCellValue(if3D);//�Ƿ���ͼֽ
                String spe_Des_Re = "".equals(length) ? length : ("����" + length+",");
                String spe_Des_Re1 = "".equals(length) ? length : ("*" + length+",");
                if(itemType.contains("DZL")||itemType.contains("DQL")||itemType.contains("QDL")||itemType.contains("JGL")||itemType.contains("BaoCai")||itemType.contains("FZL")|| itemType.contains("������") || itemType.contains("������") || itemType.contains("������") || itemType.contains("������") || itemType.contains("������") ){
					if(itemRevision.getProperty("object_string").contains("��˿")){
						row.getCell(8).setCellValue(Util.deleteProName(Util.trimValue(itemRevision.getProperty("object_desc")+spe_Des+luoYa +spe_Des_Re1))+beizhuPro);
					}else{
						row.getCell(8).setCellValue(Util.deleteProName(Util.trimValue(itemRevision.getProperty("object_desc")+spe_Des+luoYa +spe_Des_Re))+beizhuPro);
					}
				}else{
                	row.getCell(8).setCellValue(Util.deleteProName(Util.trimValue(itemRevision.getProperty("object_desc")+spe_Des+luoYa +spe_Des_Re))+beizhuPro);
                }
                row.getCell(9).setCellValue(JLH); // ������
                row.getCell(10).setCellValue(rev_Release_Status);
                if(if3D=="��"){
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
      //�������״̬��־
      Util.getOutStringInText("����ģ�� "+rootBomLine.getProperty("bl_item_item_id"), sb);
        return true;
    }

    public void groupRow(HSSFSheet sheet, int bomStartRow) throws Exception {
        TreeSet < String > set = new TreeSet < String > (); // ȥ�ظ�ʹ��TreeSet
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
                        for (TCComponent tcComponent: changeItemRevision.getRelatedComponents("CMHasProblemItem")) changeDesc = "�����������" + tcComponent.getProperty("object_string") + ",";
                        creation_date = changeItemRevision.getProperty("creation_date");
                        owning_user = itemRevision.getProperty("owning_user");
                    }
                } else {
                    changeDesc = "��һ���·�";
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
                    // ������������
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