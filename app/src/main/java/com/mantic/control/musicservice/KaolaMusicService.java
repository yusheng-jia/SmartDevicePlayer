package com.mantic.control.musicservice;

import android.os.Bundle;

import com.mantic.control.api.kaola.KaolaServiceApi;
import com.mantic.control.api.kaola.KlRetrofit;
import com.mantic.control.api.kaola.bean.KlAlbumBean;
import com.mantic.control.api.kaola.bean.KlAudioBean;
import com.mantic.control.api.kaola.bean.KlCategoryBean;
import com.mantic.control.utils.Glog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jia on 2017/5/16.
 */

public class KaolaMusicService implements MyMusicService{
    private static final String TAG = "KaolaMusicService";
    private static final String SERVICEID = "kaola";

    private static final int TYPE_CATEGOROES = 0;
    private static final int TYPE_TAGS = 1;
    public static final int TYPE_ALBUMS = 3;
//    private static final int TYPE_ALBUM = 3;

    private boolean mLoading = false;

    private static final String CATEGORY_ID = "category_id";
    private static final String TAG_NAME = "tag_name";
    public static final String ALBUM_ID = "album_id";

    private IMusicServiceSubItemList mIMusicServiceSubItemList;
    private IMusicServiceAlbum mIMusicServiceAlbum;

    private KlCategoryBean mCategory;
    private KlCategoryBean mTag;
    private KlAlbumBean mAlbum;
    private KlAudioBean mAudio;



    private KaolaServiceApi kaolaServiceApi ;

    public KaolaMusicService() {
        kaolaServiceApi = KlRetrofit.getInstance().create(KaolaServiceApi.class);
    }

    /**
     * 获取广场Catgory
     *
     */
    public void getCategoryList(){
        Glog.i(TAG,"loadCategories mLoading = "+mLoading);
        if(mLoading){
            return;
        }
        Call<KlCategoryBean> call = kaolaServiceApi.getCategoryList();
        call.enqueue(new Callback<KlCategoryBean>() {
            @Override
            public void onResponse(Call<KlCategoryBean> call, Response<KlCategoryBean> response) {
                KlCategoryBean bean = response.body();
                Glog.i(TAG,"KlCategoryBean :" +bean);
                ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
                List<KlCategoryBean.Category> listCategory = new ArrayList<KlCategoryBean.Category>();
                listCategory = bean.list;
                for (int i=0; i<listCategory.size(); i++){
                    final KlCategoryBean.Category category = listCategory.get(i);
                    if(category.id > 0){
                        IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                            @Override
                            public String getItemIconUrl() {
                                return category.img;
                            }

                            @Override
                            public String getItemText() {
                                return category.name;
                            }

                            @Override
                            public Bundle gotoNext() {
                                Bundle bundle = new Bundle();
                                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                bundle.putInt(PRE_DATA_TYPE, TYPE_CATEGOROES);
                                bundle.putLong(CATEGORY_ID,category.id);
                                //mssiFragment.setArguments(bundle);
                                //if (fragment.getActivity() instanceof FragmentEntrust) {
                                //    ((FragmentEntrust) fragment.getActivity()).pushFragment(mssiFragment, PRE_DATA_TYPE+TYPE_CATEGOROES+currCategory.getId());
                                //}
                                return bundle;
                            }

                            @Override
                            public int getNextDataType() {
                                return TYPE_DATA_LIST;
                            }

                            @Override
                            public String getNextDataId() {
                                return PRE_DATA_TYPE+TYPE_CATEGOROES+category.id;
                            }

                            @Override
                            public int getIconType() {
                                return 0;
                            }
                        };
                        subItems.add(subItem);
                    }
                }

                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                }else {
                    mIMusicServiceSubItemList.onEmpty();
                }

                mLoading = false;

            }

            @Override
            public void onFailure(Call<KlCategoryBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
            }
        });

    }

    /**
     * 根据cid 获取TAGLIST
     * @param cid
     */
    public void getTagList(int cid){
        Glog.i(TAG,"loadTagList  mLoading = " + mLoading + "---cid = "+cid);
        if (mLoading) {
            return;
        }

        mLoading = true;

        Call<KlCategoryBean> call = kaolaServiceApi.getChildListByCid(cid);
        call.enqueue(new Callback<KlCategoryBean>() {
            @Override
            public void onResponse(Call<KlCategoryBean> call, Response<KlCategoryBean> response) {
                KlCategoryBean bean = response.body();
                Glog.i(TAG,"getTagList : KlCategoryBean "+bean);

                ArrayList<IMusicServiceSubItem> subTagItems = new ArrayList<IMusicServiceSubItem>();
                List<KlCategoryBean.Category> listTag = new ArrayList<KlCategoryBean.Category>();
                listTag = bean.list;
                for (int i=0; i<listTag.size(); i++){
                    final KlCategoryBean.Category category = listTag.get(i);
                    IMusicServiceSubItem subTag = new IMusicServiceSubItem() {
                        @Override
                        public String getItemIconUrl() {
                            return category.img;
                        }

                        @Override
                        public String getItemText() {
                            return category.name;
                        }

                        @Override
                        public Bundle gotoNext() {
                            Bundle bundle = new Bundle();
                            bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                            bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                            bundle.putLong(CATEGORY_ID,category.id);
                            //mssiFragment.setArguments(bundle);
                            //if (fragment.getActivity() instanceof FragmentEntrust) {
                            //    ((FragmentEntrust) fragment.getActivity()).pushFragment(mssiFragment, PRE_DATA_TYPE+TYPE_CATEGOROES+currCategory.getId());
                            //}
                            return bundle;
                        }

                        @Override
                        public int getNextDataType() {
                            return TYPE_DATA_LIST;
                        }

                        @Override
                        public String getNextDataId() {
                            return PRE_DATA_TYPE+TYPE_CATEGOROES+category.id;
                        }

                        @Override
                        public int getIconType() {
                            return 0;
                        }
                    };
                    subTagItems.add(subTag);
                }

                if(mIMusicServiceSubItemList != null && subTagItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subTagItems);
                }else {
                    mIMusicServiceSubItemList.onEmpty();
                }
                mLoading = false;
            }

            @Override
            public void onFailure(Call<KlCategoryBean> call, Throwable t) {

            }
        });

    }

    public void getAlbumList(int cid, int pageNum, int pageSize){
        Glog.i(TAG,"getAlbumList  mLoading = " + mLoading + "--cid = " + cid + "pageNum" + pageNum + "pageSize "+ pageSize);
        String sort = "1";//排序 默认为1
        mLoading = true;

        Call<KlAlbumBean> call = kaolaServiceApi.getAlbumByCid(cid,pageNum,pageSize,sort);
        call.enqueue(new Callback<KlAlbumBean>() {
            @Override
            public void onResponse(Call<KlAlbumBean> call, Response<KlAlbumBean> response) {
                KlAlbumBean bean = response.body();
                Glog.i(TAG,"KlAlbumBean :" +bean);
                ArrayList<IMusicServiceSubItem> subTagItems = new ArrayList<IMusicServiceSubItem>();
                List<KlAlbumBean.Album> listAlbum = new ArrayList<KlAlbumBean.Album>();
                listAlbum = bean.result.dataList;

                for (int i=0; i<listAlbum.size(); i++){
                    final KlAlbumBean.Album album = listAlbum.get(i);
                    IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {
                        @Override
                        public String getCoverUrl() {
                            return album.img;
                        }

                        @Override
                        public String getAlbumTitle() {
                            return album.name;
                        }

                        @Override
                        public String getAlbumTags() {
                            return album.img;
                        }

                        @Override
                        public String getAlbumIntro() {
                            return album.name;
                        }

                        @Override
                        public long getTotalCount() {
                            return 0;
                        }

                        @Override
                        public long getUpdateAt() {
                            return 0;
                        }

                        @Override
                        public String getAlbumId() {
                            return album.id + "";
                        }

                        @Override
                        public String getMainId() {
                            return null;
                        }

                        @Override
                        public String getSinger() {
                            return "";
                        }

                        @Override
                        public int getType() {
                            return 0;
                        }

                        @Override
                        public String getPlayCount() {
                            return null;
                        }

                        @Override
                        public String getItemIconUrl() {
                            return album.img;
                        }

                        @Override
                        public String getItemText() {
                            return album.name;
                        }

                        @Override
                        public Bundle gotoNext() {
                            //MusicServiceSubItemFragment mssiFragment = new MusicServiceSubItemFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                            bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
                            bundle.putLong(ALBUM_ID,album.id);
                            //mssiFragment.setArguments(bundle);
                            //if (fragment.getActivity() instanceof FragmentEntrust) {
                            //    ((FragmentEntrust) fragment.getActivity()).pushFragment(mssiFragment, PRE_DATA_TYPE+TYPE_ALBUMS+album.getId());
                            //}
                            return bundle;
                        }

                        @Override
                        public int getNextDataType() {
                            return TYPE_DATA_ALBUM;
                        }

                        @Override
                        public String getNextDataId() {
                            return PRE_DATA_TYPE+TYPE_ALBUMS+album.id;
                        }

                        @Override
                        public int getIconType() {
                            return 0;
                        }
                    };
                    subTagItems.add(subItem);
                }
                if(mIMusicServiceSubItemList != null && subTagItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subTagItems);
                }else {
                    mIMusicServiceSubItemList.onEmpty();
                }

                mLoading = false;
            }

            @Override
            public void onFailure(Call<KlAlbumBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
            }
        });
    }

    public void getAudioList(Long ids, int pageNum, int pageSize ){
        Glog.i(TAG,"getAudioList  mLoading = " + mLoading + "--ids = " + ids + "pageNum" + pageNum + "pageSize "+ pageSize);

        int sort = 1;//排序 默认为1
        if (mLoading) {
            return;
        }
        mLoading = true;

        Call<KlAudioBean> call = kaolaServiceApi.getAudiosByAlbumId(ids,pageNum,pageSize,sort);
        call.enqueue(new Callback<KlAudioBean>() {
            @Override
            public void onResponse(Call<KlAudioBean> call, Response<KlAudioBean> response) {
                KlAudioBean bean = response.body();
                Glog.i(TAG,"KlAudioBean :" +bean);

                List<KlAudioBean.Audio> audioList = new ArrayList<KlAudioBean.Audio>();
                ArrayList<IMusicServiceTrackContent> contents = new ArrayList<IMusicServiceTrackContent>();

                audioList = bean.result.dataList;
                for (int i=0; i<audioList.size(); i++){

                    final KlAudioBean.Audio audio = audioList.get(i);
                    IMusicServiceTrackContent content = new IMusicServiceTrackContent() {
                        @Override
                        public String getCoverUrlSmall() {
                            return audio.audioPic;
                        }

                        @Override
                        public String getCoverUrlMiddle() {
                            return audio.audioPic;
                        }

                        @Override
                        public String getCoverUrlLarge() {
                            return audio.audioPic;
                        }

                        @Override
                        public String getTrackTitle() {
                            return audio.andioName;
                        }

                        @Override
                        public int getUrlType() {
                            return IMusicServiceTrackContent.STATIC_URL;
                        }

                        @Override
                        public String getPlayUrl() {
                            return audio.playUrl;
                        }

                        @Override
                        public String getSinger() {
                            return "kaola";
                        }

                        @Override
                        public long getDuration() {
                            return audio.duration;
                        }

                        @Override
                        public long getUpdateAt() {
                            return audio.updateTime;
                        }

                        @Override
                        public String getUri() {
                            return "kaola_" + audio.audioId;
                        }

                        @Override
                        public String getTimePeriods() {
                            return null;
                        }

                        @Override
                        public String getAlbumId() {
                            return null;
                        }
                    };
                    contents.add(content);
                }
                if(mIMusicServiceAlbum != null){
                    mIMusicServiceAlbum.createMusicServiceAlbum(contents);
                }
                mLoading = false;
            }

            @Override
            public void onFailure(Call<KlAudioBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
            }
        });

    }

    @Override
    public String getMusicServiceID() {
        return SERVICEID;
    }

    @Override
    public void setIMusicServiceSubItemListCallBack(IMusicServiceSubItemList callback) {
        mIMusicServiceSubItemList = callback;
    }

    @Override
    public void setIMusicServiceAlbum(IMusicServiceAlbum callback) {
        mIMusicServiceAlbum = callback;
    }

    @Override
    public IMusicServiceAlbum getIMusicServiceAlbum() {
        return this.mIMusicServiceAlbum;
    }

    @Override
    public void exec(Bundle bundle) {
        int type = bundle.getInt(PRE_DATA_TYPE);
        if(type == TYPE_FIRST){
            this.getCategoryList();
        }else if(type == TYPE_CATEGOROES){
            long id = bundle.getLong(CATEGORY_ID);
            int cid = (int) id;
            this.getTagList(cid);
        }else if(type == TYPE_TAGS){
            long id = bundle.getLong(CATEGORY_ID);
            String tag = bundle.getString(TAG_NAME);
            int cid = (int) id;
            this.getAlbumList(cid,1,20);
        }else if(type == TYPE_ALBUMS){
            String id = bundle.getString(ALBUM_ID);
            this.getAudioList(Long.parseLong(id),1,20);
        }
    }

    @Override
    public boolean isRefresh() {
        return false;
    }


    @Override
    public void RefreshAlbumList() {

    }
    @Override
    public void RefreshTrackList() {

    }

}
