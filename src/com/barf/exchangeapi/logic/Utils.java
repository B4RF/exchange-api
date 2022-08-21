package com.barf.exchangeapi.logic;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Utils {
  private static final String ERROR_NULL_INPUT = "Input can't be null.";
  private static final String ERROR_NULL_ARRAYS = "Given arrays can't be null.";

  private static final String UTF8 = "UTF-8";

  private static final String SHA256 = "SHA-256";
  private static final String HMAC_SHA256 = "HmacSHA256";
  private static final String HMAC_SHA512 = "HmacSHA512";

  public static byte[] base64Decode(final String input) {
    return Base64.getDecoder().decode(input);
  }

  public static String base64Encode(final byte[] data) {
    return Base64.getEncoder().encodeToString(data);
  }

  public static byte[] concatArrays(final byte[] a, final byte[] b) {
    if ((a == null) || (b == null)) {
      throw new IllegalArgumentException(Utils.ERROR_NULL_ARRAYS);
    }

    final byte[] concat = new byte[a.length + b.length];
    for (int i = 0; i < concat.length; i++) {
      concat[i] = i < a.length ? a[i] : b[i - a.length];
    }

    return concat;
  }

  public static byte[] hmacSha256(final byte[] key, final byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
    final Mac mac = Mac.getInstance(Utils.HMAC_SHA256);
    mac.init(new SecretKeySpec(key, Utils.HMAC_SHA256));
    return mac.doFinal(message);
  }

  public static byte[] hmacSha512(final byte[] key, final byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
    final Mac mac = Mac.getInstance(Utils.HMAC_SHA512);
    mac.init(new SecretKeySpec(key, Utils.HMAC_SHA512));
    return mac.doFinal(message);
  }

  public static byte[] sha256(final String message) throws NoSuchAlgorithmException {
    final MessageDigest md = MessageDigest.getInstance(Utils.SHA256);
    return md.digest(Utils.stringToBytes(message));
  }

  public static byte[] stringToBytes(final String input) {
    if (input == null) {
      throw new IllegalArgumentException(Utils.ERROR_NULL_INPUT);
    }

    return input.getBytes(Charset.forName(Utils.UTF8));
  }

  public static String urlEncode(final String input) throws UnsupportedEncodingException {
    return URLEncoder.encode(input, Utils.UTF8);
  }
}
