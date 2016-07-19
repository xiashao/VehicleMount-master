package car.ccut.com.vehicle.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by panzhuowen on 2016/3/29.
 */
public class MD5 {

    public static String getMd5(String str) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }

        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5.digest(byteArray)) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString().toUpperCase();
    }

}
