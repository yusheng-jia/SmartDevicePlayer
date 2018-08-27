package com.mantic.control.data.jd;

import java.io.Serializable;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/6/1.
 * desc:
 */
public class JdAddress {
    private String err_msg;
    private String err_code;
    private List<Address> data;

    public String getErr_msg() {
        return err_msg;
    }

    public void setErr_msg(String err_msg) {
        this.err_msg = err_msg;
    }

    public String getErr_code() {
        return err_code;
    }

    public void setErr_code(String err_code) {
        this.err_code = err_code;
    }

    public List<Address> getAddressList() {
        return data;
    }

    public void setAddressList(List<Address> addressList) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "JdAddress{" +
                "err_msg='" + err_msg + '\'' +
                ", err_code='" + err_code + '\'' +
                ", data=" + data +
                '}';
    }

    public class Address implements Serializable {
        private int city_id;
        private long id;
        private int town_id;
        private int province_id;
        private String name;
        private int county_id;
        private String full_address;
        private String mobile;
        private String address_detail;

        public int getCity_id() {
            return city_id;
        }

        public void setCity_id(int city_id) {
            this.city_id = city_id;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public int getTown_id() {
            return town_id;
        }

        public void setTown_id(int town_id) {
            this.town_id = town_id;
        }

        public int getProvince_id() {
            return province_id;
        }

        public void setProvince_id(int province_id) {
            this.province_id = province_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getCounty_id() {
            return county_id;
        }

        public void setCounty_id(int county_id) {
            this.county_id = county_id;
        }

        public String getFull_address() {
            return full_address;
        }

        public void setFull_address(String full_address) {
            this.full_address = full_address;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getAddress_detail() {
            return address_detail;
        }

        public void setAddress_detail(String address_detail) {
            this.address_detail = address_detail;
        }

        @Override
        public String toString() {
            return "JdAddress{" +
                    "city_id=" + city_id +
                    ", id=" + id +
                    ", town_id=" + town_id +
                    ", province_id=" + province_id +
                    ", name='" + name + '\'' +
                    ", county_id=" + county_id +
                    ", full_address='" + full_address + '\'' +
                    ", mobile='" + mobile + '\'' +
                    ", address_detail='" + address_detail + '\'' +
                    '}';
        }
    }

}
