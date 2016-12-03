package com.bcn.beacon.beacon;

import android.test.mock.MockApplication;

import com.bcn.beacon.beacon.Utility.LocationUtil;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by omar on 02/12/16.
 */

public class LocationUtilTest {
    private LocationUtil util = new LocationUtil();


    @Test
    public void dist_comparator(){
        Assert.assertEquals(0.0,(util.distFrom(0.0, 0.0, 0.0, 0.0)));
        Assert.assertEquals(0.0,(util.distFrom(123.234, 123.234, 123.234, 123.234)));

        Assert.assertEquals(0.0,(util.distFrom(-123.234, 123.234, -123.234, 123.234)));
        Assert.assertEquals(0.0,(util.distFrom(-123.234, -123.234, -123.234, -123.234)));

        Assert.assertEquals(15233.705,(util.distFrom(-137, 0, 0, 0)) );
        Assert.assertEquals(15233.705,(util.distFrom(0, 0, -137, 0)) );

        Assert.assertEquals(80.048, util.distFrom(-128.178, 52.182, -128.889, 52.001));

        Assert.assertEquals(20015.087, util.distFrom(180, 90, -180, -90));
        Assert.assertEquals(0.0, util.distFrom(360, 180, -360, -180));

        Assert.assertEquals(util.distFrom(180, 90, -180, -90), util.distFrom(360, 270, -360, -270));
    }

    @Test
    public void get_location_name(){
        MockApplication mock = new MockApplication();
        Assert.assertEquals("", util.getLocationName(0, 0, mock));

        Assert.assertEquals("", util.getLocationName(0, 0, mock));
    }
}
