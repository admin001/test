package com.teamcenter.rac.commands.saveas;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.TCConstants;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMLineType;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.FilterDocument;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.iTextField;
import com.udssoft.tc10.mycustom.baseutil.ConnectFactory;
import com.udssoft.tc10.mycustom.baseutil.StringUtil;

public class CustomMySaveasAsItemInfoPanel extends SaveAsItemInfoPanel{

	public iTextField classificalTextField;
	public iTextField firmTextField;
	public String firmname;
	public CustomMySaveasAsItemInfoPanel(Frame paramFrame, TCSession paramTCSession,
			CustomSaveAsItemRevPanel paramSaveAsItemRevPanel){
		super(paramFrame, paramTCSession, paramSaveAsItemRevPanel);
		if(getTargetsID().get(1).equals("A2YTL_DZL")||getTargetsID().get(1).equals("A2YTL_KCDZL")||getTargetsID().get(1).equals("EDAComp")){
			String targetsID= getTargetsID().get(0);
			 String classificalCode=targetsID.substring(0,targetsID.length()-2);
			 String str2 = TCSession.getServerEncodingName(this.session);
			 String str1 = TCSession.getServerEncodingName(this.session);
			 String str3 = TCSession.getServerEncodingName(this.session);
			 FilterDocument localFilterDocument = new FilterDocument(TCConstants.getDefaultMaxNameSize(this.session), str2);
			 this.classificalTextField = new iTextField(localFilterDocument,classificalCode, 11, 10, true, null);
			 this.classificalTextField.setEnabled(false);
			 this.firmTextField = new iTextField(new FilterDocument(TCConstants.getDefaultMaxNameSize(this.session), str3),"", 8, 50, true, null);
			 this.idTextField=new iTextField(new FilterDocument(TCConstants.getDefaultMaxNameSize(this.session), str1),"", 3, 8, true, null);
			 this.idTextField.setEnabled(false);
			 this.revTextField.setEditable(false);
			 this.textPanel.removeAll();
			 this.textPanel.add("left", this.classificalTextField);
			 this.textPanel.add("left.nobind", this.idTextField);
			 this.textPanel.add("left", this.firmTextField);
			 this.textPanel.add("left.nobind", new JLabel(this.idRevSeparator));
			 this.textPanel.add("left", this.revIdPanel);
			 this.textPanel.add("left.bind", new JLabel(this.revNameSeparator));
			 this.textPanel.add("unbound.bind", this.namePanel);
			 this.textPanel.add("right.nobind", this.assignButton);
			 
			 ActionListener actionListener = this.assignButton.getActionListeners()[0];
			 this.assignButton.removeActionListener(actionListener);
			 this.assignButton.addActionListener(new ActionListener()
			    {
			      public void actionPerformed(ActionEvent paramActionEvent) {
			    		  CustomMySaveasAsItemInfoPanel.this.assignButtonFunction();
			      }
			    });
		}
	}
	private void assignButtonFunction(){
		
	  	String infoText=this.classificalTextField.getText();
	 	   String firm_text = this.firmTextField.getText();
	 	   String[] firm=null;
	 	   if(!StringUtil.isEmpty(firm_text)){
		  	      if(Pattern.compile("^[a-z]{2}$").matcher(firm_text).matches()){
		  	        firm=new ConnectFactory().getFirmByNum(firm_text.toUpperCase(),null,itemPanel.selectedItemType);
		  	        if(!StringUtil.isEmpty(firm[0])){
		  	        	this.firmTextField.setText(firm[1]);
		  	        	this.idTextField.setText(firm[0]);
		  	        	this.firmTextField.setEnabled(true);
		  	        	this.idTextField.setEnabled(false);
		  	        	this.revTextField.setText("01");
		  	        	this.revTextField.setEditable(false);
		  	        	this.assignButton.setEnabled(false);
		  	        }else{
		  	        	JOptionPane.showMessageDialog(null, "无效的供应商编号");
			  	    	 this.idTextField.setText("");
			  	 	     this.idTextField.setEnabled(true);
		  	        }
		  	    	
		  	      }else{
		  	    	firm=new ConnectFactory().getFirmByNum(null,firm_text,itemPanel.selectedItemType);
		  	        if(!StringUtil.isEmpty(firm[0])){
		  	        	this.firmTextField.setText(firm[1]);
		  	        	this.idTextField.setText(firm[0]);
		  	        	this.firmTextField.setEnabled(true);
		  	        	this.idTextField.setEnabled(false);
		  	        	this.revTextField.setText("01");
		  	        	this.revTextField.setEditable(false);
		  	        	this.assignButton.setEnabled(false);
		  	        }else{
		  	        	JOptionPane.showMessageDialog(null, "无效的供应商");
			  	    	 this.idTextField.setText("");
			  	 	     this.idTextField.setEnabled(true);
		  	        }
		  	      }
		  	} else{
		  		JOptionPane.showMessageDialog(null, "请输入供应商信息");
	  	    	 this.idTextField.setText("");
	  	 	     this.idTextField.setEnabled(true);
		  	}
	}
	public List<String> getTargetsID(){
		InterfaceAIFComponent[] targets = null;
		TCComponent root=null;
		List<String> items = new ArrayList<String>();
		String itemID = "";
		String itemType = "";
		targets = AIFUtility.getCurrentApplication().getTargetComponents();
		if (targets != null&&targets.length!=0) {
			for(InterfaceAIFComponent target:targets){
				root = (TCComponent) target;
				if (root instanceof TCComponentItemRevision)
				 {
					 try {
						itemID = root.getStringProperty("item_id");
						itemType=((TCComponentItemRevision) root).getItem().getType();
					} catch (TCException e) {
						e.printStackTrace();
					}
					
				 }else if(root instanceof TCComponentBOMLine){
					 try {
						 itemID = root.getStringProperty("bl_item_item_id"); 
						 itemType =((TCComponentItemRevision) root).getItem().getType();
						} catch (TCException e) {
							e.printStackTrace();
						}
				 }else{
					MessageBox.post("选择目标不正确，请选择item版本", "消息", 2);
				 }
			}
			
		}
		items.add(itemID);
		items.add(itemType);
		return items;
	}
	public String getItemID()
	  {
		  String itemID = "";
		  if(!StringUtil.isEmpty(this.classificalTextField)){
			  itemID = this.classificalTextField.getText()+this.idTextField.getText();
		  }else{
			  itemID = this.idTextField.getText();
		  }
	    return itemID;
	  }
}
