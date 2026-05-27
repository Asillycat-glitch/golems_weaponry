package a_silly_cat.modulargolems_weaponry.modifier;

import com.mojang.logging.LogUtils;
import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.nbt.CompoundTag;
import org.slf4j.Logger;

import java.util.EnumSet;
import java.util.List;

public class HaymakerGoal extends Goal {

    private static final Logger LOGGER = LogUtils.getLogger();  // 自己定义一个 Logger

    private final AbstractGolemEntity<?, ?> golem;
    private final int level;
    private double storedDamage = 0;
    private long lastHurtTime = -1;
    private static final String TAG_STORED_DAMAGE = "haymaker_stored_damage";
    private static final String TAG_LAST_HURT_TIME = "haymaker_last_hurt_time";
    private static final String TAG_RAW_DAMAGE = "haymaker_raw_damage";

    // 保持数据包驱动的伤害类型 Key
    public static final ResourceLocation HAYMAKER_ID = new ResourceLocation("mgwe", "haymaker");
    public static final net.minecraft.resources.ResourceKey<DamageType> HAYMAKER_KEY =
            net.minecraft.resources.ResourceKey.create(Registries.DAMAGE_TYPE, HAYMAKER_ID);

    public HaymakerGoal(AbstractGolemEntity<?, ?> golem, int level) {
        this.golem = golem;
        this.level = level;
        this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingHurtEvent event) {
        if ("haymaker".equals(event.getSource().getMsgId())) {
            LivingEntity entity = event.getEntity();
            CompoundTag data = entity.getPersistentData();
            if (data.contains(TAG_RAW_DAMAGE)) {
                float rawDamage = data.getFloat(TAG_RAW_DAMAGE);
                event.setAmount(rawDamage);
                data.remove(TAG_RAW_DAMAGE);
            }
        }
    }

    @SubscribeEvent
    public void onHurt(LivingHurtEvent event) {
        if (event.getEntity() == golem && event.getAmount() > 0) {
            double maxStored = golem.getMaxHealth() * HaymakerModifier.MAX_STORED_DAMAGE_PERCENT;
            storedDamage = Math.min(storedDamage + event.getAmount(), maxStored);
            lastHurtTime = golem.level().getGameTime();
            golem.getPersistentData().putDouble(TAG_STORED_DAMAGE, storedDamage);
            golem.getPersistentData().putLong(TAG_LAST_HURT_TIME, lastHurtTime);
            // 每次受伤时打印存储量
            LOGGER.info("[Haymaker] Golem hurt, stored damage: {}/{}", storedDamage, maxStored);
        }
    }

    public void loadFromNBT() {
        CompoundTag data = golem.getPersistentData();
        if (data.contains(TAG_STORED_DAMAGE)) storedDamage = data.getDouble(TAG_STORED_DAMAGE);
        if (data.contains(TAG_LAST_HURT_TIME)) lastHurtTime = data.getLong(TAG_LAST_HURT_TIME);
    }

    @Override
    public boolean canUse() {
        loadFromNBT();
        LivingEntity target = golem.getTarget();
        if (target == null || !target.isAlive()) return false;

        double maxStored = golem.getMaxHealth() * HaymakerModifier.MAX_STORED_DAMAGE_PERCENT;
        long currentTime = golem.level().getGameTime();
        boolean maxReached = storedDamage >= maxStored;
        boolean decayExpired = lastHurtTime > 0 && (currentTime - lastHurtTime) >= HaymakerModifier.STORED_DAMAGE_DECAY_DELAY;
        boolean inCone = isTargetInCone(target);
        boolean canActivate = storedDamage > 0 && (maxReached || decayExpired) && inCone;

        // 每 2 秒输出一次状态
        if (golem.tickCount % 40 == 0 && storedDamage > 0) {
            LOGGER.info("[Haymaker] Target={}, stored={}/{}, maxReached={}, decayExpired={}, inCone={}",
                    target.getName().getString(), storedDamage, maxStored, maxReached, decayExpired, inCone);
        }

        return canActivate;
    }

    private boolean isTargetInCone(LivingEntity target) {
        Vec3 lookVec = golem.getLookAngle();
        Vec3 dirToTarget = target.position().subtract(golem.position()).normalize();
        double dotProduct = lookVec.dot(dirToTarget);
        double requiredDot = Math.cos(Math.toRadians(HaymakerModifier.ATTACK_ANGLE / 2));
        return dotProduct >= requiredDot && golem.distanceTo(target) <= HaymakerModifier.ATTACK_RANGE;
    }

    @Override
    public void start() {
        LOGGER.info(">>>> 蓄意轰拳释放！存储伤害={} <<<<", storedDamage);

        AABB area = golem.getBoundingBox().inflate(HaymakerModifier.ATTACK_RANGE);
        List<LivingEntity> entities = golem.level().getEntitiesOfClass(LivingEntity.class, area,
                e -> e.isAlive() && golem.canAttack(e) && isTargetInCone(e));

        LOGGER.info("命中 {} 个目标", entities.size());

        if (!entities.isEmpty()) {
            DamageSource trueDamage = new DamageSource(
                    golem.level().registryAccess()
                            .registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(HAYMAKER_KEY)  // 数据包驱动的伤害类型
            );
            for (LivingEntity entity : entities) {
                float damage = (float) storedDamage;
                entity.invulnerableTime = 0;
                entity.getPersistentData().putFloat(TAG_RAW_DAMAGE, damage);
                entity.hurt(trueDamage, damage);
                entity.getPersistentData().remove(TAG_RAW_DAMAGE);
                spawnHitParticles(entity);
                LOGGER.info("对 {} 造成 {} 点真实伤害", entity.getName().getString(), damage);
            }
        }

        storedDamage = 0;
        lastHurtTime = -1;
        golem.getPersistentData().remove(TAG_STORED_DAMAGE);
        golem.getPersistentData().remove(TAG_LAST_HURT_TIME);

        golem.level().playSound(null, golem.blockPosition(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.HOSTILE, 1.0f, 1.0f);
        spawnReleaseParticles();
    }

    private void spawnHitParticles(LivingEntity target) {
        if (golem.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.CRIT, target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(), 5, 0.3, 0.3, 0.3, 0.1);
        }
    }

    private void spawnReleaseParticles() {
        if (golem.level() instanceof ServerLevel serverLevel) {
            Vec3 look = golem.getLookAngle();
            for (int i = 0; i < 20; i++) {
                double x = golem.getX() + look.x * (i * 0.3);
                double y = golem.getEyeY() - 0.3;
                double z = golem.getZ() + look.z * (i * 0.3);
                serverLevel.sendParticles(ParticleTypes.SWEEP_ATTACK, x, y, z, 1, 0.1, 0.1, 0.1, 0.0);
            }
        }
    }
}