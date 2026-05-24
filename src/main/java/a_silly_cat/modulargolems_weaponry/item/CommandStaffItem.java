package a_silly_cat.modulargolems_weaponry.item;

import dev.xkmc.modulargolems.content.entity.common.AbstractGolemEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.List;

public class CommandStaffItem extends Item {

    public CommandStaffItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        Player player = context.getPlayer();

        if (be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().isPresent()) {
            AbstractGolemEntity<?, ?> golem = findNearestOwnedGolem(player, pos, 10);
            if (golem != null) {
                golem.getPersistentData().putInt("BoundContainerX", pos.getX());
                golem.getPersistentData().putInt("BoundContainerY", pos.getY());
                golem.getPersistentData().putInt("BoundContainerZ", pos.getZ());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private AbstractGolemEntity<?, ?> findNearestOwnedGolem(Player player, BlockPos pos, double range) {
        Level level = player.level();
        AABB searchBox = new AABB(pos).inflate(range);
        List<AbstractGolemEntity> golems = level.getEntitiesOfClass(
                AbstractGolemEntity.class, searchBox, e -> e.getOwner() == player
        );
        if (!golems.isEmpty()) {
            return golems.get(0);
        }
        return null;
    }
}