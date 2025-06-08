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
}
