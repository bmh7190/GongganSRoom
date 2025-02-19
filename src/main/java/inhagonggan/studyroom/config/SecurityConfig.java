package inhagonggan.studyroom.config;

import inhagonggan.studyroom.entity.Member;
import inhagonggan.studyroom.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN") // ✅ 관리자 전용 페이지 보호
                        .requestMatchers("/members/join", "/login").permitAll() // ✅ 회원가입, 로그인은 누구나 접근 가능
                        .anyRequest().authenticated() // ✅ 나머지 요청은 로그인 필요
                )
                .formLogin(login -> login
                        .successHandler((request, response, authentication) -> {
                            if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin"); // ✅ 관리자 로그인 후 이동
                            } else {
                                response.sendRedirect("/");  // ✅ 일반 사용자는 /home으로 이동
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login") // ✅ 로그아웃 후 로그인 페이지로 이동
                        .permitAll()
                );

        return http.build();
    }



    @Bean
    public UserDetailsService userDetailsService(MemberRepository memberRepository) {
        return number -> {
            Member member = memberRepository.findByNumber(number)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            // ✅ 권한을 'ROLE_' 접두사 포함하여 설정
            List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + member.getRole()));

            return new org.springframework.security.core.userdetails.User(
                    member.getNumber(),  // ✅ 학번을 로그인 ID로 사용
                    member.getPassword(),
                    authorities  // ✅ Spring Security에서 권한 설정
            );
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
