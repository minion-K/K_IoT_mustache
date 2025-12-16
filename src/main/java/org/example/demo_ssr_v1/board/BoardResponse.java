package org.example.demo_ssr_v1.board;

import lombok.Data;
import org.example.demo_ssr_v1._cors.utils.MyDateUtil;
import org.example.demo_ssr_v1.user.User;

/**
 * 응답 DTO
 */
public class BoardResponse {
    /**
     * 게시글 목록 응답 DTO
     */
    @Data
    public static class ListDTO{
        private Long id;
        private String title;
        private String username; // 작성자명 (평탄화)
        private String createdAt;

        public ListDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            // 쿼리 -> JOIN FETCH로 가져오면 문제 없음
            if(board.getUser() != null) {
                this.username = board.getUser().getUsername();
            }

            // 날짜 포맷팅
            if(board.getCreatedAt() != null) {
                this.createdAt = MyDateUtil.KstString(board.getCreatedAt());
            }
        }
    }// end of static inner class

    /**
     * 게시글 상세 응답 DTO
     */
    @Data
    public static class DetailDTO {
        private Long id;
        private String title;
        private String content;
        private Long userId;
        private String username;
        private String createdAt;

        public DetailDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
            // JOIN FETCH 활용 (한번에 JOIN 해서 Repository에서 가져옴)
            if(board.getUser() != null) {
                this.userId = board.getUser().getId();
                this.username = board.getUser().getUsername();
                this.createdAt = MyDateUtil.KstString(board.getCreatedAt());
            }
        }
    } // end of class

    /**
     * 게시글 수정 화면 응답 DTO
     */
    @Data
    public static class updateFormDTO {
        private Long id;
        private String title;
        private String content;

        public updateFormDTO(Board board) {
            this.id = board.getId();
            this.title = board.getTitle();
            this.content = board.getContent();
        }
    }
}
