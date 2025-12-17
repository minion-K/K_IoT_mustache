package org.example.demo_ssr_v1.board;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 자동 제공 메서드(별도 구현없이 사용 가능)
    // - save(T entity): (Insert 또는 Update)
    // - findById(ID id): ID로 엔티티 조회 (Optional<T> 반환)
    // - findAll()
    // - deleteById(ID id): ID로 엔티티 삭제
    // - count(): 전체 개수 조회
    // - existsById(ID id): ID 존재 여부 확인

    // 전체 조회
    // LAZY 로딩이라 한번에 username을 가져와야 함
    // JOIN FETCH
//    @Query("SELECT b FROM Board b JOIN FETCH b.user ORDER BY b.createdAt DESC")
//    List<Board> findByWithUserOrderByCreatedAtDesc();

    // 전체 조회 (페이징 처리) - 인수 값 생성한 Pageable 객체를 전달
    // 리턴 타입은 Page 객체로 반환

    /**
     *
     * @param pageable 페이징 정보(페이지 번호, 페이지 크기, 정렬)
     * @return 페이징된 BoardList를 가지고 있음(단, 작성자 정보 포함)
     *
     * ** JOIN FETCH 때문에 Hibernate가 쿼리를 이상하게 작성하는 것을 방지 **
     * select 절에 DISTINCT를 사용하면 정확한 count를 가져올 수 있음
     * countQuery - 전체 게시글에 개수를 빠르게 가져오기 위해 사용. 성능 상의 문제
     */
    @Query(value = "SELECT DISTINCT b FROM Board b JOIN FETCH b.user ORDER BY b.createdAt DESC",
    countQuery = "SELECT COUNT(DISTINCT b) FROM Board b")
    Page<Board> findByWithUserOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT b FROM Board b JOIN FETCH b.user WHERE b.id = :id")
    Optional<Board> findByWithUser(@Param("id") Long id);
}
