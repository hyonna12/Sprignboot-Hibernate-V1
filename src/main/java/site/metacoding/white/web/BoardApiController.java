package site.metacoding.white.web;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.metacoding.white.domain.Board;
import site.metacoding.white.domain.User;
import site.metacoding.white.dto.BoardReqDto.BoardSaveReqDto;
import site.metacoding.white.service.BoardService;

@RequiredArgsConstructor
@RestController
// json을 리턴할거임
public class BoardApiController {
  private final BoardService boardService;
  private final HttpSession session;

  @GetMapping("/v2/board/{id}")
  public String findByIdV2(@PathVariable Long id) {
    System.out.println("현재 open-in-view는 true 인가 false 인가 생각해보기!!");
    Board boardPS = boardService.findById(id);
    System.out.println("board.id : " + boardPS.getId());
    System.out.println("board.title : " + boardPS.getTitle());
    System.out.println("board.content : " + boardPS.getContent());
    System.out.println("open-in-view가 false이면 Lazy 로딩 못함");

    return "ok";
  }

  @PostMapping("/board")
  public String saveV2(@RequestBody BoardSaveReqDto boardSaveReqDto) {
    // 세션 정보를 들고옴
    User principal = (User) session.getAttribute("principal");
    // insert into board(title, content, user_id) values(?, ?, ?)
    boardSaveReqDto.setUser(principal);
    boardService.save(boardSaveReqDto); // 서비스에는 단 하나의 객체만 전달한다.
    // 받아서 service의 save 호출하고
    return "ok";
    // 잘되면 ok
  }

  @GetMapping("/board/{id}")
  public Board findById(@PathVariable Long id) {
    return boardService.findById(id); // Entity -> JSON 변경 (MessageConverter)
  }

  @GetMapping("/board")
  public List<Board> findAll() {
    return boardService.findAll();
  }

  @PutMapping("/board/{id}")
  public String update(@PathVariable Long id, @RequestBody Board board) {
    // id 따로 주소로 받으니까 board에는 id 받으면 안됨 / board 말고 필요한 데이터만 받아야됨 dto
    boardService.update(id, board);
    return "ok";
  }

  @DeleteMapping("/board/{id}")
  public String deleteById(@PathVariable Long id) {
    boardService.deleteById(id);
    return "ok";
  }

}
