package com.mantic.control.cache;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jia on 2017/5/26.
 */

public class FileOperate {
    private Context mContext;

    public void writeDataToLocal(String json, String path, long time) {
        File cacheDir = mContext.getCacheDir();

        File file = null;
        try {
//            file = new File(cacheDir, MD5Encoder.encode(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
            //21408348372737+1000*60*60
            //dkgkawerghosrghseligs
            //sergohelshgoeshgoserhogsh
            //3238230280333333
            bufferedWriter.write(System.currentTimeMillis() + time + "\r\n");
            bufferedWriter.write(json);
            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null)
                    bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
