package com.mantic.control.data.jd;

import java.util.Arrays;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/6/2.
 * desc:
 */
public class JdDeviceResult {
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    private int count;
    private List<JdDevice> list;

    public List<JdDevice> getList() {
        return list;
    }

    public void setList(List<JdDevice> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "JdDeviceResult{" +
                "count=" + count +
                ", list=" + list +
                '}';
    }

    public class JdDevice{
        private String p_description;
        private String share_from;
        private String product_id;
        private String status;
        private int device_type;
        private int support_share;
        private String[] sub_devices;
        private String deviceId_ble;
        private String id;
        private int card_set_status;
        private int share_user_cnt;
        private int cid;
        private int main_sub_type;
        private long feed_id;
        private String version;
        private String product_uuid;
        private String[] stream_type_list;
        private String device_name;
        private String p_img_url;
        private int own_flag;
        private String create_time;
        private String access_key;
        private String device_page_type;
        private String cname;
        private String c_img_url;

        public String getP_description() {
            return p_description;
        }

        public void setP_description(String p_description) {
            this.p_description = p_description;
        }

        public String getShare_from() {
            return share_from;
        }

        public void setShare_from(String share_from) {
            this.share_from = share_from;
        }

        public String getProduct_id() {
            return product_id;
        }

        public void setProduct_id(String product_id) {
            this.product_id = product_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getDevice_type() {
            return device_type;
        }

        public void setDevice_type(int device_type) {
            this.device_type = device_type;
        }

        public int getSupport_share() {
            return support_share;
        }

        public void setSupport_share(int support_share) {
            this.support_share = support_share;
        }

        public String[] getSub_devices() {
            return sub_devices;
        }

        public void setSub_devices(String[] sub_devices) {
            this.sub_devices = sub_devices;
        }

        public String getDeviceId_ble() {
            return deviceId_ble;
        }

        public void setDeviceId_ble(String deviceId_ble) {
            this.deviceId_ble = deviceId_ble;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getCard_set_status() {
            return card_set_status;
        }

        public void setCard_set_status(int card_set_status) {
            this.card_set_status = card_set_status;
        }

        public int getShare_user_cnt() {
            return share_user_cnt;
        }

        public void setShare_user_cnt(int share_user_cnt) {
            this.share_user_cnt = share_user_cnt;
        }

        public int getCid() {
            return cid;
        }

        public void setCid(int cid) {
            this.cid = cid;
        }

        public int getMain_sub_type() {
            return main_sub_type;
        }

        public void setMain_sub_type(int main_sub_type) {
            this.main_sub_type = main_sub_type;
        }

        public long getFeed_id() {
            return feed_id;
        }

        public void setFeed_id(long feed_id) {
            this.feed_id = feed_id;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getProduct_uuid() {
            return product_uuid;
        }

        public void setProduct_uuid(String product_uuid) {
            this.product_uuid = product_uuid;
        }

        public String[] getStream_type_list() {
            return stream_type_list;
        }

        public void setStream_type_list(String[] stream_type_list) {
            this.stream_type_list = stream_type_list;
        }

        public String getDevice_name() {
            return device_name;
        }

        public void setDevice_name(String device_name) {
            this.device_name = device_name;
        }

        public String getP_img_url() {
            return p_img_url;
        }

        public void setP_img_url(String p_img_url) {
            this.p_img_url = p_img_url;
        }

        public int getOwn_flag() {
            return own_flag;
        }

        public void setOwn_flag(int own_flag) {
            this.own_flag = own_flag;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getAccess_key() {
            return access_key;
        }

        public void setAccess_key(String access_key) {
            this.access_key = access_key;
        }

        public String getDevice_page_type() {
            return device_page_type;
        }

        public void setDevice_page_type(String device_page_type) {
            this.device_page_type = device_page_type;
        }

        public String getCname() {
            return cname;
        }

        public void setCname(String cname) {
            this.cname = cname;
        }

        public String getC_img_url() {
            return c_img_url;
        }

        public void setC_img_url(String c_img_url) {
            this.c_img_url = c_img_url;
        }

        @Override
        public String toString() {
            return "JdDevice{" +
                    "p_description='" + p_description + '\'' +
                    ", share_from='" + share_from + '\'' +
                    ", product_id='" + product_id + '\'' +
                    ", status='" + status + '\'' +
                    ", device_type=" + device_type +
                    ", support_share=" + support_share +
                    ", sub_devices=" + sub_devices +
                    ", deviceId_ble='" + deviceId_ble + '\'' +
                    ", id='" + id + '\'' +
                    ", card_set_status=" + card_set_status +
                    ", share_user_cnt=" + share_user_cnt +
                    ", cid=" + cid +
                    ", main_sub_type=" + main_sub_type +
                    ", feed_id=" + feed_id +
                    ", version='" + version + '\'' +
                    ", product_uuid='" + product_uuid + '\'' +
                    ", stream_type_list=" + stream_type_list +
                    ", device_name='" + device_name + '\'' +
                    ", p_img_url='" + p_img_url + '\'' +
                    ", own_flag=" + own_flag +
                    ", create_time='" + create_time + '\'' +
                    ", access_key='" + access_key + '\'' +
                    ", device_page_type='" + device_page_type + '\'' +
                    ", cname='" + cname + '\'' +
                    ", c_img_url='" + c_img_url + '\'' +
                    '}';
        }
    }
}
