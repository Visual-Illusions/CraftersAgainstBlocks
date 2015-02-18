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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import net.visualillusionsent.crafters.against.blocks.cards.BlackCard;
import net.visualillusionsent.crafters.against.blocks.cards.WhiteCard;
import net.visualillusionsent.crafters.against.blocks.user.HumanUser;
import net.visualillusionsent.crafters.against.blocks.user.RandoCardrissian;
import net.visualillusionsent.crafters.against.blocks.user.User;

import java.util.LinkedList;
import java.util.List;

import static net.visualillusionsent.crafters.against.blocks.play.Table.allPlayersDraw;
import static net.visualillusionsent.crafters.against.blocks.play.Table.getRandoCardrissian;
import static net.visualillusionsent.crafters.against.blocks.play.Table.informPlayers;
import static net.visualillusionsent.crafters.against.blocks.play.Table.packingHeat;

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
    private State state = State.STARTED;

    private enum State {
        STARTED,
        CZARSELECTION,
        ENDED
    }

    public Round(List<HumanUser> players, HumanUser cardCzar) {
        this.players = players;
        this.cardCzar = cardCzar;
        players.remove(cardCzar);
        cardCzar.inform("You are the Card Czar this round");
        beginRound();
    }

    private void beginRound() {
        inplay = Table.dealBlackCard();

        if (packingHeat() && inplay.getDraw() == 2) {
            allPlayersDraw(cardCzar, 1);
        }
        else {
            // Only time this should happen is with the Draw 2, Pick 3
            allPlayersDraw(cardCzar, inplay.getDraw());
        }

        informPlayers(inplay);
        informPlayers("Use /cab showhand to see the cards you have.");
        informPlayers("Use /cab select # to play white cards.");

        RandoCardrissian rando = getRandoCardrissian();
        if (rando != null) {
            addPlay(rando, rando.playCard(0));
        }
    }

    public void addPlay(User user, WhiteCard... cards) {
        if (state == State.STARTED && isInRound(user)) {
            played.put(user, cards);

            if (played.size() == (players.size() + isRandoPlaying())) {
                showCards();
            }
        }
    }

    public boolean isInRound(User user) {
        return players.contains(user);
    }

    private int isRandoPlaying() {
        return getRandoCardrissian() != null ? 1 : 0;
    }

    public int getSelectionCount() {
        return inplay.getPick();
    }

    private void showCards() {
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
        state = State.CZARSELECTION;
    }

    public void czarSelection(int position) {
        state = State.ENDED;
        LinkedList<WhiteCard[]> list = Lists.newLinkedList(played.values());

        if (position >= list.size()) {
            throw new NumberFormatException();
        }

        WhiteCard[] selection = list.get(position);
        for (WhiteCard win : selection) {
            informPlayers(Table.CAB.concat(win.toString()));
        }
        User winner = played.inverse().get(selection);
        Table.awardPointTo(winner);
        players.clear();
        played.clear();
        Table.startRound();
    }

    public boolean roundStarted() {
        return state == State.STARTED || state == State.CZARSELECTION;
    }

    public boolean roundEnded() {
        return state == State.ENDED;
    }

    public boolean canCzarSelect() {
        return state == State.CZARSELECTION;
    }

    public void remove(HumanUser humanUser) {
        players.remove(humanUser);
        played.remove(humanUser);
        if (played.size() >= (players.size() + isRandoPlaying())) {
            showCards();
        }
    }

    public boolean hasMadePlay(User user) {
        return played.containsKey(user);
    }

    public boolean isCzar(User user) {
        return cardCzar.equals(user);
    }
}
