package com.teamcenter.rac.commands.newitem;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

public class CustomMyItemInfoPanel extends ItemInfoPanel
{
  AIFDesktop desktop;

  public CustomMyItemInfoPanel(Frame paramFrame, TCSession paramTCSession, CustomMyNewItemPanel paramNewItemPanel)
  {
    super(paramFrame, paramTCSession, paramNewItemPanel);
    this.desktop = AIFUtility.getCurrentApplication().getDesktop();
  }

  public CustomMyItemInfoPanel(TCSession paramTCSession, CustomMyNewItemPanel paramNewItemPanel)
  {
    super(paramTCSession, paramNewItemPanel);
    this.desktop = AIFUtility.getCurrentApplication().getDesktop();
  }

  public CustomMyItemInfoPanel(Frame paramFrame, TCSession paramTCSession, CustomMyNewItemPanel paramNewItemPanel, boolean paramBoolean)
  {
    super(paramFrame, paramTCSession, paramNewItemPanel, paramBoolean);
    this.desktop = AIFUtility.getCurrentApplication().getDesktop();
  }

  public CustomMyItemInfoPanel(Frame paramFrame, TCSession paramTCSession, CustomMyNewItemPanel paramNewItemPanel, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3)
  {
    super(paramFrame, paramTCSession, paramNewItemPanel, paramBoolean1, 
      paramBoolean2, paramBoolean3);
    this.desktop = AIFUtility.getCurrentApplication().getDesktop();
  }

  public void buildBaseItemInfoPanel()
  {
    super.buildBaseItemInfoPanel();
    /*ActionListener actionListener = this.assignButton.getActionListeners()[0];
    this.assignButton.removeActionListener(actionListener);
    this.assignButton.addActionListener(new ActionListener()
    {
      public void actionPerformed(ActionEvent paramActionEvent) {
        CustomMyItemInfoPanel.this.itemPanel.back();
      }
    });*/
  }
}