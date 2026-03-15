package dev.kixxippi.reservarionsystem.reservations.availability;

import dev.kixxippi.reservarionsystem.reservations.ReservationRepository;
import dev.kixxippi.reservarionsystem.reservations.ReservationStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class ReservationAvailabilityService {

    private static final Logger log = LoggerFactory.getLogger(ReservationAvailabilityController.class);
    private ReservationRepository repository;

    public ReservationAvailabilityService(ReservationRepository repository) {
        this.repository = repository;
    }

    public boolean isReservationAvailable(
            Long roomId,
            LocalDate startDate,
            LocalDate endDate
    ){

        if(!endDate.isAfter(startDate)){
            throw new IllegalArgumentException("Start date should be earlier end date");
        }

        List<Long> conflictingIds = repository.findConflictReservationIds(
                roomId,
                startDate,
                endDate,
                ReservationStatus.APPROVED
        );
        if(conflictingIds.isEmpty()) {
            return true;
        }
        log.info("Conflict with ids={}", conflictingIds);
        return false;
    }
}
