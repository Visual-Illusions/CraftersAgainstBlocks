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
 *
 *
 * Cards Against Humanity is distributed under a Creative Commons BY-NC-SA 2.0 license.
 * That means you can use and remix the game for free, but you can't sell it without our permission.
 * “Cards Against Humanity” and the CAH logos are trademarks of Cards Against Humanity LLC.
 */
package net.visualillusionsent.crafters.against.blocks.canary;

import com.google.common.collect.HashMultimap;
import net.canarymod.chat.MessageReceiver;
import net.canarymod.chat.ReceiverType;
import net.canarymod.commandsys.Command;
import net.canarymod.hook.HookHandler;
import net.canarymod.hook.player.DisconnectionHook;
import net.canarymod.plugin.PluginListener;
import net.visualillusionsent.crafters.against.blocks.cards.WhiteCard;
import net.visualillusionsent.crafters.against.blocks.play.Round;
import net.visualillusionsent.crafters.against.blocks.play.Table;
import net.visualillusionsent.crafters.against.blocks.user.HumanUser;
import net.visualillusionsent.minecraft.plugin.ModMessageReceiver;
import net.visualillusionsent.minecraft.plugin.canary.CanaryMessageReceiver;
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPluginInformationCommand;

import static net.visualillusionsent.crafters.against.blocks.play.Table.addUser;
import static net.visualillusionsent.crafters.against.blocks.play.Table.isPlaying;
import static net.visualillusionsent.crafters.against.blocks.play.Table.removeUser;

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

    private HashMultimap<MessageReceiver, Integer> pendingSelection = HashMultimap.create();

    @HookHandler
    public void userLeave(DisconnectionHook hook) {
        ModMessageReceiver receiver = new CanaryMessageReceiver(hook.getPlayer());
        if (isPlaying(receiver)) {
            removeUser(receiver);
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
            permissions = "cab.play",
            description = "Join a game of Crafters Against Blocks",
            toolTip = "/cab join"
    )
    public void joinGame(MessageReceiver receiver, String[] args) {
        if (receiver.getReceiverType().equals(ReceiverType.PLAYER)) {
            ModMessageReceiver mrec = new CanaryMessageReceiver(receiver.asPlayer());
            if (!isPlaying(mrec)) {
                getPlugin().scoreBoard.addUser(receiver.asPlayer());
                addUser(mrec);
            }
            else {
                receiver.notice("You are already playing.");
            }
        }
        else {
            receiver.notice("You have to be a player to play Crafters Against Blocks.");
        }
    }

    @Command(
            aliases = { "part", "leave" },
            parent = "cab",
            permissions = "cab.play",
            description = "Departs a game of Crafters Against Blocks",
            toolTip = "/cab part"
    )
    public void partGame(MessageReceiver receiver, String[] args) {
        if (receiver.getReceiverType().equals(ReceiverType.PLAYER)) {
            ModMessageReceiver mrec = new CanaryMessageReceiver(receiver);
            if (isPlaying(mrec)) {
                getPlugin().scoreBoard.removeUser(receiver.asPlayer());
                removeUser(mrec);
            }
        }
    }

    @Command(
            aliases = { "showhand" },
            parent = "cab",
            permissions = "cab.play",
            description = "Departs a game of Crafters Against Blocks",
            toolTip = "/cab part"
    )
    public void showHand(MessageReceiver receiver, String[] args) {
        if (receiver.getReceiverType().equals(ReceiverType.PLAYER)) {
            ModMessageReceiver mrec = new CanaryMessageReceiver(receiver);
            if (isPlaying(mrec)) {
                Table.getUser(mrec).showHand();
            }
            else {
                receiver.notice("You aren't part of the current game. Use /cab join to play.");
            }
        }
    }

    @Command(
            aliases = { "select" },
            parent = "cab",
            permissions = "cab.play",
            description = "Makes White Card selection(s)",
            toolTip = "/cab select <card#>"
    )
    public void select(MessageReceiver receiver, String[] args) {
        if (receiver.getReceiverType().equals(ReceiverType.PLAYER)) {
            ModMessageReceiver mrec = new CanaryMessageReceiver(receiver.asPlayer());
            if (isPlaying(mrec)) {
                HumanUser user = Table.getUser(mrec);
                Round round = Table.getRoundInProgress();
                if (round == null || !round.roundStarted()) {
                    receiver.notice("The round hasn't started yet.");
                    return;
                }
                else if (round.isCzar(user)) {
                    if (!handleCzarSelection(round, user, args[0])) {
                        receiver.notice("You are the Card Czar, and other players are still selecting their cards.");
                    }
                    return;
                }
                else if (!round.isInRound(user)) {
                    receiver.notice("You are only spectating this round.");
                    return;
                }
                else if (round.hasMadePlay(user)) {
                    receiver.notice("You have already made your selection.");
                    return;
                }

                int selection;
                try {
                    selection = Integer.parseInt(args[0]);
                    if (selection > user.getHandSize() || selection < 1) {
                        throw new NumberFormatException();
                    }
                }
                catch (NumberFormatException nfex) {
                    receiver.notice("Invalid selection. Please enter a whole number between 1 and "+user.getHandSize());
                    return;
                }

                int count = round.getSelectionCount();
                if (count > 1) {
                    if (!pendingSelection.containsKey(receiver) || pendingSelection.get(receiver).size() < count) {
                        if (pendingSelection.get(receiver).contains(selection)) {
                            receiver.notice("You have already selected that card, Please make another selection.");
                            return;
                        }
                        pendingSelection.put(receiver, selection);
                        if (pendingSelection.get(receiver).size() < count) {
                            receiver.notice("Please make another selection.");
                            return;
                        }
                    }
                    //Process play
                    WhiteCard[] cards = new WhiteCard[count];
                    Integer[] selections = pendingSelection.get(receiver).toArray(new Integer[count]);
                    for (int index = 0; index < count; index++) {
                        cards[index] = user.playCard(selections[index]);
                    }
                    round.addPlay(user, cards);
                    pendingSelection.removeAll(receiver);
                }
                else {
                    round.addPlay(user, user.playCard(--selection));
                }
            }
        }
    }

    private boolean handleCzarSelection(Round round, HumanUser user, String select) {
        if (!round.canCzarSelect()) {
            return false;
        }

        int selection;
        try {
            selection = Integer.parseInt(select);
            selection--; //Adjust index

            round.czarSelection(selection);
        }
        catch (NumberFormatException nfex) {
            ((MessageReceiver)user.getReceiver().unwrap()).notice("Invalid selection");
        }
        return true;
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
