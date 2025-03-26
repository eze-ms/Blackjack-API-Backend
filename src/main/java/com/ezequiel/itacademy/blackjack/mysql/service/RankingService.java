package com.ezequiel.itacademy.blackjack.mysql.service;

import com.ezequiel.itacademy.blackjack.mysql.entity.Ranking;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

public interface RankingService {
    Mono<Ranking> getPlayerRanking(Long playerId);
    Flux<Ranking> getAllRankings();
    Mono<Ranking> updateRanking(Long playerId, int pointsToAdd);
}

