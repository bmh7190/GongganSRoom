package inhagonggan.studyroom.service;

import inhagonggan.studyroom.entity.Member;
import inhagonggan.studyroom.entity.Reservation;
import inhagonggan.studyroom.entity.ReservationSlotInfo;
import inhagonggan.studyroom.entity.StudyRoom;
import inhagonggan.studyroom.repository.ReservationRepository;
import inhagonggan.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final StudyRoomRepository studyRoomRepository;

    // 특정 시간대의 스터디룸 예약 확인 (중복 방지)
    public boolean isAvailable(Long studyRoomId, LocalDateTime start, LocalDateTime end) {
        List<Reservation> reservations = reservationRepository.findReservationsByRoomAndDay(studyRoomId, start, end);
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

    public ReservationSlotInfo getReservationSlotInfo(LocalDate targetDate) {
        // 1) 방 목록 조회
        List<StudyRoom> rooms = studyRoomRepository.findAll();

        // 2) 09:00 ~ 21:00, 30분 단위 슬롯
        List<LocalTime> timeSlots = generateTimeSlots(LocalTime.of(9, 0), LocalTime.of(21, 0));

        // 3) 예약된 슬롯 수집
        Map<Long, Set<LocalTime>> reservedSlotsMap = new HashMap<>();

        // ★ 메서드 파라미터로 받은 targetDate 사용
        LocalDateTime startOfDay = targetDate.atStartOfDay();      // 예) 2025-03-03T00:00
        LocalDateTime endOfDay   = targetDate.atTime(LocalTime.MAX);// 예) 2025-03-03T23:59:59.999999999

        // 각 방마다 예약 조회
        for (StudyRoom room : rooms) {
            // ★ room.getId() 사용
            List<Reservation> reservations = reservationRepository.findReservationsByRoomAndDay(
                    room.getId(), startOfDay, endOfDay
            );

            Set<LocalTime> roomReserved = new HashSet<>();
            for (Reservation res : reservations) {
                LocalDateTime start = res.getStartTime();
                LocalDateTime end   = res.getEndTime();
                LocalDateTime cur   = start;

                // 30분씩 반복하며 해당 룸이 이미 예약된 timeSlot 수집
                while (!cur.isAfter(end.minusMinutes(1))) {
                    roomReserved.add(cur.toLocalTime());
                    cur = cur.plusMinutes(30);
                }
            }
            reservedSlotsMap.put(room.getId(), roomReserved);
        }

        // 결과를 DTO(ReservationSlotInfo)에 담아 반환
        return new ReservationSlotInfo(rooms, timeSlots, reservedSlotsMap, targetDate);
    }

    // 30분 단위 슬롯 생성
    private List<LocalTime> generateTimeSlots(LocalTime start, LocalTime end) {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime cur = start;
        while (!cur.isAfter(end)) {
            slots.add(cur);
            cur = cur.plusMinutes(30);
        }
        return slots;
    }
}
