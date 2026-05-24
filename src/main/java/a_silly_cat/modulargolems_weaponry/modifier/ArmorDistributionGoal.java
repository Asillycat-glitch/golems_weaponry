package a_silly_cat.modulargolems_weaponry.modifier;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import dev.xkmc.modulargolems.content.entity.metalgolem.MetalGolemEntity;
import dev.xkmc.modulargolems.content.entity.mode.GolemModes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.List;
import java.util.function.Predicate;

public class ArmorDistributionGoal extends Goal {

    private final AbstractGolemEntity<?, ?> golem;
    private final int level;
    private int cooldown = 0;
    private static final int COOLDOWN_TICKS = 100;

    public ArmorDistributionGoal(AbstractGolemEntity<?, ?> golem, int level) {
        this.golem = golem;
        this.level = level;
    }

    @Override
    public boolean canUse() {
        if (golem.getMode() != GolemModes.STAND) return false;
        if (cooldown > 0) {
            cooldown--;
            return false;
        }
        cooldown = COOLDOWN_TICKS;
        return true;
    }

    @Override
    public void tick() {
        int x = golem.getPersistentData().getInt("BoundContainerX");
        int y = golem.getPersistentData().getInt("BoundContainerY");
        int z = golem.getPersistentData().getInt("BoundContainerZ");
        if (x == 0 && y == 0 && z == 0) return;

        BlockPos containerPos = new BlockPos(x, y, z);
        BlockEntity be = golem.level().getBlockEntity(containerPos);
        if (be == null) return;

        IItemHandler handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().orElse(null);
        if (handler == null) return;

        // 遍历容器所有槽位
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.extractItem(slot, 1, true); // 模拟提取
            if (stack.isEmpty()) continue;

            // 1. 判断物品是否可作为装备（任何有装备槽位的物品，包括专属装备）
            EquipmentSlot intendedSlot = Mob.getEquipmentSlotForItem(stack);
            if (intendedSlot == null) continue; // 不是可穿戴物品，跳过

            // 2. 寻找附近符合条件的傀儡
            AABB searchBox = golem.getBoundingBox().inflate(5 * level);
            List<AbstractGolemEntity> nearbyGolems = golem.level().getEntitiesOfClass(
                    AbstractGolemEntity.class, searchBox, e -> e != golem && e.getOwner() == golem.getOwner()
            );

            for (AbstractGolemEntity target : nearbyGolems) {
                // 3. 类型兼容检查
                boolean isMetalTarget = target instanceof MetalGolemEntity;
                String itemNamespace = stack.getItem().builtInRegistryHolder().key().location().getNamespace();
                boolean isModularGolemsEquipment = "modulargolems".equals(itemNamespace);

                // 大型傀儡只能穿戴 ModularGolems 专属装备
                if (isMetalTarget && !isModularGolemsEquipment) continue;
                // 普通傀儡不应穿戴专属装备（避免专属效果错误）
                if (!isMetalTarget && isModularGolemsEquipment) continue;

                // 4. 检查目标傀儡对应槽位是否为空
                EquipmentSlot targetSlot = intendedSlot; // 此时槽位已确定
                if (target.getItemBySlot(targetSlot).isEmpty()) {
                    // 5. 实际提取并装备
                    ItemStack extracted = handler.extractItem(slot, 1, false);
                    target.setItemSlot(targetSlot, extracted);
                    break; // 此物品已装备给一个傀儡，跳出内层循环，继续处理下一个容器物品
                }
            }
        }
    }
}