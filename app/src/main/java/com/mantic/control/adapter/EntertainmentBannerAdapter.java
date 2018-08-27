package com.mantic.control.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.api.entertainment.bean.BannerListBean;
import com.mantic.control.utils.GlideImgManager;
import com.mantic.control.utils.Glog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hejunlin on 2016/8/25.
 */
public class EntertainmentBannerAdapter extends PagerAdapter {
    private ImageView iv_entertainment_banner;
    private Activity mActivity;
    private List<BannerListBean> bannerListBeanList = new ArrayList<>();


    public EntertainmentBannerAdapter(Activity activity, List<BannerListBean> bannerListBeanList) {
        mActivity = activity;
        if (null != bannerListBeanList) {
            this.bannerListBeanList = bannerListBeanList;
        } else {
            this.bannerListBeanList.add(new BannerListBean());
        }
    }

    public void setBannerListBeanList(List<BannerListBean> bannerListBeanList) {
        this.bannerListBeanList = bannerListBeanList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return bannerListBeanList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup view, int position, Object object) {
        view.removeView((View) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        BannerListBean bannerListBean = bannerListBeanList.get(position);
        View view = LayoutInflater.from(mActivity.getApplicationContext()).inflate(R.layout.entertainment_banner_item, container, false);
        iv_entertainment_banner = (ImageView) view.findViewById(R.id.iv_entertainment_banner);
        GlideImgManager.glideLoaderCircle(mActivity, bannerListBean.getMantic_image(), R.drawable.fragment_channel_detail_cover,
                R.drawable.fragment_channel_detail_cover, iv_entertainment_banner);
        iv_entertainment_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtils.showShortSafe("点击的position = " + position);
            }
        });
        Bitmap image = ((BitmapDrawable) iv_entertainment_banner.getDrawable()).getBitmap();
        Bitmap newimage = getRoundCornerImage(image, 50);
        ImageView imageView2 = new ImageView(view.getContext());
        imageView2.setImageBitmap(newimage);
        newimage.recycle();
        newimage = null;
        image.recycle();
        image = null;
        container.addView(view);
        return view;
    }

    public Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels) {
        Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);
        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, null, rect, paint);
        return roundConcerImage;
    }
}
