package com.intelligent.custom.handlers;

import java.awt.Frame;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JFileChooser;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.intelligent.custom.util.AccessUtil;
import com.intelligent.custom.util.ConstantClass;
import com.intelligent.custom.util.ProgressBar;
import com.intelligent.custom.util.Util;
import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCClassificationService;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentICO;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.kernel.ics.ICSApplicationObject;
import com.teamcenter.rac.kernel.ics.ICSBaseObject;
import com.teamcenter.rac.kernel.ics.ICSFormat;
import com.teamcenter.rac.kernel.ics.ICSHierarchyNodeDescriptor;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.kernel.ics.ICSView;

public class UpdateCAD extends AbstractHandler {
	private File file;
	private ICSApplicationObject icsapplicationobject = null;
	private TCClassificationService icsService;
	private ICSApplicationObject icsapplicationobjects = null;
	private List<TCComponent> list;
	private AccessUtil au ;
	private AbstractAIFApplication app;
	private AIFDesktop deskTop;
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		app = AIFUtility.getCurrentApplication();
		deskTop = AIFUtility.getCurrentApplication().getDesktop();
		TCSession tcSession = (TCSession) this.app.getSession();
		icsService = tcSession.getClassificationService();
		list = new ArrayList<TCComponent>();
		au = AccessUtil.getInstance();
		new Thread() {
			public void run() {
				try {
					JFileChooser jfc = new JFileChooser();
					jfc.setDialogTitle("ѡ�񵼳��ļ���·��");
					jfc.setApproveButtonText("����");
					jfc.setMultiSelectionEnabled(true);
					jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnvalue = jfc.showOpenDialog(deskTop);
					icsapplicationobject = icsService.newICSApplicationObject("ICM");
					if (returnvalue == 0) {
						file = jfc.getSelectedFile();
						Thread thread = new Thread() {
							public void run() {
								if (file != null) {
									au.setSavedFilePathAndName(file.getPath()+"\\data.mdb");
									insert();
								} else {
									insert();
								}
							}
						};
						ProgressBar.show((Frame) null, thread, "���ڵ���AD,���Ժ򡭡�",
								"�����ɹ�", "����ʧ�ܣ����������Ƿ���ȷ��");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();
		return null;
	}

	public void insert() {
		try {
			getAccess("ICM01");
			au.deleteOldMdbFile();
			au.copyBlankMdbFile();
			au.connetAccessDB();
			//�������е�ʵ��������ݿ�
			for (int i = 0; i < list.size(); i++) {
				// ��÷�����Ϣ
				String potting = "";     // ��װ
				String spe_Des = "";
				String value = "";
				String tolerance = "";
				String ratedVoltage = "";
				String libraryPath = "";
				String libraryRef= "";
				String footprintPath= "";
				String footprintRef	  = "";
				String supplier  = "";        //��Ӧ��
				String supplierPartNumber  = "";  //�ͺ�
				String pricing	    = "";
				String componentLink1Description	= "";
				String componentLink1URL  = "";
				TCComponent tcComponent = list.get(i);
				Map<String, String> map = tcComponent.getClassificationAttributes();
				TCComponentICO[] tcico = tcComponent.getClassificationObjects();
				String item_Id = tcComponent.getProperty("IMAN_master_form_rev");
				if ((tcico.length != 0)&&(map.size()!=0)) {
					ICSPropertyDescription[] icsPropertyDescriptions = tcico[0]
							.getICSPropertyDescriptors();
					for (int j = 0; j < icsPropertyDescriptions.length; j++) {
						String name = (icsPropertyDescriptions[j]).getName();
						String unit = Util.getUnit((icsPropertyDescriptions[j])
								.getUnit());
						ICSFormat icsFormat = icsPropertyDescriptions[j]
								.getFormat();
						String icsValue = "";
						if (icsFormat.getType() == -1) {
							icsValue = Util.getNotNull(icsFormat.getKeyLov()
									.getValueOfKey(map.get(name)));
						} else {
							icsValue = Util.getNotNull(map.get(name));
						}
						unit = "".equals(icsValue) ? "" : unit;
						if("".equals(value)){
							value= getValue(item_Id,name,icsValue,unit);
						}
						if (name.contains("ƫ��")) {
							tolerance = Util.getNotNull(icsValue) + unit;
						} else if(name.contains("���ѹ")) {//���ѹ
							ratedVoltage = Util.getNotNull(icsValue) + unit;
						}else if("Library Path".equals(name)) {
							libraryPath = Util.getNotNull(icsValue) + unit;
						}else if("Library Ref".equals(name)) {
							libraryRef = Util.getNotNull(icsValue) + unit;
						}else if("Footprint Path".equals(name)) {
							footprintPath = Util.getNotNull(icsValue) + unit;
						}else if("Footprint Ref".equals(name)) {
							footprintRef = Util.getNotNull(icsValue) + unit;
						}else if("Ʒ��".equals(name)) {//Ʒ��
							supplier = Util.getNotNull(icsValue) + unit;
						}else if("�ͺ�".equals(name)) {  //�ͺ�
							supplierPartNumber = Util.getNotNull(icsValue) + unit;
						}else if("Pricing".equals(name)) {
							pricing = Util.getNotNull(icsValue) + unit;
						}else if("ComponentLink1Description".equals(name)) {
							componentLink1Description = Util.getNotNull(icsValue) + unit;
						}else if(name.contains("��װ")) {
							potting = Util.getNotNull(icsValue) + unit;
						}
						spe_Des += Util.getNotNull(icsValue) + unit+ " ";
					}
				}
				ClassificationCustom classificationCustom = new ClassificationCustom();
				classificationCustom.setId(i);
				classificationCustom.setPartNumber(item_Id);
				classificationCustom.setCategory(tcComponent.getProperty("ics_subclass_name"));
				classificationCustom.setDescription(spe_Des);
				classificationCustom.setValue(value);			    //����
				classificationCustom.setTolerance(tolerance);		//����
				classificationCustom.setRatedVoltage(ratedVoltage);	//���ѹ
				classificationCustom.setLibraryPath(libraryPath);		
				classificationCustom.setLibraryRef(libraryRef);		
				classificationCustom.setFootprintPath(footprintPath);		
				classificationCustom.setFootprintRef(footprintRef);		
				classificationCustom.setSupplier(supplier);		
				classificationCustom.setSupplierPartNumber(supplierPartNumber);		
				classificationCustom.setPricing(pricing);		
				classificationCustom.setComponentLink1Description(componentLink1Description);	
				classificationCustom.setComponentLink1URL(componentLink1URL);	
				
				System.out.println(classificationCustom.toERPTemplate());
				if(!"".equals(classificationCustom.getValue())){
					String 	sql = getSql(item_Id,potting)+"values ("+classificationCustom.toERPTemplate()+")";
					System.out.println(sql);
					au.executeSql(sql);
				}
			}
			au.closeConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getAccess(String id) {
		try {
			//������е�tccomponent
			TCComponent[] tccomponents = getClassficationObject(id);
			if(tccomponents != null && tccomponents.length > 0){
				for (int i = 0; i < tccomponents.length; i++) {
					list.add(tccomponents[i]);
				}
			}
			//�����Ӧ�ķ������Զ�Ӧ�����������
			ICSHierarchyNodeDescriptor[] ICSHierarchyNodeDescriptors = icsService
					.getChildren(id, true, true, 0, 0);
			if(ICSHierarchyNodeDescriptors != null && ICSHierarchyNodeDescriptors.length > 0){
				for (int i = 0; i < ICSHierarchyNodeDescriptors.length; i++) {
					getAccess(ICSHierarchyNodeDescriptors[i].getId());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * ������Ӧ�ķ���������ָ���ķ�����������Ӧ�����������
	 * @param classId ָ���ķ���id
	 * @param hashMap ����id��ֵ�ļ���
	 * @return tccomponent ���ش��ڵ��������������
	 * @throws TCException
	 */
	public TCComponent[] getClassficationObject(String classId) throws Exception {
		TCComponent[] tccomponent = null;
		ICSHierarchyNodeDescriptor aicshierarchynodedescriptor = icsService
				.describeNode(classId.trim(), 0);
		if (aicshierarchynodedescriptor != null
				&& aicshierarchynodedescriptor.isStorageClass()) {
			icsapplicationobject.setView("Base", classId.trim());
			ICSBaseObject icsbaseobject = icsapplicationobject
					.getICSBaseObject();
			ICSView icsview = icsbaseobject.getView();
			ICSPropertyDescription aicspropertydescription[] = icsview
					.getPropertyDescriptions();
			Vector<String> vector = new Vector<String>();
			if (aicspropertydescription != null
					&& aicspropertydescription.length > 0) {
				ICSProperty[] icsproperty = new ICSProperty[aicspropertydescription.length];
				for (int i = 0; i < aicspropertydescription.length; i++) {
					int attributeId = aicspropertydescription[i].getId();
					icsproperty[i] = new ICSProperty(attributeId, "");
				}
				System.out.println("vector.size()/................"
						+ vector.size());
				if (vector.size() == 0) {
					int i = icsapplicationobject.searchByAttributes(
							icsproperty, 0, true);
					if (i > 0) {
						setIcsapplicationobjects(icsapplicationobject);
						tccomponent = new TCComponent[i];
						for (int k = 1; k < i + 1; k++) {
							icsapplicationobject.read(k);
							tccomponent[k - 1] = icsapplicationobject
									.getClassifiedComponent();
							System.out.println("�÷����µ�ʵ����" + k + "��"
									+ tccomponent[k - 1]);
						}
					} else {
						System.out
								.println("��������������û���ڷ������ҵ�ʵ������>>>>>>>>>>>>>>>");
					}
				} else {
					System.out.println("�����б����п�ֵ");
				}
			}
		} else {
			System.out.println("����ID������" + classId
					+ "�ķ��಻���ڣ����Ƿ�optionѡ������������>>>>>>>>>>>>>>>>>>>>>>>>>.");
		}
		return tccomponent;
	}

	public ICSApplicationObject getIcsapplicationobjects() {
		return icsapplicationobjects;
	}

	public void setIcsapplicationobjects(
			ICSApplicationObject icsapplicationobjects) {
		this.icsapplicationobjects = icsapplicationobjects;
	}
	
	public String getSql(String itemId,String desc){
		String 	sql = "insert into" ;
		if(itemId.contains(ConstantClass.CAP)||itemId.contains(ConstantClass.CAP_1)){
			if(itemId.contains(ConstantClass.CAP_TAN)||itemId.contains(ConstantClass.CAP_TAN_1)){
				sql+=	" Cap_Tan ";
			}else{
				if(desc.contains("0603")){
					sql+=	" Cap_0603 ";
				}else if(desc.contains("0805")){
					sql+=	" Cap_0805 ";
				}else if(desc.contains("Al")){
					sql+=	" Cap_Alum ";
				}else{
					sql+=	" Other ";
				}
			}
		}else if(itemId.contains(ConstantClass.CONN_GEN)||itemId.contains(ConstantClass.CONN_GEN_1)){
			sql+=	" Conn_Gen ";
		}else if(itemId.contains(ConstantClass.DIODE)||itemId.contains(ConstantClass.DIODE_1)){
			sql+=	" Diode ";
		}else if(itemId.contains(ConstantClass.IC)||itemId.contains(ConstantClass.IC_1)){
			sql+=	" IC ";
		}else if(itemId.contains(ConstantClass.INDUCTOR)||itemId.contains(ConstantClass.INDUCTOR_1)){
			sql+=	" Inductor ";
		}else if(itemId.contains(ConstantClass.RELAY)||itemId.contains(ConstantClass.RELAY_1)){
			sql+=	" Relay ";
		}else if(itemId.contains(ConstantClass.RES)||itemId.contains(ConstantClass.RES_1)){
			if(itemId.contains(ConstantClass.RES_PTH)||itemId.contains(ConstantClass.RES_PTH_1)){
				sql+=	" Res_PTH ";
			}else{
				if(desc.contains("0603")){
					sql+=	" Res_0603 ";
				}else if(desc.contains("0805")){
					sql+=	" Res_0805 ";
				}else{
					sql+=	" Other ";
				}
			}
		}else if(itemId.contains(ConstantClass.TRANSISTOR)||itemId.contains(ConstantClass.TRANSISTOR_1)){
			sql+=	" Transistor ";
		}else{
			sql+=	" Other ";
		}
		return sql;
	}
	
	public String getValue(String itemId,String name,String icsValue,String unit){
		String value = "";
		if(itemId.contains(ConstantClass.CAP)||itemId.contains(ConstantClass.CAP_1)){
			if (name.contains("��")) {
				value = Util.getNotNull(icsValue) + unit;
			}
		}else if(itemId.contains(ConstantClass.CONN_GEN)||itemId.contains(ConstantClass.CONN_GEN_1)){
			if (name.contains("Footprint Ref")) {
				value = Util.getNotNull(icsValue) + unit;
			}
		}else if(itemId.contains(ConstantClass.DIODE)||itemId.contains(ConstantClass.DIODE_1)){
			if("�ͺ�".equals(name)) {  //�ͺ�
				value = Util.getNotNull(icsValue) + unit;
			}
		}else if(itemId.contains(ConstantClass.IC)||itemId.contains(ConstantClass.IC_1)){
			if("�ͺ�".equals(name)) {  //�ͺ�
				value = Util.getNotNull(icsValue) + unit;
			}
		}else if(itemId.contains(ConstantClass.INDUCTOR)||itemId.contains(ConstantClass.INDUCTOR_1)){
			if (name.contains("���ֵ")) {
				value = Util.getNotNull(icsValue) + unit;
			}
		}else if(itemId.contains(ConstantClass.RELAY)||itemId.contains(ConstantClass.RELAY_1)){
			if (name.contains("��װ")) {
				value = Util.getNotNull(icsValue) + unit;
			}
		}else if(itemId.contains(ConstantClass.RES)||itemId.contains(ConstantClass.RES_1)){
			if (name.contains("��ֵ")) {
				value = Util.getNotNull(icsValue) + unit;
			}
		}else if(itemId.contains(ConstantClass.TRANSISTOR)||itemId.contains(ConstantClass.TRANSISTOR_1)){
			if("�ͺ�".equals(name)) {  //�ͺ�
				value = Util.getNotNull(icsValue) + unit;
			}
		}else{
				value = Util.getNotNull(icsValue) + unit;
		}
		return value;
	}
}
