package site.metacoding.white.domain;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository // ioc 등록
public class UserRepository {
  // DI // 스프링이 시작될 때 ioc 컨테이너에 들어있음(httpsession, entityManager)
  private final EntityManager em;

  public User save(User user) {
    // Persistence : Context 에 영속화 시키시 -> 자동 flush (트랜잭션 종료시)
    log.debug("디버그 : " + user.getId()); // 영속화 전
    em.persist(user);
    log.debug("디버그 : " + user.getId()); // 영속화 후 (db와 동기화된다.)
    return user;
  }

  public User findByUsername(String username) {
    return em.createQuery("select u from User u where u.username=:username", User.class)
        .setParameter("username", username)
        .getSingleResult();
  }

  public User findById(Long id) {
    return em.createQuery("select u from User u where u.id=:id", User.class)
        .setParameter("id", id)
        .getSingleResult();
  }

}
