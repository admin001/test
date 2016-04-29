package com.teamcenter.rac.commands.newitem;

public class CustomMyNewItemOperation extends NewItemOperation
{
  public CustomMyNewItemOperation(CustomMyNewItemDialog paramNewItemDialog)
  {
    super(paramNewItemDialog);
  }

  public CustomMyNewItemOperation(NewItemCommand paramNewItemCommand) {
    super(paramNewItemCommand);
  }

  public CustomMyNewItemOperation()
  {
  }
}