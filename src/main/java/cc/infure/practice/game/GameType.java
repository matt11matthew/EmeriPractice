package cc.infure.practice.game;

/**
 * Created by Matthew E on 6/12/2017.
 */
public enum GameType {
    RANKED_DEBUFF_POTION,
    RANKED_NO_DEBUFF_POTION,
    RANKED_UHC_BUILD,
    RANKED_MCSG,
    RANKED_VANILLA,

    UNRANKED_DEBUFF_POTION,
    UNRANKED_NO_DEBUFF_POTION,
    UNRANKED_UHC_BUILD,
    UNRANKED_MCSG,
    UNRANKED_VANILLA;



    public static GameType[] getUnRankedGames() {
        return new GameType[]{UNRANKED_DEBUFF_POTION, UNRANKED_MCSG, UNRANKED_VANILLA, UNRANKED_UHC_BUILD, UNRANKED_NO_DEBUFF_POTION};
    }

    public static GameType[] getRankedGames() {
        return new GameType[]{RANKED_DEBUFF_POTION, RANKED_MCSG, RANKED_VANILLA, RANKED_UHC_BUILD, RANKED_NO_DEBUFF_POTION};
    }
}
