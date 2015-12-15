package edu.xjtu.cm.ex1;


/**
 * Created by Yh on 2015/12/3.
 */
public class Ex1_1 {
    //计算括号内的值
    public static double calPart(int n) {
        double a = 4.0 / (Math.pow(16, n) * (8 * n + 1)) - 2.0 / (Math.pow(16, n) * (8 * n + 4))
                - 1.0 / (Math.pow(16, n) * (8 * n + 5)) - 1.0 / (Math.pow(16, n) * (8 * n + 6));
        return a;
    }

    //计算和式 参数为n
    public static double sum(int n) {
        double sum = 0;
        for(int i = n; i >= 0; i--) {
            double a = calPart(i);
            sum += a;
        }
        return sum;
    }

    public static int getPrecision(int num) {
        int result = 0;
        while(true) {
            double precision = calPart(result++);
            if(precision < 0.5 * Math.pow(10, 0 - num)) {
                break;
            }
        }
        return result - 1;
    }

    public static void main(String[] args) {
        final int n = 11;
        final int precision = getPrecision(n);
        double result = sum(precision);
        System.out.println("S = " + result);
    }
}
