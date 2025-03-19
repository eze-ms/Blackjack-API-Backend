package com.ezequiel.itacademy.blackjack.mysql.service;

import com.ezequiel.itacademy.blackjack.exception.PlayerNotFoundException;
import com.ezequiel.itacademy.blackjack.mysql.entity.Player;
import com.ezequiel.itacademy.blackjack.mysql.repository.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PlayerServiceImpl implements PlayerService {

    private static final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);
    private final PlayerRepository playerRepository;

    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public Mono<Player> updatePlayerName(Long playerId, String newName) {
        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.error(new PlayerNotFoundException("El jugador con id " + playerId + " no fue encontrado.")))
                .flatMap(player -> {
                    player.setName(newName);
                    return playerRepository.save(player)
                            .doOnSuccess(unused ->
                                    logger.info("El nombre del jugador con ID: {} ha sido actualizado con éxito", playerId));
                });
    }
}
