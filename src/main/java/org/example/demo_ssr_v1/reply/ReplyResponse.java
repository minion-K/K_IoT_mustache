package org.example.demo_ssr_v1.reply;

import lombok.Data;
import org.example.demo_ssr_v1._cors.utils.MyDateUtil;

public class ReplyResponse {
    // 게시글 상세보기에서 댓글 목록 출력
    @Data
    public static class ListDto {
        private Long id;
        private String comment;
        private Long userId; // 댓글 작성자 ID
        private String username; // 댓글 작성자명 (평탄화)
        private String createdAt;
        private boolean isOwner; // 댓글 소유자 여부 확인(세션 ID 값과 비교)

        public ListDto(Reply reply, Long sessionId) {
            this.id = reply.getId();
            this.comment = reply.getComment();
            // Repository 에서 JOIN FETCH로 이미 로딩된 User
            if(reply.getUser() != null) {
                this.userId = reply.getUser().getId();
                this.username = reply.getUser().getUsername();
            }
            if(reply.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.KstString(reply.getCreatedAt());
            }
            // 댓글 소유자인지 확인 - true / false
            this.isOwner = reply.isOwner(sessionId);
        }
    }
}
