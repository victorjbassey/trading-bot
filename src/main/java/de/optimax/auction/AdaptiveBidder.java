package de.optimax.auction;

/**
 * An adaptive bidding agent for sequential 2-QU auctions.
 *
 * <p>Strategy highlights:
 *
 * <ul>
 *   <li><b>Dynamic adjustment</b>: Modifies aggression based on win deficit/surplus
 *   <li><b>Resource conservation</b>: Bases bids on remaining cash/rounds ratio
 *   <li><b>Opponent modeling</b>: Tracks opponent's cash expenditure and win count
 *   <li><b>Round prioritization</b>: Increases bids when trailing, minimizes when leading
 * </ul>
 *
 * Primary objective is to secure more units than opponent. If tied, maximizes retained cash.
 *
 * @implNote Tracks key auction state through:
 *     <pre>
 *   - ownCash/opponentCash: Remaining monetary units (MU)
 *   - ownWins/opponentWins: Won batches (2 QU per win)
 *   - ties: Split batches (1 QU each)
 *   - totalRounds: Total batches = quantity / 2
 * </pre>
 */
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
    if (this.ownCash == 0) {
      return 0;
    }

    int roundsLeft = this.totalRounds - (this.ownWins + this.opponentWins + this.ties);
    // Calculate average bids per remaining round
    int avgOwnBid = this.ownCash / roundsLeft;
    int avgOpponentBid = this.opponentCash / roundsLeft;
    int deficit = this.opponentWins - this.ownWins; // Negative if we're leading

    int bid;
    if (deficit > 0) {
      // We're behind: Bid opponent's average + deficit-adjusted bonus
      bid = Math.min(ownCash, avgOpponentBid + 1);
      int bonus = (deficit * avgOwnBid) / roundsLeft;
      bid = Math.min(ownCash, bid + bonus);
    } else {
      // We're leading or tied: Bid minimum to win or tie cheaply
      bid = Math.min(avgOwnBid, avgOpponentBid + 1);
    }
    return bid;
  }

  @Override
  public void bids(int own, int other) {
    if (own < 0 || other < 0) {
      throw new IllegalArgumentException("bids cannot be negative");
    }
    if (own > this.ownCash || other > this.opponentCash) {
      throw new IllegalArgumentException("bids exceed available cash");
    }
    // Update cash and win counts
    this.ownCash -= own;
    this.opponentCash -= other;
    if (own > other) {
      this.ownWins++;
    } else if (own < other) {
      this.opponentWins++;
    } else {
      this.ties++;
    }
  }

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
