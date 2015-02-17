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
package net.visualillusionsent.crafters.against.blocks.play;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.visualillusionsent.crafters.against.blocks.CraftersAgainstBlocks;
import net.visualillusionsent.crafters.against.blocks.cards.BlackCard;
import net.visualillusionsent.crafters.against.blocks.cards.WhiteCard;
import net.visualillusionsent.crafters.against.blocks.user.HumanUser;
import net.visualillusionsent.crafters.against.blocks.user.RandoCardrissian;
import net.visualillusionsent.crafters.against.blocks.user.User;

import java.util.List;

import static net.visualillusionsent.crafters.against.blocks.CraftersAgainstBlocks.getRandoCardrissian;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public final class Round {
    private HumanUser cardCzar;
    private BlackCard inplay;
    private List<HumanUser> players;
    private BiMap<User, WhiteCard[]> played = HashBiMap.create();

    public Round(List<HumanUser> players, HumanUser cardCzar) {
        this.players = players;
        this.cardCzar = cardCzar;
        cardCzar.inform("You are the Card Czar this round");
        beingRound();
    }

    public void beingRound() {
        inplay = CraftersAgainstBlocks.dealBlackCard();
        if (inplay.getDraw() > 0) {
            CraftersAgainstBlocks.allPlayersDraw(cardCzar, inplay.getDraw());
        }
        CraftersAgainstBlocks.informPlayers(inplay);
        CraftersAgainstBlocks.informPlayers("Use /cab showhand to see the cards you have.");
        CraftersAgainstBlocks.informPlayers("Use /cab select # to play white cards.");

        //TODO Rando Cardrissian config
        RandoCardrissian rando = getRandoCardrissian();
        addPlay(rando, rando.playCard(0));
    }

    public void addPlay(User user, WhiteCard... cards) {
        played.put(user, cards);

        if (played.size() == (players.size() + isRandoPlaying())) {
            showCards();
        }
    }

    private int isRandoPlaying() {
        return 1;
    }

    public int getSelectionCount() {
        return inplay.getPick();
    }

    public void showCards() {
        for (HumanUser humanUser : players) {
            int count = 1;
            for (WhiteCard[] cards : played.values()) {
                String text = "";
                for (WhiteCard card : cards) {
                    text += "\"" + card.getText() + "\" ";
                }
                humanUser.inform(String.format("#%d: %s", count, text));
                count++;
            }
        }
    }
}
