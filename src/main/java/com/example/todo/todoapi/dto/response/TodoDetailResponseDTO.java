package com.example.todo.todoapi.dto.response;

import com.example.todo.todoapi.entity.Todo;
import lombok.*;

@Setter @Getter
@ToString @EqualsAndHashCode
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TodoDetailResponseDTO {

  private String id;
  private String title;
  private boolean done;

  // 엔터티를 DTO로 만들어주는 생성자
  public TodoDetailResponseDTO(Todo todos) {
    this.id = todos.getTodoId();
    this.title = todos.getTitle();
    this.done = todos.isDone();
  }
}
