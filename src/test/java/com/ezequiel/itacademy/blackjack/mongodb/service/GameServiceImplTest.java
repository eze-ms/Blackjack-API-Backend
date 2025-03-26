package com.ezequiel.itacademy.blackjack.mongodb.service;

import com.ezequiel.itacademy.blackjack.mongodb.entity.Card;
import com.ezequiel.itacademy.blackjack.mongodb.entity.Game;
import com.ezequiel.itacademy.blackjack.mongodb.enums.GameStatus;
import com.ezequiel.itacademy.blackjack.mongodb.enums.MoveType;
import com.ezequiel.itacademy.blackjack.mongodb.enums.Rank;
import com.ezequiel.itacademy.blackjack.mongodb.enums.Suit;
import com.ezequiel.itacademy.blackjack.mongodb.repository.GameRepository;
import com.ezequiel.itacademy.blackjack.mysql.service.RankingUpdateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

class GameServiceImplTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private DeckService deckService;

    @Mock
    private HandEvaluator handEvaluator;

    @Mock
    private RankingUpdateService rankingUpdateService;

    @InjectMocks
    private GameServiceImpl gameService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGame_BlackjackNatural_PlayerWins() {
        String playerId = "123";
        Game game = new Game();
        game.setPlayerId(playerId);
        game.setId("mockedGameId");
        game.setPlayerCards(List.of(new Card(Rank.ACE, Suit.SPADES), new Card(Rank.KING, Suit.HEARTS)));
        game.setDealerCards(List.of(new Card(Rank.FIVE, Suit.DIAMONDS), new Card(Rank.NINE, Suit.CLUBS)));
        game.setStatus(GameStatus.PLAYER_WINS);
        game.setMoves(new ArrayList<>());

        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));
        when(handEvaluator.calculateHandValue(game.getPlayerCards())).thenReturn(21);
        when(handEvaluator.calculateHandValue(game.getDealerCards())).thenReturn(14);

        StepVerifier.create(gameService.createGame(playerId))
                .expectNextMatches(savedGame -> savedGame.getStatus() == GameStatus.PLAYER_WINS)
                .verifyComplete();
    }

    @Test
    void addMove_Hit_PlayerBusts() {
        String gameId = "mockedGameId";
        Game game = new Game();
        game.setId(gameId);
        game.setPlayerId("123");
        game.setPlayerCards(new ArrayList<>(List.of(new Card(Rank.TEN, Suit.SPADES), new Card(Rank.SEVEN, Suit.HEARTS))));
        game.setMoves(new ArrayList<>());
        game.setStatus(GameStatus.IN_PROGRESS);

        when(gameRepository.findById(gameId)).thenReturn(Mono.just(game));
        when(deckService.dealCard()).thenReturn(new Card(Rank.SIX, Suit.CLUBS));
        when(handEvaluator.calculateHandValue(any())).thenReturn(23); // Supera 21
        when(rankingUpdateService.updatePlayerRanking(123L, "DEALER_WINS")).thenReturn(Mono.just(true));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        StepVerifier.create(gameService.addMove(gameId, MoveType.HIT))
                .expectNextMatches(updatedGame -> updatedGame.getStatus() == GameStatus.DEALER_WINS)
                .verifyComplete();
    }

    @Test
    void addMove_Stand_DealerPlaysAndWins() {
        String gameId = "mockedGameId";
        Game game = new Game();
        game.setId(gameId);
        game.setPlayerId("123");
        game.setPlayerCards(new ArrayList<>(List.of(new Card(Rank.TEN, Suit.SPADES), new Card(Rank.NINE, Suit.HEARTS))));
        game.setDealerCards(new ArrayList<>(List.of(new Card(Rank.SIX, Suit.DIAMONDS), new Card(Rank.FIVE, Suit.CLUBS))));
        game.setMoves(new ArrayList<>());
        game.setStatus(GameStatus.IN_PROGRESS);

        when(gameRepository.findById(gameId)).thenReturn(Mono.just(game));
        when(handEvaluator.calculateHandValue(game.getPlayerCards())).thenReturn(19);
        when(handEvaluator.calculateHandValue(game.getDealerCards())).thenReturn(11);
        when(deckService.dealCard()).thenReturn(new Card(Rank.TEN, Suit.SPADES));
        when(handEvaluator.calculateHandValue(List.of(
                new Card(Rank.SIX, Suit.DIAMONDS),
                new Card(Rank.FIVE, Suit.CLUBS),
                new Card(Rank.TEN, Suit.SPADES)
        ))).thenReturn(21);
        when(rankingUpdateService.updatePlayerRanking(123L, "DEALER_WINS")).thenReturn(Mono.just(true));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        StepVerifier.create(gameService.addMove(gameId, MoveType.STAND))
                .expectNextMatches(updatedGame -> updatedGame.getStatus() == GameStatus.DEALER_WINS)
                .verifyComplete();
    }


    @Test
    void addMove_ShouldThrowException_WhenPlayerIdIsNull() {
        // 📌 Simulación: Se encuentra la partida pero sin playerId
        Game game = new Game();
        game.setId("game123");
        game.setPlayerId(null);
        game.setStatus(GameStatus.IN_PROGRESS);

        when(gameRepository.findById("game123")).thenReturn(Mono.just(game));

        // 📌 Ejecutar y verificar que lanza IllegalStateException
        StepVerifier.create(gameService.addMove("game123", MoveType.HIT))
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException &&
                        throwable.getMessage().equals("El jugador no puede ser nulo"))
                .verify();

        // 📌 Validar que se llamó al repositorio
        verify(gameRepository, times(1)).findById("game123");
    }

    @Test
    void addMove_ShouldUpdateRankingBeforeSavingGame() {
        // Simulación de partida en progreso con playerId válido
        Game game = new Game();
        game.setId("game123");
        game.setPlayerId("123");
        game.setStatus(GameStatus.IN_PROGRESS);

        when(gameRepository.findById("game123")).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));
        when(rankingUpdateService.updatePlayerRanking(123L, "IN_PROGRESS")).thenReturn(Mono.empty());

        StepVerifier.create(gameService.addMove("game123", MoveType.HIT))
                .expectNext(game)
                .verifyComplete();

        verify(gameRepository, times(1)).findById("game123");
        verify(rankingUpdateService, times(1)).updatePlayerRanking(123L, "IN_PROGRESS");
        verify(gameRepository, times(1)).save(any(Game.class));
    }

}