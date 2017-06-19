package net.emeri.practice.team;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matthew E on 6/19/2017.
 */
public class Team {
    private Player leader;
    private List<Player> memberPlayerList;

    public Team(Player leader) {
        this.leader = leader;
        this.memberPlayerList = new ArrayList<>();
    }

    public List<Player> getPlayers() {
        List<Player> playerList = new ArrayList<>();
        playerList.add(leader);
        playerList.addAll(memberPlayerList);
        return playerList;
    }

    public Player getLeader() {
        return leader;
    }

    public void addPlayer(Player player) {
        memberPlayerList.add(player);
    }

    public boolean isMember(Player player) {
        return getPlayers().contains(player);
    }

    public boolean isLeader(Player player) {
        return leader.getUniqueId().equals(player.getUniqueId());
    }

    public List<Player> getMemberPlayerList() {
        return memberPlayerList;
    }

    public void disband() {

    }

    public void removePlayer(Player player) {
        memberPlayerList.remove(player);
    }
}
