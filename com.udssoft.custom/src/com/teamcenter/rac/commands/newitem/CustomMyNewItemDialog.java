package com.teamcenter.rac.commands.newitem;

import com.teamcenter.rac.common.DialogForBOMLine;
import java.awt.Frame;

public class CustomMyNewItemDialog extends NewItemDialog
{
  public CustomMyNewItemDialog(NewItemCommand paramNewItemCommand)
  {
    super(paramNewItemCommand);
  }

  public CustomMyNewItemDialog(NewItemCommand paramNewItemCommand, Boolean paramBoolean)
  {
    super(paramNewItemCommand, paramBoolean);
  }

  public CustomMyNewItemDialog(Frame paramFrame, boolean paramBoolean)
  {
    super(paramFrame, paramBoolean);
  }

  public CustomMyNewItemDialog(DialogForBOMLine paramDialogForBOMLine)
  {
    super(paramDialogForBOMLine);
  }
}