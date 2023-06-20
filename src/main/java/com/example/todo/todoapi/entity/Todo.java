package com.example.todo.todoapi.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.cglib.proxy.Mixin;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter @Getter
@ToString @EqualsAndHashCode(of = "todoId")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tbl_todo")
public class Todo {

  @Id
  @GeneratedValue(generator = "system-uuid")
  @GenericGenerator(name = "system-uuid", strategy = "uuid")
  private String todoId;

  @Column(nullable = false, length = 50)
  private String title;//제목

  private boolean done;//일정

  private LocalDateTime createData;//등록시간

}
