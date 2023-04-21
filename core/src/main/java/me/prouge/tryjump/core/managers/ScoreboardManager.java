package me.prouge.tryjump.core.managers;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import me.prouge.tryjump.core.events.GamePlayerFinishedEvent;
import me.prouge.tryjump.core.game.player.TryJumpPlayer;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Singleton
public class ScoreboardManager {

    @Setter
    @Getter
    LocalTime time = LocalTime.of(0, 10, 0);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");

    private Objective objective;

    @Setter
    private boolean finished = false;


    public void createScoreboard(final ArrayList<TryJumpPlayer> playerArrayList, final int mapLength) {
        this.time = LocalTime.of(0, 10, 0);
        org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("stats", "dummy");
        objective.setDisplayName("§6§lTryJump§7§l-§6§lMC");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        Team time = scoreboard.registerNewTeam("time");
        time.setPrefix("§8● ");
        time.setSuffix("§b" + this.time.format(formatter));
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

    public void createLobbyScoreboard(final ArrayList<TryJumpPlayer> playerArrayList) {
        time = LocalTime.of(0, 2, 0);

        playerArrayList.forEach(tp -> {
            org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("stats", "dummy");
            objective.setDisplayName("§6§lTryJump§7§l-§6§lMC");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            Team time = scoreboard.registerNewTeam("time");
            time.setPrefix("§8● ");
            time.setSuffix("§b" + this.time.format(formatter));
            time.addEntry("§a");

            objective.getScore("§e").setScore(8);
            objective.getScore("§fVerbleibende Zeit§8: ").setScore(7);
            objective.getScore("§a").setScore(6);
            objective.getScore("§7").setScore(5);
            objective.getScore("§fCoins§8: ").setScore(4);
            objective.getScore("§b").setScore(3);
            objective.getScore("§c").setScore(2);
            objective.getScore("§8§m---------------------").setScore(1);
            objective.getScore("§8● §aTeams erlaubt").setScore(0);

            Team coins = scoreboard.registerNewTeam(tp.getPlayer().getName());
            coins.setPrefix("§8● ");
            coins.addEntry("§b");
            coins.setSuffix(String.valueOf(tp.getTokens()));
            tp.getPlayer().setScoreboard(scoreboard);
        });

    }

    public void createDeathMatchScoreboard(final ArrayList<TryJumpPlayer> playerArrayList) {
        time = LocalTime.of(0, 10, 0);
        playerArrayList.forEach(tp -> {
            org.bukkit.scoreboard.Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("stats", "dummy");
            objective.setDisplayName("§6§lTryJump§7§l-§6§lMC");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);

            Team time = scoreboard.registerNewTeam("time");
            time.setPrefix("§8● ");
            time.setSuffix("§b" + this.time.format(formatter));
            time.addEntry("§b");

            Team players = scoreboard.registerNewTeam("players");
            players.setPrefix("§8● ");
            players.addEntry("§e");
            players.setSuffix(String.valueOf(playerArrayList.size()));

            Team lives = scoreboard.registerNewTeam(tp.getPlayer().getName());
            lives.setPrefix("§8● ");
            lives.addEntry("§c");
            lives.setSuffix("3");

            objective.getScore("§e").setScore(11);
            objective.getScore("§fVerbleibende Zeit§8: ").setScore(10);
            objective.getScore("§b").setScore(9);
            objective.getScore("§7").setScore(8);
            objective.getScore("§fSpieler§8:").setScore(7);
            objective.getScore("§e").setScore(6);
            objective.getScore("§6").setScore(5);
            objective.getScore("§fLeben§8:").setScore(4);
            objective.getScore("§c").setScore(3);
            objective.getScore("§8").setScore(2);
            objective.getScore("§8§m---------------------").setScore(1);
            objective.getScore("§8● §aTeams erlaubt").setScore(0);

            tp.getPlayer().setScoreboard(scoreboard);

        });
    }

    public void updateDeathMatchScoreboard(final ArrayList<TryJumpPlayer> playerArrayList) {
        time = time.minusSeconds(1);

        playerArrayList.forEach(tp -> {
            Team timeTeam = tp.getPlayer().getScoreboard().getTeam("time");

            Team lives = tp.getPlayer().getScoreboard().getTeam(tp.getPlayer().getName());
            lives.setSuffix(String.valueOf(3 - tp.getDeathMatchDeaths()));

            Team players = tp.getPlayer().getScoreboard().getTeam("players");
            players.setSuffix(String.valueOf(playerArrayList.stream().filter(tpt -> tpt.getDeathMatchDeaths() < 3).count()));

            timeTeam.setSuffix("§b" + time.format(formatter));
        });

    }


    public void updateLobbyScoreboard(final ArrayList<TryJumpPlayer> playerArrayList) {
        time = time.minusSeconds(1);

        playerArrayList.forEach(tp -> {
            Team timeTeam = tp.getPlayer().getScoreboard().getTeam("time");
            timeTeam.setSuffix("§b" + time.format(formatter));
        });

    }


    public void updateScoreboard(final ArrayList<TryJumpPlayer> playerArrayList, final int mapLength) {
        System.out.println("test-3");
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

        if (time.getMinute() == 0 && time.getSecond() == 0 && !finished) {
            Bukkit.getServer().getPluginManager().callEvent(new GamePlayerFinishedEvent(null));
        }
    }

    private int calculateScore(TryJumpPlayer tp, final int mapLength) {
        return (int) ((tp.getWalkedDistance() / mapLength) * 100);
    }


}
