package com.mantic.control.fragment;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.activity.NetworkConfigActivity;
import com.mantic.control.bt.BtDevice;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.ResourceUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BluetoothListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BluetoothListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BluetoothListFragment extends Fragment  implements  View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final int BT_CONNECT_SUCESS = 101;
    public static final int BT_CONNECTING = 102;
    public static final int BT_CONNECT_FAILED = 103;
    public static final int BT_REFRESH_FINISHED = 104;
    private View view;
    private LinearLayout oneLayout,nextLayout,searchLayout,noDevicesLayout,connectLayout;
    private TextView tvNext, tvTitle,notFoundText,oneDeviceName,oneConnectText;
    private ImageView llback,btSearchImage,oneConnectImage;
    private RecyclerView btRecyclerView;
    private Context mContext;
    BtDeviceAdapter btDeviceAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Handler handler = new Handler();

    private OnFragmentInteractionListener mListener;

    private AnimationDrawable netAnimation;

    private boolean nextEnable = false;

    private boolean needRefresh = false;

    private boolean girdItemClick = true;

    ObjectAnimator icon_anim;

    public static boolean connecting = false;

    public BluetoothListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BluetoothListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BluetoothListFragment newInstance(String param1, String param2) {
        BluetoothListFragment fragment = new BluetoothListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        mContext = this.getContext();

        startScan();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view == null){
            view = inflater.inflate(R.layout.fragment_bluetoothlist, container, false);
            llback = (ImageView) view.findViewById(R.id.toolbar_back);
            tvNext = (TextView) view.findViewById(R.id.toolbar_next);
            tvTitle = (TextView) view.findViewById(R.id.toolbar_title);
            llback.setOnClickListener(this);

            oneLayout = (LinearLayout) view.findViewById(R.id.bt_one_layout);
            oneLayout.setOnClickListener(this);
            oneLayout.setVisibility(View.GONE);
            oneConnectImage = (ImageView)view.findViewById(R.id.bt_one_connect_img);
            oneConnectText = (TextView)view.findViewById(R.id.bt_one_connect_text);
            oneDeviceName = (TextView)view.findViewById(R.id.bt_one_name);

            nextLayout = (LinearLayout)view.findViewById(R.id.bt_scan_next);
            nextLayout.setEnabled(nextEnable);
            nextLayout.setOnClickListener(this);
            searchLayout = (LinearLayout)view.findViewById(R.id.bt_search_layout);
            noDevicesLayout = (LinearLayout)view.findViewById(R.id.bt_no_device_layout);

            btSearchImage = (ImageView)view.findViewById(R.id.bt_search_image);
            netAnimation = (AnimationDrawable)btSearchImage.getBackground();
            netAnimation.start();

            tvTitle.setText(R.string.select_device_title);
            tvNext.setVisibility(View.VISIBLE);
            tvNext.setText(R.string.refreshing);
            tvNext.setOnClickListener(this);
            tvNext.setEnabled(false);
            notFoundText = (TextView) view.findViewById(R.id.device_not_found);
            notFoundText.setOnClickListener(this);

            connectLayout = (LinearLayout)view.findViewById(R.id.connect_layout);
            icon_anim = ObjectAnimator.ofFloat(oneConnectImage, "rotation", 0.0F, 359.0F);
            icon_anim.setRepeatCount(-1);
            icon_anim.setDuration(1000);
            icon_anim.setInterpolator(new LinearInterpolator()); //设置匀速旋转，不卡顿
            if (!NetworkConfigActivity.connected){
//                icon_anim.start();
            }
            if (NetworkConfigActivity.connected){
                nextLayout.setEnabled(true);
            }

            Glog.i("jys" , "connecting is : " + connecting);

            if (connecting){
                connectLayout.setVisibility(View.VISIBLE);
                oneConnectImage.setImageResource(R.drawable.bt_connecting);
                icon_anim.start();
                oneConnectText.setText(R.string.bt_connecting);

                if (NetworkConfigActivity.btList.size()>1){

                }
            }

            btDeviceAdapter = new BtDeviceAdapter();
            btRecyclerView = (RecyclerView)view.findViewById(R.id.bt_recyclerview);
            btRecyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
            btRecyclerView.setAdapter(btDeviceAdapter);



        }
        return view;
    }

    @SuppressLint("HandlerLeak")
    public Handler btHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case BT_CONNECT_FAILED:
                    Glog.i("jys","connectFailed...");
                    connecting = false;
                    if (NetworkConfigActivity.btList.size() == 1){
                        icon_anim.end();
                        oneConnectImage.setImageResource(R.drawable.bt_connected_failed);
                        oneConnectText.setText(R.string.bt_connected_failed);
                    }else if (NetworkConfigActivity.btList.size()>1){
                        btDeviceAdapter.itemConnectFailed();
                    }
                    break;
                case BT_CONNECT_SUCESS:
                    if (NetworkConfigActivity.btList.size() == 1){/*单个设备处理*/
                    icon_anim.end();
                    oneConnectImage.setImageResource(R.drawable.bt_connected);
                    oneConnectText.setText(R.string.bt_connected);
                    }else if (NetworkConfigActivity.btList.size() > 1){/*多个设备处理*/
                        btDeviceAdapter.itemConnected();
                    }
                    nextLayout.setEnabled(true);
                    break;
                case BT_CONNECTING:
                    break;

                case BT_REFRESH_FINISHED:
                    tvNext.setEnabled(true);
                    tvNext.setText(R.string.refresh);
                    break;

            }
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (btScanRun!=null){
            btScanRun.interrupt();
            btScanRun = null;
        }
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back:
//                if (!connecting && !NetworkConfigActivity.connected){// 正在连接或者已连接 不清空btlist
//
//                }
                NetworkConfigActivity.btList.clear();
                if (getActivity() instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).popFragment(getTag());
                }
                break;
            case R.id.toolbar_next:
                tvNext.setEnabled(false);
                tvNext.setText(R.string.refreshing);
                Glog.i("jys","重新搜索");
                NetworkConfigActivity.btList.clear();
//                oneLayout.setVisibility(View.GONE);
                btRecyclerView.setVisibility(View.GONE);
                noDevicesLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);
                startScan();
//                girdItemClick = true;

                break;
            case R.id.bt_one_layout:
                // 只有一个设备，取数组0
                if (!connecting || !NetworkConfigActivity.connected){ // connecting 状态不允许点击
                    Glog.i("jys","开始连接.................");
                    startConnect(0);
                }
                break;
            case R.id.device_not_found:
                Glog.i("jys","连接不到设备被点击");
                if (this.getActivity() instanceof FragmentEntrust) {
                    ((FragmentEntrust) this.getActivity()).pushFragment(new ConnectDetailFragment(),"bt_detail");
                }
                break;
            case R.id.bt_scan_next:
                if (this.getActivity() instanceof FragmentEntrust) {
                    ((FragmentEntrust) this.getActivity()).pushFragment(new WifiConnectUIFrament(),"send_wifi");
                }
                break;
        }

    }

    private void startScan(){
        mListener.onFragmentInteraction(Uri.parse("scan"));
        handler.post(btScanRun);
    }

    private void startConnect(int index){
        connecting = true;
        mListener.onFragmentInteraction(Uri.parse("connect"+"/"+index));
        handler.removeCallbacks(btScanRun);
        connectLayout.setVisibility(View.VISIBLE);
        oneConnectImage.setImageResource(R.drawable.bt_connecting);
        icon_anim.start();
        oneConnectText.setText(R.string.bt_connecting);
//        handler.postDelayed(connectRun,1000);

    }

    private void connectDetail(){

    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public Thread btScanRun  = new Thread() {
        @Override
        public void run() {
            Glog.i("jys","btScanRun run........... device.size:" + NetworkConfigActivity.btList.size());
            if (NetworkConfigActivity.btList.size() == 1){/*搜到一个设备，显示One布局，搜索隐藏*/
                netAnimation.stop();
                searchLayout.setVisibility(View.GONE);
                oneLayout.setVisibility(View.VISIBLE);
                btRecyclerView.setVisibility(View.GONE);
                oneDeviceName.setText(NetworkConfigActivity.btList.get(0).getName());
                if(NetworkConfigActivity.connected){
                    icon_anim.end();
                    connectLayout.setVisibility(View.VISIBLE);
                    oneConnectImage.setImageResource(R.drawable.bt_connected);
                    oneConnectText.setText(R.string.bt_connected);
                }
            }else if (NetworkConfigActivity.btList.size() >1){/*搜到多余设备，显示Gird布局，搜索隐藏*/
                searchLayout.setVisibility(View.GONE);
                oneLayout.setVisibility(View.GONE);
                btRecyclerView.setVisibility(View.VISIBLE);
            }else {
                noDevicesLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);
                oneLayout.setVisibility(View.GONE);
                btRecyclerView.setVisibility(View.GONE);
            }
            btDeviceAdapter.notifyDataSetChanged();
            if (((NetworkConfigActivity)getActivity()) !=null){
                if(((NetworkConfigActivity)getActivity()).btScan.equals("stop")){
                    /*搜索结束*/
                    Glog.i("jys","btScanRun finish.......device.size: " + NetworkConfigActivity.btList.size());
                    if (NetworkConfigActivity.btList.size() == 0){ //搜索不到任何设备
                        searchLayout.setVisibility(View.GONE);
                        noDevicesLayout.setVisibility(View.VISIBLE);
//                        nextLayout.setEnabled(true);
                    }
                    handler.removeCallbacks(this);

                }else {
                    handler.postDelayed(this,1000);
                }
            }
        }
    };

    class BtDeviceAdapter extends RecyclerView.Adapter<BluetoothListFragment.BtDeviceAdapter.DeviceInfoHolder>{

        public DeviceInfoHolder deviceInfoHolder ;

        @Override
        public DeviceInfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new DeviceInfoHolder(LayoutInflater.from(mContext).inflate(R.layout.bt_device_info, parent ,false));
        }

        @Override
        public void onBindViewHolder(final DeviceInfoHolder holder, @SuppressLint("RecyclerView") final int position) {

            holder.itemImage.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (girdItemClick){
                        Glog.i("jys","Gird Item Clicked!.......... item: " + position);
                        BtDevice btDevice = NetworkConfigActivity.btList.get(position);
//                        btDevice.setConnecting();
                        deviceInfoHolder = holder;
                        girdItemClick = false;
                        holder.connectLayout.setVisibility(View.VISIBLE);
                        holder.item_icon_anim.start();
                        holder.connectImage.setImageResource(R.drawable.bt_connecting);
                        holder.connectText.setText(R.string.bt_connecting);
                        holder.itemImage.setBackgroundResource(R.drawable.bt_device_img_click_background);
                        startConnect(position);
                    }

                }
            });
            holder.updateItem(position);
        }

        private void itemConnected(){
            if (deviceInfoHolder!= null){ //多个设备
                deviceInfoHolder.item_icon_anim.end();
                deviceInfoHolder.connectImage.setImageResource(R.drawable.bt_connected);
                deviceInfoHolder.connectText.setText(R.string.bt_connected);
            }
        }

        private void itemConnectFailed(){
            if (deviceInfoHolder!= null){ //多个设备
                deviceInfoHolder.item_icon_anim.end();
                deviceInfoHolder.connectImage.setImageResource(R.drawable.bt_connected_failed);
                deviceInfoHolder.connectText.setText(R.string.bt_connected_failed);
                deviceInfoHolder.itemImage.setBackgroundResource(R.drawable.bt_device_img_background);
                girdItemClick = true;
            }
        }

        private void isConnecting(){

        }
        @Override
        public int getItemCount() {
            return ((NetworkConfigActivity)getActivity()).btList.size();
        }

        class DeviceInfoHolder extends RecyclerView.ViewHolder{
            TextView btNameText,connectText;
            ImageView connectImage,itemImage;
            LinearLayout connectLayout;
            ObjectAnimator item_icon_anim;
            DeviceInfoHolder(View itemView) {
                super(itemView);
                btNameText = (TextView) itemView.findViewById(R.id.bt_name);
                connectText = (TextView) itemView.findViewById(R.id.connect_text);
                connectImage = (ImageView)itemView.findViewById(R.id.bt_connect_img);
                itemImage = (ImageView)itemView.findViewById(R.id.bt_device_item);
                connectLayout = (LinearLayout)itemView.findViewById(R.id.bt_item_connect_layout);

                item_icon_anim = ObjectAnimator.ofFloat(connectImage, "rotation", 0.0F, 359.0F);
                item_icon_anim.setRepeatCount(-1);
                item_icon_anim.setDuration(1000);
                item_icon_anim.setInterpolator(new LinearInterpolator()); //设置匀速旋转，不卡顿

            }

            private void  updateItem( int position){
                Glog.i("jys","updateItem ... " + position);
                this.btNameText.setText(NetworkConfigActivity.btList.get(position).getName());
            }
        }
    }

    public static class DeviceInfoDecoration extends RecyclerView.ItemDecoration {
        private Context ctx;
        private int divideHeight = 1;
        private int divideMarginLeft;
        private Paint dividerPaint;

        public DeviceInfoDecoration(Context context){
            this.ctx = context;
            this.divideMarginLeft = ResourceUtils.dip2px(this.ctx,ResourceUtils.getXmlDef(this.ctx,R.dimen.channel_detail_more_list_item_left_padding));
            this.dividerPaint = new Paint();
            this.dividerPaint.setColor(this.ctx.getResources().getColor(R.color.mainTabBottomLineColor));
            divideHeight = 1;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int childCount = parent.getChildCount();
            int left = parent.getPaddingLeft() + this.divideMarginLeft;
            int right = parent.getWidth() - parent.getPaddingRight();

            for (int i = 0; i < childCount; i++) { //
                View view = parent.getChildAt(i);
                float top = view.getBottom();
                float bottom = view.getBottom() + divideHeight;
//                if (i == childCount - 1) {
//                    c.drawRect(0, top, right, bottom, dividerPaint);
//                } else {
                c.drawRect(left, top, right, bottom, dividerPaint);
//                }
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = divideHeight;
        }
    }
}
