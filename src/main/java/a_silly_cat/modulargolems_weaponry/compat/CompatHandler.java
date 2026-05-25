package a_silly_cat.modulargolems_weaponry.compat;

import net.minecraftforge.fml.ModList;

public class CompatHandler {
    public static void init() {
        if (ModList.get().isLoaded("irons_spellbooks")) {
            IronSpellsCompat.init();
        }
    }
}