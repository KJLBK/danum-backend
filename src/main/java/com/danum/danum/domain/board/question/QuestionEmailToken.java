package com.danum.danum.domain.board.question;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash(value = "questionEmail", timeToLive = 86400)
public class QuestionEmailToken {

    @Id
    private Long id;

    @Indexed
    private String email;

}
