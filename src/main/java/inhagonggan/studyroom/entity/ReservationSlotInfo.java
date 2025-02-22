package inhagonggan.studyroom.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter @Setter
public class ReservationSlotInfo {


    private List<StudyRoom> rooms;
    private List<LocalTime> timeSlots;
    private Map<Long, Set<LocalTime>> reservedSlotsMap;
    private LocalDate targetDate;

    public ReservationSlotInfo(List<StudyRoom> rooms, List<LocalTime> timeSlots,  Map<Long, Set<LocalTime>> reservedSlotsMap, LocalDate targetDate) {
        this.timeSlots = timeSlots;
        this.rooms = rooms;
        this.reservedSlotsMap = reservedSlotsMap;
        this.targetDate = targetDate;
    }

}
