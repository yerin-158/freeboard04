package com.freeboard04.api.board;

import com.freeboard04.api.PageDto;
import com.freeboard04.api.user.UserForm;
import com.freeboard04.domain.board.BoardEntity;
import com.freeboard04.domain.board.BoardService;
import com.freeboard04.domain.board.enums.SearchType;
import com.freeboard04.domain.user.enums.UserExceptionType;
import com.freeboard04.util.exception.FreeBoardException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardApiController {

    private final HttpSession httpSession;
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<PageDto<BoardDto>> get(@PageableDefault(page = 1, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<BoardEntity> pageBoardList = boardService.get(pageable);
        List<BoardDto> boardDtoList = pageBoardList.stream().map(boardEntity -> BoardDto.of(boardEntity)).collect(Collectors.toList());
        return ResponseEntity.ok(PageDto.of(pageBoardList, boardDtoList));
    }

    @PostMapping
    public ResponseEntity<BoardDto> post(@RequestBody BoardForm form) {
        if (httpSession.getAttribute("USER") == null) {
            throw new FreeBoardException(UserExceptionType.LOGIN_INFORMATION_NOT_FOUND);
        }
        BoardEntity savedEntity = boardService.post(form, (UserForm) httpSession.getAttribute("USER"));
        return ResponseEntity.ok(BoardDto.of(savedEntity));
    }

    @PutMapping("/{id}")
    public void update(@RequestBody BoardForm form, @PathVariable long id) {
        if (httpSession.getAttribute("USER") == null) {
            throw new FreeBoardException(UserExceptionType.LOGIN_INFORMATION_NOT_FOUND);
        }
        boardService.update(form, (UserForm) httpSession.getAttribute("USER"), id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        if (httpSession.getAttribute("USER") == null) {
            throw new FreeBoardException(UserExceptionType.LOGIN_INFORMATION_NOT_FOUND);
        }
        boardService.delete(id, (UserForm) httpSession.getAttribute("USER"));
    }

    @GetMapping(params = {"type", "keyword"})
    public ResponseEntity<PageDto<BoardDto>> search(@PageableDefault(page = 1, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
                                                    @RequestParam String keyword, @RequestParam SearchType type) {
        if (httpSession.getAttribute("USER") == null) {
            throw new FreeBoardException(UserExceptionType.LOGIN_INFORMATION_NOT_FOUND);
        }
        Page<BoardEntity> pageBoardList = boardService.search(pageable, keyword, type);
        List<BoardDto> boardDtoList = pageBoardList.stream().map(boardEntity -> BoardDto.of(boardEntity)).collect(Collectors.toList());
        return ResponseEntity.ok(PageDto.of(pageBoardList, boardDtoList));
    }

    @PostMapping("/{id}/good")
    public void addGoodPoint(@PathVariable long id){
        if (httpSession.getAttribute("USER") == null) {
            throw new FreeBoardException(UserExceptionType.LOGIN_INFORMATION_NOT_FOUND);
        }
        boardService.addGoodPoint(((UserForm) httpSession.getAttribute("USER")).convertUserEntity(), id);
    }

    @DeleteMapping("/{boardId}/good/{goodHistoryId}")
    public void deleteGoodPoint(@PathVariable long boardId, @PathVariable long goodHistoryId){
        if (httpSession.getAttribute("USER") == null) {
            throw new FreeBoardException(UserExceptionType.LOGIN_INFORMATION_NOT_FOUND);
        }
        boardService.deleteGoodPoint(((UserForm) httpSession.getAttribute("USER")).convertUserEntity(), goodHistoryId, boardId);
    }
}
