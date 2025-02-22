package inhagonggan.studyroom.repository;

import inhagonggan.studyroom.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r WHERE r.studyRoom.id = :studyRoomId " +
            "AND ((r.startTime < :endTime AND r.endTime > :startTime))")
    List<Reservation> findReservationsByRoomAndDay(
            @Param("studyRoomId") Long studyRoomId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    // 특정 회원의 예약 목록 조회
    List<Reservation> findByMemberId(Long memberId);


}
