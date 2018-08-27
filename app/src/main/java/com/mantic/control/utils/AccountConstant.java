package com.mantic.control.utils;

/**
 * Created by wujiangxia on 2017/4/20.
 */
public class AccountConstant {

    public static final String ONLINE_BASE_URL = "http://120.55.113.52:8081/mantic/api/";
    public static final String BASE_URL = ONLINE_BASE_URL;

    public static final String LABEL_MALE = "男";
    public static final String LABEL_FEMALE = "女";

    public enum Sex {
        MALE(LABEL_MALE), FEMALE(LABEL_FEMALE);

        private String name;

        Sex(String name) {
            this.name = name;
        }

        public static int indexOf(String name) {
            for (Sex sex : Sex.values()) {
                if (sex.name.equals(name)) {
                    return sex.ordinal();
                }
            }
            return MALE.ordinal();
        }

        public String getName() {
            return name;
        }
    }
}
