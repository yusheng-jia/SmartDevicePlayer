package com.mantic.control.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mantic.control.R;
import com.mantic.control.api.sound.MopidyRsSoundProductBean;
import com.mantic.control.utils.GlideImgManager;

import java.util.ArrayList;
import java.util.List;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/5/19.
 * desc:
 */
public class MySoundProductAdapter extends RecyclerView.Adapter<MySoundProductAdapter.ViewHolder>{
    private static final String TAG = "MySoundProductAdapter";
    private List<MopidyRsSoundProductBean.Result.Tracks> productTracks = new ArrayList<>();
    private Context mContext;
    private ProductItemClickListener itemClickListener;
    private ProductDelClickListener delClickListener;
    private RecyclerView recyclerView;
    private boolean moreFragment;
    private boolean editMode = false;

    public MySoundProductAdapter(Context context,boolean isMore){
        mContext = context;
        moreFragment = isMore;
    }

    @Override
    public MySoundProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MySoundProductAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sound_product_list_item, parent, false));

    }

    @Override
    public void onBindViewHolder(MySoundProductAdapter.ViewHolder holder, int position) {
        holder.showItem(position);
    }

    public void setData(List<MopidyRsSoundProductBean.Result.Tracks> tracks){
        productTracks = tracks;
    }

    public void setItemClickListener(ProductItemClickListener listener){
        itemClickListener = listener;
    }

    public void setDelClickListener(ProductDelClickListener listener){
        delClickListener = listener;
    }

    @Override
    public int getItemCount() {
        if (moreFragment){
            return productTracks.size();
        }else {
            if (productTracks.size()>5){
                return 5;
            }else {
                return productTracks.size();
            }
        }

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView view) {
        super.onAttachedToRecyclerView(recyclerView);
        recyclerView = view;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView view) {
        super.onDetachedFromRecyclerView(view);
        recyclerView = null;
    }

    public void setEditMode(boolean mode){
        editMode = mode;
    }


    private void deleteProduct(){

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView product_name;
        ImageView product_voice_people;
        TextView product_voice_name;
        LinearLayout ly_goto_product_play;
        private ImageView deleteView;
        public ViewHolder(final View view) {
            super(view);
            product_name = (TextView) view.findViewById(R.id.product_name);
            product_voice_people = (ImageView) view.findViewById(R.id.product_voice_people);
            product_voice_name = (TextView) view.findViewById(R.id.product_voice_name);
            ly_goto_product_play = (LinearLayout) view.findViewById(R.id.ly_goto_product_play);
            deleteView = (ImageView) view.findViewById(R.id.sound_product_delete);
//            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/DINCond-Medium.otf");
//            tv_my_channel_serial_number.setTypeface(typeface);
//            this.albumSub = (TextView) itemView.findViewById(R.id.album_sub);
//            this.albumTitle = (TextView) itemView.findViewById(R.id.album_title);
            ly_goto_product_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener!=null && !editMode){
                        int position = recyclerView.getChildAdapterPosition(view);
                        itemClickListener.onItemClick(position);
                    }
                }
            });
            deleteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (delClickListener!=null && editMode){
                        int position = recyclerView.getChildAdapterPosition(view);
                        delClickListener.onDelClick(position);
                    }
                }
            });
        }

        void showItem(int position){
            if (productTracks.size() > 0){
                MopidyRsSoundProductBean.Result.Tracks track = productTracks.get(position);
                product_name.setText(track.name);
                GlideImgManager.glideCircle(mContext,track.mantic_podcaster_avater,R.drawable.sound_people,R.drawable.sound_people,product_voice_people);
                product_voice_name.setText(track.mantic_podcaster_name);
                if (editMode){
                    deleteView.setImageResource(R.drawable.btn_audio_player_playlist_item_del);
                }else {
                    deleteView.setImageResource(R.drawable.goto_enter);
                }
            }
        }
    }

    public interface ProductItemClickListener{
        void onItemClick(int index);
    }

    public interface ProductDelClickListener{
        void onDelClick(int index);
    }
}
