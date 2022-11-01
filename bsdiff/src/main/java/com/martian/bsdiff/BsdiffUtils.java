package com.martian.bsdiff;

public class BsdiffUtils {

    // Used to load the 'bsdiff' library on application startup.
    static {
        System.loadLibrary("bsdiff");
    }

    //生成差分包
    public static native int diff(String oldFile, String newFile, String patchFile);


    public static native int patch(String oldFile, String patchFile, String newFile);
}