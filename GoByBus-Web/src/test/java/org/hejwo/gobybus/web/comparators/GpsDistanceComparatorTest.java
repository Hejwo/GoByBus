package org.hejwo.gobybus.web.comparators;

import org.junit.Before;
import org.junit.Test;

import java.awt.geom.Point2D;

import static org.assertj.core.api.Assertions.assertThat;

public class GpsDistanceComparatorTest {

    private GpsDistanceComparator comparator;

    @Before
    public void setUp() throws Exception {
        comparator = new GpsDistanceComparator();
    }

    @Test
    public void compareWhenEqualTest() throws Exception {
        Point2D.Double aDouble = new Point2D.Double(1,1);
        Point2D.Double bDouble = new Point2D.Double(1,1);


        int result = comparator.compare(aDouble, bDouble);
        assertThat(result).isEqualTo(0);
    }

    @Test
    public void compareWhenBiggerTest() throws Exception {
        Point2D.Double aDouble = new Point2D.Double(1,1);
        Point2D.Double bDouble = new Point2D.Double(2,2);


        int result = comparator.compare(aDouble, bDouble);
        assertThat(result).isEqualTo(-1);
    }

    @Test
    public void compareWhenSmallerTest() throws Exception {
        Point2D.Double aDouble = new Point2D.Double(2,2);
        Point2D.Double bDouble = new Point2D.Double(1,1);

        int result = comparator.compare(aDouble, bDouble);
        assertThat(result).isEqualTo(1);
    }

}