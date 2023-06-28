package com.example.todo.todoapi.service;

import com.example.todo.auth.TokenUserInfo;
import com.example.todo.todoapi.dto.request.TodoCreateRequestDTO;
import com.example.todo.todoapi.dto.request.TodoModifyRequestDTO;
import com.example.todo.todoapi.dto.response.TodoDetailResponseDTO;
import com.example.todo.todoapi.dto.response.TodoListResponseDTO;
import com.example.todo.todoapi.entity.Todo;
import com.example.todo.todoapi.repository.TodoRepository;
import com.example.todo.userapi.entity.Role;
import com.example.todo.userapi.entity.User;
import com.example.todo.userapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final TodoRepository todoRepository;

    @Value("${upload.path")
    private String uploadRootPath;

    //할 일 목록 조회
    //요청에 따라 데이터 갱신, 수정, 삭제 등이 발생한 후
    //최신의 데이터 내용을 클라이언트에게 전달해서 렌더링 하기 위해
    //목록 리턴 메서드를 서비스에서 처리.
    public TodoListResponseDTO retrieve(String userId) {

        User user = getUser(userId);

        List<Todo> entityList = todoRepository.findAllByUser(user);

        List<TodoDetailResponseDTO> dtoList = entityList.stream()
                /*.map(todo -> new TodoDetailResponseDTO(todo))*/
                .map(TodoDetailResponseDTO::new)
                .collect(Collectors.toList());

        return TodoListResponseDTO.builder()
                .todos(dtoList)
                .build();
    }

    private User getUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException("회원 정보가 없습니다.")
        );

        return user;
    }




    //할 일 삭제
    public TodoListResponseDTO delete(final String todoId, String userId) {
        try {
            todoRepository.deleteById(todoId);
        } catch (Exception e) {
            log.error("id가 존재하지 않아 삭제에 실패했습니다. - ID: {}, err: {}"
                        ,todoId, e.getMessage());
            throw new RuntimeException("id가 존재하지 않아 삭제에 실패했습니다.");
        }
        return retrieve(userId);
    }

    public TodoListResponseDTO create(
            final TodoCreateRequestDTO requestDTO,
            final TokenUserInfo userInfo
    )
            throws RuntimeException, IllegalStateException {

        User foundUser = getUser(userInfo.getUserId());
        // 권한에 따른 글쓰기 제한 처리
        // 일반 회원이 일정 5개 초과해서 작성하면 예외를 발생
        if(userInfo.getRole() == Role.COMMON
                && todoRepository.countByUser(foundUser) >= 5) {
            throw new IllegalArgumentException(("일반회원은 더 이상 일정을 작성할 수 없습니다."));
        }

        Todo todo = requestDTO.toEntity(foundUser);

        todoRepository.save(todo);
        log.info("할 일 저장 완료! 제목: {}", requestDTO.getTitle());
        return retrieve(userInfo.getUserId());
    }

    public TodoListResponseDTO update(final TodoModifyRequestDTO requestDTO, String userId)
        throws RuntimeException {

        Optional<Todo> targetEntity
                = todoRepository.findById(requestDTO.getId());

        targetEntity.ifPresent(entity -> {
            entity.setDone(requestDTO.isDone());

            todoRepository.save(entity);
        });

        return retrieve(userId);

    }

    /**
     * 업로드된 파일을 서버에 저장하고 저장 경로를 리턴
     * @param originalFile - 업로드 된 파일의 정보
     * @return 실제로 저장된 이미지 경로
     */

    public String uploadProfileImage(MultipartFile originalFile) throws IOException {

        //루트 디렉토리가 존재하는 지 확인 후 존재하지 않으면 생성
        File rootDir = new File(uploadRootPath);
        if(!rootDir.exists()) rootDir.mkdir();

        // 파일명을유니크하게 변경
        String uniqueFileName = UUID.randomUUID()
                + "_" + originalFile.getOriginalFilename();

        //파일을 저장
        File uploadFile = new File(uploadRootPath + "/" + uniqueFileName);
        originalFile.transferTo(uploadFile);

        return uniqueFileName;


    }


}










