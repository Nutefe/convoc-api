package com.cyberethik.convocapi.playload.helper;

import org.springframework.data.domain.Sort;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static java.time.temporal.TemporalAdjusters.*;

public class Helpers
{
    public static String path_file_docs = "/home/upload/enacp/docs/";
    public static String path_file_exports = "/home/upload/enacp/exports/";
    public static String path_file_logos = "/home/upload/convoc/logo/";
    public static String path_file_resources = "/home/upload/enacp/resources/";
    public static String base_file_url = "https://enacp2k23.enacp.com/enacp-rest-api/web/service/";

//    public static String base_file_url = "http://localhost:9197/web/service/";
//    public static String base_file_url = "http://localhost:8080/enacp-rest-api/web/service/";
//    public static String base_client_url = "http://localhost:5173/";
    public static String base_client_url = "http://192.168.1.70:8080/";
//    public static String base_client_url = "https://client2k23.enacp.com/";
    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        String nowhitespace = WHITESPACE.matcher(input).replaceAll("-");
        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);
        String slug = NONLATIN.matcher(normalized).replaceAll("");
        return slug.toLowerCase(Locale.ENGLISH);
    }
    public static Sort sortByCreatedDesc() {
        return Sort.by(Sort.Direction.DESC, new String[] { "createdAt" });
    }
    public static Sort sortByIdDesc() {
        return Sort.by(Sort.Direction.DESC, new String[] { "id" });
    }
    public static Sort sortByCreatedAsc() {
        return Sort.by(Sort.Direction.ASC, new String[] { "createdAt" });
    }

    public static String currentDate() {
        final Date date = new Date();
        final SimpleDateFormat datej = new SimpleDateFormat("yyyy-MM-dd");
        final String datejour = datej.format(date);
        final SimpleDateFormat heure = new SimpleDateFormat("HH:mm:ss");
        final String heurejour = heure.format(date);
        return datejour+" "+heurejour;
    }
    public static String currentDateSimple() {
        final Date date = new Date();
        final SimpleDateFormat datej = new SimpleDateFormat("yyyy-MM-dd");
        final String datejour = datej.format(date);
        final SimpleDateFormat heure = new SimpleDateFormat("HH:mm:ss");
        final String heurejour = heure.format(date);
        return datejour;
    }

    public static Date getDateFromString(String s) {
        final Date datej;
        try {
            datej = new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return datej;
    }
    public static Date getDateFromString1(String s) {
        final Date datej;
        try {
            datej = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(s);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return datej;
    }
    
    public static String generatRef(final String classe, final Long lastId) {
        final Date date = new Date();
        final SimpleDateFormat datej = new SimpleDateFormat("yyyyMMdd");
        final String datejour = datej.format(date);
        final SimpleDateFormat heure = new SimpleDateFormat("HHmmss");
        final String heurejour = heure.format(date);
        return classe.substring(0, 1).toUpperCase()+""+lastId+""+datejour+""+heurejour;
    }

    public static String generatRef(final String classe) {
        return classe.substring(0, 2).toUpperCase()+""+UUID.randomUUID().toString();
    }
    
    public static String generat() {
        final Date date = new Date();
        final SimpleDateFormat datej = new SimpleDateFormat("yyyyMMdd");
        final String datejour = datej.format(date);
        final SimpleDateFormat heure = new SimpleDateFormat("HHmmss");
        final String heurejour = heure.format(date);
        final Calendar calendar = Calendar.getInstance();
        return datejour+""+heurejour+""+calendar.getTimeInMillis();
    }

    public static String convertDate(Date dateInit){
        DateFormat date = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, new Locale("FR","fr"));
        SimpleDateFormat datej = new SimpleDateFormat("dd/MM/yyyy");
        String datejour = datej.format(dateInit);

        return datejour;
    }

    public static String convertHeure(Date dateInit){
        DateFormat date = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, new Locale("FR","fr"));
        SimpleDateFormat heure = new SimpleDateFormat("HH:mm:ss");
        String heurejour = heure.format(dateInit);
        return heurejour;
    }

    public static String convertAllDate(Date dateInit){
        System.out.println(dateInit);
        DateFormat date = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, new Locale("FR","fr"));
        SimpleDateFormat datej = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String datejour = datej.format(dateInit);

        return datejour;
    }

    public static String year(){
//        DateFormat date = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, new Locale("FR","fr"));
        Date date = new Date();
        SimpleDateFormat datej = new SimpleDateFormat("yyyy");
        String datejour = datej.format(date);
        return datejour;
    }

    public static String convertFr(Date date1){
//        DateFormat date = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, new Locale("FR","fr"));/
        SimpleDateFormat datej = new SimpleDateFormat("MMMM yyyy");
//        datej.format(date1);
        return datej.format(date1);
    }

    public static Long dayBetween(Date firstDate, Date secondDate){
        long diffInMillies = Math.abs(secondDate.getTime() - firstDate.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diff;
    }

    public static Date tomorowDate(){
        Date td = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(td);
        c.add(Calendar.DATE, 1);

        return c.getTime();
    }

    public static  Date getYearStartDate(){
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.with(firstDayOfYear()); // 2015-01-01
//        LocalDate lastDay = now.with(lastDayOfYear()); // 2015-12-31
        return java.sql.Date.valueOf(firstDay);
    }
    public static  Date getYearEndDate() {
        LocalDate now = LocalDate.now();
//        LocalDate firstDay = now.with(firstDayOfYear()); // 2015-01-01
        LocalDate lastDay = now.with(lastDayOfYear()); // 2015-12-31
//        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(lastDay.toString());
//        return d;
        return java.sql.Date.valueOf(lastDay);
    }
    public static  Date getMonthStartDate(){
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.with(firstDayOfMonth()); // 2015-01-01
//        LocalDate lastDay = now.with(lastDayOfYear()); // 2015-12-31
        return java.sql.Date.valueOf(firstDay);
    }
    public static  Date getMonthEndDate() {
        LocalDate now = LocalDate.now();
//        LocalDate firstDay = now.with(firstDayOfYear()); // 2015-01-01
        LocalDate lastDay = now.with(lastDayOfMonth()); // 2015-12-31
//        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(lastDay.toString());
//        return d;
        return java.sql.Date.valueOf(lastDay);
    }
    public static  Date getWeekEndDate(Date date) {
        long DAY_IN_MS = 1000 * 60 * 60 * 24;
        Date lastDay = new Date(date.getTime() - (7 * DAY_IN_MS));
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        LocalDate now = LocalDate.now();
////        LocalDate firstDay = now.with(firstDayOfYear()); // 2015-01-01
//        LocalDate lastDay = now.with(lastDayOfMonth()); // 2015-12-31
////        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(lastDay.toString());
//        return d;
        return lastDay;
    }

    public static  Date getDayDate() {
        LocalDate now = LocalDate.now();
//        Date d = new SimpleDateFormat("yyyy-MM-dd").parse(lastDay.toString());
//        return d;
        return java.sql.Date.valueOf(now);
    }

    public static  void  addDate(){
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        Calendar cTotal = (Calendar) c1.clone();

        cTotal.add(Calendar.YEAR, c2.get(Calendar.YEAR));
        cTotal.add(Calendar.MONTH, c2.get(Calendar.MONTH)); // Zero-based months
        cTotal.add(Calendar.DATE, c2.get(Calendar.DATE));
        cTotal.add(Calendar.HOUR_OF_DAY, c2.get(Calendar.HOUR_OF_DAY));
        cTotal.add(Calendar.MINUTE, c2.get(Calendar.MINUTE));
        cTotal.add(Calendar.SECOND, c2.get(Calendar.SECOND));
        cTotal.add(Calendar.MILLISECOND, c2.get(Calendar.MILLISECOND));

//        System.out.format("%s + %s = %s", c1.getTime(), c2.getTime(), cTotal.getTime());

        System.out.println("Date1 : " + c1.getTime());
        System.out.println("Date2 : " + c2.getTime());
        System.out.println("Date sum : " + cTotal.getTime());
    }

    public static  String[] splitDate(String date){
        String[] arrOfDate = date.split(":");
        String jj = arrOfDate[0];
        String hh = arrOfDate[1];
        String mm = arrOfDate[2];
        String ss = arrOfDate[3];
        System.out.println(jj);
        return  arrOfDate;
    }

    public static Date sumDate(Date dateStart, String date){
//        Date td = new Date();
        String[] arrOfDate = date.split(":");
        Calendar c = Calendar.getInstance();
        c.setTime(dateStart);
//        System.out.println(arrOfDate[0]);
//        System.out.println(Integer.valueOf(arrOfDate[0]));
//        cTotal.add(Calendar.YEAR, c2.get(Calendar.YEAR));
//        cTotal.add(Calendar.MONTH, c2.get(Calendar.MONTH)); // Zero-based months
//        cTotal.add(Calendar.DATE, c2.get(Calendar.DATE));
//        cTotal.add(Calendar.HOUR_OF_DAY, c2.get(Calendar.HOUR_OF_DAY));
//        cTotal.add(Calendar.MINUTE, c2.get(Calendar.MINUTE));
//        cTotal.add(Calendar.SECOND, c2.get(Calendar.SECOND));
//        cTotal.add(Calendar.MILLISECOND, c2.get(Calendar.MILLISECOND));
        c.add(Calendar.DATE, Integer.valueOf(arrOfDate[0]));
        c.add(Calendar.HOUR_OF_DAY, Integer.valueOf(arrOfDate[1]));
        c.add(Calendar.MINUTE, Integer.valueOf(arrOfDate[2]));
        c.add(Calendar.SECOND, Integer.valueOf(arrOfDate[3]));

        return c.getTime();
    }
    public static Date sumDate(Date dateStart){
        Calendar c = Calendar.getInstance();
        c.setTime(dateStart);
        Calendar c2 = Calendar.getInstance();

        Date d1 = dateStart;
        Date d2 = c2.getTime();

        Long difference_In_Time = d2.getTime() - d1.getTime();

        Long difference_In_Seconds = (difference_In_Time / 1000) % 60;
        Long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;
        Long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;
        Long difference_In_Years = (difference_In_Time / (1000l * 60 * 60 * 24 * 365));
        Long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;

        c.add(Calendar.YEAR, difference_In_Years.intValue());
//        c.add(Calendar.MONTH, c2.get(Calendar.MONTH));
        c.add(Calendar.DATE, difference_In_Days.intValue());
        c.add(Calendar.HOUR_OF_DAY, difference_In_Hours.intValue());
        c.add(Calendar.MINUTE, difference_In_Minutes.intValue());
        c.add(Calendar.SECOND, difference_In_Seconds.intValue());

        return c.getTime();
    }

    public static Boolean compareDate(Date first, Date second){
        Calendar c = Calendar.getInstance();
        c.setTime(first);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(second);

        if (c.after(c1)){
            return  true;
        } else {
            return  false;
        }
    }

    public static void findDifference(String start_date,
                   String end_date) {

        // SimpleDateFormat converts the
        // string format to date object
        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
//            Date d1 = sdf.parse(start_date);
//            Date d2 = sdf.parse(end_date);
            Date d1 = sdf.parse(start_date);
            Date d2 = new Date();

            // Calculate time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calculate time difference in
            // seconds, minutes, hours, years,
            // and days
            long difference_In_Seconds
                    = (difference_In_Time
                    / 1000)
                    % 60;

            long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60))
                    % 60;

            long difference_In_Hours
                    = (difference_In_Time
                    / (1000 * 60 * 60))
                    % 24;

            long difference_In_Years
                    = (difference_In_Time
                    / (1000l * 60 * 60 * 24 * 365));

            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds

            System.out.print(
                    "Difference "
                            + "between two dates is: ");

            System.out.println(
                    difference_In_Years
                            + " years, "
                            + difference_In_Days
                            + " days, "
                            + difference_In_Hours
                            + " hours, "
                            + difference_In_Minutes
                            + " minutes, "
                            + difference_In_Seconds
                            + " seconds");
        }

        // Catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static void findDifference(Date start) {

        // SimpleDateFormat converts the
        // string format to date object
//        SimpleDateFormat sdf
//                = new SimpleDateFormat(
//                "dd-MM-yyyy HH:mm:ss");

        // Try Block
//        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
//            Date d1 = sdf.parse(start_date);
//            Date d2 = sdf.parse(end_date);
            Date d1 = start;
        Calendar c = Calendar.getInstance();
            Date d2 = c.getTime();

            // Calculate time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calculate time difference in
            // seconds, minutes, hours, years,
            // and days
            long difference_In_Seconds
                    = (difference_In_Time
                    / 1000)
                    % 60;

            long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60))
                    % 60;

            long difference_In_Hours
                    = (difference_In_Time
                    / (1000 * 60 * 60))
                    % 24;

            long difference_In_Years
                    = (difference_In_Time
                    / (1000l * 60 * 60 * 24 * 365));

            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;

            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds

            System.out.print(
                    "Difference "
                            + "between two dates is: ");

            System.out.println(
                    difference_In_Years
                            + " years, "
                            + difference_In_Days
                            + " days, "
                            + difference_In_Hours
                            + " hours, "
                            + difference_In_Minutes
                            + " minutes, "
                            + difference_In_Seconds
                            + " seconds");
//        }

        // Catch the Exception
//        catch (ParseException e) {
//            e.printStackTrace();
//        }
    }

    public static Date compareDate1(Date first, Date second){
        Calendar c = Calendar.getInstance();
        c.setTime(first);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(second);

        Long resultMillis = c1.getTimeInMillis() - c.getTimeInMillis();

        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(resultMillis);

        System.out.println("La différence est de : " + result.get(Calendar.DAY_OF_YEAR) + " jours");

        System.out.println(
                result.get(Calendar.YEAR)
                        + " years, "
                        + result.get(Calendar.DAY_OF_YEAR)
                        + " days, "
                        + result.get(Calendar.HOUR_OF_DAY)
                        + " hours, "
                        + result.get(Calendar.MINUTE)
                        + " minutes, "
                        + result.get(Calendar.SECOND)
                        + " seconds");
        return result.getTime();
    }

    public static void compareDate2(Date first, Date second){
        Calendar c = Calendar.getInstance();
        c.setTime(first);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(second);
        Date d1 = c.getTime();
        Date d2 = c1.getTime();
        Long resultMillis = d2.getTime() - d1.getTime();

//        Calendar result = Calendar.getInstance();
//        result.setTimeInMillis(resultMillis);

        // Calculate time difference
        // in milliseconds
        long difference_In_Time
                = d2.getTime() - d1.getTime();

        // Calculate time difference in
        // seconds, minutes, hours, years,
        // and days
        long difference_In_Seconds
                = (difference_In_Time
                / 1000)
                % 60;

        long difference_In_Minutes
                = (difference_In_Time
                / (1000 * 60))
                % 60;

        long difference_In_Hours
                = (difference_In_Time
                / (1000 * 60 * 60))
                % 24;

        long difference_In_Years
                = (difference_In_Time
                / (1000l * 60 * 60 * 24 * 365));

        long difference_In_Days
                = (difference_In_Time
                / (1000 * 60 * 60 * 24))
                % 365;


        System.out.println(
                difference_In_Years
                        + " years, "
                        + difference_In_Days
                        + " days, "
                        + difference_In_Hours
                        + " hours, "
                        + difference_In_Minutes
                        + " minutes, "
                        + difference_In_Seconds
                        + " seconds");
//        return result.getTime();
    }

    public static String compareDate3(Date startSession, Date duration, Date reprise) {
        Calendar start = Calendar.getInstance();
        start.setTime(startSession); // Date de début

        Calendar end = Calendar.getInstance();
        end.setTime(duration); // Date de fin

        Calendar intervalStart = Calendar.getInstance();
        intervalStart.setTime(startSession); // Date de début de l'intervalle à soustraire

        Calendar intervalEnd = Calendar.getInstance();
        intervalEnd.setTime(reprise); // Date de fin de l'intervalle à soustraire

        long intervalMillis = intervalEnd.getTimeInMillis() - intervalStart.getTimeInMillis();
        long resultMillis = end.getTimeInMillis() - start.getTimeInMillis() - intervalMillis;

        Calendar result = Calendar.getInstance();
        result.setTimeInMillis(resultMillis);

        String days = convertIntToString((result.get(Calendar.DAY_OF_YEAR) - 1));
        String hours = convertIntToString(result.get(Calendar.HOUR_OF_DAY));
        String minutes = convertIntToString(result.get(Calendar.MINUTE));
        String seconds = convertIntToString(result.get(Calendar.SECOND));

//        System.out.println("La différence est de : " + result.get(Calendar.DAY_OF_YEAR) + " jours");
//        System.out.println(
//                        days
//                        + " days, "
//                        + hours
//                        + " hours, "
//                        + minutes
//                        + " minutes, "
//                        + seconds
//                        + " seconds");
        return days+":"+hours+":"+minutes+":"+seconds;

    }


    public static String convertIntToString(Integer duree){
        String res;
        if (String.valueOf(duree).length() == 1){
            res = "0"+duree;
        } else {
            res = String.valueOf(duree);
        }

        return res;
    }

}
