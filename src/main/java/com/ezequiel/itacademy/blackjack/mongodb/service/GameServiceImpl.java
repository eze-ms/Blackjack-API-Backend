package com.ezequiel.itacademy.blackjack.mongodb.service;

import com.ezequiel.itacademy.blackjack.exception.GameNotFoundExcepction;
import com.ezequiel.itacademy.blackjack.exception.PlayerNotFoundException;
import com.ezequiel.itacademy.blackjack.mongodb.entity.Card;
import com.ezequiel.itacademy.blackjack.mongodb.entity.Game;
import com.ezequiel.itacademy.blackjack.mongodb.enums.GameStatus;
import com.ezequiel.itacademy.blackjack.mongodb.enums.MoveType;
import com.ezequiel.itacademy.blackjack.mongodb.repository.GameRepository;
import com.ezequiel.itacademy.blackjack.mysql.repository.PlayerRepository;
import com.ezequiel.itacademy.blackjack.mysql.service.RankingUpdateService;
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
    private final PlayerRepository playerRepository;
    private final DeckService deckService;
    private final HandEvaluator handEvaluator;
    private final RankingUpdateService rankingUpdateService;


    public GameServiceImpl(GameRepository gameRepository, PlayerRepository playerRepository, DeckService deckService,
                           HandEvaluator handEvaluator, RankingUpdateService rankingUpdateService) {

        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.deckService = deckService;
        this.handEvaluator = handEvaluator;
        this.rankingUpdateService = rankingUpdateService;
    }


    @Override
    public Mono<Game> createGame(String playerId) {
        return playerRepository.findById(Long.parseLong(playerId))  // Verifica si el jugador existe
                .flatMap(existingPlayer -> {  // Si el jugador existe, crear la partida
                    Game game = new Game();
                    game.setPlayerId(playerId);  // Referencia al jugador con playerId
                    game.setPlayerCards(deckService.dealCards(2));
                    game.setDealerCards(deckService.dealCards(2));
                    game.setMoves(new ArrayList<>());
                    game.setStatus(GameStatus.IN_PROGRESS);
                    game.setCreatedAt(Instant.now());

                    int playerScore = handEvaluator.calculateHandValue(game.getPlayerCards());
                    int dealerScore = handEvaluator.calculateHandValue(game.getDealerCards());

                    if (playerScore == 21 && dealerScore == 21) {
                        game.setStatus(GameStatus.DRAW);
                    } else if (playerScore == 21) {
                        game.setStatus(GameStatus.PLAYER_WINS);
                    } else if (dealerScore == 21) {
                        game.setStatus(GameStatus.DEALER_WINS);
                    }

                    logger.info("Guardando partida: {}", game);
                    return gameRepository.save(game)  // Guarda la partida
                            .doOnSuccess(savedGame -> logger.info("Partida guardada con ID: {}", savedGame.getId()));
                })
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("Jugador no encontrado"))); // Si el jugador no existe, lanza un error
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
                        return Mono.error(new RuntimeException("No se puede jugar en una partida finalizada."));
                    }

                    if (game.getPlayerId() == null) {
                        return Mono.error(new IllegalStateException("El jugador no puede ser nulo"));
                    }

                    if (game.getMoves() == null) {
                        game.setMoves(new ArrayList<>());
                    }

                    if (moveType == MoveType.HIT) {
                        Card newCard = deckService.dealCard();
                        game.getPlayerCards().add(newCard);
                        logger.info("Carta agregada al jugador: {}", newCard);

                        int playerScore = handEvaluator.calculateHandValue(game.getPlayerCards());
                        if (playerScore > 21) {
                            game.setStatus(GameStatus.DEALER_WINS);
                        }
                    }

                    if (moveType == MoveType.STAND) {
                        int dealerScore = handEvaluator.calculateHandValue(game.getDealerCards());

                        while (dealerScore < 17) {
                            Card newCard = deckService.dealCard();
                            game.getDealerCards().add(newCard);
                            dealerScore = handEvaluator.calculateHandValue(game.getDealerCards());
                        }

                        logger.info("Dealer ha jugado, cartas finales: {} con puntaje: {}", game.getDealerCards(), dealerScore);

                        int playerScore = handEvaluator.calculateHandValue(game.getPlayerCards());

                        if (dealerScore > 21) {
                            game.setStatus(GameStatus.PLAYER_WINS);
                        } else if (playerScore > 21) {
                            game.setStatus(GameStatus.DEALER_WINS);
                        } else if (playerScore > dealerScore) {
                            game.setStatus(GameStatus.PLAYER_WINS);
                        } else if (dealerScore > playerScore) {
                            game.setStatus(GameStatus.DEALER_WINS);
                        } else {
                            game.setStatus(GameStatus.DRAW);
                        }
                    }

                    game.getMoves().add(moveType);

                    Long playerId = Long.parseLong(game.getPlayerId());

                    return rankingUpdateService.updatePlayerRanking(playerId, game.getStatus().name())
                            .onErrorResume(e -> {
                                logger.error("Error al actualizar el ranking", e);
                                return Mono.empty(); // No cortar el flujo si ranking falla
                            })
                            .then(Mono.defer(() -> gameRepository.save(game)))
                            .doOnSuccess(updatedGame -> logger.info("Movimiento {} agregado en partida {}", moveType, gameId));

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
