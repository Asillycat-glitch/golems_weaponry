package a_silly_cat.modulargolems_weaponry.compat;

import net.minecraftforge.fml.ModList;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

public class IronSpellsCompat {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init() {
        if (!ModList.get().isLoaded("irons_spellbooks")) return;
        LOGGER.info("铁魔法兼容性初始化中...");
        CullTrinketEventHandler.register();
        LOGGER.info("铁魔法兼容性初始化完成");
    }
}