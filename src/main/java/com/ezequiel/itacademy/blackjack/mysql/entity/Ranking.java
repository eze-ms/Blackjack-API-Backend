package com.ezequiel.itacademy.blackjack.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("ranking")
public class Ranking {

    @Id
    private Long id;

    @Column("player_id")
    private Long playerId;

    private Integer points;
}