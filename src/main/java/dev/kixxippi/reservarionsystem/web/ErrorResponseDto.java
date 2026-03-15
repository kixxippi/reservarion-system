package dev.kixxippi.reservarionsystem.web;

import java.time.LocalDateTime;

public record ErrorResponseDto (
    String message,
    String detailedMessage,
    LocalDateTime errorRime
){
}
