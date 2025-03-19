package com.ezequiel.itacademy.blackjack.mongodb.handlers;

import com.ezequiel.itacademy.blackjack.mongodb.entity.Game;
import com.ezequiel.itacademy.blackjack.mongodb.enums.MoveType;
import com.ezequiel.itacademy.blackjack.mongodb.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.springframework.web.reactive.function.server.ServerRequest;

import java.util.Map;

import static org.mockito.Mockito.*;

class GameHandlerTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameHandler gameHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addGame() {
        String playerId = "123";
        Game game = new Game();
        game.setId("mockedGameId");
        game.setPlayerId(playerId);

        when(gameService.createGame(playerId)).thenReturn(Mono.just(game));

        ServerRequest request = mock(ServerRequest.class);
        Map<String, String> requestBody = Map.of("playerId", playerId);
        when(request.bodyToMono(Map.class)).thenReturn(Mono.just(requestBody));

        // Act & Assert
        StepVerifier.create(gameHandler.addGame(request))
                .expectNextMatches(response -> response.statusCode().value() == 201)
                .verifyComplete();

        verify(gameService, times(1)).createGame(playerId);
    }

    @Test
    void getGameById() {
        // Arrange
        String existingGameId = "mockedGameId";
        String nonExistentGameId = "nonExistentId";

        Game game = new Game();
        game.setId(existingGameId);

        when(gameService.findGameById(existingGameId)).thenReturn(Mono.just(game));
        when(gameService.findGameById(nonExistentGameId)).thenReturn(Mono.empty());

        ServerRequest requestFound = mock(ServerRequest.class);
        when(requestFound.pathVariable("id")).thenReturn(existingGameId);

        ServerRequest requestNotFound = mock(ServerRequest.class);
        when(requestNotFound.pathVariable("id")).thenReturn(nonExistentGameId);

        // Act & Assert (Caso 1: Partida encontrada)
        StepVerifier.create(gameHandler.getGameById(requestFound))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        // Act & Assert (Caso 2: Partida no encontrada)
        StepVerifier.create(gameHandler.getGameById(requestNotFound))
                .expectNextMatches(response -> response.statusCode().value() == 404)
                .verifyComplete();

        verify(gameService, times(2)).findGameById(anyString());
    }

    @Test
    void playMove() {
        // Arrange
        String existingGameId = "mockedGameId";
        String nonExistentGameId = "nonExistentId";
        MoveType moveType = MoveType.HIT;

        Game game = new Game();
        game.setId(existingGameId);

        when(gameService.addMove(existingGameId, moveType)).thenReturn(Mono.just(game));
        when(gameService.addMove(nonExistentGameId, moveType)).thenReturn(Mono.empty());

        ServerRequest requestFound = mock(ServerRequest.class);
        when(requestFound.pathVariable("id")).thenReturn(existingGameId);
        when(requestFound.bodyToMono(String.class)).thenReturn(Mono.just(moveType.name()));

        ServerRequest requestNotFound = mock(ServerRequest.class);
        when(requestNotFound.pathVariable("id")).thenReturn(nonExistentGameId);
        when(requestNotFound.bodyToMono(String.class)).thenReturn(Mono.just(moveType.name()));

        // Act & Assert (Caso 1: Movimiento exitoso)
        StepVerifier.create(gameHandler.playMove(requestFound))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        // Act & Assert (Caso 2: Partida no encontrada)
        StepVerifier.create(gameHandler.playMove(requestNotFound))
                .expectNextMatches(response -> response.statusCode().value() == 404)
                .verifyComplete();

        verify(gameService, times(2)).addMove(anyString(), any(MoveType.class));
    }

    @Test
    void removeGame() {
        // Arrange
        String existingGameId = "mockedGameId";
        String nonExistentGameId = "nonExistentId";

        Game game = new Game();
        game.setId(existingGameId);

        when(gameService.findGameById(existingGameId)).thenReturn(Mono.just(game));
        when(gameService.findGameById(nonExistentGameId)).thenReturn(Mono.empty());
        when(gameService.deleteGame(existingGameId)).thenReturn(Mono.empty());

        ServerRequest requestFound = mock(ServerRequest.class);
        when(requestFound.pathVariable("id")).thenReturn(existingGameId);

        ServerRequest requestNotFound = mock(ServerRequest.class);
        when(requestNotFound.pathVariable("id")).thenReturn(nonExistentGameId);

        // Act & Assert (Caso 1: Eliminación exitosa)
        StepVerifier.create(gameHandler.removeGame(requestFound))
                .expectNextMatches(response -> response.statusCode().value() == 204)
                .verifyComplete();

        // Act & Assert (Caso 2: Partida no encontrada)
        StepVerifier.create(gameHandler.removeGame(requestNotFound))
                .expectNextMatches(response -> response.statusCode().value() == 404)
                .verifyComplete();

        verify(gameService, times(2)).findGameById(anyString());
        verify(gameService, times(1)).deleteGame(existingGameId);
    }
}
