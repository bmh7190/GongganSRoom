package inhagonggan.studyroom.service;

import inhagonggan.studyroom.entity.Member;
import inhagonggan.studyroom.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }


    // 💡 회원 가입
    public Long join(Member member) {
        // 중복 회원 체크
        validateDuplicateMember(member);

        // 비밀번호 암호화
        member.setPassword(passwordEncoder.encode(member.getPassword()));

        // 회원 저장
        memberRepository.save(member);
        return member.getId();
    }

    // 중복 회원 검증
    private void validateDuplicateMember(Member member) {
        memberRepository.findByNumber(member.getNumber())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 학번입니다.");
                });
    }


    public List<Member> findMembers() {

        List<Member> members = memberRepository.findAll();

        return members;
    }


    public Optional<Member> findByNumber(String username) {

        return memberRepository.findByNumber(username);
    }
}
