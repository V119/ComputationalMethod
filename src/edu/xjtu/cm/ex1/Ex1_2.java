package edu.xjtu.cm.ex1;

import java.math.BigDecimal;

/**
 * Created by Yh on 2015/12/5.
 */
public class Ex1_2 {
    //计算和式内的值
    public static BigDecimal calPart(int n) {
        int precision = 32;
        BigDecimal result = new BigDecimal(4).divide(new BigDecimal(Math.pow(16.0, n) * (8 * n + 1)), precision, BigDecimal.ROUND_DOWN).
                subtract(new BigDecimal(2).divide(new BigDecimal(Math.pow(16.0, n) * (8 * n + 4)), precision, BigDecimal.ROUND_DOWN)).
                subtract(new BigDecimal(1).divide(new BigDecimal(Math.pow(16.0, n) * (8 * n + 5)), precision, BigDecimal.ROUND_DOWN)).
                subtract(new BigDecimal(1).divide(new BigDecimal(Math.pow(16.0, n) * (8 * n + 6)), precision, BigDecimal.ROUND_DOWN));

        return result;
    }

    //计算在给定精度情况下需要循环的次数
    public static int getPrecision(int num) {
        final BigDecimal precision = new BigDecimal(0.5 * Math.pow(10, 0 - num));
        int result = 0;
        while(true) {
            BigDecimal part = calPart(result++);
            //如果该部分比precision小，就认为满足精度
            if(part.max(precision).compareTo(precision) == 0) {
                break;
            }
        }
        return result - 1;
    }

    public  static BigDecimal sum(int n) {
        BigDecimal result = new BigDecimal(0);
        for(int i = n; i >= 0; i--) {
            BigDecimal part = calPart(i);
            result = result.add(part);
        }

        return result;
    }

    public static String getResult(int n){
        int p = getPrecision(n);
        BigDecimal result = sum(p);
        return result.toEngineeringString();
    }

    public static void main(String[] args) {
        System.out.println("S = " + getResult(30));
    }
}
