package com.mantic.control.fragment;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.adapter.MyChannelAdapter;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mopidy.bean.MopidyRsTrackBean;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.MyChannelListRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelPlay;
import com.mantic.control.api.mylike.bean.MyLikeGetRsBean;
import com.mantic.control.api.mylike.bean.Track;
import com.mantic.control.cache.ACacheUtil;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.decoration.MyChannelItemDecoration;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.manager.MyLikeManager;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.GsonUtil;
import com.mantic.control.utils.NetworkUtils;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.utils.Util;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 17-4-10.
 */
public class MyChannelFragment extends BaseFragment implements View.OnClickListener, MyChannelAdapter.OnItemClickListener,
        DataFactory.MyChannelListListener, DataFactory.OnMyLikeMusicListener,
        DataFactory.OnUpdateDeviceNameListener, DataFactory.RefreshMyChannelListListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "MyChannelFragment";
    private SwipeRefreshLayout srl_my_channel;
    private LinearLayout ll_my_channel_header;
    private RecyclerView mMyChannelList;
    private MyChannelAdapter mMyChannelAdapter;
    private TextView tv_my_channel_count;
    private TextView tv_my_device_name;
    private ImageButton btn_my_channel_management;
    private ImageButton btn_my_channel_add;
    private RelativeLayout rl_my_channel_like_music;
    private TextView tv_like_music_count;
    private Call<MyChannelListRsBean> getListCall;
    private MyChannelOperatorServiceApi mMyChannelOperatorServiceApi;
    private MopidyServiceApi mpServiceApi;
    private boolean firstonLazyLoad = true;

    private ArrayList<MyChannel> myChannels = new ArrayList<MyChannel>();
    private ArrayList<MyChannel> definitionMyChannels = new ArrayList<MyChannel>();

    @Override
    public void onStart() {
        super.onStart();
        this.mDataFactory.registerMyChannelListListener(this);
        this.mDataFactory.registerUpdateDeviceNameListener(this);
        this.mDataFactory.registerRefreshMyChannelListListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        this.mDataFactory.unregisterMyChannelListListener(this);
        this.mDataFactory.unregisterUpdateDeviceNameListener(this);
        this.mDataFactory.unregisterRefreshMyChannelListListener(this);
    }


    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        mMyChannelAdapter = new MyChannelAdapter(getContext(), ll_my_channel_header);
        mMyChannelAdapter.setmOnItemClickListener(this);
        mMyChannelList.setAdapter(mMyChannelAdapter);
        mMyChannelList.setLayoutManager(new LinearLayoutManager(getContext()));
        mMyChannelList.addItemDecoration(new MyChannelItemDecoration(getContext()));
        mMyChannelList.setNestedScrollingEnabled(false);

//        String myChannelListStr = ACacheUtil.getData(getContext(), "MyChannelList");
        String myChannelListStr = SharePreferenceUtil.getSharePreferenceData(mContext, "Mantic", "MyChannelList");
        if (!TextUtils.isEmpty(myChannelListStr)) {
            ArrayList<MyChannel> myChannels = GsonUtil.stringToMyChannelList(myChannelListStr);
            ArrayList<MyChannel> definitionMyChannels = new ArrayList<MyChannel>();
            mDataFactory.setMyChannelList(myChannels);
            mMyChannelAdapter.updateMyChannelList(myChannels);

            for (int i = 0; i < myChannels.size(); i++) {
                if (myChannels.get(i).isSelfDefinition()) {
                    definitionMyChannels.add(myChannels.get(i));
                }
            }
            mDataFactory.setDefinitionMyChannelList(definitionMyChannels);
        }

        tv_my_channel_count.setText(String.format(getString(R.string.channel_count), mDataFactory.getMyChannelSize()));
        tv_my_device_name.setText(SharePreferenceUtil.getDeviceName(mContext));
        tv_like_music_count.setText(String.format(getString(R.string.music_count), mDataFactory.getMyLikeMusicCount()));
        mDataFactory.registerMyLikeMusiceListListener(this);
        onLazyLoad();
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
        initHeaderView();
        srl_my_channel = (SwipeRefreshLayout) view.findViewById(R.id.srl_my_channel);
        this.mMyChannelList = (RecyclerView) view.findViewById(R.id.my_channel_list);
        srl_my_channel.setEnabled(false);
        srl_my_channel.setOnRefreshListener(this);
    }

    private void initHeaderView() {
        ll_my_channel_header = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.my_channel_header, null, false);
        tv_my_channel_count = (TextView) ll_my_channel_header.findViewById(R.id.tv_my_channel_count);
        tv_my_device_name = (TextView) ll_my_channel_header.findViewById(R.id.tv_my_device_name);
        btn_my_channel_management = (ImageButton) ll_my_channel_header.findViewById(R.id.btn_my_channel_management);
        btn_my_channel_add = (ImageButton) ll_my_channel_header.findViewById(R.id.btn_my_channel_add);

        rl_my_channel_like_music = (RelativeLayout) ll_my_channel_header.findViewById(R.id.rl_my_channel_like_music);
        tv_like_music_count = (TextView) ll_my_channel_header.findViewById(R.id.tv_like_music_count);
        btn_my_channel_management.setOnClickListener(this);
        rl_my_channel_like_music.setOnClickListener(this);
        btn_my_channel_add.setOnClickListener(this);
    }

    @Override
    protected void onLazyLoad() {
        Glog.i(TAG,"onLazyLoad: ... ");
        super.onLazyLoad();
        if (firstonLazyLoad){
            firstonLazyLoad = false;
            getMyChannelList();
            getMyLikeList();
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_my_channel;
    }


    @Override
    public void onDestroy() {
        mDataFactory.unregisterMyLikeMusiceListListener(this);
        super.onDestroy();
    }


    private void getMyChannelList() {
        mMyChannelOperatorServiceApi = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class);
        getListCall = mMyChannelOperatorServiceApi.postMyChannelListQuest(MopidyTools.getHeaders(),Util.createGetListRqBean(mContext));
        MyChannelManager.getMyChannelList(getListCall, new Callback<MyChannelListRsBean>() {
            @Override
            public void onResponse(Call<MyChannelListRsBean> call, Response<MyChannelListRsBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    ArrayList<MyChannelPlay> channelPlays = response.body().result;
                    myChannels = new ArrayList<MyChannel>();
                    definitionMyChannels = new ArrayList<MyChannel>();
                    fmListUri = new ArrayList<String>();
                    if (null != channelPlays && channelPlays.size() > 0) {
                        for (int i = 0; i < channelPlays.size(); i++) {
                            MyChannel myChannel = new MyChannel();
                            MyChannelPlay myChannelPlay = channelPlays.get(i);
                            String mantic_album_uri = myChannelPlay.getMantic_album_uri();
                            Log.e(TAG, "huqiangonResponse: "+mantic_album_uri );
                            myChannel.setPlayCount(myChannelPlay.getMantic_play_count());
                            myChannel.setUrl(myChannelPlay.getUri());
                            myChannel.setChannelCoverUrl(myChannelPlay.getMantic_image());
                            if (null == myChannelPlay.getMantic_image()) {
                                myChannel.setChannelCoverUrl("");
                                myChannel.setChannelId("");
                            } else {
                                myChannel.setChannelCoverUrl(myChannelPlay.getMantic_image());
                                myChannel.setChannelId(myChannelPlay.getMantic_image());
                            }
                            myChannel.setChannelName(myChannelPlay.getName());
                            myChannel.setSelfDefinition(myChannelPlay.getMantic_type() == 101 ? true : false);
                            myChannel.setChannelType(myChannelPlay.getMantic_type());
                            myChannel.setChannelIntro(myChannelPlay.getMantic_describe());
                            if (myChannel.getChannelType() == 2){
                                myChannel.setmTotalCount(myChannelPlay.getMantic_num_tracks());
                                fmListUri.add(mantic_album_uri);
                            }else {
                                myChannel.setmTotalCount(myChannelPlay.getMantic_num_tracks());
                            }

                            myChannel.setMainId(myChannelPlay.getMantic_last_modified());
                            myChannel.setMusicServiceId(mantic_album_uri.substring(0, mantic_album_uri.indexOf(":")));
                            mantic_album_uri = mantic_album_uri.substring(mantic_album_uri.indexOf(":") + 1);
                            myChannel.setAlbumId(mantic_album_uri.substring(mantic_album_uri.indexOf(":") + 1));
                            if (myChannel.isSelfDefinition()) {
                                definitionMyChannels.add(myChannel);
                            }
                            myChannels.add(myChannel);
                        }
                    }

                    mDataFactory.setMyChannelList(myChannels);
                    mMyChannelAdapter.updateMyChannelList(myChannels);

                    mDataFactory.setDefinitionMyChannelList(definitionMyChannels);
                    mDataFactory.notifyMyChannelListChanged();
                    mDataFactory.notifyMyLikeMusicStatusChange();

                    getCurrentProgramFm(fmListUri);
                    if (srl_my_channel.isRefreshing()) {
                        srl_my_channel.setRefreshing(false);
                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.refresh_data_success), true);
                    }
                } else {
                    if (srl_my_channel.isRefreshing()) {
                        srl_my_channel.setRefreshing(false);
                        if (!NetworkUtils.isAvailableByPing(mContext)) {
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.network_suck), false);
                        } else {
                            mDataFactory.notifyOperatorResult(mContext.getString(R.string.net_failed), false);
                        }
                    }

                }
            }

            @Override
            public void onFailure(Call<MyChannelListRsBean> call, Throwable t) {
                if (srl_my_channel.isRefreshing()) {
                    srl_my_channel.setRefreshing(false);
                    if (!NetworkUtils.isAvailableByPing(mContext)) {
                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.network_suck), false);
                    } else {
                        mDataFactory.notifyOperatorResult(mContext.getString(R.string.net_failed), false);
                    }
                }
            }
        });
    }


    private void getMyLikeList() {
        MyLikeManager.getInstance().getMyLike(new Callback<MyLikeGetRsBean>() {
            @Override
            public void onResponse(Call<MyLikeGetRsBean> call, Response<MyLikeGetRsBean> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    if (null != response.body() && null != response.body().getResult()) {
                        if (null == response.body().getResult().getTracks() || response.body().getResult().getTracks().size() <= 0) {
                            return;
                        }
                        String uri = response.body().getResult().getUri();
                        Glog.i(TAG, "getMyLikeList: " + uri);
                        mDataFactory.setMyLikeUri(uri);
                        List<Track> tracks = response.body().getResult().getTracks();
                        if (tracks == null || tracks.size() <= 0) {
                            return;
                        }
                        ArrayList<Channel> myLikeChannels = new ArrayList<Channel>();
                        for (int i = 0; i < tracks.size(); i++) {
                            Track track = tracks.get(i);
                            Channel channel = new Channel();
                            if (null != track.getMantic_artists_name()) {
                                String singer = "";
                                for (int z = 0; z < track.getMantic_artists_name().size(); z++) {
                                    if (z != track.getMantic_artists_name().size() - 1) {
                                        singer = singer + track.getMantic_artists_name().get(z).toString() + "，";
                                    } else {
                                        singer = singer + track.getMantic_artists_name().get(z).toString();
                                    }
                                }
                                channel.setSinger(singer);
                            }
                            channel.setDuration(track.getLength());
                            channel.setIconUrl(track.getMantic_image());
                            channel.setPlayUrl(track.getMantic_real_url());
                            channel.setName(track.getName());
                            channel.setUri(track.getUri());
                            channel.setMantic_album_name(track.getMantic_album_name());
                            channel.setMantic_album_uri(track.getMantic_album_uri());
                            myLikeChannels.add(channel);
                        }

                        mDataFactory.setMyLikeMusicList(myLikeChannels);
                        mDataFactory.notifyMyLikeMusicListChange();
                        mDataFactory.notifyMyLikeMusicStatusChange();
                    }

                } else {
                    if (!NetworkUtils.isAvailableByPing(mContext)) {
                        mDataFactory.notifyOperatorResult(getString(R.string.network_suck), false);
                    } else {
                        mDataFactory.notifyOperatorResult(getString(R.string.service_problem), false);
                    }
                }
            }

            @Override
            public void onFailure(Call<MyLikeGetRsBean> call, Throwable t) {
                if (!NetworkUtils.isAvailableByPing(mContext)) {
                    mDataFactory.notifyOperatorResult(getString(R.string.network_suck), false);
                } else {
                    mDataFactory.notifyOperatorResult(getString(R.string.service_problem), false);
                }
            }
        }, mContext);
    }

    @Override
    public void onRefresh() {
        getMyChannelList();
        getMyLikeList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_my_channel_management:
                if (null != mDataFactory.getMyChannelList() && mDataFactory.getMyChannelList().size() > 0) {
                    Bundle bundle=new Bundle();
                    bundle.putString("addchannel","Mychannel");
                    ChannelManagementFragment channelManagementFragment = new ChannelManagementFragment();
                    channelManagementFragment.setArguments(bundle);
                    if(getActivity() instanceof FragmentEntrust){
                        ((FragmentEntrust) getActivity()).pushFragment(channelManagementFragment,ChannelManagementFragment.class.getName());
                    }
                } else {
                    ToastUtils.showShortSafe(getString(R.string.no_mychannel));
                }
                break;
            case R.id.btn_my_channel_add:
                MyChannelAddFragment myChannelAddFragment = new MyChannelAddFragment();
                if(mActivity instanceof FragmentEntrust){
                    ((FragmentEntrust) getActivity()).pushFragment(myChannelAddFragment, MyChannelAddFragment.class.getName());
                }
                break;
            case R.id.rl_my_channel_like_music:
                if (mDataFactory.getMyLikeMusicList().size() > 0) {
                    MyLikeMusicFragment myLikeMusicFragment = new MyLikeMusicFragment();
                    if( getActivity() instanceof FragmentEntrust){
                        ((FragmentEntrust) getActivity()).pushFragment(myLikeMusicFragment,MyLikeMusicFragment.class.getName());
                    }
                } else {
                    ToastUtils.showShortSafe(R.string.no_favorite_songs);
                }
                break;
            default:
                break;
        }
    }


    @Override
    public void updateDeviceName(String deviceName) {
        tv_my_device_name.setText(deviceName);
    }

    @Override
    public void callback(ArrayList<MyChannel> myChannels) {
        mMyChannelAdapter.updateMyChannelList(myChannels);
        tv_my_channel_count.setText(String.format(getString(R.string.channel_count), mDataFactory.getMyChannelSize()));

        ACacheUtil.putData(getContext(), "MyChannelList", GsonUtil.myChannelListToString(myChannels));
    }

    @Override
    public void changeMyLikeMusicCount() {
        Glog.i(TAG, "changeMyLikeMusicCount: " + mDataFactory.getMyLikeMusicCount());
        tv_like_music_count.setText(String.format(getString(R.string.music_count), mDataFactory.getMyLikeMusicCount()));
    }

    @Override
    public void refreshMyChannelList() {
        if (firstonLazyLoad) {
            firstonLazyLoad = false;
            getMyChannelList();
        } else {
            getMyChannelList();
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        Glog.i(TAG, "onItemClick: position= " + position);
        MyChannel myChannel = mDataFactory.getMyChannelList().get(position);
        ChannelDetailsFragment cdFragment = new ChannelDetailsFragment();
        String channelName = myChannel.getChannelName();
        String musicServiceId = myChannel.getMusicServiceId();
        String channnelId = myChannel.getChannelId();
        Bundle bundle = new Bundle();
        bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID,musicServiceId);
        bundle.putString(ChannelDetailsFragment.CHANNEL_ID,channnelId);
        bundle.putString(ChannelDetailsFragment.CHANNEL_NAME,channelName);
        bundle.putString(ChannelDetailsFragment.CHANNEL_FROM, "MyChannelFragment");
        bundle.putString(ChannelDetailsFragment.CHANNEL_INTRO, myChannel.getChannelIntro());
        bundle.putInt(MyMusicService.PRE_DATA_TYPE, 3);
        bundle.putString(ChannelDetailsFragment.ALBUM_ID, myChannel.getAlbumId());
        bundle.putString(ChannelDetailsFragment.MAIN_ID, myChannel.getMainId());
        cdFragment.setArguments(bundle);
        if(getActivity() instanceof FragmentEntrust){
            ((FragmentEntrust) getActivity()).pushFragment(cdFragment,musicServiceId+channnelId+channelName);
        }
    }

    private List<String> fmListUri = new ArrayList<String>();

    public void getCurrentProgramFm(final List<String> uris){
        Glog.i(TAG,"getCurrentProgramFm: uri: " + uris);
        for (int i = 0; i < uris.size(); i++){
            final int index = i;
            String uri = uris.get(i);
            uri = uri.substring(uri.indexOf(":") + 1);
            final String albumId = uri.substring(uri.indexOf(":") + 1);
            RequestBody body = MopidyTools.createRequestPageBrowse(albumId,0);
            Call<MopidyRsTrackBean> call = mpServiceApi.postMopidyTrackPageQuest(MopidyTools.getHeaders(),body);
            call.enqueue(new Callback<MopidyRsTrackBean>() {
                @Override
                public void onResponse(Call<MopidyRsTrackBean> call, Response<MopidyRsTrackBean> response) {
                    List<MopidyRsTrackBean.Result> resultList = new ArrayList<MopidyRsTrackBean.Result>();
                    resultList = response.body().results;
                    if (resultList != null){
                        for (int i = 0; i < resultList.size(); i++){
                            MopidyRsTrackBean.Result result = resultList.get(i);
                            if (TimeUtil.iscurFmPeriods(result.mantic_radio_length)){
                                for (MyChannel myChannel:myChannels){
                                    if (myChannel.getAlbumId().equals(albumId)){
                                        myChannel.setChannelIntro(result.name);
                                    }
                                }

                                Glog.i(TAG,"result.mantic_radio_length: " + result.mantic_radio_length);
                            }
                        }
                    }

                    if (index == uris.size()-1){
                        refreshChannedList();
                    }
                }

                @Override
                public void onFailure(Call<MopidyRsTrackBean> call, Throwable t) {

                }
            });
        }
    }

    private void refreshChannedList(){
        mDataFactory.setMyChannelList(myChannels);
        mMyChannelAdapter.updateMyChannelList(myChannels);

        mDataFactory.setDefinitionMyChannelList(definitionMyChannels);
        mDataFactory.notifyMyChannelListChanged();
    }
}
