package edu.xjtu.cm.ex5;

import java.io.*;

/**
 * Created by Yh on 2015/12/15.
 */
public class Utils {
    //该方法在计算大数据时会造成堆栈溢出
    //获取压缩矩阵的数据，包括右端矩阵
    @Deprecated
    public static float[][] getCompressData(File file, int n, int q, int p) throws IOException {
        long startTime = System.currentTimeMillis();
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file))); //BufferedInputStream使读取大数据文件更快
        dis.skipBytes(20);

        //初始化结果数组
        float[][] result = new float[n][];
        for(int i = 0; i < n; i++) {
            result[i] = new float[n+1];
        }

        //数据部分
        int i = 0;
        for( ; i < p; i++) {
            int j = 0;
            dis.skipBytes((p - i) * 4); //跳过前面无用字节
            for( ; j < q + i + 1; j++) {
                byte[] bt = new byte[32];
                dis.read(bt, 0, 4);
                result[i][j] = Float.intBitsToFloat(byteArrayToInt(bt));
            }
            //补0
            for( ; j < n; j++) {
                result[i][j] = 0;
            }
        }
        for( ; i < n - q; i++) {
            int j = 0;
            //补0；
            for( ; j < i - p; j++) {
                result[i][j] = 0;
            }
            for( ; j < i + q + 1; j++) {
                byte[] bt = new byte[32];
                dis.read(bt, 0, 4);
                result[i][j] = Float.intBitsToFloat(byteArrayToInt(bt));
            }
            //补0;
            for( ; j < n; j++) {
                result[i][j] = 0;
            }
        }
        for( ; i < n; i++) {
            int j = 0;
            for( ; j < i - p; j++) {
                result[i][j] = 0;
            }
            for( ; j < n; j++) {
                byte[] bt = new byte[32];
                dis.read(bt, 0, 4);
                result[i][j] = Float.intBitsToFloat(byteArrayToInt(bt));
            }
            //跳过无用数据
            dis.skipBytes((i + p - n + 1) * 4);
        }

        //读右端系数部分
        for(int k = 0; k < n; k++) {
            byte[] bt = new byte[32];
            dis.read(bt, 0, 4);
            result[k][n] = Float.intBitsToFloat(byteArrayToInt(bt));
        }

        return result;
    }

    public static int byteArrayToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = i * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }
}
