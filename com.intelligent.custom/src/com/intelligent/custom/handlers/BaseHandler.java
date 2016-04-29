package com.intelligent.custom.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.teamcenter.rac.aif.AIFDesktop;
import com.teamcenter.rac.aif.AbstractAIFApplication;
import com.teamcenter.rac.aifrcp.AIFUtility;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class BaseHandler extends AbstractHandler {

	protected AbstractAIFApplication app;
	protected AIFDesktop deskTop;
	protected TCSession session;
	protected TCComponentBOMLine rootbomline;
	
	public BaseHandler() {
		this.app = AIFUtility.getCurrentApplication();
		this.deskTop = AIFUtility.getCurrentApplication().getDesktop();
		this.session = (TCSession) this.app.getSession();
		((AbstractPSEApplication)app).expandBelow();
		this.rootbomline = ((AbstractPSEApplication)app).getBOMPanel()
				.getTreeTable().getRoot();
	}
	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		return null;
	}

}
