package com.mantic.control.musicservice;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.api.beiwa.bean.BwAlbumBean;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.TimeUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.AlbumList;
import com.ximalaya.ting.android.opensdk.model.album.Announcer;
import com.ximalaya.ting.android.opensdk.model.album.BatchAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.DiscoveryRecommendAlbums;
import com.ximalaya.ting.android.opensdk.model.album.DiscoveryRecommendAlbumsList;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategory;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerCategoryList;
import com.ximalaya.ting.android.opensdk.model.announcer.AnnouncerList;
import com.ximalaya.ting.android.opensdk.model.category.Category;
import com.ximalaya.ting.android.opensdk.model.category.CategoryList;
import com.ximalaya.ting.android.opensdk.model.live.program.Program;
import com.ximalaya.ting.android.opensdk.model.live.program.ProgramList;
import com.ximalaya.ting.android.opensdk.model.live.provinces.Province;
import com.ximalaya.ting.android.opensdk.model.live.provinces.ProvinceList;
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio;
import com.ximalaya.ting.android.opensdk.model.live.radio.RadioList;
import com.ximalaya.ting.android.opensdk.model.live.schedule.Schedule;
import com.ximalaya.ting.android.opensdk.model.live.schedule.ScheduleList;
import com.ximalaya.ting.android.opensdk.model.ranks.Rank;
import com.ximalaya.ting.android.opensdk.model.ranks.RankAlbumList;
import com.ximalaya.ting.android.opensdk.model.ranks.RankItem;
import com.ximalaya.ting.android.opensdk.model.ranks.RankList;
import com.ximalaya.ting.android.opensdk.model.ranks.RankTrackList;
import com.ximalaya.ting.android.opensdk.model.tag.Tag;
import com.ximalaya.ting.android.opensdk.model.tag.TagList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by root on 17-4-21.
 */
public class XimalayaSoundData implements MyMusicService {
    private static final String TAG = "XimalayaSoundData";
    public static String mAppSecret = "fadd9f655df9480179e0b3181be58fa2";

    private Context mContext;
    private static final int TYPE_MAIN = 0;
    private static final int TYPE_CATEGOROES = 1;
    private static final int TYPE_TAGS = 2;
    public static final int TYPE_ALBUMS = 3;
    private static final int TYPE_ALBUM = 4;

    private static final String MAIN_ID = "main_id";
    private static final String CATEGORY_ID = "category_id";
    private static final String TAG_NAME = "tag_name";
    public static final String ALBUM_ID = "album_id";

    public static final String NEXT_TYPE = "next_type";

    private CommonRequest mXimalaya;

    private static final String[] CATEGORY_MAIN = {"分类","广播","榜单","主播"};
    private static final String[] CATEGORY_MAIN_NOFM = {"分类","广播","榜单","主播"};
    private static final String[] BROADCAST_NAME = {"国家台","省市台","网络台","排行榜"};

    private List<Category> mCategories= new ArrayList<Category>();

    private boolean mLoading = false;

    private TagList mTagList = null;
    private AlbumList mAlbumList = null;
    private TrackList mTrackHotList = null;

    private IMusicServiceSubItemList mIMusicServiceSubItemList;
    private IMusicServiceAlbum mIMusicServiceAlbum;

    public XimalayaSoundData(Context context){
        this.init(context);
    }

    /*init ximalaya*/
    public void init(Context context) {
        mContext = context;
        mXimalaya = CommonRequest.getInstanse();
        mXimalaya.init(context, mAppSecret);
    }

    @Override
    public String getMusicServiceID() {
        return this.mAppSecret;
    }

    @Override
    public void setIMusicServiceSubItemListCallBack(IMusicServiceSubItemList callback) {
        this.mIMusicServiceSubItemList = callback;
    }

    @Override
    public void setIMusicServiceAlbum(IMusicServiceAlbum callback) {
        this.mIMusicServiceAlbum = callback;
    }

    @Override
    public IMusicServiceAlbum getIMusicServiceAlbum() {
        return this.mIMusicServiceAlbum;
    }

    @Override
    public void exec(Bundle bundle) {
        int type = bundle.getInt(PRE_DATA_TYPE); //上一级type
        String name = bundle.getString(MAIN_ID);
        Glog.i(TAG,"name == " + name);
        Glog.i(TAG,"type == " + type);
        if(type != -1 && TextUtils.isEmpty(name)) {
            if(mIMusicServiceAlbum != null){
                mIMusicServiceAlbum.onError(404, "No data!");
            }
            mLoading = false;
            canRefresh = false;
            return;
        }
        switch (type){
            case TYPE_FIRST:
                loadMain();
                break;
            case TYPE_MAIN:
                if (name.equals(CATEGORY_MAIN[0])){
                    loadCategories();
                }else if (name.equals(CATEGORY_MAIN[1])){
                    loadRadioCategory();
                }else if (name.equals(CATEGORY_MAIN[2])){
                    loadRankList();
                }else if (name.equals(CATEGORY_MAIN[3])){
                    loadAnnouncerCategory();
                }
                break;
            case TYPE_CATEGOROES:
                if (name.equals(CATEGORY_MAIN[0])){
                    long id = bundle.getLong(CATEGORY_ID);
                    loadTagList(id);
                }else if (name.equals(CATEGORY_MAIN[1])){
                    String radio = bundle.getString(CATEGORY_ID);
                    loadRadioTagList(radio);
                }else if (name.equals(CATEGORY_MAIN[2])){
//                    loadRankList();
                }else if (name.equals(CATEGORY_MAIN[3])){
                    long id = bundle.getLong(CATEGORY_ID);
                    loadAnnouncerList(id);
                }
                break;
            case TYPE_TAGS:
                if (name.equals(CATEGORY_MAIN[0])){
                    long id = bundle.getLong(CATEGORY_ID);
                    String tag = bundle.getString(TAG_NAME);
                    loadAlbumList(id,tag);
                }else if (name.equals(CATEGORY_MAIN[1])){
                    Long id = bundle.getLong(TAG_NAME);
                    loadProvincesRadioList(id);
                }else if (name.equals(CATEGORY_MAIN[2])){
                    String rank_key = bundle.getString(TAG_NAME);
                    String next_type = bundle.getString(NEXT_TYPE);
                    if (next_type.equals("track")){
                        loadRankAlbum(rank_key);
                    }else if (next_type.equals("album")){
                        loadRankAlbums(rank_key);
                    }
                }else if (name.equals(CATEGORY_MAIN[3])){
                    long id = bundle.getLong(CATEGORY_ID);
                    loadAnnouncerAlbums(id);
                }
                break;
            case TYPE_ALBUMS:
                if (name.equals(CATEGORY_MAIN[0])){
                    String id = bundle.getString(ALBUM_ID);
                    loadAlbum(id);
                }else if (name.equals(CATEGORY_MAIN[1])){
                    String id = bundle.getString(ALBUM_ID);
                    loadRadioPrograms(id);
                }else if (name.equals(CATEGORY_MAIN[2])){
                    String id = bundle.getString(ALBUM_ID);
                    loadAlbum(id);
                }else if (name.equals(CATEGORY_MAIN[3])){
                    String id = bundle.getString(ALBUM_ID);
                    loadAlbum(id);
                }
                break;
        }
    }

    private void loadMain(){ //喜马拉雅入口（分类，广播，榜单，主播）
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;
        ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        for (final String name : CATEGORY_MAIN) {
            if (!name.equals(CATEGORY_MAIN[1])){
                IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                    @Override
                    public String getItemIconUrl() {
                        return getMainIcon(name);
                    }

                    @Override
                    public String getItemText() {
                        return name;
                    }

                    @Override
                    public Bundle gotoNext() {
                        Bundle bundle = new Bundle();
                        bundle.putString(MY_MUSIC_SERVICE_ID, getMusicServiceID());
                        bundle.putInt(PRE_DATA_TYPE, TYPE_MAIN);
                        bundle.putString(MAIN_ID, name);
                        return bundle;
                    }

                    @Override
                    public int getNextDataType() {
                        return TYPE_DATA_LIST;
                    }

                    @Override
                    public String getNextDataId() {
                        return PRE_DATA_TYPE + TYPE_MAIN + name;
                    }

                    @Override
                    public int getIconType() {
                        return 1;
                    }
                };
                subItems.add(subItem);
            }

        }
        if(mIMusicServiceSubItemList != null && subItems.size() > 0){
            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
        }

        mLoading = false;

    }


    private void loadCategories() { //分类
        Glog.i(TAG,"loadCategories mLoading = "+mLoading);
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getCategories(map, new IDataCallBack<CategoryList>() {
            @Override
            public void onSuccess(CategoryList object) {
                if (object != null && object.getCategories() != null) {
                    mCategories.clear();
                    mCategories.addAll(object.getCategories());
//                    mCategoryAdapter.notifyDataSetChanged();
                }

                ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
                for(int i=0;i < mCategories.size(); i++) {
					/*获取分类名*/

                    final Category currCategory = mCategories.get(i);
                    Glog.i(TAG,"loadCategories  onSuccess  CategoryName = " + currCategory.getCategoryName()+"---id = "+currCategory.getId()+"---getItemIconUrl = "+currCategory.getCoverUrlSmall());
                    IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                        @Override
                        public String getItemIconUrl() {
                            return currCategory.getCoverUrlSmall();
                        }

                        @Override
                        public String getItemText() {
                            return currCategory.getCategoryName();
                        }

                        @Override
                        public Bundle gotoNext() {
                            Bundle bundle = new Bundle();
                            bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                            bundle.putInt(PRE_DATA_TYPE, TYPE_CATEGOROES);
                            bundle.putLong(CATEGORY_ID,currCategory.getId());
                            bundle.putString(MAIN_ID,CATEGORY_MAIN[0]);
                            return bundle;
                        }

                        @Override
                        public int getNextDataType() {
                            return TYPE_DATA_LIST;
                        }

                        @Override
                        public String getNextDataId() {
                            return PRE_DATA_TYPE+TYPE_CATEGOROES+currCategory.getId();
                        }

                        @Override
                        public int getIconType() {
                            return 0;
                        }
                    };
                    if (subItem.getItemText().equals("其他")){
                        break;
                    }
                    subItems.add(subItem);
                }
                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                }else {
                    mIMusicServiceSubItemList.onEmpty();
                }

                mLoading = false;
            }

            @Override
            public void onError(int code, String message) {
                mLoading = false;
                if(mIMusicServiceSubItemList != null){
                    mIMusicServiceSubItemList.onError(code,message);
                }
            }
        });
    }

    private void getGuessLikeAlbum(){ // 猜你喜欢
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.LIKE_COUNT, "3");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                for (int i = 0; i< gussLikeAlbumList.getAlbumList().size(); i++){
                    Album album = gussLikeAlbumList.getAlbumList().get(i);
                    Glog.i(TAG,"getCategoryId" + album.getCategoryId());
                    Glog.i(TAG,"getCategoryName" + album.getAlbumTitle());
                }
            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }


    private void loadProvincesList(){ //省市列表
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getProvinces(map, new IDataCallBack<ProvinceList>() {
            @Override
            public void onSuccess(ProvinceList provinceList) {
                ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();

                for (int i = 0; i< provinceList.getProvinceList().size(); i++){
                    final Province province = provinceList.getProvinceList().get(i);
//                    Glog.i(TAG,"province.getProvinceName:"+ province.getProvinceName());
//                    Glog.i(TAG,"province.getCreatedAt:"+ province.getCreatedAt());
//                    Glog.i(TAG,"province.getProvinceCode:"+ province.getProvinceCode());
//                    Glog.i(TAG,"province.getProvinceId:"+ province.getProvinceId());
                    IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                        @Override
                        public String getItemIconUrl() {
                            return "";
                        }

                        @Override
                        public String getItemText() {
                            return province.getProvinceName();
                        }

                        @Override
                        public Bundle gotoNext() {
                            Bundle bundle = new Bundle();
                            bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                            bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                            bundle.putLong(TAG_NAME, province.getProvinceCode());
                            bundle.putString(MAIN_ID,CATEGORY_MAIN[1]);
                            return bundle;
                        }

                        @Override
                        public int getNextDataType() {
                            return TYPE_DATA_LIST;
                        }

                        @Override
                        public String getNextDataId() {
                            return PRE_DATA_TYPE+TYPE_MAIN + province.getProvinceCode();
                        }

                        @Override
                        public int getIconType() {
                            return 0;
                        }
                    };
                    subItems.add(subItem);
                }
                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                }else {
                    mIMusicServiceSubItemList.onEmpty();
                }

                mLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                subItemListOnError(i,s);
            }
        });
    }

    private void loadProvincesRadioList(long provinceCode){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOTYPE, 2+"");
        map.put(DTransferConstants.PROVINCECODE, provinceCode+"");
        map.put(DTransferConstants.PAGE, 1+"");
        CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
            @Override
            public void onSuccess(RadioList radioList) {
                loadRadioList(radioList);
            }

            @Override
            public void onError(int i, String s) {
                subItemListOnError(i,s);
            }
        });
    }


    private void loadRadioCategory(){ //广播
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;
        ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        for (final String name : BROADCAST_NAME) {
            IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                @Override
                public String getItemIconUrl() {
                    return getFmIcon(name);
                }

                @Override
                public String getItemText() {
                    return name;
                }

                @Override
                public Bundle gotoNext() {
                    Bundle bundle = new Bundle();
                    bundle.putString(MY_MUSIC_SERVICE_ID, getMusicServiceID());
                    bundle.putInt(PRE_DATA_TYPE, TYPE_CATEGOROES);
                    bundle.putString(CATEGORY_ID, name);
                    bundle.putString(MAIN_ID, CATEGORY_MAIN[1]);
                    return bundle;
                }

                @Override
                public int getNextDataType() {
                    return TYPE_DATA_LIST;
                }

                @Override
                public String getNextDataId() {
                    return PRE_DATA_TYPE + TYPE_MAIN + name;
                }

                @Override
                public int getIconType() {
                    return 1;
                }
            };
            subItems.add(subItem);
        }
        if(mIMusicServiceSubItemList != null && subItems.size() > 0){
            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
        }else {
            mIMusicServiceSubItemList.onEmpty();
        }

        mLoading = false;
    }

    private void loadRadioTagList(String name){
        if (name.equals(BROADCAST_NAME[0])){//国家台
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.RADIOTYPE, 1+"");
            map.put(DTransferConstants.PAGE, 1+"");
            CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
                @Override
                public void onSuccess(RadioList radioList) {
                    loadRadioList(radioList);
                }

                @Override
                public void onError(int i, String s) {

                }
            });
        }else if (name.equals(BROADCAST_NAME[1])){ //省市台
            loadProvincesList();
        }else if (name.equals(BROADCAST_NAME[2])){//网络台
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.RADIOTYPE, 3+"");
            map.put(DTransferConstants.PAGE, 1+"");
            CommonRequest.getRadios(map, new IDataCallBack<RadioList>() {
                @Override
                public void onSuccess(RadioList radioList) {
                    loadRadioList(radioList);
                }

                @Override
                public void onError(int i, String s) {
                    subItemListOnError(i,s);
                }
            });
        }else if (name.equals(BROADCAST_NAME[3])){//直播电台排行榜
            Map<String, String> map = new HashMap<String, String>();
            map.put(DTransferConstants.RADIO_COUNT, 20+"");
            CommonRequest.getRankRadios(map, new IDataCallBack<RadioList>() {
                @Override
                public void onSuccess(RadioList radioList) {
                    loadRadioList(radioList);
                }

                @Override
                public void onError(int i, String s) {
                    subItemListOnError(i,s);
                }
            });
        }

    }

    private void loadRadioList(RadioList radioList){ //广播电台列表
        Glog.i(TAG,"loadRadioList...");
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;
        int totalCount = radioList.getTotalCount();
        int totalPage = radioList.getTotalPage();
        int currentPage = radioList.getCurrentPage();
        List<Radio> radios = radioList.getRadios();
        ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        for (int i=0; i< radios.size(); i++){
            final Radio radio = radios.get(i);
            final int count = radios.size();

            Glog.i(TAG,"current Program name " + radio.getProgramName());
            Glog.i(TAG,"current schedule_id " + radio.getScheduleID());
            Glog.i(TAG,"current Program rate24_aac_url " + radio.getRate24AacUrl());

            IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {
                @Override
                public String getCoverUrl() {
                    return radio.getCoverUrlLarge();
                }

                @Override
                public String getAlbumTitle() {
                    return radio.getRadioName();
                }

                @Override
                public String getAlbumTags() {
                    return radio.getRadioName();
                }

                @Override
                public String getAlbumIntro() {
                    return radio.getRadioDesc();
                }

                @Override
                public long getTotalCount() {
                    return count;
                }

                @Override
                public long getUpdateAt() {
                    return radio.getUpdateAt();
                }

                @Override
                public String getAlbumId() {
                    return radio.getDataId()+"";
                }

                @Override
                public String getMainId() {
                    return CATEGORY_MAIN[1];
                }

                @Override
                public String getSinger() {
                    return "";
                }

                @Override
                public int getType() {
                    return 2; //广播
                }

                @Override
                public String getPlayCount() {
                    return null;
                }

                @Override
                public String getItemIconUrl() {
                    return radio.getCoverUrlSmall();
                }

                @Override
                public String getItemText() {
                    return radio.getRadioName();
                }

                @Override
                public Bundle gotoNext() {
                    //MusicServiceSubItemFragment mssiFragment = new MusicServiceSubItemFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                    bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
                    bundle.putString(ALBUM_ID,radio.getDataId()+"");
                    bundle.putString(MAIN_ID,CATEGORY_MAIN[1]);
                    return bundle;
                }

                @Override
                public int getNextDataType() {
                    return TYPE_DATA_ALBUM;
                }

                @Override
                public String getNextDataId() {
                    return PRE_DATA_TYPE+TYPE_ALBUMS + radio.getDataId();
                }

                @Override
                public int getIconType() {
                    return 0;
                }
            };
            subItems.add(subItem);

        }
        if(mIMusicServiceSubItemList != null && subItems.size() > 0){
            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
        }else {
            mIMusicServiceSubItemList.onEmpty();
        }

        mLoading = false;

    }


    private void loadRadioPrograms(String radioId){
        Glog.i(TAG,"loadRadioPrograms : radioId" +radioId);
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;

        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RADIOID, radioId);
//        map.put(DTransferConstants.WEEKDAY, Constants.WeekDay.MONDAY);// 默认今天
        CommonRequest.getSchedules(map, new IDataCallBack<ScheduleList>() {
            @Override
            public void onSuccess(ScheduleList scheduleList) {
                List<Schedule> schedules = scheduleList.getmScheduleList();
                ArrayList<IMusicServiceTrackContent> trackContents = new ArrayList<IMusicServiceTrackContent>();

                for (int i = 0; i < schedules.size(); i++){
                    final Schedule schedule = schedules.get(i); //一天节目排期
                    final Program program = schedule.getRelatedProgram(); //相关节目
                    Glog.i(TAG,"program name : " + program.getProgramName());

                    IMusicServiceTrackContent content = new IMusicServiceTrackContent(){

                        @Override
                        public String getCoverUrlSmall() {
                            return program.getBackPicUrl();
                        }

                        @Override
                        public String getCoverUrlMiddle() {
                            return program.getBackPicUrl();
                        }

                        @Override
                        public String getCoverUrlLarge() {
                            return program.getBackPicUrl();
                        }

                        @Override
                        public String getTrackTitle() {
                            return program.getProgramName();
                        }

                        @Override
                        public int getUrlType() {
                            return IMusicServiceTrackContent.STATIC_URL;
                        }

                        @Override
                        public String getPlayUrl() {
                            return schedule.getListenBackUrl();
                        }

                        @Override
                        public String getSinger() {
                            return program.getProgramName();
                        }

                        @Override
                        public long getDuration() {
                            return TimeUtil.timeMillionByTimeFormat("hh:mm",schedule.getEndTime()) - TimeUtil.timeMillionByTimeFormat("hh:mm",schedule.getStartTime());
                        }

                        @Override
                        public long getUpdateAt() {
                            return schedule.getUpdateAt();
                        }

                        @Override
                        public String getUri() {
                            return "ximalaya_" + program.getProgramId();
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
                    trackContents.add(content);
                }
                if(mIMusicServiceAlbum != null){
                    mIMusicServiceAlbum.createMusicServiceAlbum(trackContents);
                }
                mLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                albumOnError(i,s);
            }
        });

    }

    private void loadRankList(){
        Glog.i(TAG,"loadRankList...");
        if (mLoading) {
            canRefresh = false;
            return;
        }

        mLoading = true;
        canRefresh = false;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RANK_TYPE, 1+""); // 1 节目
        CommonRequest.getRankList(map, new IDataCallBack<RankList>() {
            @Override
            public void onSuccess(RankList rankList) {
                Glog.i(TAG,"rankList" + rankList);
                ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
                final List<Rank> ranks = rankList.getRankList();
                for (int i=0; i<ranks.size(); i++){
                    final Rank rank = ranks.get(i);


                    if (rank.getRankContentType().equals("track")){
                        IMusicServiceSubItemAlbum subItem1 = new IMusicServiceSubItemAlbum() {
                            @Override
                            public String getCoverUrl() {
                                return rank.getCoverUrl();
                            }

                            @Override
                            public String getAlbumTitle() {
                                return rank.getRankTitle();
                            }

                            @Override
                            public String getAlbumTags() {
                                return rank.getRankTitle();
                            }

                            @Override
                            public String getAlbumIntro() {
                                return rank.getRankSubTitle();
                            }

                            @Override
                            public long getTotalCount() {
                                return rank.getRankItemNum();
                            }

                            @Override
                            public long getUpdateAt() {
                                return 0;
                            }

                            @Override
                            public String getAlbumId() {
                                return rank.getRankKey();
                            }

                            @Override
                            public String getMainId() {
                                return CATEGORY_MAIN[2];
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
                                return rank.getCoverUrl();
                            }

                            @Override
                            public String getItemText() {
                                return rank.getRankTitle();
                            }

                            @Override
                            public Bundle gotoNext() {
                                Bundle bundle = new Bundle();
                                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                                bundle.putString(TAG_NAME,rank.getRankKey());
                                bundle.putString(NEXT_TYPE,rank.getRankContentType());
                                bundle.putString(MAIN_ID,CATEGORY_MAIN[2]);
                                return bundle;
                            }

                            @Override
                            public int getNextDataType() {
                                return TYPE_DATA_ALBUM;
                            }

                            @Override
                            public String getNextDataId() {
                                return PRE_DATA_TYPE+TYPE_ALBUMS + rank.getRankKey();
                            }

                            @Override
                            public int getIconType() {
                                return 0;
                            }
                        };
                        subItems.add(subItem1);
                    }else {
                        IMusicServiceSubItem subItem2 = new IMusicServiceSubItem() {
                            @Override
                            public String getItemIconUrl() {
                                return rank.getCoverUrl();
                            }

                            @Override
                            public String getItemText() {
                                return rank.getRankTitle();
                            }

                            @Override
                            public Bundle gotoNext() {
                                Bundle bundle = new Bundle();
                                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                                bundle.putString(TAG_NAME,rank.getRankKey());
                                bundle.putString(NEXT_TYPE,rank.getRankContentType());
                                bundle.putString(MAIN_ID,CATEGORY_MAIN[2]);
                                return bundle;
                            }

                            @Override
                            public int getNextDataType() {
                                    return TYPE_DATA_LIST;
                            }

                            @Override
                            public String getNextDataId() {
                                return PRE_DATA_TYPE+TYPE_TAGS + rank.getRankKey();
                            }

                            @Override
                            public int getIconType() {
                                return 0;
                            }
                        };
                        subItems.add(subItem2);
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
            public void onError(int i, String s) {
                subItemListOnError(i,s);
            }
        });

    }


    public void loadRankAlbums(String rankKey){
        Glog.i(TAG,"loadRankAlbums  rankkey: "+ rankKey);
        if (mLoading) {
            canRefresh = false;
            return;
        }

        mLoading = true;
        canRefresh = false;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RANK_KEY, rankKey);
        CommonRequest.getRankAlbumList(map,
            new IDataCallBack<RankAlbumList>() {
                @Override
                public void onSuccess(RankAlbumList rankAlbumList) {
                    List<Album> rankAlbums = rankAlbumList.getRankAlbumList();
                    ArrayList<IMusicServiceSubItem> rankalbumList = new ArrayList<IMusicServiceSubItem>();
                    for (int i = 0; i < rankAlbums.size(); i++){

                        final Album album = rankAlbums.get(i);
                        IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {
                            @Override
                            public String getCoverUrl() {
                                return album.getCoverUrlSmall();
                            }

                            @Override
                            public String getAlbumTitle() {
                                return album.getAlbumTitle();
                            }

                            @Override
                            public String getAlbumTags() {
                                return album.getAlbumTags();
                            }

                            @Override
                            public String getAlbumIntro() {
                                return album.getAlbumIntro();
                            }

                            @Override
                            public long getTotalCount() {
                                return album.getIncludeTrackCount();
                            }

                            @Override
                            public long getUpdateAt() {
                                return album.getUpdatedAt();
                            }

                            @Override
                            public String getAlbumId() {
                                return album.getId() + "";
                            }

                            @Override
                            public String getMainId() {
                                return CATEGORY_MAIN[0];
                            }

                            @Override
                            public String getSinger() {
                                return album.getAnnouncer().getNickname();
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
                                return album.getCoverUrlSmall();
                            }

                            @Override
                            public String getItemText() {
                                return album.getAlbumTitle();
                            }

                            @Override
                            public Bundle gotoNext() {
                                Bundle bundle = new Bundle();
                                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
                                bundle.putLong(ALBUM_ID,album.getId());
                                bundle.putString(MAIN_ID,CATEGORY_MAIN[0]);
                                return bundle;
                            }

                            @Override
                            public int getNextDataType() {
                                return TYPE_DATA_ALBUM;
                            }

                            @Override
                            public String getNextDataId() {
                                return PRE_DATA_TYPE+TYPE_ALBUMS+album.getId();
                            }

                            @Override
                            public int getIconType() {
                                return 0;
                            }
                        };
                        rankalbumList.add(subItem);
                    }
                    if(mIMusicServiceSubItemList != null && rankalbumList.size() > 0){
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(rankalbumList);
                    }else {
                        mIMusicServiceSubItemList.onEmpty();
                    }

                    mLoading = false;
                }

                @Override
                public void onError(int i, String s) {
                    subItemListOnError(i,s);
                }
            });
    }

    public void loadRankAlbum(final String rankKey){
        Glog.i(TAG,"loadRankAlbum  rankkey: "+ rankKey);
        if (mLoading) {
            return;
        }

        mLoading = true;
        canRefresh = false;
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.RANK_KEY, rankKey);
        CommonRequest.getRankTrackList(map,
            new IDataCallBack<RankTrackList>() {
                @Override
                public void onSuccess(RankTrackList rankTrackList) {
                    ArrayList<IMusicServiceTrackContent> trackContents = new ArrayList<IMusicServiceTrackContent>();
                    List<Track> tracks = rankTrackList.getTrackList();
                    Glog.i(TAG,"tracks" + tracks);
                    for (int i = 0; i < tracks.size(); i++){
                        final Track track = tracks.get(i);

                        IMusicServiceTrackContent content = new IMusicServiceTrackContent(){

                            @Override
                            public String getCoverUrlSmall() {
                                return track.getCoverUrlSmall();
                            }

                            @Override
                            public String getCoverUrlMiddle() {
                                return track.getCoverUrlMiddle();
                            }

                            @Override
                            public String getCoverUrlLarge() {
                                return track.getCoverUrlLarge();
                            }

                            @Override
                            public String getTrackTitle() {
                                return track.getTrackTitle();
                            }

                            @Override
                            public int getUrlType() {
                                return IMusicServiceTrackContent.STATIC_URL;
                            }

                            @Override
                            public String getPlayUrl() {
                                return track.getPlayUrl32();
                            }

                            @Override
                            public String getSinger() {
                                return track.getAnnouncer().getNickname();
                            }

                            @Override
                            public long getDuration() {
                                return track.getDuration()*1000;
                            }

                            @Override
                            public long getUpdateAt() {
                                return track.getUpdatedAt();
                            }

                            @Override
                            public String getUri() {
                                return "ximalaya:"+ track.getKind() + ":" + track.getDataId();
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
                        trackContents.add(content);
                    }
                    if(mIMusicServiceAlbum != null){
                        mIMusicServiceAlbum.createMusicServiceAlbum(trackContents);
                    }

                    mLoading = false;
                }

                @Override
                public void onError(int i, String s) {
                    albumOnError(i,s);
                }
            });
    }


    private void loadAnnouncerCategory(){
        Glog.i(TAG,"loadAnnouncerCategory....");
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;
        Map<String, String> map = new HashMap<String, String>();
        CommonRequest.getAnnouncerCategoryList(map, new IDataCallBack<AnnouncerCategoryList>() {
            @Override
            public void onSuccess(AnnouncerCategoryList announcerCategoryList) {

                List<AnnouncerCategory> announcerList = announcerCategoryList.getList();
                ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();

                for (int i = 0; i < announcerList.size(); i++){
                    final AnnouncerCategory announcer = announcerList.get(i);

                    IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                        @Override
                        public String getItemIconUrl() {
                            return "";
                        }

                        @Override
                        public String getItemText() {
                            return announcer.getVcategoryName();
                        }

                        @Override
                        public Bundle gotoNext() {
                            Bundle bundle = new Bundle();
                            bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                            bundle.putInt(PRE_DATA_TYPE, TYPE_CATEGOROES);
                            bundle.putLong(CATEGORY_ID,announcer.getId());
                            bundle.putString(MAIN_ID,CATEGORY_MAIN[3]);
                            return bundle;
                        }

                        @Override
                        public int getNextDataType() {
                            return TYPE_DATA_LIST;
                        }

                        @Override
                        public String getNextDataId() {
                            return PRE_DATA_TYPE+TYPE_CATEGOROES + announcer.getId();
                        }

                        @Override
                        public int getIconType() {
                            return 0;
                        }
                    };
                    subItems.add(subItem);
                }
                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                }else {
                    mIMusicServiceSubItemList.onEmpty();
                }
                mLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                subItemListOnError(i,s);
            }
        });


    }

    private void loadAnnouncerList(long vcategory_id){
        Glog.i(TAG,"loadAnnouncerList....");
        if (mLoading) {
            return;
        }
        mLoading = true;
        Map<String, String> map = new HashMap<String, String>(); //支持配置页数
        map.put(DTransferConstants.VCATEGORY_ID , vcategory_id+"");
        map.put(DTransferConstants.CALC_DIMENSION , "1");
        map.put(DTransferConstants.PAGE_SIZE,100+"");
        CommonRequest.getAnnouncerList(map, new IDataCallBack<AnnouncerList>() {
            @Override
            public void onSuccess(AnnouncerList announcerList) {
                List<Announcer> announcers = announcerList.getAnnouncerList();
                ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();

                for (int i=0; i<announcers.size(); i++ ){
                    final Announcer announcer = announcers.get(i);

                    IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                        @Override
                        public String getItemIconUrl() {
                            return announcer.getAvatarUrl();
                        }

                        @Override
                        public String getItemText() {
                            return announcer.getNickname();
                        }

                        @Override
                        public Bundle gotoNext() {
                            //MusicServiceSubItemFragment mssiFragment = new MusicServiceSubItemFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                            bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                            bundle.putLong(CATEGORY_ID,announcer.getAnnouncerId());
//                            bundle.putString(TAG_NAME,tag.getTagName());
                            bundle.putString(MAIN_ID,CATEGORY_MAIN[3]);
                            //mssiFragment.setArguments(bundle);
                            //if (fragment.getActivity() instanceof FragmentEntrust) {
                            //    ((FragmentEntrust) fragment.getActivity()).pushFragment(mssiFragment, PRE_DATA_TYPE+TYPE_TAGS+categoryid+tag.getTagName());
                            //}
                            return bundle;
                        }

                        @Override
                        public int getNextDataType() {
                            return TYPE_DATA_LIST;
                        }

                        @Override
                        public String getNextDataId() {
                            return PRE_DATA_TYPE+TYPE_TAGS + announcer.getAnnouncerId();//+tag.getTagName();
                        }

                        @Override
                        public int getIconType() {
                            return 0;
                        }
                    };
                    subItems.add(subItem);
                }
                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                }else {
                    mIMusicServiceSubItemList.onEmpty();
                }
                mLoading = false;
            }

            @Override
            public void onError(int i, String s) {
                subItemListOnError(i,s);
            }
        });
    }

    private void loadAnnouncerAlbums(Long aid){
        Glog.i(TAG,"loadAnnouncerAlbums ... aid :" + aid);
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.AID , aid+"");
        CommonRequest.getAlbumsByAnnouncer(map, new IDataCallBack<AlbumList>() {
            @Override
            public void onSuccess(AlbumList batchAlbumList) {

                List<Album> albumList = batchAlbumList.getAlbums();
                ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();

                for(int i=0;i < albumList.size(); i++) {

                    final Album album = albumList.get(i);

                    IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {
                        @Override
                        public String getCoverUrl() {
                            return album.getCoverUrlSmall();
                        }

                        @Override
                        public String getAlbumTitle() {
                            return album.getAlbumTitle();
                        }

                        @Override
                        public String getAlbumTags() {
                            return album.getAlbumTags();
                        }

                        @Override
                        public String getAlbumIntro() {
                            return album.getAlbumIntro();
                        }

                        @Override
                        public long getTotalCount() {
                            return album.getIncludeTrackCount();
                        }

                        @Override
                        public long getUpdateAt() {
                            return album.getUpdatedAt();
                        }

                        @Override
                        public String getAlbumId() {
                            return album.getId() + "";
                        }

                        @Override
                        public String getMainId() {
                            return CATEGORY_MAIN[3];
                        }

                        @Override
                        public String getSinger() {
                            return album.getAnnouncer().getNickname();
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
                            return album.getCoverUrlSmall();
                        }

                        @Override
                        public String getItemText() {
                            return album.getAlbumTitle();
                        }

                        @Override
                        public Bundle gotoNext() {
                            //MusicServiceSubItemFragment mssiFragment = new MusicServiceSubItemFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                            bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
                            bundle.putString(ALBUM_ID,album.getId()+"");
                            bundle.putString(MAIN_ID,CATEGORY_MAIN[3]);
                            return bundle;
                        }

                        @Override
                        public int getNextDataType() {
                            return TYPE_DATA_ALBUM;
                        }

                        @Override
                        public String getNextDataId() {
                            return PRE_DATA_TYPE+TYPE_ALBUMS+album.getId();
                        }

                        @Override
                        public int getIconType() {
                            return 0;
                        }
                    };
                    subItems.add(subItem);
                }
                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                }else {
                    Glog.i("jys","nodata....................empty ");
                    mIMusicServiceSubItemList.onEmpty();
                }
            }

            @Override
            public void onError(int i, String s) {
                subItemListOnError(i,s);
            }
        });
    }


    public void loadTagList(final long categoryid) {
        Glog.i(TAG,"loadTagList  mLoading = " + mLoading + "---categoryid = "+categoryid);
        if (mLoading) {
            return;
        }

        mLoading = true;
        canRefresh = false;
        Map<String, String> param = new HashMap<String, String>();
        param.put(DTransferConstants.CATEGORY_ID, "" + categoryid);
        param.put(DTransferConstants.TYPE, "" + 0);
        CommonRequest.getTags(param, new IDataCallBack<TagList>() {

            @Override
            public void onSuccess(TagList object) {
                Glog.i(TAG,"loadTagList---> onSuccess()");
                if (object != null && object.getTagList() != null && object.getTagList().size() != 0) {
                    Glog.i(TAG,"loadTagList---> onSuccess() has data ");
                    if (mTagList == null) {
                        mTagList = object;
                    } else {
                        mTagList.getTagList().clear();
                        mTagList.getTagList().addAll(object.getTagList());
                    }
//                    mTagAdapter.notifyDataSetChanged();
                    ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
                    for(int i=0;i < mTagList.getTagList().size(); i++) {
					    /*获取某一个分类的列表内容*/
                        Glog.i(TAG,"loadTagList  onSuccess    index = " + i + " TagName = " + mTagList.getTagList().get(i).getTagName());

                        final Tag tag = mTagList.getTagList().get(i);
                        IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                            @Override
                            public String getItemIconUrl() {
                                return null;
                            }

                            @Override
                            public String getItemText() {
                                return tag.getTagName();
                            }

                            @Override
                            public Bundle gotoNext() {
                                //MusicServiceSubItemFragment mssiFragment = new MusicServiceSubItemFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                                bundle.putLong(CATEGORY_ID,categoryid);
                                bundle.putString(TAG_NAME,tag.getTagName());
                                bundle.putString(MAIN_ID,CATEGORY_MAIN[0]);
                                //mssiFragment.setArguments(bundle);
                                //if (fragment.getActivity() instanceof FragmentEntrust) {
                                //    ((FragmentEntrust) fragment.getActivity()).pushFragment(mssiFragment, PRE_DATA_TYPE+TYPE_TAGS+categoryid+tag.getTagName());
                                //}
                                return bundle;
                            }

                            @Override
                            public int getNextDataType() {
                                return TYPE_DATA_LIST;
                            }

                            @Override
                            public String getNextDataId() {
                                return PRE_DATA_TYPE+TYPE_TAGS+categoryid+tag.getTagName();
                            }

                            @Override
                            public int getIconType() {
                                return 0;
                            }
                        };
                        subItems.add(subItem);
                    }
                    if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                    }else {
                        mIMusicServiceSubItemList.onEmpty();
                    }

                }

                mLoading = false;
            }

            @Override
            public void onError(int code, String message) {
                subItemListOnError(code,message);
            }
        });
    }


    private void subItemListOnError(int code, String message){
        Glog.i(TAG, "onError " + code + ", " + message);
        mLoading = false;
        if(mIMusicServiceSubItemList != null){
            mIMusicServiceSubItemList.onError(code,message);
        }
    }

    private void albumOnError(int code, String message){
        Glog.i(TAG, "onError " + code + ", " + message);
        mLoading = false;
        if(mIMusicServiceAlbum != null){
            mIMusicServiceAlbum.onError(code,message);
        }
    }

    private ArrayList<IMusicServiceSubItem> albumList = new ArrayList<IMusicServiceSubItem>();
    private long Scategoryid;
    private String StagName;
    private int currentAlbumListPage = 1;

    private boolean canRefresh = false;

    public void loadAlbumList(long categoryid, String tagName) {
        Glog.i(TAG,"loadAlbumList...");
        if (mLoading) {
            currentAlbumListPage = 1;
            return;
        }
        currentAlbumListPage = 1;
        mLoading = true;
        canRefresh = true;
        albumList.clear();
        Scategoryid = categoryid;
        StagName = tagName;
        RefreshAlbumList();

    }

    @Override
    public boolean isRefresh(){
        return canRefresh;
    }

    @Override
    public void RefreshAlbumList(){
        canRefresh = false;
        Map<String, String> param = new HashMap<String, String>();
        if (StagName == null){
            return;
        }
        param.put(DTransferConstants.CATEGORY_ID, "" + Scategoryid);
        param.put(DTransferConstants.CALC_DIMENSION, "" + 1); //计算维度，现支持最火（1），最新（2），经典或播放最多（3）
        param.put(DTransferConstants.PAGE, "" + currentAlbumListPage);//返回第几页，必须大于等于1，不填默认为1
        param.put(DTransferConstants.TAG_NAME, StagName);
        CommonRequest.getAlbumList(param, new IDataCallBack<AlbumList>() {

            @Override
            public void onSuccess(AlbumList object) {
                if (object != null && object.getAlbums() != null && object.getAlbums().size() != 0) {
                    if (mAlbumList == null) {
                        mAlbumList = object;
                    } else {
                        mAlbumList.getAlbums().clear();
                        mAlbumList.getAlbums().addAll(object.getAlbums());
                    }

                    for(int i=0;i < mAlbumList.getAlbums().size(); i++) {
                        final Album album = mAlbumList.getAlbums().get(i);
                        Glog.i(TAG,"album.getAlbumTags()" + album.getAlbumTags());

                        IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {
                            @Override
                            public String getCoverUrl() {
                                return album.getCoverUrlLarge();
                            }

                            @Override
                            public String getAlbumTitle() {
                                return album.getAlbumTitle();
                            }

                            @Override
                            public String getAlbumTags() {
                                return album.getAlbumTags();
                            }

                            @Override
                            public String getAlbumIntro() {
                                return album.getAlbumIntro();
                            }

                            @Override
                            public long getTotalCount() {
                                return album.getIncludeTrackCount();
                            }

                            @Override
                            public long getUpdateAt() {
                                return album.getUpdatedAt();
                            }

                            @Override
                            public String getAlbumId() {
                                return album.getId() + "";
                            }

                            @Override
                            public String getMainId() {
                                return CATEGORY_MAIN[0];
                            }

                            @Override
                            public String getSinger() {
                                return album.getAnnouncer().getNickname();
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
                                return album.getCoverUrlLarge();
                            }

                            @Override
                            public String getItemText() {
                                return album.getAlbumTitle();
                            }

                            @Override
                            public Bundle gotoNext() {
                                //MusicServiceSubItemFragment mssiFragment = new MusicServiceSubItemFragment();
                                Bundle bundle = new Bundle();
                                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
                                bundle.putLong(ALBUM_ID,album.getId());
                                bundle.putString(MAIN_ID,CATEGORY_MAIN[0]);
                                return bundle;
                            }

                            @Override
                            public int getNextDataType() {
                                return TYPE_DATA_ALBUM;
                            }

                            @Override
                            public String getNextDataId() {
                                return PRE_DATA_TYPE+TYPE_ALBUMS+album.getId();
                            }

                            @Override
                            public int getIconType() {
                                return 0;
                            }
                        };
                        albumList.add(subItem);
                    }
                    if(mIMusicServiceSubItemList != null && albumList.size() > 0){
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(albumList);
                    }
                }

                mLoading = false;
                canRefresh = true;
                currentAlbumListPage++;
            }

            @Override
            public void onError(int code, String message) {
                Glog.i(TAG, "onError " + code + ", " + message);
                mLoading = false;
                if(mIMusicServiceSubItemList != null){
                    mIMusicServiceSubItemList.onError(code,message);
                }
            }
        });
    }


    private String  currentAlbumId;

    private int currentAlbumPage = 1;



    public void loadAlbum(String AlbumId) {
        Glog.i(TAG,"loadAlbum AlbumId = "+AlbumId);
        currentAlbumPage = 1;
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = true;
        currentAlbumId = AlbumId;
        RefreshTrackList();

    }

    @Override
    public void RefreshTrackList(){
        canRefresh = false;
        Map<String, String> param = new HashMap<String, String>();
        param.put(DTransferConstants.PAGE, "" + currentAlbumPage);
        param.put(DTransferConstants.ALBUM_ID, currentAlbumId);
        CommonRequest.getTracks(param, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null && trackList.getTracks() != null && trackList.getTracks().size() != 0) {
                    if(mTrackHotList == null) {
                        mTrackHotList = trackList;
                    } else {
                        mTrackHotList.getTracks().clear();
                        mTrackHotList.getTracks().addAll(trackList.getTracks());
                    }
                    ArrayList<IMusicServiceTrackContent> trackContents = new ArrayList<IMusicServiceTrackContent>();
//                    mTrackAdapter.notifyDataSetChanged();

                    Glog.i(TAG,"loadAlbum  onSuccess    mTrackHotList.getTracks().size() = " + mTrackHotList.getTracks().size());
                    for(int i=0;i < mTrackHotList.getTracks().size(); i++) {
						/*获取专辑下面资源的名称*/
                        Glog.i(TAG,"loadAlbum  onSuccess    index = " + i + " TrackName = " + mTrackHotList.getTracks().get(i).getTrackTitle());
						/*获取到的声音资源的URL*/
                        Glog.i(TAG,"loadAlbum  onSuccess    index = " + i + " URL = " + mTrackHotList.getTracks().get(i).getPlayUrl32());

                        Glog.i(TAG,"loadAlbum  onSuccess    index = " + i + " Duration = " + mTrackHotList.getTracks().get(i).getDuration());

                        final Track track = mTrackHotList.getTracks().get(i);
                        //Glog.i(TAG,"track toString"+track.toString());
                        IMusicServiceTrackContent content = new IMusicServiceTrackContent(){

                            @Override
                            public String getCoverUrlSmall() {
                                return track.getCoverUrlLarge();
                            }

                            @Override
                            public String getCoverUrlMiddle() {
                                return track.getCoverUrlLarge();
                            }

                            @Override
                            public String getCoverUrlLarge() {
                                return track.getCoverUrlLarge();
                            }

                            @Override
                            public String getTrackTitle() {
                                return track.getTrackTitle();
                            }

                            @Override
                            public int getUrlType() {
                                return IMusicServiceTrackContent.STATIC_URL;
                            }

                            @Override
                            public String getPlayUrl() {
                                return track.getPlayUrl32();
                            }

                            @Override
                            public String getSinger() {
                                return track.getAnnouncer().getNickname();
                            }

                            @Override
                            public long getDuration() {
                                return track.getDuration()*1000;
                            }

                            @Override
                            public long getUpdateAt() {
                                return track.getUpdatedAt();
                            }

                            @Override
                            public String getUri() {
                                return "ximalaya:"+ track.getKind() + ":" + track.getDataId();
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
                        trackContents.add(content);
                    }

                    if(mIMusicServiceAlbum != null){
                        mIMusicServiceAlbum.createMusicServiceAlbum(trackContents);
                    }
                    mLoading = false;
                    canRefresh = true;
                    currentAlbumPage++;
                }else { // tracklist 为0  说明数据没有了
                    ArrayList<IMusicServiceTrackContent> noTracks = new ArrayList<IMusicServiceTrackContent>();
                    Glog.i(TAG,"noTracks size" + noTracks.size());
                    if(mIMusicServiceAlbum != null){
                        mIMusicServiceAlbum.createMusicServiceAlbum(noTracks);
                    }
                    mLoading = false;
                    canRefresh = false;
                }
            }

            @Override
            public void onError(int i, String s) {
                albumOnError(i,s);
            }
        });
    }

    public String getMainIcon(String name){
        if (name.equals(CATEGORY_MAIN[0])){
            return String.valueOf(R.drawable.xmly_category_icon);
        }else if (name.equals(CATEGORY_MAIN[1])){
            return String.valueOf(R.drawable.xmly_fm_icon);
        }else if (name.equals(CATEGORY_MAIN[2])){
            return String.valueOf(R.drawable.xmly_rank_icon);
        }else if (name.equals(CATEGORY_MAIN[3])){
            return String.valueOf(R.drawable.xmly_anchor_icon);
        }else {
            return "";
        }
    }

    public String getFmIcon(String name){
        if (name.equals(BROADCAST_NAME[0])){
            return String.valueOf(R.drawable.fm_country);
        }else if (name.equals(BROADCAST_NAME[1])){
            return String.valueOf(R.drawable.fm_province);
        }else if (name.equals(BROADCAST_NAME[2])){
            return String.valueOf(R.drawable.fm_internet);
        }else if (name.equals(BROADCAST_NAME[3])){
            return String.valueOf(R.drawable.fm_rank);
        }else {
            return "";
        }
    }
}
