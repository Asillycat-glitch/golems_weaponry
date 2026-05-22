package a_silly_cat.modulargolems_weaponry.event;

import a_silly_cat.modulargolems_weaponry.item.ProficiencyTrinketItem;
import a_silly_cat.modulargolems_weaponry.mgwe;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;
import java.util.function.Predicate;

@Mod.EventBusSubscriber(modid = mgwe.MODID)
public class ProficiencyAttackListener {

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if (!(event.getSource().getEntity() instanceof LivingEntity attacker)) return;

        Predicate<ItemStack> isProficiencyTrinket = stack -> stack.getItem() instanceof ProficiencyTrinketItem;

        // 1. 检查攻击者（玩家）本身
        findTrinket(attacker, isProficiencyTrinket).ifPresent(stack ->
                applyProficiencyBonus(stack, event));

        // 2. 若攻击者是玩家，无需再向下查找
        if (attacker instanceof Player) return;

        // 3. 检查主人（TamableAnimal）
        if (attacker instanceof net.minecraft.world.entity.TamableAnimal tamable) {
            LivingEntity owner = tamable.getOwner();
            if (owner != null) {
                findTrinket(owner, isProficiencyTrinket).ifPresent(stack ->
                        applyProficiencyBonus(stack, event));
            }
        }
        // 4. 检查主人（ModularGolems 的 AbstractGolemEntity）
        else if (attacker instanceof dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity<?, ?> golem) {
            LivingEntity owner = golem.getOwner();
            if (owner != null) {
                findTrinket(owner, isProficiencyTrinket).ifPresent(stack ->
                        applyProficiencyBonus(stack, event));
            }
        }
    }

    private static void applyProficiencyBonus(ItemStack stack, LivingHurtEvent event) {
        if (stack.getItem() instanceof ProficiencyTrinketItem item) {
            int level = item.getLevel(stack);
            float bonus = item.getAttackBonus(level);
            float multiplier = item.getBonusMultiplier(level);

            event.setAmount(event.getAmount() + bonus);
            if (multiplier > 0) {
                event.setAmount(event.getAmount() * (1 + multiplier));
            }
        }
    }

    private static Optional<ItemStack> findTrinket(LivingEntity entity, Predicate<ItemStack> filter) {
        // 关键修复：使用 CuriosApi.getCuriosInventory 替代已弃用的 getCuriosHandler
        // 参考文档: https://docs.illusivesoulworks.com/1.20.x/curios/slots/slot-modifiers
        return CuriosApi.getCuriosInventory(entity).resolve().flatMap(handler -> {
            // 仅在逻辑服务端执行查找，避免在客户端意外加载服务端类
            if (!entity.level().isClientSide) {
                return handler.findFirstCurio(filter).map(SlotResult::stack);
            }
            return Optional.empty();
        });
    }
}