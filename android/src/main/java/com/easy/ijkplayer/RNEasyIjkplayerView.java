package com.easy.ijkplayer;

import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class RNEasyIjkplayerView extends SurfaceView implements LifecycleEventListener {

    private static final String TAG = "IJKPlayer";
    private static final String NAME_ERROR_EVENT = "onError";
    private static final String NAME_INFO_EVENT = "onInfo";
    private static final String NAME_COMPLETE_EVENT = "onComplete";
    private static final String NAME_PROGRESS_UPDATE_EVENT = "onProgressUpdate";
    private static final String NAME_LOAD_PROGRESS_UPDATE_EVENT = "onLoadProgressUpdate";
    private static final String NAME_PREPARE_EVENT = "onPrepared";
    public static final int PROGRESS_UPDATE_INTERVAL_MILLS = 500;
    private IjkMediaPlayer mIjkPlayer;
    public static int mDuration;
    public static int mAutoPlay = 0;
    public static WritableMap size = Arguments.createMap();
    private String mCurrUrl;
    private boolean mManualPause;
    private boolean mManualStop;
    private View parentView;
    private Handler mHandler = new Handler();
    boolean isProgressUpdateRunnableRunning = false;
    private Runnable progressUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIjkPlayer == null || mDuration == 0) {
                isProgressUpdateRunnableRunning = false;
                return;
            }
            long currProgress = mIjkPlayer.getCurrentPosition();
            int mCurrProgress = (int) Math.ceil((currProgress * 1.0f)/1000);
            WritableMap data = new WritableNativeMap();
            data.putInt("progress", mCurrProgress);
            sendEvent(NAME_PROGRESS_UPDATE_EVENT, data);
            mHandler.postDelayed(progressUpdateRunnable, PROGRESS_UPDATE_INTERVAL_MILLS);
        }
    };

    public void startProgressUpdateRunnableRunning() {
        if (isProgressUpdateRunnableRunning) return;
        isProgressUpdateRunnableRunning = true;
        mHandler.post(progressUpdateRunnable);
    }

    public void stopProgressUpdateRunnableRunning() {
        isProgressUpdateRunnableRunning = false;
        mHandler.removeCallbacks(progressUpdateRunnable);
    }

    public RNEasyIjkplayerView(ReactContext reactContext, View parentView) {
        super(reactContext);
        this.parentView = parentView;
        reactContext.addLifecycleEventListener(this);
        initIjkMediaPlayer();
        initSurfaceView();
        initIjkMediaPlayerListener();
    }

    public void dispatchInfoEvent(int infoCode, String info) {
        WritableMap data = new WritableNativeMap();
        data.putInt("infoCode", infoCode);
        data.putString("info", info);
        sendEvent(NAME_INFO_EVENT, data);
    }

    public void dispatchLoadStateEvent(String loadState) {
        WritableMap data = new WritableNativeMap();
        data.putString("loadState", loadState);
        data.putInt("currentPlaybackTime", (int)mIjkPlayer.getCurrentPosition() / 1000);
        sendEvent(NAME_LOAD_PROGRESS_UPDATE_EVENT, data);
    }

    private void initSurfaceView() {
        this.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.i(TAG, "surface created");
                mIjkPlayer.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.i(TAG, "surface changed");
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                Log.i(TAG, "surface destroyed");
            }
        });
    }

    private void initIjkMediaPlayer() {
        mIjkPlayer = new IjkMediaPlayer();
    }


    private void initIjkMediaPlayerListener() {
        mIjkPlayer.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                mDuration = (int)Math.ceil(mIjkPlayer.getDuration()/1000);
                startProgressUpdateRunnableRunning();
                WritableMap data = new WritableNativeMap();
                WritableMap size = new WritableNativeMap();
                data.putInt("duration", mDuration);
                size.putInt("width", mIjkPlayer.getVideoWidth());
                size.putInt("height", mIjkPlayer.getVideoHeight());
                data.putMap("size", size);
                sendEvent(NAME_PREPARE_EVENT, data);
                if (mAutoPlay == 1) {
                    dispatchInfoEvent(0, "playing");
                }
            }
        });

        mIjkPlayer.setOnSeekCompleteListener(new IMediaPlayer.OnSeekCompleteListener() {
            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                dispatchLoadStateEvent("playable");
                if (isPlaying()) {
                    dispatchInfoEvent(0, "playing");
                }
            }
        });

        mIjkPlayer.setOnVideoSizeChangedListener(new IMediaPlayer.OnVideoSizeChangedListener() {
            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int width, int height, int i2, int i3) {
                Log.i(TAG, "width:" + width + " height:" + height);
                size.putInt("width", width);
                size.putInt("height", height);
                float ratioHW = height * 1.0f / width;
            }
        });

        mIjkPlayer.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int infoCode, int i1) {
                switch (infoCode) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        dispatchLoadStateEvent("playable");
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        dispatchLoadStateEvent("stalled");
                        break;
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        dispatchLoadStateEvent("playable");
                        break;
                }
                dispatchInfoEvent(infoCode, null);
                return false;
            }
        });

        mIjkPlayer.setOnErrorListener(new IMediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int errorCode, int i1) {
                sendEvent(NAME_ERROR_EVENT, "infoCode", "" + errorCode);
                return false;
            }
        });

        mIjkPlayer.setOnBufferingUpdateListener(new IMediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {

            }
        });

        mIjkPlayer.setOnCompletionListener(new IMediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                dispatchInfoEvent(0, "complete");
                sendEvent(NAME_COMPLETE_EVENT, "complete", "1");
            }
        });
    }

    private void sendEvent(String eventName, String paramName, String paramValue) {
        WritableMap event = Arguments.createMap();
        event.putString(paramName, "" + paramValue);
        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                parentView.getId(),
                eventName,
                event);
    }

    private void sendEvent(String eventName, WritableMap data) {
        ReactContext reactContext = (ReactContext) getContext();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                parentView.getId(),
                eventName,
                data);
    }


    public void setMAutoPlay(int autoPlay) {
        mAutoPlay = autoPlay;
    }

    public int getMAutoPlay() {
        return mAutoPlay;
    }

    public void seekTo(long progress) {
        if (mIjkPlayer != null) {
            dispatchLoadStateEvent("stalled");
            mIjkPlayer.seekTo(progress);
        }
    }

    public void restart(String url) {
        stop();
        setDataSource(url);
        resetSurfaceView();
    }

    public void resetSurfaceView() {
        this.setVisibility(SurfaceView.GONE);
        this.setVisibility(SurfaceView.VISIBLE);
    }

    public void start() {
        startProgressUpdateRunnableRunning();
        if (mIjkPlayer != null) { //已经初始化
            if (mIjkPlayer.isPlaying()) return;
            if (mManualPause) { //手动点击暂停
                mIjkPlayer.start();
                if (mIjkPlayer.isPlayable()) {
                    dispatchInfoEvent(0, "playing");
                } else {
                    dispatchInfoEvent(0, "loading");
                }
            } else { //第一次播放
                mIjkPlayer.prepareAsync();
            }
            mManualPause = false;
        } else {
            setDataSource(mCurrUrl);
            initIjkMediaPlayerListener();
            initSurfaceView();
            resetSurfaceView();
            mIjkPlayer.prepareAsync();
            mManualStop = false;
        }
    }

    public void pause() {
        if (mIjkPlayer != null) {
            dispatchInfoEvent(0, "pause");
            mIjkPlayer.pause();
            mManualPause = true;
            stopProgressUpdateRunnableRunning();
        }
    }

    public void stop() {
        if (mIjkPlayer != null) {
            dispatchInfoEvent(0, "stop");
            mIjkPlayer.stop();
            mIjkPlayer.reset();
            mIjkPlayer = null;
            mManualStop = true;
            stopProgressUpdateRunnableRunning();
        }
    }

    public boolean isPlaying() {
        if (mIjkPlayer != null) {
            return mIjkPlayer.isPlaying();
        }
        return false;
    }

    public void setDataSource(String url) {
        try {
            if (mIjkPlayer == null) initIjkMediaPlayer();
            mIjkPlayer.setDataSource(url);
            mCurrUrl = url;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHostResume() {
        Log.i(TAG, "onHostResume");
        if (!mManualPause) {
            Log.i(TAG, "exec start");
            mIjkPlayer.start();
            startProgressUpdateRunnableRunning();
        }
    }

    @Override
    public void onHostPause() {
        Log.i(TAG, "onHostPause");
        mIjkPlayer.pause();
        stopProgressUpdateRunnableRunning();
    }

    @Override
    public void onHostDestroy() {
        Log.i(TAG, "onHostDestroy");
        mIjkPlayer.stop();
        mIjkPlayer.release();
        stopProgressUpdateRunnableRunning();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onHostDestroy();
    }
}
