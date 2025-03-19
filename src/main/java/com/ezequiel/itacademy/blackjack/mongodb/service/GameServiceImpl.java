package com.ezequiel.itacademy.blackjack.mongodb.service;

import com.ezequiel.itacademy.blackjack.exception.GameNotFoundExcepction;
import com.ezequiel.itacademy.blackjack.mongodb.entity.Card;
import com.ezequiel.itacademy.blackjack.mongodb.entity.Game;
import com.ezequiel.itacademy.blackjack.mongodb.enums.GameStatus;
import com.ezequiel.itacademy.blackjack.mongodb.enums.MoveType;
import com.ezequiel.itacademy.blackjack.mongodb.repository.GameRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;

@Service
public class GameServiceImpl implements GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
    private final GameRepository gameRepository;
    private final DeckService deckService;

    public GameServiceImpl(GameRepository gameRepository, DeckService deckService) {
        this.gameRepository = gameRepository;
        this.deckService = deckService;
    }

    @Override
    public Mono<Game> createGame(String playerId) {
        Game game = new Game();
        game.setPlayerId(playerId);
        game.setPlayerCards(deckService.dealCards(2));
        game.setDealerCards(deckService.dealCards(2));
        game.setMoves(new ArrayList<>());
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setCreatedAt(Instant.now());

        logger.info("Guardando partida: {}", game);
        return gameRepository.save(game)
                .doOnSuccess(savedGame -> logger.info("Partida guardada con ID: {}", savedGame.getId()));
    }

    @Override
    public Mono<Game> findGameById(String gameId) {
        logger.info("Buscando partida con ID: {}", gameId);
        return gameRepository.findById(gameId)
                .doOnSuccess(game -> {
                    if (game != null) {
                        logger.info("Partida encontrada: {}", game);
                    }
                })
                .switchIfEmpty(Mono.error(new GameNotFoundExcepction("La partida con id " + gameId + " no fue encontrada.")));
    }

    @Override
    public Mono<Game> addMove(String gameId, MoveType moveType) {
        logger.info("Agregando movimiento {} a la partida con ID: {}", moveType, gameId);
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFoundExcepction("La partida con id " + gameId + " no fue encontrada.")))
                .flatMap(game -> {
                    if (game.getStatus() != GameStatus.IN_PROGRESS) {
                        logger.warn("Intento de movimiento en partida finalizada: {}", gameId);
                        return Mono.error(new RuntimeException("No se puede agregar movimientos, la partida ha finalizado."));
                    }

                    if (moveType == MoveType.DOUBLE) {
                        if (game.getPlayerCards().size() != 2) {
                            logger.warn("Intento de DOUBLE con cartas insuficientes en la partida: {}", gameId);
                            return Mono.error(new RuntimeException("Para hacer un DOUBLE, el jugador debe tener exactamente dos cartas."));
                        }

                        Card newCard = deckService.dealCard();
                        game.getPlayerCards().add(newCard);
                        logger.info("Carta agregada al jugador en partida {}: {}", gameId, newCard);
                    }

                    game.getMoves().add(moveType);
                    return gameRepository.save(game)
                            .doOnSuccess(updatedGame -> logger.info("Movimiento {} agregado con éxito en partida {}", moveType, gameId));
                });
    }

    @Override
    public Mono<Void> deleteGame(String gameId) {
        logger.info("Eliminando partida con ID: {}", gameId);
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFoundExcepction("La partida con id " + gameId + " no existe.")))
                .flatMap(game -> gameRepository.delete(game)
                        .doOnSuccess(unused -> logger.info("Partida con ID: {} eliminada con éxito", gameId)));
    }
}
