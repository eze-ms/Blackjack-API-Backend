package com.ezequiel.itacademy.blackjack.mongodb.entity;

import com.ezequiel.itacademy.blackjack.mongodb.enums.Rank;
import com.ezequiel.itacademy.blackjack.mongodb.enums.Suit;

public record Card(Rank rank, Suit suit) {
}
