package site.metacoding.white.web;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import site.metacoding.white.domain.Board;
import site.metacoding.white.service.BoardService;

@RequiredArgsConstructor
@RestController
// json을 리턴할거임
public class BoardApiController {
  private final BoardService boardService;

  @GetMapping("/board")
  public List<Board> findAll() {
    return boardService.findAll();
  }

  @GetMapping("/board/{id}")
  public Board findById(@PathVariable Long id) {
    return boardService.findById(id);
  }

  @PutMapping("/board/{id}")
  public String update(@PathVariable Long id, @RequestBody Board board) {
    // id 따로 주소로 받으니까 board에는 id 받으면 안됨 / board 말고 필요한 데이터만 받아야됨 dto
    boardService.update(id, board);
    return "ok";
  }

  @PostMapping("/board")
  public String save(@RequestBody Board board) {
    // 사용자한테 json타입의 board 받음
    boardService.save(board);
    // 받아서 service의 save 호출하고
    return "ok";
    // 잘되면 ok
  }

  @DeleteMapping("/board/{id}")
  public String deleteById(@PathVariable Long id) {
    boardService.deleteById(id);
    return "ok";
  }

}
