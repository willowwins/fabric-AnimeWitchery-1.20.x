package net.willowins.animewitchery.mana;

import dev.onyxstudios.cca.api.v3.component.ComponentV3;

public interface IManaComponent extends ComponentV3 {
    int getMana();
    int getMaxMana();
    void setMana(int amount);
    boolean consume(int amount);
    void regen(int amount);
}
