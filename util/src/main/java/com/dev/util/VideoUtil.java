package com.dev.util;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class VideoUtil {

    /**

     * 获取视频文件截图

     *

     * @param path 视频文件的路径

     * @return Bitmap 返回获取的Bitmap

     */

    public static Bitmap getVideoThumb(String path) {
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(path);
        return  media.getFrameAtTime();

    }
    public static boolean saveVideoThumbnial(Bitmap bitmap,File file){
        try {
            bitmap.compress(Bitmap.CompressFormat.WEBP, 80, new FileOutputStream(file));
            Log.d("save video thumbnail at",file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("save video thumbnail","failed");
            return false;
        }
    }
}
