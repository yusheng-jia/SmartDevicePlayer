package com.mantic.control.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.api.mopidy.MopidyRetrofit;
import com.mantic.control.api.mopidy.MopidyServiceApi;
import com.mantic.control.api.mopidy.MopidyTools;
import com.mantic.control.api.mopidy.bean.MopidyRsTrackBean;
import com.mantic.control.api.mychannel.MyChannelOperatorRetrofit;
import com.mantic.control.api.mychannel.MyChannelOperatorServiceApi;
import com.mantic.control.api.mychannel.bean.MyChannelAddRsBean;
import com.mantic.control.api.mychannel.bean.MyChannelDeleteRsBean;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.data.MyChannel;
import com.mantic.control.fragment.ChannelDetailsFragment;
import com.mantic.control.fragment.FragmentEntrust;
import com.mantic.control.fragment.MusicServiceSubItemFragment;
import com.mantic.control.manager.MyChannelManager;
import com.mantic.control.musicservice.IMusicServiceSubItem;
import com.mantic.control.musicservice.IMusicServiceSubItemAlbum;
import com.mantic.control.musicservice.IMusicServiceTrackContent;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.TimeUtil;
import com.mantic.control.utils.Util;

import java.util.ArrayList;
import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Jia on 2017/6/3.
 */

public class MusicServiceSubItemListAdapter extends RecyclerView.Adapter{
    private static final String TAG = "MusicServiceSub";
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private static final int TYPE_LOAD_COMPLETE = 2;
    public boolean isHaveStatesView = true;

    private Context ctx;
    private Activity mActivity;
    private ArrayList<IMusicServiceSubItem> iMusicServiceSubItemList = new ArrayList<IMusicServiceSubItem>();
    protected DataFactory mDataFactory;

    private int currentFooterType = TYPE_LOAD_COMPLETE;
    private MopidyServiceApi mpServiceApi;
    private MyChannelOperatorServiceApi mMyChannelOperatorServiceApi;
    private Call<MyChannelAddRsBean> addCall;
    private Call<MyChannelDeleteRsBean> deleteCall;

    private  MyChannel mMyChannel;
    private List<Channel> channels = new ArrayList<Channel>();
    private String mServiceId = "";

    public MusicServiceSubItemListAdapter(Context context,Activity activity, String mServiceId){
        this.ctx = context;
        mActivity = activity;
        this.mServiceId = mServiceId;
        mpServiceApi = MopidyRetrofit.getInstance().create(MopidyServiceApi.class);
        mDataFactory = DataFactory.newInstance(ctx);
        mMyChannelOperatorServiceApi = MyChannelOperatorRetrofit.getInstance().create(MyChannelOperatorServiceApi.class);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM){
            return new MusicServiceSubItemListViewHolder(LayoutInflater.from(this.ctx).inflate(R.layout.music_service_subitem_listitem,parent,false));
        }else if(viewType == TYPE_FOOTER){
            return new FootViewHolder(LayoutInflater.from(this.ctx).inflate(R.layout.item_foot, parent, false));
        }else if (viewType == TYPE_LOAD_COMPLETE){
            return new FootViewNoHolder(LayoutInflater.from(this.ctx).inflate(R.layout.item_foot_empty, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MusicServiceSubItemListViewHolder){
            ((MusicServiceSubItemListViewHolder)holder).showItem(position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if ((position + 1) == getItemCount()) {
            return currentFooterType;
        } else {
            return TYPE_ITEM;
        }
    }

    public void onLoading(){
        currentFooterType = TYPE_FOOTER;
        isHaveStatesView = true;
    }

    public void onLoadComplete(){
        currentFooterType = TYPE_LOAD_COMPLETE;
        isHaveStatesView = false;
//        notifyItemChanged(getItemCount());
    }

    @Override
    public int getItemCount() {
        return iMusicServiceSubItemList.size() + 1;
    }

    public void setMusicServiceSubItemList(ArrayList<IMusicServiceSubItem> itemList){
        this.iMusicServiceSubItemList = itemList;
    }

    public void subItemClick(IMusicServiceSubItem item){
        Bundle bundle = item.gotoNext();
        int nextDataType = item.getNextDataType();
        String nextDataId = item.getNextDataId();
        Fragment fragment = null;
        if(nextDataType == MyMusicService.TYPE_DATA_LIST){
            fragment = new MusicServiceSubItemFragment();
            bundle.putString(MusicServiceSubItemFragment.SUB_ITEM_TITLE,item.getItemText());
        }else if(nextDataType == MyMusicService.TYPE_DATA_ALBUM){
            fragment = new ChannelDetailsFragment();
            if(item instanceof IMusicServiceSubItemAlbum){
//                if (((IMusicServiceSubItemAlbum) item).getType() == 2){
//                    ToastUtils.showShortSafe(R.string.fm_developing_toast);
//                    return;
//                }

                if (((IMusicServiceSubItemAlbum) item).getType() == 2){//FM
                    fmChannelName = ((IMusicServiceSubItemAlbum) item).getAlbumTitle();
                    fmChannelAlbumId = ((IMusicServiceSubItemAlbum) item).getAlbumId();
                    fmChannelCover = ((IMusicServiceSubItemAlbum) item).getCoverUrl();
                    Glog.i("MusicService","fmChannelAlbumId: " + fmChannelAlbumId);
                    RefreshTrackList(fmChannelAlbumId);
                    return;
                }

                bundle.putString(ChannelDetailsFragment.CHANNEL_NAME,((IMusicServiceSubItemAlbum) item).getAlbumTitle());
                bundle.putString(ChannelDetailsFragment.CHANNEL_TAGS,((IMusicServiceSubItemAlbum) item).getAlbumTags());
                bundle.putString(ChannelDetailsFragment.CHANNEL_COVER_URL,((IMusicServiceSubItemAlbum) item).getCoverUrl());
                bundle.putString(ChannelDetailsFragment.CHANNEL_INTRO,((IMusicServiceSubItemAlbum) item).getAlbumIntro());
                bundle.putLong(ChannelDetailsFragment.CHANNEL_TOTAL_COUNT,((IMusicServiceSubItemAlbum) item).getTotalCount());
                bundle.putLong(ChannelDetailsFragment.CHANNEL_UPDATEAT,((IMusicServiceSubItemAlbum) item).getUpdateAt());
                bundle.putString(ChannelDetailsFragment.CHALLEL_SINGER,((IMusicServiceSubItemAlbum) item).getSinger());
                bundle.putString(ChannelDetailsFragment.CHANNEL_ID, ((IMusicServiceSubItemAlbum) item).getCoverUrl());
                bundle.putString(ChannelDetailsFragment.ALBUM_ID, ((IMusicServiceSubItemAlbum) item).getAlbumId());
                bundle.putString(ChannelDetailsFragment.MAIN_ID, ((IMusicServiceSubItemAlbum) item).getMainId());
                bundle.putInt(ChannelDetailsFragment.CHANNEL_TYPE, ((IMusicServiceSubItemAlbum) item).getType());
                bundle.putString(ChannelDetailsFragment.CHANNEL_PLAY_COUNT, ((IMusicServiceSubItemAlbum) item).getPlayCount());
            }
        }
        if(fragment != null){
            fragment.setArguments(bundle);
            if (ctx instanceof FragmentEntrust) {
                ((FragmentEntrust) ctx).pushFragment(fragment, nextDataId);
            }
        }
    }

    private class MusicServiceSubItemListViewHolder extends RecyclerView.ViewHolder{
        private View curr_item_view;
        private ImageView music_service_subitem_listitem_icon;
        private TextView music_service_subitem_listitem_desc;
        private TextView music_service_subitem_audience;
        private ImageButton music_service_subitem_goto_next_btn;

        public MusicServiceSubItemListViewHolder(View itemView) {
            super(itemView);
            this.curr_item_view = itemView;
            this.music_service_subitem_listitem_icon = (ImageView) itemView.findViewById(R.id.music_service_subitem_listitem_icon);
            this.music_service_subitem_listitem_desc = (TextView) itemView.findViewById(R.id.music_service_subitem_listitem_desc);
            this.music_service_subitem_audience = (TextView) itemView.findViewById(R.id.music_service_subitem_audience);
            this.music_service_subitem_goto_next_btn = (ImageButton) itemView.findViewById(R.id.music_service_subitem_goto_next_btn);
        }

        public void showItem(int position){
            final IMusicServiceSubItem subItem = iMusicServiceSubItemList.get(position);
            String iconUrl = subItem.getItemIconUrl();

            if(iconUrl != null && !iconUrl.isEmpty()) {
                if (subItem.getIconType() == 0){
                    if (!TextUtils.isEmpty(subItem.getNextDataId()) && subItem.getNextDataId().contains("baidu") && !TextUtils.isEmpty(((IMusicServiceSubItemAlbum) subItem).getAlbumIntro()) && ((IMusicServiceSubItemAlbum) subItem).getAlbumIntro().contains("热门歌手")) {
                        GlideImgManager.glideLoaderCircle(ctx, iconUrl, R.drawable.default_singer_icon,
                                R.drawable.default_singer_icon, music_service_subitem_listitem_icon);
                    } else {
                        GlideImgManager.glideLoaderCircle(ctx, iconUrl, R.drawable.fragment_channel_detail_cover,
                                R.drawable.fragment_channel_detail_cover, music_service_subitem_listitem_icon);
                    }
                }else if (subItem.getIconType() == 1){
                    music_service_subitem_listitem_icon.setImageDrawable(ctx.getResources().getDrawable(Integer.parseInt(iconUrl)));
                }
            }else{
                music_service_subitem_listitem_icon.setImageDrawable(ctx.getResources().getDrawable(R.drawable.audio_bottom_bar_album_cover)); //临时修改，后边根据if 传入更换不同图标
            }

            music_service_subitem_listitem_desc.setText(subItem.getItemText());
            if (subItem.getNextDataType() == MyMusicService.TYPE_DATA_ALBUM && !TextUtils.isEmpty(((IMusicServiceSubItemAlbum) subItem).getPlayCount()) && !"0".equals(((IMusicServiceSubItemAlbum) subItem).getPlayCount())) {
                music_service_subitem_audience.setText(String.format(ctx.getString(R.string.album_fm_playcount), ((IMusicServiceSubItemAlbum) subItem).getPlayCount()));
                music_service_subitem_audience.setVisibility(View.VISIBLE);
            } else {
                music_service_subitem_audience.setVisibility(View.GONE);
            }

            if (subItem.getNextDataType() == MyMusicService.TYPE_DATA_ALBUM) {
                music_service_subitem_goto_next_btn.setVisibility(View.VISIBLE);

                if(subItem instanceof IMusicServiceSubItemAlbum){
                    if (null != mDataFactory.getMyChannelFrom(mServiceId, ((IMusicServiceSubItemAlbum) subItem).getCoverUrl(), ((IMusicServiceSubItemAlbum) subItem).getAlbumTitle())) {
                        music_service_subitem_goto_next_btn.setImageResource(R.drawable.audio_player_fm_love_btn_pre);
                    } else {
                        music_service_subitem_goto_next_btn.setImageResource(R.drawable.audio_player_fm_love_btn_nor);
                    }
                }
            } else {
                music_service_subitem_goto_next_btn.setVisibility(View.GONE);
            }


            this.curr_item_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    subItemClick(subItem);
                }
            });

            this.music_service_subitem_goto_next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == mDataFactory.getMyChannelFrom(mServiceId, ((IMusicServiceSubItemAlbum) subItem).getCoverUrl(),
                            ((IMusicServiceSubItemAlbum) subItem).getAlbumTitle())) {//不存在频道
                        if (null != deleteCall) {
                            deleteCall.cancel();
                        }
                        mMyChannel = new MyChannel();
                        mMyChannel.setChannelCoverUrl(((IMusicServiceSubItemAlbum) subItem).getCoverUrl());
                        mMyChannel.setChannelName(((IMusicServiceSubItemAlbum) subItem).getAlbumTitle());
                        mMyChannel.setChannelTags(((IMusicServiceSubItemAlbum) subItem).getAlbumTags());
                        mMyChannel.setChannelIntro(((IMusicServiceSubItemAlbum) subItem).getAlbumIntro());
                        mMyChannel.setmTotalCount(((IMusicServiceSubItemAlbum) subItem).getTotalCount());
                        mMyChannel.setmUpdateAt(((IMusicServiceSubItemAlbum) subItem).getUpdateAt());
                        mMyChannel.setSingerName(((IMusicServiceSubItemAlbum) subItem).getSinger());
                        mMyChannel.setChannelId(((IMusicServiceSubItemAlbum) subItem).getCoverUrl());
                        mMyChannel.setAlbumId(((IMusicServiceSubItemAlbum) subItem).getAlbumId());
                        if (((IMusicServiceSubItemAlbum) subItem).getType() == 2) {
                            mMyChannel.setChannelType(2);//0 1 2 音乐 电台 广播
                        }else {
                            mMyChannel.setChannelType(3);//0 1 2 音乐 电台 广播
                        }
                        mMyChannel.setPlayCount(((IMusicServiceSubItemAlbum) subItem).getPlayCount()); //收听总数
                        mMyChannel.setMainId(((IMusicServiceSubItemAlbum) subItem).getMainId());
                        mMyChannel.setMusicServiceId(mServiceId);
                        addCall = mMyChannelOperatorServiceApi.postMyChannelAddQuest(MopidyTools.getHeaders(),Util.createAddRqBean(mMyChannel, ctx));

                        MyChannelManager.addMyChannel(addCall, new Callback<MyChannelAddRsBean>() {
                            @Override
                            public void onResponse(Call<MyChannelAddRsBean> call, Response<MyChannelAddRsBean> response) {
                                if (response.isSuccessful() && null == response.errorBody()) {
                                    mDataFactory.notifyOperatorResult(ctx.getString(R.string.success_add_this_channel), true);
                                    music_service_subitem_goto_next_btn.setImageResource(R.drawable.audio_player_fm_love_btn_pre);
                                    mMyChannel.setUrl(response.body().result.uri);
                                    mDataFactory.addMyChannel(mMyChannel);
                                    mDataFactory.notifyMyLikeMusicStatusChange();
                                    if (mMyChannel.getChannelType() == 2) {//如果增加的是广播，刷新一次我的频道,用于显示正在直播的内容
                                        mDataFactory.notifyMyChannelListRefresh();
                                    }
                                } else {
                                    mDataFactory.notifyOperatorResult(ctx.getString(R.string.failed_add_this_channel), false);
                                }
                            }

                            @Override
                            public void onFailure(Call<MyChannelAddRsBean> call, Throwable t) {
                                mDataFactory.notifyOperatorResult(ctx.getString(R.string.failed_add_this_channel), false);
                            }
                        }, mMyChannel);
                    } else {
                        if (null != addCall) {
                            addCall.cancel();
                        }

                        mMyChannel = mDataFactory.getMyChannelFrom(mServiceId, ((IMusicServiceSubItemAlbum) subItem).getCoverUrl(),
                                ((IMusicServiceSubItemAlbum) subItem).getAlbumTitle());

                        if (getChannelIndex() != -1) {
                            deleteCall = mMyChannelOperatorServiceApi.postMyChannelDeleteQuest(MopidyTools.getHeaders(),Util.createDeleteRqBean(mMyChannel, ctx));
                            MyChannelManager.getInstance().deleteMyChannel(deleteCall, new Callback<MyChannelDeleteRsBean>() {
                                @Override
                                public void onResponse(Call<MyChannelDeleteRsBean> call, Response<MyChannelDeleteRsBean> response) {
                                    if (response.isSuccessful() && null == response.errorBody()) {
                                        music_service_subitem_goto_next_btn.setImageResource(R.drawable.audio_player_fm_love_btn_nor);
                                        mDataFactory.notifyOperatorResult(ctx.getString(R.string.success_delete_this_channel), true);
                                        mDataFactory.removeMyChannel(mMyChannel);
                                        mDataFactory.removeDefinnitionMyChannel(ctx, mMyChannel);
                                        mDataFactory.notifyMyLikeMusicStatusChange();
                                    } else {
                                        mDataFactory.notifyOperatorResult(ctx.getString(R.string.failed_delete_this_channel), false);
                                    }
                                }

                                @Override
                                public void onFailure(Call<MyChannelDeleteRsBean> call, Throwable t) {
                                    mDataFactory.notifyOperatorResult(ctx.getString(R.string.failed_delete_this_channel), false);
                                }
                            }, mMyChannel);
                        }
                    }
                }
            });
        }
    }


    private int getChannelIndex() {
        ArrayList<MyChannel> list = mDataFactory.getMyChannelList();
        for (int i = 0; i < list.size(); i++) {
            if (mMyChannel.getChannelCoverUrl().equals(list.get(i).getChannelCoverUrl())) {
                return i;
            }
        }
        return -1;
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {

        public FootViewHolder(View view) {
            super(view);
            view.setVisibility(View.GONE);
        }
    }

    private class FootViewNoHolder extends RecyclerView.ViewHolder {

        public FootViewNoHolder(View view) {
            super(view);
            view.setVisibility(View.GONE);
        }
    }


    public void RefreshTrackList(String uri) {
        RequestBody body = MopidyTools.createRequestPageBrowse(uri,0);
        Call<MopidyRsTrackBean> call = mpServiceApi.postMopidyTrackPageQuest(MopidyTools.getHeaders(),body);
        call.enqueue(new Callback<MopidyRsTrackBean>() {
            @Override
            public void onResponse(Call<MopidyRsTrackBean> call, final Response<MopidyRsTrackBean> response) {
                MopidyRsTrackBean bean = response.body();
                List<MopidyRsTrackBean.Result> resultList = new ArrayList<MopidyRsTrackBean.Result>();
                resultList = bean.results;
                Glog.i(TAG,"resultList: " + resultList);
                ArrayList<IMusicServiceTrackContent> trackContents = new ArrayList<IMusicServiceTrackContent>();
                if (resultList != null && resultList.size()!= 0){
                    createFmChannels(resultList);
//                    for (int i = 0 ; i < resultList.size(); i++){
//                        final MopidyRsTrackBean.Result result = resultList.get(i);
//                        IMusicServiceTrackContent content = new IMusicServiceTrackContent(){
//
//                            @Override
//                            public String getCoverUrlSmall() {
//                                return "album";
//                            }
//
//                            @Override
//                            public String getCoverUrlMiddle() {
//                                return "album";
//                            }
//
//                            @Override
//                            public String getCoverUrlLarge() {
//                                return "album";
//                            }
//
//                            @Override
//                            public String getTrackTitle() {
//                                return result.name;
//                            }
//
//                            @Override
//                            public int getUrlType() {
//                                return IMusicServiceTrackContent.STATIC_URL;
//                            }
//
//                            @Override
//                            public String getPlayUrl() {
//                                return result.mantic_real_url;
//                            }
//
//                            @Override
//                            public String getSinger() {
//                                String artist_name = "";
//                                if (result.mantic_artists_name != null){
//                                    for (int i = 0; i< result.mantic_artists_name.size(); i++){
//                                        String singer = result.mantic_artists_name.get(i);
//                                        artist_name = singer+" ";
//                                    }
//                                }
//                                if (result.uri.contains("radio")&&artist_name.equals(" ")){
//                                    return "未知";
//                                }else {
//                                    return artist_name;
//                                }
//                            }
//
//                            @Override
//                            public long getDuration() {
//                                return result.length;
//                            }
//
//                            @Override
//                            public long getUpdateAt() {
//                                if (result.uri.contains("radio")){
//                                    return 0;
//                                }else {
//                                    return TimeUtil.timeMillionByTimeFormat("yyyy-MM-dd",result.update);
//                                }
//                            }
//
//                            @Override
//                            public String getUri() {
//                                return result.uri;
//                            }
//
//                            @Override
//                            public String getTimePeriods() {
//                                return result.mantic_radio_length;
//                            }
//
//                            @Override
//                            public String getAlbumId() {
//                                return result.mantic_album_uri;
//                            }
//                        };
//                        trackContents.add(content);
//                    }

//                    if(mIMusicServiceAlbum != null){
//                        mIMusicServiceAlbum.createMusicServiceAlbum(trackContents);
//                    }
//                    canRefresh = true;
//                    mLoading = false;
//                    currentTrackListPage++;
                }else {// tracklist 为0  说明数据没有了
//                    ArrayList<IMusicServiceTrackContent> noTracks = new ArrayList<IMusicServiceTrackContent>();
//                    Glog.i(TAG,"noTracks size" + noTracks.size());
//                    if(mIMusicServiceAlbum != null){
//                        mIMusicServiceAlbum.createMusicServiceAlbum(noTracks);
//                    }
//                    mLoading = false;
//                    canRefresh = false;
                }
            }

            @Override
            public void onFailure(Call<MopidyRsTrackBean> call, Throwable t) {
                Glog.i(TAG,"onFailure...");
//                if(mIMusicServiceAlbum != null){
//                    mIMusicServiceAlbum.onError(1,"");
//                }
            }
        });
    }


    private String fmChannelName = "";
    private String fmChannelAlbumId = "";
    private String fmChannelCover = "";
    private ChannelDetailsItemAdapter mChannelDetailsItemAdapter;

    private void createFmChannels(List<MopidyRsTrackBean.Result> resultList){
        Channel currChannel = mDataFactory.getCurrChannel();
        List<Channel> fmStartChannels = new ArrayList<Channel>();
        List<Channel> fmEndChannels = new ArrayList<Channel>();
        channels.clear();
        boolean fmStart = false;
        for (int i = 0; i < resultList.size(); i++) {
            MopidyRsTrackBean.Result result = resultList.get(i);
            Channel channel = new Channel();
            String artist_name = "";
            if (result.mantic_artists_name != null){
                for (int j = 0; j< result.mantic_artists_name.size(); j++){
                    String singer = result.mantic_artists_name.get(j);
                    artist_name = singer+" ";
                }
            }
            if (result.uri.contains("radio")&&artist_name.equals(" ")){
                artist_name =  "未知";
            }
            channel.setName(result.name);
            channel.setIconUrl(fmChannelCover);
            channel.setSinger(artist_name);
            channel.setDuration(result.length);
            channel.setLastSyncTime(0);
            channel.setServiceId("qingting"); //目前只有蜻蜓
            channel.setAlbum(fmChannelName);
            channel.setUri(result.uri);


            if(null != result.mantic_radio_length && result.mantic_radio_length.contains("-00:00")) {
                int index = result.mantic_radio_length.indexOf("-");
                String startStr = result.mantic_radio_length.substring(0, index);
                channel.setTimePeriods(startStr + "-23:59");
            } else {
                channel.setTimePeriods(result.mantic_radio_length);
            }

            channel.setMantic_album_name(fmChannelName);
            channel.setMantic_album_uri(fmChannelAlbumId);


            channel.setPlayUrl(result.mantic_real_url);

            if (currChannel != null && currChannel.getUri().equals(channel.getUri())) {
                channel.setPlayState(currChannel.getPlayState());
            } else {
                channel.setPlayState(Channel.PLAY_STATE_STOP);
            }
            if (TimeUtil.iscurFmPeriods(result.mantic_radio_length) || fmStart) {
                fmStartChannels.add(channel);
                fmStart = true;
            } else {
                fmEndChannels.add(channel);
            }
        }
            channels.addAll(fmStartChannels);
            channels.addAll(fmEndChannels);

        mDataFactory.notifyBeingPlayListChange();
        mChannelDetailsItemAdapter = new ChannelDetailsItemAdapter(ctx,channels,mActivity,true);
        if (channels.size() > 0 && TimeUtil.iscurFmPeriods(channels.get(0).getTimePeriods())) {
            mChannelDetailsItemAdapter.playPause(0,null);
        } else {
            ToastUtils.showShortSafe(ctx.getString(R.string.no_content_for_the_current_radio_time));
        }

    }
}
