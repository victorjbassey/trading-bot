package de.optimax.auction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
