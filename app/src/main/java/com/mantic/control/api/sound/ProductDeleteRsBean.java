package com.mantic.control.api.sound;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/28.
 * desc:
 */
public class ProductDeleteRsBean {
    private String jsonrpc;
    private int id;
    private ProductDeleteResult result;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ProductDeleteResult getResult() {
        return result;
    }

    public void setResult(ProductDeleteResult result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ProductDeleteRsBean{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", id=" + id +
                ", result=" + result +
                '}';
    }
}
