package net.emeri.practice.game;

/**
 * Created by Matthew E on 6/12/2017.
 */
public enum GameType {
    RANKED_DEBUFF_POTION("Debuff"),
    RANKED_NO_DEBUFF_POTION("No Debuff"),
    RANKED_MCSG("MCSG"),
    RANKED_VANILLA("Vanilla"),

    UNRANKED_DEBUFF_POTION("Debuff"),
    UNRANKED_NO_DEBUFF_POTION("No Debuff"),
    UNRANKED_MCSG("MCSG"),
    UNRANKED_VANILLA("Vanilla");

    private String name;

    GameType(String name) {
        this.name = name;
    }

    public static GameType[] getUnRankedGames() {
        return new GameType[]{UNRANKED_DEBUFF_POTION, UNRANKED_MCSG, UNRANKED_VANILLA, UNRANKED_NO_DEBUFF_POTION};
    }

    public static GameType[] getRankedGames() {
        return new GameType[]{RANKED_DEBUFF_POTION, RANKED_MCSG, RANKED_VANILLA, RANKED_NO_DEBUFF_POTION};
    }

    public String getName() {
        return name;
    }
}
