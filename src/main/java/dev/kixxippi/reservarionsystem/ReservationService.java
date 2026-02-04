package dev.kixxippi.reservarionsystem;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.logging.Logger;

@Service
public class ReservationService {

    private static final Logger log = Logger.getLogger(ReservationService.class.getName());

    private final ReservationRepository repository;

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    public Reservation getReservationById(
            Long id
    ) {
        ReservationEntity reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Not found reservation with id " + id
                ));

        return toDomainReservation(reservationEntity);
    }

    public List<Reservation> findAllReservation() {
        List<ReservationEntity> allEntities = repository.findAll();

        return allEntities.stream()
                .map(it -> toDomainReservation(it))
                .toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if(reservationToCreate.getId() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (reservationToCreate.getStatus() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        var entityToSave = new ReservationEntity(
                null,
                reservationToCreate.getUserId(),
                reservationToCreate.getRoomId(),
                reservationToCreate.getStartDate(),
                reservationToCreate.getEndDate(),
                ReservationStatus.PENDING
        );

        var savedEntity = repository.save(entityToSave);

        return toDomainReservation(savedEntity);
    }

    public Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation with id " + id));

        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot modify reservation with status " + reservationEntity.getStatus());
        }
        var reservationToSave = new ReservationEntity(
                reservationEntity.getId(),
                reservationToUpdate.getUserId(),
                reservationToUpdate.getRoomId(),
                reservationToUpdate.getStartDate(),
                reservationToUpdate.getEndDate(),
                ReservationStatus.PENDING
        );
        var updatedReservation = repository.save(reservationToSave);
        return toDomainReservation(updatedReservation);
    }

    @Transactional
    public void cancelReservation(Long id) {
        if(!repository.existsById(id)) {
            throw new NoSuchElementException("Not found reservation with id " + id);
        }
        repository.setStatus(id, ReservationStatus.CANCELLED);
        log.info("Successfully cancelled reservation with id " + id);
    }

    public Reservation approveReservation(Long id) {
        var reservationEntity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Not found reservation with id " + id));
        if (reservationEntity.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot approve reservation with status " + reservationEntity.getStatus());
        }

        var isConflict = isReservationConflict(reservationEntity);

        if(isConflict) {
            throw new IllegalStateException("Cannot approve reservation because of conflict");
        }
        reservationEntity.setStatus(ReservationStatus.APPROVED);
        repository.save(reservationEntity);

        return toDomainReservation(reservationEntity);
    }

    private boolean isReservationConflict(
            ReservationEntity reservation
    ){
        var allReservations = repository.findAll();
        for(ReservationEntity existingReservation:  allReservations) {
            if(reservation.getId().equals(existingReservation.getId())) {
                continue;
            }
            if(!reservation.getRoomId().equals(existingReservation.getRoomId())) {
                continue;
            }
            if(!existingReservation.getStatus().equals(ReservationStatus.APPROVED)) {
                continue;
            }
            if(reservation.getStartDate().isBefore(existingReservation.getEndDate())
            && existingReservation.getStartDate().isBefore(reservation.getEndDate())) {
                return true;
            }
        }
        return false;
    }

    private Reservation toDomainReservation(ReservationEntity reservationEntity) {
        return new Reservation(
                reservationEntity.getId(),
                reservationEntity.getUserId(),
                reservationEntity.getRoomId(),
                reservationEntity.getStartDate(),
                reservationEntity.getEndDate(),
                reservationEntity.getStatus()
        );
    }
}
