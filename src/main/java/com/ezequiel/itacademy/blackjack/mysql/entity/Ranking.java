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
@Table("rankings")  // Cambiado de "Ranking" a "rankings"
public class Ranking {

    @Id
    private Long id;

    @Column("player_id")  // Correcto, coincide con la columna en la base de datos
    private Long playerId;

    private Integer points;
}