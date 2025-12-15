package org.example.demo_ssr_v1.board;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._cors.errors.exception.Exception403;
import org.example.demo_ssr_v1._cors.errors.exception.Exception404;
import org.example.demo_ssr_v1.user.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {
    private final BoardRepository boardRepository;

    /**
     * 게시글 목록 조회
     * 트랜잭션
     *  - 읽기 전용 트랜잭션 - 성능 최적화
     * @return 게시글 목록 (생성일 기준으로 내림차순)
     */
    public List<Board> 게시글목록조회() {
        return boardRepository.findByOrderByCreatedAtDesc();
    }

    public Board 게시글상세보기(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));
    }

    // 1. 트랜잭션 처리
    // 2. Repository 저장 처리
    @Transactional
    public Board 게시글작성(BoardRequest.SaveDTO saveDTO, User sessionUser) {
        // DTO에서 직접 new 하여 생성한 Board 객체일 뿐 아직 영속화된 객체는 아님
        Board board = saveDTO.toEntity(sessionUser);

        return boardRepository.save(board);
    }

    // 1. 게시글 조회
    // 2. 인가 처리
    public Board 게시글수정화면(Long boardId, Long sessionUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        if(!board.isOwner(sessionUserId)) {
            throw new Exception403("게시글 수정 권한이 없습니다.");
        }

        return board;
    }

    // 1. 트랜잭션 처리
    // 2. DB에서 조회
    // 3. 인가 처리
    // 4. 조회된 board의 상태값 변경 (더티 체킹)
    @Transactional
    public void 게시글수정(BoardRequest.UpdateDTO updateDTO, Long boardId, Long sessionUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        if(!board.isOwner(sessionUserId)) {
            throw new Exception403("게시글 수정 권한이 없습니다.");
        }

        board.update(updateDTO);
    }

    // 1. 트랜잭션 처리
    // 2. 게시글 조회
    // 3. 인가 처리
    // 4. Repository에 삭제 요청
    @Transactional
    public void 게시글삭제(Long boardId, Long sessionUserId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new Exception404("게시글을 찾을 수 없습니다."));

        if(!board.isOwner(sessionUserId)) {
            throw new Exception403("게시글 삭제 권한이 없습니다.");
        }

        boardRepository.deleteById(boardId);
    }
}
