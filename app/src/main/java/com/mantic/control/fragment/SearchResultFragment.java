package com.mantic.control.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.mantic.control.R;
import com.mantic.control.adapter.RecentPlayAdapter;
import com.mantic.control.data.Channel;
import com.mantic.control.data.DataFactory;
import com.mantic.control.decoration.RecentPlayItemDecoration;
import com.mantic.control.ui.adapter.MyViewPagerAdapter;
import com.mantic.control.utils.DensityUtils;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.CustomDialog;
import com.mantic.control.widget.Flow.FlowLayout;
import com.mantic.control.widget.Flow.TagAdapter;
import com.mantic.control.widget.Flow.TagFlowLayout;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wujiangxia on 2017/5/4.
 */
public class SearchResultFragment extends BaseSlideFragment implements TabLayout.OnTabSelectedListener,
        View.OnClickListener, TextView.OnEditorActionListener, DataFactory.OnRecentPlayListener,
        DataFactory.OnHideSoftKeyListener, TagFlowLayout.OnTagClickListener, DataFactory.OnSearchHistoryListener {
    private View view;
    public static final String TAG = "SearchFragment";

    private boolean isNeedShowAddBtn = true;
    private String searchFrom = "";
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyViewPagerAdapter viewPagerAdapter;
    //TabLayout标签
    private String[] titles = new String[]{"表演者", "歌曲", "专辑", "广播"};
    private List<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> uriList = new ArrayList<>();
    private List<String> searchHistoryList = new ArrayList<>();

    private LinearLayout ll_search_data;
    private LinearLayout ll_search_history;
    private TextView tv_last_play;
    private TextView tv_search_history;
    private ImageView iv_search_history_empty;
    private EditText mEditSearch;
    private TextView mBtnCancel;
    private ImageView ivClear;
    private boolean isSearch;
    private AuthorFragment mAuthorFragment;
    private SongsFragment mSongsFragement;
    private AlbumFragment mAlbumFragment;
    private RadioFragment mRadioFragment;
    private SearchLastPlayListFragment mSearchLastPlayListFragment;
    private Set<String> hasSelectList;
    private String key;
    private String searchKey;
    private SearchFragment mSearchFragment;
    private RecyclerView rcv_recent_play;
    private ScrollView sv_history;
    private TagFlowLayout tfl_search_history;

    private RecentPlayAdapter mRecentPlayAdapter;
    private TagAdapter<String> mSearchHistoryAdapter;

    private LinearLayout llLast;
    private TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {


        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            enableSubmitIfReady();
            if (s.length() > 0) {
                ivClear.setVisibility(View.VISIBLE);
            } else {
                ivClear.setVisibility(View.GONE);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        if (null != arguments) {
            uriList = arguments.getStringArrayList("uriList");
            searchFrom = arguments.getString("searchFrom");
        }

        if ("MyChannelAddFragment".equals(searchFrom)) {
            isNeedShowAddBtn = true;
        } else {
            isNeedShowAddBtn = false;
        }


        searchHistoryList = Util.getSearchHistoryList(mContext, mDataFactory);
        mSearchHistoryAdapter = new TagAdapter<String>(searchHistoryList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView tv = (TextView) View.inflate(mContext, R.layout.search_history_item, null);
                tv.setText(s);
                return tv;
            }
        };

        tfl_search_history.setAdapter(mSearchHistoryAdapter);

        if (!Util.hasSearchHistoryData(mContext)) {
            ll_search_history.setVisibility(View.GONE);
        } else {
            ll_search_history.setVisibility(View.VISIBLE);
        }

        mRecentPlayAdapter = new RecentPlayAdapter(mContext, mActivity, uriList, isNeedShowAddBtn);
        rcv_recent_play.setAdapter(mRecentPlayAdapter);
        if (!Util.hasRecentPlayData(mContext)) {
            tv_last_play.setVisibility(View.GONE);
        } else {
            tv_last_play.setVisibility(View.VISIBLE);
        }

        hasSelectList = new HashSet<String>();

        Bundle bundle = new Bundle();
        bundle.putString("searchFrom", searchFrom);

        if ("MyChannelAddFragment".equals(searchFrom)) {
            mSongsFragement = new SongsFragment();
            mSongsFragement.setArguments(bundle);
            mSearchLastPlayListFragment = new SearchLastPlayListFragment();
            fragments.add(mSongsFragement);
            viewPagerAdapter = new MyViewPagerAdapter(getChildFragmentManager(), titles, fragments);
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setOffscreenPageLimit(4);
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setVisibility(View.GONE);
        } else {
            mAuthorFragment = new AuthorFragment();
            mAuthorFragment.setArguments(bundle);
            mSongsFragement = new SongsFragment();
            mSongsFragement.setArguments(bundle);
            mAlbumFragment = new AlbumFragment();
            mAlbumFragment.setArguments(bundle);
            mRadioFragment = new RadioFragment();
            mSearchLastPlayListFragment = new SearchLastPlayListFragment();
            fragments.add(mAuthorFragment);
            fragments.add(mSongsFragement);
            fragments.add(mAlbumFragment);
            fragments.add(mRadioFragment);
            viewPagerAdapter = new MyViewPagerAdapter(getChildFragmentManager(), titles, fragments);
            viewPager.setAdapter(viewPagerAdapter);
            viewPager.setOffscreenPageLimit(4);
            tabLayout.setupWithViewPager(viewPager);
        }


        setIndicator(tabLayout);
        mDataFactory.registerRecentPlayListener(this);
        mDataFactory.registerHideSoftKeyListener(this);
        mDataFactory.registerSearchHistoryListener(this);

        searchKey = arguments.getString("searchKey");
        if (!TextUtils.isEmpty(searchKey)) {
            mEditSearch.setText(searchKey);
            key = searchKey;
            search(searchKey);
        }

    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        mEditSearch = (EditText) view.findViewById(R.id.search_input);
        mEditSearch.addTextChangedListener(mTextWatcher);
        mBtnCancel = (TextView) view.findViewById(R.id.btn_cancel);
        ivClear = (ImageView) view.findViewById(R.id.btn_clear);
        llLast = (LinearLayout) view.findViewById(R.id.ll_last);
        rcv_recent_play = (RecyclerView) view.findViewById(R.id.rcv_recent_play);
        rcv_recent_play.setLayoutManager(new LinearLayoutManager(mContext));
        rcv_recent_play.addItemDecoration(new RecentPlayItemDecoration(mContext));
        rcv_recent_play.setNestedScrollingEnabled(false);

        tfl_search_history = (TagFlowLayout) view.findViewById(R.id.tfl_search_history);
        sv_history = (ScrollView) view.findViewById(R.id.sv_history);

        ll_search_data = (LinearLayout) view.findViewById(R.id.ll_search_data);
        ll_search_history = (LinearLayout) view.findViewById(R.id.ll_search_history);
        iv_search_history_empty = (ImageView) view.findViewById(R.id.iv_search_history_empty);
        tv_last_play = (TextView) view.findViewById(R.id.tv_last_play);
        tv_search_history = (TextView) view.findViewById(R.id.tv_search_history);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        isSearch = false;
        ll_search_data.setVisibility(View.GONE);
        llLast.setVisibility(View.VISIBLE);
        //设置TabLayout标签的显示方式
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        //循环注入标签
        for (String tab : titles) {
            tabLayout.addTab(tabLayout.newTab().setText(tab));
        }

        //设置TabLayout点击事件
        tabLayout.setOnTabSelectedListener(this);

        mBtnCancel.setOnClickListener(this);
        ivClear.setOnClickListener(this);
        iv_search_history_empty.setOnClickListener(this);
        mEditSearch.setOnEditorActionListener(this);

        tfl_search_history.setOnTagClickListener(this);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.search_result_fragment;
    }


    @Override
    public void recentPlayChange(List<Channel> channelList) {
        tv_last_play.setVisibility(View.VISIBLE);
        for (int i = 0; i < channelList.size(); i++) {

            Channel channel = channelList.get(i);
            Glog.i(TAG, "recentPlayChange: " + channel.getName() + "   " + channel.getSinger());
            if (channel.getName().equals(mDataFactory.getCurrChannel().getName()) && channel.getUri().equals(mDataFactory.getCurrChannel().getUri())) {
                channel.setPlayState(mDataFactory.getCurrChannel().getPlayState());
            } else {
                channel.setPlayState(Channel.PLAY_STATE_STOP);
            }
            channelList.set(i, channel);
        }

        mRecentPlayAdapter.setRecentPlayList(channelList);
    }

    @Override
    public void searchHistoryChange(List<String> searchHistoryList) {
        Glog.i(TAG, "searchHistoryChange: " + searchHistoryList.size());
        this.searchHistoryList = searchHistoryList;
        if (searchHistoryList.size() <= 0) {
            ll_search_history.setVisibility(View.GONE);
        } else {
            ll_search_history.setVisibility(View.VISIBLE);
        }
        mSearchHistoryAdapter.setTagDatas(this.searchHistoryList);
        mSearchHistoryAdapter.notifyDataChanged();
    }

    @Override
    public void onDestroy() {
        mDataFactory.unregisterRecentPlayListener(this);
        mDataFactory.unregisterHideSoftKeyListener(this);
        mDataFactory.unregisterSearchHistoryListener(this);
        hideSoftKey1();
        super.onDestroy();
    }

    private void hideSoftKey1() {
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getWindow().getDecorView().getWindowToken(), 0);
    }

    @Override
    public void hideSoftKey() {
        hideSoftKey1();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            isSearch = true;
            Glog.v("wujx", "setOnEditorActionListener");
            key = mEditSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(key)) {
                mDataFactory.addSearchHistory(mContext, key);
                mDataFactory.notifySearchHistoryListChange(Util.getSearchHistoryList(mContext, mDataFactory));
            }

            search(key);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTagClick(View view, int position, FlowLayout parent) {
        key = ((TextView)view).getText().toString().trim();
        mDataFactory.addSearchHistory(mContext, key);
        mDataFactory.notifySearchHistoryListChange(Util.getSearchHistoryList(mContext, mDataFactory));
        mEditSearch.setText(key);
        mEditSearch.setSelection(key.length());
        search(key);
        return true;
    }

    private void search(final String key) {
        if (!TextUtils.isEmpty(key)) {
            hideSoftKey1();
            hasSelectList.clear();
            sv_history.setVisibility(View.GONE);
            ll_search_data.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(0);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mDataFactory.notifySearchKeySet(key);
                }
            }, 0);
        } else {
            Toast.makeText(getActivity(), "请输入搜索内容", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());
        String position = String.valueOf(tab.getPosition());
        key = mEditSearch.getText().toString();

    /*    switch (tab.getPosition()) {
            case 0:
                mSearchFragment = mAuthorFragment;
                break;
            case 1:
                mSearchFragment = mSongsFragement;
                break;
            case 2:
                mSearchFragment = mAlbumFragment;
                break;
            case 3:
                mSearchFragment = mPlaylistFragment;
                break;
            case 4:
                mSearchFragment = mAnnouncerFragment;
                break;
            default:
                mSearchFragment = null;
        }

        if (!hasSelectList.contains(position) && mSearchFragment != null) {
            hasSelectList.add(position);
            mSearchFragment.setSearch(key);
        }*/


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void enableSubmitIfReady() {
        if (TextUtils.isEmpty(mEditSearch.getText())) {
            Glog.v("wujx", "enableSubmitIfReady");
            isSearch = false;
            ll_search_data.setVisibility(View.GONE);
            sv_history.setVisibility(View.VISIBLE);
//            mDataFactory.notifySearchKeySet("");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_clear:
                mEditSearch.setText("");
                break;
            case R.id.btn_cancel:
                if (getActivity() instanceof FragmentEntrust) {
                    ((FragmentEntrust) getActivity()).popFragment(getTag());
                }
                break;
            case R.id.iv_search_history_empty:
                CustomDialog.Builder mBuilder = new CustomDialog.Builder(mActivity);
                mBuilder.setTitle(mActivity.getString(R.string.dialog_btn_prompt));
                mBuilder.setMessage(mActivity.getString(R.string.sure_clear_search_history));
                mBuilder.setPositiveButton(mActivity.getString(R.string.dialog_btn_confirm), new CustomDialog.Builder.DialogPositiveClickListener() {
                    @Override
                    public void onPositiveClick(CustomDialog dialog) {
                        mDataFactory.clearSearchHistory(mContext);
                        mDataFactory.notifySearchHistoryListChange(Util.getSearchHistoryList(mContext, mDataFactory));
                        dialog.dismiss();
                    }
                });
                mBuilder.setNegativeButton(mActivity.getString(R.string.dialog_btn_cancel), new CustomDialog.Builder.DialogNegativeClickListener() {
                    @Override
                    public void onNegativeClick(CustomDialog dialog) {
                        dialog.dismiss();
                    }
                });
                mBuilder.create().show();
                break;

        }

    }



    public void setIndicator(TabLayout tabs) {
        try {
            //拿到tabLayout的mTabStrip属性
            Field mTabStripField = tabs.getClass().getDeclaredField("mTabStrip");
            mTabStripField.setAccessible(true);

            LinearLayout mTabStrip = (LinearLayout) mTabStripField.get(tabs);

            int dp10 = DensityUtils.dip2px(getContext(), 30);

            for (int i = 0; i < mTabStrip.getChildCount(); i++) {
                View tabView = mTabStrip.getChildAt(i);

                //拿到tabView的mTextView属性
                Field mTextViewField = tabView.getClass().getDeclaredField("mTextView");
                mTextViewField.setAccessible(true);

                TextView mTextView = (TextView) mTextViewField.get(tabView);

                tabView.setPadding(0, 0, 0, 0);

                //因为我想要的效果是   字多宽线就多宽，所以测量mTextView的宽度
                int width = 0;
                width = mTextView.getWidth();
                if (width == 0) {
                    mTextView.measure(0, 0);
                    width = mTextView.getMeasuredWidth();
                }

                Glog.i(TAG, "setIndicator: " + width);
                //设置tab左右间距为10dp  注意这里不能使用Padding 因为源码中线的宽度是根据 tabView的宽度来设置的
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tabView.getLayoutParams();
                params.width = width;
                params.leftMargin = dp10;
                params.rightMargin = dp10;
                tabView.setLayoutParams(params);

                tabView.invalidate();
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
