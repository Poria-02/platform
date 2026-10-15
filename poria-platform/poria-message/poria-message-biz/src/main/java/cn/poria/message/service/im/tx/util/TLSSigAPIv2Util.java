package cn.poria.message.service.im.tx.util;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import java.util.zip.Deflater;


public class TLSSigAPIv2Util {
    final private long sdkappid;
    final private String key;

    public TLSSigAPIv2Util(long sdkappid, String key) {
        this.sdkappid = sdkappid;
        this.key = key;
    }

    /**
     * 【功能说明】用于签发 IM 服务中必须要使用的 UserSig 鉴权票据
     * <p>
     * 【参数说明】
     *
     * @param userid - 用户id，限制长度为32字节，只允许包含大小写英文字母（a-zA-Z）、数字（0-9）及下划线和连词符。
     * @param expire - UserSig 票据的过期时间，单位是秒，比如 86400 代表生成的 UserSig 票据在一天后就无法再使用了。
     * @return usersig -生成的签名
     */
    public String genUserSig(String userid, long expire) {

        long currTime = System.currentTimeMillis() / 1000;

        JSONObject sigDoc = JSONUtil.createObj();
        sigDoc.set("TLS.ver", "2.0");
        sigDoc.set("TLS.identifier", userid);
        sigDoc.set("TLS.sdkappid", sdkappid);
        sigDoc.set("TLS.expire", expire);
        sigDoc.set("TLS.time", currTime);

        String sig = hmacsha256(userid, currTime, expire);
        if (sig.length() == 0) {
            return "";
        }
        sigDoc.set("TLS.sig", sig);
        Deflater compressor = new Deflater();
        compressor.setInput(sigDoc.toString().getBytes(StandardCharsets.UTF_8));
        compressor.finish();
        byte[] compressedBytes = new byte[2048];
        int compressedBytesLength = compressor.deflate(compressedBytes);
        compressor.end();
        return (new String(Base64URL.base64EncodeUrl(Arrays.copyOfRange(compressedBytes,
                0, compressedBytesLength)))).replaceAll("\\s*", "");
    }

    private String hmacsha256(String identifier, long currTime, long expire) {
        String contentToBeSigned = "TLS.identifier:" + identifier + "\n"
                + "TLS.sdkappid:" + sdkappid + "\n"
                + "TLS.time:" + currTime + "\n"
                + "TLS.expire:" + expire + "\n";
        try {
            byte[] byteKey = key.getBytes(StandardCharsets.UTF_8);
            Mac hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA256");
            hmac.init(keySpec);
            byte[] byteSig = hmac.doFinal(contentToBeSigned.getBytes(StandardCharsets.UTF_8));
            return (Base64.getEncoder().encodeToString(byteSig)).replaceAll("\\s*", "");
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return "";
        }
    }
}
