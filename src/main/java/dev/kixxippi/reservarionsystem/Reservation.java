package dev.kixxippi.reservarionsystem;



import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Reservation {
    private final Long id;  // final обязательно!
    private final Long userId;
    private final Long roomId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final ReservationStatus status;
}