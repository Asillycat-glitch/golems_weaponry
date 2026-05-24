package a_silly_cat.modulargolems_weaponry.command;

import a_silly_cat.modulargolems_weaponry.capability.ProficiencyCounterProvider;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
                        // /proficiency [target]
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(ProficiencyCommand::showProficiency)
                        )
                        .executes(ctx -> {
                            ServerPlayer player = ctx.getSource().getPlayerOrException();
                            return showProficiencyFor(player, ctx.getSource());
                        })
                        // /proficiency add <target> <amount>
                        .then(Commands.literal("add")
                                .then(Commands.argument("target", EntityArgument.player())
                                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                                .executes(ProficiencyCommand::addPoints)
                                        )
                                )
                        )
        );
    }

    // 查询指定玩家
    private static int showProficiency(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        return showProficiencyFor(target, ctx.getSource());
    }

    // 显示点数
    private static int showProficiencyFor(ServerPlayer player, CommandSourceStack source) {
        var capOpt = player.getCapability(ProficiencyCounterProvider.PLAYER_COUNTER_CAP).resolve();
        if (capOpt.isPresent()) {
            int points = capOpt.get().getPoints();
            source.sendSuccess(() -> Component.literal(
                    "§a" + player.getName().getString() + " §fhas §e" + points + " §fproficiency points."
            ), false);
        } else {
            source.sendFailure(Component.literal("§cProficiency counter not found for this player."));
        }
        return 1;
    }

    // 添加点数
    private static int addPoints(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
        int amount = IntegerArgumentType.getInteger(ctx, "amount");

        var capOpt = target.getCapability(ProficiencyCounterProvider.PLAYER_COUNTER_CAP).resolve();
        if (capOpt.isPresent()) {
            capOpt.get().addPoints(amount);
            ctx.getSource().sendSuccess(() -> Component.literal(
                    "§aAdded §e" + amount + " §fpoints to §a" + target.getName().getString() + "§f. " +
                            "Now has §e" + capOpt.get().getPoints() + " §fpoints."
            ), true);
            return 1;
        } else {
            ctx.getSource().sendFailure(Component.literal("§cProficiency counter not found for target player."));
            return 0;
        }
    }
}