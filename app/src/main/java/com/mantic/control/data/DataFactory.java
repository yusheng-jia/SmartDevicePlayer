package com.mantic.control.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.text.TextUtils;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.MyChannelAddRsBean;
import com.mantic.control.entiy.MusicService;
import com.mantic.control.entiy.SleepInfo;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.musicservice.BaiduSoundData;
import com.mantic.control.musicservice.BeiwaMusicService;
import com.mantic.control.musicservice.EntertainmentService;
import com.mantic.control.musicservice.IdaddyMusicService;
import com.mantic.control.musicservice.KaolaMusicService;
import com.mantic.control.musicservice.LanrenMusicService;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.musicservice.NapsterMusicService;
import com.mantic.control.musicservice.QingtingMusicService;
import com.mantic.control.musicservice.TunelnRadioMusicService;
import com.mantic.control.musicservice.WangyiMusicService;
import com.mantic.control.musicservice.XimalayaSoundData;
import com.mantic.control.utils.FileUtil;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.GsonUtil;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscriber;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 17-4-12.
 */
public class DataFactory {
    private MyChannelOperatorServiceApi mMyChannelOperatorServiceApi;


    private static final String TAG = "DataFactory";
    private static DataFactory DATA_FACTORY;
    private ArrayList<MyChannel> mMyChannelList = new ArrayList<MyChannel>();
    private ArrayList<MyChannel> mMyInterestChannelList = new ArrayList<MyChannel>();
    private ArrayList<MyChannel> mDefinitionMyChannelList = new ArrayList<MyChannel>();
    private ArrayList<Channel> mMyLikeMusicList = new ArrayList<Channel>();
    private String myLikeUri = "";
    private ArrayList<MyChannelListListener> mMyChannelListListeners = new ArrayList<MyChannelListListener>();
    public interface MyChannelListListener{
        public void callback(ArrayList<MyChannel> myChannels);
    }

    /*
    private Flowable<ArrayList<Channel>> mBeingPlayListFlowable;
    //private Subscriber<? super ArrayList<Channel>> mBeingPlayListSubscriber;
    private ArrayList<Subscriber<? super ArrayList<Channel>>> mBeingPlayListSubscriberList = new ArrayList<Subscriber<? super ArrayList<Channel>>>();
    private ArrayList<Disposable> mBeingPlayListDisposable = new ArrayList<Disposable>();
    */

    private List<SleepInfo> mSleepInfoList = new ArrayList<SleepInfo>();

    private ArrayList<Channel> mBeingPlayList = new ArrayList<Channel>();

    private ArrayList<Channel> recentPlayList = new ArrayList<Channel>();

    private ArrayList<String> searchHistoryList = new ArrayList<String>();


    private ArrayList<RefreshMyChannelListListener> mRefreshMyChannelListListeners = new ArrayList<RefreshMyChannelListListener>();
    private ArrayList<BeingPlayListListener> mBeingPlayListListeners = new ArrayList<BeingPlayListListener>();
    private ArrayList<OnAddMyMusciServiceListener> myMusicServiceListListeners = new ArrayList<OnAddMyMusciServiceListener>();
    private ArrayList<OnMyLikeMusicListener> mMyLikeMusicListeners = new ArrayList<OnMyLikeMusicListener>();
    private ArrayList<OnMyLikeMusicStateListener> mMyLikeMusicstatusListeners = new ArrayList<OnMyLikeMusicStateListener>();
    private ArrayList<OnAddChannelListener> mOnAddChannelListeners = new ArrayList<OnAddChannelListener>();
    private ArrayList<OnOperatorResultListener> mOnOperatorResultListeners = new ArrayList<OnOperatorResultListener>();
    private ArrayList<OnSearchResultListener> mOnSearchResultListeners = new ArrayList<OnSearchResultListener>();
    private ArrayList<OnSearchKeySetListener> mOnSearchKeySetListeners = new ArrayList<OnSearchKeySetListener>();
    private ArrayList<OnRecentPlayListener> mOnRecentPlayListeners = new ArrayList<OnRecentPlayListener>();
    private ArrayList<OnSearchHistoryListener> mOnSearchHistoryListeners = new ArrayList<OnSearchHistoryListener>();
    private ArrayList<OnHideSoftKeyListener> mOnHideSoftKeyListeners = new ArrayList<OnHideSoftKeyListener>();
    private ArrayList<OnUpdateDeviceNameListener> mOnUpdateDeviceNameListeners = new ArrayList<OnUpdateDeviceNameListener>();
    private ArrayList<OnMyChannelStateChangeListener> mOnMyChannelStateChangeListeners = new ArrayList<OnMyChannelStateChangeListener>();
    private ArrayList<OnSleepStateChangeListener> mOnSleepStateChangeListeners = new ArrayList<OnSleepStateChangeListener>();
    private ArrayList<OnDeviceModeChangeListener> mOnDeviceModeChangeListeners = new ArrayList<OnDeviceModeChangeListener>();
    private ArrayList<OnUpdateAudioPlayerByBaiduDurationListener> mOnUpdateAudioPlayerByBaiduDurationListeners = new ArrayList<OnUpdateAudioPlayerByBaiduDurationListener>();

    private ArrayList<OnMainViewPagerListener> mOnMainViewPagerListeners = new ArrayList<OnMainViewPagerListener>();

    /*设备在线状态*/
    private ArrayList<DeviceOnlineStateListener> mDeviceOnlineStatusListeners = new ArrayList<DeviceOnlineStateListener>();
    /*设备在线状态*/

    public interface RefreshMyChannelListListener{
        void refreshMyChannelList();
    }

    /*设备在线状态*/
    public interface DeviceOnlineStateListener{
        void onlineStatus(boolean status);
    }
    /*设备在线状态*/

    public interface BeingPlayListListener{
        void callback(ArrayList<Channel> beingPlayList);
    }

    public interface OnAddMyMusciServiceListener {
        void addToMyMusicService(MusicService musicService, boolean isNotify);
        void removeToMyMusicService(MusicService musicService, boolean isNotify);
    }

    public interface OnAddChannelListener {
        void addChannel(List<Channel> channelList);
    }


    public interface OnMyLikeMusicListener {
        void changeMyLikeMusicCount();
    }

    public interface OnMyLikeMusicStateListener {
        void changeMyLikeMusicState();
    }

    public interface OnOperatorResultListener {
        void operatorResult(String content, boolean success);
    }

    public interface OnSearchResultListener {
        void searchResult(Object object, int type);
    }

    public interface OnSearchKeySetListener {
        void setSearchKey(String key);
    }

    public interface OnRecentPlayListener {
        void recentPlayChange(List<Channel> channelList);
    }

    public interface OnSearchHistoryListener {
        void searchHistoryChange(List<String> searchHistoryList);
    }

    public interface OnHideSoftKeyListener {
        void hideSoftKey();
    }

    public interface OnUpdateDeviceNameListener {
        void updateDeviceName(String deviceName);
    }

    public interface OnMyChannelStateChangeListener {
        void myChannelStateChange(boolean isAdd);
    }

    public interface OnSleepStateChangeListener {
        void sleepStateOrClearTimeChange(boolean isSleepState);
        void resetTimeChange(int time);
    }

    public interface OnDeviceModeChangeListener {
        void deviceModeChange(int deviceMode);
    }

    public interface OnUpdateAudioPlayerByBaiduDurationListener {
        void updateAudioPlayerByBaiduDuration();
    }

    public interface OnMainViewPagerListener {
        void callScroll(boolean canScroll);
    }

    private ArrayList<ChannelControlListener> mChannelControlListeners = new ArrayList<ChannelControlListener>();
    private Channel mCurrChannel;

    public interface ChannelControlListener{
        public void preChannelControl();
        public void afterChannelControl();
        public void beginChannelControl(int index);
    }


    private ArrayList<MusicService> mMusicServiceList = new ArrayList<MusicService>();
    //用于搜索时候用的uris
    private List<String> myMusicServiceUriList = new ArrayList<String>();

    private List<MusicService> mMyMusicServiceList = new ArrayList<MusicService>();

    private ArrayList<ManticDevice> mManticDeviceList = new ArrayList<ManticDevice>();

    private Flowable<ArrayList<MyChannel>> mMyChannelListFlowable;
    private Subscriber<? super ArrayList<MyChannel>> mMyChannelListSubscriber;
    private ArrayList<Disposable> mMyChannelListDisposable = new ArrayList<Disposable>();

    public static DataFactory newInstance(Context mContext){
        if(DATA_FACTORY == null){
            DATA_FACTORY = new DataFactory(mContext);
        }
        return DATA_FACTORY;
    }

    public static DataFactory getInstance(){
        return DATA_FACTORY;
    }

    public DataFactory(Context mContext){
        this.initData(mContext);
    }

    public void clearDataFactory() {
        if(DATA_FACTORY != null){
            DATA_FACTORY = null;
        }
    }

    /*
    public static Subscriber<Channel> newChannelPlaySubscriber(){
        return new Subscriber<Channel>() {
            @Override
            public void onSubscribe(Subscription s) {

            }

            @Override
            public void onNext(Channel channel) {

            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
    }
    */

    //我喜欢的歌
    public int getMyLikeMusicCount(){
        //虚拟数据start
        return mMyLikeMusicList.size();//18;
        //虚拟数据end
    }


    private int interestNumber = 0;
    private int recStatus = 1;
    private void initData(final Context mContext){
        mMyChannelOperatorServiceApi = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class);
        SharedPreferences sp = ManticSharedPreferences.getInstance(mContext);
        String interest = sp.getString(ManticSharedPreferences.KEY_INTEREST,null);
        if(!TextUtils.isEmpty(interest)){
            final String[] its = interest.split(",");
            if(its.length > 0 ){
                mMyInterestChannelList = FileUtil.getInterestChannel(its, "InterestChannels.txt", mContext);
                this.mMyChannelList = mMyInterestChannelList;
                for (int i = 0; i < mMyInterestChannelList.size(); i++) {
                    Call<MyChannelAddRsBean> addCall = mMyChannelOperatorServiceApi.postMyChannelAddQuest(MopidyTools.getHeaders(),Util.createAddRqBean(mMyChannelList.get(i), mContext));
                    MyChannelManager.addMyChannel(addCall, new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            if (response.isSuccessful() && null == response.errorBody()) {
                                interestNumber ++;
                                if (interestNumber == mMyInterestChannelList.size()) {
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            notifyMyChannelListRefresh();
                                        }
                                    }, 1250);
                                }

                                if (recStatus > 0) {
                                    recStatus --;
                                    RequestBody body = MopidyTools.createSetRecStatus(true, mContext );
                                    MopidyServiceApi mopidyServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
                                    Call<ResponseBody> recStatusCall = mopidyServiceApi.postMopidySetRecStatus(MopidyTools.getHeaders(),body);
                                    recStatusCall.enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            if (response.isSuccessful() && null == response.errorBody()) {
                                                try {
                                                    JSONObject mainObject = new JSONObject(response.body().string()); //json 主体
                                                    boolean result = mainObject.optBoolean("result");// result json 主体
                                                    if (result) {

                                                    } else {
                                                        recStatus ++;
                                                    }

                                                } catch (JSONException | IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            recStatus ++;
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    }, mMyChannelList.get(i));
                }

                sp.edit().putString(ManticSharedPreferences.KEY_INTEREST, "").apply();
            }
        }

/*
        MusicService qqMusicService = new MusicService();
        qqMusicService.setMyMusicService(DuoyaoMusicService.getInstance(mContext));
        qqMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.xiami_music_service_icon));
        qqMusicService.setName("虾米音乐");
        qqMusicService.setActive(true);
        this.mMusicServiceList.add(qqMusicService);
        mMyMusicServiceList.add(qqMusicService);
        MusicService xmMusicService = new MusicService();
        xmMusicService.setMyMusicService(DuoyaoMusicService.getInstance(mContext));
        xmMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.net_radio));
        xmMusicService.setName("多听FM");
        xmMusicService.setActive(true);
        this.mMusicServiceList.add(xmMusicService);
        mMyMusicServiceList.add(xmMusicService);*/
        /*
        MusicService lzMusicService = new MusicService();
        lzMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.lizhi_music_service_icon));
        lzMusicService.setName("荔枝FM");
        this.mMusicServiceList.add(lzMusicService);
        */

        MusicService baiduMusicService = new MusicService();
        baiduMusicService.setMyMusicService(new BaiduSoundData(mContext));
        baiduMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.baidu_music_service_icon));
        baiduMusicService.setName("百度云音乐");
        baiduMusicService.setActive(true);
        baiduMusicService.setIntroduction(mContext.getString(R.string.baidu_introduction));
        this.mMusicServiceList.add(baiduMusicService);
        mMyMusicServiceList.add(baiduMusicService);

        MusicService xmlyMusicService = new MusicService();
        xmlyMusicService.setMyMusicService(new XimalayaSoundData(mContext));
        xmlyMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.ximalaya_music_service_icon));
        xmlyMusicService.setName("喜马拉雅");
        xmlyMusicService.setActive(true);
        xmlyMusicService.setIntroduction(mContext.getString(R.string.ximalaya_introduction));
        this.mMusicServiceList.add(xmlyMusicService);
        mMyMusicServiceList.add(xmlyMusicService);

        MusicService wyyyMusicService = new MusicService();
        wyyyMusicService.setMyMusicService(new WangyiMusicService());
        wyyyMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.wyyyy_music_service_icon));
        wyyyMusicService.setName("网易云音乐");
        wyyyMusicService.setActive(true);
        wyyyMusicService.setIntroduction(mContext.getString(R.string.wangyi_introduction));
        this.mMusicServiceList.add(wyyyMusicService);
        mMyMusicServiceList.add(wyyyMusicService);

        MusicService qingtingMusicService = new MusicService();
        qingtingMusicService.setMyMusicService(new QingtingMusicService());
        qingtingMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.qingting_music_service_icon));
        qingtingMusicService.setName("蜻蜓FM");
        qingtingMusicService.setIntroduction(mContext.getString(R.string.qingting_introduction));
        this.mMusicServiceList.add(qingtingMusicService);
        mMyMusicServiceList.add(qingtingMusicService);


        MusicService idaddyMusicService = new MusicService();
        idaddyMusicService.setMyMusicService(new IdaddyMusicService());
        idaddyMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.idaddy_music_service_icon));
        idaddyMusicService.setName("工程师爸爸");
        idaddyMusicService.setIntroduction(mContext.getString(R.string.idaddy_introduction));
        this.mMusicServiceList.add(idaddyMusicService);
        mMyMusicServiceList.add(idaddyMusicService);

        MusicService entertainmentService = new MusicService();
        entertainmentService.setMyMusicService(new EntertainmentService(mContext));
        entertainmentService.setName("娱乐服务");
        this.mMusicServiceList.add(entertainmentService);


        /*MusicService kaolaMusicService = new MusicService();
        kaolaMusicService.setMyMusicService(new KaolaMusicService());
        kaolaMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.kaola_music_service_icon));
        kaolaMusicService.setName("考拉FM");
        kaolaMusicService.setIntroduction(mContext.getString(R.string.kaola_introduction));
        this.mMusicServiceList.add(kaolaMusicService);

        MusicService beiwaMusicService = new MusicService();
        beiwaMusicService.setMyMusicService(new BeiwaMusicService());
        beiwaMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.beiwa_music_service_icon));
        beiwaMusicService.setName("贝瓦听听");
        beiwaMusicService.setIntroduction(mContext.getString(R.string.bewa_introduction));
        this.mMusicServiceList.add(beiwaMusicService);



        MusicService lanrenMusicService = new MusicService();
        lanrenMusicService.setMyMusicService(new LanrenMusicService());
        lanrenMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.lanren_music_service_icon));
        lanrenMusicService.setName("懒人听书");
        lanrenMusicService.setIntroduction(mContext.getString(R.string.lanren_introduction));
        this.mMusicServiceList.add(lanrenMusicService);

        MusicService napsterMusicService = new MusicService();
        napsterMusicService.setMyMusicService(new NapsterMusicService());
        napsterMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.spotify_music_service_icon));
        napsterMusicService.setName("Spotify");
        napsterMusicService.setIntroduction(mContext.getString(R.string.napster_introduction));
        this.mMusicServiceList.add(napsterMusicService);

        MusicService tunelnMusicService = new MusicService();
        tunelnMusicService.setMyMusicService(new TunelnRadioMusicService());
        tunelnMusicService.setIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.tuneln_music_service_icon));
        tunelnMusicService.setName("TunelnRadio");
        tunelnMusicService.setIntroduction(mContext.getString(R.string.tunein_introduction));
        this.mMusicServiceList.add(tunelnMusicService);
*/
        this.mManticDeviceList = new ArrayList<ManticDevice>();
        ManticDevice md1 = new ManticDevice();
        md1.setState(1);
        md1.setName("Mantic(102548)");
        this.mManticDeviceList.add(md1);
        ManticDevice md2 = new ManticDevice();
        md2.setState(2);
        md2.setName("Mantic(102549)");
        this.mManticDeviceList.add(md2);
        ManticDevice md3 = new ManticDevice();
        md3.setState(0);
        md3.setName("卧室的音");
        this.mManticDeviceList.add(md3);
        //虚拟数据end

        //睡眠时间开始
        mSleepInfoList.add(new SleepInfo(mContext.getString(R.string.not_open), true, -2));
        mSleepInfoList.add(new SleepInfo(mContext.getString(R.string.after_current_audio), false, -1));
        mSleepInfoList.add(new SleepInfo(mContext.getString(R.string.after_ten_minute), false, 600));
        mSleepInfoList.add(new SleepInfo(mContext.getString(R.string.after_twenty_minute), false, 1200));
        mSleepInfoList.add(new SleepInfo(mContext.getString(R.string.after_thirty_minute), false, 1800));
        mSleepInfoList.add(new SleepInfo(mContext.getString(R.string.after_sixty_minute), false, 3600));
        mSleepInfoList.add(new SleepInfo(mContext.getString(R.string.after_ninety_minute), false, 5400));

        //睡眠时间结束
    }

    public MyMusicService getMyMusicServiceFromServiceId(String serviceId){
        MyMusicService service = null;
        ArrayList<MusicService> musicServiceList = this.getMusicServiceList();
        for(int i = 0;i < musicServiceList.size();i++) {
            MyMusicService myMusicService = musicServiceList.get(i).getMyMusicService();
            if(myMusicService == null || myMusicService.getMusicServiceID() == null){
                continue;
            }

            if(serviceId.equals(myMusicService.getMusicServiceID())){
                service = myMusicService;
            }
        }
        return service;
    }

    public ArrayList<MusicService> getMusicServiceList(){
        return this.mMusicServiceList;
    }

    public List<MusicService> getmMyMusicServiceList() {
        return mMyMusicServiceList;
    }

    public void removeMyService(MusicService musicService) {
        Glog.i(TAG, "removeMyService: " + mMyMusicServiceList.size());
        for (int i = (mMyMusicServiceList.size() - 1); i >= 0; i--) {
            Glog.i(TAG, "removeMyService: " + i);
            if (mMyMusicServiceList.get(i).getName().equals(musicService.getName())) {
                mMyMusicServiceList.remove(i);
            }
        }
    }

    public void setMyMusicServiceList(List<MusicService> mMyMusicServiceList) {
        this.mMyMusicServiceList = mMyMusicServiceList;
    }

    public void setMyMusicServiceUriList(List<String> myMusicServiceUriList) {
        this.myMusicServiceUriList = myMusicServiceUriList;
    }

    public List<String> getMyMusicServiceUriList() {
        return myMusicServiceUriList;
    }

    public void registerMyChannelListSubscriber(Subscriber<? super ArrayList<MyChannel>> myChannelListSubscriber){
        if(this.mMyChannelListFlowable == null){
            /*
            this.mMyChannelListFlowable = Flowable.create(new FlowableOnSubscribe<ArrayList<MyChannel>>() {
                @Override
                public void subscribe(FlowableEmitter<ArrayList<MyChannel>> e) throws Exception {
                    e.onNext(mMyChannelList);
                    e.onComplete();
                }
            }, BackpressureStrategy.BUFFER);
            */
            this.mMyChannelListFlowable = Flowable.just(this.mMyChannelList);
        }
        this.mMyChannelListSubscriber = myChannelListSubscriber;
    }

    private void notifyMyChannelListChange(){
        if(this.mMyChannelListSubscriber != null){
            Disposable disposable = this.mMyChannelListFlowable.subscribe(new Consumer<ArrayList<MyChannel>>(){

                @Override
                public void accept(ArrayList<MyChannel> channels) throws Exception {
                    mMyChannelListSubscriber.onNext(channels);
                    mMyChannelListSubscriber.onComplete();
                }

            });
            this.mMyChannelListDisposable.add(disposable);
        }
    }

    public void unRegisterMyChannelListSubscriber(){
        for(int i = 0;i < this.mMyChannelListDisposable.size();i++){
            Disposable dispos = this.mMyChannelListDisposable.get(i);
            if(dispos != null){
                if(!dispos.isDisposed()){
                    dispos.dispose();
                }
            }
            this.mMyChannelListDisposable.clear();
        }
    }


    public void setMyChannelList (ArrayList<MyChannel> myChannelList) {
        this.mMyChannelList = myChannelList;
    }


    public ArrayList<MyChannel> getMyChannelList(){
        return this.mMyChannelList;
    }

    public ArrayList<MyChannel> getDefinitionMyChannelList() {
        for (int i = 0; i < mDefinitionMyChannelList.size(); i++) {
            mDefinitionMyChannelList.get(i).setSelect(false);
        }
        return mDefinitionMyChannelList;
    }

    public void setDefinitionMyChannelList(ArrayList<MyChannel> mDefinitionMyChannelList) {
        this.mDefinitionMyChannelList = mDefinitionMyChannelList;
    }

    public ArrayList<Channel> getMyLikeMusicList() {
        return mMyLikeMusicList;
    }

    public void setMyLikeMusicList(ArrayList<Channel> myLikeMusicList) {
        this.mMyLikeMusicList = myLikeMusicList;
    }

    public String getMyLikeUri() {
        return myLikeUri;
    }

    public void setMyLikeUri(String myLikeUri) {
        this.myLikeUri = myLikeUri;
    }

    public void addMyLikeMusic(Channel channel){
        this.mMyLikeMusicList.add(channel);
    }

    public void removeMyLikeMusic(Channel channel) {
        this.mMyLikeMusicList.remove(channel);
    }

    public boolean isExistMyLikeMusic(Channel channel) {
        for (int i = 0; i < mMyLikeMusicList.size(); i++) {
            if (mMyLikeMusicList.get(i).getName().equals(channel.getName()) && mMyLikeMusicList.get(i).getUri().equals(channel.getUri())) {
                return true;
            }
        }

        return false;
    }

    public boolean isExistMyRadio(String uri) {
        int indexOfTrack = uri.indexOf("track:");
        String headUri = uri.substring(0, indexOfTrack + 6);
        String tailUri = uri.substring(indexOfTrack + 6);
        int indexOfColon = tailUri.indexOf(":");
        headUri = headUri + tailUri.substring(0, indexOfColon);
        headUri = headUri.replace(":track:", ":album:");
        for (int i = 0; i < mMyChannelList.size(); i++) {
            if (!TextUtils.isEmpty(mMyChannelList.get(i).getAlbumId()) && mMyChannelList.get(i).getAlbumId().equals(headUri)) {
                return true;
            }
        }

        return false;
    }

    public String radioMyChannelUrl(String uri) {
        int indexOfTrack = uri.indexOf("track:");
        String headUri = uri.substring(0, indexOfTrack + 6);
        String tailUri = uri.substring(indexOfTrack + 6);
        int indexOfColon = tailUri.indexOf(":");
        headUri = headUri + tailUri.substring(0, indexOfColon);
        headUri = headUri.replace(":track:", ":album:");
        for (int i = 0; i < mMyChannelList.size(); i++) {
            if (!TextUtils.isEmpty(mMyChannelList.get(i).getAlbumId()) && mMyChannelList.get(i).getAlbumId().equals(headUri)) {
                return mMyChannelList.get(i).getUrl();
            }
        }

        return "";
    }


    public void addMyChannel(MyChannel channel){
        this.mMyChannelList.add(channel);
        //this.notifyMyChannelListChange();
        this.notifyMyChannelListChanged();
    }

    public void removeMyChannel(MyChannel channel){
        for (int i = 0; i < mMyChannelList.size(); i++) {
            if (mMyChannelList.get(i).getChannelName().equals(channel.getChannelName())
                    && mMyChannelList.get(i).getUrl().equals(channel.getUrl())) {
                this.mMyChannelList.remove(i);
            }
        }
        this.notifyMyChannelListChanged();
    }

    public void addMyChannelAt(int position,MyChannel channel){
        this.mMyChannelList.add(position,channel);
        //this.notifyMyChannelListChange();
        this.notifyMyChannelListChanged();
    }

    public void setMyChannelAt(int position,MyChannel channel){
        this.mMyChannelList.set(position,channel);
        //this.notifyMyChannelListChange();
        this.notifyMyChannelListChanged();
    }

    public void removeMyChannelAt(int position){
        if (this.mMyChannelList.size() > position) {
            this.mMyChannelList.remove(position);
            this.notifyMyChannelListChanged();
        }
        //this.notifyMyChannelListChange();
    }

    public void removeDefinnitionMyChannel(Context mContext, MyChannel myChannel){
        Glog.i(TAG, "removeDefinnitionMyChannel: " + myChannel.getChannelName());
        for (int i = 0; i < mDefinitionMyChannelList.size(); i++) {
            if (mDefinitionMyChannelList.get(i).getChannelName().equals(myChannel.getChannelName())
                    && mDefinitionMyChannelList.get(i).getChannelCoverUrl().equals(myChannel.getChannelCoverUrl())) {
                mDefinitionMyChannelList.remove(i);
//                ACacheUtil.putData(mContext, "DefinitionMyChannelList", GsonUtil.myChannelListToString(mDefinitionMyChannelList));
                SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "DefinitionMyChannelList", GsonUtil.myChannelListToString(mDefinitionMyChannelList));
            }
        }
    }


    public MyChannel getMyChannelAt(int position){
        return this.mMyChannelList.get(position);
    }

    public MyChannel getMyChannelFrom(String musicServiceId,String channelId,String channelName){
        MyChannel myChannel = null;
        for(int i = 0;i < mMyChannelList.size();i++){
            MyChannel currChannel = mMyChannelList.get(i);
            if (channelId.equals(currChannel.getChannelId()) && channelName.equals(currChannel.getChannelName())
                    && !TextUtils.isEmpty(currChannel.getMusicServiceId()) && currChannel.getMusicServiceId().equals(musicServiceId)) {
                myChannel = currChannel;
                break;
            }
        }
        return myChannel;
    }

    public int getMyChannelSize(){
        return this.mMyChannelList.size();
    }

    /*
    public void registerBeingPlayListSubscriber(Subscriber<? super ArrayList<Channel>> beingPlayListSubscriber){
        if(this.mBeingPlayListFlowable == null){
            this.mBeingPlayListFlowable = Flowable.just(this.mBeingPlayList);
        }
        //this.mBeingPlayListSubscriber = beingPlayListSubscriber;
        if(this.mBeingPlayListSubscriberList.indexOf(beingPlayListSubscriber) == -1) {
            this.mBeingPlayListSubscriberList.add(beingPlayListSubscriber);
        }
    }

    public void unRegisterBeingPlayListSubscriber(){
        for(int i = 0;i < this.mBeingPlayListDisposable.size();i++){
            Disposable dispos = this.mBeingPlayListDisposable.get(i);
            if(dispos != null){
                if(!dispos.isDisposed()){
                    dispos.dispose();
                }
            }
            this.mBeingPlayListDisposable.clear();
        }
    }

    public void unRegisterBeingPlayListSubscriber(Subscriber<? super ArrayList<Channel>> beingPlayListSubscriber){
        this.mBeingPlayListSubscriberList.remove(beingPlayListSubscriber);
    }
    */

    public void registerMyChannelListListener(MyChannelListListener listener){
        if(this.mMyChannelListListeners.indexOf(listener) < 0){
            this.mMyChannelListListeners.add(listener);
        }
    }

    public void unregisterMyChannelListListener(MyChannelListListener listener){
        this.mMyChannelListListeners.remove(listener);
    }

    public void notifyMyChannelListChanged(){
        for(int i = 0;i < this.mMyChannelListListeners.size();i++){
            this.mMyChannelListListeners.get(i).callback(this.mMyChannelList);
        }
    }

    public void registerBeingPlayListListener(BeingPlayListListener listener){
        if(this.mBeingPlayListListeners.indexOf(listener) < 0) {
            this.mBeingPlayListListeners.add(listener);
        }
    }

    public void unregisterBeingPlayListListener(BeingPlayListListener listener){
        this.mBeingPlayListListeners.remove(listener);
    }

    public void registerRefreshMyChannelListListener(RefreshMyChannelListListener listener){
        if(this.mRefreshMyChannelListListeners.indexOf(listener) < 0) {
            this.mRefreshMyChannelListListeners.add(listener);
        }
    }

    public void unregisterRefreshMyChannelListListener(RefreshMyChannelListListener listener){
        this.mRefreshMyChannelListListeners.remove(listener);
    }

    public void registerMyMusiceServiceListListener(OnAddMyMusciServiceListener listener){
        if(this.myMusicServiceListListeners.indexOf(listener) < 0) {
            this.myMusicServiceListListeners.add(listener);
        }
    }

    public void unregisterMyMusiceServiceListListener(OnAddMyMusciServiceListener listener){
        this.myMusicServiceListListeners.remove(listener);
    }

    public void registerMyLikeMusiceListListener(OnMyLikeMusicListener listener){
        if(this.mMyLikeMusicListeners.indexOf(listener) < 0) {
            this.mMyLikeMusicListeners.add(listener);
        }
    }

    public void unregisterMyLikeMusiceListListener(OnMyLikeMusicListener listener){
        this.mMyLikeMusicListeners.remove(listener);
    }


    public void registerMyLikeMusiceStatusListener(OnMyLikeMusicStateListener listener){
        if(this.mMyLikeMusicstatusListeners.indexOf(listener) < 0) {
            this.mMyLikeMusicstatusListeners.add(listener);
        }
    }

    public void unregisterMyLikeMusiceStatusListener(OnMyLikeMusicStateListener listener){
        this.mMyLikeMusicstatusListeners.remove(listener);
    }

    public void registerAddChannelListener(OnAddChannelListener listener){
        if(this.mOnAddChannelListeners.indexOf(listener) < 0) {
            this.mOnAddChannelListeners.add(listener);
        }
    }

    public void unregisterAddChannelListener(OnAddChannelListener listener){
        this.mOnAddChannelListeners.remove(listener);
    }

    public void registerOperatorResultListener(OnOperatorResultListener listener){
        if(this.mOnOperatorResultListeners.indexOf(listener) < 0) {
            this.mOnOperatorResultListeners.add(listener);
        }
    }

    public void unregisterOperatorResultListener(OnOperatorResultListener listener){
        this.mOnOperatorResultListeners.remove(listener);
    }

    public void registerSearchResultListener(OnSearchResultListener listener){
        if(this.mOnSearchResultListeners.indexOf(listener) < 0) {
            this.mOnSearchResultListeners.add(listener);
        }
    }

    public void unregisterSearchResultListener(OnSearchResultListener listener){
        this.mOnSearchResultListeners.remove(listener);
    }

    public void registerSearchKeySetListener(OnSearchKeySetListener listener){
        if(this.mOnSearchKeySetListeners.indexOf(listener) < 0) {
            this.mOnSearchKeySetListeners.add(listener);
        }
    }

    public void unregisterSearchKeySetListener(OnSearchKeySetListener listener){
        this.mOnSearchKeySetListeners.remove(listener);
    }

    public void registerRecentPlayListener(OnRecentPlayListener listener){
        if(this.mOnRecentPlayListeners.indexOf(listener) < 0) {
            this.mOnRecentPlayListeners.add(listener);
        }
    }

    public void unregisterRecentPlayListener(OnRecentPlayListener listener){
        this.mOnRecentPlayListeners.remove(listener);
    }

    public void registerSearchHistoryListener(OnSearchHistoryListener listener){
        if(this.mOnSearchHistoryListeners.indexOf(listener) < 0) {
            this.mOnSearchHistoryListeners.add(listener);
        }
    }

    public void unregisterSearchHistoryListener(OnSearchHistoryListener listener){
        this.mOnSearchHistoryListeners.remove(listener);
    }

    public void registerHideSoftKeyListener(OnHideSoftKeyListener listener){
        if(this.mOnHideSoftKeyListeners.indexOf(listener) < 0) {
            this.mOnHideSoftKeyListeners.add(listener);
        }
    }

    public void unregisterHideSoftKeyListener(OnHideSoftKeyListener listener){
        this.mOnHideSoftKeyListeners.remove(listener);
    }

    public void registerUpdateDeviceNameListener(OnUpdateDeviceNameListener listener){
        if(this.mOnUpdateDeviceNameListeners.indexOf(listener) < 0) {
            this.mOnUpdateDeviceNameListeners.add(listener);
        }
    }

    public void unregisterUpdateDeviceNameListener(OnUpdateDeviceNameListener listener){
        this.mOnUpdateDeviceNameListeners.remove(listener);
    }

    public void registerMyChannelStateChangeListener(OnMyChannelStateChangeListener listener){
        if(this.mOnMyChannelStateChangeListeners.indexOf(listener) < 0) {
            this.mOnMyChannelStateChangeListeners.add(listener);
        }
    }

    public void unregisterMyChannelStateChangeListener(OnMyChannelStateChangeListener listener){
        this.mOnMyChannelStateChangeListeners.remove(listener);
    }

    public void registerSleepStateChangeListener(OnSleepStateChangeListener listener){
        if(this.mOnSleepStateChangeListeners.indexOf(listener) < 0) {
            this.mOnSleepStateChangeListeners.add(listener);
        }
    }

    public void unregisterSleepStateChangeListener(OnSleepStateChangeListener listener){
        this.mOnSleepStateChangeListeners.remove(listener);
    }

    public void registerDeviceModeChangeListener(OnDeviceModeChangeListener listener){
        if(this.mOnDeviceModeChangeListeners.indexOf(listener) < 0) {
            this.mOnDeviceModeChangeListeners.add(listener);
        }
    }

    public void unregisterDeviceModeChangeListener(OnDeviceModeChangeListener listener){
        this.mOnDeviceModeChangeListeners.remove(listener);
    }

    public void registerUpdateAudioPlayerByBaiduDurationListener(OnUpdateAudioPlayerByBaiduDurationListener listener){
        if(this.mOnUpdateAudioPlayerByBaiduDurationListeners.indexOf(listener) < 0) {
            this.mOnUpdateAudioPlayerByBaiduDurationListeners.add(listener);
        }
    }

    public void unregisterUpdateAudioPlayerByBaiduDurationListener(OnUpdateAudioPlayerByBaiduDurationListener listener){
        this.mOnUpdateAudioPlayerByBaiduDurationListeners.remove(listener);
    }

    public void registerMainViewPagerListener(OnMainViewPagerListener listener){
        if(this.mOnMainViewPagerListeners.indexOf(listener) < 0) {
            this.mOnMainViewPagerListeners.add(listener);
        }
    }

    public void unregisterMainViewPagerListener(OnMainViewPagerListener listener){
        this.mOnMainViewPagerListeners.remove(listener);
    }

    /*设备在线状态*/
    public void registerDeviceOnlineStatusListener(DeviceOnlineStateListener listener){
        if(this.mDeviceOnlineStatusListeners.indexOf(listener) < 0) {
            this.mDeviceOnlineStatusListeners.add(listener);
        }
    }

    public void unregisterDeviceOnlineStatusListener(DeviceOnlineStateListener listener){
        this.mDeviceOnlineStatusListeners.remove(listener);
    }
    /*设备在线状态*/

    public void notifyBeingPlayListChange(){
        /*
        if(this.mBeingPlayListSubscriber != null){
            Disposable disposable = this.mBeingPlayListFlowable.subscribe(new Consumer<ArrayList<Channel>>(){

                @Override
                public void accept(ArrayList<Channel> channels) throws Exception {
                    mBeingPlayListSubscriber.onNext(channels);
                    mBeingPlayListSubscriber.onComplete();
                }

            });
            this.mBeingPlayListDisposable.add(disposable);
        }
        */
        /*
        for(int i = 0;i < this.mBeingPlayListSubscriberList.size();i++){
            final Subscriber<? super ArrayList<Channel>> subscriber = this.mBeingPlayListSubscriberList.get(i);
            Disposable disposable = this.mBeingPlayListFlowable.subscribe(new Consumer<ArrayList<Channel>>(){

                @Override
                public void accept(ArrayList<Channel> channels) throws Exception {
                    subscriber.onNext(channels);
                    subscriber.onComplete();
                }

            });
            this.mBeingPlayListDisposable.add(disposable);
        }
        */
        Glog.i(TAG, "notifyBeingPlayListChange: ");
        for(int i = 0;i < this.mBeingPlayListListeners.size();i++){
            this.mBeingPlayListListeners.get(i).callback(this.mBeingPlayList);
        }
    }

    public void notifyMyChannelListRefresh(){
        for(int i = 0;i < this.mRefreshMyChannelListListeners.size();i++){
            this.mRefreshMyChannelListListeners.get(i).refreshMyChannelList();
        }
    }

    public void notifyBMyMusicServiceListChange(MusicService musicService, boolean isAdd, boolean isNeedNotify){
        for(int i = 0;i < this.myMusicServiceListListeners.size();i++){
            if (isAdd) {
                this.myMusicServiceListListeners.get(i).addToMyMusicService(musicService, isNeedNotify);
            } else {
                this.myMusicServiceListListeners.get(i).removeToMyMusicService(musicService, isNeedNotify);
            }

        }
    }

    public void notifyMyLikeMusicListChange(){
        for(int i = 0;i < this.mMyLikeMusicListeners.size();i++){
            this.mMyLikeMusicListeners.get(i).changeMyLikeMusicCount();
        }
    }

    public void notifyMyLikeMusicStatusChange(){
        for(int i = 0;i < this.mMyLikeMusicstatusListeners.size();i++){
            this.mMyLikeMusicstatusListeners.get(i).changeMyLikeMusicState();
        }
    }

    public void notifyChannelAddChange(List<Channel> channelList){
        for(int i = 0;i < this.mOnAddChannelListeners.size();i++){
            this.mOnAddChannelListeners.get(i).addChannel(channelList);
        }
    }

    public void notifyOperatorResult(String content, boolean success){
        for(int i = 0;i < this.mOnOperatorResultListeners.size();i++){
            this.mOnOperatorResultListeners.get(i).operatorResult(content, success);
        }
    }

    public void notifySearchResult(Object object, int type){
        for(int i = 0;i < this.mOnSearchResultListeners.size();i++){
            this.mOnSearchResultListeners.get(i).searchResult(object, type);
        }
    }

    public void notifySearchKeySet(String key){
        for(int i = 0;i < this.mOnSearchKeySetListeners.size();i++){
            this.mOnSearchKeySetListeners.get(i).setSearchKey(key);
        }
    }

    public void notifyRecentPlayListChange(List<Channel> channelList){
        for(int i = 0;i < this.mOnRecentPlayListeners.size();i++){
            this.mOnRecentPlayListeners.get(i).recentPlayChange(channelList);
        }
    }

    public void notifySearchHistoryListChange(List<String> searchHistoryList){
        for(int i = 0;i < this.mOnSearchHistoryListeners.size();i++){
            this.mOnSearchHistoryListeners.get(i).searchHistoryChange(searchHistoryList);
        }
    }

    public void notifyHideSoftKey(){
        for(int i = 0;i < this.mOnHideSoftKeyListeners.size();i++){
            this.mOnHideSoftKeyListeners.get(i).hideSoftKey();
        }
    }

    public void notifyUpdateDeviceName(String deviceName){
        for(int i = 0;i < this.mOnUpdateDeviceNameListeners.size();i++){
            this.mOnUpdateDeviceNameListeners.get(i).updateDeviceName(deviceName);
        }
    }

    /*设备在线状态*/
    public void notifyDeviceOnlineStatus(boolean status){
        for(int i = 0;i < this.mDeviceOnlineStatusListeners.size();i++){
            this.mDeviceOnlineStatusListeners.get(i).onlineStatus(status);
        }
    }
    /*设备在线状态*/

    public void notifyMyChannelStateChange(boolean isAdd){
        for(int i = 0;i < this.mOnMyChannelStateChangeListeners.size();i++){
            this.mOnMyChannelStateChangeListeners.get(i).myChannelStateChange(isAdd);
        }
    }

    public void notifySleepStateOrClearTimeChange(boolean isSleepState){
        for(int i = 0;i < this.mOnSleepStateChangeListeners.size();i++){
            this.mOnSleepStateChangeListeners.get(i).sleepStateOrClearTimeChange(isSleepState);
        }
    }

    public void notifyResetTimeChange(int time){
        for(int i = 0;i < this.mOnSleepStateChangeListeners.size();i++){
            this.mOnSleepStateChangeListeners.get(i).resetTimeChange(time);
        }
    }

    public void notifyDeviceModeChange(int deviceMode){
        for(int i = 0;i < this.mOnDeviceModeChangeListeners.size();i++){
            this.mOnDeviceModeChangeListeners.get(i).deviceModeChange(deviceMode);
        }
    }

    public void notifyUpdateAudioPlayerByBaiduDuration(){
        for(int i = 0;i < this.mOnUpdateAudioPlayerByBaiduDurationListeners.size();i++){
            this.mOnUpdateAudioPlayerByBaiduDurationListeners.get(i).updateAudioPlayerByBaiduDuration();
        }
    }

    public void notifyMainViewPagerCanScroll(boolean canScroll){
        for(int i = 0;i < this.mOnMainViewPagerListeners.size();i++){
            this.mOnMainViewPagerListeners.get(i).callScroll(canScroll);
        }
    }


    public void addBeingPlayList(Channel channel){
        Glog.i(TAG,"addBeingPlayList channel = "+channel);
        if(channel != null){
            this.mBeingPlayList.add(channel);
            this.notifyBeingPlayListChange();
            notifyMyLikeMusicStatusChange();
        }
        /*
        boolean isFind = false;
        Channel isPlayingChannel = null;
        Channel isSelectedChannel = null;
        for(int i = 0;i < this.mBeingPlayList.size();i++){
            Channel currChannel = this.mBeingPlayList.get(i);
            if(currChannel.equals(channel)){
                isFind = true;
            }
            if(currChannel.getIsPlaying()){
                isPlayingChannel = currChannel;
            }
            if(currChannel.getIsSelected()){
                isSelectedChannel = currChannel;
            }
        }
        if(!isFind) {
            this.mBeingPlayList.add(channel);
            if(isSelectedChannel != null && channel.getIsSelected()){
                isSelectedChannel.setIsSelected(false);
            }
            if(isPlayingChannel != null && channel.getIsPlaying()){
                isPlayingChannel.setIsPlaying(false);
            }
            this.notifyBeingPlayListChange();
        }
        */
    }

    public ArrayList<Channel> getBeingPlayList(){
        return this.mBeingPlayList;
    }

    public void setBeingPlayList(ArrayList<Channel> mBeingPlayList) {
        if (null != mBeingPlayList) {
            this.mBeingPlayList = mBeingPlayList;
        }
    }

    public List<SleepInfo> getSleepInfoList() {
        return mSleepInfoList;
    }

    public void setSleepInfoList(List<SleepInfo> mSleepInfoList) {
        this.mSleepInfoList = mSleepInfoList;
    }

    public void setRecentPlayList(ArrayList<Channel> recentPlatList) {
        if (null != recentPlatList) {
            this.recentPlayList = recentPlatList;
        }
    }

    public ArrayList<Channel> getRecentPlayList() {
        return recentPlayList;
    }

    public void addRecentPlay(Context mContext, Channel channel) {
        if (channel.getUri().contains("radio:") || channel.getUri().contains("audio:")) {
            return;
        }

        if (TextUtils.isEmpty(channel.getUri()) || TextUtils.isEmpty(channel.getName())) {
            return;
        }

        for (int i = recentPlayList.size() - 1; i >= 0; i--) {
            if (recentPlayList.get(i).getUri().equals(channel.getUri()) && recentPlayList.get(i).getName().equals(channel.getName())) {
                recentPlayList.remove(i);
            }
        }
        recentPlayList.add(0, channel);

        if (recentPlayList.size() > 10) {
            for (int i = 10; i < recentPlayList.size(); i++) {
                recentPlayList.remove(i);
            }
        }

//        ACacheUtil.putData(mContext, "recentPlayList", GsonUtil.channellistToString(recentPlayList));
        SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "recentPlayList", GsonUtil.channellistToString(recentPlayList));
    }

    public ArrayList<String> getSearchHistoryList() {
        return searchHistoryList;
    }

    public void setSearchHistoryList(ArrayList<String> searchHistoryList) {
        this.searchHistoryList = searchHistoryList;
    }

    public void addSearchHistory(Context mContext, String searchStr) {
        for (int i = searchHistoryList.size() - 1; i >= 0; i--) {
            if (searchHistoryList.get(i).toString().equals(searchStr)) {
                searchHistoryList.remove(i);
            }
        }
        searchHistoryList.add(0, searchStr);

        if (searchHistoryList.size() > 10) {
            for (int i = 10; i < searchHistoryList.size(); i++) {
                searchHistoryList.remove(i);
            }
        }
        SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "searchHistoryList", GsonUtil.searchListToString(searchHistoryList));
    }

    public void clearSearchHistory(Context mContext) {
        searchHistoryList.clear();
        SharePreferenceUtil.setSharePreferenceData(mContext, "Mantic", "searchHistoryList", "");
    }


    public void setCurrChannel(Channel channel){
        this.mCurrChannel = channel;
    }

    public Channel getCurrChannel(){
        return this.mCurrChannel;
    }

    public int getCurrChannelIndex(){
        int index = -1;
        if(this.mCurrChannel != null){
            for (int i = 0; i < mBeingPlayList.size(); i++) {
                if (!TextUtils.isEmpty(mBeingPlayList.get(i).getName()) && !TextUtils.isEmpty(mBeingPlayList.get(i).getUri()) && mBeingPlayList.get(i).getName().equals(mCurrChannel.getName()) &&
                        mBeingPlayList.get(i).getUri().equals(mCurrChannel.getUri())) {
                    return i;
                }
            }
            // 有可能有出现正在播放的歌曲不存在播放列表里面
            if (mBeingPlayList.size() > 0) {
                return 0;
            }
        } else {
            if (mBeingPlayList.size() > 0) {
                this.mCurrChannel = mBeingPlayList.get(0);
                return 0;
            }
        }
        return index;
    }

    public int getLastChannelIndex(){
        int index = -1;
        if(getBeingPlayList() != null){
            return getBeingPlayList().size();
        }
        return index;
    }

    public Channel getNextPlayChannel() {
        int currChannelIndex = getCurrChannelIndex();
        if (currChannelIndex != -1) {
            if (currChannelIndex < mBeingPlayList.size() - 1) {
                return getBeingPlayList().get(getCurrChannelIndex() + 1);
            } else {
                return getBeingPlayList().get(0);
            }
        }

        return null;
    }


    public Channel getLastPlayChannel() {
        if (mBeingPlayList != null && mBeingPlayList.size() > 0) {
            return mBeingPlayList.get(mBeingPlayList.size() - 1);
        }

        return null;
    }

    public boolean channelIsCurrent(Channel channel) {
        int currChannelIndex = getCurrChannelIndex();
        if (currChannelIndex != -1 && null != getCurrChannel()) {
            if (getCurrChannel().getName().equals(channel.getName()) && getCurrChannel().getUri().equals(channel.getUri())) {
                return true;
            }
        }

        return false;
    }

    public boolean channelIsInBeingPlayList(Channel channel){
        boolean result = false;
        if(this.mBeingPlayList != null){
            for (int i = 0; i < mBeingPlayList.size(); i++) {
                Glog.i(TAG, "channelIsInBeingPlayList: " + mBeingPlayList.get(i).getName());
                Channel cha = mBeingPlayList.get(i);
                if (TextUtils.isEmpty(cha.getName()) || TextUtils.isEmpty(cha.getUri())) {
                    return false;
                }
                if (cha.getName().equals(channel.getName()) && cha.getUri().equals(channel.getUri())) {
                    channel.setTlid(cha.getTlid());
                    return true;
                }
            }
        }
        return result;
    }

    public int channelPositionInBeingPlayList(Channel channel){
        if(this.mBeingPlayList != null){
            for (int i = 0; i < mBeingPlayList.size(); i++) {
                Channel cha = mBeingPlayList.get(i);
                if (cha.getName().equals(channel.getName()) && cha.getUri().equals(channel.getUri())) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void removeBeingPlayChannel(Channel channel){
        if(this.mBeingPlayList != null){
            for (int i = 0; i < mBeingPlayList.size(); i++) {
                Channel cha = mBeingPlayList.get(i);
                if (cha.getName().equals(channel.getName()) && cha.getUri().equals(channel.getUri())) {
                    mBeingPlayList.remove(i);
                }
            }
        }
    }

    public ArrayList<ChannelControlListener> getChannelControlListeners(){
        return this.mChannelControlListeners;
    }

    public boolean addChannelControlListener(ChannelControlListener listener){
        boolean result = false;
        int index = this.mChannelControlListeners.indexOf(listener);
        if(index < 0) {
            result = this.mChannelControlListeners.add(listener);
        }
        return result;
    }

    public boolean removeChannelControlListener(ChannelControlListener listener){
        return this.mChannelControlListeners.remove(listener);
    }

    public ArrayList<ManticDevice> getManticDeviceList(){
        return this.mManticDeviceList;
    }

    public ConnMode getConnMode(Context mContext){
        ConnMode connMode = new ConnMode();
        //虚拟数据start
        connMode.setConnIcon(BitmapFactory.decodeResource(mContext.getResources(),R.drawable.conn_mode_bluetooth));
        connMode.setConnName(mContext.getString(R.string.conn_mode_bluetooth));
        connMode.setConnType(ConnMode.CONN_TYPE_BLUETOOTH);
        connMode.setConnClient("华为P9");
        //虚拟数据end
        return connMode;
    }

    public class ConnMode{
        public static final int CONN_TYPE_BLUETOOTH = 0;
        public static final int CONN_TYPE_WLAN = 1;
        private Bitmap connIcon;
        private int connType;
        private String connName;
        private String connClient;
        public void setConnIcon(Bitmap icon){
            this.connIcon = icon;
        }

        public void setConnType(int type){
            this.connType = type;
        }

        public void setConnName(String name){
            this.connName = name;
        }

        public void setConnClient(String client){
            this.connClient = client;
        }

        public Bitmap getConnIcon(){
            return this.connIcon;
        }

        public int getConnType(){
            return this.connType;
        }

        public String getConnName(){
            return this.connName;
        }

        public String getConnClient(){
            return this.connClient;
        }
    }



    public class ManticDevice{
        public static final int STATE_IS_OFF = 0;
        public static final int STATE_IS_ON = 1;
        public static final int STATE_IS_SELECT = 2;
        private int state;
        private String name;

        public void setState(int sta){
            this.state = sta;
        }

        public int getState(){
            return this.state;
        }

        public void setName(String na){
            this.name = na;
        }

        public String getName(){
            return this.name;
        }
    }
}
