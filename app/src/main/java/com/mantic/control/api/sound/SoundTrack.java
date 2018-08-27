package com.mantic.control.api.sound;

import com.google.gson.annotations.SerializedName;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/25.
 * desc:
 */
public class SoundTrack {
    @SerializedName("__model__") public String model;
    @SerializedName("name") public String name;
    @SerializedName("uri") public String uri;
    @SerializedName("length") public int length;
    @SerializedName("mantic_real_url") public String mantic_real_url;
    @SerializedName("mantic_describe") public String mantic_describe;
    @SerializedName("mantic_album_name") public String mantic_album_name;
    @SerializedName("mantic_image") public String mantic_image;
    @SerializedName("mantic_podcaster_avater") public String mantic_podcaster_avater;
    @SerializedName("mantic_podcaster_key") public String mantic_podcaster_key;
    @SerializedName("mantic_podcaster_name") public String mantic_podcaster_name;

    @Override
    public String toString() {
        return "SoundTrack{" +
                "model='" + model + '\'' +
                ", name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", length=" + length +
                ", mantic_real_url='" + mantic_real_url + '\'' +
                ", mantic_describe='" + mantic_describe + '\'' +
                ", mantic_album_name='" + mantic_album_name + '\'' +
                ", mantic_image='" + mantic_image + '\'' +
                ", mantic_podcaster_avater='" + mantic_podcaster_avater + '\'' +
                ", mantic_podcaster_key='" + mantic_podcaster_key + '\'' +
                ", mantic_podcaster_name='" + mantic_podcaster_name + '\'' +
                '}';
    }
}
