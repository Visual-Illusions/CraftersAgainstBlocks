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

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.commandsys.CommandDependencyException;
import net.visualillusionsent.crafters.against.blocks.CraftersAgainstBlocks;
import net.visualillusionsent.crafters.against.blocks.play.Table;
import net.visualillusionsent.crafters.against.blocks.user.HumanUser;
import net.visualillusionsent.crafters.against.blocks.user.RandoCardrissian;
import net.visualillusionsent.crafters.against.blocks.user.User;
import net.visualillusionsent.minecraft.plugin.canary.CanaryMessageReceiver;
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPlugin;
import net.visualillusionsent.utils.PropertiesFile;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public class CanaryAgainstBlocks extends VisualIllusionsCanaryPlugin implements CraftersAgainstBlocks {
    CABScoreboard scoreBoard;

    public final boolean enable() {
        makeConfiguration();
        Table.setModReference(this);
        super.enable();
        scoreBoard = new CABScoreboard(this);
        CABListener CABListener = new CABListener(this);
        registerListener(CABListener);
        try {
            registerCommands(CABListener, false);
        }
        catch (CommandDependencyException e) {
            return false;
        }
        return Table.loadCards("config/CraftersAgainstBlocks/cardsets");
    }

    public final void disable() {
        if (scoreBoard != null) {
            scoreBoard.clear();
        }
    }

    @Override
    public void awardPointTo(User winner) {
        if (winner instanceof RandoCardrissian) {
            scoreBoard.awardPointToRando();
        }
        else {
            Player player = ((CanaryMessageReceiver)((HumanUser)winner).getReceiver()).unwrap().asPlayer();
            scoreBoard.awardPointTo(player);
        }
    }

    /* The Rules and Configurations */
    private void makeConfiguration() {
        PropertiesFile cfg = getConfig();

        cfg.getBoolean("Gambling", false);
        cfg.setComments("Gambling", "If a Black Card is played and a player has more than one White Card they think could win, they can bet one Awesome Point to play an additional White Card. If they win, they keep their point. If they lose, whoever won the round gets the point they wagered.");

        cfg.getBoolean("RebootingTheUniverse", true);
        cfg.setComments("RebootingTheUniverse", "At any time, players may trade in an Awesome Point to return as many White Cards as they'd like to the deck and draw back up to ten.");

        cfg.getBoolean("PackingHeat", false);
        cfg.setComments("PackingHeat", "For Pick 2s, all players draw an extra card before playing the hand to open up more options.");

        cfg.getBoolean("RandoCardrissian", true);
        cfg.setComments("RandoCardrissian", "Every round, A random white card is played.", "This card belongs to the Computer Player, Rando Cardrissian", "And if he wins the game, all players goes home in a state of everlasting shame.");
    }

    @Override
    public boolean gambling() {
        return getConfig().getBoolean("Gambling", false);
    }

    @Override
    public boolean rebootingTheUniverse() {
        return getConfig().getBoolean("RebootingTheUniverse", false);
    }

    @Override
    public boolean packingHeat() {
        return getConfig().getBoolean("PackingHeat", false);
    }

    @Override
    public boolean randoCardrissian() {
        return getConfig().getBoolean("RandoCardrissian", true);
    }

}
