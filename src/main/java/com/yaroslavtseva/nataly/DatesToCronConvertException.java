package com.yaroslavtseva.nataly;

public class DatesToCronConvertException extends Exception {
    public DatesToCronConvertException(String message) {
        super("DatesToCronConvertException " + message);
    }

    public DatesToCronConvertException() throws DatesToCronConvertException {
        throw new DatesToCronConvertException();
    }
}
