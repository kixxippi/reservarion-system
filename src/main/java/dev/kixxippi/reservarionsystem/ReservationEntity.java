package dev.kixxippi.reservarionsystem;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Table(name = "reservations")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")// можно не использовать так как названия одинаковые
    private ReservationStatus status;
}
