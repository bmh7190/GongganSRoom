package inhagonggan.studyroom.controller;

import inhagonggan.studyroom.entity.Member;
import inhagonggan.studyroom.entity.Role;
import inhagonggan.studyroom.service.MemberService;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    private final MemberService memberService;

    public LoginController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model){
        model.addAttribute("member", new Member());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("member") Member member, Model model) {
        try {
            // 1) 회원가입 진행
            memberService.join(member);
            // 2) 가입 성공 → 로그인 페이지로
            return "redirect:/login";
        } catch (IllegalStateException e) {
            // 중복 회원 에러 메시지 등 처리
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
    }
}
