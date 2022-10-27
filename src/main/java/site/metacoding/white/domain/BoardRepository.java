package site.metacoding.white.domain;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class BoardRepository {

  private final EntityManager em;
  // preparedsatement와 같은 역할
  // db에서 들고온 다른 오브젝트를 자바 오브 젝트로 바꿔줌
  // preparestatement와는 다르게 object로 매핑해줌, hibernate 기술

  public Board save(Board board) {
    em.persist(board);
    // 영속화, insert 됨
    // db에 쿼리를 전송

    // String sql = "insert into board(title, content, author)" + "values(:title,
    // :content, :author)";
    // em.createQuery(sql)
    // .setParameter("title", board.getTitle())
    // .setParameter("content", board.getContent())
    // .setParameter("author", board.getAuthor())
    // .executeUpdate();
    return board;
  }
  // 인터페이스가 아니라서 형태가 있어야함

  public Optional<Board> findById(Long id) {
    try {
      Optional<Board> boardOP = Optional.of(em
          .createQuery("select b from Board b join fetch b.user u join fetch b.comments c where b.id = :id",
              Board.class) // board 타입으로 받을거임
          .setParameter("id", id)
          .getSingleResult()); // 한건보기
      // entity 조회 쿼리, jpql
      // 객체로 query 를 날림)
      return boardOP;
    } catch (Exception e) {
      return Optional.empty();
    }

  }

  public List<Board> findAll() {
    List<Board> boardList = em.createQuery("select b from Board b", Board.class) // board 타입으로 받을거임
        .getResultList();
    // entity 조회 쿼리, jpql
    // 객체로 query 를 날림
    return boardList;
  }

  public void deleteById(Long id) {
    em.createQuery("delete from Board b where b.id = :id")
        .setParameter("id", id)
        .executeUpdate();
  }

  // JPQL 문법
  // Board boardPS = em.createQuery("select b from Board b where b.id = :id",
  // Board.class)
  // .setParameter("id", id)
  // .getSingleResult();

  // Board boardPS = (Board) em
  // .createNativeQuery("select * from board b inner join user u on b.user_id =
  // u.id where b.id = :id",
  // Board.class)
  // .setParameter("id", id)
  // .getSingleResult();

  // Board boardPS = em
  // .createQuery("select b from Board b join fetch b.user u where b.id = :id",
  // Board.class)
  // .setParameter("id", id)
  // .getSingleResult();
}
