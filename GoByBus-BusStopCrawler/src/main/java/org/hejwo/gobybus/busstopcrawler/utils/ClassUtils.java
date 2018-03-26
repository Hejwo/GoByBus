package org.hejwo.gobybus.busstopcrawler.utils;

import org.apache.commons.lang3.text.WordUtils;

public abstract class ClassUtils {

    public static String getLowerCasedName(Class aClass) {
        return WordUtils.uncapitalize(aClass.getSimpleName());
    }

}
