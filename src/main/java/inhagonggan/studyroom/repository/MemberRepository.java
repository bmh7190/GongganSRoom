package inhagonggan.studyroom.repository;

import inhagonggan.studyroom.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 학번(number)으로 회원 찾기 (로그인용)
    Optional<Member> findByNumber(String number);
}
