package com.udssoft.tc10.mycustom.newitemid;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Locales
{
  private static final String BUNDLE_NAME = "com.udssoft.tc10.mycustom.newitemid.newitemid_locale";
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("com.udssoft.tc10.mycustom.newitemid.newitemid_locale");

  public static String getString(String paramString) {
    try {
      return RESOURCE_BUNDLE.getString(paramString);
    }
    catch (MissingResourceException localMissingResourceException) {
    }
    return '!' + paramString + '!';
  }
}