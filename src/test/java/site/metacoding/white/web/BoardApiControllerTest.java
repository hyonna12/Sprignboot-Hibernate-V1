package site.metacoding.white.web;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.white.domain.Board;
import site.metacoding.white.domain.BoardRepository;
import site.metacoding.white.domain.Comment;
import site.metacoding.white.domain.CommentRepository;
import site.metacoding.white.domain.User;
import site.metacoding.white.domain.UserRepository;
import site.metacoding.white.dto.SessionUser;
import site.metacoding.white.dto.BoardReqDto.BoardSaveReqDto;
import site.metacoding.white.dto.BoardReqDto.BoardUpdateReqDto;
import site.metacoding.white.util.SHA256;

@ActiveProfiles("test") // test application으로 실행
@Sql("classpath:truncate.sql") // 메서드 실행 직전에 truncate 실행 - 데이터 다 날림
@Transactional // 트랜잭션 안붙이면 영속성 컨텍스트에서 DB로 flush 안됨 (Hibernate 사용시)
@AutoConfigureMockMvc // MockMvc Ioc 컨테이너에 등록 - 가짜 환경
@SpringBootTest(webEnvironment = WebEnvironment.MOCK) // 통합 테스트 - 모든 걸 ioc에 띄움(가짜 환경으로 실행)
public class BoardApiControllerTest {

  private static final String APPLICATOIN_JSON = "application/json; charset=utf-8";

  // spring 실제 ioc에 있는게 아니라 가짜 ioc 에 떠있는거
  @Autowired
  private MockMvc mvc;

  @Autowired
  private ObjectMapper om;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private SHA256 sha256;

  private MockHttpSession session;

  private static HttpHeaders headers;

  @BeforeAll // 한번 실행되는 것
  public static void init() {
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
  }

  @BeforeEach // 메서드 실행 직전마다 실행 // beforeEach 부터 트랜잭션 발동
  public void sessionInit() { // 세션 생성(가짜 세션)
    session = new MockHttpSession(); // 가짜 httpSession - 가짜 환경에 new해서 띄움
    User user = User.builder().id(1L).username("ssar").build();
    session.setAttribute("sessionUser", new SessionUser(user));
  }

  @BeforeEach // 메서드 실행 직전마다 실행 // beforeEach 부터 트랜잭션 발동
  public void dataInit() {
    String encPassword = sha256.encrypt("1234");
    User user = User.builder().username("ssar").password(encPassword).build();
    User userPS = userRepository.save(user); // user 한명 db에 insert 됨

    Board board = Board.builder()
        .title("스프링1강")
        .content("트랜잭션관리")
        .user(userPS)
        .build();
    Board boardPS = boardRepository.save(board); // 게시글 한건 생성
    // boardPS에 commet 넣어줌

    Comment comment1 = Comment.builder()
        .content("내용좋아요")
        .board(boardPS)
        .user(userPS)
        .build();

    Comment comment2 = Comment.builder()
        .content("내용싫어요")
        .board(boardPS)
        .user(userPS)
        .build();

    commentRepository.save(comment1);
    commentRepository.save(comment2);
  }

  @Test
  public void save_test() throws Exception {
    // given
    BoardSaveReqDto boardSaveReqDto = new BoardSaveReqDto();
    boardSaveReqDto.setTitle("스프링1강");
    boardSaveReqDto.setContent("트랜잭션관리"); // 게시글 값 넣고

    String body = om.writeValueAsString(boardSaveReqDto); // json으로 바꿈

    // when
    ResultActions resultActions = mvc
        .perform(MockMvcRequestBuilders.post("/board").content(body)
            .contentType(APPLICATOIN_JSON).accept(APPLICATOIN_JSON)
            .session(session));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("디버그 : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1L));
  }

  @Test
  public void findById_test() throws Exception {
    // given
    Long id = 1L;

    // when
    ResultActions resultActions = mvc
        .perform(MockMvcRequestBuilders.get("/board/" + id).accept(APPLICATOIN_JSON));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("디버그 : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("스프링1강"));
  }

  @Test
  public void findAll_test() throws Exception {
    // given

    // when
    ResultActions resultActions = mvc
        .perform(MockMvcRequestBuilders.get("/board").accept(APPLICATOIN_JSON));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("디버그 : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(MockMvcResultMatchers.status().isOk());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1L));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.[0].title").value("스프링1강"));
  }

  @Test
  public void update_test() throws Exception {
    // given
    Long id = 1L;
    BoardUpdateReqDto boardUpdateReqDto = new BoardUpdateReqDto();
    boardUpdateReqDto.setTitle("스프링2강");
    boardUpdateReqDto.setContent("JUNIT 공부"); // 게시글 수정할 값 넣고
    boardUpdateReqDto.setId(id);

    String body = om.writeValueAsString(boardUpdateReqDto); // json으로 바꿈

    // when
    ResultActions resultActions = mvc
        .perform(MockMvcRequestBuilders.put("/board/" + id).content(body)
            .contentType(APPLICATOIN_JSON).accept(APPLICATOIN_JSON)
            .session(session));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("디버그 : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1L));
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.data.title").value("스프링2강"));
  }

  @Test
  public void deleteById_test() throws Exception {
    // given
    Long id = 1L;

    // when
    ResultActions resultActions = mvc
        .perform(MockMvcRequestBuilders.delete("/board/" + id)
            .accept(APPLICATOIN_JSON)
            .session(session));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("디버그 : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(MockMvcResultMatchers.jsonPath("$.code").value(1L));
  }
}
