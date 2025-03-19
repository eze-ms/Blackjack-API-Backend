package com.ezequiel.itacademy.blackjack.mysql.handlers;

import com.ezequiel.itacademy.blackjack.exception.PlayerNotFoundException;
import com.ezequiel.itacademy.blackjack.mysql.entity.Player;
import com.ezequiel.itacademy.blackjack.mysql.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

import static org.mockito.Mockito.*;

class PlayerHandlerTest {

    @Mock
    private PlayerService playerService;

    @InjectMocks
    private PlayerHandler playerHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updatePlayerName() {
        // Arrange
        Long existingPlayerId = 1L;
        Long nonExistentPlayerId = 99L;
        String newName = "Ezequiel";

        Player player = new Player();
        player.setId(existingPlayerId);
        player.setName(newName);

        when(playerService.updatePlayerName(existingPlayerId, newName)).thenReturn(Mono.just(player));
        when(playerService.updatePlayerName(nonExistentPlayerId, newName))
                .thenReturn(Mono.error(new PlayerNotFoundException("El jugador con id " + nonExistentPlayerId + " no fue encontrado.")));

        ServerRequest requestFound = mock(ServerRequest.class);
        when(requestFound.pathVariable("playerId")).thenReturn(existingPlayerId.toString());
        when(requestFound.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("name", newName)));

        ServerRequest requestNotFound = mock(ServerRequest.class);
        when(requestNotFound.pathVariable("playerId")).thenReturn(nonExistentPlayerId.toString());
        when(requestNotFound.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("name", newName)));

        // Act & Assert (Caso 1: Actualización exitosa)
        StepVerifier.create(playerHandler.updatePlayerName(requestFound))
                .expectNextMatches(response -> response.statusCode().value() == 200)
                .verifyComplete();

        // Act & Assert (Caso 2: Jugador no encontrado)
        StepVerifier.create(playerHandler.updatePlayerName(requestNotFound))
                .expectNextMatches(response -> response.statusCode().value() == 404)
                .verifyComplete();

        verify(playerService, times(2)).updatePlayerName(anyLong(), anyString());
    }
}
