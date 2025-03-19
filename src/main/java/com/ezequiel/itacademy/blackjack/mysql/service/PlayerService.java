package com.ezequiel.itacademy.blackjack.mysql.service;

import com.ezequiel.itacademy.blackjack.mysql.entity.Player;
import reactor.core.publisher.Mono;

public interface PlayerService {
    Mono<Player> updatePlayerName(Long playerId, String newName);
}
