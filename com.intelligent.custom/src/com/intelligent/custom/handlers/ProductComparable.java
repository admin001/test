package com.intelligent.custom.handlers;

import java.util.Comparator;
import java.util.Map;

import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentChangeItemRevision;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.pse.AbstractPSEApplication;

public class ProductComparable implements Comparator<Object>{  
    
  public int compare(Object o0, Object o1) {  
//      UserPo user0 = (UserPo) o0;  
//      UserPo user1 = (UserPo) o1;  
//      if (user0.getAge() > user1.getAge()) {  
//          return 1; // 第一个大于第二个  
//      } else if (user0.getAge() < user1.getAge()) {  
//          return -1;// 第一个小于第二个  
//      } else {  
          return 0; // 等于  
//      }  
  }  
}  
