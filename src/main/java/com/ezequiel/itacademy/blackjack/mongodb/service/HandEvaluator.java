package com.ezequiel.itacademy.blackjack.mongodb.service;

import com.ezequiel.itacademy.blackjack.mongodb.entity.Card;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class HandEvaluator {

    public int calculateHandValue(List<Card> hand) {
        int total = 0;
        int aceCount = 0;

        for (Card card : hand) {
            switch (card.rank()) {
                case TWO -> total += 2;
                case THREE -> total += 3;
                case FOUR -> total += 4;
                case FIVE -> total += 5;
                case SIX -> total += 6;
                case SEVEN -> total += 7;
                case EIGHT -> total += 8;
                case NINE -> total += 9;
                case TEN, JACK, QUEEN, KING -> total += 10;
                case ACE -> {
                    total += 11;
                    aceCount++;
                }
            }
        }

        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }

        return total;
    }
}
