package com.mantic.control.api.kaola;

import com.mantic.control.api.kaola.bean.KlAlbumBean;
import com.mantic.control.api.kaola.bean.KlAudioBean;
import com.mantic.control.api.kaola.bean.KlCategoryBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Jia on 2017/5/16.
 */

public interface KaolaServiceApi {
    /**
     *
     * @return 类别
     */
    @GET(KaolaUrl.CATEGORY_LIST)
    Call<KlCategoryBean> getCategoryList();

    /**
     *
     * @param id 2117 头条
     * @return 子类别
     */
    @POST(KaolaUrl.CHILD_LIST)
    @FormUrlEncoded
    Call<KlCategoryBean> getChildListByCid(@Field("cid") int id);

    /**
     *
     * @param id cid 2114 深度
     * @param pageNum 页码（如1）可选参数，非必须参数，默认为1
     * @param pageSize 可选参数，非必须参数，默认为10
     * @param sort 排序，可选参数，非必须参数  0-上线时间降序，1-热度降序(默认)
     * @return 专辑列表
     */
    @POST(KaolaUrl.GETALBUM_BYID)
    @FormUrlEncoded
    Call<KlAlbumBean> getAlbumByCid(@Field("cid") int id,
                         @Field("pagenum") int pageNum,
                         @Field("pagesize") int pageSize,
                         @Field("sorttype") String sort);

    /**
     *
     * @param ids 专辑的id，可以是多个，用逗号隔开   例如:1100000174773,1100000148763
     * @return 专辑详情
     */
    @POST(KaolaUrl.GETALBUMDETAIL_BYALBUMID)
    @FormUrlEncoded
    Call<KlAlbumBean.AlbumDetail> getAlbumDetailsByAlbumId(@Field("albumids") String ids);

    /**
     *
     * @param ids 专辑id 1100000174773
     * @param pageNum 非必须参数，页数，默认=1
     * @param pageSize 非必须参数，每页条数，默认=10
     * @param sort 非必须参数，期数排序，1=正序，-1=倒序
//     * @param audioId  非必须参数 碎片id，根据碎片id定位分页
     * @return 作品列表
     */
    @POST(KaolaUrl.GETAUDIO_BYALBUMID)
    @FormUrlEncoded
    Call<KlAudioBean> getAudiosByAlbumId(@Field("albumId") Long ids,
                                         @Field("pagenum") int pageNum,
                                         @Field("pagesize") int pageSize,
                                         @Field("sorttype") int sort
                                         );


    /**
     *
     * @param audioId 1000002445058
     * @return 作品详情
     */
    @POST(KaolaUrl.GETAUDIODETAIL_BYAUDIOID)
    @FormUrlEncoded
    Call<KlAudioBean.AudioDetail> getAudioDetailByAudioId(@Field("audioId") String audioId);






}
