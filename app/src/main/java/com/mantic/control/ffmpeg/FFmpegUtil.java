package com.mantic.control.ffmpeg;

import android.annotation.SuppressLint;
import android.util.Log;

import java.io.File;

/**
 * author: jayson
 * blog: http://blog.csdn.net/jia4525036
 * time: 2018/4/24.
 * desc: ffmpeg工具：拼接命令行处理音视频
 */

public class FFmpegUtil {
    /**
    * 使用ffmpeg命令行进行音频转码
    * @param srcFile 源文件
    * @param targetFile 目标文件（后缀指定转码格式）
    * @return 转码后的文件
    */
    public static String[] transformAudio(String srcFile, String targetFile){
        String transformAudioCmd = "ffmpeg -i %s -acodec copy %s";
        transformAudioCmd = String.format(transformAudioCmd, srcFile, targetFile);
        return transformAudioCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行音频剪切
     * @param srcFile 源文件
     * @param startTime 剪切的开始时间(单位为秒)
     * @param duration 剪切时长(单位为秒)
     * @param targetFile 目标文件
     * @return 剪切后的文件
     */
    @SuppressLint("DefaultLocale")
    public static  String[] cutAudio(String srcFile, int startTime, int duration, String targetFile){
        String cutAudioCmd = "ffmpeg -i %s -ss %d -t %d -acodec copy %s";
        cutAudioCmd = String.format(cutAudioCmd, srcFile, startTime, duration, targetFile);
        return cutAudioCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行音频合并
     * @param srcFile 源文件
     * @param appendFile 待追加的文件
     * @param targetFile 目标文件
     * @return 合并后的文件
     */
    public static  String[] concatAudio(String srcFile, String appendFile, String targetFile){
        String concatAudioCmd = "ffmpeg -i concat:%s|%s -acodec copy %s";
        File file = new File(targetFile);
        if (file.exists()){
            file.delete();
        }
        concatAudioCmd = String.format(concatAudioCmd, srcFile, appendFile, targetFile);
        return concatAudioCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行音频混合
     * @param srcFile 源文件
     * @param mixFile 待混合文件
     * @param targetFile 目标文件
     * @return 混合后的文件
     */
    public static  String[] mixAudio(String srcFile, String mixFile, String targetFile){
        String mixAudioCmd = "ffmpeg -i %s -i %s -filter_complex amix=inputs=2:duration=first -strict -2 %s";
        mixAudioCmd = String.format(mixAudioCmd, srcFile, mixFile, targetFile);
        return mixAudioCmd.split(" ");//以空格分割为字符串数组
    }
    //混音公式：value = sample1 + sample2 - (sample1 * sample2 / (pow(2, 16-1) - 1))


    /**
     * 使用ffmpeg命令行进行音视频合成
     * @param videoFile 视频文件
     * @param audioFile 音频文件
     * @param duration 视频时长
     * @param muxFile 目标文件
     * @return 合成后的文件
     */
    @SuppressLint("DefaultLocale")
    public static  String[] mediaMux(String videoFile, String audioFile, int duration, String muxFile){
        //-t:时长  如果忽略音视频时长，则把"-t %d"去掉
        String mixAudioCmd = "ffmpeg -i %s -i %s -t %d %s";
        mixAudioCmd = String.format(mixAudioCmd, videoFile, audioFile, duration, muxFile);
        return mixAudioCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行抽取音频
     * @param srcFile 原文件
     * @param targetFile 目标文件
     * @return 抽取后的音频文件
     */
    public static  String[] extractAudio(String srcFile, String targetFile){
        //-vn:video not
        String mixAudioCmd = "ffmpeg -i %s -acodec copy -vn %s";
        mixAudioCmd = String.format(mixAudioCmd, srcFile, targetFile);
        return mixAudioCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行抽取视频
     * @param srcFile 原文件
     * @param targetFile 目标文件
     * @return 抽取后的视频文件
     */
    public static  String[] extractVideo(String srcFile, String targetFile){
        //-an audio not
        String mixAudioCmd = "ffmpeg -i %s -vcodec copy -an %s";
        mixAudioCmd = String.format(mixAudioCmd, srcFile, targetFile);
        return mixAudioCmd.split(" ");//以空格分割为字符串数组
    }


    /**
     * 使用ffmpeg命令行进行视频转码
     * @param srcFile 源文件
     * @param targetFile 目标文件（后缀指定转码格式）
     * @return 转码后的文件
     */
    public static String[] transformVideo(String srcFile, String targetFile){
//        String transformVideoCmd = "ffmpeg -i %s -r 25 -b 200 -s 1080x720 %s";
        String transformVideoCmd = "ffmpeg -i %s %s";
        transformVideoCmd = String.format(transformVideoCmd, srcFile, targetFile);
        return transformVideoCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行视频剪切
     * @param srcFile 源文件
     * @param startTime 剪切的开始时间(单位为秒)
     * @param duration 剪切时长(单位为秒)
     * @param targetFile 目标文件
     * @return 剪切后的文件
     */
    @SuppressLint("DefaultLocale")
    public static  String[] cutVideo(String srcFile, int startTime, int duration, String targetFile){
        String cutVideoCmd = "ffmpeg -i %s -ss %d -t %d %s";
        cutVideoCmd = String.format(cutVideoCmd, srcFile, startTime, duration, targetFile);
        return cutVideoCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行视频截图
     * @param srcFile 源文件
     * @param size 图片尺寸大小
     * @param targetFile 目标文件
     * @return 截图后的文件
     */
    public static  String[] screenShot(String srcFile, String size, String targetFile){
        String screenShotCmd = "ffmpeg -i %s -f image2 -t 0.001 -s %s %s";
        screenShotCmd = String.format(screenShotCmd, srcFile, size, targetFile);
        return screenShotCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行给视频添加水印
     * @param srcFile 源文件
     * @param waterMark 水印文件路径
     * @param targetFile 目标文件
     * @return 添加水印后的文件
     */
    public static  String[] addWaterMark(String srcFile, String waterMark, String targetFile){
        String waterMarkCmd = "ffmpeg -i %s -i %s -filter_complex overlay=0:0 %s";
        waterMarkCmd = String.format(waterMarkCmd, srcFile, waterMark, targetFile);
        return waterMarkCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行视频转成Gif动图
     * @param srcFile 源文件
     * @param startTime 开始时间
     * @param duration 截取时长
     * @param targetFile 目标文件
     * @return Gif文件
     */
    @SuppressLint("DefaultLocale")
    public static  String[] generateGif(String srcFile, int startTime, int duration, String targetFile){
        //String screenShotCmd = "ffmpeg -i %s -vframes %d -f gif %s";
        String screenShotCmd = "ffmpeg -i %s -ss %d -t %d -s 320x240 -f gif %s";
        screenShotCmd = String.format(screenShotCmd, srcFile, startTime, duration, targetFile);
        return screenShotCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行屏幕录制
     * @param size 视频尺寸大小
     * @param recordTime 录屏时间
     * @param targetFile 目标文件
     * @return 屏幕录制文件
     */
    @SuppressLint("DefaultLocale")
    public static  String[] screenRecord(String size, int recordTime, String targetFile){
        //-vd x11:0,0 指录制所使用的偏移为 x=0 和 y=0
        //String screenRecordCmd = "ffmpeg -vcodec mpeg4 -b 1000 -r 10 -g 300 -vd x11:0,0 -s %s %s";
        String screenRecordCmd = "ffmpeg -vcodec mpeg4 -b 1000 -r 10 -g 300 -vd x11:0,0 -s %s -t %d %s";
        screenRecordCmd = String.format(screenRecordCmd, size, recordTime, targetFile);
        Log.i("VideoHandleActivity", "screenRecordCmd=" + screenRecordCmd);
        return screenRecordCmd.split(" ");//以空格分割为字符串数组
    }

    /**
     * 使用ffmpeg命令行进行图片合成视频
     * @param srcFile 源文件
     * @param targetFile 目标文件(mpg格式)
     * @return 合成的视频文件
     */
    @SuppressLint("DefaultLocale")
    public static  String[] pictureToVideo(String srcFile, String targetFile){
        //-f image2：代表使用image2格式，需要放在输入文件前面
        String combineVideo = "ffmpeg -f image2 -r 1 -i %simg#d.jpg -vcodec mpeg4 %s";
        combineVideo = String.format(combineVideo, srcFile, targetFile);
        combineVideo = combineVideo.replace("#", "%");
        Log.i("VideoHandleActivity", "combineVideo=" + combineVideo);
        return combineVideo.split(" ");//以空格分割为字符串数组
    }
    @SuppressLint("DefaultLocale")
    public static String[] volumeChange(String srcFile,float volume,String targetFile){
        String changeVolumeCmd="ffmpeg -i %s -af volume=%f %s";
        changeVolumeCmd=String.format(changeVolumeCmd,srcFile,volume,targetFile);
        Log.e("FFmpeg", "volumeChange: "+changeVolumeCmd );
        return changeVolumeCmd.split(" ");
    }
}
