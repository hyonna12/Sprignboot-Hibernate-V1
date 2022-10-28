package site.metacoding.white.web;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import site.metacoding.white.dto.UserReqDto.JoinReqDto;

@ActiveProfiles("test")
// @Transactional // 통합테스트에서 RANDOM_PORT를 사용하면 새로운 스레드로 돌기 때문에 rollback 무의미
@Sql("classpath:truncate.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class UserApiControllerTest {

  // @Autowired // 메모리에 띄울려면 autowired 로 띄워야함 - DI 코드 annotation을 보고 주입해줌
  // private UserService userService;

  @Autowired
  private TestRestTemplate rt; // 통신할 때 쓰는 라이브러리, 회원가입 메서드를 direct하게 요청할거임
  @Autowired
  private static ObjectMapper om; // json

  private static HttpHeaders headers; // 모든 테스트가 실행되기 전에 한번만 띄우면 됨

  @BeforeAll // 규칙 - beforeall 은 static으로 만들어야함
  public static void init() {
    headers = new HttpHeaders(); // http 요청 header에 필요
    headers.setContentType(MediaType.APPLICATION_JSON);
  }
  // ioc에 띄워져있으면 autowired 하면 되는데 없으니까 띄워줌

  @Order(1)
  @Test
  public void join_test() throws JsonProcessingException {
    // given
    JoinReqDto joinReqDto = new JoinReqDto();
    joinReqDto.setUsername("very");
    joinReqDto.setPassword("1234");

    // 메서드를 때리려면 json 변환이 필요함
    String body = om.writeValueAsString(joinReqDto); // json으로 변환
    System.out.println(body);

    /// when
    HttpEntity<String> request = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = rt.exchange("/join", HttpMethod.POST,
        request, String.class);

    // then
    // System.out.println(response.getStatusCode());
    // System.out.println(response.getBody());

    DocumentContext dc = JsonPath.parse(response.getBody());
    // System.out.println(dc.jsonString());
    Integer code = dc.read("$.code");
    Assertions.assertThat(code).isEqualTo(1);
  }

  @Order(2)
  @Test
  public void join_test2() throws JsonProcessingException {
    // given
    JoinReqDto joinReqDto = new JoinReqDto();
    joinReqDto.setUsername("very");
    joinReqDto.setPassword("1234");

    String body = om.writeValueAsString(joinReqDto);
    System.out.println(body);

    // when
    HttpEntity<String> request = new HttpEntity<>(body, headers);
    ResponseEntity<String> response = rt.exchange("/join", HttpMethod.POST,
        request, String.class);

    // then
    // System.out.println(response.getStatusCode());
    // System.out.println(response.getBody());

    DocumentContext dc = JsonPath.parse(response.getBody());
    // System.out.println(dc.jsonString());
    Integer code = dc.read("$.code");
    Assertions.assertThat(code).isEqualTo(1);
  }

}
