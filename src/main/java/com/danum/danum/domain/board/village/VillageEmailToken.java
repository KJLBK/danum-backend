package com.danum.danum.domain.board.village;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "villageEmail", timeToLive = 86400)
public class VillageEmailToken {

    @Id
    private String id;

    @Indexed
    private String email;

}
