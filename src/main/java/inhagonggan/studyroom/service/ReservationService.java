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

    // 예약 취소
    public void deleteReservation(Long id, Member user){
        Reservation reservation = reservationRepository.findById(id).orElseThrow(()->
                    new IllegalArgumentException("해당 예약이 존재하지 않습니다"));

        if(!reservation.getMember().getId().equals(user.getId())){
            throw new IllegalStateException("본인의 예약만 취소할 수 있습니다.");
        }
        reservationRepository.delete(reservation);
    }



    public ReservationSlotInfo getReservationSlotInfo(LocalDate targetDate) {

        List<StudyRoom> rooms = studyRoomRepository.findAll();
        List<LocalTime> timeSlots = generateTimeSlots(LocalTime.of(9, 0), LocalTime.of(21, 0));

        Map<Long, Set<LocalTime>> reservedSlotsMap = new HashMap<>();

        LocalDateTime startOfDay = targetDate.atStartOfDay();
        LocalDateTime endOfDay   = targetDate.atTime(LocalTime.MAX);

        for (StudyRoom room : rooms) {

            List<Reservation> reservations = reservationRepository.findReservationsByRoomAndDay(
                    room.getId(), startOfDay, endOfDay
            );

            Set<LocalTime> roomReserved = new HashSet<>();
            for (Reservation res : reservations) {
                LocalDateTime start = res.getStartTime();
                LocalDateTime end   = res.getEndTime();
                LocalDateTime cur   = start;

                while (!cur.isAfter(end.minusMinutes(1))) {
                    roomReserved.add(cur.toLocalTime());
                    cur = cur.plusMinutes(30);
                }
            }
            reservedSlotsMap.put(room.getId(), roomReserved);
        }

        return new ReservationSlotInfo(rooms, timeSlots, reservedSlotsMap, targetDate);
    }

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
