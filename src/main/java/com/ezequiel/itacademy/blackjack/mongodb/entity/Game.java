package com.ezequiel.itacademy.blackjack.mongodb.entity;

import com.ezequiel.itacademy.blackjack.mongodb.enums.GameStatus;
import com.ezequiel.itacademy.blackjack.mongodb.enums.MoveType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "games")
public class Game {

    @Id
    private String id;

    @Schema(description = "ID del jugador", required = true, example = "\"1234\"")
    private String playerId;

    @CreatedDate
    private Instant createdAt;

    private List<Card> playerCards = new ArrayList<>();
    private List<Card> dealerCards = new ArrayList<>();
    private GameStatus status = GameStatus.IN_PROGRESS;

    private List<MoveType> moves = new ArrayList<>();
}