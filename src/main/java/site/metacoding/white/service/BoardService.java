package site.metacoding.white.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.metacoding.white.domain.Board;
import site.metacoding.white.domain.BoardRepository;
import site.metacoding.white.dto.BoardReqDto.BoardSaveReqDto;
import site.metacoding.white.dto.BoardReqDto.BoardUpdateReqDto;
import site.metacoding.white.dto.BoardRespDto.BoardAllRespDto;
import site.metacoding.white.dto.BoardRespDto.BoardDetailRespDto;
import site.metacoding.white.dto.BoardRespDto.BoardSaveRespDto;
import site.metacoding.white.dto.BoardRespDto.BoardUpdateRespDto;

@RequiredArgsConstructor
@Service
// 트랜잭션 관리하는 역할
// DTO 변환해서 컨트롤러에게 돌려줘야함 (화면에 필요한 데이터만 뿌려줌)
// annotation이 component scan 하면서 오브젝트를 io 컨테이너에 띄울려면 new로 띄우는데
// 디폴트 생성자를 때려서 띄워줌 //ioc, di
public class BoardService {
  private final BoardRepository boardRepository;

  @Transactional
  public BoardSaveRespDto save(BoardSaveReqDto boardSaveReqDto) {
    // 핵심로직 // dto 받아서 entity로 바꿔서 db에 save
    // select해서 db에서 조회해서 영속화된 유저객체
    Board boardPS = boardRepository.save(boardSaveReqDto.toEntity());

    // DTO 전환 // db에서 entity로 받은걸 dto로 바꿔서 응답
    BoardSaveRespDto boardSaveRespDto = new BoardSaveRespDto(boardPS);
    return boardSaveRespDto;
  }

  @Transactional(readOnly = true) // 트랜잭션을 걸면 OSIV가 false여도 디비 커넥션이 유지됨.
  public BoardDetailRespDto findById(Long id) {
    Optional<Board> boardOP = boardRepository.findById(id); // 오픈 인뷰가 false니까 조회후 세션 종료
    if (boardOP.isPresent()) {
      BoardDetailRespDto boardDetailRespDto = new BoardDetailRespDto(boardOP.get());
      return boardDetailRespDto;
    } else {
      throw new RuntimeException("해당 " + id + "로 상세보기를 할 수 없습니다.");
    }

    // boardPS.getUser().getUsername(); // Lazy 로딩됨. (근데 Eager이면 이미 로딩되서 select 두번
    // 4. user select 됨?
    // System.out.println("서비스단에서 지연로딩 함. 왜? 여기까지는 디비커넥션이 유지되니까");

  }

  @Transactional
  public BoardUpdateRespDto update(BoardUpdateReqDto boardUpdateReqDto) { // 클라이언트한테 넘겨받은 데이터
    Long id = boardUpdateReqDto.getId();
    Optional<Board> boardOP = boardRepository.findById(id); // db에서 조회한 데이터
    if (boardOP.isPresent()) {
      // db에서 pc 거쳤으니까 영속화
      Board boardPS = boardOP.get();
      boardPS.update(boardUpdateReqDto.getTitle(), boardUpdateReqDto.getContent());
      return new BoardUpdateRespDto(boardPS);

      // 영속화된 데이터를 클라이언트한테 받은 데이터로 수정
      // 영속화를 시키고 트랜잭션 종료되면 자동으로 flush 됨
    } else {
      throw new RuntimeException("해당 " + id + "로 수정할 수 없습니다.");
    }

  }// 트랜잭션 종료시 -> 더티체킹을 함

  @Transactional
  public void deleteById(Long id, Long userId) {
    Optional<Board> boardOP = boardRepository.findById(id);

    if (boardOP.isPresent()) {
      Board boardPS = boardOP.get();
      if (boardPS.getUser().getId() != userId)
        throw new RuntimeException("해당 게시글을 삭제할 권한이 없습니다.");
    }
    boardRepository.deleteById(id);
  }

  @Transactional(readOnly = true)
  public List<BoardAllRespDto> findAll() {
    List<Board> boardList = boardRepository.findAll();

    List<BoardAllRespDto> boardAllRespDtoList = new ArrayList<>();
    for (Board board : boardList) {
      boardAllRespDtoList.add(new BoardAllRespDto(board));
    }

    return boardAllRespDtoList;
  }

}
