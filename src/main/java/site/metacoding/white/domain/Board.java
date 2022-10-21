package site.metacoding.white.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Board { // 자바코드로 테이블을 자동 생성
  @Id // primary key
  @GeneratedValue // auto_increment
  private Long id; // 64bit, id가 pk이고 자동으로 생성됨
  private String title;
  @Column(length = 1000)
  private String content;
}