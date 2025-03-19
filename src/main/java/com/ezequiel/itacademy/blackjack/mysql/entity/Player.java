package com.ezequiel.itacademy.blackjack.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("players")
public class Player {

    @Id
    private Long id;

    @Column("name")
    @NotBlank(message = "Please add the player name")
    private String name;
}
