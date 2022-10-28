package site.metacoding.white.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<User, Long> {// <entity, id type>
  // @repository 붙이지 않아도 ioc 컨테이너에 등록됨

  @Query(value = "select u from User u where u.username = :username")
  User findByUsername(@Param("username") String username);
  // findByEmail, findByGender
  // findBy 뒤에 나오는 필드가 자동으로 where 절에 걸려서 query도 작성 안해줘도 됨
  // jpa namedQuery

}
