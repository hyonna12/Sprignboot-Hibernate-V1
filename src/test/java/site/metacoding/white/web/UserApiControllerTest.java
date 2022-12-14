package site.metacoding.white.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.white.domain.Board;
import site.metacoding.white.domain.BoardRepository;
import site.metacoding.white.domain.Comment;
import site.metacoding.white.domain.CommentRepository;
import site.metacoding.white.domain.User;
import site.metacoding.white.domain.UserRepository;
import site.metacoding.white.dto.SessionUser;
import site.metacoding.white.dto.UserReqDto.JoinReqDto;
import site.metacoding.white.dto.UserReqDto.LoginReqDto;
import site.metacoding.white.util.SHA256;

@ActiveProfiles("test")
@Sql("classpath:truncate.sql") // 기본 정책 (전 - 후)
@Transactional // 트랜잭션 안붙이면 영속성 컨텍스트에서 DB로 flush 안됨 (Hibernate 사용시)
@AutoConfigureMockMvc // MockMvc Ioc 컨테이너에 등록
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class UserApiControllerTest {

  private static final String APPLICATION_JSON = "application/json; charset=utf-8";

  // 메모리에 띄울려면 autowired 로 띄워야함 - DI 코드 annotation을 보고 주입해줌
  @Autowired
  private MockMvc mvc;

  @Autowired
  private static ObjectMapper om; // json

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private SHA256 sha256;

  private MockHttpSession session;

  @BeforeEach
  public void sessionInit() {
    session = new MockHttpSession();
    User user = User.builder().id(1L).username("ssar").build();
    session.setAttribute("sessionUser", new SessionUser(user));
  }

  @BeforeEach
  public void dataInit() {
    String encPassword = sha256.encrypt("1234");
    User user = User.builder().username("ssar").password(encPassword).build();
    User userPS = userRepository.save(user);

    Board board = Board.builder()
        .title("스프링1강")
        .content("트랜잭션관리")
        .user(userPS)
        .build();
    Board boardPS = boardRepository.save(board);

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

  // @Order(1)
  @Test
  public void join_test() throws Exception {
    // given
    JoinReqDto joinReqDto = new JoinReqDto();
    joinReqDto.setUsername("cos");
    joinReqDto.setPassword("1234");

    // 메서드를 때리려면 json 변환이 필요함
    String body = om.writeValueAsString(joinReqDto); // json으로 변환

    // when
    ResultActions resultActions = mvc
        .perform(post("/join").content(body)
            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("디버그 : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(jsonPath("$.code").value(1L));
  }

  @Test
  public void login_test() throws Exception {
    // given
    LoginReqDto loginReqDto = new LoginReqDto();
    loginReqDto.setUsername("ssar"); // ssar 로 해야함.
    loginReqDto.setPassword("1234");
    String body = om.writeValueAsString(loginReqDto);

    // when
    ResultActions resultActions = mvc
        .perform(post("/login").content(body)
            .contentType(APPLICATION_JSON).accept(APPLICATION_JSON));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("디버그 : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(jsonPath("$.code").value(1L));
  }

}
