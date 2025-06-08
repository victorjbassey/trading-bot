package de.optimax.auction;

public class AdaptiveBidder implements Bidder {
  private int totalRounds;
  private int ownCash;
  private int opponentCash;
  private int ownWins;
  private int opponentWins;
  private int ties;

  @Override
  public void init(int quantity, int cash) {
    if (cash < 0) {
      throw new IllegalArgumentException("cash cannot be negative");
    }
    if (quantity < 2 || quantity % 2 != 0) {
      throw new IllegalArgumentException("quantity must be a positive even number");
    }
    // Each round auctions 2 QU. Total rounds = quantity / 2
    this.totalRounds = quantity / 2;
    this.ownCash = cash;
    this.opponentCash = cash; // Opponent starts with same cash
    this.ownWins = 0;
    this.opponentWins = 0;
    this.ties = 0;
  }

  @Override
  public int placeBid() {
    return 0;
  }

  @Override
  public void bids(int own, int other) {}

  // --- Helper Getters for auction state (Useful for testing) ---

  /**
   * Returns the total number of auction rounds.
   *
   * @return the total rounds
   */
  public int getTotalRounds() {
    return totalRounds;
  }

  /**
   * Returns the remaining cash for this bidder.
   *
   * @return the remaining cash
   */
  public int getOwnCash() {
    return ownCash;
  }

  /**
   * Returns the remaining cash for the opponent bidder.
   *
   * @return the opponent's remaining cash
   */
  public int getOpponentCash() {
    return opponentCash;
  }

  /**
   * Returns the number of wins for this bidder.
   *
   * @return the number of wins
   */
  public int getOwnWins() {
    return ownWins;
  }

  /**
   * Returns the number of wins for the opponent bidder.
   *
   * @return the opponent's number of wins
   */
  public int getOpponentWins() {
    return opponentWins;
  }

  /**
   * Returns the number of ties in the auction.
   *
   * @return the number of ties
   */
  public int getTies() {
    return ties;
  }
}
