/*
 * <copyright file="VideoRenderer.java" company="Qihoo 360 Corporation">
 * Copyright (c) Qihoo 360 Corporation. All rights reserved.
 * </copyright>
 */

package com.yanjingw.video.renderer;

import android.content.Context;
import android.view.SurfaceHolder;

import com.yanjingw.video.util.LogUtil;

/**
 * VideoRenderer class.
 */
public class VideoRenderer {

    private Context mContext;
    private final MyMediaPlayer myMediaPlayer;


    public VideoRenderer(Context context, SurfaceHolder surfaceHolder, String videoPath) {
        mContext = context;
        SurfaceHolder mSurfaceHolder = surfaceHolder;
        mSurfaceHolder.addCallback(mSurfaceHolderCallback);
        myMediaPlayer = new MyMediaPlayer(mSurfaceHolder);
        myMediaPlayer.setVideoPath(videoPath);
    }

    private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.i("surfaceDestroyed");

            // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
            myMediaPlayer.stop();


        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.i("surfaceCreated");

            myMediaPlayer.play();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.i("surfaceChanged");
        }

    };

}