package com.yushan.cameraandphotodemo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yushan.cameraandphotodemo.utils.PictureUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btn_camera;
    private Button btn_photo;
    private Button btn_camera_to_cut;
    private Button btn_photo_to_cut;
    private ImageView iv_show;
    private Uri photoUri;
    private final int PHOTO_PICKED_WITH_DATA = 1010;
    private final int CAMERA_PICKED_WITH_DATA = 1011;
    private final int CROP_RESULT_DATA = 1012;
    private final int CUSTOM_CROP_RESULT_DATA = 1013;

    public static final String TMP_PATH = "clip_temp.jpg";
    private static final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory() + "/DCIM/Camera");// 拍摄照片存储的文件夹路径
    private String picPath;
    private File picFile;
    private Bitmap picBitmap;
    private BitmapDrawable picDrawable;
    private File capturefile;
    private Button btn_system_cut_photo;
    private String doNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView(){
        btn_camera = (Button)findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(this);
        btn_photo = (Button)findViewById(R.id.btn_photo);
        btn_photo.setOnClickListener(this);
        btn_camera_to_cut = (Button)findViewById(R.id.btn_camera_to_cut);
        btn_camera_to_cut.setOnClickListener(this);
        btn_photo_to_cut = (Button)findViewById(R.id.btn_photo_to_cut);
        btn_photo_to_cut.setOnClickListener(this);
        btn_system_cut_photo = (Button)findViewById(R.id.btn_system_cut_photo);
        btn_system_cut_photo.setOnClickListener(this);
        iv_show = (ImageView)findViewById(R.id.iv_show);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_camera:
                doNext = "2show";
                startCamera();
                break;
            case R.id.btn_photo:
                doNext = "2show";
                startPhoto();
                break;
            case R.id.btn_camera_to_cut:
                doNext = "2custom_cut";
                startCamera();
                break;
            case R.id.btn_photo_to_cut:
                doNext = "2custom_cut";
                startPhoto();
                break;
            case R.id.btn_system_cut_photo:
                doNext = "2system_cut";
                startPhoto();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA:
                Uri uri_ca = null;
                if (data != null && data.getData() != null) {
                    uri_ca = data.getData();
                } else {
                    if (photoUri != null) {
                        uri_ca = photoUri;
                    } else {
                        return;
                    }
                }
                picPath = PictureUtil.getPath(this, uri_ca);
                showPicture(uri_ca,picPath,doNext);
                break;
            case CAMERA_PICKED_WITH_DATA:

                // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                picPath = Environment.getExternalStorageDirectory() + "/" + TMP_PATH;
                showPicture(null,picPath,doNext);
                break;
            case CROP_RESULT_DATA:
                picPath = data.getStringExtra("crop_image_path");
                showPicture(null,picPath,"2show");
                break;
            case CUSTOM_CROP_RESULT_DATA:

                Uri uri = null;
                if (data != null && data.getData() != null) {
                    uri = data.getData();
                } else {
                    if (photoUri != null) {
                        uri = photoUri;
                    } else {
                        return;
                    }
                }
                picPath = PictureUtil.getPath(this, uri);
                if (picPath == null) {
                    return;
                }
                showPicture(null,picPath,"2show");
                break;
        }

    }

    private void startPhoto(){
        // 手机相册
        Intent intent = new Intent();
        // 开启Pictures画面Type设定为image
        intent.setType("image/*");
        // 使用Intent.ACTION_GET_CONTENT这个Action
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // 取得相片后返回本画面
        startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
    }

    private void startCamera(){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(
                    Environment.getExternalStorageDirectory(), TMP_PATH)));
            startActivityForResult(intent, CAMERA_PICKED_WITH_DATA);
        } else {
            Toast.makeText(MainActivity.this, "没有SD卡",Toast.LENGTH_SHORT).show();
        }
    }

    private void startImageZoom(Uri uri) {
        int dp = 500;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);//输出是X方向的比例
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高，切忌不要再改动下列数字，会卡死
        intent.putExtra("outputX", dp);//输出X方向的像素
        intent.putExtra("outputY", dp);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("return-data", true);//设置是否返回数据
        startActivityForResult(intent, CUSTOM_CROP_RESULT_DATA);
    }


    /**
     * 裁剪图片的Activity
     */
    private void startCropImageActivity(String path) {
        ClipImageActivity.startActivity(this, path, CROP_RESULT_DATA);
    }
    /**
     * 获取文件路径
     */
    public File getFilePath(String filePath, String fileName) {
        File file = null;
        makeFileDirectory(filePath);
        try {
            file = new File(filePath + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }
    /**
     * 创建文件
     */
    public static void makeFileDirectory(String filePath) {
        File file = null;
        try {
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 通过相机回传图片的文件名
     */
    @SuppressLint("SimpleDateFormat")
    private String getPhotoFileName() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "'IMG'_yyyyMMdd_HHmmss");
        return dateFormat.format(date) + ".jpg";
    }

    private void showPicture(Uri uri,String filePath, String doNext){
        switch (doNext){
            case "2show":
                picFile = new File(filePath);
                picBitmap = PictureUtil.getBitmapThumbnail(picFile);
                picDrawable = new BitmapDrawable(picBitmap);
                if (picDrawable != null) {
                    iv_show.setImageDrawable(picDrawable);
                }
                break;
            case "2custom_cut":
                startCropImageActivity(filePath);
                break;
            case "2system_cut":
                startImageZoom(uri);
                break;
        }
    }
}
