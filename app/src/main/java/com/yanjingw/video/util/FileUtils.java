/*
 * <copyright file="FileUtils.java" company="Qihoo 360 Corporation">
 * Copyright (c) Qihoo 360 Corporation. All rights reserved.
 * </copyright>
 */

package com.yanjingw.video.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static final String ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "video_yanjingw";
    public static final String ASSETS_PATH = "video_sample";

    public static void copyFiles(Context context) {
        File ads = new File(ROOT);
        if (!ads.exists()) {
            ads.mkdirs();
        }

        String assetsInPath = ASSETS_PATH;
        String faceModelDir = ROOT;
        copyAssetsFiles(context, false, assetsInPath, faceModelDir);
    }

    /**
     *
     * @param context
     * @param inPath
     * @param outPath
     * @return
     */

    /**
     * 从assets目录下拷贝整个文件夹，不管是文件夹还是文件都能拷贝
     *
     * @param context 上下文
     * @param isCover 如果文件存在，是否覆蓋老文件
     * @param inPath  文件目录，要拷贝的目录
     * @param outPath 目标文件夹位置如：/sdcrad/mydir
     */
    public static boolean copyAssetsFiles(Context context, boolean isCover, String inPath, String outPath) {
        String[] fileNames = null;
        try {// 获得Assets一共有几多文件
            fileNames = context.getAssets().list(inPath);
        } catch (IOException e1) {
            e1.printStackTrace();
            return false;
        }
        if (fileNames.length > 0) {//如果是目录
            File fileOutDir = new File(outPath);
            if (fileOutDir.isFile()) {
                boolean ret = fileOutDir.delete();
                if (!ret) {
                }
            }
            if (!fileOutDir.exists()) { // 如果文件路径不存在
                if (!fileOutDir.mkdirs()) { // 创建文件夹
                    return false;
                }
            }
            for (String fileName : fileNames) { //递归调用复制文件夹
                String inDir = inPath;
                String outDir = outPath + File.separator;
                if (!inPath.equals("")) { //空目录特殊处理下
                    inDir = inDir + File.separator;
                }
                copyAssetsFiles(context, isCover, inDir + fileName, outDir + fileName);
            }
            return true;
        } else {//如果是文件
            try {
                File fileOut = new File(outPath);
                //如果isCover是true，则删掉文件，重新copy
                if (isCover && fileOut.exists()) {
                    boolean ret = fileOut.delete();
                    if (!ret) {
                    }
                }

                //文件存在，则不需要copy
                if (fileOut.exists() && fileOut.length() > 0) {
                    return true;
                }

                boolean ret = fileOut.createNewFile();
                if (!ret) {
                }

                FileOutputStream fos = new FileOutputStream(fileOut);
                InputStream is = context.getAssets().open(inPath);
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();//刷新缓冲区
                is.close();
                fos.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}
