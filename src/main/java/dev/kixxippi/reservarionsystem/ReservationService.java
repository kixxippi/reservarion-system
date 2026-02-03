package dev.kixxippi.reservarionsystem;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ReservationService {


    private final Map<Long, Reservation> reservationMap;
    public final AtomicLong idCounter; // long with многопоточность

    public ReservationService(){
        reservationMap = new HashMap<>();
        idCounter = new AtomicLong();
    }

    public Reservation getReservationById(
            Long id
    ) {
       if(!reservationMap.containsKey(id)) {
           throw new NoSuchElementException("Not found reservation with id " + id);
       }
        return reservationMap.get(id);
    }

    public List<Reservation> findAllReservation() {
        return reservationMap.values().stream().toList();
    }

    public Reservation createReservation(Reservation reservationToCreate) {
        if(reservationToCreate.getId() != null) {
            throw new IllegalArgumentException("Id should be empty");
        }
        if (reservationToCreate.getStatus() != null) {
            throw new IllegalArgumentException("Status should be empty");
        }
        var newReservation = new Reservation(
                idCounter.incrementAndGet(),
                reservationToCreate.getUserId(),
                reservationToCreate.getRoomId(),
                reservationToCreate.getStartDate(),
                reservationToCreate.getEndDate(),
                ReservationStatus.PENDING
        );
        reservationMap.put(newReservation.getId(), newReservation);
        return newReservation;
    }

    public Reservation updateReservation(
            Long id,
            Reservation reservationToUpdate
    ) {
        if(!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation with id " + id);
        }
        var reservation = reservationMap.get(id);
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot modify reservation with status " + reservation.getStatus());
        }
        var updatedReservation = new Reservation(
                reservation.getId(),
                reservationToUpdate.getUserId(),
                reservationToUpdate.getRoomId(),
                reservationToUpdate.getStartDate(),
                reservationToUpdate.getEndDate(),
                ReservationStatus.PENDING
        );
        reservationMap.put(updatedReservation.getId(), updatedReservation);
        return updatedReservation;
    }

    public void deleteReservation(Long id) {
        if(!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation with id " + id);
        }
        reservationMap.remove(id);
    }

    public Reservation approveReservation(Long id) {
        if(!reservationMap.containsKey(id)) {
            throw new NoSuchElementException("Not found reservation with id " + id);
        }
        var reservation = reservationMap.get(id);
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Cannot approve reservation with status " + reservation.getStatus());
        }
        var isConflict = isReservationConflict(reservation);
        if(isConflict) {
            throw new IllegalStateException("Cannot approve reservation because of conflict");
        }
        var approvedReservation = new Reservation(
                reservation.getId(),
                reservation.getUserId(),
                reservation.getRoomId(),
                reservation.getStartDate(),
                reservation.getEndDate(),
                ReservationStatus.APPROVED
        );
        reservationMap.put(approvedReservation.getId(), approvedReservation);
        return approvedReservation;
    }

    private boolean isReservationConflict(
            Reservation reservation
    ){
        for(Reservation existingReservation:  reservationMap.values()) {
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
}
