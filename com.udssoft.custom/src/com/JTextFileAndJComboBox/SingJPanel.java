package com.JTextFileAndJComboBox;

import java.util.ArrayList;

import com.teamcenter.rac.classification.common.form.AbstractG4MFormElement;


//����ʽ������.�ڵ�һ�ε��õ�ʱ��ʵ�����Լ�   
public class SingJPanel {  
  private SingJPanel() {}  
  private static AutoCompleteComponet single;  
  //��̬��������   
  public static AutoCompleteComponet getInstance(ArrayList<String> items,AbstractG4MFormElement m_formElement) {  
	  if(single !=null){
		  single.setVisible(true);
	  }
	  if (single == null) {    
           try {
			single = new AutoCompleteComponet(items,m_formElement);
		} catch (Exception e) {
			e.printStackTrace();
		}  
       }    
      return single;  
  }  
}  
