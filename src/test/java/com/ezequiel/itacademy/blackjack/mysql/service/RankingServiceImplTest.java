package com.ezequiel.itacademy.blackjack.mysql.service;

import com.ezequiel.itacademy.blackjack.mysql.entity.Ranking;
import com.ezequiel.itacademy.blackjack.mysql.repository.RankingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class RankingServiceImplTest {

    @Mock
    private RankingRepository rankingRepository;

    @InjectMocks
    private RankingServiceImpl rankingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPlayerRanking() {
        Ranking ranking = new Ranking(1L, 1L, 10);

        when(rankingRepository.findByPlayerId(1L)).thenReturn(Mono.just(ranking));

        StepVerifier.create(rankingService.getPlayerRanking(1L))
                .expectNextMatches(r -> r.getPlayerId().equals(1L) && r.getPoints() == 10)
                .verifyComplete();

        verify(rankingRepository, times(1)).findByPlayerId(1L);
    }

    @Test
    void getAllRankings() {
        Ranking ranking1 = new Ranking(1L, 1L, 10);
        Ranking ranking2 = new Ranking(2L, 2L, 20);

        when(rankingRepository.findAll()).thenReturn(Flux.just(ranking1, ranking2));

        StepVerifier.create(rankingService.getAllRankings())
                .expectNext(ranking1)
                .expectNext(ranking2)
                .verifyComplete();

        verify(rankingRepository, times(1)).findAll();
    }

    @Test
    void updateRanking() {
        Ranking existingRanking = new Ranking(1L, 1L, 10);
        Ranking updatedRanking = new Ranking(1L, 1L, 15);

        when(rankingRepository.findByPlayerId(1L)).thenReturn(Mono.just(existingRanking));
        when(rankingRepository.save(any(Ranking.class))).thenReturn(Mono.just(updatedRanking));

        StepVerifier.create(rankingService.updateRanking(1L, 5))
                .expectNextMatches(r -> r.getPoints() == 15)
                .verifyComplete();

        verify(rankingRepository, times(1)).findByPlayerId(1L);
        verify(rankingRepository, times(1)).save(any(Ranking.class));
    }
}
