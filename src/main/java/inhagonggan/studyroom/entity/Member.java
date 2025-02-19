package inhagonggan.studyroom.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@Entity
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String number;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true) // 🔥 양방향 관계 설정
    private List<Reservation> orders = new ArrayList<>();
}
