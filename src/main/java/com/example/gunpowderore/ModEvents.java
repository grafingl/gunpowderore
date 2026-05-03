package com.example.gunpowderore;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.ArrayList;

/**
 * Перехватывает дропы пороховых руд и заменяет их на порох.
 * Срабатывает на стороне сервера после стандартной обработки лут-таблицы.
 */
@EventBusSubscriber(modid = GunpowderOreMod.MODID)
public class ModEvents {

    @SubscribeEvent
    public static void onBlockDrops(BlockDropsEvent event) {
        BlockState state = event.getState();
        Block block = state.getBlock();

        // Проверяем, что это наш блок
        boolean isOurOre = block == ModBlocks.GUNPOWDER_ORE.get()
                        || block == ModBlocks.DEEPSLATE_GUNPOWDER_ORE.get();
        if (!isOurOre) return;

        // Если ломали с Silk Touch — оставляем стандартный дроп (сам блок)
        ItemStack tool = event.getTool();
        if (tool != null && hasSilkTouch(tool)) {
            // Стандартное поведение — пусть выпадает сам блок (это уже происходит, ничего не трогаем)
            // Но на всякий случай заменим вручную, чтобы точно был блок:
            event.getDrops().clear();
            BlockPos pos = event.getPos();
            ItemEntity entity = new ItemEntity(
                event.getLevel(),
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                new ItemStack(block)
            );
            event.getDrops().add(entity);
            return;
        }

        // Иначе — заменяем дроп на порох
        var random = event.getLevel().getRandom();
        int count = 1 + random.nextInt(2); // 1-2 пороха

        // Применяем Fortune
        if (tool != null) {
            int fortuneLevel = getFortuneLevel(tool);
            if (fortuneLevel > 0) {
                int multiplier = Math.max(1, random.nextInt(fortuneLevel + 2));
                count *= multiplier;
            }
        }

        // Чистим стандартные дропы и добавляем свой
        event.getDrops().clear();
        BlockPos pos = event.getPos();
        ItemEntity entity = new ItemEntity(
            event.getLevel(),
            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
            new ItemStack(Items.GUNPOWDER, count)
        );
        event.getDrops().add(entity);
    }

    private static boolean hasSilkTouch(ItemStack stack) {
        ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (var entry : enchants.entrySet()) {
            if (entry.getKey().is(Enchantments.SILK_TOUCH)) {
                return entry.getIntValue() > 0;
            }
        }
        return false;
    }

    private static int getFortuneLevel(ItemStack stack) {
        ItemEnchantments enchants = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (var entry : enchants.entrySet()) {
            if (entry.getKey().is(Enchantments.FORTUNE)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }
}
