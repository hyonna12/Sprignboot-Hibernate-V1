package site.metacoding.white.service;

import javax.management.RuntimeErrorException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.white.domain.User;
import site.metacoding.white.domain.UserRepository;

@RequiredArgsConstructor
@Service
public class UserService {
  private final UserRepository userRepository;

  @Transactional // 트랜잭션을 붙이지 않으면 영속화 되어 있는 객체가 flush가 안됨
  public void save(User user) {
    userRepository.save(user);
  }// 트랜잭션 종료

  @Transactional(readOnly = true)
  public User login(User user) { // 외부에서 받음 user
    User userPS = userRepository.findByUsername(user.getUsername());
    // db에 select 한 user
    if (userPS.getPassword().equals(user.getPassword())) {
      return userPS;
    } else {
      throw new RuntimeException("아이디 혹은 패스워드가 잘못 입력되었습니다."); // IllegalArgumentException();
    }
  }

}
