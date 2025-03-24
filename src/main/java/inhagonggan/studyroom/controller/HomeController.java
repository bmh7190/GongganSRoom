package inhagonggan.studyroom.controller;

import inhagonggan.studyroom.entity.Member;
import inhagonggan.studyroom.entity.Reservation;
import inhagonggan.studyroom.entity.ReservationSlotInfo;
import inhagonggan.studyroom.entity.StudyRoom;
import inhagonggan.studyroom.service.MemberService;
import inhagonggan.studyroom.service.ReservationService;
import inhagonggan.studyroom.service.StudyRoomService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final StudyRoomService studyRoomService;
    private final ReservationService reservationService;
    private final MemberService memberService;

    public HomeController(StudyRoomService studyRoomService, ReservationService reservationService, MemberService memberService) {
        this.studyRoomService = studyRoomService;
        this.reservationService = reservationService;
        this.memberService = memberService;
    }

    @PostMapping("/reservations")
    public String reserveStudyRoom(@RequestParam Long roomId,
                                   @RequestParam String startTime,
                                   @RequestParam String endTime,
                                   @AuthenticationPrincipal User user) {

        Member member = memberService.findByNumber(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        StudyRoom studyRoom = studyRoomService.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("스터디룸을 찾을 수 없습니다."));

        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);

        reservationService.addReservation(member, studyRoom, start, end );

        return "redirect:/";
    }

    @GetMapping("/")
    public String showHome(Model model, @AuthenticationPrincipal User user ) {
        // 오늘 날짜 기준
        LocalDate targetDate = LocalDate.now();

        // Service에서 모든 로직 처리 후 DTO/VO 등으로 받음
        ReservationSlotInfo slotInfo = reservationService.getReservationSlotInfo(targetDate);

        Member member = memberService.findByNumber(user.getUsername()).
                orElseThrow(() -> new IllegalArgumentException("사용자 정보 없음"));

        // 🔥 예약 정보 가져오기
        List<Reservation> reservations = reservationService.getAllReservations(); // 또는 하루치만 추출해도 OK

        // 🔑 예약을 슬롯별로 매핑 (roomId + time → 예약 객체)
        Map<String, Reservation> reservationMap = new HashMap<>();
        for (Reservation res : reservations) {
            String key = res.getStudyRoom().getId() + res.getStartTime().toLocalTime().toString();
            reservationMap.put(key, res);
        }

        // 모델에 추가
        model.addAttribute("rooms", slotInfo.getRooms());
        model.addAttribute("timeSlots", slotInfo.getTimeSlots());
        model.addAttribute("reservedSlotsMap", slotInfo.getReservedSlotsMap());
        model.addAttribute("targetDate", slotInfo.getTargetDate());
        model.addAttribute("userName",member.getName());
        model.addAttribute("userNumber", member.getNumber()); // 취소 버튼 조건용
        model.addAttribute("reservationMap", reservationMap); // 취소 버튼용

        return "home"; // home.html
    }

    @PostMapping("/reservations/delete")
    public String delete(@RequestParam Long reservationId,
                         @AuthenticationPrincipal User user) {
        Member member = memberService.findByNumber(user.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        reservationService.deleteReservation(reservationId, member);
        return "redirect:/";
    }

}
