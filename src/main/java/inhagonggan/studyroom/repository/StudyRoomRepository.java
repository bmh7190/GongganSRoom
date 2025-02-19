package inhagonggan.studyroom.repository;

import inhagonggan.studyroom.entity.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {

    StudyRoom findByName(String name);
}
