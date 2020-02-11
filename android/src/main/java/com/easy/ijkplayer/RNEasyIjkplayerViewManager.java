package com.easy.ijkplayer;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.view.ReactViewGroup;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class RNEasyIjkplayerViewManager extends SimpleViewManager<FrameLayout> {
    private static final String TAG = "RNEasyIjkplayerViewManager";
    private final String REACT_CLASS = "RNEasyIjkplayerView";
    private static final int COMMAND_PAUSE_ID = 1;
    private static final String COMMAND_PAUSE_NAME = "pause";
    private static final int COMMAND_PLAY_ID = 2;
    private static final String COMMAND_PLAY_NAME = "play";
    private static final int COMMAND_STOP_ID = 3;
    private static final String COMMAND_STOP_NAME = "stop";
    private static final int COMMAND_SEEK_TO_ID = 4;
    private static final String COMMAND_SEEK_TO_NAME = "seekTo";
    RNEasyIjkplayerView ijkPlayerView;

    @Nonnull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Nonnull
    @Override
    protected FrameLayout createViewInstance(@Nonnull ThemedReactContext reactContext) {
        ViewGroup.LayoutParams framelayout_params =
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        FrameLayout mFrameLayout = new FrameLayout(reactContext);
        mFrameLayout.setLayoutParams(framelayout_params);
        ijkPlayerView = new RNEasyIjkplayerView(reactContext, mFrameLayout);
        mFrameLayout.addView(ijkPlayerView);
        return mFrameLayout;
    }

//    @ReactProp(name = "url")
//    public void setUrl(RNEasyIjkplayerView ijkPlayer, String url) {
//        Log.i(TAG, "url:" + url);
//        if (ijkPlayer.isPlaying()) {
//            ijkPlayer.restart(url);
//        } else {
//            if (!url.equals("")) {
//                ijkPlayer.setDataSource(url);
//            }
//        }
//    }

    @ReactProp(name = "options")
    public void setOptions(FrameLayout v, ReadableMap options) {
        /* auto start */
        int autoPlay = 0;
        if(options.hasKey("autoPlay")){
            autoPlay = options.getInt("autoPlay");
            Log.i(TAG,"autoPlay::"+autoPlay);
            if(autoPlay == 1){
                ijkPlayerView.setMAutoPlay(1);
            }
        }
        /* url */
        if(options.hasKey("url")){
            String url = options.getString("url");
            Log.i(TAG,url);
            if (ijkPlayerView.isPlaying()) {
                Log.i(TAG,"isPlaying");
                ijkPlayerView.restart(url);
            } else {
                if (!url.equals("")) {
                    ijkPlayerView.setDataSource(url);
                    if(autoPlay == 1){
                        ijkPlayerView.start();
                    }
                }
            }
        }
    }


    @javax.annotation.Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                COMMAND_PAUSE_NAME, COMMAND_PAUSE_ID,
                COMMAND_PLAY_NAME, COMMAND_PLAY_ID,
                COMMAND_STOP_NAME, COMMAND_STOP_ID,
                COMMAND_SEEK_TO_NAME, COMMAND_SEEK_TO_ID
        );
    }

    @Override
    public void receiveCommand(@Nonnull FrameLayout root, int commandId, @javax.annotation.Nullable ReadableArray args) {
        switch (commandId) {
            case COMMAND_PAUSE_ID:
                ijkPlayerView.pause();
                break;
            case COMMAND_PLAY_ID:
                ijkPlayerView.start();
                break;
            case COMMAND_STOP_ID:
                ijkPlayerView.stop();
                break;
            case COMMAND_SEEK_TO_ID:
                int progress = args.getInt(0);
                Log.i(TAG, "seek Progress:" + progress);
                ijkPlayerView.seekTo(progress * 1000);
                break;
            default:
                break;
        }

    }

    @Nullable
    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        "onComplete",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onComplete")))
                .put(
                        "onInfo",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onInfo")))
                .put(
                        "onError",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onError")))
                .put(
                        "onPrepared",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onPrepared")))
                .put(
                        "onProgressUpdate",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onProgressUpdate")))
                .put(
                        "onLoadProgressUpdate",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onLoadProgressUpdate")))
                .build();
    }

}
