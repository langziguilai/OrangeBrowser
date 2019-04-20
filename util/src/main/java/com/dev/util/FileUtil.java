package com.dev.util;

import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class FileUtil {
    /**
     * 删除文件，可以是文件或文件夹     *     * @param fileName     *            要删除的文件名     * @return 删除成功返回true，否则返回false
     */
    public static boolean delete(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) return deleteFile(fileName);
            else return deleteDirectory(fileName);
        }
    }

    /**
     * 删除单个文件     *     * @param fileName     *            要删除的文件的文件名     * @return 单个文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {

                return false;
            }
        } else {

            return false;
        }
    }

    /**
     * 删除目录及目录下的文件     *     * @param dir     *            要删除的目录的文件路径     * @return 目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String dir) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        if (!dir.endsWith(File.separator)) dir = dir + File.separator;
        File dirFile = new File(dir);
        if ((!dirFile.exists()) || (!dirFile.isDirectory())) {
            return false;
        }
        boolean flag = true;
        File[] files = dirFile.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                flag = deleteFile(file.getAbsolutePath());
                if (!flag) break;
            } else if (file.isDirectory()) {
                flag = deleteDirectory(file.getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) {
            return false;
        }
        if (dirFile.delete()) {
            return true;
        } else {
            return false;
        }
    }
    public static File getOrCreateDir(String dirname) throws Exception {
        String externalDirectory = Environment.getExternalStorageDirectory().getPath();
        File file= new File(externalDirectory+File.separator+dirname+File.separator);
        if (!file.exists()){
            file.mkdir();
        }
        return file;
    }
}