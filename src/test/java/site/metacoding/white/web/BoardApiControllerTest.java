package site.metacoding.white.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import site.metacoding.white.domain.Board;
import site.metacoding.white.domain.BoardRepository;
import site.metacoding.white.domain.Comment;
import site.metacoding.white.domain.CommentRepository;
import site.metacoding.white.domain.User;
import site.metacoding.white.domain.UserRepository;
import site.metacoding.white.dto.BoardReqDto.BoardSaveReqDto;
import site.metacoding.white.dto.BoardReqDto.BoardUpdateReqDto;
import site.metacoding.white.dto.SessionUser;
import site.metacoding.white.util.SHA256;

@ActiveProfiles("test") // test application?????? ??????
@Sql("classpath:truncate.sql") // ????????? ?????? ????????? truncate ?????? - ????????? ??? ??????
@Transactional // ???????????? ???????????? ????????? ?????????????????? DB??? flush ?????? (Hibernate ?????????)
@AutoConfigureMockMvc // MockMvc Ioc ??????????????? ?????? - ?????? ??????
@SpringBootTest(webEnvironment = WebEnvironment.MOCK) // ?????? ????????? - ?????? ??? ioc??? ??????(?????? ???????????? ??????)
public class BoardApiControllerTest {

  private static final String APPLICATOIN_JSON = "application/json; charset=utf-8";

  // spring ?????? ioc??? ????????? ????????? ?????? ioc ??? ????????????
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

  @BeforeAll // ?????? ???????????? ???
  public static void init() {
    headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
  }

  @BeforeEach // ????????? ?????? ???????????? ?????? // beforeEach ?????? ???????????? ??????
  public void sessionInit() { // ?????? ??????(?????? ??????)
    session = new MockHttpSession(); // ?????? httpSession - ?????? ????????? new?????? ??????
    User user = User.builder().id(1L).username("ssar").build();
    session.setAttribute("sessionUser", new SessionUser(user));
  }

  @BeforeEach // ????????? ?????? ???????????? ?????? // beforeEach ?????? ???????????? ??????
  public void dataInit() {
    String encPassword = sha256.encrypt("1234");
    User user = User.builder().username("ssar").password(encPassword).build();
    User userPS = userRepository.save(user); // user ?????? db??? insert ???

    Board board = Board.builder()
        .title("?????????1???")
        .content("??????????????????")
        .user(userPS)
        .build();
    Board boardPS = boardRepository.save(board); // ????????? ?????? ??????
    // boardPS??? commet ?????????

    Comment comment1 = Comment.builder()
        .content("???????????????")
        .board(boardPS)
        .user(userPS)
        .build();

    Comment comment2 = Comment.builder()
        .content("???????????????")
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
    boardSaveReqDto.setTitle("?????????1???");
    boardSaveReqDto.setContent("??????????????????"); // ????????? ??? ??????

    String body = om.writeValueAsString(boardSaveReqDto); // json?????? ??????

    // when
    ResultActions resultActions = mvc
        .perform(post("/board").content(body)
            .contentType(APPLICATOIN_JSON).accept(APPLICATOIN_JSON)
            .session(session));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("????????? : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(jsonPath("$.code").value(1L));
  }

  @Test
  public void findById_test() throws Exception {
    // given
    Long id = 1L;

    // when
    ResultActions resultActions = mvc
        .perform(get("/board/" + id).accept(APPLICATOIN_JSON));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("????????? : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.data.title").value("?????????1???"));
  }

  @Test
  public void findAll_test() throws Exception {
    // given

    // when
    ResultActions resultActions = mvc
        .perform(get("/board").accept(APPLICATOIN_JSON));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("????????? : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(status().isOk());
    resultActions.andExpect(jsonPath("$.code").value(1L));
    resultActions.andExpect(jsonPath("$.data.[0].title").value("?????????1???"));
  }

  @Test
  public void update_test() throws Exception {
    // given
    Long id = 1L;
    BoardUpdateReqDto boardUpdateReqDto = new BoardUpdateReqDto();
    boardUpdateReqDto.setTitle("?????????2???");
    boardUpdateReqDto.setContent("JUNIT ??????"); // ????????? ????????? ??? ??????
    boardUpdateReqDto.setId(id);

    String body = om.writeValueAsString(boardUpdateReqDto); // json?????? ??????

    // when
    ResultActions resultActions = mvc
        .perform(put("/board/" + id).content(body)
            .contentType(APPLICATOIN_JSON).accept(APPLICATOIN_JSON)
            .session(session));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("????????? : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(jsonPath("$.code").value(1L));
    resultActions.andExpect(jsonPath("$.data.title").value("?????????2???"));
  }

  @Test
  public void deleteById_test() throws Exception {
    // given
    Long id = 1L;

    // when
    ResultActions resultActions = mvc
        .perform(delete("/board/" + id)
            .accept(APPLICATOIN_JSON)
            .session(session));

    // then
    MvcResult mvcResult = resultActions.andReturn();
    System.out.println("????????? : " + mvcResult.getResponse().getContentAsString());
    resultActions.andExpect(jsonPath("$.code").value(1L));
  }
}
