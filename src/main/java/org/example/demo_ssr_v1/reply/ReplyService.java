package org.example.demo_ssr_v1.reply;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._cors.errors.exception.Exception403;
import org.example.demo_ssr_v1._cors.errors.exception.Exception404;
import org.example.demo_ssr_v1.board.Board;
import org.example.demo_ssr_v1.board.BoardRepository;
import org.example.demo_ssr_v1.user.User;
import org.example.demo_ssr_v1.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    // 댓글 목록 조회기능
    /**
     * OSIV 대응하기 위해 DTO 설계, 계층 간 결합도를 줄이기 위해 설계
     * - JOIN FETCH로 한번에 User를 가져옴
     */
    public List<ReplyResponse.ListDto> 댓글목록조회(Long boardId, Long sessionUserId) {

        // 1. 조회 -> List<Reply>
        // 2. 인가 처리
        // 3. 데이터 변환(DTO 생성) List<ReplyResponse.ListDTO>
        List<Reply> replyList = replyRepository.findByBoardIdWithUserIdOrderByASC(boardId);

        return replyList.stream()
                .map(reply -> new ReplyResponse.ListDto(reply, sessionUserId))
                .collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public Reply 댓글작성(ReplyRequest.SaveDTO saveDTO, Long sessionUserId) {
        // 조회 -> 영속상태
        Board board = boardRepository.findById(saveDTO.getBoardId())
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(sessionUserId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        // 비영속상태
        Reply reply = saveDTO.toEntity(board, user);

        return replyRepository.save(reply);
    }

    // 댓글 삭제
    @Transactional
    public Long 댓글삭제(Long replyId, Long sessionUserId) {
        // 댓글 조회 findById(replyId) -> LAZY 때문에 댓글 작성자 정보는 없음
        // -> 소유자 확인을 해야하므로 댓글 작성자 정보가 함께 필요
        // 권한 체크
        // 댓글 삭제 처리
        Reply reply = replyRepository.findByIdWithUser(replyId)
                .orElseThrow(() -> new Exception404("해당 댓글을 찾을 수 없습니다."));

        boolean isOwner = reply.isOwner(sessionUserId);
        if(!isOwner) {
            throw new Exception403("게시글 삭제 권한이 없습니다.");
        }

        replyRepository.delete(reply);

        // 화면 재 렌더링을 위한 boardId 전달
        Long boardId = reply.getBoard().getId();

        return boardId;
    }
}
