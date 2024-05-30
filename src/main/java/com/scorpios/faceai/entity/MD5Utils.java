package com.scorpios.faceai.entity;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密/验证工具类
 *
 */
public class MD5Utils {
    static String[] random = {
        "13", "20", "01", "03"
    };
    /**
     * 使用MD5算法对指定的明文字符串和盐值进行加密，返回加密后的字符串。
     *
     * @param plainText 需要加密的明文字符串。
     * @param saltValue 盐值，用于增加加密的复杂度和防撞撞性。
     * @return 加密后的字符串。如果加密过程中发生异常，则返回null。
     */
    public static String MD5(String plainText, String saltValue) {
        try {
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 使用指定的字节更新摘要
            md.update(plainText.getBytes());
            md.update(saltValue.getBytes());

            // digest()最后确定返回md5 hash值，返回值为8位字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值。1 固定值
            return new BigInteger(1,  md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 验证用户登录的合法性。
     *
     * @param userName 用户名，字符串类型。
     * @param password 密码，字符串类型。
     * @param md5 给定的密码经过MD5加密后的字符串。
     * @return 返回一个布尔值，如果给定的md5与用户名和密码经过MD5加密后得到的字符串相等，则返回true，否则返回false。
     */
    public static boolean valid(String userName,String password, String md5) {
        return md5.equals(getMd5(userName, password));
    }
    /**
     * 获取经过MD5加密处理的字符串。
     * @param userName 用户名，用于增强加密的随机性。
     * @param password 需要加密的密码。
     * @return 返回经过两次MD5加密并截取部分字符后转换为整数，再与随机字符串进行一次MD5加密的结果。
     */
    public static String getMd5(String userName,String password){
        String md5 = MD5(password, userName);
        if (md5 != null) {
            // 从基础MD5字符串的最后两位提取出一个整数
            String md5_ls = md5.substring(md5.length() - 2);
            int md5_li = Integer.parseInt(md5_ls, 16);
            // 使用这个整数作为索引，从一个预定义的数组中选择一个字符串，与基础MD5字符串再次进行MD5加密
            md5 = MD5(md5, random[md5_li%4]);
        }
        return md5;
    }
    /**
     * 测试
     * @param args
     */
    public static void main(String[] args) {
        String md5 = getMd5("admin", "111111");
        System.out.println(valid("admin", "111111", md5));
    }
}
