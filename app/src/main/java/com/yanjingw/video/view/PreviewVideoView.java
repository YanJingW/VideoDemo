package com.yanjingw.video.view;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.yanjingw.video.util.PlayerUtils;

import static android.content.ContentValues.TAG;

/**
 * Created by wangyanjing on 2018/3/19.
 */

public class PreviewVideoView extends SurfaceView {


    //默认宽高比16:9
    private int defaultWidthProportion = 16;
    private int defaultHeightProportion = 9;

    private float mediaPlayerX;
    private float mediaPlayerY;
    private int playerViewW;
    private int playerViewH;

    public PreviewVideoView(Context context) {
        super(context);
    }

    public PreviewVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mediaPlayerX = this.getX();
        mediaPlayerY = this.getY();
        playerViewW = this.getWidth();
        playerViewH = this.getHeight();
    }

    public void updateLayout(Configuration newConfig, int videoWidth, int videoHeight) {

        int screenWidth = PlayerUtils.getScreenWidth(this.getContext());
        int screenHeight = PlayerUtils.getScreenHeight(this.getContext());
        ViewGroup.LayoutParams layoutParams = this.getLayoutParams();

        //newConfig.orientation获得当前屏幕状态是横向或者竖向
        //Configuration.ORIENTATION_PORTRAIT 表示竖向
        //Configuration.ORIENTATION_LANDSCAPE 表示横屏
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            //计算视频的大小16：9
            layoutParams.width = screenWidth;
            layoutParams.height = screenWidth * defaultHeightProportion / defaultWidthProportion;
            this.setX(mediaPlayerX);
            this.setY(mediaPlayerY);
        }
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            layoutParams.width = screenWidth;
            layoutParams.height = screenHeight;

            this.setX(0);
            this.setY(0);
        }
        this.setLayoutParams(layoutParams);

        playerViewW = screenWidth;
        playerViewH = layoutParams.height;


        //适配大小
        fitVideoSize(videoWidth, videoHeight);

    }

    public void fitVideoSize(int videoWidth, int videoHeight) {
        int parentWidth = playerViewW;
        int parentHeight = playerViewH;
        int screenWidth = PlayerUtils.getScreenWidth(this.getContext());
        int screenHeight = PlayerUtils.getScreenHeight(this.getContext());

        //判断视频宽高和父布局的宽高
        int surfaceViewW;
        int surfaceViewH;
        if ((float) videoWidth / (float) videoHeight > (float) parentWidth / (float) parentHeight) {
            surfaceViewW = parentWidth;
            surfaceViewH = videoHeight * surfaceViewW / videoWidth;
        } else {
            surfaceViewH = parentHeight;
            surfaceViewW = videoWidth * parentHeight / videoHeight;
        }

        Log.i(TAG, "fitVideoSize---" +
                "videoWidth：" + videoWidth + ",videoHeight:" + videoHeight +
                ",parentWidth:" + parentWidth + ",parentHeight:" + parentHeight +
                ",screenWidth:" + screenWidth + ",screenHeight:" + screenHeight +
                ",surfaceViewW:" + surfaceViewW + ",surfaceViewH:" + surfaceViewH
        );
        //改变surfaceView的大小
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = surfaceViewH;
        params.width = surfaceViewW;
        this.setLayoutParams(params);
    }


}
