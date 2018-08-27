package com.mantic.control.api.beiwa.bean;

import java.util.List;

/**
 * Created by Jia on 2017/5/18.
 */

public class BwCategoryBean {

    public List<TAGS> tags;
    public String err_msg;
    public int err_code;
    public String status;

    @Override
    public String toString() {
        return "BwCategoryBean{" +
                "tags=" + tags +
                ", err_msg='" + err_msg + '\'' +
                ", err_code=" + err_code +
                ", status='" + status + '\'' +
                '}';
    }

    public class TAGS{
        public int tagId;
        public String tagName;

        @Override
        public String toString() {
            return "TAGS{" +
                    "tagId=" + tagId +
                    ", tagName='" + tagName + '\'' +
                    '}';
        }
    }

}
