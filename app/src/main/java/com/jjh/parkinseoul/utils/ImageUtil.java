package com.jjh.parkinseoul.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.jjh.parkinseoul.R;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by JJH on 2016-09-20.
 */
public class ImageUtil {

    public static final float IMAGE_MAX_SIZE = 500.0f;

    /**
     * 이미지 경로 Uri => Bitmap
     */
    public static Bitmap getBitmapFromUri(Context context, Uri imageUri){
        int imageOrientation = -1;

        try {
            //이미지 Orientation 구하기
            imageOrientation = getExifOrientation(context, imageUri);
        }catch(Exception e){
            e.printStackTrace();
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2; //사이즈 1/2로 조절
        Bitmap dest = null;
        try {
            Bitmap src = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(imageUri), null, options);
            float scale = 1;
            int originWidth = src.getWidth();
            int originHeight = src.getHeight();
            if(originWidth > originHeight){
                if(originWidth > IMAGE_MAX_SIZE){
                    scale = IMAGE_MAX_SIZE / originWidth;
                }
            }else{
                if(originHeight > IMAGE_MAX_SIZE){
                    scale = IMAGE_MAX_SIZE / originHeight;
                }
            }
            //사이즈 한번 더 조절
            dest = src.createScaledBitmap(src,(int)(originWidth * scale), (int)(originHeight * scale), true);

            if(imageOrientation > 0){
                dest = getRotatedBitmap(dest, imageOrientation);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return dest;
    }

    /**
     * Bitmap => Base64
     */
    public static String convertBase64FromBitmap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress( Bitmap.CompressFormat.JPEG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
    }

    /**
     * Base64 String을 ImageView에 표시
     */
    public static boolean setBase64ToImageView(ImageView imageView, String base64){
        if(DisplayUtil.isEmptyStr(base64)){
            imageView.setVisibility(View.GONE);
            return false;
        }else{
            imageView.setVisibility(View.VISIBLE);
            byte[] image = Base64.decode(base64,Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(image,0,image.length));
            return true;
        }
    }

    public static int getExifOrientation(Context context, Uri imageUri){

        String imagePath = getPathFromUri(context, imageUri);

        int degree = 0;
        ExifInterface exif = null;

        try
        {
            exif = new ExifInterface(imagePath);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (exif != null)
        {
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

            if (orientation != -1)
            {
                // We only recognize a subset of orientation tag values.
                switch(orientation)
                {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        degree = 90;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        degree = 180;
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        degree = 270;
                        break;
                }

            }
        }

        return degree;
    }

    public static String getPathFromUri(Context context, Uri uri){
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();

        return path;
    }

    public static Bitmap getRotatedBitmap(Bitmap bitmap, int degrees)
    {
        if ( degrees != 0 && bitmap != null )
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
            try
            {
                Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != b2)
                {
                    bitmap.recycle();
                    bitmap = b2;
                }
            }
            catch (OutOfMemoryError ex)
            {
                // We have no memory to rotate. Return the original bitmap.
            }
        }

        return bitmap;
    }
}
