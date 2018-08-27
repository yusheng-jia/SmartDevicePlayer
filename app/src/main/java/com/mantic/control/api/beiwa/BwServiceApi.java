package com.mantic.control.api.beiwa;

import com.mantic.control.api.beiwa.bean.BwAlbumBean;
import com.mantic.control.api.beiwa.bean.BwCategoryBean;
import com.mantic.control.api.beiwa.bean.BwWordsBean;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;


/**
 * Created by Jia on 2017/5/18.
 */

public interface BwServiceApi {
    /**
     *
     * @return Categoty 广场
     */
    @GET(BwUrl.CATEGORY_TAGLIST)
    Call<BwCategoryBean> getCategoryList();

    /**
     *
     * @param tagId
     * @param page
     * @return 专辑列表
     */
    @POST(BwUrl.ALBUMLIST_BYTAGID)
    @FormUrlEncoded
    Call<BwAlbumBean> getAlbumListByCategoryId(@Field("tagId") String tagId,
                                               @Field("page") String page);

    /**
     *
     * @param albumId
     * @return
     */
    @POST(BwUrl.ALBUMDETAIL_BYALBUMID)
    @FormUrlEncoded
    Call<BwAlbumBean> getAlbumDetailsByAlbumId(@Field("albumId") String albumId);

    /**
     *
     * @param albumId
     * @param page
     * @return 专辑下的作品
     */
    @POST(BwUrl.WORKS_BYALBUMID)
    @FormUrlEncoded
    Call<BwWordsBean> getWordsByAlbumId(@Field("albumId") String albumId, @Field("page") String page);

    /**
     *
     * @param worksId
     * @return 作品详情（播放url）
     */
    @POST(BwUrl.WORKSDETAIL_BYWORKSID)
    @FormUrlEncoded
    Call<BwWordsBean.Word> getWordDetailByWordId(@Field("worksId") String worksId);


}
