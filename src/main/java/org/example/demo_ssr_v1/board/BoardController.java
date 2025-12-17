package org.example.demo_ssr_v1.board;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1.reply.ReplyResponse;
import org.example.demo_ssr_v1.reply.ReplyService;
import org.example.demo_ssr_v1.user.User;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller // IoC
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    private final ReplyService replyService;

    /**
     * 게시글 수정 화면 요청
     * @param id
     * @param model
     * @param session
     * @return
     */
    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable Long id,Model model, HttpSession session) {

        // 1. 인증 검사 o
        User sessionUser = (User) session.getAttribute("sessionUser"); // sessionUser -> 상수
        BoardResponse.updateFormDTO board = boardService.게시글수정화면(id, sessionUser.getId());

        model.addAttribute("board", board);

        return "board/update-form";
    }

    /**
     * 게시글 수정 요청 기능
     * @param id
     * @param updateDTO
     * @param session
     * @return
     */
    @PostMapping("/board/{id}/update")
    public String updateProc(
            @PathVariable Long id,
            BoardRequest.UpdateDTO updateDTO,
            HttpSession session
    ) {
        // 1. 인증 처리
        User sessionUser = (User) session.getAttribute("sessionUser");
        updateDTO.validate();

        boardService.게시글수정(updateDTO, id, sessionUser.getId());

        return "redirect:/board/list";
    }

    /**
     * 게시글 목록 화면 요청 (페이징 기능 추가)
     * @param model
     * @return
     *
     * 예시 /board/list?page=1&size=5
     */
    @GetMapping({"/board/list"})
    public String boardList(
            Model model,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "3") int size
    ) {
        // 1. 페이지 번호 변환: 사용자는 1부터 시작하는 페이지번호를 사용하지만
        //      , Spring의 Pageable은 0부터 시작하므로 1을 빼서 변환해야 함
        int pageIndex = Math.max(0, page - 1);
        // size = 5 (일단 고정) - 한 페이지에 보여야 할 게시글 개수
        BoardResponse.PageDTO boardPage = boardService.게시글목록조회(pageIndex, size);
        model.addAttribute("boardPage", boardPage);

        return "board/list";
    }

    /** TODO 삭제 예졍
     * 게시글 목록 화면 요청
     * @param model
     * @return
     */

//    @GetMapping({"/board/list"})
//    public String boardList(Model model, Long boardId) {
//        List<BoardResponse.ListDTO> boardList = boardService.게시글목록조회();
//        model.addAttribute("boardList", boardList);
//
//        return "board/list";
//    }

    /**
     * 게시글 작성 화면 요청
     * @param session
     * @return
     */
    @GetMapping("/board/save")
    public String saveFrom(HttpSession session) {
        User sessionUser = (User) session.getAttribute("sessionUser");

        return "board/save-form";
    }

    /**
     * 게시글 작성 요청 기능
     * @param saveDTO
     * @param session
     * @return
     */
    @PostMapping("/board/save")
    public String saveProc(BoardRequest.SaveDTO saveDTO, HttpSession session) {
        // 인증 처리
        User sessionUser = (User) session.getAttribute("sessionUser");

        boardService.게시글작성(saveDTO, sessionUser);

        return "redirect:/";
    }

    /**
     * 기능 삭제 요청 기능
     * @param id
     * @param session
     * @return
     */
    @PostMapping("/board/{id}/delete")
    public String delete(@PathVariable Long id, HttpSession session) {
        // 1. 인증 처리
        User sessionUser = (User) session.getAttribute("sessionUser");

        // 2. 인가 처리 || 관리자 권한
        boardService.게시글삭제(id, sessionUser.getId());

        return "redirect:/";
    }

    /**
     * 게시글 상세보기 화면 요청
     * @param id
     * @param model
     * @return
     */
    @GetMapping("board/{id}")
    public String detail(@PathVariable Long id, Model model, HttpSession session) {
        BoardResponse.DetailDTO board = boardService.게시글상세보기(id);

        User sessionUser = (User) session.getAttribute("sessionUser");
        Long sessionUserId = sessionUser != null ? sessionUser.getId() : null;

        List<ReplyResponse.ListDto> replyList = replyService.댓글목록조회(id, sessionUserId);

        boolean isOwner = false;
        if(sessionUser != null && board.getUserId() != null) {
            isOwner = board.getUserId().equals(sessionUser.getId());
        }

        model.addAttribute("board", board);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("replyList", replyList);

        return "board/detail";
    }
}
