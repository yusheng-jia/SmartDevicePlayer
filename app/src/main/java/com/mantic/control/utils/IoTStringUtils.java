package com.mantic.control.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jia on 2017/6/6.
 */

public class IoTStringUtils {
    public static int toInt(String num) {
        try {
            return Integer.parseInt(num);
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isEmpty(String str) {
        if (str == null || str == "" || str.trim().equals(""))
            return true;
        return false;
    }

    public static StringBuffer getBuffer() {
        return new StringBuffer(50);
    }

    public static StringBuffer getBuffer(int length) {
        return new StringBuffer(length);
    }

    public static String getStrDate(String longDate, String format) {
        if (isEmpty(longDate))
            return "";
        long time = Long.parseLong(longDate);
        Date date = new Date(time);
        return getStrDate(date, format);
    }

    public static String getStrDate(long time, String format) {
        Date date = new Date(time);
        return getStrDate(date, format);
    }

    public static String getStrDate() {
        SimpleDateFormat dd = new SimpleDateFormat("yyyy-MM-dd");
        return dd.format(new Date());
    }

    public static String getStrDate(Date date, String formate) {
        SimpleDateFormat dd = new SimpleDateFormat(formate);
        return dd.format(date);
    }

    public static String utcToLocalTime(String utcTime) {
        String localTime = "";

        return localTime;
    }

    public static String sqliteEscape(String keyWord) {
        keyWord = keyWord.replace("/", "//");
        keyWord = keyWord.replace("'", "''");
        keyWord = keyWord.replace("[", "/[");
        keyWord = keyWord.replace("]", "/]");
        keyWord = keyWord.replace("%", "/%");
        keyWord = keyWord.replace("&", "/&");
        keyWord = keyWord.replace("_", "/_");
        keyWord = keyWord.replace("(", "/(");
        keyWord = keyWord.replace(")", "/)");
        return keyWord;
    }


    public static String sqliteUnEscape(String keyWord) {
        keyWord = keyWord.replace("//", "/");
        keyWord = keyWord.replace("''", "'");
        keyWord = keyWord.replace("/[", "[");
        keyWord = keyWord.replace("/]", "]");
        keyWord = keyWord.replace("/%", "%");
        keyWord = keyWord.replace("/&", "&");
        keyWord = keyWord.replace("/_", "_");
        keyWord = keyWord.replace("/(", "(");
        keyWord = keyWord.replace("/)", ")");
        return keyWord;
    }

    public static String getStrFomat(String str, int length, boolean isPoints) {
        String result = "";

        if (str.length() > length) {
            result = str.substring(0, length);
            if (isPoints) {
                result = result + "...";
            }
        } else {
            result = str;
        }

        return result;

    }

    public static Map<String, String> parseQRCode(String str) {
        Map<String, String> result = new HashMap<>();
        String query = "";
        try {
            URL url = new URL(str);
            query = url.getQuery();
            if (!TextUtils.isEmpty(query)) {
                String[] params = query.split("&");
                if (params != null && params.length > 0) {
                    for (String param : params) {
                        String[] keyAndValue = param.split("=");
                        if (keyAndValue.length == 2) {
                            result.put(keyAndValue[0], URLDecoder.decode(keyAndValue[1], "UTF-8"));
                        }
                    }
                }
            }
            result.put("url", str);
        } catch (MalformedURLException e) {
            //e.printStackTrace();
            query = str;
            if (!TextUtils.isEmpty(query)) {
                String[] params = query.split("&");
                if (params != null && params.length > 0) {
                    for (String param : params) {
                        String[] keyAndValue = param.split("=");
                        if (keyAndValue.length == 2) {
                            result.put(keyAndValue[0], keyAndValue[1]);
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
