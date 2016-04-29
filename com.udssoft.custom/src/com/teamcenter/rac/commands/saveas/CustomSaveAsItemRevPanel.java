package com.teamcenter.rac.commands.saveas;

import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.commands.newalternateid.AdditionalIdInfoPanel;
import com.teamcenter.rac.commands.newalternateid.AdditionalRevInfoPanel;
import com.teamcenter.rac.commands.newitem.AssignProjectPanel;
import com.teamcenter.rac.commands.newitem.CustomMyItemInfoPanel;
import com.teamcenter.rac.commands.newitem.ItemInfoPanel;
import com.teamcenter.rac.commands.newitem.ItemMasterFormPanel;
import com.teamcenter.rac.commands.newitem.ItemRevMasterFormPanel;
import com.teamcenter.rac.commands.newitem.ItemTypePanel;
import com.teamcenter.rac.commands.newitem.NewItemFinishPanel;
import com.teamcenter.rac.commands.newitem.OpenOptionPanel;
import com.teamcenter.rac.form.ItemMasterForm;
import com.teamcenter.rac.form.ItemRevisionMasterForm;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.Utilities;
import com.teamcenter.rac.util.log.Debug;
import com.udssoft.tc10.mycustom.baseutil.ConnectFactory;

public class CustomSaveAsItemRevPanel extends SaveAsItemRevPanel{
	public CustomMySaveasAsItemInfoPanel customMySaveasAsItemInfoPanel;
	public CustomSaveAsItemRevPanel(Frame paramFrame, TCSession paramTCSession,
			CustomSaveAsItemRevDialog paramSaveAsItemRevDialog) {
		super(paramFrame, paramTCSession, paramSaveAsItemRevDialog);
	}
	public CustomSaveAsItemRevPanel(Frame paramFrame, TCSession paramTCSession, CustomSaveAsItemRevDialog paramSaveAsItemRevDialog, Boolean paramBoolean)
	  {
	    super(paramFrame, paramTCSession, paramSaveAsItemRevDialog, paramBoolean);
	  }
	
	public void finish(){
		System.out.println(this.itemInfoPanel.getItemID());
		int a=this.step;
		if(a!=this.getItemMasterFormStep()){
			JOptionPane.showMessageDialog(null, "请点击下一步");
			return;
		}
		try {
			String id=this.itemInfoPanel.getItemID();
			String[] ss=new ConnectFactory().getFirmByNum(id.substring(id.length()-2, id.length()), "", this.itemInfoPanel.itemPanel.selectedItemType);
			this.itemMasterPanel.itemMasterForm.setProperty("user_data_1", ss[1]);
			System.out.println(this.itemInfoPanel);
		} catch (TCException e) {
			e.printStackTrace();
		}
		super.finish();
	}
	 public void loadPanels()
	  {
		 if (Debug.isOn("NewItem"))
		      System.out.println("SaveAsItemRevPanel::loadPanels()");
		    if (this.stepPanels == null)
		      this.stepPanels = new ArrayList();
		    this.selectedItemType = ((SaveAsItemRevDialog)this.dialog).selectedItemType;
		    this.itemInfoPanel = new CustomMySaveasAsItemInfoPanel(this.parentFrame, this.session, this);
		    this.stepPanels.add(this.itemInfoPanel);
		    this.itemMasterPanel = new ItemMasterFormPanel(this.parentFrame, this.session, this);
		    this.stepPanels.add(this.itemMasterPanel);
		    this.itemRevMasterPanel = new ItemRevMasterFormPanel(this.parentFrame, this.session, this);
		    this.stepPanels.add(this.itemRevMasterPanel);
		    this.altIdInfoPanel = new SaveAsAlternateIdInfoPanel(this.parentFrame, this.session, this);
		    this.stepPanels.add(this.altIdInfoPanel);
		    this.additionalAltIdInfoPanel = new AdditionalIdInfoPanel(this.parentFrame, this.session, this);
		    this.stepPanels.add(this.additionalAltIdInfoPanel);
		    this.additionalAltRevInfoPanel = new AdditionalRevInfoPanel(this.parentFrame, this.session, this);
		    this.stepPanels.add(this.additionalAltRevInfoPanel);
		    this.attachmentPanel = new SaveAsItemRevAttachmentPanel(this.parentFrame, this.session, this);
		    this.stepPanels.add(this.attachmentPanel);
		    this.assignProjectPanel = new AssignProjectPanel(this.parentFrame, this.session, this);
		    this.stepPanels.add(this.assignProjectPanel);
		    this.openOptionPanel = new OpenOptionPanel(this.parentFrame, this.session, this); 
		    try
		    {
		      Utilities.invokeMethod(this.assignProjectPanel.projSelPanel, "setSource", new Class[] { TCComponent.class }, this.pasteTargets);
		      Utilities.invokeMethod(this.assignProjectPanel.projSelPanel, "loadProjects", new Object[0]);
		    }
		    catch (Exception localException)
		    {
		    }
		    this.stepPanels.add(this.openOptionPanel);
		    
		    
		    this.finishPanel = new NewItemFinishPanel(this.parentFrame, this.session, this);
	  }
}
