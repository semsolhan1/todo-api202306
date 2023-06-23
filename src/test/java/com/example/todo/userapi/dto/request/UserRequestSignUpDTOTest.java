package com.example.todo.userapi.dto.request;

import com.example.todo.userapi.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(false)
class UserRequestSignUpDTOTest {

  @Autowired
  UserService userService;

  @Test
  @DisplayName("중복된 이메일로 회원 가입을 시도하면 RuntimeExeption이 발생해야 한다.")
  void validateEmailTest() {
      //given
      String email = "abc1234@naver.com";

      UserRequestSignUpDTO dto = UserRequestSignUpDTO.builder()
              .email(email)
              .password("ffff")
              .userName("ddddd")
              .build();
      //when
      //then
      //assertThrows
      //param1: 어떤 에러가 발생할 시 에러 클래스를 적용
      //param2: 에러가 발생하는 상황을 함수로 전달
      //이 상황에서 이런 에러가 발생한다는 가정으로 작성. => 에러가 난다는 가정하에 작성함.
      assertThrows(RuntimeException.class, () -> {userService.create(dto);});
  }

}