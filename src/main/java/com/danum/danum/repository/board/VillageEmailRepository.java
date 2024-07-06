package com.danum.danum.repository.board;

import com.danum.danum.domain.board.village.VillageEmailToken;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VillageEmailRepository extends CrudRepository<VillageEmailToken, Long> {
}
