package com.bcn.beacon.beacon;


import com.bcn.beacon.beacon.Utility.DataUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by neema on 2016-11-29.
 */
public class DataUtilTest {


    @Test
    public void convertMonthToStringTest(){
        String output = DataUtil.convertMonthToString(1);
        assert (output == "JAN");

        output = DataUtil.convertMonthToString(8);
        assert (output == "AUG");

        output = DataUtil.convertMonthToString(12);
        assert(output == "DEC");

        output = DataUtil.convertMonthToString(13);
        assert(output.length() == 0);

        output = DataUtil.convertMonthToString(-1);
        assert(output.length() == 0);

        output = DataUtil.convertMonthToString(0);
        assert(output.length() == 0);

    }


    @Test
    public void convertDayToStringTest(){
        String output = DataUtil.convertDayToString(0);
        assert (output == "Sat");

        output = DataUtil.convertDayToString(5);
        assert (output == "Thu");

        output = DataUtil.convertDayToString(3);
        assert (output == "Tue");

        output = DataUtil.convertDayToString(7);
        assert (output.length() == 0);
    }


}