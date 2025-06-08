# Post-Mortem: AdaptiveBidder Trading Bot

## ✅ What Went Well

- **Structured Implementation**: The code follows clean design principles with strong encapsulation, JavaDoc documentation, and modular logic.
- **Dynamic Strategy**: The bidding logic adapts to the current win/loss balance, adjusts based on the opponent’s estimated cash, and scales aggression when trailing.
- **State Tracking**: Tracks key auction variables (rounds left, cash, win counts) to inform decisions without overcomplicating the model.
- **Robustness**: Includes input validation, well-defined boundaries, and comprehensive unit tests using JUnit.
- **Build Quality**: Integrated with Maven and adheres to formatting standards via Spotless and `google-java-format`. 
- **Tooling & Automation**:
    - **CI via GitHub Actions**: Continuous integration is set up using GitHub Actions to automatically run unit tests, check formatting, and build the project on every pull request and commit to the **main** branch.
    - **Code Quality via SonarCloud**: The project is integrated with [SonarCloud](https://sonarcloud.io) for static code analysis, tracking code smells, test coverage, and maintainability.
    - **Code Formatting**: Enforced consistently using [Spotless](https://github.com/diffplug/spotless) with the `google-java-format` rule set.

## 🟡 What Could Be Improved

- **Limited Opponent Modeling**: While the bot estimates average opponent spending, it doesn’t yet learn from bid patterns or exploit irrational behavior.
- **Static Heuristics**: Aggression scaling is heuristic-based and might not perform optimally in all scenarios (e.g., against bluffing strategies).
- **Lack of Randomization**: Bidding is deterministic, which may be exploitable by a non-deterministic opponent or a bot trained against fixed strategies.
- **Test Scenarios**: Unit tests cover correctness but could be expanded to simulate more competitive matchups and stress tests.

## 🚀 Lessons Learned / Next Steps

- **Introduce Learning**: Future versions could incorporate a learning mechanism to adjust bidding parameters based on past rounds.
- **Behavioral Tracking**: Incorporate pattern detection to identify overly aggressive or conservative opponents and exploit accordingly.
- **Stochastic Elements**: Add a controlled amount of randomness to bids (e.g., ±1) to prevent predictability in head-to-head matchups.
- **Performance Benchmarking**: Set up automated simulations against various opponent types (greedy, random, mirror) to measure effectiveness.

---

This task was a valuable exercise in balancing game theory, resource allocation, and clean software engineering practices under constraints.
