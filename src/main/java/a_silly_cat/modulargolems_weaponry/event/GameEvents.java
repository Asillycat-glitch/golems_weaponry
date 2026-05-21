package a_silly_cat.modulargolems_weaponry.event;

// 原有 import 保持不变
import a_silly_cat.modulargolems_weaponry.mgwe;
import a_silly_cat.modulargolems_weaponry.capability.ProficiencyCounterProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = mgwe.MODID)
public class GameEvents {

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            event.addCapability(
                    new ResourceLocation(mgwe.MODID, "proficiency_counter"),
                    new ProficiencyCounterProvider()
            );
        }
    }

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide) return;

        Entity attacker = event.getSource().getEntity();
        if (attacker == null) return;

        Player player = null;
        if (attacker instanceof Player p) {
            player = p;
        } else {
            player = getPlayerOwner(attacker);
        }

        if (player != null) {
            player.getCapability(ProficiencyCounterProvider.KILL_COUNTER_CAP).ifPresent(cap -> cap.addKill(1));
        }
    }

    @Nullable
    private static Player getPlayerOwner(Entity entity) {
        // 1. 原版可驯服生物（狼、猫等实现了 OwnableEntity 接口）
        if (entity instanceof OwnableEntity ownable) {
            UUID ownerUUID = ownable.getOwnerUUID();
            if (ownerUUID != null) {
                return entity.level().getPlayerByUUID(ownerUUID);
            }
        }

        // 2. 尝试通过反射获取 getOwnerUUID() 方法（兼容 ModularGolems 等未实现 OwnableEntity 的模组）
        try {
            Method method = entity.getClass().getMethod("getOwnerUUID");
            Object result = method.invoke(entity);
            if (result instanceof UUID uuid) {
                return entity.level().getPlayerByUUID(uuid);
            }
        } catch (Exception ignored) {
            // 方法不存在或调用失败，忽略
        }

        return null;
    }
}