package com.turminaz.myratingapp.match.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public record Match(UUID id, LocalDateTime startTime ) implements Serializable {
}
