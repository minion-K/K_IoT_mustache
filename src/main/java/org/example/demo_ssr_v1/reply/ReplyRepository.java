package org.example.demo_ssr_v1.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {
    // 댓글 목록 - 시간 내림차순
    /**
     * SELECT r.*, b.*, u.*
     * FROM reply_tb r
     * INNER JOIN board_tb b ON r.board_id = b.id
     * INNER JOIN user_tb u ON r.user_id = u.id
     * WHERE r.board_id = ?
     * ORDER BY r.created_at DESC
     */
    @Query("SELECT r\n" +
            "FROM Reply r\n" +
            "JOIN FETCH r.board\n" +
            "JOIN FETCH r.user\n" +
            "WHERE r.board.id = :boardId\n" +
            "ORDER BY r.createdAt ASC")
    List<Reply> findByBoardIdWithUserIdOrderByASC(@Param("boardId") Long boardId);

    @Query("SELECT r " +
            "FROM Reply r " +
            "JOIN FETCH r.user " +
            "JOIN FETCH r.board " +
            "WHERE r.id = :id")
    Optional<Reply> findByIdWithUser(@Param("id") Long id);

    // 댓글 삭제
    // 게시글 ID로 댓글 삭제
    void deleteByBoardId(Long boardId);
}
