package com.ezequiel.itacademy.blackjack.mysql.service;

import com.ezequiel.itacademy.blackjack.mysql.entity.Ranking;
import com.ezequiel.itacademy.blackjack.mysql.repository.RankingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RankingServiceImpl implements RankingService {

    private final RankingRepository rankingRepository;

    public RankingServiceImpl(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    @Override
    public Mono<Ranking> getPlayerRanking(Long playerId) {
        return rankingRepository.findByPlayerId(playerId);
    }

    @Override
    public Flux<Ranking> getAllRankings() {
        return rankingRepository.findAll();
    }

    @Override
    public Mono<Ranking> updateRanking(Long playerId, int pointsToAdd) {
        return rankingRepository.findByPlayerId(playerId)
                .flatMap(existingRanking -> {
                    existingRanking.setPoints(existingRanking.getPoints() + pointsToAdd);
                    return rankingRepository.save(existingRanking);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    Ranking newRanking = new Ranking();
                    newRanking.setPlayerId(playerId);
                    newRanking.setPoints(pointsToAdd);
                    return rankingRepository.save(newRanking);
                }));
    }
}
