package com.mantic.control.musicservice;

import android.content.Context;
import android.content.Loader;
import android.os.Bundle;

import com.mantic.control.fragment.MusicServiceSubItemFragment;
import com.mantic.control.cache.CacheUtils;
import com.mantic.control.utils.Glog;
import com.rogen.netcontrol.model.Music;
import com.rogen.netcontrol.model.MusicList;
import com.rogen.netcontrol.model.SongTable;
import com.rogen.netcontrol.model.SquareAudio;
import com.rogen.netcontrol.model.SquareAudioTagList;
import com.rogen.netcontrol.model.SquareAudioTagListItem;
import com.rogen.netcontrol.model.SquareButton;
import com.rogen.netcontrol.model.SquareDaRenSongs;
import com.rogen.netcontrol.model.SquareMoreSongs;
import com.rogen.netcontrol.model.SquareMusic;
import com.rogen.netcontrol.model.SquareNewSongs;
import com.rogen.netcontrol.model.SquareRankSongs;
import com.rogen.netcontrol.model.SquareSceneClass;
import com.rogen.netcontrol.model.SquareSceneClassInfo;
import com.rogen.netcontrol.model.SquareSceneClassTag;
import com.rogen.netcontrol.net.DataManagerEngine;
import com.rogen.netcontrol.net.MusicManager;
import com.rogen.netcontrol.net.SquareManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by root on 17-4-24.
 */
public class DuoyaoMusicService implements MyMusicService {
    public static final String TAG = "DuoyaoMusicService";
    private String mAppSecret = "a806aa9062dd1d26a315b7533144b606";

    //cache add
    private File  cacheDir;
    private CacheUtils mCacheUtils;
    private static final String MUSIC_SQUARE = "music_square";
    private static final String MUSIC_PAIHANG = "music_paihang";
    private static final String MUSIC_DAREN = "music_daren";
    private static final String MUSIC_NEWSONG = "music_newsong";
    private static final String MUSIC_SCENE = "music_scene";
    private static final String MUSIC_SCENE_ITEM = "music_scene_item";

    private static final String AUDIO_SQUARE = "audio_square";
    private static final String AUDIO_SQUARE_ITEM = "audio_square_item";
    //cache add

    private static final int TYPE_CATEGOROES = 0;
    private static final int TYPE_SQUAREBUTTON = 1;
    private static final int TYPE_TAGS = 2;
    private static final int TYPE_ALBUMS = 3;
    private static final String CATEGORY_ID = "category_id";
    private static final String TAG_ID = "tag_id";
    private static final String SQUAREBUTTON_ID = "squarebutton_id";
    private static final String SQUAREBUTTON_CHANGJING = "1";//场景分类
    private static final String SQUAREBUTTON_XINGE = "2";//新歌首发
    private static final String SQUAREBUTTON_DAREN = "3";//达人歌单
    private static final String SQUAREBUTTON_PAIHANG = "4";//排行榜
    private static final String LIST_ID = "list_id";
    private static final String LIST_TYPE = "list_type";
    private static final String LIST_SRC = "list_src";
    private static final String TAG_NAME = "tag_name";
    private static final String ALBUM_ID = "album_id";

    private static DuoyaoMusicService INSTANCE;
    public Context mContext;

    private static final String[] CATEGORY_NAMES = {"电台","音乐"};

    private String currentTpye = CATEGORY_NAMES[0];

    private IMusicServiceSubItemList mIMusicServiceSubItemList;
    private IMusicServiceAlbum mIMusicServiceAlbum;

    /*朵钥 sdk */
    public MusicManager musicManager;
    public SquareManager squareManager;

    public DuoyaoMusicService(Context context){
        mContext = context;
        squareManager = DataManagerEngine.getInstance(context).getSquareManager();
        musicManager =  DataManagerEngine.getInstance(context).getMusicManager();
        cacheDir = mContext.getCacheDir();
        mCacheUtils = CacheUtils.getInstance(cacheDir);


    }

    public synchronized static DuoyaoMusicService getInstance(Context context){
        if(INSTANCE == null){
            INSTANCE = new DuoyaoMusicService(context);
        }
        return INSTANCE;
    }



    @Override
    public String getMusicServiceID() {
        return mAppSecret;
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
        int type = bundle.getInt(PRE_DATA_TYPE);
        if(type == TYPE_FIRST) {
            //this.initCategory();
            String subTitle = bundle.getString(MusicServiceSubItemFragment.SUB_ITEM_TITLE);
            if("虾米音乐".equals(subTitle)){
                this.loadTypeList(CATEGORY_NAMES[1]);
            }else if("多听FM".equals(subTitle)){
                this.loadTypeList(CATEGORY_NAMES[0]);
            }
        }else if(type == TYPE_CATEGOROES){
            String id = bundle.getString(CATEGORY_ID);
            this.loadTypeList(id);
        }else if(type == TYPE_SQUAREBUTTON){
            String squarebuttonId = bundle.getString(SQUAREBUTTON_ID);
            Glog.i(TAG,"exec squarebuttonId = "+squarebuttonId);
            if(squarebuttonId.equals(SQUAREBUTTON_CHANGJING)){
                this.initSquareSceneCategory();
            }else if(squarebuttonId.equals(SQUAREBUTTON_XINGE)){
                this.initSquareNewSongs();
            }else if(squarebuttonId.equals(SQUAREBUTTON_DAREN)){
                this.initSquareDaRenSongs();
            }else if(squarebuttonId.equals(SQUAREBUTTON_PAIHANG)){
                this.initSquareRankSongs();
            }
        }else if(type == TYPE_TAGS){
            String categoryId = bundle.getString(CATEGORY_ID);
            if(categoryId.equals(CATEGORY_NAMES[0])){
                long tagId = bundle.getLong(TAG_ID);
                this.getAudioMusicListById(tagId);
            }else if(categoryId.equals(CATEGORY_NAMES[1])){
                int tagId = bundle.getInt(TAG_ID);
                this.getSquareSceneMusicListbyTagId(tagId);
            }
        }else if(type == TYPE_ALBUMS){
            long listId = bundle.getLong(LIST_ID);
            int listType = bundle.getInt(LIST_TYPE);
            int listSrc = bundle.getInt(LIST_SRC);
            if (0 != listId && 0 != listType && 0!= listSrc) {

                this.getMusicListByMusicListId(listId,listType,listSrc);
            }
        }
    }

    public void initCategory(){
        ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        for(int i = 0;i < CATEGORY_NAMES.length;i++){
            final String categoryName = CATEGORY_NAMES[i];
            IMusicServiceSubItem subItem = new IMusicServiceSubItem() {
                @Override
                public String getItemIconUrl() {
                    return null;
                }

                @Override
                public String getItemText() {
                    return categoryName;
                }

                @Override
                public Bundle gotoNext() {
                    Bundle bundle = new Bundle();
                    bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                    bundle.putInt(PRE_DATA_TYPE, TYPE_CATEGOROES);
                    bundle.putString(CATEGORY_ID,categoryName);
                    return bundle;
                }

                @Override
                public int getNextDataType() {
                    return TYPE_DATA_LIST;
                }

                @Override
                public String getNextDataId() {
                    return PRE_DATA_TYPE+TYPE_CATEGOROES+categoryName;
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
        }

    }

    public void loadTypeList(final String categoryid) {
        Glog.i(TAG,"loadTypeList............");
        final ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();

        if(categoryid.equals(CATEGORY_NAMES[0])){//电台
            currentTpye = CATEGORY_NAMES[0];
            JSONArray audioArray = mCacheUtils.getJSONArray(AUDIO_SQUARE);
            if (audioArray == null){
                squareManager.getSquareAudioAsync(new SquareManager.SquareAudioListener() {
                    @Override
                    public void onGetSquareAudio(SquareAudio squareAudio) {
                    if(squareAudio!= null && squareAudio.getErrorCode() == SquareAudio.OK){
                        if (squareAudio.mData != null){
                            JSONArray audioArray = new JSONArray();
                            List<SquareAudioTagListItem> squareAudioTagList = squareAudio.mData.mTags.mSquareAudioTagList;

                            for(int i = 0;i < squareAudioTagList.size();i++){
                                final SquareAudioTagListItem squareAudioTagListItem = squareAudioTagList.get(i);

                                JSONObject musicItem = new JSONObject();
                                try {
                                    musicItem.put("id",squareAudioTagListItem.tagId);
                                    musicItem.put("name",squareAudioTagListItem.title);
                                    musicItem.put("image",squareAudioTagListItem.image_url);
                                    audioArray.put(i,musicItem);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                IMusicServiceSubItem musicServiceSubItem = new IMusicServiceSubItem() {
                                    @Override
                                    public String getItemIconUrl() {
                                        return squareAudioTagListItem.image_url;
                                    }

                                    @Override
                                    public String getItemText() {
                                        return squareAudioTagListItem.title;
                                    }

                                    @Override
                                    public Bundle gotoNext() {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                        bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                                        bundle.putString(CATEGORY_ID,categoryid);
                                        bundle.putLong(TAG_ID,squareAudioTagListItem.tagId);
                                        return bundle;
                                    }

                                    @Override
                                    public int getNextDataType() {
                                        return TYPE_DATA_LIST;
                                    }

                                    @Override
                                    public String getNextDataId() {
                                        return PRE_DATA_TYPE+TYPE_TAGS+categoryid+squareAudioTagListItem.title;
                                    }

                                    @Override
                                    public int getIconType() {
                                        return 0;
                                    }
                                };
                                subItems.add(musicServiceSubItem);
                            }
                            //写入缓冲 失效期限 24小时
                            mCacheUtils.put(AUDIO_SQUARE,audioArray,24 * CacheUtils.HOUR);

                            if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                                mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                            }
                        }
                    }else{
                        mIMusicServiceSubItemList.onError(squareAudio.getErrorCode(),squareAudio.getErrorDescription());
                    }
                    }
                });
            }else {
                for (int i = 0; i< audioArray.length(); i++){
                    try {
                        final JSONObject musicItem = audioArray.getJSONObject(i);
                        final String name = musicItem.getString("name");
                        final String image = musicItem.getString("image");
                        final String id = musicItem.getString("id");

                        IMusicServiceSubItem musicServiceSubItem = new IMusicServiceSubItem() {
                            @Override
                            public String getItemIconUrl() {
                                return image;
                            }

                            @Override
                            public String getItemText() {
                                return name;
                            }

                            @Override
                            public Bundle gotoNext() {
                                Bundle bundle = new Bundle();
                                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                                bundle.putString(CATEGORY_ID,categoryid);
                                bundle.putLong(TAG_ID,Long.parseLong(id));
                                return bundle;
                            }

                            @Override
                            public int getNextDataType() {
                                return TYPE_DATA_LIST;
                            }

                            @Override
                            public String getNextDataId() {
                                return PRE_DATA_TYPE+TYPE_TAGS+categoryid+name;
                            }

                            @Override
                            public int getIconType() {
                                return 0;
                            }
                        };
                        subItems.add(musicServiceSubItem);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                }
            }
        }else if(categoryid.equals(CATEGORY_NAMES[1])){//音乐
            //从缓冲中获取数据
            currentTpye = CATEGORY_NAMES[1];
            JSONArray musicArray = mCacheUtils.getJSONArray(MUSIC_SQUARE);
            if ( musicArray == null){
                squareManager.getSquareMusicAsync(new SquareManager.SquareMusicListener() {
                    @Override
                    public void onGetSquareMusic(SquareMusic squareMusic) {
                        if(squareMusic!= null && squareMusic.getErrorCode() == SquareAudio.OK) {
                            if (squareMusic != null && squareMusic.getErrorCode() == SquareMusic.OK) {
                                JSONArray musicSquare = new JSONArray();
                                for(int i = 0;i < squareMusic.mSquareButtons.size();i++){
                                    final SquareButton sButton = squareMusic.mSquareButtons.get(i);

                                    JSONObject musicItem = new JSONObject();
                                    try {
                                        musicItem.put("id",sButton.mId);
                                        musicItem.put("name",sButton.mName);
                                        musicItem.put("image",sButton.mBtimage);
                                        musicSquare.put(i,musicItem);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    IMusicServiceSubItem musicServiceSubItem = new IMusicServiceSubItem() {
                                        @Override
                                        public String getItemIconUrl() {
                                            return sButton.mBtimage;
                                        }

                                        @Override
                                        public String getItemText() {
                                            return sButton.mName;
                                        }

                                        @Override
                                        public Bundle gotoNext() {
                                            Bundle bundle = new Bundle();
                                            bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                            bundle.putInt(PRE_DATA_TYPE, TYPE_SQUAREBUTTON);
                                            bundle.putString(CATEGORY_ID,categoryid);
                                            Glog.i(TAG,"sButton.mId = "+sButton.mId);
                                            bundle.putString(SQUAREBUTTON_ID,sButton.mId);
                                            return bundle;
                                        }

                                        @Override
                                        public int getNextDataType() {
                                            return TYPE_DATA_LIST;
                                        }

                                        @Override
                                        public String getNextDataId() {
                                            return PRE_DATA_TYPE+TYPE_SQUAREBUTTON+categoryid+sButton.mId;
                                        }

                                        @Override
                                        public int getIconType() {
                                            return 0;
                                        }
                                    };
                                    subItems.add(musicServiceSubItem);
                                }
                                //写入缓冲 失效期限 24小时
                                mCacheUtils.put(MUSIC_SQUARE,musicSquare,24 * CacheUtils.HOUR);

                                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                                }

                            }
                        }else{
                            mIMusicServiceSubItemList.onError(squareMusic.getErrorCode(),squareMusic.getErrorDescription());
                        }
                    }
                });
            }else {
                for (int i = 0; i< musicArray.length(); i++){
                    try {
                        final JSONObject musicItem = musicArray.getJSONObject(i);
                        final String name = musicItem.getString("name");
                        final String image = musicItem.getString("image");
                        final String id = musicItem.getString("id");
                        IMusicServiceSubItem musicServiceSubItem = new IMusicServiceSubItem() {
                            @Override
                            public String getItemIconUrl() {
                                return image;
                            }

                            @Override
                            public String getItemText() {
                                return name;
                            }

                            @Override
                            public Bundle gotoNext() {
                                Bundle bundle = new Bundle();
                                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                bundle.putInt(PRE_DATA_TYPE, TYPE_SQUAREBUTTON);
                                bundle.putString(CATEGORY_ID,categoryid);
                                Glog.i(TAG,"sButton.mId = "+id);
                                bundle.putString(SQUAREBUTTON_ID,id);
                                return bundle;
                            }

                            @Override
                            public int getNextDataType() {
                                return TYPE_DATA_LIST;
                            }

                            @Override
                            public String getNextDataId() {
                                return PRE_DATA_TYPE+TYPE_SQUAREBUTTON+categoryid+id;
                            }

                            @Override
                            public int getIconType() {
                                return 0;
                            }
                        };
                        subItems.add(musicServiceSubItem);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(mIMusicServiceSubItemList != null && subItems.size() > 0){
                    mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                }
            }
        }
    }

    public void initSquareSceneCategory(){
        final ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
//        mCacheUtils.remove(MUSIC_SCENE);
        JSONArray sceneArray = mCacheUtils.getJSONArray(MUSIC_SCENE);
        if (sceneArray == null){
            squareManager.getSquareSceneClassAsync(new SquareManager.SquareSceneClassListener() {
                @Override
                public void onGetSquareSceneClass(SquareSceneClass squareSceneClass) {
                    if(squareSceneClass != null && squareSceneClass.getErrorCode() == SquareSceneClass.OK) {
                        JSONArray sceneArray = new JSONArray();
                        int count = 0;
                        for (int i = 0; i < squareSceneClass.mClassinfo.size(); i++) {
                            final SquareSceneClassInfo sceneClassInfo = squareSceneClass.mClassinfo.get(i);
                            for(int j = 0;j < sceneClassInfo.mTags.size();j++){
                                final SquareSceneClassTag squareSceneClassTag = sceneClassInfo.mTags.get(j);
                                JSONObject itemObject = new JSONObject();
                                try {
                                    itemObject.put("name",squareSceneClassTag.mTagName);
                                    itemObject.put("id",squareSceneClassTag.mTagId);
                                    sceneArray.put(count,itemObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                count ++;
                                IMusicServiceSubItem musicServicesubItem = new IMusicServiceSubItem() {
                                    @Override
                                    public String getItemIconUrl() {
                                        return null;
                                    }

                                    @Override
                                    public String getItemText() {
                                        return squareSceneClassTag.mTagName;
                                    }

                                    @Override
                                    public Bundle gotoNext() {
                                        Bundle bundle = new Bundle();
                                        bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                                        bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                                        bundle.putString(CATEGORY_ID,CATEGORY_NAMES[1]);
                                        bundle.putInt(TAG_ID,squareSceneClassTag.mTagId);
                                        return bundle;
                                    }

                                    @Override
                                    public int getNextDataType() {
                                        return TYPE_DATA_LIST;
                                    }

                                    @Override
                                    public String getNextDataId() {
                                        return PRE_DATA_TYPE+TYPE_TAGS+squareSceneClassTag.mTagId;
                                    }

                                    @Override
                                    public int getIconType() {
                                        return 0;
                                    }
                                };
                                subItems.add(musicServicesubItem);
                            }
                        }
                        //写入缓冲
                        mCacheUtils.put(MUSIC_SCENE,sceneArray,24*CacheUtils.HOUR);

                        if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                        }
                    }else{
                        if(mIMusicServiceSubItemList != null){
                            mIMusicServiceSubItemList.onError(squareSceneClass.getErrorCode(),squareSceneClass.getErrorDescription());
                        }
                    }
                }
            });
        }else {
            for (int i=0; i<sceneArray.length(); i++){
                try {
                    JSONObject itemObject = sceneArray.getJSONObject(i);
                    final String id = itemObject.getString("id");
                    final String name = itemObject.getString("name");

                    IMusicServiceSubItem musicServicesubItem = new IMusicServiceSubItem() {
                        @Override
                        public String getItemIconUrl() {
                            return null;
                        }

                        @Override
                        public String getItemText() {
                            return name;
                        }

                        @Override
                        public Bundle gotoNext() {
                            Bundle bundle = new Bundle();
                            bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                            bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                            bundle.putString(CATEGORY_ID,CATEGORY_NAMES[1]);
                            bundle.putInt(TAG_ID, Integer.parseInt(id));
                            return bundle;
                        }

                        @Override
                        public int getNextDataType() {
                            return TYPE_DATA_LIST;
                        }

                        @Override
                        public String getNextDataId() {
                            return PRE_DATA_TYPE+TYPE_TAGS+Integer.parseInt(id);
                        }

                        @Override
                        public int getIconType() {
                            return 0;
                        }
                    };
                    subItems.add(musicServicesubItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
            }
        }
    }

    private IMusicServiceSubItem getIMusicServiceSubItem(final SongTable table){
        IMusicServiceSubItem subItem = new IMusicServiceSubItem() {

            @Override
            public String getItemIconUrl() {
                return table.listImage;
            }

            @Override
            public String getItemText() {
                return table.listName;
            }

            @Override
            public Bundle gotoNext() {
                Bundle bundle = new Bundle();
                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                bundle.putInt(PRE_DATA_TYPE, TYPE_TAGS);
                bundle.putString(CATEGORY_ID,CATEGORY_NAMES[1]);
                bundle.putInt(TAG_ID,table.tag_id);
                return bundle;
            }

            @Override
            public int getNextDataType() {
                return TYPE_DATA_LIST;
            }

            @Override
            public String getNextDataId() {
                return PRE_DATA_TYPE+TYPE_TAGS+table.tag_id;
            }

            @Override
            public int getIconType() {
                return 0;
            }
        };
        return subItem;
    }

    public void initSquareNewSongs(){
        final ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        JSONArray newsongArray = mCacheUtils.getJSONArray(MUSIC_NEWSONG);
        if (newsongArray == null){
            squareManager.getSquareNewSongsAsync(new SquareManager.SquareNewSongsListener() {
                @Override
                public void onGetSquareNewSongs(SquareNewSongs squareNewSongs) {
                    if(squareNewSongs != null && squareNewSongs.getErrorCode() == SquareMusic.OK) {
                        JSONArray listArray = new JSONArray();
                        int count = 0;
                        for (int i = 0 ; i < squareNewSongs.mSongInfo.size(); i++){
                            List<SongTable> songs = squareNewSongs.mSongInfo.get(i).mLists;
                            for(int j = 0;j < songs.size();j++){
                                SongTable table = songs.get(j);
                                //IMusicServiceSubItem subItem = getIMusicServiceSubItem(table);
                                JSONObject jsonObject = CacheCurrentSontTable(table);
                                try {
                                    listArray.put(count,jsonObject);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                count ++;
                                IMusicServiceSubItem subItem = getIMusicServiceSubItemAlbum(table);
                                subItems.add(subItem);
                            }
                        }

                        mCacheUtils.put(MUSIC_NEWSONG,listArray);
                        if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                        }
                    }else{
                        if(mIMusicServiceSubItemList != null){
                            mIMusicServiceSubItemList.onError(squareNewSongs.getErrorCode(),squareNewSongs.getErrorDescription());
                        }
                    }
                }

            });
        }else {
            for (int i = 0;i<newsongArray.length(); i++){
                try {
                    JSONObject jsonObject = newsongArray.getJSONObject(i);
                    IMusicServiceSubItem subItem = getIMusicServiceSubItemAlbumByCache(jsonObject);
                    subItems.add(subItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
            }
        }
    }

    public void initSquareDaRenSongs(){
        final ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        JSONArray darenArray = mCacheUtils.getJSONArray(MUSIC_DAREN);
        if (darenArray == null ){
            squareManager.getSquareDaRenSongsAsync(new SquareManager.SquareDaRenSongsListener() {
                @Override
                public void onGetSquareDaRenSongs(SquareDaRenSongs squareDaRenSongs) {
                    if (squareDaRenSongs != null && squareDaRenSongs.getErrorCode() == SquareMusic.OK){
                        JSONArray listArray = new JSONArray();
                        for (int i = 0; i < squareDaRenSongs.mLists.size(); i++){
                            SongTable songTable = squareDaRenSongs.mLists.get(i);
                            //IMusicServiceSubItem subItem = getIMusicServiceSubItem(songTable);
                            JSONObject jsonObject = CacheCurrentSontTable(songTable);
                            try {
                                listArray.put(i,jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            IMusicServiceSubItem subItem = getIMusicServiceSubItemAlbum(songTable);
                            subItems.add(subItem);
                        }
                        if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                        }

                        mCacheUtils.put(MUSIC_DAREN,listArray);
                    }else{
                        if(mIMusicServiceSubItemList != null){
                            mIMusicServiceSubItemList.onError(squareDaRenSongs.getErrorCode(),squareDaRenSongs.getErrorDescription());
                        }
                    }
                }
            });
        }else {
            for (int i = 0;i<darenArray.length(); i++){
                try {
                    JSONObject jsonObject = darenArray.getJSONObject(i);
                    IMusicServiceSubItem subItem = getIMusicServiceSubItemAlbumByCache(jsonObject);
                    subItems.add(subItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
            }
        }
    }

    public void initSquareRankSongs(){
        final ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        JSONArray rankArray = mCacheUtils.getJSONArray(MUSIC_PAIHANG);
        if(rankArray == null){
            squareManager.getSquareRankSongsAsync(new SquareManager.SquareRankSongsListener() {

                @Override
                public void onGetSquareRankSongs(SquareRankSongs squareRankSongs) {
                    if(squareRankSongs !=null && squareRankSongs.getErrorCode() == SquareMusic.OK){
                        JSONArray listArray = new JSONArray();
                        for (int i = 0; i < squareRankSongs.mSongInfoLists.size(); i++){
                            SongTable songTable = squareRankSongs.mSongInfoLists.get(i);
                            //IMusicServiceSubItem subItem = getIMusicServiceSubItem(songTable);

                            JSONObject jsonObject = CacheCurrentSontTable(songTable);
                            try {
                                listArray.put(i,jsonObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            IMusicServiceSubItem subItem = getIMusicServiceSubItemAlbum(songTable);
                            subItems.add(subItem);
                        }
                        if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                            mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                        }
                        //写入缓冲
                        mCacheUtils.put(MUSIC_PAIHANG,listArray);
                    }else{
                        if(mIMusicServiceSubItemList != null){
                            mIMusicServiceSubItemList.onError(squareRankSongs.getErrorCode(),squareRankSongs.getErrorDescription());
                        }
                    }
                }
            });
        }else {
            for (int i = 0;i<rankArray.length(); i++){
                try {
                    JSONObject jsonObject = rankArray.getJSONObject(i);
                    IMusicServiceSubItem subItem = getIMusicServiceSubItemAlbumByCache(jsonObject);
                    subItems.add(subItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
            }
        }
    }

    private JSONObject CacheCurrentSontTable(SongTable songTable){
        JSONObject itemObject = new JSONObject();
        try {
            itemObject.put("image",songTable.listImage);
            itemObject.put("name",songTable.listName);
            itemObject.put("id",songTable.listId);
            itemObject.put("type",songTable.listType);
            itemObject.put("src",songTable.listSrc);
            itemObject.put("avatar",songTable.getAvatar());
            itemObject.put("desc",songTable.getDesc());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemObject;

    }

    public IMusicServiceSubItem getIMusicServiceSubItemAlbumByCache(JSONObject jsonObject){
        IMusicServiceSubItemAlbum subItem = null;
        try {
            final String listImage = jsonObject.getString("image");
            final String listName = jsonObject.getString("name");
            final String listId = jsonObject.getString("id");
            final String listType = jsonObject.getString("type");
            final String listSrc = jsonObject.getString("src");
            final String listAvatar = jsonObject.getString("avatar");
            final String listDesc = jsonObject.getString("desc");
            subItem = new IMusicServiceSubItemAlbum() {

                @Override
                public String getItemIconUrl() {
                    return listImage;
                }

                @Override
                public String getItemText() {
                    return listName;
                }

                @Override
                public Bundle gotoNext() {
                    Bundle bundle = new Bundle();
                    bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                    bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
                    bundle.putLong(LIST_ID, Long.parseLong(listId));
                    bundle.putInt(LIST_TYPE, Integer.parseInt(listType));
                    bundle.putInt(LIST_SRC, Integer.parseInt(listSrc));
                    return bundle;
                }

                @Override
                public int getNextDataType() {
                    return TYPE_DATA_ALBUM;
                }

                @Override
                public String getNextDataId() {
                    return PRE_DATA_TYPE+TYPE_ALBUMS+Long.parseLong(listId);
                }

                @Override
                public int getIconType() {
                    return 0;
                }

                @Override
                public String getCoverUrl() {
                    return listImage;
                }

                @Override
                public String getAlbumTitle() {
                    return listName;
                }

                @Override
                public String getAlbumTags() {
                    return listAvatar;
                }

                @Override
                public String getAlbumIntro() {
                    return listDesc;
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
                    return null;
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
            };

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return subItem;
    }

    public void getSquareSceneMusicListbyTagId(final int tagId){
        Glog.i(TAG,"getSquareSceneMusicListbyTagId tagId = "+tagId);
        final ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        final JSONArray sceneListArray = mCacheUtils.getJSONArray(MUSIC_SCENE_ITEM+tagId);
        if (sceneListArray == null){
            squareManager.getSquareMoreSongsAsync(tagId, 0, new SquareManager.SquareMoreSongsListener() {
                @Override
                public void onGetSquareMoreSongs(SquareMoreSongs squareMoreSongs) {
                Glog.i(TAG,"onGetSquareMoreSongs = "+squareMoreSongs);
                if (squareMoreSongs != null && squareMoreSongs.getErrorCode() == SquareMusic.OK){
                    JSONArray listArray = new JSONArray();
                    Glog.i(TAG,"squareMoreSongs.getSongTableList().size = "+squareMoreSongs.getSongTableList().size());
                    for (int i = 0; i < squareMoreSongs.getSongTableList().size(); i++){
                        SongTable songTable = squareMoreSongs.getSongTableList().get(i);

                        JSONObject jsonObject = CacheCurrentSontTable(songTable);
                        try {
                            listArray.put(i,jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        IMusicServiceSubItemAlbum subItem = getIMusicServiceSubItemAlbum(songTable);

                        subItems.add(subItem);
                    }
                    //写入缓冲
                    mCacheUtils.put(MUSIC_SCENE_ITEM+tagId,listArray,24 * CacheUtils.HOUR);

                    if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                    }
                }else{
                    if(mIMusicServiceSubItemList != null){
                        mIMusicServiceSubItemList.onError(squareMoreSongs.getErrorCode(),squareMoreSongs.getErrorDescription());
                    }
                }
                }
            });
        }else {
            for (int i = 0;i<sceneListArray.length(); i++){
                try {
                    JSONObject jsonObject = sceneListArray.getJSONObject(i);
                    IMusicServiceSubItem subItem = getIMusicServiceSubItemAlbumByCache(jsonObject);
                    subItems.add(subItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
            }
        }

    }
    //url
    public String getDetailMusicUrl(final long  musicId, final int musicSrc){
        Glog.i(TAG,"getDetailMusicUrl --> musicId" + musicId + "musicSrc" + musicSrc);
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                Music music = musicManager.getMusicSync(musicId,musicSrc);
                Glog.i(TAG,"getDetailMusicUrl music.mUrl = "+music.mUrl);
            }
        }).start();
        */
        return musicManager.getMusicSync(musicId,musicSrc).mUrl;
    }

    public void getMusicListByMusicListId(long listId, int listType, int listSrc){
        Glog.i(TAG,"getMusicListByMusicListId listId = "+listId+"---listType = "+listType+"---listSrc = "+listSrc);
        musicManager.getMusicListAsync(listId, listType, listSrc, 1, new MusicManager.MusicListener() {

            @Override
            public void onGetMusic(Music music) {

            }

            @Override
            public void onGetMusicList(MusicList musicList) {
                if (musicList != null && musicList.getErrorCode() == SquareMusic.OK){
                    ArrayList<IMusicServiceTrackContent> contents = new ArrayList<IMusicServiceTrackContent>();
                    for(int i = 0; i < (musicList.mItems.size() > 20 ? 20 :musicList.mItems.size()); i++){
                        final Music music = musicList.mItems.get(i);
                        Glog.i(TAG,"getMusicListByMusicListId onGetMusicList music = "+music.toString());
                        IMusicServiceTrackContent content = new IMusicServiceTrackContent(){
                            long musicId = music.mId;
                            int musicSrc = music.mSrc;

                            @Override
                            public String getCoverUrlSmall() {
                                return music.mSmallAlbumImage;
                            }

                            @Override
                            public String getCoverUrlMiddle() {
                                return music.mAlbumImage;
                            }

                            @Override
                            public String getCoverUrlLarge() {
                                return music.mAlbumImage;
                            }

                            @Override
                            public String getTrackTitle() {
                                return music.mName;
                            }

                            @Override
                            public int getUrlType() {
                                return IMusicServiceTrackContent.DYNAMIC_URL;
                            }

                            @Override
                            public String getPlayUrl() {
                                return "";
//                                UriThread thread = new UriThread(musicId,musicSrc);
//
//                                try {
//                                    thread.start();
//                                    thread.join();
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//                                Glog.i(TAG,"getPlayUrl  ---> :" + thread.uri);
//                                return thread.uri;
                            }

                            @Override
                            public String getSinger() {
                                return music.mSinger;
                            }

                            @Override
                            public long getDuration() {
                                if (currentTpye == CATEGORY_NAMES[0]){
                                    Glog.i(TAG,"music.mDuration :" +currentTpye + music.mDuration);
                                    return music.mDuration;
                                }else {
                                    Glog.i(TAG,"music.mDuration :" +currentTpye + music.mDuration);
                                    return music.mDuration*1000;
                                }
                            }

                            @Override
                            public long getUpdateAt() {
                                return 0;
                            }

                            @Override
                            public String getUri() {
                                return "duoyao_" + music.mId;
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

                        contents.add(content);
                    }
                    if(mIMusicServiceAlbum != null){
                        mIMusicServiceAlbum.createMusicServiceAlbum(contents);
                    }

                }else{
                    if(mIMusicServiceAlbum != null){
                        mIMusicServiceAlbum.onError(musicList.getErrorCode(),musicList.getErrorDescription());
                    }
                }
            }
        });
    }

    private class UriThread extends Thread{
        private long musicId;
        private int musicSrc;
        public String uri;

        public UriThread(long id, int src) {
            musicId = id;
            musicSrc = src;
        }

        @Override
        public void run() {
            uri = getDetailMusicUrl(musicId,musicSrc);
        }
    }

    private IMusicServiceSubItemAlbum getIMusicServiceSubItemAlbum(final SongTable songTable){
        IMusicServiceSubItemAlbum subItem = new IMusicServiceSubItemAlbum() {

            @Override
            public String getItemIconUrl() {
                return songTable.listImage;
            }

            @Override
            public String getItemText() {
                return songTable.listName;
            }

            @Override
            public Bundle gotoNext() {
                Bundle bundle = new Bundle();
                bundle.putString(MY_MUSIC_SERVICE_ID,getMusicServiceID());
                bundle.putInt(PRE_DATA_TYPE, TYPE_ALBUMS);
                bundle.putLong(LIST_ID,songTable.listId);
                bundle.putInt(LIST_TYPE,songTable.listType);
                bundle.putInt(LIST_SRC,songTable.listSrc);
                return bundle;
            }

            @Override
            public int getNextDataType() {
                return TYPE_DATA_ALBUM;
            }

            @Override
            public String getNextDataId() {
                return PRE_DATA_TYPE+TYPE_ALBUMS+songTable.listId;
            }

            @Override
            public int getIconType() {
                return 0;
            }

            @Override
            public String getCoverUrl() {
                return songTable.listImage;
            }

            @Override
            public String getAlbumTitle() {
                return songTable.listName;
            }

            @Override
            public String getAlbumTags() {
                return songTable.getAvatar();
            }

            @Override
            public String getAlbumIntro() {
                return songTable.getDesc();
            }

            @Override
            public long getTotalCount() {
                return songTable.musiccount;
            }

            @Override
            public long getUpdateAt() {
                if(songTable.update !=null){
                    return Long.parseLong(songTable.update);
                }else {
                    return 0;
                }

            }

            @Override
            public String getAlbumId() {
                return null;
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
        };
        return subItem;
    }

    public void getAudioMusicListById(final long tagId){
        final ArrayList<IMusicServiceSubItem> subItems = new ArrayList<IMusicServiceSubItem>();
        JSONArray listArray = mCacheUtils.getJSONArray(AUDIO_SQUARE_ITEM+String.valueOf(tagId));
        if (listArray == null){
            squareManager.getSquareTagListsByCategoryid(tagId, 0, new SquareManager.SquareAudioTagListListener() {
                @Override
                public void onGetSquareAudioTagList(SquareAudioTagList squareAudioTagList) {
                if(squareAudioTagList != null && squareAudioTagList.getErrorCode() == SquareAudio.OK){
                    JSONArray listArray = new JSONArray();
                    for (int i = 0; i< squareAudioTagList.mAll_ists.size(); i++){
                        final SongTable songTable = squareAudioTagList.mAll_ists.get(i);

                        JSONObject jsonObject = CacheCurrentSontTable(songTable);
                        try {
                            listArray.put(i,jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        IMusicServiceSubItemAlbum subItem = getIMusicServiceSubItemAlbum(songTable);
                        subItems.add(subItem);
                    }
                    //写入缓冲
                    mCacheUtils.put(AUDIO_SQUARE_ITEM+tagId,listArray,24 * CacheUtils.HOUR);

                    if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                        mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
                    }
                }else{
                    if(mIMusicServiceSubItemList != null){
                        mIMusicServiceSubItemList.onError(squareAudioTagList.getErrorCode(),squareAudioTagList.getErrorDescription());
                    }
                }
                }

            });
        }else {
            for (int i = 0;i<listArray.length(); i++){
                try {
                    JSONObject jsonObject = listArray.getJSONObject(i);
                    IMusicServiceSubItem subItem = getIMusicServiceSubItemAlbumByCache(jsonObject);
                    subItems.add(subItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            if (mIMusicServiceSubItemList != null && subItems.size() > 0) {
                mIMusicServiceSubItemList.createMusicServiceSubItemList(subItems);
            }
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
        ArrayList<IMusicServiceTrackContent> trackContents = new ArrayList<IMusicServiceTrackContent>();
        if(mIMusicServiceAlbum != null){
            mIMusicServiceAlbum.createMusicServiceAlbum(trackContents);
        }
    }

}
