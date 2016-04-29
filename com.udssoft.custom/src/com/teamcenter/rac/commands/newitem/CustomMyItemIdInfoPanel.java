package com.teamcenter.rac.commands.newitem;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.common.lov.LOVComboBox;
import com.teamcenter.rac.kernel.ListOfValuesInfo;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentListOfValues;
import com.teamcenter.rac.kernel.TCComponentListOfValuesType;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCPreferenceService;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.rac.util.PropertyLayout;
import com.teamcenter.rac.util.Registry;
import com.teamcenter.rac.util.VerticalLayout;
import com.teamcenter.rac.util.iTextField;
import com.teamcenter.rac.util.wizard.AbstractControlWizardStepPanel;
import com.udssoft.tc10.mycustom.baseutil.ConnectFactory;
import com.udssoft.tc10.mycustom.baseutil.StringUtil;
import com.udssoft.tc10.mycustom.newitemid.Locales;

public class CustomMyItemIdInfoPanel extends AbstractControlWizardStepPanel
{
  public Frame parentFrame;
  public TCSession session;
  public CustomMyNewItemPanel itemPanel;
  public JLabel infoLabel;
  public iTextField idTextField;
  public LOVComboBox one_text;
  public LOVComboBox two_text;
  public LOVComboBox three_text;
  public LOVComboBox four_text;
  public LOVComboBox six_text;
  protected Registry packageReg = null;
  public JPanel onePanel;
  public JPanel panel2;
  public JPanel panel3;
  public JPanel panel4;
  public JPanel panel5;
  public JPanel panel6;
  public JPanel panel7;
  public JPanel panel8;
  //public JPanel panel9;
  public JPanel panel10;
  public JPanel configCodePanel; 
  public JPanel waterCodePanel;
  public JLabel configCodeLable;
  public JLabel endCodeLable;
  public JLabel centerModelLable;
  public JLabel smallModelLable;
  public JLabel numCodeLable;
  public JLabel supplierLable;
  public JLabel waterCodeLable;
  public JLabel lable;
  public iTextField supplierCode;
  public iTextField waterCode;
  public iTextField configCode;
  public iTextField endCode;
  public iTextField centerModelCode;
  public iTextField smallModelCode;
  public iTextField numCode;
  public JButton waterCodeButton;
  public JButton configCodeButton;
  public JButton nButton;
  public JRadioButton optionRButton;
private Object String ;

  public CustomMyItemIdInfoPanel(Frame paramFrame, TCSession paramTCSession, CustomMyNewItemPanel paramNewItemPanel)
  {
    this(paramFrame, paramTCSession, paramNewItemPanel, false);
  }

  public CustomMyItemIdInfoPanel(TCSession paramTCSession, CustomMyNewItemPanel paramNewItemPanel)
  {
    super(paramNewItemPanel);
    this.session = paramTCSession;
    this.itemPanel = paramNewItemPanel;
    this.packageReg = paramNewItemPanel.getRegistry();
  }

  public CustomMyItemIdInfoPanel(Frame paramFrame, TCSession paramTCSession, CustomMyNewItemPanel paramNewItemPanel, boolean paramBoolean)
  {
    this(paramFrame, paramTCSession, paramNewItemPanel, paramBoolean, true, 
      false);
  }

  public CustomMyItemIdInfoPanel(Frame paramFrame, TCSession paramTCSession, CustomMyNewItemPanel paramNewItemPanel, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    super(paramNewItemPanel);
    this.parentFrame = paramFrame;
    this.session = paramTCSession;
    this.itemPanel = paramNewItemPanel;
    this.packageReg = paramNewItemPanel.getRegistry();
    initPanel();
  }

  public void initPanel() {
    setLayout(new VerticalLayout(5, 2, 2, 2, 2));
    this.infoLabel = new JLabel(getPageTitle(), getPageIcon(), 2);
    this.infoLabel.setForeground(Color.BLUE);
    add("top.bind.left.top", this.infoLabel);
    this.idTextField = new iTextField("", 20);
    this.idTextField.setEditable(false);
    JLabel descLable = new JLabel(Locales.getString("itemId") + "：");
    JPanel descPanel = new JPanel(new PropertyLayout());
    descPanel.add("1.1.left.top", descLable);
    descPanel.add("1.2.center.center", this.idTextField);
    add("top.bind.left.top", descPanel);
    getOnePanel();
    //this.onePanel.setVisible(false);
    getTwoPanel(null);
    this.panel2.setVisible(false);
    getThreePanel(null);
    this.panel3.setVisible(false);
    getFourPanel(null);
    this.panel4.setVisible(false);
    getSixPanel(null);
    this.panel6.setVisible(false);
    
    //PCB成品编码  图号
    this.endCode = new iTextField("", 15);
    endCodeLable = new JLabel("图     号" + "：");
    panel5 = new JPanel(new PropertyLayout());
    panel5.add("1.1.left.top", endCodeLable);
    panel5.add("1.2.center.center", this.endCode);
    add("top.bind.left.top", panel5);
    panel5.setVisible(true);
    
//    //中模centerModel
//    this.centerModelCode = new iTextField("", 15);
//    centerModelLable = new JLabel("中   模 " + 
//    	      "：");
//    panel8 = new JPanel(new PropertyLayout());
//    panel8.add("1.1.left.top", centerModelLable);
//    panel8.add("1.2.center.center", this.centerModelCode);
//    add("top.bind.left.top", panel8);
//    panel8.setVisible(false);
    
    //装配号
      this.centerModelCode = new iTextField("", 15);
	  centerModelLable = new JLabel("装 配 号 " +"：");
	  panel8 = new JPanel(new PropertyLayout());
	  panel8.add("1.1.left.top", centerModelLable);
	  panel8.add("1.2.center.center", this.centerModelCode);
	  add("top.bind.left.top", panel8);
	  panel8.setVisible(false);
  
    
    //组合台数，PCBA片数，机加件两位流水码
    this.numCode = new iTextField("", 15);
    numCodeLable = new JLabel("装 配 号" + "：");
    panel7 = new JPanel(new PropertyLayout());
    panel7.add("1.1.left.top", numCodeLable);
    panel7.add("1.2.center.center", this.numCode);
    add("top.bind.left.top", panel7);
    panel7.setVisible(false);
    
//    //小模smallModel
//    this.smallModelCode = new iTextField("", 15);
//    smallModelLable = new JLabel("小   模 " + 
//    	      "：");
//    panel9 = new JPanel(new PropertyLayout());
//    panel9.add("1.1.left.top", smallModelLable);
//    panel9.add("1.2.center.center", this.smallModelCode);
//    add("top.bind.left.top", panel9);
//    panel9.setVisible(false);
    
    //供应商
    this.supplierCode = new iTextField("", 15);
    supplierLable = new JLabel("供 应 商" + "：");
    panel10 = new JPanel(new PropertyLayout());
    panel10.add("1.1.left.top", supplierLable);
    panel10.add("1.2.center.center", this.supplierCode);
    add("top.bind.left.top", panel10);
    panel10.setVisible(false);
    
    
    this.waterCode = new iTextField("", 15);
    this.waterCode.setEditable(false);
    this.waterCode.getDocument().addDocumentListener(new DocumentListener()
    {
      public void removeUpdate(DocumentEvent e) {
        judgeButtonByTextField();
      }

      public void insertUpdate(DocumentEvent e)
      {
        judgeButtonByTextField();
      }

      public void changedUpdate(DocumentEvent e)
      {
        judgeButtonByTextField();
      }

      public void judgeButtonByTextField() {
        if (!StringUtil.isEmpty(CustomMyItemIdInfoPanel.this.waterCode.getText())) {
        	CustomMyItemIdInfoPanel.this.waterCodeButton.setEnabled(false);
        	CustomMyItemIdInfoPanel.this.configCodeButton.setEnabled(true);
        	CustomMyItemIdInfoPanel.this.itemPanel.nextButton.setEnabled(false);
        } else {
          CustomMyItemIdInfoPanel.this.waterCodeButton.setEnabled(true);
          CustomMyItemIdInfoPanel.this.configCodeButton.setEnabled(false);
          CustomMyItemIdInfoPanel.this.configCode.setText("");
          CustomMyItemIdInfoPanel.this.idTextField.setText("");
        }
      }
    });
     waterCodeLable = new JLabel(Locales.getString("SerialNumber") + 
      "：");
    waterCodePanel = new JPanel(new PropertyLayout());
    waterCodePanel.add("1.1.left.top", waterCodeLable);
    waterCodePanel.add("1.2.center.center", this.waterCode);
    this.waterCodeButton = new JButton(Locales.getString("assignButton"));
    this.waterCodeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CustomMyItemIdInfoPanel.this.waterCodeButtonFunction();
      }
    });
    this.waterCodeButton.setEnabled(false);
    waterCodePanel.add("1.3.center.center", this.waterCodeButton);
    add("top.bind.left.top", waterCodePanel);
    
    this.configCode = new iTextField("", 15);
    this.configCode.setEditable(false);
    configCodeLable = new JLabel(Locales.getString("ConfigNo") + "：");
    configCodePanel = new JPanel(new PropertyLayout());
    configCodePanel.add("1.1.left.top", configCodeLable);
    configCodePanel.add("1.2.center.center", this.configCode);
    this.configCodeButton = new JButton(Locales.getString("assignButton"));
    this.configCodeButton.setEnabled(false);
    this.configCodeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        CustomMyItemIdInfoPanel.this.configCodeButtonFunction();
      }
    });
    configCodePanel.add("1.3.center.center", this.configCodeButton);
    add("top.bind.left.top", configCodePanel);
    configCodePanel.setVisible(false);
    
  }
  
  /**
   * PCBA成品编码类型
   */
  public void pcbabcpClass(){
	  this.onePanel.setVisible(true);
	 // numCodeLable.setText("板 序 号：");
	  waterCodeLable.setText("板 序 号：");
	  endCodeLable.setText("图    号：");
	  panel5.setVisible(true);
	  this.waterCodeButton.setEnabled(true);
	  waterCodePanel.setVisible(true);
	  configCodePanel.setVisible(false);
	  configCodeButton.setEnabled(false);
	  configCode.setEditable(true);
  }
  
  /**
   * 备件编码类型
   */
  public void bjbmClass(){
	  this.onePanel.setVisible(false);
	  endCodeLable.setText("成品编码：");
	  panel5.setVisible(true);
	  this.waterCodeButton.setEnabled(true);
	  waterCodePanel.setVisible(true);
	  configCodePanel.setVisible(false);
	  configCodeButton.setEnabled(false);
	  configCode.setEditable(false);
  }
  
  /**
   * 机加件类
   */
  public void machineAddClass(){
	  endCodeLable.setText("图   号 ：");
	  numCodeLable.setText("流 水 码：");
	  this.waterCodeButton.setEnabled(true);
	  panel5.setVisible(true);
	  panel7.setVisible(false);
	  panel8.setVisible(true);
	  //panel9.setVisible(true);
	  this.onePanel.setVisible(true);
  }

  public void getOnePanel() {
    JLabel oneLable = new JLabel(Locales.getString("oneclass") + "：");
    onePanel = new JPanel(new PropertyLayout());
    TCComponentListOfValues oneLovs = null;
  /*  try {
      oneLovs = getParentLOV();
    } catch (TCException e) {
      e.printStackTrace();
    }
    if (StringUtil.isEmpty(oneLovs)) {
      MessageBox.post(AIFUtility.getCurrentApplication().getDesktop(), 
        Locales.getString("errorMessage1"), "提示", 2);
      return;
    }*/
    this.one_text = new LOVComboBox();
    this.one_text.setLOVComponent(oneLovs);
    this.one_text.addActionListener(new ActionListener() {
      @SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent arg0) {
    	CustomMyItemIdInfoPanel.this.isWaterCodeButtonEnabled(CustomMyItemIdInfoPanel.this.one_text);
        CustomMyItemIdInfoPanel.this.panel2.setVisible(false);
        CustomMyItemIdInfoPanel.this.two_text.setSelectedIndex(0);
        CustomMyItemIdInfoPanel.this.panel3.setVisible(false);
        CustomMyItemIdInfoPanel.this.three_text.setSelectedIndex(0);
        CustomMyItemIdInfoPanel.this.panel4.setVisible(false);
        CustomMyItemIdInfoPanel.this.four_text.setSelectedIndex(0);
        CustomMyItemIdInfoPanel.this.panel6.setVisible(false);
        CustomMyItemIdInfoPanel.this.six_text.setSelectedIndex(0);
        CustomMyItemIdInfoPanel.this.waterCode.setEnabled(false);
        Object selectObj = CustomMyItemIdInfoPanel.this.one_text.getSelectedObject();
        if (!StringUtil.isEmpty(selectObj)) {
          ListOfValuesInfo li = null;
          try {
            li = CustomMyItemIdInfoPanel.this.one_text.getLovComponent().getListOfValues();
          } catch (TCException e) {
            e.printStackTrace();
          }
          
          int index = CustomMyItemIdInfoPanel.this.one_text.getSelectedIndex();
          TCComponentListOfValues twoLovs = li.getListOfFilterAtIndex(index);
          CustomMyItemIdInfoPanel.this.clearLabelWatButtonAndConButton();
          if(itemPanel.selectedItemType.equals("A2YTL_ZZBCP")){
        	  CustomMyItemIdInfoPanel.this.endCodeLable.setText("图   号 ：");
        	  CustomMyItemIdInfoPanel.this.panel5.setVisible(true);
        	  CustomMyItemIdInfoPanel.this.panel7.setVisible(true);
        	  CustomMyItemIdInfoPanel.this.waterCodePanel.setVisible(true);
        	  CustomMyItemIdInfoPanel.this.configCodePanel.setVisible(false);
        	  CustomMyItemIdInfoPanel.this.configCodeButton.setEnabled(false);
        	  CustomMyItemIdInfoPanel.this.configCode.setEditable(false);
          }
          if(itemPanel.selectedItemType.equals("EDAComp")||itemPanel.selectedItemType.equals("A2YTL_QDL")||itemPanel.selectedItemType.equals("A2YTL_DQL")||itemPanel.selectedItemType.equals("A2YTL_JGL")||itemPanel.selectedItemType.equals("A2YTL_FZL")
        		  ||itemPanel.selectedItemType.equals("A2YTL_BaoCai")||itemPanel.selectedItemType.equals("A2YTL_KCQDL")||itemPanel.selectedItemType.equals("A2YTL_KCDQL")||itemPanel.selectedItemType.equals("A2YTL_KCJGL")||itemPanel.selectedItemType.equals("A2YTL_KCFZL")
        		  ||itemPanel.selectedItemType.equals("A2YTL_KCBCL")){
        	  CustomMyItemIdInfoPanel.this.panel10.setVisible(true);
          }else{
        	  CustomMyItemIdInfoPanel.this.panel10.setVisible(false);
          }
          if (!StringUtil.isEmpty(twoLovs)) {
              if (!CustomMyItemIdInfoPanel.this.panel2.isVisible()) {
                CustomMyItemIdInfoPanel.this.panel2
                  .setVisible(true);
              }
              CustomMyItemIdInfoPanel.this.panel2
                .remove(CustomMyItemIdInfoPanel.this.two_text);
              CustomMyItemIdInfoPanel.this.getTwoLOVComboBox(twoLovs);
              CustomMyItemIdInfoPanel.this.panel2.add(
                "1.2.center.center", 
                CustomMyItemIdInfoPanel.this.two_text);
              CustomMyItemIdInfoPanel.this.panel2.revalidate();
            }
            else {
          	  CustomMyItemIdInfoPanel.this.isWaterCodeButtonEnabled(CustomMyItemIdInfoPanel.this.one_text);
            }
          if(CustomMyItemIdInfoPanel.this.one_text.getSelectedString().equals("010AA")){
        	  CustomMyItemIdInfoPanel.this.panel5.setVisible(false);
          }
        }
      }
    });
    onePanel.add("1.1.left.top", oneLable);
    onePanel.add("1.2.center.center", this.one_text);
    add("top.bind.left.top", onePanel);
  }

  
  public void changeLOVComponent(String itemType) {
    TCComponentListOfValues oneLovs = null;
    String lovName = null;
    if (!StringUtil.isEmpty(itemType)) {
    	if(itemType.substring(0, 2).equals("A2")){
    		String str = itemType.substring(2, itemType.length());
    		lovName="ITEMID_naming"+str+"level";
    	}else {
    		lovName="ITEMID_naming"+itemType+"level";
    	//	lovName = "ITEMID_naming_DZLlevel";
//            MessageBox.post(AIFUtility.getCurrentApplication().getDesktop(), 
//                    Locales.getString("此类型无编码，不可指派"), "提示", 2);
                }
    	
    }
    else
      MessageBox.post(AIFUtility.getCurrentApplication().getDesktop(), 
        Locales.getString("类型为null,请重新选择"), "提示", 2);
    try
    {
      oneLovs = getParentLOV1(lovName);
    }
    catch (TCException e) {
      e.printStackTrace();
    }
    this.one_text.setLOVComponent(oneLovs);
  }
  public void getTwoLOVComboBox(TCComponentListOfValues lovs) {
    this.two_text = new LOVComboBox();
    this.two_text.setLOVComponent(lovs);
    this.two_text.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
    	CustomMyItemIdInfoPanel.this.isWaterCodeButtonEnabled(CustomMyItemIdInfoPanel.this.two_text);
        CustomMyItemIdInfoPanel.this.panel3.setVisible(false);
        CustomMyItemIdInfoPanel.this.three_text.setSelectedIndex(0);
        CustomMyItemIdInfoPanel.this.panel4.setVisible(false);
        CustomMyItemIdInfoPanel.this.four_text.setSelectedIndex(0);
        CustomMyItemIdInfoPanel.this.panel5.setVisible(false);
        CustomMyItemIdInfoPanel.this.panel6.setVisible(false);
        CustomMyItemIdInfoPanel.this.six_text.setSelectedIndex(0);
        Object selectObj = CustomMyItemIdInfoPanel.this.two_text
          .getSelectedObject();
        if (!StringUtil.isEmpty(selectObj)) {
          ListOfValuesInfo li = null;
          try {
            li = CustomMyItemIdInfoPanel.this.two_text
              .getLovComponent().getListOfValues();
          } catch (TCException e) {
            e.printStackTrace();
          }
          int index = CustomMyItemIdInfoPanel.this.two_text
            .getSelectedIndex();
          TCComponentListOfValues threeLovs = li
            .getListOfFilterAtIndex(index);
          CustomMyItemIdInfoPanel.this.clearLabelWatButtonAndConButton1();
          String two = CustomMyItemIdInfoPanel.this.two_text.getSelectedString();
        if(two.equals("PCB")){
        	  CustomMyItemIdInfoPanel.this.configCodeLable.setText("片   数 ：");
        	  CustomMyItemIdInfoPanel.this.panel5.setVisible(true);
        	  CustomMyItemIdInfoPanel.this.waterCodePanel.setVisible(false);
        	  CustomMyItemIdInfoPanel.this.configCodePanel.setVisible(true);
        	  CustomMyItemIdInfoPanel.this.configCodeButton.setEnabled(true);
        	  CustomMyItemIdInfoPanel.this.configCode.setEditable(true);
          }else if(itemPanel.selectedItemType.equals("A2YTL_CPBM")){
        	  //珠海改为默认，一级菜单需要隐藏
        	  CustomMyItemIdInfoPanel.this.endCodeLable.setText("图    号：");
        	  CustomMyItemIdInfoPanel.this.panel5.setVisible(true);
        	  CustomMyItemIdInfoPanel.this.waterCodePanel.setVisible(true);
        	  CustomMyItemIdInfoPanel.this.configCodePanel.setVisible(false);
        	  CustomMyItemIdInfoPanel.this.configCodeButton.setEnabled(false);
        	  CustomMyItemIdInfoPanel.this.configCode.setEditable(false);
          } else{
        	  CustomMyItemIdInfoPanel.this.configCodeLable.setText(Locales.getString("ConfigNo") + "：");
        	  CustomMyItemIdInfoPanel.this.endCode.setText("");
        	  CustomMyItemIdInfoPanel.this.panel5.setVisible(false);
        	  CustomMyItemIdInfoPanel.this.waterCodePanel.setVisible(true); 
        	  CustomMyItemIdInfoPanel.this.configCodePanel.setVisible(false);
        	  CustomMyItemIdInfoPanel.this.configCodeButton.setEnabled(false);
        	  CustomMyItemIdInfoPanel.this.configCode.setEditable(false);
          }
          if (!StringUtil.isEmpty(threeLovs)) {
            CustomMyItemIdInfoPanel.this.clearLabelWatButtonAndConButton();
            if (!CustomMyItemIdInfoPanel.this.panel3.isVisible()) {
              CustomMyItemIdInfoPanel.this.panel3
                .setVisible(true);
            }
            CustomMyItemIdInfoPanel.this.panel3
              .remove(CustomMyItemIdInfoPanel.this.three_text);
            CustomMyItemIdInfoPanel.this
              .getThreeLOVComboBox(threeLovs);
            CustomMyItemIdInfoPanel.this.panel3.add(
              "1.2.center.center", 
              CustomMyItemIdInfoPanel.this.three_text);
            CustomMyItemIdInfoPanel.this.panel3.revalidate();
          }
        }
      } } );
  }

  private void getTwoPanel(TCComponentListOfValues lovs) {
    JLabel lable = new JLabel(Locales.getString("twoclass") + "：");
    getTwoLOVComboBox(lovs);
    this.panel2 = new JPanel(new PropertyLayout());
    this.panel2.add("1.1.left.top", lable);
    this.panel2.add("1.2.center.center", this.two_text);
    add("top.bind.left.top", this.panel2);
  }

  public void getThreeLOVComboBox(TCComponentListOfValues lovs) {
    this.three_text = new LOVComboBox();
    this.three_text.setLOVComponent(lovs);
    this.three_text.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
    	  CustomMyItemIdInfoPanel.this.isWaterCodeButtonEnabled(CustomMyItemIdInfoPanel.this.three_text);
        CustomMyItemIdInfoPanel.this.panel4.setVisible(false);
        CustomMyItemIdInfoPanel.this.four_text.setSelectedIndex(0);
        CustomMyItemIdInfoPanel.this.panel6.setVisible(false);
        CustomMyItemIdInfoPanel.this.six_text.setSelectedIndex(0);
        Object selectObj = CustomMyItemIdInfoPanel.this.three_text
          .getSelectedObject();
        if (!StringUtil.isEmpty(selectObj)) {
          ListOfValuesInfo li = null;
          try {
            li = CustomMyItemIdInfoPanel.this.three_text
              .getLovComponent().getListOfValues();
          } catch (TCException e) {
            e.printStackTrace();
          }
          int index = CustomMyItemIdInfoPanel.this.three_text
            .getSelectedIndex();
          TCComponentListOfValues fourLovs = li
            .getListOfFilterAtIndex(index);
          CustomMyItemIdInfoPanel.this.clearLabelWatButtonAndConButton1();
          
          if (!StringUtil.isEmpty(fourLovs)) {
            CustomMyItemIdInfoPanel.this.clearLabelWatButtonAndConButton();
            if (!CustomMyItemIdInfoPanel.this.panel4.isVisible()) {
              CustomMyItemIdInfoPanel.this.panel4
                .setVisible(true);
            }
            CustomMyItemIdInfoPanel.this.panel4
              .remove(CustomMyItemIdInfoPanel.this.four_text);
            CustomMyItemIdInfoPanel.this
              .getFourLOVComboBox(fourLovs);
            CustomMyItemIdInfoPanel.this.panel4.add(
              "1.2.center.center", 
              CustomMyItemIdInfoPanel.this.four_text);
            CustomMyItemIdInfoPanel.this.panel4.revalidate();
          }
        }
      } } );
  }

  private void getThreePanel(TCComponentListOfValues lovs) {
    JLabel lable = new JLabel(Locales.getString("threeclass") + "：");
    getThreeLOVComboBox(lovs);
    this.panel3 = new JPanel(new PropertyLayout());
    this.panel3.add("1.1.left.top", lable);
    this.panel3.add("1.2.center.center", this.three_text);
    add("top.bind.left.top", this.panel3);
  }

  public void getFourLOVComboBox(TCComponentListOfValues lovs) {
    this.four_text = new LOVComboBox();
    this.four_text.setLOVComponent(lovs);
    this.four_text.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
    	  CustomMyItemIdInfoPanel.this.isWaterCodeButtonEnabled(CustomMyItemIdInfoPanel.this.four_text);
    	  CustomMyItemIdInfoPanel.this.panel6.setVisible(false);
          CustomMyItemIdInfoPanel.this.six_text.setSelectedIndex(0);
        Object selectObj = CustomMyItemIdInfoPanel.this.four_text
          .getSelectedObject();
        if (!StringUtil.isEmpty(selectObj)){
        	 ListOfValuesInfo li = null;
             try {
               li = CustomMyItemIdInfoPanel.this.four_text
                 .getLovComponent().getListOfValues();
             } catch (TCException e) {
               e.printStackTrace();
             }
             int index = CustomMyItemIdInfoPanel.this.four_text
               .getSelectedIndex();
             TCComponentListOfValues sixLovs = li
               .getListOfFilterAtIndex(index);
             CustomMyItemIdInfoPanel.this.clearLabelWatButtonAndConButton1();
             if (!StringUtil.isEmpty(sixLovs)) {
               CustomMyItemIdInfoPanel.this.clearLabelWatButtonAndConButton();
               if (!CustomMyItemIdInfoPanel.this.panel6.isVisible()) {
                 CustomMyItemIdInfoPanel.this.panel6
                   .setVisible(true);
               }
               CustomMyItemIdInfoPanel.this.panel6
                 .remove(CustomMyItemIdInfoPanel.this.six_text);
               CustomMyItemIdInfoPanel.this
                 .getSixLOVComboBox(sixLovs);
               CustomMyItemIdInfoPanel.this.panel6.add(
                 "1.2.center.center", 
                 CustomMyItemIdInfoPanel.this.six_text);
               CustomMyItemIdInfoPanel.this.panel6.revalidate();
             }
        }
      } } );
  }

  private void getFourPanel(TCComponentListOfValues lovs) {
    JLabel lable = new JLabel(Locales.getString("fourclass") + "：");
    getFourLOVComboBox(lovs);
    this.panel4 = new JPanel(new PropertyLayout());
    this.panel4.add("1.1.left.top", lable);
    this.panel4.add("1.2.center.center", this.four_text);
    add("top.bind.left.top", this.panel4);
  }
  
  public void getSixLOVComboBox(TCComponentListOfValues lovs) {
	    this.six_text = new LOVComboBox();
	    this.six_text.setLOVComponent(lovs);
	    this.six_text.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent arg0) {
	    	  CustomMyItemIdInfoPanel.this.isWaterCodeButtonEnabled(CustomMyItemIdInfoPanel.this.six_text);
	    	  	        Object selectObj = CustomMyItemIdInfoPanel.this.six_text
	          .getSelectedObject();
	        if (!StringUtil.isEmpty(selectObj)){
	        	CustomMyItemIdInfoPanel.this.clearLabelWatButtonAndConButton1();
	        	CustomMyItemIdInfoPanel.this.six_text.getSelectedString(); 
	        }
	      } } );
	  }
  
  private void getSixPanel(TCComponentListOfValues lovs) {
	    JLabel lable = new JLabel("五级分类"+ "：");
	    getSixLOVComboBox(lovs);
	    this.panel6 = new JPanel(new PropertyLayout());
	    this.panel6.add("1.1.left.top", lable);
	    this.panel6.add("1.2.center.center", this.six_text);
	    add("top.bind.left.top", this.panel6);
	  }
  
  private TCComponentListOfValues getParentLOV() throws TCException {
    TCPreferenceService service = this.session.getPreferenceService();
    String itemidPerfer = service.getStringValue("ITEMID_naming_onelevel");
    if (StringUtil.isEmpty(itemidPerfer)) {
      return null;
    }
    TCComponentListOfValuesType lovType = (TCComponentListOfValuesType)this.session
      .getTypeComponent("ListOfValues");
    TCComponentListOfValues[] lovs = lovType.find(itemidPerfer);
    System.out.println(lovs[0]);
    if ((StringUtil.isEmpty(lovs)) || (lovs.length == 0)) {
      return null;
    }
    return lovs[0];
  }

  private TCComponentListOfValues getParentLOV1(String name) throws TCException {
    TCPreferenceService service = this.session.getPreferenceService();
    String itemidPerfer = service.getStringValue(name);
    if (StringUtil.isEmpty(itemidPerfer)) {
      return null;
    }
    TCComponentListOfValuesType lovType = (TCComponentListOfValuesType)this.session
      .getTypeComponent("ListOfValues");
    TCComponentListOfValues[] lovs = lovType.find(itemidPerfer);
    if ((StringUtil.isEmpty(lovs)) || (lovs.length == 0)) {
      return null;
    }
    return lovs[0];
  }

  public String getPageTitle()
  {
    return Locales.getString("itemIdInfoStep.LABEL");
  }

  public Icon getPageIcon() {
    String str = getClass().getName() + "." + "ICON";
    ImageIcon localImageIcon = this.packageReg.getImageIcon(str, null);
    if (localImageIcon == null)
      localImageIcon = this.packageReg.getImageIcon("info.ICON");
    return localImageIcon;
  }

  public void setPanelDisplay(boolean paramBoolean) {
  }

  public String getControlName() {
    return Locales.getString("itemIdInfoStep.NAME");
  }

  public String getToolTip() {
    return Locales.getString("itemIdInfoStep.TOOLTIP");
  }

  @SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })private void waterCodeButtonFunction() {//指派按钮方法
    String one = this.one_text.getSelectedString();
    String two = this.two_text.getSelectedString();
    String three = this.three_text.getSelectedString();
    String four = this.four_text.getSelectedString();
    String six = this.six_text.getSelectedString();
    String end_Code = this.endCode.getText();
    String num_Code = this.numCode.getText();
    String centerModelCode_Code = this.centerModelCode.getText();
    //String smallModelCode_Code = this.smallModelCode.getText();
    String code;
    int count=0;
    String code_prefix = null;//获取所有级分类
      code_prefix = six;
      if (StringUtil.isEmpty(six)){
    	  code_prefix=four;
      }else{
    	  code_prefix=four;
      }
      if (StringUtil.isEmpty(code_prefix)){
    	  code_prefix=three;
      }
      if (StringUtil.isEmpty(code_prefix)){
    	  code_prefix=two;
      }
      if (StringUtil.isEmpty(code_prefix)){
    	  code_prefix=one;
      }
      
    if(itemPanel.selectedItemType.equals("A2YTL_CPBM")){
    	code_prefix="I"+one+two+end_Code;
    }
    
    if(itemPanel.selectedItemType.equals("A2YTL_PCBABCP")){
    	if(one.isEmpty()){
    		one="A";
    	}
    	if(this.one_text.getSelectedString().isEmpty()){
    		this.one_text.setText("珠海"); 
    	}
    	code_prefix="05"+one+end_Code;
    }
    
    if(itemPanel.selectedItemType.equals("A2YTL_ZZBCP")){
    	code_prefix="9"+one+end_Code+num_Code;
    }
    if(itemPanel.selectedItemType.equals("A2YTL_BJBM")){
    	code_prefix=end_Code+"-B";
    	if(code_prefix.equals("")){
    		code_prefix="IAA";
    		JOptionPane.showMessageDialog(null, "成品编码不正确");
			this.waterCodeButton.setEnabled(true);
			this.itemPanel.nextButton.setEnabled(false);
			this.waterCode.setText("");
			this.idTextField.setText("");
    	}
    }
    if(itemPanel.selectedItemType.equals("A2YTL_JJJL")){//机加件
//    	if(!centerModelCode_Code.equals("")&&!smallModelCode_Code.equals("")){
//    		if (validateModelCode(centerModelCode_Code, smallModelCode_Code)) {
//    			code_prefix="5"+code_prefix+end_Code+centerModelCode_Code+smallModelCode_Code;
//    		}
//    	}else if(!centerModelCode_Code.equals("")&&smallModelCode_Code.equals("")){
//    		if (validateModelCode(centerModelCode_Code, "11")) 
//    			code_prefix="5"+code_prefix+end_Code+centerModelCode_Code+"00";
//    	}else if(centerModelCode_Code.equals("")&&!smallModelCode_Code.equals("")){
//    		if (validateModelCode("11", smallModelCode_Code)) {
//    			code_prefix="5"+code_prefix+end_Code+"00"+smallModelCode_Code;
//    		}
//    	}else{
//    		code_prefix="5"+code_prefix+end_Code+"00"+"00";
//    	}
    	if(!centerModelCode_Code.equals("")){
    		if (validateModelCode(centerModelCode_Code)) {
    			code_prefix="5"+code_prefix+end_Code+centerModelCode_Code;
    		}
    	}else{
    		code_prefix="5"+code_prefix+end_Code+"00"+"00";
    	}
    }
    if(itemPanel.selectedItemType.equals("A2YTL_JJBZJ")){//机加标准件
//    	if(!centerModelCode_Code.equals("")&&!smallModelCode_Code.equals("")){
//    		if (validateModelCode(centerModelCode_Code, smallModelCode_Code)) {
//    			code_prefix="5"+code_prefix+end_Code+centerModelCode_Code+smallModelCode_Code;
//    		}
//    	}else if(!centerModelCode_Code.equals("")&&smallModelCode_Code.equals("")){
//    		if (validateModelCode(centerModelCode_Code, "11")) 
//    			code_prefix="5"+code_prefix+end_Code+centerModelCode_Code+"00";
//    	}else if(centerModelCode_Code.equals("")&&!smallModelCode_Code.equals("")){
//    		if (validateModelCode("11", smallModelCode_Code)) {
//    			code_prefix="5"+code_prefix+end_Code+"00"+smallModelCode_Code;
//    		}
//    	}else{
//    		code_prefix="5"+code_prefix+end_Code+"00"+"00";
//    	}
    	if(!centerModelCode_Code.equals("")){
    		if (validateModelCode(centerModelCode_Code)) {
    			code_prefix="4"+code_prefix+end_Code+centerModelCode_Code;
    		}
    	}else{
    		code_prefix="4"+code_prefix+end_Code+"00"+"00";
    	}
    }
    if(itemPanel.selectedItemType.equals("A2YTL_WDBM")){//IA-WI-部门代号-01
    	code_prefix="IA-"+one;
    	if(!StringUtil.isEmpty(two)){
    		code_prefix=code_prefix+"-"+two;
    	}
    }
    List item_ids = new ArrayList();
    Boolean bb=true;
    if(this.itemPanel.selectedItemType.equals("A2YTL_SCLY")||this.itemPanel.selectedItemType.equals("A2YTL_JJJL")||this.itemPanel.selectedItemType.equals("A2YTL_JJBZJ")
    		||itemPanel.selectedItemType.equals("A2YTL_CPBM")||itemPanel.selectedItemType.equals("A2YTL_ZZBCP")||itemPanel.selectedItemType.equals("A2YTL_WDBM")
    		||this.itemPanel.selectedItemType.equals("A2YTL_BJBM")||this.itemPanel.selectedItemType.equals("A2YTL_PCBABCP")){
    	bb=false;
    }
    if(this.itemPanel.selectedItemType.substring(0, 2).equals("A2")){
    	if(this.itemPanel.selectedItemType.substring(0, 8).equals("A2YTL_KC")){
    		code_prefix="T"+code_prefix;
    	}
    }
    String[] suppliers=null;
    boolean hasSupplier=false;
    if(itemPanel.selectedItemType.equals("A2YTL_QDL")
    		||itemPanel.selectedItemType.equals("A2YTL_DQL")
    		||itemPanel.selectedItemType.equals("EDAComp")
    		||itemPanel.selectedItemType.equals("A2YTL_JGL")
    		||itemPanel.selectedItemType.equals("A2YTL_FZL")
    		||itemPanel.selectedItemType.equals("A2YTL_BaoCai")
    		||itemPanel.selectedItemType.equals("A2YTL_KCQDL")
    		||itemPanel.selectedItemType.equals("A2YTL_KCDQL")
    		||itemPanel.selectedItemType.equals("A2YTL_KCJGL")
    		||itemPanel.selectedItemType.equals("A2YTL_KCFZL")
    		||itemPanel.selectedItemType.equals("A2YTL_KCBCL")){
      	String supplier = this.supplierCode.getText();
      	hasSupplier=true;
      	if(StringUtil.isEmpty(supplier)){
      		suppliers= new String[2];
      		suppliers[0]="-0";
      		suppliers[1]="";
      	}else{
      		if(Pattern.compile("^[a-z]{2}$").matcher(supplier).matches()){
      			suppliers=new ConnectFactory().getFirmByNum(supplier.toUpperCase(), "", findTableByItemType(itemPanel.selectedItemType));
      		}else{
      			suppliers=new ConnectFactory().getFirmByNum(null, supplier, findTableByItemType(itemPanel.selectedItemType));
      		}
      	}
      }
    
    try {
//    	if(itemPanel.selectedItemType.equals("A2YTL_CPBM")){
//    		sql="SELECT * FROM PITEM WHERE PITEM_ID LIKE '%"+code_prefix+"%"+"' order by PITEM_ID asc";
//					itemid2=u.getResult(sql);
//    	}
      TCComponent[] components = this.session.getClassService().findByClass("Item", "item_id", code_prefix + "*"+"-0");
    	
      if(!bb){
    	  components = this.session.getClassService().findByClass("Item", "item_id", code_prefix + "*");
      }
      if(hasSupplier){
    	  components = this.session.getClassService().findByClass("Item", "item_id", code_prefix +"*" +suppliers[0]);
      }
      if(itemPanel.selectedItemType.equals("A2YTL_CPBM")){
  		components = this.session.getClassService().findByClass("A2YTL_CPBM", "item_id", code_prefix + "*");
  	  }else if(itemPanel.selectedItemType.equals("A2YTL_BJBM")){
  		components = this.session.getClassService().findByClass("A2YTL_BJBM", "item_id", code_prefix + "*");
  	  }
      /*if(this.itemPanel.selectedItemType.equals("A2YTL_JJJL")){
    	  components = this.session.getClassService()
  		        .findByClass("Item", "item_id", code_prefix + "*");
      }*/
      for (TCComponent component : components) {
        String item_id = component.getTCProperty("item_id").getStringValue();
        item_ids.add(item_id);
      }
    }
    catch (TCException e) {
      e.printStackTrace();
    }
    if (item_ids.size() == 0) {
    	if(bb){
    		code = "00001";
    	}else{
    		code="0000001";
    	}
    	if(itemPanel.selectedItemType.equals("A2YTL_WDBM")){
    		code="01";
    	}
    	if(itemPanel.selectedItemType.equals("A2YTL_ZZBCP")){
    		code="";
    	}
    	if(itemPanel.selectedItemType.equals("A2YTL_CPBM")){
    		code="00";
    	}
    	if(itemPanel.selectedItemType.equals("A2YTL_JJJL")){
    		code="001";
    	}
    	if(itemPanel.selectedItemType.equals("A2YTL_JJBZJ")){
    		code="001";
    	}
    	if(itemPanel.selectedItemType.equals("A2YTL_BJBM")){
    		code="01";
    	}
    	if(itemPanel.selectedItemType.equals("A2YTL_PCBABCP")){
    		code="01";
    	}
//      this.waterCode.setText(code);
//      this.idTextField.setText(code_prefix +"-"+ code);
    } else {
      String[] names = (String[])item_ids.toArray(new String[0]);
      Arrays.sort(names);

      String name = maxArraysIsNum(names, Boolean.valueOf(true));
      code = "";
      String max = "";
      if(itemPanel.selectedItemType.equals("A2YTL_ZZBCP")){
    	  max="";
    	  code="";
      }
      if (!StringUtil.isEmpty(name)) {
        max = ((java.lang.String) String).valueOf(Integer.parseInt(name.replaceAll("^" + 
          code_prefix, "")) +1);
//        max = String.valueOf(Integer.parseInt(name.replaceAll("^" + 
//                code_prefix, "")) );
        int num=5;
        if(!bb){
        	num=7;
        }
        if(itemPanel.selectedItemType.equals("A2YTL_PCBABCP")||itemPanel.selectedItemType.equals("A2YTL_BJBM")||itemPanel.selectedItemType.equals("A2YTL_CPBM")||itemPanel.selectedItemType.equals("A2YTL_ZZBCP")||itemPanel.selectedItemType.equals("A2YTL_WDBM")){
        	num=2;
        }
        if(itemPanel.selectedItemType.equals("A2YTL_JJJL")){
        	num=3;
        }
        if(itemPanel.selectedItemType.equals("A2YTL_JJBZJ")){
        	num=3;
        }
        for (int i = 0; i < num - max.length(); i++)
          code = code + "0";
      }else {
    	 if(bb){
    		 code = "00001";
    	 }else{
    		 code = "0000001";
    	 }
    	 if(itemPanel.selectedItemType.equals("A2YTL_CPBM")||itemPanel.selectedItemType.equals("A2YTL_WDBM")){
    		 code="01";
    	 }
    	 if(itemPanel.selectedItemType.equals("A2YTL_ZZBCP")){
    		 code="";
    	 }
    	 if(itemPanel.selectedItemType.equals("A2YTL_JJJL")){
    		 code="001";
    	 }
    	 if(itemPanel.selectedItemType.equals("A2YTL_JJBZJ")){
    		 code="001";
    	 }
      }
//      if(itemPanel.selectedItemType.equals("A2YTL_BJBM")){
//    	  max=count+"";
//    	  if(count<10){
//    		  code="0";
//    	  }
//      }
      code = code + max;
    }
    this.waterCode.setText(code);
    if(code==""){
    	this.idTextField.setText(code_prefix +"-"+ code+"-0");
    }
    this.idTextField.setText(code_prefix +"-"+ code+"-0");
    this.idTextField.setText(code_prefix +"-"+ code);
    this.itemPanel.nextButton.setEnabled(true);
    if(itemPanel.selectedItemType.equals("A2YTL_CPBM")){
    	if(validateNum(end_Code,"12")){
    		this.idTextField.setText(code_prefix + code);
    	}
    }
    if(itemPanel.selectedItemType.equals("A2YTL_PCBABCP")){
    	if(validateNum(end_Code,code)){
    		if(code.length()==1){
    			code="0"+code;
    		}
    		if(code.isEmpty()){
    			code="01";
    		}
    		if(one.isEmpty()){
    			one="A";
    			this.one_text.setText("珠海"); 
    		}
    		code_prefix="05"+one+end_Code+code;
    		//String s=getSelectCPCode().substring(4, 12);
    		this.configCode.setText(code); 
    		this.idTextField.setText(code_prefix);
    	}
    }
    if(itemPanel.selectedItemType.equals("A2YTL_ZZBCP")){
    	if(code.equals("02")){
    		JOptionPane.showMessageDialog(null, "图号或装配号已存在,请使用修订");
			this.waterCodeButton.setEnabled(true);
			this.itemPanel.nextButton.setEnabled(false);
			this.waterCode.setText("");
			this.idTextField.setText("");
    	}else{
    		if(validateNum(end_Code,num_Code)){
        		this.waterCode.setText("");
        		this.idTextField.setText(code_prefix);
        	}
    	}
    }
    if(itemPanel.selectedItemType.equals("A2YTL_JJJL")){
    	if(validateNum(end_Code,"11")){
    		this.idTextField.setText(code_prefix + code);
    	}
    }
    if(itemPanel.selectedItemType.equals("A2YTL_JJBZJ")){
    	if(validateNum(end_Code,"11")){
    		this.idTextField.setText(code_prefix + code);
    	}
    }
    if(itemPanel.selectedItemType.equals("A2YTL_BJBM")){
    	if(Pattern.compile("^[A-Z]{3}[0-9]{7}-B$").matcher(code_prefix).matches()||Pattern.compile("^[0-9]{1}[A-Z]{1}[0-9]{5}[0-9,A-Z]{2}[0-9]{2}-B$").matcher(code_prefix).matches()){
    	   this.waterCode.setText("B"+code);
    	   this.idTextField.setText(code_prefix+code);
    	   this.itemPanel.nextButton.setEnabled(true);
        }else{
        	JOptionPane.showMessageDialog(null, "请输入10位成品编码");
			this.waterCodeButton.setEnabled(true);
			this.itemPanel.nextButton.setEnabled(false);
			this.waterCode.setText("");
			this.idTextField.setText("");
        }
    }
    if(hasSupplier){
    	if(StringUtil.isEmpty(this.supplierCode.getText())){
    		this.idTextField.setText(code_prefix +"-"+ code+"-0");
//    		this.idTextField.setText(code_prefix +"-"+ code);
    		this.supplierCode.setEnabled(false);
    	}else{
    		if(!StringUtil.isEmpty(suppliers[0])){
    			this.supplierCode.setEnabled(false);
    			this.supplierCode.setText(suppliers[1]);
    			this.idTextField.setText(code_prefix +"-"+ code+suppliers[0]);
    		}else{
    			JOptionPane.showMessageDialog(null, "无效的供应商编号");
    			this.waterCodeButton.setEnabled(true);
    			this.itemPanel.nextButton.setEnabled(false);
    			this.waterCode.setText("");
    			this.idTextField.setText("");
    		}
    	}
    }
  }

  private String findTableByItemType(String itemType){
	  if(itemType=="EDAComp"||itemType.equals("EDAComp"))
		  itemType="A2YTL_DZL";
	  String ss=itemType;
	  if(Pattern.compile("A2YTL_KC").matcher(itemType.subSequence(0, 8)).matches()){
		  ss=itemType.substring(0, 6)+itemType.substring(8, itemType.length());
	  }
	  return ss;
  }
  private void configCodeButtonFunction() {
    String one = this.one_text.getSelectedString();
    String two = this.two_text.getSelectedString();
    String three = this.three_text.getSelectedString();
    String four = this.four_text.getSelectedString();
    String six = this.six_text.getSelectedString();
    String waterCode = this.waterCode.getText();
    String end_Code = this.endCode.getText();
    String config_Code=this.configCode.getText();
    String code ="0";
    String code_prefix = six;
    if (StringUtil.isEmpty(six)){
  	  code_prefix=four; 
    }
    if (StringUtil.isEmpty(four)){
    	  code_prefix=three; 
      }
    if (StringUtil.isEmpty(three)){
  	  code_prefix=two; 
    }
    if (StringUtil.isEmpty(two)){
  	  code_prefix=one;  
    }
    if(!StringUtil.isEmpty(two)&&two.equals("PCB")){
    	if(validateNum(end_Code,config_Code)){
    		if(config_Code.length()==1){
    			config_Code="0"+config_Code;
    		}
    		this.idTextField.setText(code_prefix+end_Code+config_Code);
    	}
    }else if(itemPanel.selectedItemType.equals("A2YTL_PCBABCP")){
    	if(validateNum(end_Code,config_Code)){
    		if(config_Code.length()==1){
    			config_Code="0"+config_Code;
    		}
    		if(config_Code.isEmpty()){
    			config_Code="01";
    		}
    		if(one.isEmpty()){
    			one="A";
    			this.one_text.setText("珠海"); 
    		}
    		code_prefix="05"+one+end_Code+config_Code;
    		//String s=getSelectCPCode().substring(4, 12);
    		this.configCode.setText(config_Code); 
    		this.idTextField.setText(code_prefix);
    	}
    }else{
    	if(itemPanel.selectedItemType.equals("A2YTL_PCBABCP")){
    		this.configCode.setText(code); 
            this.idTextField.setText(code_prefix+waterCode+code);
            this.itemPanel.nextButton.setEnabled(true);
    	}else{
    	this.configCode.setText("-"+code); 
        this.idTextField.setText(code_prefix+"-"+waterCode+"-"+code);
        this.itemPanel.nextButton.setEnabled(true);
       /* if(itemPanel.selectedItemType.equals("A2YTL_CPBM")){
        	this.idTextField.setText(code_prefix +waterCode+"-"+code);
        }*/
    }
    }
  }
 
  /**
   * 验证中模小
   * @param s1
   * @param s2
   * @return
   */
  public boolean validateModelCode(String s1){
	  String ss="装配号格式为：0001,A001或者AA01";
	  //String ss1="小模请输入两个0-9的数字！";
	  try {
	      if (!Pattern.compile("^[A-Z,0-9]{3}[0-9]{1}$").matcher(s1).matches()){
	    	  this.waterCodeButton.setEnabled(true);
	    	  this.itemPanel.nextButton.setEnabled(false);
	    	  this.waterCode.setText("");
    	      this.idTextField.setText("");
	    	  throw new NumberFormatException();
	      }
	    }
	    catch (NumberFormatException localNumberFormatException) {
	      JOptionPane.showMessageDialog(null, ss);
	      return false;
	    }
//	  try {
//	      if (!Pattern.compile("^[0-9]{2}$").matcher(s2).matches()){
//	    	  this.waterCodeButton.setEnabled(true);
//	    	  this.itemPanel.nextButton.setEnabled(false);
//	    	  this.waterCode.setText("");
//    	      this.idTextField.setText("");
//	    	  throw new NumberFormatException();
//	      }
//	    }
//	    catch (NumberFormatException localNumberFormatException) {
//	      JOptionPane.showMessageDialog(null, ss1);
//	      return false;
//	    }
	 this.waterCode.setEnabled(false);
    this.itemPanel.nextButton.setEnabled(true);
    return true;
  }
  
  /**
   * 验证选择的是Item版本
   * @return
   */
  public String getSelectCPCode(){
	  InterfaceAIFComponent[] targets=null;
	  TCComponent root = null;
	  String ItemId="";
	  targets=AIFUtility.getCurrentApplication().getTargetComponents();
	  if ((targets != null) && (targets.length != 0)) {
		  for (InterfaceAIFComponent target : targets) {
			  root = (TCComponent)target;
			  if ((root instanceof TCComponentItemRevision)||(root instanceof TCComponentItem))
			  try {
				ItemId=root.getStringProperty("item_id");
				if(!ItemId.subSequence(0, 2).equals("IA")){
					MessageBox.post("选择目标不正确，请选择成品编码的item版本", "消息", 2);
					return null;
				}
			} catch (TCException e) {
				e.printStackTrace();
			}
			  else{
				  MessageBox.post("选择目标不正确，请选择成品编码的item版本", "消息", 2);
			  }
		  }
	  }else{
		  MessageBox.post("出现错误", "消息", 2);
	  }
	  return ItemId;
  }
  
  
  public String maxArraysIsNum(String[] strs, Boolean isWaterCon) {
    String max = "";
    for (int i = strs.length - 1; i >= 0; i--) {
      if (isWaterCon.booleanValue()) {
    	  String str = strs[i].substring(6, strs[i].length() - 2);
    	  if(this.itemPanel.selectedItemType.equals("A2YTL_SCLY")){
    		  str = strs[i].substring(6, strs[i].length());
    	    }
    	  if(this.itemPanel.selectedItemType.equals("A2YTL_BJBM")||this.itemPanel.selectedItemType.equals("A2YTL_PCBABCP")){
    		  str = strs[i].substring(strs[i].length()-2, strs[i].length());
    	    }
    	  if(itemPanel.selectedItemType.equals("A2YTL_CPBM")||itemPanel.selectedItemType.equals("A2YTL_WDBM")){
    		  str = strs[i].substring(strs[i].length()-2, strs[i].length());
    	  }
    	  if(itemPanel.selectedItemType.equals("A2YTL_ZZBCP")){
    		  str = strs[i].substring(strs[i].length()-2, strs[i].length());
    	  }
    	  if(this.itemPanel.selectedItemType.equals("A2YTL_JJJL")){
    		  str = strs[i].substring(strs[i].length()-3, strs[i].length());
    	  }
    	  if(this.itemPanel.selectedItemType.equals("A2YTL_JJBZJ")){
    		  str = strs[i].substring(strs[i].length()-3, strs[i].length());
    	  }
    	  if(this.itemPanel.selectedItemType.substring(0, 2).equals("A2")){
    		  if(this.itemPanel.selectedItemType.substring(0, 8).equals("A2YTL_KC")){
    			  str = strs[i].substring(7, strs[i].length() - 2);
    		  }
    	  }
        if (isNum(str).booleanValue()) {
        	max = str;
          break;
        }
      }
      else if (isNum(strs[i]).booleanValue()) {
        max = strs[i];
        break;
      }
    }

    return max;
  }
  /**
   * 验证3位数字，两位字母
   * @param str
   * @return
   */
  public Boolean isNum(String str) {
   // Pattern pattern = Pattern.compile("^[0-9]*$");
    Pattern pattern = Pattern.compile("^[0-9]{3}[A-Z]{2}[0-9]*$");
    Matcher match = pattern.matcher(str);
   /* if (!match.matches())
    {
      return Boolean.valueOf(false);
    }*/

    return Boolean.valueOf(true);
  }
  
  /**
   * 验证是五位数字！
   * @param s
   * @return
   */
  public boolean validateNumber(String s) {
    try {
      Integer.parseInt(s);
      if (s.length() != 5)
        throw new NumberFormatException();
    }
    catch (NumberFormatException localNumberFormatException) {
      JOptionPane.showMessageDialog(null, "流水号请输入五位数字!");
      clearTextField();
      return false;
    }
    this.itemPanel.nextButton.setEnabled(true);
    return true;
  }
  
  /**
   * 验证s是大写字母或者两位数字
   * @param s
   * @return
   */
  public boolean validateNum(String s){
	  Pattern pattern = Pattern.compile("^[A-Z]{1}$");
	  Matcher match = pattern.matcher(s);
	  try {
	      if(StringUtil.isEmpty(s)||!match.matches()){
	    	  this.waterCodeButton.setEnabled(true);
	    	  this.itemPanel.nextButton.setEnabled(false);
	    	  this.waterCode.setText("");
    	      this.idTextField.setText("");
	    	  throw new NumberFormatException();
	      }
	    }
	    catch (NumberFormatException localNumberFormatException) {
	      JOptionPane.showMessageDialog(null, "请选择产品组合!");
	      return false;
	    }
	  this.itemPanel.nextButton.setEnabled(true);
	  return true;
  }
  
  /**
   * 验证成品编码和片数
   * @param s
   * @param s1
   * @return
   */
  public boolean validateNum(String s,String s1) {
	  Pattern pattern = Pattern.compile("^[0-9,A-Z]{6}$");
	  String ss="成品编码请输入六位数字或大写字母!";
	  String ss1="片数请输入两位数字!";
	  if("A2YTL_CPBM".equals(itemPanel.selectedItemType)||itemPanel.selectedItemType.equals("A2YTL_ZZBCP")){
		  pattern = Pattern.compile("^[0-9]{5}$");
		  ss="图号格式'年'+'第几张'如：14251";
		  ss1="装配号格式为：0001,A001或者AA01";
	  }
	  if(itemPanel.selectedItemType.equals("A2YTL_PCBABCP")){
		  pattern = Pattern.compile("^[0-9]{5}$");
		  ss="图号格式'年'+'第几张'如：14251";
		  ss1="板序号请输入两位数字!";
//		  if(StringUtil.isEmpty(this.one_text.getSelectedString())){
//			  pattern = Pattern.compile("^[0-9,A-Z]{9999999}$");
//			  ss="请选择一级分类!";
//		  }
	  }
	  if(itemPanel.selectedItemType.equals("A2YTL_BJBM")){
		  pattern = Pattern.compile("^[A-Z]{3}[0-9]{7}$");
		  ss="请输入10位成品编码";
//		  if(StringUtil.isEmpty(this.one_text.getSelectedString())){
//			  pattern = Pattern.compile("^[0-9,A-Z]{9999999}$");
//			  ss="请选择一级分类!";
//		  }
	  }
	  if(itemPanel.selectedItemType.equals("A2YTL_JJJL")){
		  pattern = Pattern.compile("^[0-9]{5}$");
		  ss="图号格式'年'+'第几张'如：14251";
		  ss1="流水码请输入两位数字!";
	  }
	  if(itemPanel.selectedItemType.equals("A2YTL_JJBZJ")){
		  pattern = Pattern.compile("^[0-9]{5}$");
		  ss="图号格式'年'+'第几张'如：14251";
		  ss1="流水码请输入两位数字!";
	  }
	   Matcher match = pattern.matcher(s);
	    try {
//	      Integer.parseInt(s);
//	      if (s.length() != 5)
	      if(!match.matches()){
	    	  this.waterCodeButton.setEnabled(true);
	    	  this.itemPanel.nextButton.setEnabled(false);
	    	  this.waterCode.setText("");
    	      this.idTextField.setText("");
	    	  throw new NumberFormatException();
	      }
	    }
	    catch (NumberFormatException localNumberFormatException) {
	      JOptionPane.showMessageDialog(null, ss);
	      return false;
	    }
	    if(itemPanel.selectedItemType.equals("A2YTL_ZZBCP")){
			  ss1="装配号格式为：0001,A001或者AA01";
			  if (!Pattern.compile("^[A-Z,0-9]{3}[0-9]{1}$").matcher(s1).matches()){
				  if(s1.isEmpty()){
					  ss1 = "装配号不能为空！";
				  }
		    	  this.waterCodeButton.setEnabled(true);
		    	  this.itemPanel.nextButton.setEnabled(false);
		    	  this.waterCode.setText("");
	    	      this.idTextField.setText("");
	    	      JOptionPane.showMessageDialog(null, ss1);
			      return false;
		      }else{
		    	  return true;
		      }
		  }
//	    try {
//		      if (!Pattern.compile("^[0-9]{4}$").matcher(s1).matches()){
//		    	  this.waterCodeButton.setEnabled(true);
//		    	  this.itemPanel.nextButton.setEnabled(false);
//		    	  this.waterCode.setText("");
//	    	      this.idTextField.setText("");
//		    	  throw new NumberFormatException();
//		      }
//		    }
//		    catch (NumberFormatException localNumberFormatException) {
//		      JOptionPane.showMessageDialog(null, ss1);
//		      return false;
//		    }
	    this.itemPanel.nextButton.setEnabled(true);
	    return true;
	  }

  public void clearTextField() {
    this.configCodeButton.setEnabled(false);
    this.idTextField.setText("");
    this.waterCode.setText("");
    this.configCode.setText("");
  }

  public void isWaterCodeButtonEnabled(LOVComboBox textLOV) {
    if (textLOV.getSelectedIndex() != -1) {
      this.waterCodeButton.setEnabled(true);
      this.waterCode.setEditable(true);
    }
 
  }

  public void clearLabelWatButtonAndConButton()
  {
    this.waterCodeButton.setEnabled(false);
    this.waterCode.setEditable(false);
    this.waterCode.setText("");
    this.configCode.setText("");
    this.idTextField.setText("");
    this.itemPanel.nextButton.setEnabled(false);
  }
  public void clearLabelWatButtonAndConButton1()
  {
    this.waterCode.setEditable(false);
    this.waterCode.setText("");
    this.configCode.setText("");
    this.idTextField.setText("");
    this.itemPanel.nextButton.setEnabled(false);
  }
}