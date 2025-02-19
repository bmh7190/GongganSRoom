package inhagonggan.studyroom.controller;

import inhagonggan.studyroom.entity.Member;
import inhagonggan.studyroom.entity.Reservation;
import inhagonggan.studyroom.entity.StudyRoom;
import inhagonggan.studyroom.service.MemberService;
import inhagonggan.studyroom.service.ReservationService;
import inhagonggan.studyroom.service.StudyRoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final MemberService memberService;
    private final StudyRoomService studyRoomService;
    private final ReservationService reservationService;

    public AdminController(MemberService memberService, StudyRoomService studyRoomService, ReservationService reservationService) {
        this.memberService = memberService;
        this.studyRoomService = studyRoomService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/members")
    public String getAllMembers(Model model) {
        model.addAttribute("members", memberService.findMembers());
        return "admin/memberList";
    }

    @GetMapping("/studyrooms")
    public String manageStudyRooms(Model model) {
        model.addAttribute("studyRooms", studyRoomService.getAllRooms());
        return "admin/studyRoomList";
    }

    @GetMapping("/reservations")
    public String getAllReservations(Model model) {
        model.addAttribute("reservations", reservationService.getAllReservations());
        return "admin/reservationList";
    }

    @PostMapping("/studyrooms")
    public String addStudyRoom(@RequestParam String name, @RequestParam int capacity){

        StudyRoom studyRoom = new StudyRoom();

        studyRoom.setName(name);
        studyRoom.setCapacity(capacity);

        studyRoomService.addRoom(studyRoom);

        return "redirect:/admin/studyrooms";
    }
}
