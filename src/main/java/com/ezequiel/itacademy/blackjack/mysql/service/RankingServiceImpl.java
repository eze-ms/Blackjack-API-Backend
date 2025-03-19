package com.ezequiel.itacademy.blackjack.mysql.service;

import com.ezequiel.itacademy.blackjack.exception.PlayerNotFoundException;
import com.ezequiel.itacademy.blackjack.mysql.entity.Ranking;
import com.ezequiel.itacademy.blackjack.mysql.repository.RankingRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class RankingServiceImpl implements RankingService {

    private final RankingRepository rankingRepository;

    public RankingServiceImpl(RankingRepository rankingRepository) {
        this.rankingRepository = rankingRepository;
    }

    @Override
    public Mono<Ranking> getPlayerRanking(Long playerId) {
        return rankingRepository.findByPlayerId(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("El jugador con ID " + playerId + " no tiene un ranking registrado.")));
    }

    @Override
    public Mono<List<Ranking>> getAllRankings() {
        return rankingRepository.findAll().collectList();
    }
}
