package org.example.demo_ssr_v1.reply;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_ssr_v1._cors.errors.exception.Exception400;
import org.example.demo_ssr_v1.board.Board;
import org.example.demo_ssr_v1.user.User;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Table(name = "reply_tb")
@Entity
@NoArgsConstructor
@Data
public class Reply {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 500)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @CreationTimestamp
    private Timestamp createdAt;

    @Builder
    public Reply(String comment, Board board, User user) {
        this.comment = comment;
        this.board = board;
        this.user = user;
    }

    // 소유자 여부 확인(댓글)
    public boolean isOwner(Long userId) {
        if(this.user == null || userId == null) {
            return false;
        }

        Long replyUserId = this.user.getId();
        if(replyUserId == null) return false;

        boolean result = replyUserId.equals(userId);

        return result;
    }

    // 댓글 내용 수정
    public void update(String newString) {
        if(newString == null || newString.trim().isEmpty()) {
            throw new Exception400("댓글 내용을 입력해주세요.");
        }

        if(newString.length() > 500) {
            throw new Exception400("댓글을 500자 이하여야 합니다.");
        }
    }
}
