package com.ezequiel.itacademy.blackjack.mysql.repository;

import com.ezequiel.itacademy.blackjack.mysql.entity.Ranking;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RankingRepository extends ReactiveCrudRepository<Ranking, Long> {
    Mono<Ranking> findByPlayerId(Long playerId);
}
