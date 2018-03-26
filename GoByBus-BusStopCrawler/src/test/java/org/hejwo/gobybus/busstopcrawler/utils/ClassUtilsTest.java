package org.hejwo.gobybus.busstopcrawler.utils;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ClassUtilsTest {

    @Test
    public void getLowerCasedName_shouldUncapitalizeClassName() {
        String lowerCasedName = ClassUtils.getLowerCasedName(ClassUtilsTest.class);

        assertThat(lowerCasedName).isEqualToIgnoringCase("classUtilsTest");
    }
}