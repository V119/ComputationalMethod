package edu.xjtu.cm.ex5;

import java.io.*;

/**
 * Created by Yh on 2015/12/12.
 */
public class Ex5_1 {

    //获取Fileinfo中的内容
    public static FileInfo getFileInfo(File file) throws IOException {
        long startTime = System.currentTimeMillis();
        FileInfo info = new FileInfo();
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        for(int i = 0; i < 3; i++) {
            byte[] bt = new byte[32];
            raf.read(bt, 0, 4);
            switch (i) {
                case 0 : info.setId(byteArrayToInt(bt));
                case 1 : info.setVar(byteArrayToInt(bt));
                case 2 : info.setId1(byteArrayToInt(bt));
            }

        }
        raf.close();
        long endTime = System.currentTimeMillis();
        System.out.println("\t读取FileInfo所用时间：" + (endTime - startTime) + "ms");

        return info;
    }

    //获取headInfo内容
    public static HeadInfo getHeadInfo(File file) throws IOException {
        long startTime = System.currentTimeMillis();

        HeadInfo info = new HeadInfo();
        RandomAccessFile raf = new RandomAccessFile(file, "r");
        raf.seek(8);
        for(int i = 0; i < 3; i++) {
            byte[] bt = new byte[32];
            raf.read(bt, 0, 4);
            switch (i) {
                case 0 : info.setN(byteArrayToInt(bt));
                case 1 : info.setQ(byteArrayToInt(bt));
                case 2 : info.setP(byteArrayToInt(bt));
            }

        }
        raf.close();
        long endTime = System.currentTimeMillis();
        System.out.println("\t读取HeadInfo文件所用时间：" + (endTime - startTime) + "ms");

        return info;
    }

    //获取非压缩数据部分，包括右端系数部分
    public static float[][] getNonCompressMatrix(File file, int order) throws IOException {
        long startTime = System.currentTimeMillis();
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file))); //BufferedInputStream使读取大数据文件更快
        dis.skipBytes(20);

        float[][] result = new float[order][];
        for(int i = 0; i < order; i++) {
            result[i] = new float[order + 1];
        }
        //获取数据部分
        for(int i = 0; i < order; i++) {
            for(int j = 0; j < order; j++) {
                byte[] bt = new byte[32];
                dis.read(bt, 0, 4);
                result[i][j] = Float.intBitsToFloat(byteArrayToInt(bt));
            }
        }
        //获取右端系数部分
        for(int i = 0; i < order; i++) {
            byte[] bt = new byte[32];
            dis.read(bt, 0, 4);
            result[i][order] = Float.intBitsToFloat(byteArrayToInt(bt));
        }

        dis.close();
        long endTime = System.currentTimeMillis();
        System.out.println("\t读取数据所用时间：" + (endTime - startTime) + "ms");
        return result;
    }

    //计算结果
    public static float[] getResult(float[][] matrix, int q, int p) throws Exception{
        final int order = matrix.length;
        float[] result = new float[order];

        long startTime = System.currentTimeMillis();
        //消去过程
        for(int i = 0; i < order; i++) {
            if(0 == matrix[i][i]) {
                throw new Exception("矩阵为奇异矩阵!!!");
            }

            //计算Uij
            for(int j = i; j < getMin(order, i + q + 1); j++) {
                float sumU = 0;

                for(int t = getMax(0, i - p, j - q); t < i; t++) {
                    sumU = sumU + matrix[i][t] * matrix[t][j];
                }
                matrix[i][j] = matrix[i][j] - sumU;
            }

            //计算Lij
            for(int k = i + 1; k < getMin(order, i + p + 1); k++) {
                float sumL = 0;

                for(int t = getMax(0, k - p, i - q); t < i; t++) {
                    sumL += matrix[k][t] * matrix[t][i];
                }
                matrix[k][i] = (matrix[k][i] - sumL) / matrix[i][i];
            }
        }

        //回代过程
        //追的过程
        for(int i = 1; i < order; i++) {
            float sum = 0;
            for(int j = 0; j < i; j++) {
                sum += matrix[i][j] * matrix[j][order];
            }
            matrix[i][order] = matrix[i][order] - sum;
        }

        //赶的过程
        result[order - 1] = matrix[order - 1][order] / matrix[order - 1][order - 1];
        for(int k = order - 2; k >= 0; k--) {
            float sum = 0;
            for(int i = k + 1; i < order; i++) {
                sum += matrix[k][i] * result[i];
            }
//            result[k] = matrix[k][order]  / matrix[k][k] - sum;
            result[k] = (matrix[k][order] - sum) / matrix[k][k];
        }
        long endTime = System.currentTimeMillis();
        System.out.println("\t计算所用时间：" + (endTime - startTime) + "ms");

        return result;
    }

    //获取压缩格式数据，包括右端系数矩阵
    public static float[][] getCompressMatrix(File file, HeadInfo headInfo) throws IOException {
        int n = headInfo.getN();
        int p = headInfo.getP();
        int q = headInfo.getQ();
        long startTime = System.currentTimeMillis();
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file))); //BufferedInputStream使读取大数据文件更快
        dis.skipBytes(20);

        //初始化数组
        float[][] result = new float[n][];
        for(int i = 0; i < n; i++) {
            result[i] = new float[p + q + 2];
        }

        //读取数据部分
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < p + q + 1; j++) {
                byte[] bt = new byte[32];
                dis.read(bt, 0, 4);
                result[i][j] = Float.intBitsToFloat(byteArrayToInt(bt));
            }
        }

        //读取右端系数部分
        for(int i = 0; i < n; i ++) {
            byte[] bt = new byte[32];
            dis.read(bt, 0, 4);
            result[i][p + q + 1] = Float.intBitsToFloat(byteArrayToInt(bt));
        }
        long endTime = System.currentTimeMillis();
        System.out.println("\t读取数据所用时间：" + (endTime - startTime) + "ms");

        return result;
    }

    //在压缩矩阵中获取未压缩时的数据
    public static float[] getCompressResult(float[][] matrix, int q, int p) throws Exception {
        final int n = matrix.length;
        float[] result = new float[n];

        long startTime = System.currentTimeMillis();

        //消去过程
        for (int i = 0; i < n; i++) {
            if (0 == matrix[i][p]) {
                throw new Exception("矩阵为奇异矩阵!!!");
            }

            //计算Uij
            for (int j = i; j < getMin(n, i + q + 1); j++) {
                float sum = 0;
                for (int t = getMax(0, i - p, j - q); t < i; t++) {
                    sum += matrix[i][t + p - i] * matrix[t][j + p - t];
                }
                matrix[i][j + p - i] = matrix[i][j + p - i] - sum;
            }

            //计算Lij
            for (int k = i + 1; k < getMin(n, i + p + 1); k++) {
                float sum = 0;
                for (int t = getMax(0, k - p, i - q); t < i; t++) {
                    sum += matrix[k][t + p - k] * matrix[t][i + p - t];
                }
                matrix[k][i + p - k] = (matrix[k][i + p - k] - sum) / matrix[i][p];
            }

        }

        //回代过程
        //追的过程
        for(int i = 1; i < n; i++) {
            float sum = 0;
            for(int j = 0; j < i; j++) {
                if(j + p < i) {
                    continue;
                }
                sum += matrix[i][j + p - i] * matrix[j][p + q + 1];
            }
            matrix[i][p + q + 1] = matrix[i][p + q + 1] - sum;
        }

        //赶的过程
        result[n - 1] = matrix[n - 1][p + q + 1] /matrix[n - 1][p];
        for(int k = n - 2; k >= 0; k--) {
            float sum = 0;
            for(int i = k + 1; i < n; i++) {
                if(i > k + q) {
                    continue;
                }
                sum += matrix[k][i + p - k] * result[i];
            }
            result[k] = (matrix[k][p + q + 1] - sum) /matrix[k][p];
        }
        long endTime = System.currentTimeMillis();
        System.out.println("\t计算所用时间：" + (endTime - startTime) + "ms");
        return result;
    }

    public static void calculate(File file) throws IOException {
        FileInfo fileInfo = getFileInfo(file);
        HeadInfo head = getHeadInfo(file);
        float[] result = new float[head.getN()];
        if(fileInfo.getId() == 0xF1E1D1A0 && fileInfo.getVar() == 0x102) {
            float[][] data = getNonCompressMatrix(file, head.getN());
            try {
                result = getResult(data,head.getQ(), head.getP());
                for(int i = 0; i < result.length; i++) {
                    System.out.println(result[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if(fileInfo.getId() == 0xF1E1D1A0 && fileInfo.getVar() == 0x202) {
            float[][] data = getCompressMatrix(file, head);
            try {
                result = getCompressResult(data,head.getQ(), head.getP());
                for(int i = 0; i < result.length; i++) {
                    System.out.println(result[i]);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        File file = new File("src/edu/xjtu/cm/ex5/data/dat54.dat");
        calculate(file);
    }

    //转换文件中数据的高低位
    public static int byteArrayToInt(byte[] b) {
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int shift = i * 8;
            value += (b[i] & 0x000000FF) << shift;
        }
        return value;
    }

    //求三个数的最大值
    public static int getMax(int a, int b, int c) {
        int max = a > b ? a : b;
        return max > c ? max : c;
    }

    //求两个数的最小值
    public static int getMin(int a, int b) {
        return a < b ? a : b;
    }
}

//文件信息
class FileInfo {
    private int id; //数据文件标识
    private int var; //数据文件版本号
    private int id1; //备用标识

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVar() {
        return var;
    }

    public void setVar(int var) {
        this.var = var;
    }

    public int getId1() {
        return id1;
    }

    public void setId1(int id1) {
        this.id1 = id1;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "id=" + Integer.toHexString(id) +
                ", var=" + Integer.toHexString(var) +
                ", id1=" + Integer.toHexString(id1) +
                '}';
    }
}

//文件头信息
class HeadInfo {
    private int n; //方程组的阶数
    private int q; //带状矩阵上带宽
    private int p; //带状矩阵下带宽

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getQ() {
        return q;
    }

    public void setQ(int q) {
        this.q = q;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    @Override
    public String toString() {
        return "HeadInfo{" +
                "n=" + n +
                ", q=" + q +
                ", p=" + p +
                '}';
    }
}
