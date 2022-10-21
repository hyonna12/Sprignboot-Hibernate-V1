package site.metacoding.white.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Board { // 자바코드로 테이블을 자동 생성
  @Id // primary key
  @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment(해당 db에 번호증가 전략을 따라감)
  private Long id; // 64bit, id가 pk이고 자동으로 생성됨
  private String title;
  @Column(length = 1000)
  private String content;
  private String author;
}