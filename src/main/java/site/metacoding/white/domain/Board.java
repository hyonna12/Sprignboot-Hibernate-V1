package site.metacoding.white.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
public class Board { // 자바코드로 테이블을 자동 생성
  @Id // primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment(해당 db에 번호증가 전략을 따라감)
  private Long id; // 64bit, id가 pk이고 자동으로 생성됨
  private String title;
  @Column(length = 1000)
  private String content;

  // FK가 만들어짐. user_id
  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  // 조회를 위해서만 필요함 /fk를 만드는 코드가 아님
  // many 는 컬럼이 될 수 없음(table에 list를 컬럼으로 둘 수 없음)
  // CascadeType.ALL를 걸면 Board 객체 save시에 comment를 함께 저장할 수 있다.
  @OnDelete(action = OnDeleteAction.CASCADE) // 양방향 매핑시 걸어준다.
  @OneToMany(mappedBy = "board", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  // 컬럼으로 만들지 않고 comment의 board가 fk임
  private List<Comment> comments = new ArrayList<>(); // board를 new 해도 comment가 new되지 않아서 comment는 미리 new 해놔야함

  public void addComment(Comment comment) {
    this.comments.add(comment);
  }

  @Builder
  public Board(Long id, String title, String content, User user) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.user = user;
  }

  // 변경하는 코드는 의미 있게 메서드로 구현
  public void update(String title, String content) {
    this.title = title;
    this.content = content;
  }

}