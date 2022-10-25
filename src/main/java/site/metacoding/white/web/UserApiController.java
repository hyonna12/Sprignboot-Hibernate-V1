package site.metacoding.white.web;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.metacoding.white.domain.User;
import site.metacoding.white.dto.ResponseDto;
import site.metacoding.white.dto.SessionUser;
import site.metacoding.white.dto.UserReqDto;
import site.metacoding.white.dto.UserRespDto;
import site.metacoding.white.dto.UserReqDto.JoinReqDto;
import site.metacoding.white.dto.UserReqDto.LoginReqDto;
import site.metacoding.white.dto.UserRespDto.JoinRespDto;
import site.metacoding.white.service.UserService;

@RequiredArgsConstructor
@RestController
// json을 리턴할거임
public class UserApiController {
  private final UserService userService;
  private final HttpSession session;

  @PostMapping("/join")
  public ResponseDto<?> save(@RequestBody JoinReqDto joinReqDto) {
    JoinRespDto joinRespDTO = userService.save(joinReqDto);
    return new ResponseDto<>(1, "ok", joinRespDTO);
  }

  @PostMapping("/login")
  public ResponseDto<?> login(@RequestBody LoginReqDto loginReqDto) {
    SessionUser sessionUser = userService.login(loginReqDto);
    session.setAttribute("sessionUser", sessionUser);
    return new ResponseDto<>(1, "ok", sessionUser);
  }

}
