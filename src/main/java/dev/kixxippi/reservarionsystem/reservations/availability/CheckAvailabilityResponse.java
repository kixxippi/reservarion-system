package dev.kixxippi.reservarionsystem.reservations.availability;

public record CheckAvailabilityResponse(
        String message,
        AvailabiltyStatus status
) {
}
