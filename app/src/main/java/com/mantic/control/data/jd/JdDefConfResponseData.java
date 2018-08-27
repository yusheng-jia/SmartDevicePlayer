package com.mantic.control.data.jd;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/6/5.
 * desc:
 */
public class JdDefConfResponseData {
    private Switch configInfo;
    private JdAddress.Address address;
    private boolean is_addr_del;

    public Switch getConfigInfo() {
        return configInfo;
    }

    public void setConfigInfo(Switch configInfo) {
        this.configInfo = configInfo;
    }

    public JdAddress.Address getAddress() {
        return address;
    }

    public void setAddress(JdAddress.Address address) {
        this.address = address;
    }

    public boolean isIs_addr_del() {
        return is_addr_del;
    }

    public void setIs_addr_del(boolean is_addr_del) {
        this.is_addr_del = is_addr_del;
    }

    @Override
    public String toString() {
        return "JdDefConfResponseData{" +
                "configInfo=" + configInfo +
                ", address=" + address +
                ", is_addr_del=" + is_addr_del +
                '}';
    }

    public class Switch{
        private int switch_btn;

        public int getSwitch_btn() {
            return switch_btn;
        }

        public void setSwitch_btn(int switch_btn) {
            this.switch_btn = switch_btn;
        }
    }

}
