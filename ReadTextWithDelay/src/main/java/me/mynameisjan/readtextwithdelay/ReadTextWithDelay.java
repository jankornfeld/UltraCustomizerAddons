package me.mynameisjan.readtextwithdelay;

import me.TechsCode.UltraCustomizer.UltraCustomizer;
import me.TechsCode.UltraCustomizer.base.item.XMaterial;
import me.TechsCode.UltraCustomizer.dependencies.commons.io.FileUtils;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.*;
import me.TechsCode.UltraCustomizer.scriptSystem.objects.datatypes.DataType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

public class ReadTextWithDelay extends Element {

    public ReadTextWithDelay(UltraCustomizer ultraCustomizer) {
        super(ultraCustomizer);
    }

    @Override
    public String getName() {
        return "Read Text with Delay";
    }

    @Override
    public String getInternalName() {
        return "ReadTextWithDelay";
    }

    @Override
    public boolean isHidingIfNotCompatible() {
        return false;
    }

    @Override
    public XMaterial getMaterial() {
        return XMaterial.WRITTEN_BOOK;
    }

    @Override
    public String[] getDescription() {
        return new String[]{
                ChatColor.translateAlternateColorCodes('&', "&eReads a .txt file with a specific delay after each text."),
                ChatColor.translateAlternateColorCodes('&', "&eEach line is a separate message."),
                ChatColor.translateAlternateColorCodes('&', "&eAdd a semicolon after your text with a delay in seconds."),
                ChatColor.translateAlternateColorCodes('&', "&9Hello World; 2 &f<- &esends a message to the player with a delay of 2 seconds after it.")
        };
    }

    @Override
    public Argument[] getArguments(ElementInfo elementInfo) {
        return new Argument[]{
                new Argument("path", "Path", DataType.STRING, elementInfo),
                new Argument("player", "Player", DataType.PLAYER, elementInfo),
                new Argument("sound", "Sound", DataType.SOUND, elementInfo)
        };
    }

    @Override
    public OutcomingVariable[] getOutcomingVariables(ElementInfo elementInfo) {
        return new OutcomingVariable[]{};
    }

    @Override
    public Child[] getConnectors(ElementInfo elementInfo) {
        return new Child[]{
                new DefaultChild(elementInfo, "next")
        };
    }

    @Override
    public void run(ElementInfo elementInfo, ScriptInstance scriptInstance) {
        final String path = (String) this.getArguments(elementInfo)[0].getValue(scriptInstance);
        final Player player = (Player) this.getArguments(elementInfo)[1].getValue(scriptInstance);
        Bukkit.getScheduler().runTaskAsynchronously(plugin.getBootstrap(), () -> {

            try {
                List<String> fileTxt = FileUtils.readLines(new File(path), Charset.defaultCharset());
                if (!fileTxt.isEmpty()) {
                    scheduleTask(fileTxt, player, 0, elementInfo, scriptInstance);
                }
            } catch (IndexOutOfBoundsException e) {
                this.getConnectors(elementInfo)[0].run(scriptInstance);
            }
        });
    }

    private void scheduleTask(List<String> fileTxt, Player player, int i, ElementInfo elementInfo, ScriptInstance scriptInstance) {
        Sound sound = (Sound) this.getArguments(elementInfo)[2].getValue(scriptInstance);
        if (i == 0) {
            player.playSound(player.getLocation(), sound, SoundCategory.MASTER, 1.0F, 1.0F);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', fileTxt.get(i).split(";")[0]));
        }
        if (fileTxt.size() - 1 >= i + 1) {
            String selectedText = fileTxt.get(i + 1);
            if (selectedText != null) {
                String text = selectedText.split(";")[0];
                try {
                    String[] delayStr = fileTxt.get(i).split(";");
                    if (delayStr.length > 1) {
                        long waitFor = Long.parseLong(delayStr[1]);

                        Bukkit.getScheduler().runTaskLater(plugin.getBootstrap(), () -> {
                            player.playSound(player.getLocation(), sound, SoundCategory.MASTER, 1.0F, 1.0F);
                            player.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
                            if (fileTxt.size() > i) {
                                this.scheduleTask(fileTxt, player, i + 1, elementInfo, scriptInstance);
                            }
                        }, waitFor * 20);
                    }
                } catch (SecurityException ignored) {
                }
            }
        } else {
            this.getConnectors(elementInfo)[0].run(scriptInstance);
        }
    }
}