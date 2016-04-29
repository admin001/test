package com.teamcenter.rac.commands.newitem;

import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.wizard.AbstractControlWizardStepPanel;
import com.teamcenter.rac.util.wizard.WizardControlLabel;
import com.udssoft.tc10.mycustom.baseutil.StringUtil;
import java.awt.Frame;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class CustomMyNewItemPanel extends NewItemPanel
{
  public CustomMyItemIdInfoPanel itemIdInfoPanel;

  public CustomMyNewItemPanel(Frame paramFrame, TCSession paramTCSession, CustomMyNewItemDialog paramNewItemDialog)
  {
    super(paramFrame, paramTCSession, paramNewItemDialog);
  }

  public CustomMyNewItemPanel(Frame paramFrame, TCSession paramTCSession, CustomMyNewItemDialog paramNewItemDialog, Boolean paramBoolean)
  {
    super(paramFrame, paramTCSession, paramNewItemDialog, paramBoolean);
  }

  public ItemInfoPanel createInfoPanel(Frame paramFrame, TCSession paramTCSession, boolean paramBoolean)
  {
    return new CustomMyItemInfoPanel(paramFrame, paramTCSession, this, 
      paramBoolean, this.dialog.showUOM, this.dialog.showQuantity);
  }

  public void loadPanels()
  {
    super.loadPanels();
    this.itemIdInfoPanel = 
      new CustomMyItemIdInfoPanel(this.parentFrame, 
      this.session, this);
    this.stepPanels.add(1, this.itemIdInfoPanel);
    System.out.println("-CustomNewItemPanel222--" + this.stepPanels.size());
  }

  public void next()
  {
    if (this.step == getItemTypeStep())
    {
    	if("A2YTL_PCBABCP".equals(this.selectedItemType)){
    		itemIdInfoPanel.pcbabcpClass();
    	}
    	if("A2YTL_BJBM".equals(this.selectedItemType)){
    		itemIdInfoPanel.bjbmClass();
    	}
    	if("A2YTL_JJJL".equals(this.selectedItemType)){
    		itemIdInfoPanel.machineAddClass();
    	}
    	if("A2YTL_JJBZJ".equals(this.selectedItemType)){
    		itemIdInfoPanel.machineAddClass();
    	}
    	if(this.selectedItemType.equals("A2YTL_KCL")){
    		this.step = getItemIdInfoStep();
    	}
    	if(this.selectedItemType.length()>9&&"A2YTL_LS".contains(this.selectedItemType.substring(0, 8))){
        	this.step = getItemIdInfoStep();
        }
      this.nextButton.setEnabled(false);
      this.backButton.setEnabled(true);
      this.homeButton.setEnabled(false);
    }
//    if (this.step == getItemMasterFormStep()){
//    	this.step = getAdditionalAltRevInfoStep();
//  }
    /*if(this.step > getItemInfoStep()){
     //    	
    	try {
			this.itemMasterPanel.itemMasterForm.setProperty("user_data_1", "user_data_1");
		} catch (TCException e) {
			e.printStackTrace();
		}
    }*/
    
    if (this.step == getItemIdInfoStep()) {
      getControlLabel(getItemInfoStep()).setEnabled(true);
    }
    if (this.step == getItemInfoStep()) {
      setItemInfo();
    }

    
    if (this.step == getItemRevMasterFormStep()){
    	this.step = getAdditionalAltRevInfoStep();
    }
    super.next();
  }
  /* reBulidItemIdPanel();
  try {
  	this.itemMasterPanel.itemMasterForm.setProperty("user_data_1", "user_data_1");
  	System.out.println(this.itemMasterPanel.itemMasterForm.getProperty("user_data_1"));
  } catch (TCException e) {
  	e.printStackTrace();
  }
  super.finish();*/
  public void finish()
  {
	 /* System.out.println(this.itemInfoPanel.itemPanel.itemMasterPanel.itemMasterForm);
		int a=this.step;
		if(a!=this.getItemMasterFormStep()){
			JOptionPane.showMessageDialog(null, "请点击下一步");
			return;
		}
		try {
			this.itemInfoPanel.itemPanel.itemMasterPanel.itemMasterForm.save();
			this.itemInfoPanel.itemPanel.itemMasterPanel.itemMasterForm.setProperty("user_data_2", "user_data_3");
			System.out.println(this.itemInfoPanel);
		} catch (TCException e) {
			e.printStackTrace();
		}*/
		
		super.finish();
		reBulidItemIdPanel();
  }

  public void back() {
    if (this.step == getItemInfoStep()) {
      this.nextButton.setEnabled(true);
      this.backButton.setEnabled(true);
      this.homeButton.setEnabled(true);
    }

    if (this.step == getAdditionalAltRevInfoStep() + 1) {
      this.step = (getItemRevMasterFormStep() + 1);
    }

    if (this.step == getItemIdInfoStep()) {
      reBulidItemIdPanel();
    }
    super.back();
  }

  public int getTotalStep()
  {
    return super.getTotalStep() + 1;
  }

  public int getItemTypeStep()
  {
    return super.getItemTypeStep();
  }

  public int getItemIdInfoStep() {
    return this.dialog.viDialog == null ? 1 : 2;
  }

  public int getItemInfoStep()
  {
    return super.getItemInfoStep() + 1;
  }

  public int getAttachFilesStep()
  {
    return super.getAttachFilesStep() + 1;
  }

  public int getSubmitToWorkflowStep()
  {
    return super.getSubmitToWorkflowStep() + 1;
  }

  public int getItemMasterFormStep()
  {
    return super.getItemMasterFormStep() + 1;
  }

  public int getItemRevMasterFormStep()
  {
    return super.getItemRevMasterFormStep() + 1;
  }

  public int getAltIdInfoStep()
  {
    return super.getAltIdInfoStep() + 1;
  }

  public int getAdditionalAltIdInfoStep()
  {
    return super.getAdditionalAltIdInfoStep() + 1;
  }

  public int getAdditionalAltRevInfoStep()
  {
    return super.getAdditionalAltRevInfoStep() + 1;
    
  }

  public int getAssignProjectStep()
  {
    return super.getAssignProjectStep() + 1;
  }

  public int getOpenOptionStep()
  {
    return super.getOpenOptionStep() + 1;
  }

  public void itemTypeSelected()
  {
    super.itemTypeSelected();
    getControlLabel(getItemIdInfoStep()).setEnabled(true);
    this.itemIdInfoPanel.changeLOVComponent(this.selectedItemType);
  }

  protected void setStepsStatus() {
    super.setStepsStatus();
    getControlLabel(getItemInfoStep()).setEnabled(false);
  }

  public void afterSetPanel(int paramInt)
  {
    if (paramInt == getItemInfoStep())
      setItemInfo();
  }

  private void setItemInfo()
  {
	Pattern pattern = Pattern.compile("^[A-Z,0-9]{1}$");
    String code = this.itemIdInfoPanel.idTextField.getText();
    if (!StringUtil.isEmpty(code)) {
      this.itemInfoPanel.idTextField.setText(code);
      this.itemInfoPanel.idTextField.setEditable(false);
      this.itemInfoPanel.revTextField.setText("01");
      this.itemInfoPanel.revTextField.setEditable(true);
      try {
        String name = "";
        try {
            name = this.itemIdInfoPanel.six_text.getSelectedDisplayString(); 
            if((!StringUtil.isEmpty(this.itemIdInfoPanel.six_text.getSelectedString()))&&pattern.matcher(this.itemIdInfoPanel.six_text.getSelectedString()).matches()){
          	  name = this.itemIdInfoPanel.four_text.getSelectedDisplayString(); 
          	  if(isLastNode(name)){
            	  name = this.itemIdInfoPanel.three_text.getSelectedDisplayString();
              }
            }
            if(isLastNode(name)){
          	  name = this.itemIdInfoPanel.four_text.getSelectedDisplayString();
            }
            } catch (Exception localException1) {
          }
        if (StringUtil.isEmpty(name))  try {
          name = this.itemIdInfoPanel.four_text.getSelectedDisplayString(); 
          if((!StringUtil.isEmpty(this.itemIdInfoPanel.four_text.getSelectedString()))&&pattern.matcher(this.itemIdInfoPanel.four_text.getSelectedString()).matches()){
        	  name = this.itemIdInfoPanel.three_text.getSelectedDisplayString();
        	  if(isLastNode(name)){
            	  name = this.itemIdInfoPanel.two_text.getSelectedDisplayString();
              }
          }
          if(isLastNode(name)){
        	  name = this.itemIdInfoPanel.three_text.getSelectedDisplayString();
          }
          } catch (Exception localException1) {
        }
        if (StringUtil.isEmpty(name)) try {
            name = this.itemIdInfoPanel.three_text.getSelectedDisplayString();
            if((!StringUtil.isEmpty(this.itemIdInfoPanel.three_text.getSelectedString()))&&pattern.matcher(this.itemIdInfoPanel.three_text.getSelectedString()).matches()){
            	 name = this.itemIdInfoPanel.two_text.getSelectedDisplayString(); 
            	 if(isLastNode(name)){
                 	  name = this.itemIdInfoPanel.one_text.getSelectedDisplayString();
                   }
            }
            if(isLastNode(name)){
          	  name = this.itemIdInfoPanel.two_text.getSelectedDisplayString();
            }
          } catch (Exception localException2) {
          } if (StringUtil.isEmpty(name)) try {
            name = this.itemIdInfoPanel.two_text.getSelectedDisplayString();
            if((!StringUtil.isEmpty(this.itemIdInfoPanel.two_text.getSelectedString()))&&pattern.matcher(this.itemIdInfoPanel.two_text.getSelectedString()).matches()){
            	name = this.itemIdInfoPanel.one_text.getSelectedDisplayString();
            }
            if(isLastNode(name)){
            	  name = this.itemIdInfoPanel.one_text.getSelectedDisplayString();
              }
          } catch (Exception localException3) {
          } if (StringUtil.isEmpty(name)) try {
            name = this.itemIdInfoPanel.one_text.getSelectedDisplayString();
          }
          catch (Exception localException4) {
          } 
          if("A2YTL_CPBM".equals(this.selectedItemType)||"A2YTL_ZZBCP".equals(this.selectedItemType)||"A2YTL_PCBABCP".equals(this.selectedItemType)){
        	  name="zzbcp";
        	  this.itemInfoPanel.revTextField.setText("001");
          }
          if("A2YTL_WDBM".equals(this.selectedItemType)){
        	  this.itemInfoPanel.revTextField.setText("A00");
          }
          if("A2YTL_JJJL".equals(this.selectedItemType)){
        	  name="";
          }
          if("A2YTL_JJBZJ".equals(this.selectedItemType)){
        	  name="";
          }
          if(name.equals("免费辅料类")){
        	  this.itemInfoPanel.nameTextField.setText("");
          }else{
        	  this.itemInfoPanel.nameTextField.setText(name);//BMIDE LOV 名称
          }
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public boolean isLastNode(String str){
	  if(StringUtil.isEmpty(str)){
		  return false;
	  }
	  if(str.equals("五金类")||str.equals("塑胶类")||str.equals("尼龙类")||str.equals("工程塑料")||str.equals("橡胶类")||str.equals("橡胶类聚氨酯")){
		  return true;
	  }
	  return false;
  }
  public boolean isPerformable()
  {
    boolean bool1 = getBool1();
    boolean bool2 = false;
    if ((getItemInfoStep() < this.stepPanels.size()) && (this.stepPanels.get(getItemInfoStep()) != null)) {
      bool2 = infoPanelReady();
    }
    if ((getAltIdInfoStep() < this.stepPanels.size()) && 
      (this.stepPanels.get(getAltIdInfoStep()) != null)) {
      NewItemAlternateIdInfoPanel localNewItemAlternateIdInfoPanel = null;
      try {
        localNewItemAlternateIdInfoPanel = 
          (NewItemAlternateIdInfoPanel)this.stepPanels
          .get(getAltIdInfoStep());
      } catch (Exception e) {
        e.printStackTrace();
        try { localNewItemAlternateIdInfoPanel = 
            (NewItemAlternateIdInfoPanel)this.stepPanels
            .get(getAltIdInfoStep() - 1); } catch (Exception localException1) {
        }
      }
      try {
        boolean bool3 = localNewItemAlternateIdInfoPanel
          .isPerformable();
        WizardControlLabel localWizardControlLabel1 = getControlLabel(getAdditionalAltIdInfoStep());
        if (localWizardControlLabel1 != null) {
          boolean bool4 = (bool3) && (bool2);
          localWizardControlLabel1.setEnabled(bool4);
        }

        WizardControlLabel localWizardControlLabel2 = getControlLabel(getAdditionalAltRevInfoStep());
        if (localWizardControlLabel2 != null) {
          boolean bool5 = (bool3) && (bool2);
          localWizardControlLabel2.setEnabled(bool5);
        }
      } catch (Exception localException2) {
      }
    }
    return bool1;
  }

  public boolean getBool1() {
    if ((this.stepPanels == null) || (this.stepPanels.size() == 0))
      return false;
    int i = this.stepPanels.size();
    for (int j = 0; j < i; j++)
      if ((this.stepPanels.get(j) == null) || 
        (!((AbstractControlWizardStepPanel)this.stepPanels
        .get(j)).isPerformable()))
        return false;
    return true;
  }

  public void reBulidItemIdPanel() {
    this.stepPanels.remove(this.itemIdInfoPanel);
    this.itemIdInfoPanel = 
      new CustomMyItemIdInfoPanel(this.parentFrame, 
      this.session, this);
    this.stepPanels.add(1, this.itemIdInfoPanel);
    this.itemIdInfoPanel.changeLOVComponent(this.selectedItemType);
  }
}