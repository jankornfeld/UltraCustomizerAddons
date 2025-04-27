package me.mynameisjan.playerpointschecks;

import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.entity.Player;

public class HasPlayerPoints extends Element {
    public HasPlayerPoints(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Has PlayerPoints";
    }

    @Override
    public String getInternalName() {
        return "HasPlayerPoints";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.COMPARATOR;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Checks whether the given player has enough PlayerPoints",
        };
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{
                new Argument("player", "Player", DataType.PLAYER, elementInfo),
                new Argument("number", "Number", DataType.NUMBER, elementInfo),
        };
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[0];
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[]{
                new Child(elementInfo, "true") {
                    @Override
                    public String getName() {
                        return "True";
                    }

                    @Override
                    public String[] getDescription() {
                        return new String[]{
                                "If the player has enough PlayerPoints",
                        };
                    }

                    @Override
                    public XMaterial getIcon() {
                        return XMaterial.GREEN_STAINED_GLASS_PANE;
                    }
                },
                new Child(elementInfo, "false") {
                    @Override
                    public String getName() {
                        return "False";
                    }

                    @Override
                    public String[] getDescription() {
                        return new String[]{
                                "If the player has not enough PlayerPoints",
                        };
                    }

                    @Override
                    public XMaterial getIcon() {
                        return XMaterial.RED_STAINED_GLASS_PANE;
                    }
                },
        };
    }

    @Override
    public void run(ElementInfo elementInfo, ScriptInstance scriptInstance) {
        Player player = (Player) this.getArguments(elementInfo)[0].getValue(scriptInstance);
        Long minPoints = (Long) this.getArguments(elementInfo)[1].getValue(scriptInstance);

        PlayerPointsAPI api = PlayerPoints.getInstance().getAPI();
        if (api == null) {
            UltraCustomizer.getInstance().log("PlayerPoints API is null. Unable to obtain data.");
            return;
        }

        if (api.look(player.getUniqueId()) >= minPoints) {
            this.getConnectors(elementInfo)[0].run(scriptInstance);
        } else {
            this.getConnectors(elementInfo)[1].run(scriptInstance);
        }
    }
}
