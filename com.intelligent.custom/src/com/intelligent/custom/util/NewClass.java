package com.intelligent.custom.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;

public class NewClass {

    public static void main(String[] args) {
        JFrame frame = new JFrame("ÄãÃÃµÄ");
        JComboBox bar = new JComboBox();
        Component com = bar.getEditor().getEditorComponent();
        final String[] datas = new String[]{"123123", "12548", "345123", "456123", "567123", "678123"};
        final JTextComponent component = (JTextComponent) com;
        final JList p = new JList(datas);
        component.addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if (component.getText() == null || component.getText().length() == 0) {
                    return;
                }
                for (int i = 0; i < datas.length; i++) {
                    if (datas[i].toLowerCase().startsWith(component.getText())) {
                        p.setSelectedValue(datas[i], true);
                        String str = component.getText();
                        component.setText(datas[i]);
                        component.setSelectionStart(str.length());
                        component.setSelectionEnd(datas[i].length());
                    }
                }
            }
        });
        bar.setEditable(true);
        frame.getContentPane().add(bar, BorderLayout.NORTH);
        frame.getContentPane().add(new JScrollPane(p), BorderLayout.CENTER);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }
}