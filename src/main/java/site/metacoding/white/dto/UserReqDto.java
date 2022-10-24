package site.metacoding.white.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserReqDto {
  private String username;
  private String password;

  public static class JoinDto {
    private String username;
    private String password;

    @Setter
    @Getter
    public class ServiceDto {

    }

    private ServiceDto serviceDto;

    public void newInstance() {
      serviceDto = new ServiceDto();
    }

  }
}
