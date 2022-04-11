import org.junit.Test;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MyTest {
    @Test
    public void checkDatesAtEveryFirstDayMonth() throws DatesToCronConvertException {
        List<String> dateList = new ArrayList<>();
        dateList.add("2022-01-01T00:00:00");
        dateList.add("2022-02-01T00:00:00");
        dateList.add("2022-03-01T00:00:00");
        dateList.add("2022-04-01T00:00:00");
        dateList.add("2022-05-01T00:00:00");
        dateList.add("2022-06-01T00:00:00");
        dateList.add("2022-07-01T00:00:00");
        dateList.add("2022-08-01T00:00:00");
        DatesToCron d = new DatesToCron();
        assertEquals("0 0 0 1/0 * *", d.convert(dateList));
    }

    @Test
    public void checkDatesAtEverySecondMinute() throws DatesToCronConvertException {
        List<String> dateList = new ArrayList<>();
        dateList.add("2022-01-24T19:53:00");
        dateList.add("2022-01-24T19:54:00");
        dateList.add("2022-01-24T19:55:00");
        dateList.add("2022-01-24T19:56:00");
        dateList.add("2022-01-24T19:57:00");
        dateList.add("2022-01-24T19:58:00");
        dateList.add("2022-01-24T19:59:00");
        dateList.add("2022-01-24T20:00:00");
        dateList.add("2022-01-24T20:01:00");
        dateList.add("2022-01-24T20:02:00");

        DatesToCron d = new DatesToCron();
        assertEquals("0 * * * * MON", d.convert(dateList));
    }

    @Test
    public void checkDatesAtEveryDayBetween8and9() throws DatesToCronConvertException {
        List<String> dateList = new ArrayList<>();
        dateList.add("2022-01-25T08:00:00");
        dateList.add("2022-01-25T08:30:00");
        dateList.add("2022-01-25T09:00:00");
        dateList.add("2022-01-25T09:30:00");
        dateList.add("2022-01-26T08:00:00");
        dateList.add("2022-01-26T08:30:00");
        dateList.add("2022-01-26T09:00:00");
        dateList.add("2022-01-26T09:30:00");

        DatesToCron d = new DatesToCron();
        assertEquals("0 0/30 8-9 * * *", d.convert(dateList));
    }

    @Test
    public void checkDatesAtEveryHalfHourB() throws DatesToCronConvertException {
        List<String> dateList = new ArrayList<>();
        dateList.add("2022-01-25T08:00:00");
        dateList.add("2022-01-25T08:30:00");
        dateList.add("2022-01-26T08:00:00");
        dateList.add("2022-01-26T08:30:00");
        dateList.add("2022-02-27T08:00:00");
        dateList.add("2022-02-27T08:30:00");
        dateList.add("2022-02-28T08:00:00");
        dateList.add("2022-02-28T08:30:00");

        DatesToCron d = new DatesToCron();
        assertEquals("0 0/30 8 25-28 * *", d.convert(dateList));
    }

    @Test
    public void checkDatesAtEveryTwelveHours() throws DatesToCronConvertException {
        List<String> dateList = new ArrayList<>();
        dateList.add("2022-01-01T00:00:00");
        dateList.add("2022-01-01T12:00:00");
        dateList.add("2022-01-02T00:00:00");
        dateList.add("2022-01-02T12:00:00");
        dateList.add("2022-02-03T00:00:00");
        dateList.add("2022-02-03T12:00:00");
        dateList.add("2022-02-04T00:00:00");
        dateList.add("2022-02-04T12:00:00");

        DatesToCron d = new DatesToCron();
        assertEquals("0 0 0/12 1-4 * *", d.convert(dateList));
    }

    @Test
    public void checkDatesAtEveryTenMinutes() throws DatesToCronConvertException {
        List<String> dateList = new ArrayList<>();
        dateList.add("2022-01-05T08:00:00");
        dateList.add("2022-01-05T08:10:00");
        dateList.add("2022-01-05T08:20:00");
        dateList.add("2022-01-05T08:30:00");
        dateList.add("2022-01-05T08:40:00");
        dateList.add("2022-01-05T08:50:00");
        dateList.add("2022-01-05T09:00:00");
        dateList.add("2022-01-05T09:10:00");

        DatesToCron d = new DatesToCron();
        assertEquals("0 0/10 * * * WED", d.convert(dateList));
    }

    @Test(expected = DatesToCronConvertException.class)
    public void checkDatesExceptionParse() throws DatesToCronConvertException, ParseException {
        List<String> dateList = new ArrayList<>();
        dateList.add("2022-01-05T08:00:00");
        dateList.add("2022-02-05T08:15:00");
        dateList.add("2022-03-05T08:26:00");
        dateList.add("2022-04-05T08:35:00");
        dateList.add("2022-05-05T08:48:00");
        dateList.add("2022-06-07T08:50:70");
        dateList.add("2022-07-05T09:09:00");
        dateList.add("2022-09-06T09:10:00");

        DatesToCron d = new DatesToCron();
        d.convert(dateList);
    }

    @Test(expected = DatesToCronConvertException.class)
    public void checkDatesException() throws DatesToCronConvertException, ParseException {
        List<String> dateList = new ArrayList<>();
        dateList.add("2022-01-05T08:00:00");
        dateList.add("2022-02-05T08:15:00");
        dateList.add("2022-03-05T08:26:00");
        dateList.add("2022-04-05T08:35:00");
        dateList.add("2022-12-08T08:48:00");
        dateList.add("2022-06-07T08:50:00");
        dateList.add("2022-07-05T09:09:00");
        dateList.add("2022-09-06T09:10:00");

        DatesToCron d = new DatesToCron();
        d.convert(dateList);
    }
}
