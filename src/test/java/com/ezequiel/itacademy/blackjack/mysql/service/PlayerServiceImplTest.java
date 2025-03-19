package com.ezequiel.itacademy.blackjack.mysql.service;

import com.ezequiel.itacademy.blackjack.exception.PlayerNotFoundException;
import com.ezequiel.itacademy.blackjack.mysql.entity.Player;
import com.ezequiel.itacademy.blackjack.mysql.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class PlayerServiceImplTest {

    @Mock
    private PlayerRepository playerRepository;

    @InjectMocks
    private PlayerServiceImpl playerService;

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
        player.setName("OldName");

        when(playerRepository.findById(existingPlayerId)).thenReturn(Mono.just(player));
        when(playerRepository.findById(nonExistentPlayerId)).thenReturn(Mono.empty());
        when(playerRepository.save(any(Player.class))).thenReturn(Mono.just(player));

        // Act & Assert (Caso 1: Actualización exitosa)
        StepVerifier.create(playerService.updatePlayerName(existingPlayerId, newName))
                .expectNextMatches(updatedPlayer -> updatedPlayer.getName().equals(newName))
                .verifyComplete();

        // Act & Assert (Caso 2: Jugador no encontrado)
        StepVerifier.create(playerService.updatePlayerName(nonExistentPlayerId, newName))
                .expectErrorMatches(throwable -> throwable instanceof PlayerNotFoundException &&
                        throwable.getMessage().equals("El jugador con id " + nonExistentPlayerId + " no fue encontrado."))
                .verify();

        verify(playerRepository, times(2)).findById(anyLong()); // Se llama dos veces, una por cada ID
        verify(playerRepository, times(1)).save(any(Player.class)); // Solo se llama en el caso exitoso
    }

}
