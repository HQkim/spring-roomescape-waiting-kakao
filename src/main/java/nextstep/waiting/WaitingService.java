package nextstep.waiting;

import auth.AuthenticationException;
import auth.TokenMember;
import lombok.RequiredArgsConstructor;
import nextstep.member.Member;
import nextstep.reservation.Reservation;
import nextstep.reservation.ReservationDao;
import nextstep.schedule.Schedule;
import nextstep.schedule.ScheduleDao;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WaitingService {
    private final ScheduleDao scheduleDao;
    private final ReservationDao reservationDao;
    private final WaitingDao waitingDao;

    public WaitingCreateResultDTO create(Member member, WaitingRequest waitingRequest) {
        Schedule schedule = scheduleDao.findById(waitingRequest.getScheduleId());
        if (schedule == null) {
            throw new NullPointerException();
        }

        List<Reservation> reservation = reservationDao.findByScheduleId(schedule.getId());
        if (!reservation.isEmpty()) {
            Waiting newWaiting = new Waiting(
                    schedule,
                    member
            );

            Long id = waitingDao.save(newWaiting);
            return new WaitingCreateResultDTO(true, id);
        }

        Reservation newReservation = new Reservation(
                schedule,
                member
        );

        Long id = reservationDao.save(newReservation);
        return new WaitingCreateResultDTO(false, id);
    }


    public void deleteById(Member member, Long id) {
        Waiting waiting = waitingDao.findById(id);
        if (waiting == null) {
            throw new NullPointerException();
        }

        if (!waiting.isSameMember(member)) {
            throw new AuthenticationException();
        }

        reservationDao.deleteById(id);
    }

    public List<MyWaiting> findAllByMember(TokenMember member) {
        return waitingDao.findAllByMemberId(member.getId());
    }
}
