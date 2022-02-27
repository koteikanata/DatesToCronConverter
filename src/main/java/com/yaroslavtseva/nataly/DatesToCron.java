package com.yaroslavtseva.nataly;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class DatesToCron implements DatesToCronConverter {

    public DatesToCron() {
    }

    public String convert(List<String> dates) throws DatesToCronConvertException {

        List<String> crons = new ArrayList<>();
        List<LocalDateTime> dateList = new ArrayList<>();

        //check input dates
        for (String date : dates) {
            dateList.add(tryParseDate(date));
        }

        //convert to cron each date and fill in the list
        for (LocalDateTime dt : dateList) {
            crons.add(createSubCron(dt));
        }

        //create cron for all input dates
        String preResult = createCron(crons);

        //last check a possible create a cron and return this or throw exception
        return checkDaysOfWeek(preResult, crons);
    }


    public String getImplementationInfo() {
        StringBuilder res = new StringBuilder().append("Yaroslavtseva Natalya\n");
        res.append("name of class implementation: ").append(getClass().getSimpleName());
        res.append("\npackage: ").append(getClass().getPackage().getName());
        res.append("\nhttps://github.com/koteikanata");

        return res.toString();
    }

    private static LocalDateTime tryParseDate(String date) throws DatesToCronConvertException {
        try {
            return LocalDateTime.parse(date);
        } catch (DateTimeParseException e) {
            throw new DatesToCronConvertException("can not parse");
        }
    }

    private static String createSubCron(LocalDateTime date) {
        return date.getSecond() + " " +
                date.getMinute() + " " +
                date.getHour() + " " +
                date.getDayOfMonth() + " " +
                date.getMonthValue() + " " +
                date.getDayOfWeek();
    }

    private static String createCron(List<String> crons) throws DatesToCronConvertException {
        //create list with crons
        List<ArrayList> list = new ArrayList<>();
        list.add(new ArrayList<Integer>());
        list.add(new ArrayList<Integer>());
        list.add(new ArrayList<Integer>());
        list.add(new ArrayList<Integer>());
        list.add(new ArrayList<Integer>());
        list.add(new ArrayList<String>());

        ArrayList<Integer> seconds = list.get(0);
        ArrayList<Integer> minutes = list.get(1);
        ArrayList<Integer> hours = list.get(2);
        ArrayList<Integer> daysOfMonth = list.get(3);
        ArrayList<Integer> months = list.get(4);

        List<LocalTime> localTimeList = new ArrayList<>();
        List<LocalDate> localDateList = new ArrayList<>();
        Map<LocalDate, List<LocalTime>> map = new HashMap<>();

        //put all in cron without repeats
        for (String cron : crons) {
            String[] splitCron = cron.split(" ");

            LocalDate localDate = LocalDate.of(0,
                    Integer.parseInt(splitCron[4]),
                    Integer.parseInt(splitCron[3]));

            LocalTime localTime = LocalTime.of(Integer.parseInt(splitCron[2]),
                    Integer.parseInt(splitCron[1]),
                    Integer.parseInt(splitCron[0]));

            if (!map.containsKey(localDate)) {
                map.put(localDate, new ArrayList<>());
            }
            map.get(localDate).add(localTime);

            if (!localTimeList.contains(LocalTime.of(Integer.parseInt(splitCron[2]),
                    Integer.parseInt(splitCron[1]),
                    Integer.parseInt(splitCron[0])))) {
                localTimeList.add(LocalTime.of(Integer.parseInt(splitCron[2]),
                        Integer.parseInt(splitCron[1]),
                        Integer.parseInt(splitCron[0])));
            }
            if (!localDateList.contains(LocalDate.of(0,
                    Integer.parseInt(splitCron[4]),
                    Integer.parseInt(splitCron[3])))) {
                localDateList.add(LocalDate.of(0,
                        Integer.parseInt(splitCron[4]),
                        Integer.parseInt(splitCron[3])));
            }
        }

        //check time for all dates
        List<List<LocalTime>> listLocalTime = map.values().stream().distinct().collect(toList());

        List<LocalTime> key = listLocalTime.get(0);
        for (LocalDate localDate : localDateList) {
            if (!map.get(localDate).equals(key)) {
                map.remove(localDate);
            } else {
                if (!daysOfMonth.contains(localDate.getDayOfMonth()))
                    daysOfMonth.add(localDate.getDayOfMonth());

                if (!months.contains(localDate.getMonthValue()))
                    months.add(localDate.getMonthValue());

                if (!list.get(5).contains(localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US).toUpperCase(Locale.ROOT)))
                    list.get(5).add(localDate.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US).toUpperCase(Locale.ROOT));
            }
        }
        localDateList = new ArrayList<>(map.keySet());

        for (int i = 0; i < key.size(); i++) {
            if (!seconds.contains(key.get(i).getSecond()))
                seconds.add(key.get(i).getSecond());
            if (!minutes.contains(key.get(i).getMinute()))
                minutes.add(key.get(i).getMinute());
            if (!hours.contains(key.get(i).getHour()))
                hours.add(key.get(i).getHour());
        }

        LocalTime lt1 = localTimeList.get(0);
        LocalTime lt2;
        if (localTimeList.size() > 1) {
            lt2 = localTimeList.get(1);
        } else {
            lt2 = localTimeList.get(0);
        }
        Duration duration = Duration.between(lt1, lt2);

        for (int i = 0; i < localTimeList.size() - 1; i++) {
            lt1 = localTimeList.get(i);
            lt2 = localTimeList.get(i + 1);

            duration = Duration.between(lt1, lt2);
        }

        LocalDate ld1 = localDateList.get(0);
        LocalDate ld2 = localDateList.get(1 % localDateList.size());
        Period period = Period.between(ld1, ld2);

        for (int i = 0; i < localDateList.size() - 1; i++) {
            ld1 = localDateList.get(i);
            ld2 = localDateList.get(i + 1);

            period = Period.between(ld1, ld2);
        }

        String res = "";

        if (list.get(5).size() == 1) {
            res = " " + list.get(5).get(0);
        } else {
            res = " *" + res;
        }

        // if month one - *
        // if is interval a-b
        // else write specific values
        if ((months.size() == 1) || (checkSegment(months, 4) == 1)) {
            res = " *" + res;
        } else {
            res = " " + months.get(0) + "/" + checkSegment(months, 4) + res;
        }

        // each segment checking
        // if day is one or interval between days == 1 and amount of months == 1 -> day is *
        // else write an interval
        if (((daysOfMonth.size() == 1) || (checkSegment(daysOfMonth, 3) == 1)) && (months.size() == 1)) {
            res = " *" + res;
        } else if (checkSegment(daysOfMonth, 3) == 1) {
            res = " " + daysOfMonth.get(0) + "-" + daysOfMonth.get(daysOfMonth.size() - 1) + res;
        } else {
            res = " " + daysOfMonth.get(0) + "/" + checkSegment(daysOfMonth, 3) + res;
        }

        // repeat the same logic for hours
        if (((hours.size() == 1) || (checkSegment(hours, 2) == 1)) && (res.charAt(1) == '*') && (daysOfMonth.size() == 1)) {
            res = " *" + res;
        } else {
            if (hours.size() == 1) {
                res = " " + hours.get(0) + res;
            } else if (checkSegment(hours, 2) == 1) {
                res = " " + hours.get(0) + "-" + hours.get(hours.size() - 1) + res;
            } else {
                res = " " + hours.get(0) + "/" + checkSegment(hours, 2) + res;
            }
        }

        //for minutes
        if (((minutes.size() == 1) || (checkSegment(minutes, 1) == 1)) && (res.charAt(1) == '*') /*&& (hours.size() == 1)*/) {
            res = " *" + res;
        } else {
            if (minutes.size() == 1) {
                res = " " + minutes.get(0) + res;
            } else if (checkSegment(minutes, 1) == 1) {
                res = " " + minutes.get(0) + "-" + minutes.get(minutes.size() - 1) + res;
            } else if (checkSegment(minutes, 1) != -1) {
                res = " " + minutes.get(0) + "/" + checkSegment(minutes, 1) + res;
            } else {
                res = " " + list.get(1).stream().collect(joining(",")) + res;
            }
        }

        // for seconds
        if (((seconds.size() == 1) || (checkSegment(seconds, 0) == 1)) && (res.charAt(1) == '*') && (minutes.size() == 1)) {
            res = "*" + res;
        } else {
            if (seconds.size() == 1) {
                res = seconds.get(0) + res;
            } else if (checkSegment(seconds, 0) == 1) {
                res = seconds.get(0) + "-" + seconds.get(seconds.size() - 1) + res;
            } else {
                res = seconds.get(0) + "/" + checkSegment(seconds, 0) + res;
            }
        }

        // if amount of days of week > 1 -> day of week can be any -> *
        if (list.get(5).size() > 1) {
            list.get(5).clear();
            list.get(5).add("*");
        }

        // create array for checking an exception
        long[] mask = new long[]{duration.getSeconds() % 60, duration.toMinutes() % 60, duration.toHours(),
                period.getDays(), period.getMonths(), 0};

        // checking for the possibility of creating the cron
        checkException(list, mask);

        return res;
    }

    private static void checkException(List<ArrayList> list, long[] mask) throws DatesToCronConvertException {
        StringBuilder str = new StringBuilder("");
        boolean check = true;

        for (int i = 4; i >= 0; i--) {
            ArrayList current = list.get(i);
            if (check) {
                if (mask[i] != 0) {
                    check = false;
                }
                if (mask[i] == 0 || mask[i] == 1) {
                    if ((list.get(i + 1).size() == 1)) {
                        str.append(" *").append(str);
                    } else {
                        if (current.indexOf(current.stream().max(Comparator.naturalOrder()).get()) + 1 ==
                                current.indexOf(current.stream().min(Comparator.naturalOrder()).get())) {
                            str.append(" *").append(str);
                        } else {
                            str.append(" ").append(current.get(0)).append("-")
                                    .append(current.get(current.size() - 1)).append(str);
                        }
                    }
                    check = true;
                } else {
                    str.append(" ").append(current.get(0)).append("/").append(mask[i]).append(str);
                }
            } else {
                if (mask[i] != 0) {
                    throw new DatesToCronConvertException("can not crete cron");
                }
                check = true;
                if (current.size() == 1) {
                    str.append(current.get(0)).append(str);
                } else if (current.size() == 2) {
                    str.append(current.get(0)).append(",").append(current.get(1)).append(str);
                } else {
                    str.append(current.get(0)).append("-").append(current.get(current.size() - 1)).append(str);
                }
            }
        }
    }

    // for each segment find a period or duration
    private static long checkSegment(List list, int posInCron) {
        int dateFirst = (int) list.get(0);
        int dateSecond = 0;
        if (list.size() > 1) {
            dateSecond = (int) list.get(1);
        }
        switch (posInCron) {
            case 4: {
                Period period = Period.between(LocalDate.of(0, dateFirst, 1),
                        LocalDate.of(0, dateSecond, 1));

                return period.getMonths();
            }
            case 3: {
                Period period;
                if (list.size() > 1) {
                    period = Period.between(LocalDate.of(0, 1, dateFirst),
                            LocalDate.of(0, 1, dateSecond));
                } else {
                    period = Period.between(LocalDate.of(0, 1, dateFirst),
                            LocalDate.of(0, 1, dateFirst));
                }

                return period.getDays();
            }
            case 2: {
                Duration duration = Duration.between(LocalTime.of(dateFirst, 0, 0),
                        LocalTime.of(dateSecond, 0, 0));
                for (int i = 1; i < list.size() - 1; i++) {
                    if (duration != Duration.between(LocalTime.of((Integer) list.get(i), 0, 0),
                            LocalTime.of((Integer) list.get(i + 1), 0, 0)))
                        return -1;
                }
                return duration.toHours();
            }
            case 1: {
                Duration duration = Duration.between(LocalTime.of(0, dateFirst, 0),
                        LocalTime.of(0, dateSecond, 0));
                for (int i = 1; i < list.size() - 1; i++) {
                    LocalTime l1 = LocalTime.of(0, (Integer) list.get(i), 0);
                    LocalTime l2 = LocalTime.of(0, (Integer) list.get(i + 1), 0);
                    Duration d;
                    if (l1.compareTo(l2) <= 0) {
                        d = Duration.between(l1, l2);
                    } else
                        d = Duration.between(l1, l2.plusHours(1));
                    if (duration.toMinutes() != d.toMinutes()) {
                        System.out.println(d.getSeconds());
                        System.out.println(duration.toMinutes());
                        return -1;
                    }
                }
                return duration.toMinutes();
            }

            case 0: {
                Duration duration = Duration.between(LocalTime.of(0, 0, dateFirst),
                        LocalTime.of(0, 0, dateSecond));
                for (int i = 1; i < list.size() - 1; i++) {
                    if (duration != Duration.between(LocalTime.of(0, 0, (Integer) list.get(i)),
                            LocalTime.of(0, 0, (Integer) list.get(i + 1))))
                        return -1;
                }
                return duration.getSeconds();
            }
            default:
                return 0;
        }
    }

    private String checkDaysOfWeek(String preResult, List<String> crons) throws DatesToCronConvertException {
        ArrayList arrayOfDays = new ArrayList();

        for (int i = 0; i < crons.size(); i++) {
            String[] str = crons.get(i).split(" ");
            if (!arrayOfDays.contains(str[5]))
                arrayOfDays.add(str[5]);
        }

        if (preResult.contains("* * * * *") && arrayOfDays.size() > 1) {
            throw new DatesToCronConvertException("can not crete cron");
        }
        return preResult;
    }

}
