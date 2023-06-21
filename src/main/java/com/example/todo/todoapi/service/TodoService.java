package com.example.todo.todoapi.service;

import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {

  private final TodoRepository todoRepository;

  //할 일 목록 조회
  //요청에 따라 데이터 갱신,삭제 등이 발생한 후
  //최신의 데이터 내용을 클라이언트에게 전달해서 렌더링 하기 위해
  // 목록 리턴 메서드를 서비스에서 처리.
   public TodoListResponseDTO retrieve() {
     List<Todo> entityList = todoRepository.findAll();

     List<TodoDetailResponseDTO> dtoList
             = entityList.stream()
  //             .map(todo -> new TodoDetailResponseDTO(todo))
               .map(TodoDetailResponseDTO::new)
               .collect(Collectors.toList());

     return TodoListResponseDTO.builder()
             .todos(dtoList)
             .build();

   }

  //할 일 삭제
  public TodoListResponseDTO delete(final String todoId) {

    try {
      todoRepository.deleteById(todoId);

    } catch (Exception e) {
      log.error("id가 존재하지 않아 삭제에 실패했습니다. - ID: {}, err: {}"
              , todoId, e.getMessage());
      throw new RuntimeException("id가 존재하지 않아 삭제에 실패했습니다.");
    }

    return retrieve();

  }

  public TodoListResponseDTO create(final TodoCreateRequestDTO requestDTO)
          throws RuntimeException {

     todoRepository.save(requestDTO.toEntity()); //엔터티로 바꿔준다.
     log.info("할 일 저장 완료! 제목: {}", requestDTO.getTitle());
     return retrieve(); // controller로 리턴

  }

  public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO) {
    Optional<Todo> targetEntity = todoRepository.findById(requestDTO.getId());
              //orElsethrow로 할수도 있다.
    targetEntity.ifPresent(entity -> {
      entity.setDone(requestDTO.isDone()); // vscode에 이미 뒤집어서 오기때문에 다시 뒤집을 필요는 없다.

      todoRepository.save(entity);
      //조회 결과가 있다면 클라이언트가 요청한 던값을 가져오고 .....
    });

    return retrieve();
  }
}
