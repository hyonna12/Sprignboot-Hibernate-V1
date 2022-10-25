package site.metacoding.white.dto;

import lombok.Getter;
import lombok.Setter;
import site.metacoding.white.domain.User;

@Setter
@Getter
public class UserReqDto {
  private String username;
  private String password;

  @Setter
  @Getter
  public static class JoinReqDto { // 로그인 전 로직들은 전부 다 앞에 엔티티 안붙임.
    private String username;
    private String password;

    public User toEntity() {
      return User.builder().username(username).password(password).build();
    }
  }

  @Setter
  @Getter
  public static class LoginReqDto {
    private String username;
    private String password;
  }
}
