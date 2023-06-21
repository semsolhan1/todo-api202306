package com.example.todo.todoapi.api;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/todos")
public class TodoController {

  private final TodoService todoService;

  //할 일 등록 요청
  @PostMapping// 맵핑 URL은 위에 /api/todos
  public ResponseEntity<?> createTodo(// 검증결과 @Validated JSON으로 오니까 @RequestBody
          @Validated @RequestBody TodoCreateRequestDTO requestDTO
                                      ,BindingResult result

  ){
    if(result.hasErrors()) {
      log.warn("DTO 검증 에러 발생: {}", result.getFieldError());//  검증 하나여서 Error임
      return ResponseEntity
              .badRequest()
              .body(result.getFieldError());
    }

    try {
      TodoListResponseDTO responseDTO = todoService.create(requestDTO);//검증하기위해 try문
      return ResponseEntity // insert까지반영된 데이터를 controller로 리턴해 준다.
              .ok()
              .body(responseDTO);
    } catch (RuntimeException e) {
      log.error(e.getMessage());
      return ResponseEntity
              .internalServerError()
              .body(TodoListResponseDTO.builder().error(e.getMessage()));
    }


  }

  //할 일 삭제 요청
  @DeleteMapping("/{id}")
  public ResponseEntity<?> deleteTodo(
          @PathVariable("id") String todoId
  ) {
    log.info("/api/todos/{} DELETE request!", todoId);

    if(todoId == null || todoId.trim().equals("")) {
      return ResponseEntity
              .badRequest()
              .body(TodoListResponseDTO.builder().error("ID를 전달해 주세요."));
    }

    try {
      TodoListResponseDTO responseDTO = todoService.delete(todoId);
      return ResponseEntity.ok().body(responseDTO);
    } catch (Exception e) {
      return ResponseEntity.internalServerError()
              .body(TodoListResponseDTO.builder().error(e.getMessage()));
    }

  }

  //할 일 목록 요청
  @GetMapping
  public ResponseEntity<?> retrieveTodoList() {
    log.info("/api/todos GET request");
    TodoListResponseDTO responseDTO = todoService.retrieve();

    return ResponseEntity.ok().body(responseDTO);

  }

  //할 일 수정 요청
  @RequestMapping(method = {RequestMethod.PUT, RequestMethod.PATCH})
  public ResponseEntity<?> updateTodo(// 수정하는 데이터가 오기때문에 변수가 온다.
          @Validated @RequestBody TodoModifyRequestDTO requestDTO,
                                      BindingResult result,
                                      HttpServletRequest request
  ) {
    if (result.hasErrors()) {
      return ResponseEntity.badRequest()
              .body(result.getFieldError());
    }

    log.info("/api/todos {} request!", request.getMethod());
    log.info("modifying dto: {}", requestDTO);

    try {
      TodoListResponseDTO responseDTO = todoService.update(requestDTO);
      return ResponseEntity.ok().body(responseDTO);
    } catch (Exception e) {
      return ResponseEntity.internalServerError()//update내에서의 문제 이다.
              .body(TodoListResponseDTO.builder().error(e.getMessage()));
    }

  }


}
