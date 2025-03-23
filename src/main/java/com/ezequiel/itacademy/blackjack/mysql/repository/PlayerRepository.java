package com.ezequiel.itacademy.blackjack.mysql.repository;

import com.ezequiel.itacademy.blackjack.mysql.entity.Player;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface PlayerRepository extends R2dbcRepository<Player,Long> {
}