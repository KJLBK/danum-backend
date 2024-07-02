package com.danum.danum.repository;

import com.danum.danum.domain.board.village.VillageEmailToken;
import org.springframework.data.repository.CrudRepository;

public interface VillageEmailRepository extends CrudRepository<VillageEmailToken, Long> {
}
