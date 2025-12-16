package org.example.demo_ssr_v1.user;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1._cors.errors.exception.Exception400;
import org.example.demo_ssr_v1._cors.errors.exception.Exception403;
import org.example.demo_ssr_v1._cors.errors.exception.Exception404;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Service -> 응답 DTO 설계 후 전달 -> Controller

/**
 * 사용자 Service Layer
 *
 * 1. 역할
 *  - 비즈니스 로직을 처리하는 계층
 *  - Controller와 Repository 사이의 중간 계층
 *  - 트랜잭션 관리
 *  - 여러 Repository를 조합하여 복잡한 비즈니스 로직을 처리
 */
@Service // IoC
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    // 객체 지향 개념 -> SOLID 원칙
    // DIP - 추상화가 높은 객체를 선언하는 것이 좋음
    private final UserRepository userRepository;

    @Transactional
    public User 회원가입(UserRequest.JoinDTO joinDTO) {
        // 1. 사용자명 중복 체크
        if(userRepository.findByUsername(joinDTO.getUsername()).isPresent()) {
            // isPresent - 있으면 true 반환, 없으면 false 반환
            throw new Exception400("이미 존재하는 사용자 명입니다.");
        }
        User user = joinDTO.toEntity();

        return userRepository.save(user);
    }

    public User 로그인(UserRequest.LoginDTO loginDTO) {
        // 사용자가 던진 값과 DB에 사용자 이름과 비밀번호 일치 여부 확인
        User user = userRepository.findByUsernameAndPassword(loginDTO.getUsername(), loginDTO.getPassword())
                .orElse(null);

        if(user == null) {
            throw new Exception400("사용자명 또는 비밀번호가 일치하지 않습니다.");
        }

        return user;
    }

    public User 회원정보수정화면(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        if(!user.isOwner(userId)) {
            throw new Exception403("회원 정보 수정 권한이 없습니다.");
        }

        return user;
    }

    // 데이터의 수정(더티 체킹 - 반드시 먼저 조회 -> 조회된 객체의 상태값 변경 -> 자동 반영
    // 1. 회원 정보 조회
    // 2. 인가 검사
    // 3. 엔티티 상태 변경 (더티 체킹)
    // 4. 트랜잭션이 일어나고 변경된 User 엔티티 반환
    @Transactional
    public User 회원정보수정(UserRequest.UpdateDTO updateDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        if(!user.isOwner(userId)) {
            throw new Exception403("회원 정보 수정 권한이 없습니다.");
        }

        // 객체 상태값 변경(트랜잭션이 끝나면 자동으로 commit 및 반영)
        user.update(updateDTO);

        return user;
    }
}
