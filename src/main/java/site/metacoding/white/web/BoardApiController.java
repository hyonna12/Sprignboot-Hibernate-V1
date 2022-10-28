package site.metacoding.white.web;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.metacoding.white.dto.BoardReqDto.BoardSaveReqDto;
import site.metacoding.white.dto.BoardReqDto.BoardUpdateReqDto;
import site.metacoding.white.dto.BoardRespDto.BoardSaveRespDto;
import site.metacoding.white.dto.ResponseDto;
import site.metacoding.white.dto.SessionUser;
import site.metacoding.white.service.BoardService;

@RequiredArgsConstructor
@RestController
// json을 리턴할거임
public class BoardApiController {
  private final BoardService boardService;
  private final HttpSession session;

  // @GetMapping("/v2/board/{id}")
  // public String findByIdV2(@PathVariable Long id) {
  // System.out.println("현재 open-in-view는 true 인가 false 인가 생각해보기!!");
  // Board boardPS = boardService.findById(id);
  // System.out.println("board.id : " + boardPS.getId());
  // System.out.println("board.title : " + boardPS.getTitle());
  // System.out.println("board.content : " + boardPS.getContent());
  // System.out.println("open-in-view가 false이면 Lazy 로딩 못함");

  // return "ok";
  // }

  @PostMapping("/board")
  public ResponseDto<?> saveV2(@RequestBody BoardSaveReqDto boardSaveReqDto) {
    // 세션 정보를 들고옴
    SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
    if (sessionUser == null) {
      throw new RuntimeException("로그인이 필요합니다.");
    }
    // insert into board(title, content, user_id) values(?, ?, ?)
    boardSaveReqDto.setSessionUser(sessionUser);
    BoardSaveRespDto boardSaveRespDto = boardService.save(boardSaveReqDto); // 서비스에는 단 하나의 객체만 전달한다.
    // 받아서 service의 save 호출하고
    return new ResponseDto<>(1, "성공", boardSaveRespDto);
    // 잘되면 ok
  }

  // 게시글 상세보기 (Board + User + List<Comment>)
  @GetMapping("/board/{id}")
  public ResponseDto<?> findById(@PathVariable Long id) {
    return new ResponseDto<>(1, "성공", boardService.findById(id)); // Entity -> JSON 변경 (MessageConverter)
  }

  @GetMapping("/board")
  public ResponseDto<?> findAll() {
    return new ResponseDto<>(1, "성공", boardService.findAll());
  }

  @PutMapping("/board/{id}")
  public ResponseDto<?> update(@PathVariable Long id, @RequestBody BoardUpdateReqDto boardUpdateReqDto) {
    SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
    if (sessionUser == null) {
      throw new RuntimeException("로그인이 필요합니다.");
    }
    // id 따로 주소로 받으니까 board에는 id 받으면 안됨 / board 말고 필요한 데이터만 받아야됨 dto
    boardUpdateReqDto.setId(id);
    return new ResponseDto<>(1, "성공", boardService.update(boardUpdateReqDto));
  }

  @DeleteMapping("/board/{id}")
  public ResponseDto<?> deleteById(@PathVariable Long id) {
    SessionUser sessionUser = (SessionUser) session.getAttribute("sessionUser");
    if (sessionUser == null) {
      throw new RuntimeException("로그인이 필요합니다.");
    }
    boardService.deleteById(id);
    return new ResponseDto<>(1, "성공", null);
  }

}
