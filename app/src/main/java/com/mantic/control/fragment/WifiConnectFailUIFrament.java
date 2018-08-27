package com.mantic.control.fragment;

import android.content.Context;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantic.control.R;
import com.mantic.control.activity.NetworkConfigActivity;
import com.mantic.control.utils.Glog;

/**
 * Created by wujiangxia on 2017/4/1.
 */
public class WifiConnectFailUIFrament extends Fragment implements View.OnClickListener {

    private String TAG=WifiConnectFailUIFrament.class.getName();

    private TextView tvNext, tvTitle,failText;
    private Button btn_retry;
    private View view;
    private WifiConnectFailUIFrament.OnFragmentInteractionListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof WifiConnectFailUIFrament.OnFragmentInteractionListener) {
            mListener = (WifiConnectFailUIFrament.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Glog.v("wujx", "WifiConnectFailUIFrament initView");
        if (view == null) {
            view = inflater.inflate(R.layout.wificonnect_ui_fail_frag,container,false);
            tvNext = (TextView) view.findViewById(R.id.toolbar_next);
            ImageView llback = (ImageView) view.findViewById(R.id.toolbar_back);
            llback.setOnClickListener(this);
            btn_retry = (Button) view.findViewById(R.id.btn_retry);
            tvTitle = (TextView) view.findViewById(R.id.toolbar_title);
            failText = (TextView)view.findViewById(R.id.wifi_net_fail_id) ;
            tvTitle.setText(R.string.fail_connet);
            tvNext.setOnClickListener(this);
            btn_retry.setOnClickListener(this);
            tvNext.setText("教程");
            tvNext.setVisibility(View.VISIBLE);
        }
        Bundle bundle = getArguments();

        Toast.makeText(getContext(),bundle.getString("param2"),Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onCreateView: "+bundle.getString("param2") );

        boolean is5fWifi = bundle.getBoolean("param1");
        if (is5fWifi){
            failText.setText(R.string.wifi_net_fail_5g);
        }
        return view;
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_retry:
//                NetworkConfigActivity.btList.clear();
                if (getActivity() instanceof FragmentEntrust) {
                    if (NetworkConfigActivity.bleNetwork){
                        mListener.onFragmentInteraction(Uri.parse("re-ble-pair"));
                    }
                    ((NetworkConfigActivity)getActivity()).need_sendData = true;
                    ((FragmentEntrust) getActivity()).popAllFragment();
                }
                break;
            case R.id.toolbar_next:
                if (getActivity() instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).popAllFragment();
                }
                break;
            case R.id.toolbar_back:
                if (getActivity() instanceof FragmentEntrust) {
                    if (NetworkConfigActivity.bleNetwork){
                        mListener.onFragmentInteraction(Uri.parse("re-ble-pair"));
                    }
                    ((NetworkConfigActivity)getActivity()).need_sendData = true;
                    ((FragmentEntrust) getActivity()).popAllFragment();
                }
                break;
        }
    }
}
