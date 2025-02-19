package inhagonggan.studyroom.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "studyroom_id")
    private StudyRoom studyRoom;

    @Column(nullable = false)
    private LocalDateTime startTime; // 예약 시작 시간

    @Column(nullable = false)
    private LocalDateTime endTime; // 예약 종료 시간

    private boolean isActive = true; // 예약 상태
}
