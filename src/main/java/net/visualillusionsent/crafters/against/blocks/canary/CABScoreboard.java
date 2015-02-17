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

import com.google.common.collect.Lists;
import net.canarymod.Canary;
import net.canarymod.api.entity.living.humanoid.Player;
import net.canarymod.api.scoreboard.Score;
import net.canarymod.api.scoreboard.ScoreObjective;
import net.canarymod.api.scoreboard.ScorePosition;
import net.canarymod.api.scoreboard.Scoreboard;
import net.visualillusionsent.crafters.against.blocks.CraftersAgainstBlocks;
import net.visualillusionsent.minecraft.plugin.canary.CanaryMessageReceiver;

import java.util.ArrayList;

/**
 * Copyright (C) 2015 Visual Illusions Entertainment
 * All Rights Reserved.
 *
 * @author Jason Jones (darkdiplomat)
 */
public final class CABScoreboard {
    private final Scoreboard scoreboard;
    private final ScoreObjective objective;

    public CABScoreboard() {
        scoreboard = Canary.scoreboards().getScoreboard("CraftersAgainstHumanity");
        objective = scoreboard.addScoreObjective("AwesomePoints");
        objective.setDisplayName("Awesome Points");

        //TODO: Configuration for Rando Cardrissian
        scoreboard.getScore("Rando Cardrissian", objective).setScore(0);
    }

    public void addUser(Player player) {
        scoreboard.getScore(player, objective).setScore(0);
        objective.setScoreboardPosition(ScorePosition.SIDEBAR, player);
        CraftersAgainstBlocks.addUser(new CanaryMessageReceiver(player));
    }

    public void removeUser(Player player) {
        scoreboard.removeScore(player.getName(), objective);
        scoreboard.clearScoreboardPosition(ScorePosition.SIDEBAR, player);
        CraftersAgainstBlocks.removeUser(new CanaryMessageReceiver(player));
    }

    public void awardPointTo(Player player) {
        scoreboard.getScore(player, objective).addToScore(1);
    }

    public void awardPointToRando() {
        scoreboard.getScore("Rando Cardrissian", objective).addToScore(1);
    }

    void resetGame() {
        ArrayList<String> scoreNames = Lists.newArrayList();
        for (Score score : scoreboard.getAllScores()) {
            scoreNames.add(score.getName());
        }
        for (String name : scoreNames) {
            scoreboard.removeScore(name);
        }
    }

    void clear() {
        resetGame();
        scoreboard.removeScoreObjective(objective);
    }
}
