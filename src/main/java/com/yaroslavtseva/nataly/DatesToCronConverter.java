package com.yaroslavtseva.nataly;

import java.text.ParseException;
import java.util.List;

public interface DatesToCronConverter {

    public final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    String convert(List<String> dates) throws DatesToCronConvertException, ParseException;

    String getImplementationInfo();
}
