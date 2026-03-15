package dev.kixxippi.reservarionsystem.reservations;



import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
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
    @Null
    private final Long id;  // final обязательно!
    @NotNull
    private final Long userId;
    @NotNull
    private final Long roomId;
    @FutureOrPresent
    @NotNull
    private final LocalDate startDate;
    @FutureOrPresent
    @NotNull
    private final LocalDate endDate;
    private final ReservationStatus status;
}