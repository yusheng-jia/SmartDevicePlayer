package com.mantic.control.fragment;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.data.DataFactory;
import com.mantic.control.entiy.MusicService;
import com.mantic.control.musicservice.MyMusicService;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;

import java.util.ArrayList;

/**
 * Created by root on 17-4-10.
 */
public class MusicServiceFragment extends Fragment {
    private static final String TAG = "MyMusicServiceFragment";
    private RecyclerView mMusicServiceGrid;
    private GridLayoutManager mGridLayoutManager;
    private MusicServiceItemDecoration mMusicServiceItemDecoration;
    private MusicServiceItemAdapter mMusicServiceItemAdapter;
    private DataFactory mDataFactory;
    private ArrayList<MusicService> mMusicServiceList;

    public MusicServiceFragment(){
        this.initMusicServiceData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_service,container,false);
        this.initMusicServiceGrid(view);
        return view;
    }

    private void initMusicServiceData(){
        this.mDataFactory = DataFactory.newInstance(getActivity());
        this.mMusicServiceList = this.mDataFactory.getMusicServiceList();
    }

    private void initMusicServiceGrid(View view){
        this.mMusicServiceGrid = (RecyclerView) view.findViewById(R.id.music_service_grid);
        this.mMusicServiceItemDecoration = new MusicServiceItemDecoration(getActivity());
        this.mMusicServiceGrid.addItemDecoration(this.mMusicServiceItemDecoration);
        this.mGridLayoutManager = new GridLayoutManager(getActivity(),2);
        this.mMusicServiceGrid.setLayoutManager(this.mGridLayoutManager);
        this.mMusicServiceItemAdapter = new MusicServiceItemAdapter(getActivity());
        this.mMusicServiceGrid.setAdapter(this.mMusicServiceItemAdapter);
    }

    public class MusicServiceItemDecoration extends RecyclerView.ItemDecoration{
        private Context ctx;

        public MusicServiceItemDecoration(Context context){
            this.ctx = context;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            if(position == 0 || position == 1){
                outRect.top = ResourceUtils.dip2px(this.ctx,ResourceUtils.getXmlDef(this.ctx,R.dimen.musicServiceGridMarginTop));
            } else {
                outRect.top = ResourceUtils.dip2px(this.ctx, ResourceUtils.getXmlDef(this.ctx, R.dimen.MusicServiceItemBottomDecoration));
            }
            if(position == mMusicServiceItemAdapter.getItemCount() - 1){
                outRect.bottom = ResourceUtils.dip2px(this.ctx,ResourceUtils.getXmlDef(this.ctx,R.dimen.myChannelLastItemGap));
            }
        }
    }

    public class MusicServiceItemAdapter extends RecyclerView.Adapter{
        private Context ctx;

        public MusicServiceItemAdapter(Context context){
            this.ctx = context;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MusicServiceItemViewHolder(LayoutInflater.from(this.ctx).inflate(R.layout.music_service_item,parent,false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((MusicServiceItemViewHolder)holder).showMusicServiceItem(position);
        }

        @Override
        public int getItemCount() {
            return mMusicServiceList.size();
        }

        class MusicServiceItemViewHolder extends RecyclerView.ViewHolder{
            private ImageView music_service_icon;
            private TextView music_service_name;
            private String serviceId;
            private String serviceName;
            private MusicService musicService;

            public MusicServiceItemViewHolder(View itemView) {
                super(itemView);
                this.music_service_icon = (ImageView) itemView.findViewById(R.id.music_service_icon);
                this.music_service_name = (TextView) itemView.findViewById(R.id.music_service_name);
                this.music_service_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Glog.i(TAG,"goto_channel_details onClick serviceId = "+serviceId);
                        if(serviceId != null && !serviceId.isEmpty()) {
                            /*
                            MusicServiceSubItemFragment mssiFragment = new MusicServiceSubItemFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("serviceId", serviceId);
                            mssiFragment.setArguments(bundle);
                            if (getActivity() instanceof FragmentEntrust) {
                                ((FragmentEntrust) getActivity()).pushFragment(mssiFragment, serviceId);
                            }
                            */
                            /*
                            Bundle bundle = new Bundle();
                            bundle.putInt(MyMusicService.PRE_DATA_TYPE,MyMusicService.TYPE_FIRST);
                            MyMusicService service = musicService.getMyMusicService();
                            service.exec(MyMusicServiceFragment.this,bundle);
                            */
                            MusicServiceSubItemFragment mssiFragment = new MusicServiceSubItemFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString(MyMusicService.MY_MUSIC_SERVICE_ID,serviceId);
                            bundle.putInt(MyMusicService.PRE_DATA_TYPE,MyMusicService.TYPE_FIRST);
                            bundle.putString(MusicServiceSubItemFragment.SUB_ITEM_TITLE,serviceName);
                            mssiFragment.setArguments(bundle);
                            if (MusicServiceFragment.this.getActivity() instanceof FragmentEntrust) {
                                String fragmentTag = MyMusicService.PRE_DATA_TYPE+MyMusicService.TYPE_FIRST+serviceId;
                                Glog.i(TAG,"fragmentTag = "+fragmentTag);
                                ((FragmentEntrust) MusicServiceFragment.this.getActivity()).pushFragment(mssiFragment, fragmentTag);
                            }
                        }
                    }
                });
            }

            public void showMusicServiceItem(int position){
                musicService = mMusicServiceList.get(position);
                MyMusicService service = musicService.getMyMusicService();
                if(service != null) {
                    this.serviceId = service.getMusicServiceID();
                }
                this.music_service_icon.setImageBitmap(musicService.getIcon());
                this.serviceName = musicService.getName();
                this.music_service_name.setText(this.serviceName);
            }
        }
    }
}
