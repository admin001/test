package com.teamcenter.rac.classification.common.form;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.omg.CORBA.LocalObject;

import com.JTextFileAndJComboBox.AutoCompleteComponet;
import com.JTextFileAndJComboBox.SingJPanel;
import com.teamcenter.rac.classification.common.AbstractG4MContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentRevisionRule;
import com.teamcenter.rac.kernel.ics.G4MConstants;
import com.teamcenter.rac.kernel.ics.ICSKeyLov;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.log.Debug;

public class CustomMyColorUserButton extends JButton implements
		InterfaceG4MFormUserButton, ActionListener {
	private AbstractG4MFormElement m_formElement;
//	private int m_multiFieldIndex;
	private AbstractG4MContext cus_context;
	private CompleteProValuePanel single; 
	public static final  String BRAND = "a2_PP";
	public static final  String Model = "a2_XH";
	// ============================================================================
	public CustomMyColorUserButton(AbstractG4MFormElement theFormElement,
			int theIndex) {
		m_formElement = theFormElement;
//		m_multiFieldIndex = theIndex;
		configure();
		addActionListener(this);
	}
	// ============================================================================
	private void configure() {
//		Icon icon = 
//				m_formElement.getForm().getRegistry()
//				.getImageIcon(getClass().getName() + ".ICON");
//		if (icon == null) {
//			icon = m_formElement.getForm().getRegistry()
//					.getImageIcon("g4mform.userbutton.DEFAULT_ICON");
//		}
//		if (icon != null) {
//			setIcon(icon);
//		}
		
		setText("映射SolidWorks");
		setMargin(new Insets(0, 0, 0, 0));
		setFocusPainted(false);
		this.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent evt){
				jButton1MouseClicked(evt);
				System.out.println("fdfd");
			}
		});
	}

	// ============================================================================
	@Override
	public void actionPerformed(ActionEvent ev) {
		try {
			if(m_formElement.getForm().getContext().isSaveRequired()){
				ICSProperty[] icsProps = m_formElement.getForm().getProperties();
				icsProps[0].getValue();
				AbstractG4MFormElement[] formElements = m_formElement.getForm().getFormElements();
				//获得此分类的itemrevision对象
				 cus_context = m_formElement.getForm().getContext();
//				 m_formElement.getForm().getSession()
				 String str2 = this.cus_context.getClassifiedObjectUid();
				 TCComponent tcComponent = this.cus_context.getClassificationService().getTCComponent(str2);
				 CustomMyColorUserButton customMyColorUserButton = (CustomMyColorUserButton) ev.getSource();
				 AbstractG4MFormElement g4MformElement = customMyColorUserButton.getFormElement();
				 ICSPropertyDescription propertyDescription =  g4MformElement.getPropertyDescription();
				 ICSProperty property = g4MformElement.getProperty();
				 if (tcComponent.isTypeOf("ItemRevision"))
		          {
					 TCComponentItemRevision tcComponentItemRevision = ((TCComponentItemRevision)tcComponent);
					// Map<String, String> maps= tcComponentItemRevision.getClassificationAttributes();
					 if(!propertyDescription.isHidden()){
						/* if (propertyDescription.getFormat().isList()
								 &&propertyDescription.getFormat().getKeyLov().getValueCount()>7) {
//							 String[] values = propertyDescription.getFormat().getKeyLov().getValues();
//							 items.put(propertyDescription.getName(),values );
							 single = new CompleteProValuePanel(g4MformElement,tcComponentItemRevision);
							 single.setVisible(true);
						 }*/
						 //判断是否为下拉框
						 //ICSPropertyDescription[] icss= tcComponentItemRevision.getClassificationObjects()[0].getICSPropertyDescriptors();
						 String pp="";
						 String xh="";
						 for(int i=0; i<formElements.length;i++){
							 ICSPropertyDescription dispalyName = formElements[i].getPropertyDescription();
							 if(dispalyName.getDisplayName().contains("品牌")){
								 if(dispalyName.getFormat().isList()){
									 pp=dispalyName.getFormat().getKeyLov().getDisplayValue(icsProps[i].getValue());
								 }else{
									 pp=icsProps[i].getValue();
								 }
								 tcComponentItemRevision.setProperty(BRAND, pp);
							 }
							 if(dispalyName.getDisplayName().contains("型号")){
								 if(dispalyName.getFormat().isList()){
									 xh=dispalyName.getFormat().getKeyLov().getDisplayValue(icsProps[i].getValue());
								 }else{
									 xh=icsProps[i].getValue();
								 }
								 tcComponentItemRevision.setProperty(Model, xh);
							 }
						 }
						 /*for (int i = 0; i < icss.length; i++) {
							 if(icss[i].getDisplayName().contains("品牌")){
								 if(icss[i].getFormat().isList()){
									 pp=icss[i].getFormat().getKeyLov().getDisplayValue(maps.get(icss[i].getDisplayName()));
								 }else{
									 pp=maps.get(icss[i].getDisplayName());
								 }
								 tcComponentItemRevision.setProperty(BRAND, pp);
							 }
							 if(icss[i].getDisplayName().contains("型号")){
								 if(icss[i].getFormat().isList()){
									 xh=icss[i].getFormat().getKeyLov().getDisplayValue(maps.get(icss[i].getDisplayName()));
								 }else{
									 xh=maps.get(icss[i].getDisplayName());
								 }
								 tcComponentItemRevision.setProperty(Model, xh);
							 }
						}*/
						/* //判断是否选择完毕
						 if(propertyDescription.getName().contains("品牌")){
						 }
						 if(propertyDescription.getName().contains("型号")){
						 }*/
//						 this.addMouseListener(new MouseAdapter() {
//								public void mouseClicked(MouseEvent evt){
//									jButton1MouseClicked(evt);
//									System.out.println("fdfd");
//								}
//							});
	            	}
		          }
//				setEnabled(false);
			}else{
				throw new Exception();
			}
			// Loop through all properties
			//this.setEnabled(false);
//			MessageBox.post("Class ID: " + m_formElement.getForm().getClassId()
//					+ "\n" + "Attribute ID: "
//					+ m_formElement.getPropertyDescription().getId() + "\n"
//					+ "Attribute Name: "
//					+ m_formElement.getPropertyDescription().getName() + "\n"
//					+ "Attribute Format: "
//					+ m_formElement.getPropertyDescription().getFormat() + "\n"
//					+ "Attribute Value: "
//					+ m_formElement.getProperty().getValue(),
//					"User-Defined Button", MessageBox.INFORMATION);
		} catch (Exception ex) {
			 JOptionPane.showMessageDialog(null, "请在编辑状态下，进行设置");
		}
	}
	


	// ============================================================================
	public AbstractG4MFormElement getFormElement() {
		return m_formElement;
	}
	
	public JComponent getJComponent() {
		return this;
	}

	public void setMode(int theMode) {
		switch (theMode) {
		case G4MConstants.MODE_SEARCH:
			setVisible(true);
			break;
		case G4MConstants.MODE_SHOW:
			setVisible(true);
			break;
		case G4MConstants.MODE_NEW:
			setVisible(true);
			break;
		case G4MConstants.MODE_EDIT:
			setVisible(true);
			break;
		}
	}
	 private void jButton1MouseClicked(MouseEvent evt) {//*关键*一个处理鼠标单击按钮事件的方法  通过鼠标单击调用下面一个类的窗体
//		 SingJPanel.getInstance(items,m_formElement);
    }
}