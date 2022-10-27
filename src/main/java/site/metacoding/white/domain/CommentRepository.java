package site.metacoding.white.domain;

import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository // ioc 등록
public class CommentRepository {

  private final EntityManager em;

  public Comment save(Comment comment) {
    em.persist(comment);
    return comment;
  }

  public void deleteById(Long id) {
    em.createQuery("delete from Comment c where c.id = :id")
        .setParameter("id", id)
        .executeUpdate();
  }

  public Optional<Comment> findById(Long id) {
    try {
      Optional<Comment> commentOP = Optional.of(em
          .createQuery("select c from Comment c where c.id = :id", Comment.class)
          .getSingleResult()); // 한건보기
      // entity 조회 쿼리, jpql
      // 객체로 query 를 날림)
      return commentOP;
    } catch (Exception e) {
      return Optional.empty();
    }

  }
}
