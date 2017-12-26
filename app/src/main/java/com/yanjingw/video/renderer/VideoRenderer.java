/*
 * <copyright file="VideoRenderer.java" company="Qihoo 360 Corporation">
 * Copyright (c) Qihoo 360 Corporation. All rights reserved.
 * </copyright>
 */

package com.yanjingw.video.renderer;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;

import com.yanjingw.video.util.LogUtil;

/**
 * VideoRenderer class.
 */
public class VideoRenderer {

    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private String mVideoPath;


    public VideoRenderer(Context context, SurfaceHolder surfaceHolder, String videoPath) {
        mContext = context;
        mSurfaceHolder = surfaceHolder;
        mSurfaceHolder.addCallback(mSurfaceHolderCallback);
        mVideoPath = videoPath;
    }

    private SurfaceHolder.Callback mSurfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            LogUtil.i("surfaceDestroyed");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            LogUtil.i("surfaceCreated");
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            LogUtil.i("surfaceChanged");
        }
    };

}