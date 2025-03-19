package com.ezequiel.itacademy.blackjack.mongodb.service;

import com.ezequiel.itacademy.blackjack.mongodb.entity.Card;
import com.ezequiel.itacademy.blackjack.mongodb.enums.Rank;
import com.ezequiel.itacademy.blackjack.mongodb.enums.Suit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DeckService {

    private List<Card> deck;

    public DeckService() {
        createDeck();
    }

    // Crea el mazo
    public void createDeck() {
        deck = Stream.of(Rank.values())
                .flatMap(rank -> Stream.of(Suit.values())
                        .map(suit -> new Card(rank, suit)))
                .collect(Collectors.toList());
        shuffleDeck();
    }

    // Baraja el mazo
    public void shuffleDeck() {
        Collections.shuffle(deck);
    }

    // Reparte una carta
    public Card dealCard() {
        if (deck.isEmpty()) {
            createDeck();  // Si el mazo está vacío, lo recreamos
        }
        return deck.remove(deck.size() - 1);
    }

    // Reparte un número específico de cartas
    public List<Card> dealCards(int num) {
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            cards.add(dealCard());
        }
        return cards;
    }
}

