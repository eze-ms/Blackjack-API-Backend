package com.ezequiel.itacademy.blackjack.mysql.repository;

import com.ezequiel.itacademy.blackjack.mysql.entity.Player;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PlayerRepository extends ReactiveCrudRepository<Player,Long> {
}