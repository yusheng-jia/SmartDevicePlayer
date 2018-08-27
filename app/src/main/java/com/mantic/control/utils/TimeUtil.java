package com.mantic.control.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by lin on 2017/6/8.
 */

public class TimeUtil {

    /**
     * 获取时间间隔
     *
     * @param millisecond
     * @return
     */
    public static String getSpaceTime(Long millisecond) {
        Calendar calendar = Calendar.getInstance();
        Long currentMillisecond = calendar.getTimeInMillis();

        //间隔秒
        Long spaceSecond = (currentMillisecond - millisecond) / 1000;

        //一分钟之内
        if (spaceSecond >= 0 && spaceSecond < 60) {
            return "片刻之前";
        }
        //一小时之内
        else if (spaceSecond / 60 > 0 && spaceSecond / 60 < 60) {
            return spaceSecond / 60 + "分钟之前";
        }
        //一天之内
        else if (spaceSecond / (60 * 60) > 0 && spaceSecond / (60 * 60) < 24) {
            return spaceSecond / (60 * 60) + "小时之前";
        }
        //3天之内
        else if (spaceSecond/(60*60*24)>0&&spaceSecond/(60*60*24)<3){
            return spaceSecond/(60*60*24)+"天之前";
        }else {
            return getDateFromMillisecond(millisecond);
        }
    }

    /**
     * 从时间(毫秒)中提取出日期
     *
     * @param millisecond
     * @return
     */
    public static String getDateFromMillisecond(Long millisecond) {

        Date date = null;
        try {
            date = new Date(millisecond);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Calendar current = Calendar.getInstance();


        ////今天
        Calendar today = Calendar.getInstance();

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));

        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        //昨天
        Calendar yesterday = Calendar.getInstance();

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);


        // 今年
        Calendar thisYear = Calendar.getInstance();

        thisYear.set(Calendar.YEAR, current.get(Calendar.YEAR));
        thisYear.set(Calendar.MONTH, 0);
        thisYear.set(Calendar.DAY_OF_MONTH, 0);
        thisYear.set(Calendar.HOUR_OF_DAY, 0);
        thisYear.set(Calendar.MINUTE, 0);
        thisYear.set(Calendar.SECOND, 0);


        current.setTime(date);

        //今年以前
        if (current.before(thisYear)) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String dateStr = format.format(date);
            return dateStr;
        } else if (current.after(today)) {
            return "今天";
        } else if (current.before(today) && current.after(yesterday)) {
            return "1天前";
        } else {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd");
            String dateStr = format.format(date);
            return dateStr;
        }
    }


    /*
       * 将毫秒秒数转为时分秒
       * */
    public static String secondToTime(int second) {
        second = second / 1000;
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;

            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }

        if (h <= 0) {
            return String.format("%02d", d) + ":" + String.format("%02d", s) + "";
        }

        return String.format("%02d", h) + ":" + String.format("%02d", d) + ":" + String.format("%02d", s) + "";
    }

    public static long timeMillionByTimeFormat(String format,String time){
        SimpleDateFormat sdf = new SimpleDateFormat(format);//yyyy-MM-dd
        long millionSeconds;
        try {
            millionSeconds = sdf.parse(time).getTime();
        } catch (ParseException e) {
            millionSeconds = 0;
            e.printStackTrace();
        }
            return millionSeconds;
    }

    /**
     * 判断当前播放FM时间段
     * @param time 00:00-02:00
     * @return
     */
    public static boolean iscurFmPeriods(String time) {
        String [] timeArray = null;
        if (!TextUtils.isEmpty(time)) {
            timeArray = time.split("-");
            String timeStart = timeArray[0];
            String timeEnd = timeArray[1];
            if ("00:00".equals(timeEnd)) {
                timeEnd = "23:59";
            }

            Glog.i("lbj", "iscurFmPeriods: " + timeEnd);
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            try {
                Date startDate = format.parse(timeStart);
                Date endData = format.parse(timeEnd);
                Date curDate = format.parse(format.format(new Date(System.currentTimeMillis())));
                return endData.getTime() > curDate.getTime() && curDate.getTime() >= startDate.getTime();
            } catch (ParseException e) {
                return false;
            }
        }
        return false;
    }

    public static int getCurrentFmProgress(String timePerid){
        String [] timeArray = null;
        timeArray = timePerid.split("-");
        String timeStart = timeArray[0];
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        int time = 0;
        try {
            time = (int)format.parse(format.format(new Date(System.currentTimeMillis()))).getTime() - (int)format.parse(timeStart).getTime();
//            Glog.i("jys", " current time : " + time);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }

    public static  String getCurrentFmStartTime(){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String currentTime = format.format(new Date(System.currentTimeMillis()));
//        Glog.i("jys", " current time : " + currentTime);
        return currentTime;
    }

    public static  String getCurrentFmStartTime(long time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        String currentTime = format.format(new Date(time));
//        Glog.i("jys", " current time : " + currentTime);
        return currentTime;
    }

    public static long getFmTime(String time){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        try {
            Date date = format.parse(time);
            return date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getEndFmDuration(String timePerid){
        if (TextUtils.isEmpty(timePerid)) {
            return 0;
        }
        String [] timeArray = timePerid.split("-");
        String timeEnd = timeArray[1] + ":00";
        long time = 0;
        try {
            time = getFmTime(timeEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;
    }

    public static long getStartFmDuration(String timePerid){
        if (TextUtils.isEmpty(timePerid)) {
            return 0;
        }
        String [] timeArray = timePerid.split("-");
        String timeStart = timeArray[0] + ":00";
        long time = 0;
        try {
            time = getFmTime(timeStart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return time;
    }

    public static int getCurrentFmDuration(String timePerid){
        String [] timeArray = null;
        timeArray = timePerid.split("-");
        String timeStart = timeArray[0];
        String timeEnd = timeArray[1];
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        int time = 0;
        try {
            time = (int)format.parse(timeEnd).getTime() - (int)format.parse(timeStart).getTime();
//            Glog.i("jys", " End time : " + time);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time;
    }


    /**
     * 当地时间 ---> UTC时间
     * @return
     */
    public static String Local2UTC(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("gmt"));
        String gmtTime = sdf.format(date);
        return gmtTime;
    }
}
