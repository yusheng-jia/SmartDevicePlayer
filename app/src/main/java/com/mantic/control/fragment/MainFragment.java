package com.mantic.control.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.activity.DeviceDetailActivity;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.data.DataFactory;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.SharePreferenceUtil;
import com.mantic.control.widget.MainViewPager;

import java.util.ArrayList;
import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements DataFactory.OnMainViewPagerListener,DataFactory.DeviceOnlineStateListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = "MainFragment";

    private RelativeLayout rl_search_bar;
    private Toolbar mToolbar;
    private ImageView mNavButton;
    public ImageView mSelectDevice;
    private ImageButton mSearchButton;
    //private Button mBottomDialogButton;
    private TabLayout mTablayout;
    private View view_tab_layout_separator;
    private int[] mTabTitles = {R.string.my_channel,R.string.music_service};
    private ArrayList<Fragment> mTabFragmentList;
    private FragmentAdapter mFragmentAdapter;
    private MainViewPager mViewPager;

    private String selectButtonName = "酷曼音箱";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
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
    }

    @Override
    public void callScroll(boolean canScroll) {
        mViewPager.setScroll(canScroll);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        this.initToolbar(view);
//        initDeiviceName();
        /*
        this.mBottomDialogButton = (Button) view.findViewById(R.id.open_bottom_dialog);
        this.mBottomDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
                dialog.setTitle("底部Dialog");
                dialog.setContentView(R.layout.bottom_dialog);
                dialog.show();
            }
        });
        */
        this.mTablayout = (TabLayout) view.findViewById(R.id.main_tab);

        this.mViewPager = (MainViewPager) view.findViewById(R.id.main_viewpager);
        this.mTabFragmentList = new ArrayList<Fragment>();
        this.mTabFragmentList.add(new EntertainmentFragment());
        this.mTabFragmentList.add(new MySkillsFragment());
        this.mTabFragmentList.add(new MyChannelFragment());
        if (SharePreferenceUtil.getAdvertSwitch(getContext())){
            this.mTabFragmentList.add(new MakeSoundFragment());
        }
//        this.mTabFragmentList.add(new MyMusicServiceFragment());

        this.mFragmentAdapter = new FragmentAdapter(this.getChildFragmentManager(),this.mTabFragmentList);
        this.mViewPager.setAdapter(this.mFragmentAdapter);
        this.mViewPager.setOffscreenPageLimit(3);
//        this.mViewPager.setScroll(false);
        this.mTablayout.setupWithViewPager(this.mViewPager);

        view_tab_layout_separator = view.findViewById(R.id.view_tab_layout_separator);
        ViewGroup.LayoutParams layoutParams = view_tab_layout_separator.getLayoutParams();
        layoutParams.height = 1;
        view_tab_layout_separator.setLayoutParams(layoutParams);

        for(int i = 0;i < this.mFragmentAdapter.getCount();i++){
            TabLayout.Tab tab = this.mTablayout.getTabAt(i);
            tab.setCustomView(R.layout.main_tab_item);
            ImageView imageView = (ImageView) tab.getCustomView().findViewById(R.id.tab_indicator);
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tab_text);
            textView.setText(this.mFragmentAdapter.getPageTitle(i));
            if(i == 0){
                textView.setSelected(true);
                textView.getPaint().setFakeBoldText(true);
                imageView.setVisibility(View.VISIBLE);
            }else{
                textView.setSelected(false);
                imageView.setVisibility(View.INVISIBLE);
            }
        }
        this.mTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TextView tv_tab_text = (TextView) tab.getCustomView().findViewById(R.id.tab_text);
                tv_tab_text.setSelected(true);
                tv_tab_text.getPaint().setFakeBoldText(true);
                tab.getCustomView().findViewById(R.id.tab_indicator).setVisibility(View.VISIBLE);
                mViewPager.setCurrentItem(tab.getPosition(), false);
                if (getActivity() instanceof MainActivity){
                    Glog.i(TAG,"getActivity -- > MainActivity ");
                    ((MainActivity)getActivity()).onAttachFragment(mTabFragmentList.get(tab.getPosition()));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView tv_tab_text = (TextView) tab.getCustomView().findViewById(R.id.tab_text);
                tv_tab_text.setSelected(false);
                tv_tab_text.getPaint().setFakeBoldText(false);
                tab.getCustomView().findViewById(R.id.tab_indicator).setVisibility(View.INVISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    public void onBackPressed(){

    }

    private void initToolbar(View view){
//        this.rl_search_bar = (RelativeLayout) view.findViewById(R.id.rl_search_bar);
//        this.mToolbar = (Toolbar) view.findViewById(R.id.fragment_main_toolbar);
//        ((AppCompatActivity)this.getActivity()).setSupportActionBar(this.mToolbar);
        this.setHasOptionsMenu(true);

        this.mNavButton = (ImageView) view.findViewById(R.id.nav_button);
        this.mNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if(activity instanceof ClickNavButton){
                    ((ClickNavButton)activity).onClickNavButton();
                }
            }
        });

        this.mSelectDevice = (ImageView) view.findViewById(R.id.select_device);
        this.mSelectDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showSelectDeviceDialog();
                startDeviceManage();
            }
        });

        this.mSearchButton = (ImageButton) view.findViewById(R.id.search_button);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchResultFragment searchFragment= new SearchResultFragment();
                Bundle bundle = new Bundle();
                bundle.putString("searchFrom", "MainFragment");
                searchFragment.setArguments(bundle);
                if (getActivity() instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).pushFragment(searchFragment, searchFragment.getClass().getSimpleName());
                }
            }
        });
    }

    private void initDeiviceName() {
        String deviceName = SharePreferenceUtil.getDeviceName(getActivity());
        Glog.i(TAG,"initDeiviceName -> deviceName: " + deviceName);
      /*  if (deviceName.isEmpty()){
            this.mSelectDevice.setText(selectButtonName);
        }else {
            this.mSelectDevice.setText(deviceName);
        }*/
    }

    private void showSelectDeviceDialog(){
        SelectDeviceDialogFragment.showDialog((AppCompatActivity) getActivity());
    }

    private void startDeviceManage(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), DeviceDetailActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_left_out);
//        DeviceDetailFragment deviceDetailFragment = new DeviceDetailFragment();
//        if(getActivity() instanceof FragmentEntrust){
//            ((FragmentEntrust) getActivity()).pushFragment(deviceDetailFragment, DeviceDetailFragment.class.getName());
//        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        initDeiviceName();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        DataFactory.newInstance(getContext()).registerMainViewPagerListener(this);
        DataFactory.newInstance(getContext()).registerDeviceOnlineStatusListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        DataFactory.newInstance(getContext()).unregisterMainViewPagerListener(this);
        DataFactory.newInstance(getContext()).unregisterDeviceOnlineStatusListener(this);
    }

    @Override
    public void onlineStatus(boolean status) {
        if (status){
            mSelectDevice.setImageResource(R.drawable.select_device_selector_online);
        }else {
            mSelectDevice.setImageResource(R.drawable.select_device_selector_offline);
        }
    }

    public interface ClickNavButton{
        public void onClickNavButton();
    }

    public class FragmentAdapter extends FragmentStatePagerAdapter {

//        private int [] titles = {R.string.my_channel,R.string.music_service,R.string.skills};//,
        private int [] titles = {R.string.entertainment, R.string.skills,R.string.my_channel, R.string.make_sound};
        private int [] titlesnoSkill = {R.string.entertainment, R.string.skills,R.string.my_channel};
        private List<Fragment> fragmentList;
        public FragmentAdapter(FragmentManager fm, List<Fragment> fragmentList) {
            super(fm);
            this.fragmentList = fragmentList;
        }
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            if (SharePreferenceUtil.getAdvertSwitch(getContext())){
                return getContext().getResources().getString(titles[position]);
            }else {
                return getContext().getResources().getString(titlesnoSkill[position]);
            }

        }
    }
}
