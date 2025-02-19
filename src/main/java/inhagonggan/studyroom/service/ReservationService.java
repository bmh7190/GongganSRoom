package inhagonggan.studyroom.service;

import inhagonggan.studyroom.entity.Member;
import inhagonggan.studyroom.entity.Reservation;
import inhagonggan.studyroom.entity.StudyRoom;
import inhagonggan.studyroom.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    // 특정 시간대의 스터디룸 예약 확인 (중복 방지)
    public boolean isAvailable(Long studyRoomId, LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = reservationRepository.findOverlappingReservations(studyRoomId, start, end);
        return reservations.isEmpty();
    }

    // 예약 생성
    public Reservation addReservation(Member member, StudyRoom studyRoom, LocalDateTime startTime, LocalDateTime endTime) {
        if (!isAvailable(studyRoom.getId(), startTime, endTime)) {
            throw new RuntimeException("이미 예약된 시간입니다.");
        }

        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setStudyRoom(studyRoom);
        reservation.setStartTime(startTime);
        reservation.setEndTime(endTime);

        return reservationRepository.save(reservation);
    }

    // 예약 조회
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        return reservations;
    }

    // 예약 업데이트


    // 예약 취소
    public void deleteReservation(Long id){
            reservationRepository.findById(id).orElseThrow(()->
                    new IllegalArgumentException("해당 예약이 존재하지 않습니다"));
    }

    public void addReservation(Reservation reservation) {
    }
}
