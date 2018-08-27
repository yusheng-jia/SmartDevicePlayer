package com.mantic.control.fragment;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mantic.antservice.util.ToastUtils;
import com.mantic.control.R;
import com.mantic.control.activity.MainActivity;
import com.mantic.control.api.netizen.NetizenOperatorRetrofit;
import com.mantic.control.api.netizen.NetizenOperatorServiceApi;
import com.mantic.control.api.netizen.bean.NetizenUpLoadRqBean;
import com.mantic.control.api.netizen.bean.NetizenUploadRqParams;
import com.mantic.control.utils.Glog;
import com.mantic.control.utils.Utility;
import com.mantic.control.widget.ScrollEditText;
import com.mantic.control.widget.TitleBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by root on 17-4-20.
 * 上传网友歌单
 */
public class UploadSongFragment extends BaseSlideFragment implements TitleBar.OnButtonClickListener, View.OnClickListener {
    private TitleBar tb_upload_album;
    private ScrollEditText edit_upload_album_name;
    private ScrollEditText edit_upload_album_url;
    private RoundedImageView iv_selected_photo;
    private ImageView iv_select_photo;

    private Dialog mDialog;
    private LinearLayout ll_camera;
    private LinearLayout ll_select_photo;
    private LinearLayout ll_photo_cancel;


    private Uri imageUri;//相机拍照图片保存地址
    private Uri outputUri;//裁剪万照片保存地址
    private String imagePath;//打开相册选择照片的路径
    private boolean isClickCamera;//是否是拍照裁剪


    private static final int PHOTO_REQUEST_CAMERA = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int PICTURE_CUT = 3;
    private static final String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath() + "/picture/";

    private NetizenOperatorServiceApi serviceApi;
    @Override
    protected void initView(View view) {
        super.initView(view);
        tb_upload_album = (TitleBar) view.findViewById(R.id.tb_upload_album);
        edit_upload_album_name = (ScrollEditText) view.findViewById(R.id.edit_upload_album_name);
        edit_upload_album_url = (ScrollEditText) view.findViewById(R.id.edit_upload_album_url);
        iv_select_photo = (ImageView) view.findViewById(R.id.iv_select_photo);
        iv_selected_photo = (RoundedImageView) view.findViewById(R.id.iv_selected_photo);
        tb_upload_album.setOnButtonClickListener(this);
        iv_select_photo.setOnClickListener(this);


        mDialog = Utility.getDialog(mContext, R.layout.picture_select_method);
        ll_camera = (LinearLayout) mDialog.findViewById(R.id.ll_camera);
        ll_select_photo = (LinearLayout) mDialog.findViewById(R.id.ll_select_photo);
        ll_photo_cancel = (LinearLayout) mDialog.findViewById(R.id.ll_photo_cancel);
        ll_camera.setOnClickListener(this);
        ll_select_photo.setOnClickListener(this);
        ll_photo_cancel.setOnClickListener(this);

        ((MainActivity) mActivity).setAudioPlayerVisible(false);
    }

    @Override
    protected void initData(Bundle arguments) {
        super.initData(arguments);
        serviceApi = NetizenOperatorRetrofit.getInstance().create(NetizenOperatorServiceApi.class);
        File tempFile = new File(externalStorageDirectory);
        if (!tempFile.exists()) {
            tempFile.mkdirs();
        }
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.fragment_upload_song;
    }

    @Override
    public void onLeftClick() {
        if (getActivity() instanceof FragmentEntrust) {
            ((FragmentEntrust) getActivity()).popFragment(getTag());
        }
    }

    @Override
    public void onRightClick() {
        if (TextUtils.isEmpty(edit_upload_album_name.getText().toString().trim())) {
            ToastUtils.showShortSafe("请输入你的歌单名称");
            return;
        }

        if (TextUtils.isEmpty(edit_upload_album_name.getText().toString().trim())) {
            ToastUtils.showShortSafe("请输入你的歌单播放源");
            return;
        }

        if (getActivity() instanceof FragmentEntrust) {
            ((FragmentEntrust) getActivity()).popFragment(getTag());
        }

        Call<ResponseBody> responseBodyCall = serviceApi.postUploadAlbumResultQuest(createUpLoadAlbumRqBean());
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && null == response.errorBody()) {
                    ToastUtils.showShortSafe("上传歌单成功");
                    Glog.i("UploadSongFragment", response.body().toString());
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

    }


    private RequestBody createUpLoadAlbumRqBean() {
        NetizenUpLoadRqBean bean = new NetizenUpLoadRqBean();
        bean.setMethod("core.playlists.mantic_netizen_playlist_upload");
        bean.setJsonrpc("2.0");
        bean.setId(1);
        NetizenUploadRqParams params = new NetizenUploadRqParams();
        params.setPlaylist(edit_upload_album_name + ":" + edit_upload_album_url);
        bean.setParams(params);
        Gson gson = new Gson();
        String request = gson.toJson(bean);
        Glog.i("lbj", "createUpLoadAlbumRqBean: " + request);
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), request.toString());
        return body;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_select_photo:
                showPictureSelectMethod();
                break;
            case R.id.ll_camera:
                openCamera();
                mDialog.dismiss();
                break;
            case R.id.ll_select_photo:
                //动态权限
                if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    selectFromAlbum();//打开相册
                }
                mDialog.dismiss();
                break;
            case R.id.ll_photo_cancel:
                mDialog.dismiss();
                break;
        }
    }

    private void showPictureSelectMethod() {
        mDialog.show();
    }

    private void openCamera() {
        // 创建File对象，用于存储拍照后的图片
        File outputImage = new File(mContext.getExternalCacheDir(), "output_image.jpg");
        try {
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT < 24) {
            imageUri = Uri.fromFile(outputImage);
        } else {
            //Android 7.0系统开始 使用本地真实的Uri路径不安全,使用FileProvider封装共享Uri
            //参数二:fileprovider绝对路径 com.dyb.testcamerademo：项目包名
            imageUri = FileProvider.getUriForFile(mContext, "com.mantic.control.fileProvider", outputImage);
        }
        // 启动相机程序
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, PHOTO_REQUEST_CAMERA);

    }


    private boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    private void selectFromAlbum() {
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            openAlbum();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // 打开相册
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Glog.i("onActivityResult", "requestCode = " + requestCode + "  resultCode= " + resultCode);

        switch (requestCode) {
            case PHOTO_REQUEST_CAMERA://拍照
                if (resultCode == mActivity.RESULT_OK) {
                    cropPhoto(imageUri);//裁剪图片
                }
                break;
            case PICK_IMAGE_REQUEST://打开相册
                if (null == data) {
                    return;
                }
                // 判断手机系统版本号
                if (Build.VERSION.SDK_INT >= 19) {
                    // 4.4及以上系统使用这个方法处理图片
                    handleImageOnKitKat(data);
                } else {
                    // 4.4以下系统使用这个方法处理图片
                    handleImageBeforeKitKat(data);
                }
                break;
            case PICTURE_CUT://裁剪完成
                isClickCamera = true;
                Bitmap bitmap = null;
                try {
                    if (isClickCamera) {
                        bitmap = BitmapFactory.decodeStream(mContext.getContentResolver().openInputStream(outputUri));
                    } else {
                        bitmap = BitmapFactory.decodeFile(imagePath);
                    }

                    iv_selected_photo.setImageBitmap(bitmap);
                    bitmap.recycle();
                    bitmap = null;
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    // 4.4及以上系统使用这个方法处理图片 相册图片返回的不再是真实的Uri,而是分装过的Uri
    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        imagePath = null;
        Uri uri = data.getData();
        Glog.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(mContext, uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        cropPhoto(uri);
    }


    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = mContext.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        imagePath = getImagePath(uri, null);
        cropPhoto(uri);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(mContext, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }


    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        // 创建File对象，用于存储裁剪后的图片，避免更改原图
        File file = new File(mContext.getExternalCacheDir(), "crop_image.jpg");
        try {
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        outputUri = Uri.fromFile(file);
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        //裁剪图片的宽高比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("crop", "true");//可裁剪
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);//支持缩放
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//输出图片格式
        intent.putExtra("noFaceDetection", true);//取消人脸识别
        startActivityForResult(intent, PICTURE_CUT);
    }



    public void hideSoftKeyboard(EditText editText, Context context) {
        if (editText != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editText.getApplicationWindowToken(), 0);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hideSoftKeyboard(edit_upload_album_name, mContext);
        hideSoftKeyboard(edit_upload_album_url, mContext);
        ((MainActivity) mActivity).setAudioPlayerVisible(true);
    }

}
