package com.mantic.control.api.myservice.bean;

import java.util.List;

/**
 * Created by lin on 2018/1/4.
 */

public class MusicServiceBean {
    private List<ServiceBean> MUSIC;

    public List<ServiceBean> getMUSIC() {
        return MUSIC;
    }

    public void setMUSIC(List<ServiceBean> MUSIC) {
        this.MUSIC = MUSIC;
    }

    public class ServiceBean {
        private String LC;
        private List<MyServiceBean> SERVICE;

        public String getLC() {
            return LC;
        }

        public void setLC(String LC) {
            this.LC = LC;
        }

        public List<MyServiceBean> getSERVICE() {
            return SERVICE;
        }

        public void setSERVICE(List<MyServiceBean> SERVICE) {
            this.SERVICE = SERVICE;
        }

        public class MyServiceBean {
            private String ID;
            private String NAME;
            private String DES;
            private String PIC_URL;

            public String getID() {
                return ID;
            }

            public void setID(String ID) {
                this.ID = ID;
            }

            public String getNAME() {
                return NAME;
            }

            public void setNAME(String NAME) {
                this.NAME = NAME;
            }

            public String getDES() {
                return DES;
            }

            public void setDES(String DES) {
                this.DES = DES;
            }

            public String getPIC_URL() {
                return PIC_URL;
            }

            public void setPIC_URL(String PIC_URL) {
                this.PIC_URL = PIC_URL;
            }
        }
    }

}
