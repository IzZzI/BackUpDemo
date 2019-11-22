package com.test.backupdemo.util;

import android.util.Log;

import java.io.File;

/**
 * 文件工具类
 * Created by wcy on 2016/1/3.
 */
public class FileUtils {


    public static String mkdirs(String dir) {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    /**
     * 删除文件夹下的文件解决open failed: EBUSY (Device or resource busy)
     *
     * @param file
     */
    public static void RecursionDeleteFileReName(File file) {
        if (file.isFile()) {
            File toFile = new File(file.getAbsolutePath() + System.currentTimeMillis());
            String fileName = file.getAbsolutePath();
            file.renameTo(toFile);
            boolean isDelete = toFile.delete();
            Log.e("FileUtils", "删除文件 " + fileName + ": " + isDelete);
            return;
        }
        if (file.isDirectory()) {
            if (file.getAbsolutePath().contains("AdFileDownLoad") || file.getAbsolutePath().contains("AdLogs")) {
                return;
            }
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                if (file.getAbsolutePath().contains("Android")) {
                    return;
                }
                File toDir = new File(file.getAbsolutePath() + System.currentTimeMillis());
                String fileName = file.getAbsolutePath();
                file.renameTo(toDir);
                boolean isDelete = toDir.delete();
                Log.e("FileUtils", "删除空文件夹 " + fileName + ": " + isDelete);
                return;
            }
            for (File f : childFile) {
                RecursionDeleteFileReName(f);
            }
            if (file.getAbsolutePath().contains("Android")) {
                return;
            }
            File toDir = new File(file.getAbsolutePath() + System.currentTimeMillis());
            String fileName = file.getAbsolutePath();
            file.renameTo(toDir);
            boolean isDelete = toDir.delete();
            Log.e("FileUtils", "删除文件夹 " + fileName + ": " + isDelete);
        }
    }

    /**
     * 删除文件夹下的文件解决open failed: EBUSY (Device or resource busy)
     *
     * @param file
     */
    public static void RecursionDeleteAllFileReName(File file) {
        if (file.isFile()) {
            File toFile = new File(file.getAbsolutePath() + System.currentTimeMillis());
            String fileName = file.getAbsolutePath();
            file.renameTo(toFile);
            boolean isDelete = toFile.delete();
            Log.e("FileUtils", "删除文件 " + fileName + ": " + isDelete);
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length == 0) {
                File toDir = new File(file.getAbsolutePath() + System.currentTimeMillis());
                String fileName = file.getAbsolutePath();
                file.renameTo(toDir);
                boolean isDelete = toDir.delete();
                Log.e("FileUtils", "删除空文件夹 " + fileName + ": " + isDelete);
                return;
            }
            for (File f : childFile) {
                RecursionDeleteAllFileReName(f);
            }
            File toDir = new File(file.getAbsolutePath() + System.currentTimeMillis());
            String fileName = file.getAbsolutePath();
            file.renameTo(toDir);
            boolean isDelete = toDir.delete();
            Log.e("FileUtils", "删除文件夹 " + fileName + ": " + isDelete);
        }
    }


}
