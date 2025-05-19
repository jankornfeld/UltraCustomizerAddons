package me.mynameisjan.regionclearer;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector2;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.List;

public class RegionClearer extends Element {
    public RegionClearer(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "RegionClearer";
    }

    @Override
    public String getInternalName() {
        return "RegionClearer";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.WOODEN_AXE;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                "Sets a given region in a given world with nothing but air",
        };
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{
                new Argument("rString", "Region name", DataType.STRING, elementInfo),
                new Argument("wString", "World name", DataType.STRING, elementInfo),
        };
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[0];
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[]{new DefaultChild(elementInfo, "next")};
    }

    @Override
    public void run(ElementInfo elementInfo, ScriptInstance scriptInstance) {
        String regionName = (String) this.getArguments(elementInfo)[0].getValue(scriptInstance);
        String worldName = (String) this.getArguments(elementInfo)[1].getValue(scriptInstance);

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        World world = Bukkit.getWorld(worldName);

        if (world == null) {
            this.handleLogging("World " + worldName + " not found");
            return;
        }
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);

        assert BlockTypes.AIR != null;
        BlockState airState = BlockTypes.AIR.getDefaultState();

        try(EditSession editSession = WorldEdit.getInstance().newEditSession(weWorld)) {
            RegionManager regions = container.get(weWorld);

            if (regions == null) {
                this.handleLogging("No regions found for world " + worldName);
                this.getConnectors(elementInfo)[0].run(scriptInstance);
                return;
            }

            ProtectedRegion region = regions.getRegion(regionName);
            if (region != null) {
                if (region instanceof ProtectedCuboidRegion) {
                    CuboidRegion cRegion = new CuboidRegion(weWorld, region.getMinimumPoint(), region.getMaximumPoint());
                    editSession.setBlocks((Region) cRegion, airState);
                } else if (region instanceof ProtectedPolygonalRegion) {
                    List<BlockVector2> points = region.getPoints();
                    int minY = region.getMinimumPoint().y();
                    int maxY = region.getMaximumPoint().y();

                    Polygonal2DRegion polyRegion = new Polygonal2DRegion(weWorld, points, minY, maxY);
                    editSession.setBlocks((Region) polyRegion, airState);
                }
            } else {
                this.handleLogging("Region " + regionName + " not found");
            }
            this.getConnectors(elementInfo)[0].run(scriptInstance);
        } catch (Exception e) {
            this.handleLogging(e.getMessage());
            this.getConnectors(elementInfo)[0].run(scriptInstance);
        }
    }

    private void handleLogging(String message) {
        this.plugin.getBootstrap().getLogger().warning(String.format("[%s] %s", this.getName(), message));
    }
}
