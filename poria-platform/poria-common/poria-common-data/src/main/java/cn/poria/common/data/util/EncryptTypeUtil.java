package cn.poria.common.data.util;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;

public class EncryptTypeUtil {

    private static final byte[] KEY = "3e2f4e970DF3a3eA6e6W1b6248E64b08".getBytes();

    public static String setResultEncrypt( Object columnIndex)  {
        return   SecureUtil.aes(KEY).encryptHex(JSONUtil.toJsonStr(columnIndex));
    }

    public static String getResultDecrypt( String columnIndex)  {
        return SecureUtil.aes(KEY).decryptStr(columnIndex);
    }

}
