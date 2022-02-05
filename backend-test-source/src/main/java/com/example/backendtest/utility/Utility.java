package com.example.backendtest.utility;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utility {
    public static String convertTimeStampToDisplayDateTime(Date d, String format) {
        DateFormat df = new SimpleDateFormat(format);
        return df.format(d);
    }

    public static String getReferenceCode(String phone) {
        return String.format("%s%s",
                convertTimeStampToDisplayDateTime(new Date(), Constant.DATE_FORMAT),
                phone.substring(phone.length() - 4));
    }
}
