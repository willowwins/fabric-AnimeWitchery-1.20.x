package net.willowins.animewitchery.util;

import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.VillagerProfession;
import net.willowins.animewitchery.item.ModItems;
import net.willowins.animewitchery.villager.ModVillagers;

public class ModCustomTrades {
    public static void registerCustomTrades() {
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.FARMER,1,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                       new ItemStack(Items.WHEAT_SEEDS,1),
                       new ItemStack(ModItems.STRAWBERRY_SEEDS,1),
                       100,5,0.5f
                    ));
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.WHEAT_SEEDS,1),
                            new ItemStack(ModItems.LEMON_SEEDS,1),
                            100,5,0.5f
                    ));
                });
        TradeOfferHelper.registerVillagerOffers(VillagerProfession.CLERIC,5,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.EMERALD,16),
                            new ItemStack(Items.NETHERITE_INGOT,1),
                            new ItemStack(Items.TOTEM_OF_UNDYING,1),
                            5,12,0.6f
                    ));



                });

        TradeOfferHelper.registerVillagerOffers(ModVillagers.SOUND_MASTER,1,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.IRON_INGOT,1),
                            new ItemStack(ModItems.SILVER,1),
                            100,1,0f
                    ));



                });
        TradeOfferHelper.registerWanderingTraderOffers(1,
                factories -> {
                    factories.add((entity, random) -> new TradeOffer(
                            new ItemStack(Items.IRON_INGOT,1),
                            new ItemStack(ModItems.SILVER,2),
                            100,5,0.5f

                    ));
                });
    }
}
