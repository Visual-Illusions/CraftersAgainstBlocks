/*
 * This file is part of CraftersAgainstBlocks.
 *
 * Copyright © 2015 Visual Illusions Entertainment
 *
 * CraftersAgainstBlocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License v3 as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License v3 for more details.
 *
 * You should have received a copy of the GNU General Public License v3 along with this program.
 * If not, see http://www.gnu.org/licenses/gpl.html.
 */
package net.visualillusionsent.crafters.against.blocks.canary;

import net.canarymod.chat.MessageReceiver;
import net.canarymod.chat.ReceiverType;
import net.canarymod.commandsys.Command;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.DisconnectionHook;
import net.canarymod.plugin.PluginListener;
import net.visualillusionsent.crafters.against.blocks.CraftersAgainstBlocks;
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPluginInformationCommand;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public final class CABListener extends VisualIllusionsCanaryPluginInformationCommand implements PluginListener {

    public CABListener(CanaryAgainstBlocks plugin) {
        super(plugin);
    }

    @HookHandler
    public void userLeave(DisconnectionHook hook) {
        if (CraftersAgainstBlocks.isPlaying(hook.getPlayer().getUUID())) {
            CraftersAgainstBlocks.removeUser(hook.getPlayer().getUUID());
        }
    }

    @Command(
            aliases = { "cab", "craftersagainstblocks" },
            permissions = "",
            description = "Main Crafters Against Blocks command",
            toolTip = "/cab <debug>",
            version = 2
    )
    public void main(MessageReceiver receiver, String[] args) {
        super.sendInformation(receiver);
    }

    @Command(
            aliases = { "join" },
            parent = "cab",
            permissions = "cab.join",
            description = "Join a game of Crafters Against Blocks",
            toolTip = "/cab join"
    )
    public void joinGame(MessageReceiver receiver, String[] args) {
        if (receiver.getReceiverType().equals(ReceiverType.PLAYER)) {
            getPlugin().scoreBoard.addUser(receiver.asPlayer());
        }
    }

    @Command(
            aliases = { "part", "leave" },
            parent = "cab",
            permissions = "cab.part",
            description = "Departs a game of Crafters Against Blocks",
            toolTip = "/cab part"
    )
    public void partGame(MessageReceiver receiver, String[] args) {
        if (receiver.getReceiverType().equals(ReceiverType.PLAYER)) {
            getPlugin().scoreBoard.removeUser(receiver.asPlayer());
        }
    }

    @Command(
            aliases = { "debug" },
            parent = "cab",
            permissions = "cab.debug",
            description = "Crafters Against Blocks debugging command",
            toolTip = "/cab debug",
            min = 1
    )
    public void debug(MessageReceiver receiver, String[] args) {
        if (args[0].equals("score")) {
            if (args.length > 1) {
                if (args[1].equals("add")) {
                    if (args.length > 2 && args[2].toLowerCase().equals("rando")) {
                        getPlugin().scoreBoard.awardPointToRando();
                        return;
                    }
                    else if (receiver.getReceiverType().equals(ReceiverType.PLAYER)) {
                        getPlugin().scoreBoard.awardPointTo(receiver.asPlayer());
                        return;
                    }
                }
            }
        }
        else if (args[0].equals("reset")) {
            getPlugin().scoreBoard.resetGame();
            receiver.notice("Game reset!");
            return;
        }
        receiver.notice("unknown debug command");
    }

    @Override
    protected CanaryAgainstBlocks getPlugin() {
        return (CanaryAgainstBlocks)plugin;
    }
}
