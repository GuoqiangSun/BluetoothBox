package com.bazooka.bluetoothbox.utils;

/**
 * @author 尹晓童
 *         邮箱：yinxtno1@yeah.net
 *         时间：2018/1/17
 *         作用：
 */

public class HexUtils {

    public static String bytes2hex(byte[] bytes) {
        final String HEX = "0123456789abcdef";
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            // 取出这个字节的高4位，然后与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt((b >> 4) & 0x0f));
            // 取出这个字节的低位，与0x0f与运算，得到一个0-15之间的数据，通过HEX.charAt(0-15)即为16进制数
            sb.append(HEX.charAt(b & 0x0f));
        }

        return sb.toString();
    }

    /**
     * long 转 字节数组
     * @param num
     * @return
     */
    public static byte[] longToByteArray(long num) {
        byte[] result = new byte[8];
        result[0] = (byte) (num >>> 56);// 取最高8位放到0下标
        result[1] = (byte) (num >>> 48);// 取最高8位放到0下标
        result[2] = (byte) (num >>> 40);// 取最高8位放到0下标
        result[3] = (byte) (num >>> 32);// 取最高8位放到0下标
        result[4] = (byte) (num >>> 24);// 取最高8位放到0下标
        result[5] = (byte) (num >>> 16);// 取次高8为放到1下标
        result[6] = (byte) (num >>> 8); // 取次低8位放到2下标
        result[7] = (byte) (num); // 取最低8位放到3下标
        return result;
    }

}
