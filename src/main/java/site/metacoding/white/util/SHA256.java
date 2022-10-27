package site.metacoding.white.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Component;

// https://bamdule.tistory.com/233
@Component
public class SHA256 {

  public String encrypt(String text) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(text.getBytes());
      return bytesToHex(md.digest());
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("비밀번호 해싱이 실패했습니다.");
    }
  } // public 이니까 encrypt 메서드만 호출가능하고 encrypt에 text를 넣고 hasing

  private String bytesToHex(byte[] bytes) {
    StringBuilder builder = new StringBuilder();
    for (byte b : bytes) {
      builder.append(String.format("%02x", b));
    }
    return builder.toString();
  }

}
