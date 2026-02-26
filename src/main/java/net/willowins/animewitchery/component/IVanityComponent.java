package net.willowins.animewitchery.component;

import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.inventory.Inventory;

public interface IVanityComponent extends Component, AutoSyncedComponent {
    Inventory getInventory();
}
