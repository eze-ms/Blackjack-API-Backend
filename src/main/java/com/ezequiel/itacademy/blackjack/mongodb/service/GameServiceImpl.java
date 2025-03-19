package com.ezequiel.itacademy.blackjack.mongodb.service;

import com.ezequiel.itacademy.blackjack.exception.GameNotFoundExcepction;
import com.ezequiel.itacademy.blackjack.mongodb.entity.Card;
import com.ezequiel.itacademy.blackjack.mongodb.entity.Game;
import com.ezequiel.itacademy.blackjack.mongodb.enums.GameStatus;
import com.ezequiel.itacademy.blackjack.mongodb.enums.MoveType;
import com.ezequiel.itacademy.blackjack.mongodb.repository.GameRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;

@Service
public class GameServiceImpl implements GameService {

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

        System.out.println("Guardando partida: " + game);
        return gameRepository.save(game)
                .doOnSuccess(savedGame -> System.out.println("Partida guardada con ID: " + savedGame.getId()));
    }

    @Override
    public Mono<Game> findGameById(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFoundExcepction("La partida con id " + gameId + " no fue encontrada."))); // Lanzamos GameNotFoundExcepction
    }

    @Override
    public Mono<Game> addMove(String gameId, MoveType moveType) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFoundExcepction("La partida con id " + gameId + " no fue encontrada."))) // Lanzamos GameNotFoundExcepction
                .flatMap(game -> {
                    // Verificar si la partida está en curso
                    if (game.getStatus() != GameStatus.IN_PROGRESS) {
                        return Mono.error(new RuntimeException("No se puede agregar movimientos, la partida ha finalizado."));
                    }

                    // Verificar si la jugada es DOUBLE
                    if (moveType == MoveType.DOUBLE) {
                        // El jugador debe tener exactamente dos cartas
                        if (game.getPlayerCards().size() != 2) {
                            return Mono.error(new RuntimeException("Para hacer un DOUBLE, el jugador debe tener exactamente dos cartas."));
                        }

                        // Repartir una carta adicional al jugador
                        Card newCard = deckService.dealCard(); // Obtener carta del mazo
                        game.getPlayerCards().add(newCard);
                        System.out.println("🃏 Carta agregada al jugador: " + newCard);
                    }

                    game.getMoves().add(moveType); // Registrar el movimiento
                    return gameRepository.save(game);
                });
    }

    @Override
    public Mono<Void> deleteGame(String gameId) {
        return gameRepository.findById(gameId)
                .switchIfEmpty(Mono.error(new GameNotFoundExcepction("La partida con id " + gameId + " no existe.")))
                .flatMap(gameRepository::delete); // Si la partida existe, la elimina
    }
}
