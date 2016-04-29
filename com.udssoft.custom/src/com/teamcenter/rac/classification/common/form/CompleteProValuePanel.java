package com.teamcenter.rac.classification.common.form;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.JTextFileAndJComboBox.AutoCompleteComponet;
import com.teamcenter.rac.classification.common.AbstractG4MContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.ics.ICSKeyLov;
import com.teamcenter.rac.kernel.ics.ICSProperty;
import com.teamcenter.rac.kernel.ics.ICSPropertyDescription;

public class CompleteProValuePanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -657597877199067494L;
	private JButton submit;
	private JTextField txtInput;
	private AbstractG4MFormElement g4MformElement;
	private ICSPropertyDescription propertyDescription;
	private ICSProperty property;
	private String[] values ;
	private int status = 0;
	private TCComponentItemRevision tcComponentItemRevision;
	public CompleteProValuePanel(AbstractG4MFormElement g4MformElement,TCComponentItemRevision tcComponentItemRevision) {
		this.g4MformElement = g4MformElement;
		this.tcComponentItemRevision = tcComponentItemRevision;
		init();
	}

	public void init() {		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			setTitle("Auto Completion Test");
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			setBounds(200, 200, 500, 400);
			
			txtInput = new JTextField();
			propertyDescription =  g4MformElement.getPropertyDescription();
			property = g4MformElement.getProperty();
			values = propertyDescription.getFormat().getKeyLov().getValues();
			setupAutoComplete();
			txtInput.setColumns(30);
			
			submit = new JButton("确定");
			getContentPane().setLayout(new FlowLayout());
			getContentPane().add(txtInput, BorderLayout.EAST);
			getContentPane().add(submit, BorderLayout.NORTH);
			this.submit.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println(txtInput.getText());
					property.setValue(propertyDescription.getFormat().getKeyLov().getKeyofLOV(txtInput.getText()));
					g4MformElement.setProperty(property);
					try {
						if(!(tcComponentItemRevision == null)){
								if(propertyDescription.getName().contains("品牌")){
									tcComponentItemRevision.setProperty(CustomMyColorUserButton.BRAND, txtInput.getText());
								}
								if(propertyDescription.getName().contains("型号")){
									tcComponentItemRevision.setProperty(CustomMyColorUserButton.Model, txtInput.getText());
								}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					setVisible(false);
					status = 1;
				}
			});
		} catch (Exception e) {
		}
	}
	
	public int getStatus(){
		return this.status;
	}

//	public TCComponentItemRevision getProValues(AbstractG4MFormElement m_formElement) {
//		TCComponentItemRevision tcComponentItemRevision = null;
//		List proValus = new ArrayList();
//		try {
//			ICSProperty[] icsProps = m_formElement.getForm().getProperties();
//			AbstractG4MFormElement[] formElements = m_formElement.getForm()
//					.getFormElements();
//			// 获得此分类的itemrevision对象
//			cus_context = m_formElement.getForm().getContext();
//			String str2 = cus_context.getClassifiedObjectUid();
//			TCComponent tcComponent = cus_context.getClassificationService()
//					.getTCComponent(str2);
//			if (tcComponent.isTypeOf("ItemRevision")) {
//				tcComponentItemRevision = ((TCComponentItemRevision) tcComponent);
//
//				for (int i = 0; i < icsProps.length; i++) {
//					// 是否隐藏
//					if (formElements[i].getPropertyDescription().isHidden()) {
//						continue;
//					}
//
//					//获得名称
//					if(formElements[i].getPropertyDescription().getName().contains("品牌")){
//						if (formElements[i].getPropertyDescription().getFormat()
//								.isList()) {
//							ICSKeyLov keyLov = m_formElement.getPropertyDescription()
//									.getFormat().getKeyLov();
//							
//							String[] keys = keyLov.getKeys();
//							String[] values = keyLov.getValues();
//							for (int j = 0; j < keys.length; j++) {
//								proValus.add(values[j]);
//							}
//							items.put(CustomMyColorUserButton.BRAND, proValus);
//						}
//					}
//					if(formElements[i].getPropertyDescription().getName().contains("型号")){
//						tcComponentItemRevision.setProperty("a2_XH", icsProps[i].getValue());
//					}
//				}
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		
//		return tcComponentItemRevision;
//	}

	private static boolean isAdjusting(JComboBox cbInput) {
		if (cbInput.getClientProperty("is_adjusting") instanceof Boolean) {
			return (Boolean) cbInput.getClientProperty("is_adjusting");
		}
		return false;
	}

	private static void setAdjusting(JComboBox cbInput, boolean adjusting) {
		cbInput.putClientProperty("is_adjusting", adjusting);
	}

	public void setupAutoComplete() {
		final DefaultComboBoxModel model = new DefaultComboBoxModel();
		final JComboBox cbInput = new JComboBox(model) {
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, 0);
			}
		};
		setAdjusting(cbInput, false);
		for (String item : values) {
			model.addElement(item);
		}

		cbInput.setSelectedItem(null);
		cbInput.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!isAdjusting(cbInput)) {
					if (cbInput.getSelectedItem() != null) {
						txtInput.setText(cbInput.getSelectedItem().toString());
					}
				}
			}
		});

		txtInput.addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				setAdjusting(cbInput, true);
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					if (cbInput.isPopupVisible()) {
						e.setKeyCode(KeyEvent.VK_ENTER);
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_ENTER
						|| e.getKeyCode() == KeyEvent.VK_UP
						|| e.getKeyCode() == KeyEvent.VK_DOWN) {
					e.setSource(cbInput);
					cbInput.dispatchEvent(e);
					if (e.getKeyCode() == KeyEvent.VK_ENTER) {
						txtInput.setText(cbInput.getSelectedItem().toString());
						cbInput.setPopupVisible(false);
					}
				}
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					cbInput.setPopupVisible(false);
				}
				setAdjusting(cbInput, false);
			}
		});
		txtInput.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				updateList();
			}

			public void removeUpdate(DocumentEvent e) {
				updateList();
			}

			public void changedUpdate(DocumentEvent e) {
				updateList();
			}

			private void updateList() {
				setAdjusting(cbInput, true);
				model.removeAllElements();
				String input = txtInput.getText();
				if (!input.isEmpty()) {
					for (String value :values) {
						if (value.toLowerCase().contains(input.toLowerCase())) {
							model.addElement(value);
						}
					}
				}
				cbInput.setPopupVisible(model.getSize() > 0);
				setAdjusting(cbInput, false);
			}
		});
		txtInput.setLayout(new BorderLayout());
		txtInput.add(cbInput, BorderLayout.SOUTH);
	}

}
