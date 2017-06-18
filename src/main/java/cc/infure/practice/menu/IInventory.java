package cc.infure.practice.menu;

import cc.infure.practice.menu.item.MenuItem;

/**
 * Created by matt1 on 3/21/2017.
 */
public interface IInventory {

    void addItem(MenuItem menuItem);

    void setItem(int slot, MenuItem menuItem);

    void removeItem(int slot);
}
