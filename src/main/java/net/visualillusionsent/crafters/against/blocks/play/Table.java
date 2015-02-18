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
package net.visualillusionsent.crafters.against.blocks.play;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.canarymod.chat.ChatFormat;
import net.visualillusionsent.crafters.against.blocks.CraftersAgainstBlocks;
import net.visualillusionsent.crafters.against.blocks.cards.BlackCard;
import net.visualillusionsent.crafters.against.blocks.cards.BlackCardDeck;
import net.visualillusionsent.crafters.against.blocks.cards.WhiteCard;
import net.visualillusionsent.crafters.against.blocks.cards.WhiteCardDeck;
import net.visualillusionsent.crafters.against.blocks.user.HumanUser;
import net.visualillusionsent.crafters.against.blocks.user.RandoCardrissian;
import net.visualillusionsent.crafters.against.blocks.user.User;
import net.visualillusionsent.minecraft.plugin.ModMessageReceiver;
import net.visualillusionsent.utils.BooleanUtils;
import net.visualillusionsent.utils.FileUtils;
import net.visualillusionsent.utils.JarUtils;
import net.visualillusionsent.utils.Verify;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public final class Table {
    public static final String CAB = ChatFormat.GRAY.concat("[") + ChatFormat.WHITE.concat("CAB") + ChatFormat.GRAY.concat("]") + ChatFormat.RESET.concat(" ");
    private static final BlackCardDeck bcd = new BlackCardDeck();
    private static final WhiteCardDeck wcd = new WhiteCardDeck();
    private static final Map<ModMessageReceiver, HumanUser> players = Maps.newHashMap();
    // private static final XMLOutputter xmlSerializer = new XMLOutputter(Format.getPrettyFormat().setExpandEmptyElements(false).setOmitDeclaration(true).setOmitEncoding(true).setLineSeparator("\n"));
    private static SAXBuilder fileBuilder = new SAXBuilder();

    private static CraftersAgainstBlocks modInstance;
    private static RandoCardrissian rando;
    private static int czarindex = 0;
    private static Round inProgress = null;

    public static void setModReference(CraftersAgainstBlocks craftersagainstblocks) {
        if (modInstance == null) {
            modInstance = craftersagainstblocks;
            if (craftersagainstblocks.randoCardrissian()) {
                rando = new RandoCardrissian(craftersagainstblocks);
            }
        }
    }

    public static void loadBlackCard(BlackCard blackCard) {
        Verify.notNull(blackCard, "BlackCard blackCard");
        bcd.addBlackCard(blackCard);
    }

    public static void loadWhiteCard(WhiteCard whiteCard) {
        Verify.notNull(whiteCard, "WhiteCard whiteCard");
        wcd.addCard(whiteCard);
    }

    public static WhiteCard dealWhiteCard() {
        return wcd.dealCard();
    }

    public static BlackCard dealBlackCard() {
        return bcd.dealCard();
    }

    public static boolean isPlaying(ModMessageReceiver receiver) {
        return players.containsKey(receiver);
    }

    public static void removeUser(ModMessageReceiver receiver) {
        players.remove(receiver);
        if (inProgress.roundStarted() && !inProgress.roundEnded()) {
            inProgress.remove(players.get(receiver));
        }
    }

    public static void addUser(ModMessageReceiver receiver) {
        players.put(receiver, new HumanUser(receiver));
    }

    public static HumanUser getUser(ModMessageReceiver receiver) {
        return players.get(receiver);
    }

    public static RandoCardrissian getRandoCardrissian() {
        return rando;
    }

    public static void allPlayersDraw(HumanUser cardCzar, int draw) {
        if (draw == 0) {
            return;
        }

        for (HumanUser humanUser : players.values()) {
            if (humanUser.equals(cardCzar)) {
                continue;
            }
            for (int count = 0; count < draw; count++) {
                humanUser.giveCard(wcd.dealCard());
            }
        }
    }

    public static void informPlayers(BlackCard inplay) {
        for (HumanUser humanUser : players.values()) {
            humanUser.inform(CAB.concat(inplay.toString()));
        }
    }

    public static void informPlayers(String message) {
        for (HumanUser humanUser : players.values()) {
            humanUser.inform(CAB.concat(message));
        }
    }

    public static boolean packingHeat() {
        return modInstance.packingHeat();
    }

    private static HumanUser getCzar() {
        if (inProgress.roundStarted() && !inProgress.roundEnded()) {
            throw new IllegalStateException("Getting the Card Czar during a round is progress is outside manipulations.");
        }
        return new ArrayList<HumanUser>(players.values()).get(czarindex++);
    }

    public static Round getRoundInProgress() {
        return inProgress;
    }

    public static void awardPointTo(User winner) {
        modInstance.awardPointTo(winner);
    }

    public static void startRound() {
        if (players.size() > 3 && (inProgress == null || inProgress.roundEnded())) {
            inProgress = new Round(Lists.newArrayList(players.values()), getCzar());
        }
    }

    public static boolean loadCards(String setDirectory) {
        File base = new File(setDirectory);
        File[] sets = base.listFiles();
        if (sets == null) {
            if (!new File(base, "baseset").mkdirs()) {
                modInstance.getPluginLogger().log(Level.SEVERE, "Unable to create card set directories, Plugin unable to run.");
                return false;
            }
            try {
                FileUtils.cloneFileFromJar(JarUtils.getJarPath(Table.class), "resources/baseset.blackcards.xml", setDirectory.concat("/baseset/blackcards.xml"));
                FileUtils.cloneFileFromJar(JarUtils.getJarPath(Table.class), "resources/baseset.whitecards.xml", setDirectory.concat("/baseset/whitecards.xml"));
            }
            catch (IOException ioex) {
                modInstance.getPluginLogger().log(Level.SEVERE, "Unable to move baseset card files. Plugin unable to run.", ioex);
                return false;
            }

            sets = base.listFiles();
        }
        // technical impossibility safety
        if (sets == null) {
            return false;
        }

        for (File cardsets : sets) {
            if (!cardsets.isDirectory()) {
                // Doesn't Matter
                continue;
            }
            File whiteCards = new File(cardsets, "whitecards.xml");
            File blackCards = new File(cardsets, "blackcards.xml");
            if (!whiteCards.exists() || !blackCards.exists()) {
                modInstance.getPluginLogger().log(Level.WARNING, "The \"" + cardsets.getName() + "\" Card Set is missing a cards file, skipping loading...");
                continue;
            }
            try {
                Document white = fileBuilder.build(whiteCards);
                for (Element card : white.getRootElement().getChildren()) {
                    if (card.getChild("enabled") == null || card.getChild("value") == null) {
                        modInstance.getPluginLogger().log(Level.WARNING, "A white card in the \"" + cardsets.getName() + "\" Card Set is corrupted (invalid/missing value or enabled fields), skipping loading...");
                        continue;
                    }
                    if (!BooleanUtils.parseBoolean(card.getChildText("enabled"))) {
                        // Card disabled
                        continue;
                    }
                    String text = card.getChildText("value");
                    if (text.trim().isEmpty()) {
                        modInstance.getPluginLogger().log(Level.WARNING, "A white card in the \"" + cardsets.getName() + "\" Card Set is corrupted (invalid/missing value text), skipping loading...");
                        continue;
                    }
                    Table.loadWhiteCard(new WhiteCard(cardsets.getName(), text));
                }

                Document black = fileBuilder.build(blackCards);
                for (Element card : black.getRootElement().getChildren()) {
                    if (card.getChild("enabled") == null || card.getChild("value") == null) {
                        modInstance.getPluginLogger().log(Level.WARNING, "A black card in the \"" + cardsets.getName() + "\" Card Set is corrupted (invalid/missing value or enabled fields), skipping loading...");
                        continue;
                    }
                    if (!BooleanUtils.parseBoolean(card.getChild("enabled").getValue())) {
                        // Card disabled
                        continue;
                    }
                    String text = card.getChildText("value");
                    if (text.trim().isEmpty()) {
                        modInstance.getPluginLogger().log(Level.WARNING, "A black card in the \"" + cardsets.getName() + "\" Card Set is corrupted (invalid/missing value text), skipping loading...");
                        continue;
                    }
                    int pick = Math.max(1, text.length() - text.replace("_", "").length());
                    int draw = 0;
                    if (card.getChild("draw") != null) {
                        try {
                            draw = Integer.parseInt(card.getChildText("draw"));
                        }
                        catch (NumberFormatException nfex) {
                            modInstance.getPluginLogger().log(Level.WARNING, "A black card in the \"" + cardsets.getName() + "\" Card Set is corrupted (invalid/missing draw amount), skipping loading...");
                            continue;
                        }
                    }
                    Table.loadBlackCard(new BlackCard(cardsets.getName(), card.getChildText("value"), pick, draw));
                }
            }
            catch (JDOMException e) {
                e.printStackTrace();
                return false;
            }
            catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        bcd.shuffle();
        wcd.shuffle();
        return true;
    }
}
