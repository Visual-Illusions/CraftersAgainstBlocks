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
package net.visualillusionsent.crafters.against.blocks.user;

import net.visualillusionsent.crafters.against.blocks.cards.Hand;
import net.visualillusionsent.crafters.against.blocks.cards.WhiteCard;
import net.visualillusionsent.crafters.against.blocks.play.Table;
import net.visualillusionsent.minecraft.plugin.ModMessageReceiver;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public class HumanUser implements User {
    protected final Hand hand = new Hand();
    private final ModMessageReceiver receiver;

    public HumanUser(ModMessageReceiver receiver) {
        this.receiver = receiver;

        for (int count = 0; count < 10; count++) {
            hand.addCard(Table.dealWhiteCard());
        }
    }

    public WhiteCard playCard(int position) {
        return hand.removeCard(position);
    }

    public void replenishHand() {
        hand.replenish();
    }

    public void giveCard(WhiteCard card) {
        hand.addCard(card);
    }

    public void inform(String msg) {
        receiver.message(msg);
    }

    public boolean equals(Object obj) {
        return !(!(obj instanceof HumanUser) && !(obj instanceof ModMessageReceiver)) && (receiver.equals(obj) || super.equals(obj));
    }

    public void showHand() {
        hand.show(this);
    }

    public ModMessageReceiver getReceiver() {
        return receiver;
    }
}
