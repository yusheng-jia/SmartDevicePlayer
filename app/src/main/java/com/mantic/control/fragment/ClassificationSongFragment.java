package com.mantic.control.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.adapter.ClassificationSongAdapter;
import com.mantic.control.decoration.classification.StickyHeadContainer;
import com.mantic.control.decoration.classification.StickyItemDecoration;
import com.mantic.control.entiy.ClassificationBean;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.Util;
import com.mantic.control.widget.TitleBar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lin on 2018/1/16.
 * 分类歌单
 */

public class ClassificationSongFragment extends BaseSlideFragment implements TitleBar.OnButtonClickListener{
    private TitleBar tb_classification_song;
    private RecyclerView rv_classification_song;
    private ClassificationSongAdapter mClassificationSongAdapter;
    protected List<ClassificationBean> classificationBeanList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private StickyHeadContainer shc_container;
    private TextView tv_classification_title_name;

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        mClassificationSongAdapter = new ClassificationSongAdapter(mContext);
        rv_classification_song.setAdapter(mClassificationSongAdapter);
        rv_classification_song.addItemDecoration(new StickyItemDecoration(shc_container, ClassificationSongAdapter.ITEM_VIEW_TYPE_TITLE));
        initClassificationData();
        shc_container.setDataCallback(new StickyHeadContainer.DataCallback() {
            @Override
            public void onDataChange(int pos) {
                String category = classificationBeanList.get(pos).getCategory();
                tv_classification_title_name.setText(category);
            }
        });
    }

    @Override
    protected void initView(View view) {
        super.initView(view);
        shc_container = (StickyHeadContainer) view.findViewById(R.id.shc_container);
        tv_classification_title_name = (TextView) shc_container.findViewById(R.id.tv_classification_title_name);
        tb_classification_song = (TitleBar) view.findViewById(R.id.tb_classification_song);
        tb_classification_song.setOnButtonClickListener(this);
        rv_classification_song = (RecyclerView) view.findViewById(R.id.rv_classification_song);
        gridLayoutManager = new GridLayoutManager(mContext, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return classificationBeanList.get(position).isTitle()? 3 : 1;
            }
        });
        rv_classification_song.setLayoutManager(gridLayoutManager);
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_classification_song;
    }

    @Override
    public void onLeftClick() {
        if(mActivity instanceof FragmentEntrust){
            ((FragmentEntrust) getActivity()).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {

    }

    private void initClassificationData() {
        try {
            String json = Util.getJson(mContext, "classification.json");
            JSONObject singerJson = new JSONObject(json);
            JSONArray array = singerJson.optJSONArray("result");
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.optJSONObject(i);
                String names = jsonObject.optString("names");
                String[] classificationNames = names.split(",");
                ClassificationBean bean = new ClassificationBean();
                bean.setClassificationName(jsonObject.optString("category"));
                bean.setCategory(jsonObject.optString("category"));
                bean.setTitle(true);
                classificationBeanList.add(bean);
                for (int j = 0; j < classificationNames.length; j++) {
                    ClassificationBean bean1 = new ClassificationBean();
                    bean1.setCategory(jsonObject.optString("category"));
                    bean1.setClassificationName(classificationNames[j]);
                    bean1.setTitle(false);
                    classificationBeanList.add(bean1);
                    Glog.i("ClassificationSongAdapter", bean1.getClassificationName());
                }
            }
            mClassificationSongAdapter.setClassificationBeanList(classificationBeanList);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
