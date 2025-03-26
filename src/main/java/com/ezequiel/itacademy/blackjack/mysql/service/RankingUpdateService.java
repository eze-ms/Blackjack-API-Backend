package com.ezequiel.itacademy.blackjack.mysql.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RankingUpdateService {

    private final RankingService rankingService;

    public RankingUpdateService(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    public Mono<Boolean> updatePlayerRanking(Long playerId, String gameStatus) {
        int points = switch (gameStatus) {
            case "PLAYER_WINS" -> 10;
            case "DRAW" -> 5;
            default -> 0;
        };

        System.out.println("🏁 Estado del juego: " + gameStatus + ", puntos: " + points + ", playerId: " + playerId);

        if (points == 0) {
            System.out.println("⚠️ No se actualiza el ranking porque los puntos son 0");
            return Mono.just(false);
        }

        return rankingService.updateRanking(playerId, points)
                .doOnSuccess(r -> System.out.println("✅ Ranking actualizado: " + r))
                .doOnError(e -> System.err.println("❌ Error actualizando ranking: " + e.getMessage()))
                .thenReturn(true);
    }

}
