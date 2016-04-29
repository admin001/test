package com.udssoft.custom;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import com.teamcenter.rac.aif.kernel.InterfaceAIFComponent;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMView;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCProperty;
import com.udssoft.custom.handlers.BaseHander;

public class ModifyItemId extends BaseHander {

	// private File file = null;
	private TCComponentItem item;
	private TCComponentBOMView bOMView;
	protected InterfaceAIFComponent[] targets = null;
	private TCComponent root;

	public ModifyItemId() {
		super();
	}

	@Override
	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		// get different file types
		this.targets = this.app.getTargetComponents();
		for (InterfaceAIFComponent target : targets) {
			root = (TCComponent) target;
			if (root instanceof TCComponentItem) {
				item = (TCComponentItem) root;
				modify(item);
			}
//			if (root instanceof TCComponentBOMView) {
//				bOMView = (TCComponentBOMView) root;
//				modify(bOMView);
//			}
		}

		return null;
	}

	public void modify(TCComponentItem item) {
		try {
			TCProperty property = item.getTCProperty("item_id");
			property.setStringValue("C999999999");
			item.setTCProperty(property);
		} catch (TCException e) {
			e.printStackTrace();
		}

	}

//	public void modify(TCComponentBOMView item) {
//		try {
//			TCProperty property = item.getTCProperty("object_string");
//			property.setStringValue("C999999999-йсм╪");
//			item.setTCProperty(property);
//		} catch (TCException e) {
//			e.printStackTrace();
//		}
//
//	}

}
