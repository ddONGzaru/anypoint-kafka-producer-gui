package tv.anypoint.utils;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Administrator on 2015-10-31.
 */
public final class DateUtils {

    private DateUtils() { }

    public static final int HOURS_24 = 24;

    public static final int MINUTES_60 = 60;

    public static final int SECONDS_60 = 60;

    public static final int MILLI_SECONDS_1000 = 1000;

    private static final int UNIT_HEX = 16;

    /** Date pattern */
    public static final String DATE_PATTERN_DASH = "yyyy-MM-dd";

    /** Time pattern */
    public static final String TIME_PATTERN = "HH:mm";

    /** Date Time pattern */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /** Date HMS pattern */
    public static final String DATE_HMS_PATTERN = "yyyyMMddHHmmss";

    /** Time stamp pattern */
    public static final String TIMESTAMP_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";

    /** year pattern (yyyy)*/
    public static final String YEAR_PATTERN = "yyyy";

    /** month pattern (MM) */
    public static final String MONTH_PATTERN = "MM";

    /** day pattern (dd) */
    public static final String DAY_PATTERN = "dd";

    /** date pattern (yyyyMMdd) */
    public static final String DATE_PATTERN = "yyyyMMdd";

    /** hour, minute, second pattern (HHmmss) */
    public static final String TIME_HMS_PATTERN = "HHmmss";

    /** hour, minute, second pattern (HH:mm:ss) */
    public static final String TIME_HMS_PATTERN_COLONE = "HH:mm:ss";

    /**
     * 현재 날짜, 시간을 조회하여 문자열 형태로 반환한다.<br>
     *
     * @return (yyyy-MM-dd HH:mm:ss) 포맷으로 구성된 현재 날짜와 시간
     */
    public static String getCurrentDateTime() {
        return getCurrentDateTime(DATE_TIME_PATTERN);
    }

    /**
     * 현재 날짜, 시간을 조회을 조회하고, pattern 형태의 포맷으로 문자열을 반환한다.<br><br>
     *
     * DateUtils.getCurrentDateTime("yyyy년 MM월 dd일 hh시 mm분 ss초") = "2012년 04월 12일 20시 41분 50초"<br>
     * DateUtils.getCurrentDateTime("yyyy-MM-dd hh-mm-ss") = "2012-04-12 20:41:50"
     *
     * @param pattern 날짜 및 시간에 대한 포맷
     * @return patter 포맷 형태로 구성된 현재 날짜와 시간
     */
    public static String getCurrentDateTime(String pattern) {
        DateTime dt = new DateTime();
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.print(dt);
    }

    /**
     * 현재 년월을 조회하여 "yyyy-MM" 포맷의 문자열을 반환한다.
     *
     * @return (yyyy-MM) 포맷으로 구성된 현재 년월
     */
    public static String getThisMonth() {
        return getCurrentDateTime("yyyy-MM");
    }

    /**
     * 현재 년월을 조회하고, pattern 형태의 포맷으로 문자열을 반환한다.
     *
     * @param pattern 날짜 및 시간에 대한 포맷
     * @return patter 포맷 형태로 구성된 현재 년월
     */
    public static String getThisMonth(String pattern) {
        return getCurrentDateTime(pattern);
    }

    /**
     * 현재 년을 조회하여 "yyyy" 포맷의 문자열을 반환한다.
     *
     * @return (yyyy-MM) 포맷으로 구성된 현재 년도
     */
    public static String getThisYear() {
        return getCurrentDateTime("yyyy");
    }

    /**
     * 입력받은 일자의 요일을 반환한다.<br><br>
     *
     * DateUtils.getDayOfWeek("2010-11-26") = "금"
     *
     * @param str (yyyy-MM-dd) 포맷의 문자열
     * @return 입력받은 일자에 해당하는 요일
     */
    public static String getDayOfWeek(String str) {
        return getDayOfWeek(str, true, new Locale("ko","KR"));
    }

    /**
     * 입력받은 일자의 요일을 반환한다.<br>
     * Locale 정보를 받아 해당하는 언어에 대해서 약어로 보여주거나 전체 요일 형태로 보여준다.<br><br>
     *
     * DateUtil.getDayOfWeek("2011-02-04", true, Locale.US) = "Fri";<br>
     * DateUtil.getDayOfWeek("2011-02-04", false, Locale.US) = "Friday";<br>
     * DateUtil.getDayOfWeek("2011-02-04", true, Locale.KOREA) = "금";<br>
     * DateUtil.getDayOfWeek("2011-02-04", false, Locale.KOREA) = "금요일";<br>
     *
     * @param str (yyyy-MM-dd) 포맷의 문자열
     * @param abbreviation true면 약어, false면 전체 요일 형태
     * @param locale locale
     * @return 입력받은 일자에 해당하는 요일
     */
    public static String getDayOfWeek(String str, Boolean abbreviation, Locale locale) {

        DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_PATTERN_DASH);
        DateTime dt = fmt.parseDateTime(str);
        DateTime.Property dayOfWeek = dt.dayOfWeek();

        if (abbreviation) {
            return dayOfWeek.getAsShortText(locale);
        } else {
            return dayOfWeek.getAsText(locale);
        }
    }

    /**
     * 입력받은 두 날짜 사이의 일자를 계산한다.<br><br>
     *
     * DateUtils.getDays(new GregorianCalendar(2010, 10, 14), new GregorianCalendar(2010, 11, 28)) = 44
     *
     * @param cal1 시작일 캘린더
     * @param cal2 종료일 캘린더
     * @return 두 날짜 사이의 일자
     */
    public static int getDays(Calendar cal1, Calendar cal2) {

        long min = getMinutes(cal1, cal2);

        return (int) (min / (HOURS_24 * MINUTES_60));
    }

    /**
     * 입력받은 두 날짜 사이의 일자를 계산한다.<br><br>
     *
     * DateUtils.getDays("2010-11-24", "2010-12-30") = 36
     *
     * @param startDate (yyyy-MM-dd) 포맷 형태의 시작일
     * @param endDate (yyyy-MM-dd) 포맷 형태의 종료일
     * @return 두 날짜 사이의 일자
     */
    public static int getDays(String startDate, String endDate) {
        return getDays(startDate, endDate, DATE_PATTERN_DASH);
    }

    /**
     * 입력받은 두 날짜 사이의 일자를 계산한다.<br>
     * -startDate와 endDate는 patter의 포맷형식을 꼭 따라야 한다.<br><br>
     *
     * DateUtils.getDays("2010-11-24", "2010-12-30", "yyyy-MM-dd") = 36
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @param pattern 날짜 포맷
     * @return 두 날짜 사이의 일자
     */
    public static int getDays(String startDate, String endDate, String pattern) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);

        DateTime startDateTime = fmt.parseDateTime(startDate);
        DateTime endDateTime = fmt.parseDateTime(endDate);

        long startMillis = startDateTime.getMillis();
        long endMillis = endDateTime.getMillis();

        int result = (int) (startMillis / (60 * 60 * MILLI_SECONDS_1000 * 24));
        int result1 = (int) (endMillis / (60 * 60 * MILLI_SECONDS_1000 * 24));

        return result1 - result;
    }

    /**
     * 입력받은 두 일자가 같은지 여부를 확인한다.<br><br>
     *
     * DateUtils.equals(new Date(1292252400000l), "2010-12-14") = true
     *
     * @param date1 Date형의 일자
     * @param date2 (yyyy-MM-dd) 포맷형태의 일자
     * @return 일치하면 true를 그렇지않으면 false를 반환
     */
    public static boolean equals(Date date1, String date2) {
        return equals(date1, date2, DATE_PATTERN_DASH);
    }

    /**
     * 입력받은 두 일자가 같은지 여부를 확인한다.<br><br>
     *
     * DateUtils.equals(new Date(1292252400000l), "2010/12/14", "yyyy/MM/dd") = true
     *
     * @param date1 Date형의 일자
     * @param date2 date2pattern 포맷형태의 일자
     * @param date2pattern 날짜 포맷
     * @return 입력받은 두 일자가 같으면 true를 그렇지않으면 false를 반환.
     */
    public static boolean equals(Date date1, String date2, String date2pattern) {
        Date date = convertStringToDate(date2, date2pattern);
        return equals(date1, date);
    }

    /**
     * 입력받은 두 일자가 같은지 여부를 확인한다.<br><br>
     *
     * DateUtils.equals( new Date(1292252400000l), new Date(1292252400000l)) = true
     *
     * @param date1 Date형의 일자
     * @param date2 Date형의 일자
     * @return 입력받은 두 일자가 같으면 true를 그렇지않으면 false를 반환.
     */
    public static boolean equals(Date date1, Date date2) {
        if (date1.getTime() == date2.getTime()) {
            return true;
        }
        return false;
    }

    /**
     * 입력받은 두 일자의 배치 여부를 확인한다.<br><br>
     *
     * DateUtils.greaterThan(new Date(1292311593000l), "2010-12-02") = true
     *
     * @param date1 Date형의 일자
     * @param date2 (yyyy-MM-dd) 포맷형태의 일자
     * @return date1의 일자가 date2보다 뒤의 일자이면 true를 그렇지않으면 false를 반환
     */
    public static boolean greaterThan(Date date1, String date2) {
        return greaterThan(date1, date2, DATE_PATTERN_DASH);
    }

    /**
     * 입력받은 두 일자의 배치 여부를 확인한다.<br><br>
     *
     * DateUtils.greaterThan(new Date(1292311593000l), "2010/12/02", "yyyy/MM/dd") = true
     *
     * @param date1 Date형의 일자
     * @param date2 date2pattern 포맷형태의 일자
     * @param date2pattern 날짜 포맷
     * @return date1의 일자가 date2보다 뒤의 일자이면 true를 그렇지않으면 false를 반환
     */
    public static boolean greaterThan(Date date1, String date2, String date2pattern) {
        Date date = convertStringToDate(date2, date2pattern);
        return greaterThan(date1, date);
    }

    /**
     * 입력받은 두 일자의 배치 여부를 확인한다.<br><br>
     *
     * DateUtils.greaterThan(new Date(1292311593000l), new Date(1292252400000l)) = true
     *
     * @param date1 Date형의 일자
     * @param date2 Date형의 일자
     * @return date1의 일자가 date2보다 뒤의 일자이면 true를 그렇지않으면 false를 반환
     */
    public static boolean greaterThan(Date date1, Date date2) {
        if (date1.getTime() > date2.getTime()) {
            return true;
        }
        return false;
    }

    /**
     * 입력받은 두 일자의 배치 여부를 확인한다.<br><br>
     *
     * DateUtils.greaterThan(new Timestamp(1292311593000l), "2010-12-02 00:00:00.000") = true
     *
     * @param timestamp1 Timestamp형의 일자
     * @param timestamp2 (yyyy-MM-dd hh:MM:ss.SSS) 포맷형태의 일자
     * @return timestamp1 일자가 timestamp2보다 뒤의 일자이면 true를 그렇지않으면 false를 반환
     */
    public static boolean greaterThan(Timestamp timestamp1, String timestamp2) {
        return greaterThan(timestamp1, timestamp2, TIMESTAMP_PATTERN);
    }

    /**
     * 입력받은 두 일자의 배치 여부를 확인한다.<br><br>
     *
     * DateUtils.greaterThan(new Timestamp(1292311593000l), "2010/12/02", "yyyy/MM/dd") = true
     *
     * @param timestamp1 Timestamp형의 일자
     * @param timestamp2 date2pattern 포맷형태의 일자
     * @param timestamp2pattern 날짜 포맷
     * @return timestamp1의 일자가 timestamp2보다 뒤의 일자이면 true를 그렇지않으면 false를 반환
     */
    public static boolean greaterThan(Timestamp timestamp1, String timestamp2, String timestamp2pattern) {
        Timestamp date = convertStringToTimestamp(timestamp2, timestamp2pattern);
        return greaterThan(timestamp1, date);
    }

    /**
     * 입력받은 두 일자의 배치 여부를 확인한다.<br><br>
     *
     * DateUtils.greaterThan(new Timestamp(1292311593000l), new Date(1292252400000l)) = true
     *
     * @param timestamp1 Timestamp형의 일자
     * @param timestamp2 Timestamp형의 일자
     * @return timestamp1 일자가 timestamp2보다 뒤의 일자이면 true를 그렇지않으면 false를 반환
     */
    public static boolean greaterThan(Timestamp timestamp1, Timestamp timestamp2) {
        if (timestamp1.getTime() > timestamp2.getTime()) {
            return true;
        }
        return false;
    }

    /**
     * 입력받은 일자에 대해서 해당 일만큼 더한 일자를 반환한다.<br>
     * 마이너스 일자는 입력받은 일자보다 이전의 일자로 계산해서 반환한다.<br><br>
     *
     * DateUtils.addDays("2010-12-18", 30) = "2011-01-17"<br>
     * DateUtils.addDays("2012-12-12", -35) = "2012-11-07"
     *
     * @param date (yyyy-MM-dd) 포맷형태의 일자
     * @param days 더할 날짜
     * @return date에 days를 더한 일자
     */
    public static String addDays(String date, int days) {
        return addDays(date, days, DATE_PATTERN_DASH);
    }

    /**
     * 입력받은 일자에 대해서 해당 일만큼 더한 일자를 datePattern 형식으로 반환한다.<br>
     * 마이너스 일자는 입력받은 일자보다 이전의 일자로 계산해서 반환한다.<br><br>
     *
     * DateUtils.addDays("2010-12-18 12:12:12", 30, "yyyy-MM-dd HH:mm:ss") = "2011-01-17 12:12:12"<br>
     * DateUtils.addDays("2012-12-12", -35, "yyyy-MM-dd") = "2012-11-07"
     *
     * @param date 일자
     * @param days 더할 날짜
     * @param datePattern 날짜형식
     * @return date에 days를 더한 일자
     */
    public static String addDays(String date, int days, String datePattern) {

        if (days == 0) {
            return date;
        }

        DateTimeFormatter fmt = DateTimeFormat.forPattern(datePattern);
        DateTime dt = fmt.parseDateTime(date);
        DateTime subtracted = dt.withFieldAdded(DurationFieldType.days(), days);
        return fmt.print(subtracted);
    }

    /**
     * 입력받은 일자에 대해서 해당 개월수만큼 더한 일자를 반환한다.<br>
     * 마이너스 개월수는 입력받은 일자보다 이전 일자로 계산해서 반환한다.<br><br>
     *
     * DateUtils.addMonths("2010-12-18", 2) = "2011-02-18"<br>
     * DateUtils.addMonths("2010-12-18", -2) = "2010-10-18"
     *
     * @param date (yyyy-MM-dd) 포맷형태의 일자
     * @param months 더할 개월수
     * @return date에 months를 더한 일자
     */
    public static String addMonths(String date, int months) {
        return addMonths(date, months, DATE_PATTERN_DASH);
    }

    /**
     * 입력받은 일자에 대해서 해당 개월수만큼 더한 일자를 datePattern 형식으로 반환한다.<br>
     * 마이너스 개월수는 입력받은 일자보다 이전 일자로 계산해서 반환한다.<br><br>
     *
     * DateUtils.addMonths("2010-12-18 12:12:12", 2, "yyyy-MM-dd HH:mm:ss") = "2011-02-18 12:12:12"<br>
     * DateUtils.addMonths("2010-12-18", -2, "yyyy-MM-dd") = "2010-10-18"
     *
     * @param date 일자
     * @param months 더할 개월수
     * @param datePattern 날짜형식
     * @return date에 months를 더한 일자
     */
    public static String addMonths(String date, int months, String datePattern) {
        if (months == 0) {
            return date;
        }
        DateTimeFormatter fmt = DateTimeFormat.forPattern(datePattern);
        DateTime dt = fmt.parseDateTime(date);
        DateTime subtracted = dt.withFieldAdded(DurationFieldType.months(), months);
        return fmt.print(subtracted);
    }

    /**
     * 입력받은 일자에 대해서 해당 년수만큼 더한 일자를 반환한다.<br>
     * 마이너스 년수는 입력받은 일자보다 이전 일자로 계산해서 반환한다.<br><br>
     *
     * DateUtils.addYears("2010-12-18", 2) = "2012-12-18"<br>
     * DateUtils.addYears("2010-12-18", -2) = "2008-12-18"
     *
     * @param date (yyyy-MM-dd) 포맷형태의 일자
     * @param years 더할 년수
     * @return date에 years를 더한 일자
     */
    public static String addYears(String date, int years) {
        return addYears(date, years, DATE_PATTERN_DASH);
    }

    /**
     * 입력받은 일자에 대해서 해당 년수만큼 더한 일자를 datePattern 형식으로 반환한다.<br>
     * 마이너스 년수는 입력받은 일자보다 이전 일자로 계산해서 반환한다.<br><br>
     *
     * DateUtils.addYears("2010-12-18 12:12:12", 2, "yyyy-MM-dd HH:mm:ss") = "2012-12-18 12:12:12"<br>
     * DateUtils.addYears("2010-12-18", -2, "yyyy-MM-dd") = "2008-12-18"
     *
     * @param date 일자
     * @param years 더할 년수
     * @param datePattern 날짜 형식
     * @return date에 years를 더한 일자
     */
    public static String addYears(String date, int years, String datePattern) {
        if (years == 0) {
            return date;
        }
        DateTimeFormatter fmt = DateTimeFormat.forPattern(datePattern);
        DateTime dt = fmt.parseDateTime(date);
        DateTime subtracted = dt.withFieldAdded(DurationFieldType.years(), years);
        return fmt.print(subtracted);
    }

    /**
     * 입력된 일자에 대해서 년,월,일만큼 가감해서 계산한 일자를 반환한다.<br><br>
     *
     * DateUtils.addYearMonthDay("2010-04-18", 2, 4, 3) = "2012-08-21"<br>
     * DateUtils.addYearMonthDay("2010-04-18", -2, -4, -3) = "2007-12-15"
     *
     * @param date (yyyy-MM-dd) 포맷형태의 일자
     * @param years 더할 년수
     * @param months 더할 개월수
     * @param days 더할 일수
     * @return date에 years, months, days를 더한 일자
     */
    public static String addYearMonthDay(String date, int years, int months, int days) {

        DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_PATTERN_DASH);
        DateTime dt = fmt.parseDateTime(date);

        if (years != 0) {
            dt = dt.withFieldAdded(DurationFieldType.years(), years);
        }
        if (months != 0) {
            dt = dt.withFieldAdded(DurationFieldType.months(), months);
        }
        if (days != 0) {
            dt = dt.withFieldAdded(DurationFieldType.days(), days);
        }

        return fmt.print(dt);
    }

    /**
     * 입력일을 기준으로 이달 첫번째 일자를 반환한다.<br><br>
     *
     * DateUtils.getFirstDateOfMonth("2010-12-18") = "2010-12-01"
     *
     * @param date 일자
     * @param dateFormat 일자에 대한 날짜형식
     * @return 입력일을 기준으로한 이번달 첫번째 일자
     */
    public static String getFirstDateOfMonth(String date, String dateFormat) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(dateFormat);
        DateTime dt = fmt.parseDateTime(date);
        DateTime dtRet = new DateTime(dt.getYear(), dt.getMonthOfYear(), 1, 0, 0, 0, 0);
        return fmt.print(dtRet);
    }

    /**
     * 입력일을 기준으로 이달 첫번째 일자를 반환한다.<br><br>
     *
     * DateUtils.getFirstDateOfMonth("2010-12-18") = "2010-12-01"
     *
     * @param date (yyyy-MM-dd) 포맷형태의 일자
     * @return 입력을을 기준으로한 이달 첫번째 일자
     */
    public static String getFirstDateOfMonth(String date) {
        return getFirstDateOfMonth(date, DATE_PATTERN_DASH);
    }

    /**
     * 입력일을 기준으로 이달 마지막 일자를 반환한다.<br><br>
     *
     * DateUtils.getLastDateOfMonth("2010-11-20") = "2010-11-30"
     *
     * @param date 일자
     * @param dateFormat 일자에 대한 날짜형식
     * @return 입력일을 기준으로한 이번달 첫번째 일자
     */
    public static String getLastDateOfMonth(String date, String dateFormat) {
        String firstDateOfMonth = getFirstDateOfMonth(date, dateFormat);

        DateTimeFormatter fmt = DateTimeFormat.forPattern(dateFormat);
        DateTime dt = fmt.parseDateTime(firstDateOfMonth);
        dt = dt.plusMonths(1).minusDays(1);
        return fmt.print(dt);
    }

    /**
     * 입력일을 기준으로 이달 마지막 일자를 반환한다.<br><br>
     *
     * DateUtils.getLastDateOfMonth("2010-11-20") = "2010-11-30"
     *
     * @param date (yyyy-MM-dd) 포맷형태의 일자
     * @return 입력을을 기준으로한 이달 마지막 일자
     */
    public static String getLastDateOfMonth(String date) {
        return getLastDateOfMonth(date, DATE_PATTERN_DASH);
    }

    /**
     * 입력일을 기준으로 전달의 첫번째 일자를 반환한다.<br><br>
     *
     * DateUtils.getFirstDateOfPrevMonth("2010-11-20") = "2010-10-01"
     *
     * @param date (yyyy-MM-dd) 포맷형태의 일자
     * @return 입력을을 기준으로한 전달 첫번째 일자
     */
    public static String getFirstDateOfPrevMonth(String date) {
        String firstDateOfMonth = getFirstDateOfMonth(date);

        DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_PATTERN_DASH);
        DateTime dt = fmt.parseDateTime(firstDateOfMonth);
        dt = dt.minusMonths(1);
        return fmt.print(dt);
    }

    /**
     * 입력일을 기준으로 전달의 마지막 일자를 반환한다.<br><br>
     *
     * DateUtils.getLastDateOfPrevMonth("2010-11-20") = "2010-10-31"
     *
     * @param date (yyyy-MM-dd) 포맷형태의 일자
     * @return 입력을을 기준으로한 전달 마지막 일자
     */
    public static String getLastDateOfPrevMonth(String date) {
        String firstDateOfMonth = getFirstDateOfMonth(date);

        DateTimeFormatter fmt = DateTimeFormat.forPattern(DATE_PATTERN_DASH);
        DateTime dt = fmt.parseDateTime(firstDateOfMonth);
        dt = dt.minusDays(1);
        return fmt.print(dt);
    }

    /**
     * 입력된 일자가 유효한 일자인지 체크한다.<br><br>
     *
     * DateUtils.isDate("2010-12-01") = true<br>
     * DateUtils.isDate("2010-13-01") = false
     *
     * @param date (yyyy-MM-dd) 포맷형태의 일자
     * @return 유효한 일자이면 true를 그렇지않으면 false를 반환
     */
    public static boolean isDate(String date) {
        return isDate(date, DATE_PATTERN_DASH);
    }

    /**
     * 입력된 일자가 유효한 일자인지 체크한다.<br><br>
     *
     * DateUtils.isDate("2010/12/01", "yyyy/MM/dd") = true<br>
     * DateUtils.isDate("2010년12월32일", "yyyy년MM월dd일") = false
     *
     * @param date 입력일자
     * @param pattern 포맷형태
     * @return 유효한 일자이면 true를 그렇지않으면 false를 반환
     */
    public static boolean isDate(String date, String pattern) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        DateTime dt = new DateTime();
        try {
            dt = fmt.parseDateTime(date);
        } catch (Exception e) {
            return false;
        }

        if (!fmt.print(dt).equals(date)) {
            return false;
        }
        return true;
    }

    /**
     * 입력된 시간이 유효한 시간인지를 체크한다.<br><br>
     *
     * DateUtils.isTime("11:56") = true<br>
     * DateUtils.isTime("31:56") = false
     *
     * @param time (HH:mm) 포맷형태의 시간
     * @return 유효한 시간이면 true를 그렇지않으면 false를 반환
     */
    public static boolean isTime(String time) {
        return isTime(time, TIME_PATTERN);
    }

    /**
     * 입력된 시간이 유효한 시간인지를 체크한다.
     *
     * @param time 입력시간
     * @param pattern 포맷형태
     * @return 유효한 시간이면 true를 그렇지 않으면 false를 반환
     */
    public static boolean isTime(String time, String pattern) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        DateTime dt = new DateTime();
        try {
            dt = fmt.parseDateTime(time);
        } catch (Exception e) {
            return false;
        }

        if (!fmt.print(dt).equals(time)) {
            return false;
        }
        return true;
    }

    /**
     * 문자열을 java.util.Date 타입으로 변경한다.<br><br>
     *
     * DateUtils.convertStringToDate("2010-12-14")
     *
     * @param str (yyyy-MM-dd) 포맷의 Date형으로 변환할 문자열
     * @return (yyyy-MM-dd) 포맷의 Date형
     */
    public static Date convertStringToDate(String str) {
        return convertStringToDate(str, DATE_PATTERN_DASH);
    }

    /**
     * 문자열을 java.util.Date 타입으로 변경한다.<br><br>
     *
     * DateUtils.convertStringToDate("2010-12-14 16:26:33", "yyyy-MM-dd HH:mm:ss")
     *
     * @param str pattern 포맷의 Date형으로 변환할 문자열
     * @param pattern 변경할 포맷
     * @return pattern 포맷의 Date형
     */
    public static Date convertStringToDate(String str, String pattern) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.parseDateTime(str).toDate();
    }

    /**
     * java.util.Date 타입을 문자열로 변경한다.<br><br>
     *
     * DateUtils.convertDateToString(new Date(1292311593557l)) = "2010-12-14"
     *
     * @param date Date형의 입력된 날짜
     * @return (yyyy-MM-dd) 포맷의 문자열
     */
    public static String convertDateToString(Date date) {
        return convertDateToString(date, DATE_PATTERN_DASH);
    }

    /**
     * java.util.Date 타입을 패턴에 맞는 문자열로 변경한다.<br><br>
     *
     * DateUtils.convertDateToString(new Date(1292311593557l, "yyyy/MM/dd") = "2010/12/14"
     *
     * @param date Date형의 입력된 날짜
     * @param pattern 날짜 패턴
     * @return pattern 포맷의 문자열
     */
    public static String convertDateToString(Date date, String pattern) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.print(date.getTime());
    }

    /**
     * 문자열을 java.sql.Date 타입으로 변환한다.<br><br>
     *
     * DateUtils.convertStringToSQLDate("2010-12-14")
     *
     * @param str (yyyy-MM-dd) 포맷의 Sql Date형으로 변환할 문자열
     * @return (yyyy-MM-dd) 포맷의 Sql Date형
     */
    public static java.sql.Date convertStringToSQLDate(String str) {
        return convertStringToSQLDate(str, DATE_PATTERN_DASH);
    }

    /**
     * 문자열을 java.sql.Date 타입으로 변환한다.<br><br>
     *
     * DateUtils.convertStringToSQLDate("2010/12/14", "yyyy/MM/dd")
     *
     * @param str pattern 포맷의 Sql Date형으로 변환할 문자열
     * @param pattern 날짜 패턴
     * @return pattern 포맷의 Sql Date형
     */
    public static java.sql.Date convertStringToSQLDate(String str, String pattern) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return new java.sql.Date(fmt.parseDateTime(str).getMillis());
    }

    /**
     * 문자열을 java.sql.Timestamp 타입으로 변환한다.<br><br>
     *
     * DateUtils.convertStringToTimestamp("2010-12-14")
     *
     * @param str (yyyy-MM-dd) 포맷의 Timestamp형으로 변환할 문자열
     * @return (yyyy-MM-dd) 포맷의 Timestamp형
     */
    public static Timestamp convertStringToTimestamp(String str) {
        return convertStringToTimestamp(str, DATE_PATTERN_DASH);
    }

    /**
     * 패턴에 맞게 들어온 문자열을 java.sql.Timestamp 타입으로 변환한다.<br><br>
     *
     * DateUtils.convertStringToTimestamp("2010/12/14 12:12:12.123", "yyyy/MM/dd hh:mm:ss.SSS")
     *
     * @param str pattern 포맷의 Timestamp형으로 변환할 문자열
     * @param pattern 날짜 패턴
     * @return pattern 포맷의 Timestamp형
     */
    public static Timestamp convertStringToTimestamp(String str, String pattern) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return new Timestamp(fmt.parseDateTime(str).getMillis());
    }

    /**
     * java.sql.Timestamp 타입의 일자를 문자열로 변환한다.<br><br>
     *
     * DateUtils.convertTimestampToString(new Timestamp(1292311593000l))<br>
     * DateUtils.convertTimestampToString(new Timestamp(new Date().getTime()))
     *
     * @param date (yyyy-MM-dd) 포맷의 문자열로 변환할 timestamp
     * @return (yyyy-MM-dd) 포맷의 문자열
     */
    public static String convertTimestampToString(Timestamp date) {
        return convertTimestampToString(date, DATE_PATTERN_DASH);
    }

    /**
     * java.sql.Timestamp 타입의 일자를 패턴에 맞는 문자열로 변환한다.<br><br>
     *
     * DateUtils.convertTimestampToString(new Timestamp(1292311593000l), "yyyy/MM/dd hh:mm") = "2010/12/14 16:26"
     *
     * @param date pattern 포맷의 문자열로 변환할 timestamp
     * @param pattern 날짜 패턴
     * @return pattern 포맷의 문자열
     */
    public static String convertTimestampToString(Timestamp date, String pattern) {
        if (date == null) {
            return "";
        }
        return convertDateToString(date, pattern);
    }

    /**
     * 문자열을 java.util.Calendar 타입으로 변환한다.<br><br>
     *
     * DateUtils.convertStringToCalender("20101214123412")
     *
     * @param str Calendar형으로 변환할 (yyyyMMddHHmmss) 포맷의 문자열
     * @return Calendar
     */
    public static Calendar convertStringToCalender(String str) {
        if ((str == null) || (str.length() < 14))
            return null;

        String year = str.substring(0, 4);
        String month = str.substring(4, 6);
        String day = str.substring(6, 8);
        String hour = str.substring(8, 10);
        String minute = str.substring(10, 12);
        String second = str.substring(12, 14);

        return (new GregorianCalendar(Integer.valueOf(year), Integer.valueOf(month) - 1, Integer.valueOf(day),
                Integer.valueOf(hour), Integer.valueOf(minute), Integer.valueOf(second)));
    }


    /**
     * 문자열 date를 Mills 타입으로 변환한다.<br><br>
     *
     * @param date "yyyy-MM-dd HH:mm:ss" 형태의 문자열
     * @return Mills의 long 형태 값
     */
    public static long convertStringToTimeMills(String date) {
        return convertStringToTimeMills(date, DATE_TIME_PATTERN);
    }

    /**
     * 문자열 date를 Mills 타입으로 변환한다.<br><br>
     *
     * @param date "yyyy-MM-dd HH:mm:ss" 형태의 문자열
     * @param pattern date가 가지는 날짜 포맷
     * @return Mills의 long 형태 값
     */
    public static long convertStringToTimeMills(String date, String pattern) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern(pattern);
        return fmt.parseDateTime(date).getMillis();
    }


    /**
     * java.util.Calendar타입의 일자를 문자열로 변환한다.<br><br>
     *
     * DateUtils.convertCalendarToString(new GregorianCalendar(2010, 11, 14, 12, 34, 12)) = "20101214123412000"
     *
     * @param calendar 문자열로 변환할 calendar
     * @return (yyyyMMddHHmmss) 포맷 형태의 문자열
     */
    public static String convertCalendarToString(Calendar calendar) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

        return dateFormat.format(calendar.getTime()) + "000";
    }

    /**
     * 사용자 정의 패턴에 맞게 들어온 문자열을 새로운 문자열로 변환한다.<br><br>
     *
     * DateUtils.convertStringToString("20101214", "yyyyMMdd", "yyyy-MM-dd") = "2010-12-14"<br>
     * DateUtils.convertStringToString("2010.12.14", "yyyy.MM.dd", "yyyy/MM/dd") = "2010/12/14"
     *
     * @param str 일자 문자열
     * @param basePattern 기존 일자 패턴
     * @param wantedPattern 변환할 일자 패턴
     * @return 변환할 일자 패턴으로 변환된 일자
     */
    public static String convertStringToString(String str, String basePattern, String wantedPattern) {
        DateTimeFormatter basefmt = DateTimeFormat.forPattern(basePattern);
        DateTimeFormatter wantedfmt = DateTimeFormat.forPattern(wantedPattern);
        DateTime dt = basefmt.parseDateTime(str);
        return wantedfmt.print(dt);
    }

    /**
     * 시각에 대한 파라미터 값을 16진수로 변환하여 문자열로 반환한다.<br><br>
     *
     * @param currentDate 시각에 대한 long값
     * @return 현재 시각을 16진수로 변환한 문자열
     */
    public static String convertTimeMillisToHexString(long currentDate) {
        return String.format("%012x", currentDate);
    }

    /**
     * 현재 시각에 대한 값을 16진수로 변환하여 문자열로 반환한다.<br><br>
     *
     * @return 현재 시각을 16진수로 변환한 문자열
     */
    public static String convertCurrentTimeMillisToHexString() {
        return String.format("%012x", System.currentTimeMillis());
    }


    public static String convertHexDateToTimeMills(String hexString) {
        return convertHexDateToTimeMills(hexString, DATE_TIME_PATTERN);
    }

    public static String convertHexDateToTimeMills(String hexString, String datePattern) {

        long timestamp = Long.valueOf(hexString, UNIT_HEX);

        return DateUtils.convertDateToString(new Date(timestamp), datePattern);
    }


    /**
     * 입력된 두 일자 사이의 분을 계산하여 반환한다.<br><br>
     *
     * DateUtils.getMinutes(new GregorianCalendar(2010, 11, 14, 12, 34, 12), new GregorianCalendar(2010, 11, 14, 13, 32, 12)) = 58
     *
     * @param cal1 입력된 calendar1
     * @param cal2 입력된 calendar2
     * @return 입력된 두 일자 사이의 분
     */
    public static int getMinutes(Calendar cal1, Calendar cal2) {
        long utc1 = cal1.getTimeInMillis();
        long utc2 = cal2.getTimeInMillis();

        long result = (utc2 - utc1) / (SECONDS_60 * MILLI_SECONDS_1000);

        return (int) result;
    }

    /**
     * 입력된 두 일자 사이의 분을 계산하여 반환한다.<br><br>
     *
     * DateUtils.getMinutes("20121212012321","20121212014321") = 20
     *
     * @param date1 (yyyyMMddHHmmss) 포맷의 문자열
     * @param date2 (yyyyMMddHHmmss) 포맷의 문자열
     * @return 입력된 두 일자 사이의 분
     */
    public static int getMinutes(String date1, String date2) {
        Calendar cal1 = convertStringToCalender(date1);
        Calendar cal2 = convertStringToCalender(date2);

        return getMinutes(cal1, cal2);
    }

    /**
     * 어제 일자를 반환한다.
     *
     * @return (yyyy-MM-dd) 포맷의 어제 일자
     */
    public static String getYesterday() {
        return getYesterday(DATE_PATTERN_DASH);
    }

    /**
     * 어제 일자를 반환한다.
     *
     * @param pattern 날짜 포맷
     * @return pattern 포맷의 어제 일자
     */
    public static String getYesterday(String pattern) {
        Calendar cal = getCalendar();
        cal.add(Calendar.DATE, -1);
        Date date = cal.getTime();
        return convertDateToString(date, pattern);
    }

    /**
     * 한국 시간대에 맞는java.util.Calendar 타입의 일자를 반환한다.
     *
     * @return Calendar 타입
     */
    public static Calendar getCalendar() {
        Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT+09:00"), Locale.KOREA);
        calendar.setTime(new Date());

        return calendar;
    }

    /**
     * 두 일자 사이의 일자 목록을 반환한다.<br><br>
     *
     * DateUtils.getDates("2010-12-17", "2010-12-20") = 2010-12-17, 2010-12-18, 2010-12-19, 2010-12-20
     *
     * @param startDay (yyyy-MM-dd) 포맷의 시작일
     * @param endDay (yyyy-MM-dd) 포맷의 종료일
     * @return 시작일과 종료일 사이의 일자 목록(String 배열)
     */
    public static String[] getDates(String startDay, String endDay) {
        return getDates(startDay, endDay, DATE_PATTERN_DASH);
    }

    /**
     * 두 일자 사이의 일자 목록을 반환한다.<br><br>
     *
     * DateUtils.getDates("2010/12/17", "2010/12/20", "yyyy/MM/dd") = 2010/12/17, 2010/12/18, 2010/12/19, 2010/12/20
     *
     * @param startDay 시작일
     * @param endDay 종료일
     * @param pattern 일자 포맷
     * @return pattern 포맷 형식의 시작일과 종요일 사이의 일자 목록(String 배열)
     */
    public static String[] getDates(String startDay, String endDay, String pattern) {
        List<String> result = new ArrayList<String>();
        result.add(startDay);

        Calendar cal = getCalendar();
        cal.setTime(convertStringToDate(startDay, pattern));
        String nextDay = convertDateToString(cal.getTime(), pattern);

        while (!nextDay.equals(endDay)) {
            cal.add(Calendar.DATE, 1);
            nextDay = convertDateToString(cal.getTime(), pattern);
            result.add(nextDay);
        }
        return result.toArray(new String[0]);
    }

    /**
     * 현재 일자를 yyyy-MM-dd 패턴의 문자열로 반환한다.<br><br>
     *
     * DateUtils.getCurrentDateAsString = 2009-04-28
     *
     * @return (yyyy-MM-dd) 포맷 형태의 현재 일자
     */
    public static String getCurrentDateAsString() {
        return getCurrentDateAsString(DATE_PATTERN_DASH);
    }

    /**
     * 현재 일자를 지정된 패턴의 문자열로 반환한다.<br><br>
     *
     * DateUtils.getCurrentDateAsString("yyyyMMdd") = 20090428
     *
     * @param pattern 일자의 포맷 형태
     * @return patter 포맷 형태의 현재 일자
     */
    public static String getCurrentDateAsString(String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        return df.format(new Date());
    }

    /**
     * 현재 일자를 java.sql.Date 타입으로 반환한다.
     *
     * @return java.sql.Date 타입의 현재 일자
     */
    public static java.sql.Date getCurrentDate() {
        return new java.sql.Date((new Date()).getTime());
    }

    /**
     * 현재 시각에 대한 Time 객체를 반환한다.
     *
     * @return java.sql.Time 타입의 현재 시각
     */
    public static Time getCurrentTime() {
        return new Time(new Date().getTime());
    }

    /**
     * 현재 시각에 대한 Time 문자열을 반환한다.
     *
     * @return (HH:mm:ss) 포맷의 현재 시각
     */
    public static String getCurrentTimeAsString() {
        return new Time(new Date().getTime()).toString();
    }

    /**
     * 현재 시각에 대한 Timestamp 객체를 반환한다.
     *
     * @return java.sql.Timestamp 타입의 현재 시각
     */
    public static Timestamp getCurrentTimestamp() {
        Timestamp timestamp = new Timestamp(new Date().getTime());
        return timestamp;
    }

    /**
     * 현재 시각에 대한 Timestamp 문자열을 구한다.
     *
     * @return (yyyy-MM-dd HH:mm:ss.SSS) 포맷의 현재 시각
     */
    public static String getCurrentTimestampAsString() {
        return getCurrentTimestamp().toString();
    }

    /**
     * 입력된 일자를 기준으로 해당년도가 윤년인지 여부를 반환한다.
     *
     * @param  inputDate (yyyy-MM-dd) 형식의 일자
     * @return 윤년이면 true를 그렇지 않으면 false를 반환
     */
    public static boolean isLeapYear(String inputDate) {
        return isLeapYear(Integer.parseInt(inputDate.substring(0, 4)));
    }

    /**
     * 정수형태로 입력된 년도를 기준으로 해당년도가 윤년인지 여부를 반환한다.
     *
     * @param year 년도
     * @return year이 윤년이면 true를 그렇지 않으면 false를 반환
     */
    public static boolean isLeapYear(int year) {
        return ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) ? true : false;
    }



}

