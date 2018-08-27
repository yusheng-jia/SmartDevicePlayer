package com.mantic.control.musicservice;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.baidu.iot.sdk.IoTSDKManager;
import com.mantic.control.R;
import com.mantic.control.api.baidu.BaiduRetrofit;
import com.mantic.control.api.baidu.BaiduServiceApi;
import com.mantic.control.api.baidu.bean.BaiduTrackList;
import com.mantic.control.api.myservice.MyServiceOperatorRetrofit;
import com.mantic.control.api.myservice.MyServiceOperatorServiceApi;
import com.mantic.control.api.myservice.bean.PopularSingerResultBean;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 17-4-21.
 */
public class BaiduSoundData implements MyMusicService {
    private static final String TAG = "BaiduSoundData";

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
    public static final String CHANNEL_INTRO = "channel_INTRO";
    private int LoadingDataType = 0;//0代表tag,1代表歌手,2代表top

    private static final String[] CATEGORY_MAIN = {"心情", "风格", "主题", "年代", "乐器", "语言", "场景", "流派", "影视节目", "人声", "热门歌手", "排行榜"};
    private static final String[] CATEGORY_MOOD = {"伤感", "安静", "舒服", "甜蜜", "励志", "寂寞", "想念", "浪漫", "怀念", "喜悦", "深情", "美好", "怀旧", "轻松", "激情", "孤独", "失眠", "伤心", "愉快", "感动", "治愈", "洗脑", "放松", "回忆", "清新", "欢快", "热血", "抒情", "冥想", "可爱", "忧伤", "快乐", "思念", "兴奋", "动感"};
    private static final String[] CATEGORY_STYLE = {/*"DJ舞曲",*/"小清新", "纯净", "唯美", "轻音乐", "舒缓", "劲爆", "慢摇", "民歌", "青春", "好听", "交响乐", "后摇", "口哨", "优美", "舞曲", "英伦", "串烧", "嘻哈", "蓝调", "美声", "说唱", "大气", "电音", "性感", "空明", "空灵", "灵动", "另类"};
    private static final String[] CATEGORY_THEME = {"成名曲", "胎教", "武侠", "情歌", "军旅", "红歌", "古风", "流金岁月", "网络歌曲", "儿歌", "中国风", "英文儿歌", "格莱美", "民族", "世界杯", "翻唱", "独立", "大合唱", "小众", "儿童"};
    private static final String[] CATEGORY_YEARS = {"经典老歌", "欧美经典", "00后", "50年代", "60年代", "70后", "70年代", "80后", "80年代", "90后", "90年代"};
    private static final String[] CATEGORY_MUSICAL_INSTRUMENT = {"钢琴", "古筝", "萨克斯", "葫芦丝", "纯音乐", "小提琴", "管弦乐", "吉他", "竖琴", "长笛", "口琴", "二胡", "器乐", "琵琶", "钢琴", "大提琴"};
    private static final String[] CATEGORY_LANGUAGE = {"国语", "华语", "粤语", "闽南语", "法语", "韩语", "阿拉伯语", "葡萄牙语", "西班牙语", "拉丁语", "小语种", "草原", "新疆民歌", "云南民歌", "藏歌", "欧美", "内地", "台湾", "俄罗斯", "新加坡", "拉丁", "印度", "东南亚", "非洲"};
    private static final String[] CATEGORY_SCENE = {"广场舞", "校园", "旅行", "背景音乐", "午后", "酒吧", "咖啡厅", "婚礼", "工作", "学习", "夜店", "地铁", "夜晚", "看书", "睡眠", "散步", "清晨", "下午茶", "瑜伽", "驾车", "抖腿", "运动", "车载", "跑步", "迪斯科", "午休", "助眠", "阴暗", "恶搞"};
    private static final String[] CATEGORY_SCHOOL = {"摇滚", "古典音乐", "节奏布鲁斯", "乡村", "民谣", "电子", "爵士", "流行", "布鲁斯", "世界音乐", "新世纪", "雷鬼", "金属", "古典", "佛教", "朋克"};
    private static final String[] CATEGORY_FILM_PROGRAM = {"日本动漫", "动画", "游戏", "游戏", "影视原声", "电影", "电视剧", "韩剧", "中国好声音",/*"二次元","二人转","KTV","广告歌曲","ACG",*/"儿童故事", "搞笑"};
    private static final String[] CATEGORY_VOICE = {"男高音", "小鲜肉", "女高音", "女声", "女生", "童声", "天籁童声", "海豚音", "内地女团", "欧美女团", "欧美男团", "韩国女团", "最美和声", "合唱", "男女对唱", "对唱"};

    private String[] CATEGORY_POPULAR_SINGER = {};

//    private static final String[] CATEGORY_RANK = {"虾米音乐榜", "虾米新歌榜", "虾米原创榜", "Hito中文排行榜", "香港劲歌金榜", "英国UK单曲榜", "Billboard单曲榜", "Oricon公信单曲榜", "M-net综合数据周榜", "云音乐飙升榜", "云音乐新歌榜", "网易原创歌曲榜", "云音乐热歌榜", "云音乐韩语榜", "云音乐ACG音乐榜", "云音乐古典音乐榜",
//            "云音乐电音榜", "UK排行榜周榜", "美国Billboard周榜", "Beatport全球电子舞曲榜", "法国 NRJ Vos Hits 周榜", "KTV唛榜", "iTunes榜", "日本Oricon周榜", "Hit FM Top榜", "台湾Hito排行榜", "中国TOP排行榜（港台榜）", "中国TOP排行榜（内地榜）", "香港电台中文歌曲龙虎榜", "中国嘻哈榜", "巅峰榜·流行指数", "巅峰榜·热歌",
//            "巅峰榜·新歌", "巅峰榜·中国新歌声", "巅峰榜·网络歌曲", "巅峰榜·内地", "巅峰榜·港台", "巅峰榜·欧美", "巅峰榜·韩国", "巅峰榜·日本", "巅峰榜·音乐人", "巅峰榜·K歌金曲", "vivo 手机 高品质音乐榜", "美国公告牌榜", "美国iTunes榜", "韩国Mnet榜", "英国UK榜", "日本公信榜", "香港电台榜", "香港商台榜", "台湾幽浮榜", "Billboard榜",
//            "韩国M-net榜", "英国UK榜", "iTunes音乐榜", "全球电子舞曲榜", "日本公信榜", "台湾Hito", "香港音乐榜", "酷我新歌榜", "酷我飙升榜", "酷音乐流行榜", "酷我畅销榜", "酷我华语榜", "网络神曲榜", "夜店舞曲榜", "热门影视榜", "K歌翻唱榜", "酷我真声音", "酷狗飙升榜", "酷狗TOP500", "网络红歌榜", "华语新歌榜 ", "欧美新歌榜", "韩国新歌榜",
//            "日本新歌榜", "粤语新歌榜", "vivo热歌分享榜", "原创音乐榜", "5sing音乐榜", "校园歌曲榜", "繁星音乐榜", "美国BillBoard榜", "英国单曲榜", "日本公信榜", "韩国M-net音乐榜", "中国TOP排行榜", "中文DJ", "全球百大DJ", "车载音乐", "KTV", "影视金曲", "洗脑神曲"};
    private static final String[] CATEGORY_RANK = {"全球音乐榜", "虾米音乐", "网易云音乐", "QQ音乐", "酷我音乐", "酷狗音乐"};
    private static final String[] TAG_GLOBAL = {"vivo热歌分享榜", "vivo 手机 高品质音乐榜", "中国TOP排行榜", "Hito中文排行榜", "香港电台中文歌曲龙虎榜"};
    private static final String[] TAG_WANGYI = {"云音乐飙升榜", "云音乐新歌榜", "网易原创歌曲榜", "云音乐热歌榜", "云音乐古典音乐榜", "云音乐电音榜"};
    private static final String[] TAG_KUWO = {"酷我新歌榜", "酷我飙升榜", "酷音乐流行榜", "酷我华语榜", "网络神曲榜", "夜店舞曲榜", "酷我真声音"};
    private static final String[] TAG_QQ = {"巅峰榜·流行指数", "巅峰榜·热歌", "巅峰榜·新歌", "巅峰榜·网络歌曲 ", "巅峰榜·内地", "巅峰榜·港台", "巅峰榜·欧美"};
    private static final String[] TAG_XIAMI = {"虾米音乐榜", "虾米新歌榜", "虾米原创榜"};
    private static final String[] TAG_KUGOU = {"酷狗飙升榜", "酷狗TOP500", "影视金曲"};


    private static final String ROOTPATH = "http://znzs.image.alimmdn.com/bdfm/";
    private static final String[] IMG_GLOBAL = { "qqb/vivorgfxb.png", "qqb/vivogpzyyb.jpg", "qqb/zgTOPphb.png", "qqb/twHito.png", "qqb/xgdtzwgqlhb.png"};
    private static final String[] IMG_WANGYI = {"wy/wyybsb.png", "wy/wyyxgb.png", "wy/wyyycb.png", "wy/wyyrgb.png", "wy/wyygdyyb.png", "wy/wyydyb.png"};
    private static final String[] IMG_KUWO = {"kw/kwxgb.png", "kw/kwbsb.png", "kw/kwlxb.png", "kw/kwhyb.png", "kw/kwwlsqb.png", "kw/kwydwqb.png", "kw/kwzsy.png"};
    private static final String[] IMG_QQ = {"qq/qq-lxzs.png", "qq/qq-rg.png", "qq/qq-xg.png", "qq/qq-wlgq.png", "qq/qq-nd.png", "qq/qq-gt.png", "qq/qq-om.png"};
    private static final String[] IMG_XIAMI = {"xm/xmrgb.png", "xm/xmxgb.png", "xm/xmycb.png"};
    private static final String[] IMG_KUGOU = {"kg/kgbsb.png", "kg/kgtop500.png", "kg/kgysjq.png"};

    private String[] SINGER_SMALL_HEAD_IMAGE = null;
    private String[] SINGER_BIG_HEAD_IMAGE = null;

    private boolean mLoading = false;

    private IMusicServiceSubItemList mIMusicServiceSubItemList;
    private IMusicServiceAlbum mIMusicServiceAlbum;

    public BaiduSoundData(Context context) {
        this.init(context);
    }

    public void init(Context context) {
        mContext = context;
    }

    /**
     * 热门歌手
     */
    public void getEntertainmentPopularSingerData(final String name) {
        MyServiceOperatorServiceApi myServiceOperatorServiceApi = MyServiceOperatorRetrofit.getInstance().create(MyServiceOperatorServiceApi.class);
        Call<PopularSingerResultBean> getSingersCall = myServiceOperatorServiceApi.getPopularSingers();
        getSingersCall.enqueue(new Callback<PopularSingerResultBean>() {
            @Override
            public void onResponse(Call<PopularSingerResultBean> call, Response<PopularSingerResultBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    if (null != response.body() && null != response.body().getResult()) {
                        List<PopularSingerResultBean.PopularSinger> popularSingerList = response.body().getResult();
                        if (popularSingerList.size() > 0) {
                            CATEGORY_POPULAR_SINGER = new String[popularSingerList.size()];
                            SINGER_SMALL_HEAD_IMAGE = new String[popularSingerList.size()];
                            SINGER_BIG_HEAD_IMAGE = new String[popularSingerList.size()];
                            for (int i = 0; i < popularSingerList.size(); i++) {
                                PopularSingerResultBean.PopularSinger popularSinger = popularSingerList.get(i);
                                CATEGORY_POPULAR_SINGER[i] = popularSinger.getName();
                                SINGER_SMALL_HEAD_IMAGE[i] = popularSinger.getHead_image_small();
                                SINGER_BIG_HEAD_IMAGE[i] = popularSinger.getHead_image_big();
                                //          }
                            }
                        }

                        loadTagList(name);
                    }
                }
            }

            @Override
            public void onFailure(Call<PopularSingerResultBean> call, Throwable t) {

            }
        });
    }

    @Override
    public String getMusicServiceID() {
        return "baidu";
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
        Glog.i(TAG, "name == " + name);
        Glog.i(TAG, "type == " + type);
        if (type != -1 && TextUtils.isEmpty(name)) {
            if (mIMusicServiceAlbum != null) {
                mIMusicServiceAlbum.onError(404, "No data!");
            }
            mLoading = false;
            canRefresh = false;
            return;
        }
        switch (type) {
            case TYPE_FIRST:
                loadMain();
                break;
            case TYPE_MAIN:
                if ("热门歌手".equals(name) && CATEGORY_POPULAR_SINGER.length == 0) {
                    getEntertainmentPopularSingerData(name);
                } else {
                    loadTagList(name);
                }

                break;
            case TYPE_TAGS:
                loadAlbumList(name);
                break;

            case TYPE_ALBUMS:
                String albumId = bundle.getString(ALBUM_ID);
                try {
                    if (!TextUtils.isEmpty(bundle.getString(CHANNEL_INTRO))) {
                        String channelInfo = bundle.getString(CHANNEL_INTRO);
                        String[] infos = channelInfo.split(":");
                        int loadingDataType = Integer.parseInt(infos[1]);
                        Glog.i(TAG, "albumId = " + albumId);
                        loadTrackList(name, loadingDataType);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }
    }

    private void loadMain() { //百度入口（"心情","风格","主题","年代","乐器","语言","场景","流派","影视节目", "人声", "热门歌手", "排行榜"）
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;
        ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        for (final String name : CATEGORY_MAIN) {
            IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                @Override
                public String getItemIconUrl() {
//                    return getMainIcon(name);
                    return "";
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
        if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
        }

        mLoading = false;

    }

    private void loadTagList(final String categoryName) { //风格
        Glog.i(TAG, "loadTagList mLoading = " + mLoading);
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;

        String[] albumListName = null;
        switch (categoryName) {
            case "心情":
                albumListName = CATEGORY_MOOD;
                break;
            case "风格":
                albumListName = CATEGORY_STYLE;
                break;
            case "主题":
                albumListName = CATEGORY_THEME;
                break;
            case "年代":
                albumListName = CATEGORY_YEARS;
                break;
            case "乐器":
                albumListName = CATEGORY_MUSICAL_INSTRUMENT;
                break;
            case "语言":
                albumListName = CATEGORY_LANGUAGE;
                break;
            case "场景":
                albumListName = CATEGORY_SCENE;
                break;
            case "流派":
                albumListName = CATEGORY_SCHOOL;
                break;
            case "影视节目":
                albumListName = CATEGORY_FILM_PROGRAM;
                break;
            case "人声":
                albumListName = CATEGORY_VOICE;
                break;
            case "热门歌手":
                albumListName = CATEGORY_POPULAR_SINGER;
                break;
            case "排行榜":
                albumListName = CATEGORY_RANK;
                break;
            default:
                break;
        }
        ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();


        if ("排行榜".equals(categoryName)) {
            for (int i = 0; i < albumListName.length; i++) {
                final String name = albumListName[i];
                IMusicServiceSubItem subItem2 = new IMusicServiceSubItem() {
                    @Override
                    public String getItemIconUrl() {
                        return "";
                    }

                    @Override
                    public String getItemText() {
                        return name;
                    }

                    @Override
                    public Bundle gotoNext() {
                        Bundle bundle = new Bundle();
                        bundle.putString(MY_MUSIC_SERVICE_ID, getMusicServiceID());
                        bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                        bundle.putString(MAIN_ID, name);
                        return bundle;
                    }

                    @Override
                    public int getNextDataType() {
                        return TYPE_DATA_LIST;
                    }

                    @Override
                    public String getNextDataId() {
                        return PRE_DATA_TYPE + TYPE_TAGS + "baidu:album:" + name;
                    }

                    @Override
                    public int getIconType() {
                        return 1;
                    }
                };
                subItems.add(subItem2);
            }
        } else {
            for (int i = 0; i < albumListName.length; i++) {
                final String name = albumListName[i];
                final int position = i;
                IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {
                    @Override
                    public String getCoverUrl() {
                        if ("热门歌手".equals(categoryName)) {
                            return (null == SINGER_BIG_HEAD_IMAGE) ? "" : SINGER_BIG_HEAD_IMAGE[position];
                        }
                        return "";
                    }

                    @Override
                    public String getAlbumTitle() {
                        return name;
                    }

                    @Override
                    public String getAlbumTags() {
                        return "";
                    }

                    @Override
                    public String getAlbumIntro() {
                        if ("热门歌手".equals(categoryName)) {
                            return "热门歌手:1";
                        }
                        return "歌曲:0";
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
                        return "baidu:album:" + name;
                    }

                    @Override
                    public String getMainId() {
                        return name;
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
                        return "";
                    }

                    @Override
                    public String getItemIconUrl() {
                        if ("热门歌手".equals(categoryName)) {
                            return (null == SINGER_SMALL_HEAD_IMAGE) ? "" : SINGER_SMALL_HEAD_IMAGE[position];
                        }
                        return "";
                    }

                    @Override
                    public String getItemText() {
                        return name;
                    }

                    @Override
                    public Bundle gotoNext() {
                        Bundle bundle = new Bundle();
                        bundle.putString(MAIN_ID, name);
                        bundle.putString(MY_MUSIC_SERVICE_ID, getMusicServiceID());
                        if (categoryName.equals("热门歌手")) {
                            LoadingDataType = 1;
                            bundle.putString(ALBUM_ID, "baidu:singer:" + name);
                            bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
                        } else {
                            LoadingDataType = 0;
                            bundle.putString(ALBUM_ID, "baidu:album:" + name);
                            bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
                        }

                        return bundle;
                    }

                    @Override
                    public int getNextDataType() {
                        return TYPE_DATA_ALBUM;
                    }

                    @Override
                    public String getNextDataId() {
                        return PRE_DATA_TYPE + TYPE_ALBUMS + "baidu:album:" + name;
                    }

                    @Override
                    public int getIconType() {
                        return 0;
                    }
                };
                subItems.add(subItem);
            }
        }

        if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
        }

        mLoading = false;
    }

    private void loadAlbumList(final String tagName) {
        Glog.i(TAG, "loadAlbumList mLoading = " + mLoading);
        if (mLoading) {
            return;
        }
        mLoading = true;
        canRefresh = false;

        String[] albumListName = null;
        String[] imagePath = null;
        switch (tagName) {
            case "全球音乐榜":
                albumListName = TAG_GLOBAL;
                imagePath = IMG_GLOBAL;
                break;
            case "虾米音乐":
                albumListName = TAG_XIAMI;
                imagePath = IMG_XIAMI;
                break;
            case "网易云音乐":
                albumListName = TAG_WANGYI;
                imagePath = IMG_WANGYI;
                break;
            case "QQ音乐":
                albumListName = TAG_QQ;
                imagePath = IMG_QQ;
                break;
            case "酷我音乐":
                albumListName = TAG_KUWO;
                imagePath = IMG_KUWO;
                break;
            case "酷狗音乐":
                albumListName = TAG_KUGOU;
                imagePath = IMG_KUGOU;
                break;
            default:
                break;
        }


        ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        for (int i = 0; i < albumListName.length; i++) {
            final String name = albumListName[i];
            final String path = imagePath[i];
            IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {
                @Override
                public String getCoverUrl() {
                    return ROOTPATH + path;
                }

                @Override
                public String getAlbumTitle() {
                    return name;
                }

                @Override
                public String getAlbumTags() {
                    return "";
                }

                @Override
                public String getAlbumIntro() {
                    return "排行榜:2";
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
                    return "baidu:top:" + name;
                }

                @Override
                public String getMainId() {
                    return name;
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
                    return "";
                }

                @Override
                public String getItemIconUrl() {
                    return ROOTPATH + path;
                }

                @Override
                public String getItemText() {
                    return name;
                }

                @Override
                public Bundle gotoNext() {
                    Bundle bundle = new Bundle();
                    bundle.putString(MAIN_ID, name);
                    bundle.putString(MY_MUSIC_SERVICE_ID, getMusicServiceID());
                    bundle.putString(ALBUM_ID, "baidu:top:" + name);
                    LoadingDataType = 2;
                    bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);

                    return bundle;
                }

                @Override
                public int getNextDataType() {
                    return TYPE_DATA_ALBUM;
                }

                @Override
                public String getNextDataId() {
                    return PRE_DATA_TYPE + TYPE_ALBUMS + "baidu:top:" + name;
                }

                @Override
                public int getIconType() {
                    return 0;
                }
            };
            subItems.add(subItem);
        }
        if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
        }

        mLoading = false;
    }


    private void albumOnError(int code, String message) {
        Glog.i(TAG, "onError " + code + ", " + message);
        mLoading = false;
        if (mIMusicServiceAlbum != null) {
            mIMusicServiceAlbum.onError(code, message);
        }
    }

    private ArrayList<IMusicServiceSubItem> albumList = new ArrayList<IMusicServiceSubItem>();
    private long Scategoryid;
    private String StagName;
    private int currentAlbumListPage = 1;

    private boolean canRefresh = false;

    public void loadTrackList(String tagName, int LoadingDataType) {
        Glog.i(TAG, "loadTrackList..." + tagName);
        Glog.i(TAG, "mLoading..." + mLoading);
        if (mLoading) {
            currentAlbumListPage = 1;
            return;
        }
        currentAlbumListPage = 1;
        mLoading = true;
        canRefresh = true;
        albumList.clear();
        StagName = tagName;

        this.LoadingDataType = LoadingDataType;
        RefreshTrackList();
    }

    @Override
    public boolean isRefresh() {
        return canRefresh;
    }

    @Override
    public void RefreshAlbumList() {

    }


    private String currentAlbumId;

    private int currentAlbumPage = 1;

    @Override
    public void RefreshTrackList() {
        canRefresh = false;
        if (TextUtils.isEmpty(StagName)) {
            return;
        }
        StagName = StagName.trim();

        String nonce = Util.randomString(16);
        String access_token = IoTSDKManager.getInstance().getAccessToken().getAccessToken();
        String securityAppKey = Util.getSecurityAppKey(nonce, access_token);

        Map<String, String> headersMap = new LinkedHashMap<>();
        headersMap.put("X-IOT-APP", "d3S3SbItdlYDj4KaOB1qIfuM");
        headersMap.put("X-IOT-Signature", nonce + ":" + securityAppKey);
        headersMap.put("X-IOT-Token", access_token);
        Glog.i(TAG, "headersMap: " + headersMap);

        Map<String, String> paramsMap = new LinkedHashMap<>();

        if (LoadingDataType == 2) {
            paramsMap.put("top", StagName);
            paramsMap.put("page", currentAlbumListPage + "");
            paramsMap.put("page_size", "20");
            Glog.i(TAG, "paramsMap: " + paramsMap);
            Call<BaiduTrackList> getTrackListCall = BaiduRetrofit.getInstance().create(BaiduServiceApi.class).getTrackListByTopName(headersMap, paramsMap);
            getTrackListCall.enqueue(new Callback<BaiduTrackList>() {
                @Override
                public void onResponse(Call<BaiduTrackList> call, Response<BaiduTrackList> response) {
                    Glog.i("getTrackListCall", response.body().toString());
                    if (null != response.body() && null != response.body().data) {
                        Glog.i("getTrackListCall", response.body().toString());
                        BaiduTrackList.TrackItem trackItem = response.body().data;
                        ArrayList<IMusicServiceTrackContent> trackContents = new ArrayList<IMusicServiceTrackContent>();
                        if (null != trackItem.list && trackItem.list.size() > 0) {

                            for (int i = 0; i < trackItem.list.size(); i++) {
                                final BaiduTrackList.TrackItem.Track track = trackItem.list.get(i);
                                IMusicServiceTrackContent content = new IMusicServiceTrackContent() {

                                    @Override
                                    public String getCoverUrlSmall() {
                                        return track.head_image_url;
                                    }

                                    @Override
                                    public String getCoverUrlMiddle() {
                                        return track.head_image_url;
                                    }

                                    @Override
                                    public String getCoverUrlLarge() {
                                        return track.head_image_url;
                                    }

                                    @Override
                                    public String getTrackTitle() {
                                        return track.name;
                                    }

                                    @Override
                                    public int getUrlType() {
                                        return IMusicServiceTrackContent.STATIC_URL;
                                    }

                                    @Override
                                    public String getPlayUrl() {
                                        return "";
                                    }

                                    @Override
                                    public String getSinger() {
                                        String artist_name = "";
                                        if (track.singer_name != null) {
                                            for (int i = 0; i< track.singer_name.size(); i++){
                                                if (i == 0) {
                                                    artist_name = track.singer_name.get(i);
                                                } else {
                                                    artist_name = artist_name + "," + track.singer_name.get(i);;
                                                }
                                            }
                                        }

                                        return artist_name;
                                    }

                                    @Override
                                    public long getDuration() {
                                        return 0;
                                    }

                                    @Override
                                    public long getUpdateAt() {
                                        return 0;
                                    }

                                    @Override
                                    public String getUri() {
                                        return "baidu:track:" + track.id;
                                    }

                                    @Override
                                    public String getTimePeriods() {
                                        return "";
                                    }

                                    @Override
                                    public String getAlbumId() {
                                        return "";
                                    }
                                };
                                trackContents.add(content);
                            }
                            currentAlbumListPage++;
                        }

                        canRefresh = true;
                        mLoading = false;

                        if (mIMusicServiceAlbum != null) {
                            mIMusicServiceAlbum.createMusicServiceAlbum(trackContents);
                        }
                    }

                    mLoading = false;
                }

                @Override
                public void onFailure(Call<BaiduTrackList> call, Throwable t) {
                    Glog.i("getTrackListCall", t.toString());
                    mLoading = false;
                    if (mIMusicServiceAlbum != null) {
                        mIMusicServiceAlbum.onError(401, t.getMessage());
                    }
                }
            });
        } else if (LoadingDataType == 0) {
            paramsMap.put("tag", StagName);
            paramsMap.put("page", currentAlbumListPage + "");
            paramsMap.put("page_size", "20");
            Glog.i(TAG, "paramsMap: " + paramsMap);
            Call<BaiduTrackList> getTrackListCall = BaiduRetrofit.getInstance().create(BaiduServiceApi.class).getTrackListByTagName(headersMap, paramsMap);
            getTrackListCall.enqueue(new Callback<BaiduTrackList>() {
                @Override
                public void onResponse(Call<BaiduTrackList> call, Response<BaiduTrackList> response) {
                    if (null != response.body() && null != response.body().data) {
                        Glog.i("getTrackListCall", response.body().toString());
                        BaiduTrackList.TrackItem trackItem = response.body().data;
                        ArrayList<IMusicServiceTrackContent> trackContents = new ArrayList<IMusicServiceTrackContent>();
                        if (null != trackItem.list && trackItem.list.size() > 0) {
                            for (int i = 0; i < trackItem.list.size(); i++) {
                                final BaiduTrackList.TrackItem.Track track = trackItem.list.get(i);
                                IMusicServiceTrackContent content = new IMusicServiceTrackContent() {

                                    @Override
                                    public String getCoverUrlSmall() {
                                        return track.head_image_url;
                                    }

                                    @Override
                                    public String getCoverUrlMiddle() {
                                        return track.head_image_url;
                                    }

                                    @Override
                                    public String getCoverUrlLarge() {
                                        return track.head_image_url;
                                    }

                                    @Override
                                    public String getTrackTitle() {
                                        return track.name;
                                    }

                                    @Override
                                    public int getUrlType() {
                                        return IMusicServiceTrackContent.STATIC_URL;
                                    }

                                    @Override
                                    public String getPlayUrl() {
                                        return "";
                                    }

                                    @Override
                                    public String getSinger() {
                                        String artist_name = "";
                                        if (track.singer_name != null) {
                                            for (int i = 0; i< track.singer_name.size(); i++){
                                                if (i == 0) {
                                                    artist_name = track.singer_name.get(i);
                                                } else {
                                                    artist_name = artist_name + "," + track.singer_name.get(i);;
                                                }
                                            }
                                        }

                                        return artist_name;
                                    }

                                    @Override
                                    public long getDuration() {
                                        return 0;
                                    }

                                    @Override
                                    public long getUpdateAt() {
                                        return 0;
                                    }

                                    @Override
                                    public String getUri() {
                                        return "baidu:track:" + track.id;
                                    }

                                    @Override
                                    public String getTimePeriods() {
                                        return "";
                                    }

                                    @Override
                                    public String getAlbumId() {
                                        return "";
                                    }
                                };
                                trackContents.add(content);
                            }
                            currentAlbumListPage++;
                        }

                        if (mIMusicServiceAlbum != null) {
                            mIMusicServiceAlbum.createMusicServiceAlbum(trackContents);
                        }
                        canRefresh = true;
                        mLoading = false;
                    }

                    mLoading = false;
                }

                @Override
                public void onFailure(Call<BaiduTrackList> call, Throwable t) {
                    Glog.i("getTrackListCall", t.toString());
                    mLoading = false;
                    if (mIMusicServiceAlbum != null) {
                        mIMusicServiceAlbum.onError(401, t.getMessage());
                    }

                }
            });
        } else if (LoadingDataType == 1) {
            paramsMap.put("singer", StagName);
            paramsMap.put("page", currentAlbumListPage + "");
            paramsMap.put("page_size", "20");
            Glog.i(TAG, "paramsMap: " + paramsMap);
            Call<BaiduTrackList> getTrackListCall = BaiduRetrofit.getInstance().create(BaiduServiceApi.class).getTrackListBySingerName(headersMap, paramsMap);
            getTrackListCall.enqueue(new Callback<BaiduTrackList>() {
                @Override
                public void onResponse(Call<BaiduTrackList> call, Response<BaiduTrackList> response) {
                    if (null != response.body() && null != response.body().data) {
                        Glog.i("getTrackListCall", response.body().toString());
                        BaiduTrackList.TrackItem trackItem = response.body().data;
                        ArrayList<IMusicServiceTrackContent> trackContents = new ArrayList<IMusicServiceTrackContent>();
                        if (null != trackItem.list && trackItem.list.size() > 0) {
                            for (int i = 0; i < trackItem.list.size(); i++) {
                                final BaiduTrackList.TrackItem.Track track = trackItem.list.get(i);
                                IMusicServiceTrackContent content = new IMusicServiceTrackContent() {

                                    @Override
                                    public String getCoverUrlSmall() {
                                        return track.head_image_url;
                                    }

                                    @Override
                                    public String getCoverUrlMiddle() {
                                        return track.head_image_url;
                                    }

                                    @Override
                                    public String getCoverUrlLarge() {
                                        return track.head_image_url;
                                    }

                                    @Override
                                    public String getTrackTitle() {
                                        return track.name;
                                    }

                                    @Override
                                    public int getUrlType() {
                                        return IMusicServiceTrackContent.STATIC_URL;
                                    }

                                    @Override
                                    public String getPlayUrl() {
                                        return "";
                                    }

                                    @Override
                                    public String getSinger() {
                                        String artist_name = "";
                                        if (track.singer_name != null) {
                                            for (int i = 0; i< track.singer_name.size(); i++){
                                                if (i == 0) {
                                                    artist_name = track.singer_name.get(i);
                                                } else {
                                                    artist_name = artist_name + "," + track.singer_name.get(i);;
                                                }
                                            }
                                        }

                                        return artist_name;
                                    }

                                    @Override
                                    public long getDuration() {
                                        return 0;
                                    }

                                    @Override
                                    public long getUpdateAt() {
                                        return 0;
                                    }

                                    @Override
                                    public String getUri() {
                                        return "baidu:track:" + track.id;
                                    }

                                    @Override
                                    public String getTimePeriods() {
                                        return null;
                                    }

                                    @Override
                                    public String getAlbumId() {
                                        return "";
                                    }
                                };
                                trackContents.add(content);
                            }
                            currentAlbumListPage++;
                        }

                        if (mIMusicServiceAlbum != null) {
                            mIMusicServiceAlbum.createMusicServiceAlbum(trackContents);
                        }
                        canRefresh = true;
                        mLoading = false;
                    }

                    mLoading = false;
                }

                @Override
                public void onFailure(Call<BaiduTrackList> call, Throwable t) {
                    Glog.i("getTrackListCall", t.toString());
                    mLoading = false;
                    if (mIMusicServiceAlbum != null) {
                        mIMusicServiceAlbum.onError(401, t.getMessage());
                    }

                }
            });
        }

    }

    public String getMainIcon(String name) {
        if (name.equals(CATEGORY_MAIN[0])) {
            return String.valueOf(R.drawable.xmly_category_icon);
        } else if (name.equals(CATEGORY_MAIN[1])) {
            return String.valueOf(R.drawable.xmly_fm_icon);
        } else if (name.equals(CATEGORY_MAIN[2])) {
            return String.valueOf(R.drawable.xmly_rank_icon);
        } else if (name.equals(CATEGORY_MAIN[3])) {
            return String.valueOf(R.drawable.xmly_anchor_icon);
        } else {
            return "";
        }
    }
}
