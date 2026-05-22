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
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ProficiencyCommand::showProficiency))
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            return showProficiencyFor(player, ctx.getSource());
                        })
        );
    }

    private static int showProficiency(CommandContext<CommandSourceStack> ctx) {
        try {
            ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
            return showProficiencyFor(target, ctx.getSource());
        } catch (Exception e) {
            ctx.getSource().sendFailure(Component.literal("Player not found!"));
            return 0;
        }
    }

    private static int showProficiencyFor(ServerPlayer player, CommandSourceStack source) {
        var capOpt = player.getCapability(ProficiencyCounterProvider.PLAYER_COUNTER_CAP).resolve();
        if (capOpt.isPresent()) {
            int points = capOpt.get().getPoints();
            source.sendSuccess(() -> Component.literal(
                    "§a%s §fhas §e%d §fproficiency points.".formatted(
                            player.getName().getString(), points
                    )
            ), false);
        } else {
            source.sendFailure(Component.literal("§cProficiency counter not found for this player."));
        }
        return 1;
    }
}