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
import java.util.List;

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
    public String showHome(Model model) {
        // 오늘 날짜 기준
        LocalDate targetDate = LocalDate.now();

        // Service에서 모든 로직 처리 후 DTO/VO 등으로 받음
        ReservationSlotInfo slotInfo = reservationService.getReservationSlotInfo(targetDate);

        // 모델에 추가
        model.addAttribute("rooms", slotInfo.getRooms());
        model.addAttribute("timeSlots", slotInfo.getTimeSlots());
        model.addAttribute("reservedSlotsMap", slotInfo.getReservedSlotsMap());
        model.addAttribute("targetDate", slotInfo.getTargetDate());
        model.addAttribute("userName", "홍길동");

        return "home"; // home.html
    }


}
