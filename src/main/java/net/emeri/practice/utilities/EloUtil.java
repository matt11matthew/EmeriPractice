package net.emeri.practice.utilities;

/**
 * Created by Matthew E on 6/13/2017.
 */
public class EloUtil {
    public static final int K_FACTOR = 32;

    /**
     * Returns the new rating for the player after winning or losing to the opponent(s) with the given rating.
     *
     * @param rating         Player's old rating.
     * @param opponentRating The rating of the opposing player(s).
     * @param score          Game score (WIN = 1.0, DRAW = 0.5, LOSS = 0.0).
     * @return Player's new rating.
     */
    public static int newRating(int rating, int opponentRating, double score) {
        double expectedScore = calculateExpectedScore(rating, opponentRating);
        return calculateNewRating(rating, score, expectedScore, K_FACTOR);
    }

    /**
     * Calculates the expected score based on two players.
     * In a 2v2 game opponent rating will be an average of the opposing players rating.
     *
     * @param rating         Player rating.
     * @param opponentRating The rating of the opposing player(s).
     * @return Expected score.
     */
    public static double calculateExpectedScore(int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, (opponentRating - rating) / 400.0));
    }

    /**
     * Calculates the new rating for the player based on the old rating, the game score, the expected game score
     * and the k-factor.
     *
     * @param rating        Player's old rating.
     * @param score         Game score (WIN = 1.0, DRAW = 0.5, LOSS = 0.0).
     * @param expectedScore Expected game score (based on participant ratings).
     * @param kFactor       K-factor.
     * @return Player's new rating.
     */
    private static int calculateNewRating(int rating, double score, double expectedScore, double kFactor) {
        return rating + (int) Math.round(kFactor * (score - expectedScore));
    }

}