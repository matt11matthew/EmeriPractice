package net.emeri.practice.menu.enums;

/**
 * Created by Matthew E on 4/29/2017.
 */
public enum EnumMenuSize {
    SMALL_CHEST(27), ONE_ROW(9), FOUR_ROWS(36), LARGE_CHEST(54), NOTHING(0), TWO_ROWS(18), CRAFTING_TABLE(-1);

    private int slots;

    EnumMenuSize(int slots) {
        this.slots = slots;
    }

    public int getSlots() {
        return slots;
    }

    public static int getSizeByAmount(int amount) {
        if (amount < 10) {
            return 9;
        } else if ((amount > 9) && (amount < 19)) {
            return 18;
        } else if ((amount > 18) && (amount < 28)) {
            return 27;
        } else if ((amount > 27) && (amount < 37)) {
            return 36;
        } else if ((amount > 36) && (amount < 46)) {
            return 45;
        }  else if ((amount > 45) && (amount < 55)) {
            return 54;
        }
        return -1;
    }
}
