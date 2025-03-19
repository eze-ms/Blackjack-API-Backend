package com.ezequiel.itacademy.blackjack.mongodb.repository;

import com.ezequiel.itacademy.blackjack.mongodb.entity.Game;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface GameRepository extends ReactiveMongoRepository<Game, String> {
}