package com.ezequiel.itacademy.blackjack.mongodb.service;

import com.ezequiel.itacademy.blackjack.mongodb.entity.Game;
import com.ezequiel.itacademy.blackjack.mongodb.enums.MoveType;
import reactor.core.publisher.Mono;

public interface GameService {
    Mono<Game> createGame(String playerId);
    Mono<Game> findGameById(String gameId);
    Mono<Game> addMove(String gameId, MoveType moveType);
    Mono<Void> deleteGame(String gameId);
}
