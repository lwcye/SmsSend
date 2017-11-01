package com.gcit.smssend;

import com.blankj.utilcode.constant.TimeConstants;
import com.blankj.utilcode.util.TimeUtils;

import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        long day = System.currentTimeMillis() % TimeConstants.DAY + 8 * TimeConstants.HOUR;
        final long currentDay = System.currentTimeMillis() - day;
        System.out.println(TimeUtils.millis2String(currentDay));
    }
}