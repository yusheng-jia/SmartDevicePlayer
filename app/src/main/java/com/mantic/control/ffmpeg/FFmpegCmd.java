package com.mantic.control.ffmpeg;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/4/24.
 * desc:
 */

public class FFmpegCmd {
    public interface OnHandleListener{
        void onBegin();
        void onEnd(int result);
    }

    static{
        System.loadLibrary("media-handle");
        System.loadLibrary("ffmpeg");
    }

    //开子线程调用native方法进行音频处理
    public static void execute(final String[] commands, final OnHandleListener onHandleListener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(onHandleListener != null){
                    onHandleListener.onBegin();
                }
                //调用ffmpeg进行处理
                int result = handle(commands);
                if(onHandleListener != null){
                    onHandleListener.onEnd(result);
                }
            }
        }).start();
    }
    private native static int handle(String[] commands);




}
