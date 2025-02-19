package inhagonggan.studyroom.service;

import inhagonggan.studyroom.entity.StudyRoom;
import inhagonggan.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;

    // 모든 스터디룸 조회
    public List<StudyRoom> getAllRooms() {
        return studyRoomRepository.findAll();
    }

    // 스터디룸 추가 (관리자 전용)
    public StudyRoom addRoom(StudyRoom studyRoom) {
        return studyRoomRepository.save(studyRoom);
    }

    // 스터디룸 삭제
    public void deleteRoom(Long id) {
        studyRoomRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("해당 ID의 스터디룸이 존재하지 않습니다.")
        );

        studyRoomRepository.deleteById(id);
    }

    public Optional<StudyRoom> findById(Long roomId) {

        return studyRoomRepository.findById(roomId);
    }
}
