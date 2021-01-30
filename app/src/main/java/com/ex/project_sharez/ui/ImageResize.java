package com.ex.project_sharez.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.ex.project_sharez.BuildConfig;
import com.ex.project_sharez.R;

import java.io.File;
import java.io.FileOutputStream;

public class ImageResize {

    // ========== Bitmap을 받아 섬네일 크기로 크롭하는 것.(상품이미지->섬네일) ==========
    public static Bitmap cropLeftRight_Rate(Bitmap bitmap, int width, int height){
        int oriWidth = bitmap.getWidth();   //원본 너비
        int oriHeight = bitmap.getHeight(); //원본 높이
        int newWidth, newHeight;

        // 비율에 맞춰서(viewpager size) 변경
        if(oriWidth <= oriHeight) {
            newWidth = width;
            newHeight = width * oriHeight / oriWidth;
        } else {
            newWidth = width * oriWidth / oriHeight;
            newHeight = width;
        }

        // 이미지파일 리사이징
        Bitmap resize = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        Bitmap newbitmap = cropCenterBitmap(resize,width,width);// 상하or좌우 크롭

        return newbitmap;
    }

    // ========== image.center 기준으로 상하좌우 크롭 ==========
    public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
        if (src == null)
            return null;

        //기본 크기
        int width = src.getWidth();
        int height = src.getHeight();

        //높이, 너비 중 하나의 옵션만 사용할 때
        if (w == -1) w = width;
        if (h == -1) h = height;

        if (width < w && height < h)
            return src;

        int x = 0;
        int y = 0;

        if (width > w) x = (width - w) / 2;
        if (height > h) y = (height - h) / 2;

        int cw = w; // crop width
        int ch = h; // crop height

        if (w > width) cw = width;
        if (h > height) ch = height;

        return Bitmap.createBitmap(src, x, y, cw, ch);//x,y 좌표부터 cw, ch크기의 비트맵을 생성.
    }

    // ========== Bitmap을 받아 Viewpager 클릭시 보여줄 이미지 사이즈 조절 ==========
    public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
        int oriWidth = bitmap.getWidth();   //원본 너비
        int oriHeight = bitmap.getHeight(); //원본 높이
        int deWidth, deHeight;

        //비율에 맞춰서(device size) 변경
        if(oriWidth <= oriHeight) {
            deWidth = height * oriWidth / oriHeight;
            deHeight = height;
        } else {
            deWidth = width;
            deHeight = width * oriHeight / oriWidth;
        }
        //Log.d("TAG", "cropLeftRight_Rate: w=" + deWidth + " h=" + deHeight);
        Bitmap resize = Bitmap.createScaledBitmap(bitmap, deWidth, deHeight, true);
        return resize;
    }

    /*// ========== uri를 받아, 높이는 비율로 줄이고 좌우를 크롭하는 것.(상품이미지->섬네일) ==========
    public static Uri cropLeftRight_Rate(Context context, Uri uri, int width, int height){

        String filename = "crop_width_"+width+".png";
        File storageDir = new File(Environment.getExternalStorageDirectory() +
                context.getString(R.string.title_notice));//내장메모리/폴더명 에 저장
        File image = new File(storageDir,filename);

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);//원본 이미지

            int oriWidth = bitmap.getWidth(); //원본 너비
            int oriHeight = bitmap.getHeight(); //원본 높이

            //비율에 맞춰서(높이기준) 변경
            int newWIdth = height * oriWidth / oriHeight;
            int newHeight = height;

            //리사이징
            Bitmap resize = Bitmap.createScaledBitmap(bitmap, newWIdth, newHeight, true);

            Bitmap newbitmap = cropCenterBitmap(resize,width,-1);//좌우 크롭

            if(image.exists()) {//만약 이미 이 파일이 존재한다면(1회 이상 했다면)
                image.delete();//중복되므로 과거 파일은 삭제
                image = new File(storageDir, filename);//그리고 다시 오픈
            }

            FileOutputStream outputStream = new FileOutputStream(image);
            newbitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            outputStream.close();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {//sdk 24 이상, 누가(7.0)
                uri = FileProvider.getUriForFile(context,// 7.0에서 바뀐 부분은 여기다.
                        BuildConfig.APPLICATION_ID + ".provider", image);
            } else {//sdk 23 이하, 7.0 미만
                uri = Uri.fromFile(image);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return uri;
    }

    // ========== image.center 기준으로 상하좌우 크롭 ==========
    public static Bitmap cropCenterBitmap(Bitmap src, int w, int h) {
        if (src == null)
            return null;

        //기본 크기
        int width = src.getWidth();
        int height = src.getHeight();

        //높이, 너비 중 하나의 옵션만 사용할 때
        if (w == -1) w = width;
        if (h == -1) h = height;

        if (width < w && height < h)
            return src;

        int x = 0;
        int y = 0;

        if (width > w) x = (width - w) / 2;
        if (height > h) y = (height - h) / 2;

        int cw = w; // crop width
        int ch = h; // crop height

        if (w > width) cw = width;
        if (h > height) ch = height;

        return Bitmap.createBitmap(src, x, y, cw, ch);//x,y 좌표부터 cw, ch크기의 비트맵을 생성.
    }*/
}
