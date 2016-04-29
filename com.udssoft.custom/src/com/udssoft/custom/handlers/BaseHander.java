package com.udssoft.custom.handlers;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aif.AbstractAIFUIApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCSession;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

public class BaseHander extends AbstractHandler
{
  protected AbstractAIFApplication app;
  protected AIFDesktop deskTop;
  protected TCSession session;

  public BaseHander()
  {
    this.app = AIFUtility.getCurrentApplication();
    this.deskTop = AIFUtility.getCurrentApplication().getDesktop();
    this.session = ((TCSession)this.app.getSession());
  }

  public Object execute(ExecutionEvent arg0) throws ExecutionException {
    return null;
  }
}