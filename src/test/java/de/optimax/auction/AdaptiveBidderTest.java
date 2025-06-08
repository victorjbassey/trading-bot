package de.optimax.auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** Tests for {@link AdaptiveBidder}. */
class AdaptiveBidderTest {
  @ParameterizedTest
  @CsvSource({
    "10, -100", // Negative cash
    "0, 50", // Zero quantity
    "13, 100", // Odd quantity
    "-20, 40" // Negative quantity
  })
  void init_whenQuantityOrCashIsInvalid_shouldThrowException(int quantity, int cash) {
    AdaptiveBidder bidder = new AdaptiveBidder();
    assertThrows(IllegalArgumentException.class, () -> bidder.init(quantity, cash));
  }

  @Test
  void init_whenQuantityAndCashAreValid_shouldSetInitialState() {
    int quantity = 10;
    int cash = 100;
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(quantity, cash);
    assertEquals(cash, bidder.getOwnCash());
    assertEquals(cash, bidder.getOpponentCash());
    assertEquals(5, bidder.getTotalRounds());
    assertEquals(0, bidder.getOwnWins());
    assertEquals(0, bidder.getOpponentWins());
    assertEquals(0, bidder.getTies());
  }

  @Test
  void bids_shouldUpdateStateCorrectlyOnWin() {
    int quantity = 10;
    int cash = 100;
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(quantity, cash);
    bidder.bids(30, 20);
    assertEquals(70, bidder.getOwnCash());
    assertEquals(80, bidder.getOpponentCash());
    assertEquals(1, bidder.getOwnWins());
  }

  @Test
  void bids_shouldUpdateStateCorrectlyOnTies() {
    int quantity = 10;
    int cash = 100;
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(quantity, cash);
    bidder.bids(50, 50);
    assertEquals(50, bidder.getOwnCash());
    assertEquals(50, bidder.getOpponentCash());
    assertEquals(1, bidder.getTies());
  }

  @Test
  void bids_whenOpponentWins_shouldIncreaseOpponentWins() {
    int quantity = 10;
    int cash = 100;
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(quantity, cash);
    bidder.bids(50, 60);
    assertEquals(1, bidder.getOpponentWins());
    assertEquals(40, bidder.getOpponentCash());
    assertEquals(50, bidder.getOwnCash());
  }

  @ParameterizedTest
  @CsvSource({
    "-5, -1", // Both bids negative
    "-20, 50", // Own bid negative
    "13, -10", // Other bid negative
  })
  void bids_whenAnyBidIsNegative_shouldThrowException(int own, int other) {
    AdaptiveBidder bidder = new AdaptiveBidder();
    assertThrows(IllegalArgumentException.class, () -> bidder.bids(own, other));
  }

  @ParameterizedTest
  @CsvSource({
    "10, 110", // Opponent bid exceeds opponent's remaining cash
    "200, 50", // Own bid exceeds own remaining cash
    "130, 500", // Both bids exceed cash
  })
  void bids_whenValuesExceedRemainingCash_shouldThrowException(int own, int other) {
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(150, 100);
    assertThrows(IllegalArgumentException.class, () -> bidder.bids(own, other));
  }

  @Test
  void placeBid_whenLeading_shouldBidMinimally() {
    int quantity = 10;
    int cash = 100;
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(quantity, cash);

    // Simulate 2 wins for us, 0 for opponent
    bidder.bids(20, 15);
    bidder.bids(20, 15);

    int bid = bidder.placeBid();
    assertEquals(20, bid);
  }

  @Test
  void placeBid_whenTrailing_shouldBidAggressively() {
    int quantity = 10;
    int cash = 100;
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(quantity, cash);

    // Simulate 2 losses
    bidder.bids(10, 25);
    bidder.bids(10, 25);

    int bid = bidder.placeBid();
    assertTrue(bid > 30 && bid <= 38);
  }

  @Test
  void placeBid_onFinalRound_shouldBidRemainingCash() {
    int quantity = 14;
    int cash = 200;
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(quantity, cash);
    // Exhaust 6 rounds
    for (int i = 0; i < 6; i++) {
      bidder.bids(10, 10);
    }
    assertEquals(bidder.getOwnCash(), bidder.placeBid());
  }

  @Test
  void placeBid_withZeroCash_shouldReturnZero() {
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(2, 0);
    assertEquals(0, bidder.placeBid());
  }

  // --- Auction Simulation Tests ---
  @Test
  void againstAggressiveOpponent_shouldConserveCash() {
    int quantity = 10;
    int totalCash = 100;
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(quantity, totalCash);

    // Simulate an aggressive opponent who bids half their cash each round
    int ourCashSpent = 0;
    for (int round = 0; round < 5; round++) {
      int ourBid = bidder.placeBid();
      int theirBid = bidder.getOpponentCash() / 2;

      bidder.bids(ourBid, theirBid);
      ourCashSpent += ourBid;
    }
    assertTrue(ourCashSpent < totalCash * 0.9);
    assertTrue(bidder.getOwnWins() > bidder.getOpponentWins());
  }

  @Test
  void againstConservativeOpponent_shouldDominate() {
    int quantity = 10;
    int totalCash = 100;
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(quantity, totalCash);

    // Simulate a conservative opponent who always bids a fixed 10 MU amount
    for (int round = 0; round < 5; round++) {
      int ourBid = bidder.placeBid();
      bidder.bids(ourBid, 10);
    }
    assertTrue(bidder.getOwnWins() > bidder.getOpponentWins());
  }

  @Test
  void againstRandomOpponent_shouldWin() {
    int quantity = 100;
    int totalCash = 1000;
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(quantity, totalCash);

    // Simulate an opponent who bids randomly within their cash limit
    Random rand = new Random();
    for (int round = 0; round < 50; round++) {
      int ourBid = bidder.placeBid();
      int theirBid = rand.nextInt(bidder.getOpponentCash() + 1);
      bidder.bids(ourBid, theirBid);
    }
    assertTrue(bidder.getOwnCash() >= 0);
    assertTrue(bidder.getOpponentCash() >= 0);
    assertTrue(bidder.getOwnWins() >= bidder.getOpponentWins());
  }

  @Test
  void shouldNeverExceedOwnCash() {
    AdaptiveBidder bidder = new AdaptiveBidder();
    bidder.init(20, 100);
    for (int i = 0; i < 10; i++) {
      int bid = bidder.placeBid();
      assertTrue(bid >= 0 && bid <= bidder.getOwnCash());
      bidder.bids(bid, 0);
    }
  }
}
