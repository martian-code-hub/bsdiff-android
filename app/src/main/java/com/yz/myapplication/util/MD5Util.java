package com.yz.myapplication.util;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by：Martian
 * on 2022/7/18
 */
public class MD5Util {

    private static MappedByteBuffer[] mappedByteBuffers;
    private static int bufferCount;

    /**
     * 获取单个文件的MD5值！
     * @param file
     * @return
     * 解决首位0被省略问题
     * 解决超大文件问题
     */

    public static String getFileMD5(File file) {

        StringBuffer stringbuffer = null;
        try {
            char[] hexDigits = { '0', '1', '2','3', '4','5', '6','7','8', '9', 'a','b' ,'c', 'd','e', 'f' };
            FileInputStream in = new FileInputStream(file);
            FileChannel ch = in.getChannel();

            long fileSize = ch.size();
            bufferCount = (int) Math.ceil((double) fileSize / (double) Integer.MAX_VALUE);
            mappedByteBuffers = new MappedByteBuffer[bufferCount];

            long preLength = 0;
            long regionSize = Integer.MAX_VALUE;
            for (int i = 0; i < bufferCount; i++) {
                if (fileSize - preLength < Integer.MAX_VALUE) {
                    regionSize = fileSize - preLength;
                }
                mappedByteBuffers[i] = ch.map(FileChannel.MapMode.READ_ONLY, preLength, regionSize);
                preLength += regionSize;
            }

            MessageDigest messagedigest = MessageDigest.getInstance("MD5");

            for(int i = 0; i < bufferCount; i ++){
                messagedigest.update(mappedByteBuffers[i]);
            }
            byte[] bytes = messagedigest.digest();
            int n = bytes.length;
            stringbuffer = new StringBuffer(2 * n);
            for (int l = 0; l < n; l++) {
                byte bt = bytes[l];
                char c0 = hexDigits[(bt & 0xf0) >> 4];
                char c1 = hexDigits[bt & 0xf];
                stringbuffer.append(c0);
                stringbuffer.append(c1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (stringbuffer !=null)?stringbuffer.toString():"";

    }


    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */

//    public static String getFileMD5(File file) {
//        if (!file.isFile()) {
//            return null;
//        }
//        MessageDigest digest = null;
//        FileInputStream in = null;
//        byte buffer[] = new byte[1024];
//        int len;
//        try {
//            digest = MessageDigest.getInstance("MD5");
//            in = new FileInputStream(file);
//            while ((len = in.read(buffer, 0, 1024)) != -1) {
//                digest.update(buffer, 0, len);
//            }
//            in.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        }
//        BigInteger bigInt = new BigInteger(1, digest.digest());
//        return bigInt.toString(16);
//    }

    /**
     * 获取文件夹中文件的MD5值
     *
     * @param file
     * @param listChild ;true递归子目录中的文件
     * @return
     */
    public static Map<String, String> getDirMD5(File file, boolean listChild) {
        if (!file.isDirectory()) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        String md5;
        File files[] = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory() && listChild) {
                map.putAll(getDirMD5(f, listChild));
            } else {
                md5 = getFileMD5(f);
                if (md5 != null) {
                    map.put(f.getPath(), md5);
                }
            }
        }
        return map;
    }
}
