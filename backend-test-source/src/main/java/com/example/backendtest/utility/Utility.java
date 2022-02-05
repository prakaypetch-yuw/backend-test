package com.example.backendtest.utility;

import org.joda.time.DateTime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Utility {
    public static String convertTimeStampToDisplayDateTime(long d, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(d);
    }

    public static String getReferenceCode(String phone) {
        return String.format("%s%s",
                convertTimeStampToDisplayDateTime(DateTime.now().getMillis(), Constant.DATE_FORMAT),
                phone.substring(phone.length() - 4));
    }
}
