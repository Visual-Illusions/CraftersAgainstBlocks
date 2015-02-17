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

import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.commandsys.CommandDependencyException;
import net.visualillusionsent.crafters.against.blocks.CraftersAgainstBlocks;
import net.visualillusionsent.minecraft.plugin.canary.VisualIllusionsCanaryPlugin;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public class CanaryAgainstBlocks extends VisualIllusionsCanaryPlugin {
    CABScoreboard scoreBoard;

    public final boolean enable() {
        CraftersAgainstBlocks.setModReference(this);
        super.enable();
        scoreBoard = new CABScoreboard();
        CABListener CABListener = new CABListener(this);
        registerListener(CABListener);
        try {
            registerCommands(CABListener, false);
        }
        catch (CommandDependencyException e) {
            return false;
        }
        return CraftersAgainstBlocks.loadCards("config/CraftersAgainstBlocks/cardsets");
    }

    public final void disable() {
        if (scoreBoard != null) {
            scoreBoard.clear();
        }
    }

    public void addUserToBoard(Player player) {
        scoreBoard.addUser(player);
    }
}
