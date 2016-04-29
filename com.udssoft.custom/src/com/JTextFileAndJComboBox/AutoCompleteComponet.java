package com.JTextFileAndJComboBox;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.teamcenter.rac.classification.common.AbstractG4MContext;
import com.teamcenter.rac.classification.common.form.AbstractG4MFormElement;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.ics.ICSKeyLov;
import com.teamcenter.rac.kernel.ics.ICSProperty;

public class AutoCompleteComponet extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AutoCompleteComponet(ArrayList<String> items,
			final AbstractG4MFormElement m_formElement) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		setTitle("Auto Completion Test");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(200, 200, 500, 400);
		final JTextField txtInput = new JTextField();
		setupAutoComplete(txtInput, items);
		txtInput.setColumns(30);
		JButton submit = new JButton("确定");
		getContentPane().setLayout(new FlowLayout());
		getContentPane().add(txtInput, BorderLayout.EAST);
		getContentPane().add(submit, BorderLayout.NORTH);
		submit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ICSProperty[] icsProps = m_formElement.getForm()
						.getProperties();
				AbstractG4MFormElement[] formElements = m_formElement.getForm()
						.getFormElements();
				AbstractG4MContext cus_context = m_formElement.getForm()
						.getContext();
				String str2 = cus_context.getClassifiedObjectUid();
				try {
					TCComponent tcComponent = cus_context
							.getClassificationService().getTCComponent(str2);
					TCComponentItemRevision tcComponentItemRevision = ((TCComponentItemRevision) tcComponent);
					for (int i = 0; i < icsProps.length; i++) {
						// 是否隐藏
						if (formElements[i].getPropertyDescription().isHidden()) {
							continue;
						}
						// 获得名称
						if (formElements[i].getPropertyDescription().getName()
								.contains("品牌")) {
							if (formElements[i].getPropertyDescription()
									.getFormat().isList()) {
								ICSKeyLov keyLov = m_formElement
										.getPropertyDescription().getFormat()
										.getKeyLov();
								String[] strs = keyLov.getKeys();
								Map<String, String> maps = new HashMap<String, String>();
								for (String str : strs) {
									maps.put(keyLov.getValueOfKey(str), str);
								}
								icsProps[i].setValue(maps.get(txtInput
										.getText()));
								formElements[i].setProperty(icsProps[i]);
								tcComponentItemRevision.setProperty("a2_PP", keyLov.getValueOfKey(icsProps[i].getValue()));
							}
						}
						if(formElements[i].getPropertyDescription().getName().contains("型号")){
							if(icsProps[i].getValue().isEmpty()){
								tcComponentItemRevision.setProperty("a2_XH", "");
							}else{
								tcComponentItemRevision.setProperty("a2_XH", icsProps[i].getValue().substring(1));	
							}
//							icsProps[i].setValue("ANURRNN");
//							formElements[i].setProperty(icsProps[i]);
						}
					}
				} catch (TCException e1) {
					e1.printStackTrace();
				}
				setVisible(false);
			}
		});

	}

	private static boolean isAdjusting(JComboBox cbInput) {
		if (cbInput.getClientProperty("is_adjusting") instanceof Boolean) {
			return (Boolean) cbInput.getClientProperty("is_adjusting");
		}
		return false;
	}

	private static void setAdjusting(JComboBox cbInput, boolean adjusting) {
		cbInput.putClientProperty("is_adjusting", adjusting);
	}

	public static void setupAutoComplete(final JTextField txtInput,
			final ArrayList<String> items) {
		final DefaultComboBoxModel model = new DefaultComboBoxModel();
		final JComboBox cbInput = new JComboBox(model) {
			public Dimension getPreferredSize() {
				return new Dimension(super.getPreferredSize().width, 0);
			}
		};
		setAdjusting(cbInput, false);
		for (String item : items) {
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
					for (String item : items) {
						if (item.toLowerCase().startsWith(input.toLowerCase())) {
							model.addElement(item);
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

	/**
	 * 获取站点所对应的站点名
	 * 
	 * @param stations1
	 *            站点的字符串一
	 * @param stations2
	 *            站点的字符串二 因为一个字符串装不下
	 * */
	public static Map getStationMap(String stations1, String stations2) {
		Map map = new HashMap();
		if (!stations1.equals(null)) {
			String[] strs1 = stations1.split("@");
			for (int i = 1; i < strs1.length; i++) {
				String[] strs2 = strs1[i].split("\\|");
				for (int j = 0; j < strs2.length; j++) {
					map.put(strs2[1], strs2[2]);
				}
			}
		}
		if (!stations2.equals(null)) {
			String[] strs2 = stations2.split("@");
			for (int i = 1; i < strs2.length; i++) {
				String[] strs3 = strs2[i].split("\\|");
				for (int j = 0; j < strs3.length; j++) {
					map.put(strs3[1], strs3[2]);
				}
			}
		}
		return map;
	}
}