package a_silly_cat.modulargolems_weaponry.command;

import a_silly_cat.modulargolems_weaponry.capability.IProficiencyCounter;
import a_silly_cat.modulargolems_weaponry.capability.ProficiencyCounterProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ProficiencyCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("proficiency")
                        .requires(source -> source.hasPermission(2))
                        .then(Commands.argument("player", EntityArgument.player())
                                .executes(ProficiencyCommand::showProficiency))
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            return showProficiencyFor(player, ctx.getSource());
                        })
        );
    }

    private static int showProficiency(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
            return showProficiencyFor(target, ctx.getSource());
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Player not found!"));
            return 0;
        }
    }

    private static int showProficiencyFor(ServerPlayer player, CommandSourceStack source) {
        IProficiencyCounter cap = player.getCapability(ProficiencyCounterProvider.KILL_COUNTER_CAP).resolve().orElse(null);
        if (cap != null) {
            source.sendSuccess(() -> Component.literal(
                    "§a%s: §fKills %d/%d | Level %d/%d".formatted(
                            player.getName().getString(),
                            cap.getCurrentKills(),
                            cap.getRequiredKills(),
                            cap.getLevel(),
                            cap.getMaxLevel()
                    )
            ), false);
        } else {
            source.sendFailure(Component.literal("§cProficiency counter not found for this player."));
        }
        return 1;
    }
}