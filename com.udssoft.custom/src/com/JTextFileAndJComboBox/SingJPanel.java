package com.JTextFileAndJComboBox;

import java.util.ArrayList;

import com.teamcenter.rac.classification.common.form.AbstractG4MFormElement;


//懒汉式单例类.在第一次调用的时候实例化自己   
public class SingJPanel {  
  private SingJPanel() {}  
  private static AutoCompleteComponet single;  
  //静态工厂方法   
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
