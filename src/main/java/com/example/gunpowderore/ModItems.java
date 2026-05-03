package com.example.gunpowderore;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(GunpowderOreMod.MODID);

    public static final DeferredItem<BlockItem> GUNPOWDER_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("gunpowder_ore", ModBlocks.GUNPOWDER_ORE);

    public static final DeferredItem<BlockItem> DEEPSLATE_GUNPOWDER_ORE_ITEM =
            ITEMS.registerSimpleBlockItem("deepslate_gunpowder_ore", ModBlocks.DEEPSLATE_GUNPOWDER_ORE);
}
