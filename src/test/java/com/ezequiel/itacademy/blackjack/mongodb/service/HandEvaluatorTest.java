package com.ezequiel.itacademy.blackjack.mongodb.service;

import com.ezequiel.itacademy.blackjack.mongodb.entity.Card;
import com.ezequiel.itacademy.blackjack.mongodb.enums.Rank;
import com.ezequiel.itacademy.blackjack.mongodb.enums.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class HandEvaluatorTest {

    private HandEvaluator handEvaluator;

    @BeforeEach
    void setUp() {
        handEvaluator = new HandEvaluator();
    }

    @Test
    void testCalculateHandValue_NoAces() {
        List<Card> hand = List.of(new Card(Rank.TEN, Suit.HEARTS),
                new Card(Rank.SEVEN, Suit.SPADES),
                new Card(Rank.TWO, Suit.CLUBS));

        int score = handEvaluator.calculateHandValue(hand);
        assertEquals(19, score);
    }

    @Test
    void testCalculateHandValue_OneAce_AsEleven() {
        List<Card> hand = List.of(new Card(Rank.ACE, Suit.DIAMONDS),
                new Card(Rank.NINE, Suit.CLUBS));

        int score = handEvaluator.calculateHandValue(hand);
        assertEquals(20, score);
    }

    @Test
    void testCalculateHandValue_AcesConvertedToOne() {
        List<Card> hand = List.of(new Card(Rank.ACE, Suit.HEARTS),
                new Card(Rank.TEN, Suit.SPADES),
                new Card(Rank.FIVE, Suit.CLUBS));

        int score = handEvaluator.calculateHandValue(hand);
        assertEquals(16, score);
    }
}
