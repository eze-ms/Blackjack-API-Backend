package com.ezequiel.itacademy.blackjack.mysql.repository;

import com.ezequiel.itacademy.blackjack.mysql.entity.Ranking;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface RankingRepository extends R2dbcRepository<Ranking, Long> {
    Mono<Ranking> findByPlayerId(Long playerId);
}
