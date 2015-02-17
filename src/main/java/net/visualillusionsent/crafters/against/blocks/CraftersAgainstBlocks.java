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
package net.visualillusionsent.crafters.against.blocks;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.visualillusionsent.crafters.against.blocks.cards.BlackCard;
import net.visualillusionsent.crafters.against.blocks.cards.BlackCardDeck;
import net.visualillusionsent.crafters.against.blocks.cards.WhiteCard;
import net.visualillusionsent.crafters.against.blocks.cards.WhiteCardDeck;
import net.visualillusionsent.crafters.against.blocks.user.User;
import net.visualillusionsent.minecraft.plugin.VisualIllusionsPlugin;
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
import java.util.UUID;
import java.util.logging.Level;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public class CraftersAgainstBlocks {
    private static VisualIllusionsPlugin modReference;
    private static final BlackCardDeck bcd = new BlackCardDeck();
    private static final WhiteCardDeck wcd = new WhiteCardDeck();
    private static final BiMap<UUID, User> players = HashBiMap.create();
    // private static final XMLOutputter xmlSerializer = new XMLOutputter(Format.getPrettyFormat().setExpandEmptyElements(false).setOmitDeclaration(true).setOmitEncoding(true).setLineSeparator("\n"));
    private static SAXBuilder fileBuilder = new SAXBuilder();

    public static void setModReference(VisualIllusionsPlugin visualIllusionsPlugin) {
        if (modReference == null) {
            modReference = visualIllusionsPlugin;
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

    public static boolean isPlaying(UUID uuid) {
        return players.containsKey(uuid);
    }

    public static void removeUser(UUID uuid) {
        players.remove(uuid);
    }

    public static void addUser(UUID uuid) {
        players.put(uuid, new User(uuid));
    }

    public static User getUser(UUID uuid) {
        return players.get(uuid);
    }

    public static boolean loadCards(String setDirectory) {
        File base = new File(setDirectory);
        File[] sets = base.listFiles();
        if (sets == null) {
            if (!new File(base, "baseset").mkdirs()) {
                modReference.getPluginLogger().log(Level.SEVERE, "Unable to create card set directories, Plugin unable to run.");
                return false;
            }
            try {
                FileUtils.cloneFileFromJar(JarUtils.getJarPath(CraftersAgainstBlocks.class), "resources/baseset.blackcards.xml", setDirectory.concat("/baseset/blackcards.xml"));
                FileUtils.cloneFileFromJar(JarUtils.getJarPath(CraftersAgainstBlocks.class), "resources/baseset.whitecards.xml", setDirectory.concat("/baseset/whitecards.xml"));
            }
            catch (IOException ioex) {
                modReference.getPluginLogger().log(Level.SEVERE, "Unable to move baseset card files. Plugin unable to run.", ioex);
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
                modReference.getPluginLogger().log(Level.WARNING, "The \"" + cardsets.getName() + "\" Card Set is missing a cards file, skipping loading...");
                continue;
            }
            try {
                Document white = fileBuilder.build(whiteCards);
                for (Element card : white.getRootElement().getChildren()) {
                    if (card.getChild("enabled") == null || card.getChild("value") == null) {
                        modReference.getPluginLogger().log(Level.WARNING, "A white card in the \"" + cardsets.getName() + "\" Card Set is corrupted (invalid/missing value or enabled fields), skipping loading...");
                        continue;
                    }
                    if (!BooleanUtils.parseBoolean(card.getChildText("enabled"))) {
                        // Card disabled
                        continue;
                    }
                    String text = card.getChildText("value");
                    if (text.trim().isEmpty()) {
                        modReference.getPluginLogger().log(Level.WARNING, "A white card in the \"" + cardsets.getName() + "\" Card Set is corrupted (invalid/missing value text), skipping loading...");
                        continue;
                    }
                    CraftersAgainstBlocks.loadWhiteCard(new WhiteCard(cardsets.getName(), text));
                }
                Document black = fileBuilder.build(blackCards);
                for (Element card : white.getRootElement().getChildren()) {
                    if (card.getChild("enabled") == null || card.getChild("value") == null || card.getChild("pick") == null) {
                        // Warn faulty card
                        continue;
                    }
                    if (!BooleanUtils.parseBoolean(card.getChild("enabled").getValue())) {
                        // Card disabled
                        continue;
                    }
                    int pick;
                    try {
                        pick = Integer.parseInt(card.getChildText("pick"));
                    }
                    catch (NumberFormatException nfex) {
                        modReference.getPluginLogger().log(Level.WARNING, "A black card in the \"" + cardsets.getName() + "\" Card Set is corrupted (invalid pick count), skipping loading...");
                        continue;
                    }
                    String text = card.getChildText("value");
                    if (text.trim().isEmpty()) {
                        modReference.getPluginLogger().log(Level.WARNING, "A black card in the \"" + cardsets.getName() + "\" Card Set is corrupted (invalid/missing value text), skipping loading...");
                        continue;
                    }
                    CraftersAgainstBlocks.loadBlackCard(new BlackCard(cardsets.getName(), card.getChildText("value"), pick));
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
        return true;
    }
}
