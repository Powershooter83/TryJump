package me.prouge.tryjump.core.managers;

import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class ScoreboardManager {

    LocalTime time = LocalTime.of(0, 10, 0);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");

    private Objective objective;


    public void createScoreboard(final ArrayList<TryJumpPlayer> playerArrayList, final int mapLength) {
        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("stats", "dummy");
        objective.setDisplayName("§6§lTryJump§7§l-§6§lMC");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Team time = scoreboard.registerNewTeam("time");
        time.setPrefix("§8● ");
        time.setSuffix("§b05:30");
        time.addEntry("§a");

        objective.getScore("§e").setScore(playerArrayList.size() + 3);
        objective.getScore("§fVerbleibende Zeit§8: ").setScore(playerArrayList.size() + 2);
        objective.getScore("§a").setScore(playerArrayList.size() + 1);
        objective.getScore("§b").setScore(playerArrayList.size());

        for (int i = 0; i < playerArrayList.size(); i++) {
            TryJumpPlayer tp = playerArrayList.get(i);
            Team team = scoreboard.registerNewTeam(tp.getPlayer().getName());
            objective.getScore(tp.getPlayer().getName()).setScore(i);
            team.addEntry(tp.getPlayer().getName());
            team.setPrefix("§a");
            team.setSuffix(" §8┃ §e" + calculateScore(tp, mapLength) + "%");
        }

        playerArrayList.forEach(tp -> tp.getPlayer().setScoreboard(scoreboard));

        this.objective = objective;
    }

    public void updateScoreboard(final ArrayList<TryJumpPlayer> playerArrayList, final int mapLength) {

        time = time.minusSeconds(1);
        playerArrayList.sort((tp1, tp2) -> Float.compare(tp1.getWalkedDistance(), tp2.getWalkedDistance()));

        int position = 0;
        for (TryJumpPlayer tp : playerArrayList) {
            Team team = objective.getScoreboard().getTeam(tp.getPlayer().getName());
            objective.getScore(tp.getPlayer().getName()).setScore(position);

            team.setSuffix(" §8┃ §e" + calculateScore(tp, mapLength) + "%");

            position++;
        }

        Team timeTeam = objective.getScoreboard().getTeam("time");
        timeTeam.setSuffix("§b" + time.format(formatter));
    }

    private int calculateScore(TryJumpPlayer tp, final int mapLength) {
        return (int) ((tp.getWalkedDistance() / mapLength) * 100);
    }

}
