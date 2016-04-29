package com.udssoft.tc10.mycustom.baseutil;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil
{
  public static boolean isEmpty(Object obj)
  {
    if (obj == null) {
      return true;
    }
    if (obj.toString().length() == 0) {
      return true;
    }

    return obj.toString().equalsIgnoreCase("null");
  }
  
  public static String maxArraysIsNum(String[] strs, Boolean isWaterCon) {
	    String max = "";
	    for (int i = strs.length - 1; i >= 0; i--) {
	      if (isWaterCon.booleanValue()) {
	        if (isNum(strs[i]).booleanValue()) {
	          max = strs[i];
	          break;
	        }
	      }
	      else if (isNum(strs[i]).booleanValue()) {
	        max = strs[i];
	        break;
	      }
	    }

	    return max;
	  }
  
  public static Boolean isNum(String str) {
	    Pattern pattern = Pattern.compile("^[0-9]*$");
	    Matcher match = pattern.matcher(str);
	    if (!match.matches())
	    {
	      return Boolean.valueOf(false);
	    }

	    return Boolean.valueOf(true);
	  }
  public static void main(String[] args) {
	  
	  Pattern pattern = Pattern.compile("^I[A-Z]{2}[0-9]{7}$");
	    Matcher match = pattern.matcher("IIA1211111");
	    System.out.println(match.matches());
}
}