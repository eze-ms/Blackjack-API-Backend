package com.ezequiel.itacademy.blackjack.mongodb.service;

import com.ezequiel.itacademy.blackjack.exception.GameNotFoundExcepction;
import com.ezequiel.itacademy.blackjack.mongodb.entity.Card;
import com.ezequiel.itacademy.blackjack.mongodb.entity.Game;
import com.ezequiel.itacademy.blackjack.mongodb.enums.MoveType;
import com.ezequiel.itacademy.blackjack.mongodb.enums.Rank;
import com.ezequiel.itacademy.blackjack.mongodb.enums.Suit;
import com.ezequiel.itacademy.blackjack.mongodb.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private DeckService deckService;

    @InjectMocks
    private GameServiceImpl gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Simulamos que deckService.dealCards(2) devuelve dos cartas de prueba
        when(deckService.dealCards(2)).thenReturn(List.of(new Card(Rank.ACE, Suit.SPADES), new Card(Rank.KING, Suit.HEARTS)));
    }

    @Test
    void createGame() {
        String playerId = "123";
        Game game = new Game();
        game.setPlayerId(playerId);
        game.setId("mockedGameId");

        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        StepVerifier.create(gameService.createGame(playerId))
                .expectNextMatches(savedGame -> savedGame.getPlayerId().equals(playerId) && savedGame.getId().equals("mockedGameId"))
                .verifyComplete();

        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void findGameById() {
        String existingGameId = "mockedGameId";
        String nonExistentGameId = "nonExistentId";

        Game game = new Game();
        game.setId(existingGameId);

        when(gameRepository.findById(existingGameId)).thenReturn(Mono.just(game));
        when(gameRepository.findById(nonExistentGameId)).thenReturn(Mono.empty());

        // Caso: Partida encontrada
        StepVerifier.create(gameService.findGameById(existingGameId))
                .expectNext(game)
                .verifyComplete();

        // Caso: Partida no encontrada
        StepVerifier.create(gameService.findGameById(nonExistentGameId))
                .expectErrorMatches(throwable -> throwable instanceof GameNotFoundExcepction &&
                        throwable.getMessage().equals("La partida con id " + nonExistentGameId + " no fue encontrada."))
                .verify();

        verify(gameRepository, times(2)).findById(anyString()); // Se llama dos veces con distintos IDs
    }

    @Test
    void addMove() {
        String gameId = "mockedGameId";
        Game game = new Game();
        game.setId(gameId);

        when(gameRepository.findById(gameId)).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        StepVerifier.create(gameService.addMove(gameId, MoveType.HIT))
                .expectNext(game) // Solo verificamos que devuelve un Game
                .verifyComplete();

        verify(gameRepository, times(1)).findById(gameId);
        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void deleteGame() {
        String gameId = "mockedGameId";
        Game game = new Game();
        game.setId(gameId);

        when(gameRepository.findById(gameId)).thenReturn(Mono.just(game));
        when(gameRepository.delete(game)).thenReturn(Mono.empty());

        StepVerifier.create(gameService.deleteGame(gameId))
                .verifyComplete(); // Verifica que se complete sin errores

        verify(gameRepository, times(1)).findById(gameId);
        verify(gameRepository, times(1)).delete(game);
    }
}
