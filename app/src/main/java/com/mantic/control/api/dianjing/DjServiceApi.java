package com.mantic.control.api.dianjing;

import java.util.List;

import io.reactivex.Flowable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Jia on 2017/5/12.
 */

public interface DjServiceApi {
    @GET(DjUrl.DJMUSIC)
    Flowable<List<DJMusicBean>> getMusicList(@Query("pagenum") int pageNume,
                                             @Query("page") int page,
                                             @Query("categoryId")int categoryId);

}
