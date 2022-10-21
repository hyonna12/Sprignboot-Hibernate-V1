package site.metacoding.white.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.white.domain.Board;
import site.metacoding.white.domain.BoardRepository;

@RequiredArgsConstructor
@Service
// 트랜잭션 관리하는 역할
// annotation이 component scan 하면서 오브젝트를 io 컨테이너에 띄울려면 new로 띄우는데
// 디폴트 생성자를 때려서 띄워줌 //ioc, di
public class BoardService {
  private final BoardRepository boardRepository;

  @Transactional
  public void save(Board board) {
    // controller한테 board받음
    boardRepository.save(board);
    // 받아서 repository의 save 호출
  }

  public Board findById(Long id) {
    return boardRepository.findById(id);
  }

  @Transactional
  public void update(Long id, Board board) { // 클라이언트한테 넘겨받은 데이터
    Board boardPS = boardRepository.findById(id); // db에서 조회한 데이터
    // db에서 pc 거쳤으니까 영속화
    boardPS.setTitle(board.getTitle());
    boardPS.setContent(board.getContent());
    boardPS.setAuthor(board.getAuthor());
    // 영속화된 데이터를 클라이언트한테 받은 데이터로 수정
    // 영속화를 시키고 트랜잭션 종료되면 자동으로 flush 됨
  }// 트랜잭션 종료시 -> 더티체킹을 함

  @Transactional
  public void deleteById(Long id) {
    boardRepository.deleteById(id);
  }

  public List<Board> findAll() {
    return boardRepository.findAll();
  }

}
