package net.minecraft.server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ScoreboardServer extends Scoreboard {

    private final MinecraftServer a;
    private final Set b = new HashSet();
    private PersistentScoreboard c;

    public ScoreboardServer(MinecraftServer minecraftserver) {
        this.a = minecraftserver;
    }

    public void handleScoreChanged(ScoreboardScore scoreboardscore) {
        super.handleScoreChanged(scoreboardscore);
        if (this.b.contains(scoreboardscore.getObjective())) {
            this.a.getPlayerList().sendAll(new PacketPlayOutScoreboardScore(scoreboardscore, 0));
        }

        this.b();
    }

    public void handlePlayerRemoved(String s) {
        super.handlePlayerRemoved(s);
        this.a.getPlayerList().sendAll(new PacketPlayOutScoreboardScore(s));
        this.b();
    }

    public void setDisplaySlot(int i, ScoreboardObjective scoreboardobjective) {
        ScoreboardObjective scoreboardobjective1 = this.getObjectiveForSlot(i);

        super.setDisplaySlot(i, scoreboardobjective);
        if (scoreboardobjective1 != scoreboardobjective && scoreboardobjective1 != null) {
            if (this.h(scoreboardobjective1) > 0) {
                this.a.getPlayerList().sendAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            } else {
                this.g(scoreboardobjective1);
            }
        }

        if (scoreboardobjective != null) {
            if (this.b.contains(scoreboardobjective)) {
                this.a.getPlayerList().sendAll(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            } else {
                this.e(scoreboardobjective);
            }
        }

        this.b();
    }

    public boolean addPlayerToTeam(String s, String s1) {
        if (super.addPlayerToTeam(s, s1)) {
            ScoreboardTeam scoreboardteam = this.getTeam(s1);

            this.a.getPlayerList().sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, Arrays.asList(new String[] { s}), 3));
            this.b();
            return true;
        } else {
            return false;
        }
    }

    public void removePlayerFromTeam(String s, ScoreboardTeam scoreboardteam) {
        super.removePlayerFromTeam(s, scoreboardteam);
        this.a.getPlayerList().sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, Arrays.asList(new String[] { s}), 4));
        this.b();
    }

    public void handleObjectiveAdded(ScoreboardObjective scoreboardobjective) {
        super.handleObjectiveAdded(scoreboardobjective);
        this.b();
    }

    public void handleObjectiveChanged(ScoreboardObjective scoreboardobjective) {
        super.handleObjectiveChanged(scoreboardobjective);
        if (this.b.contains(scoreboardobjective)) {
            this.a.getPlayerList().sendAll(new PacketPlayOutScoreboardObjective(scoreboardobjective, 2));
        }

        this.b();
    }

    public void handleObjectiveRemoved(ScoreboardObjective scoreboardobjective) {
        super.handleObjectiveRemoved(scoreboardobjective);
        if (this.b.contains(scoreboardobjective)) {
            this.g(scoreboardobjective);
        }

        this.b();
    }

    public void handleTeamAdded(ScoreboardTeam scoreboardteam) {
        super.handleTeamAdded(scoreboardteam);
        this.a.getPlayerList().sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 0));
        this.b();
    }

    public void handleTeamChanged(ScoreboardTeam scoreboardteam) {
        super.handleTeamChanged(scoreboardteam);
        this.a.getPlayerList().sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 2));
        this.b();
    }

    public void handleTeamRemoved(ScoreboardTeam scoreboardteam) {
        super.handleTeamRemoved(scoreboardteam);
        this.a.getPlayerList().sendAll(new PacketPlayOutScoreboardTeam(scoreboardteam, 1));
        this.b();
    }

    public void a(PersistentScoreboard persistentscoreboard) {
        this.c = persistentscoreboard;
    }

    protected void b() {
        if (this.c != null) {
            this.c.c();
        }
    }

    public List getScoreboardScorePacketsForObjective(ScoreboardObjective scoreboardobjective) {
        ArrayList arraylist = new ArrayList();

        arraylist.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 0));

        for (int i = 0; i < 3; ++i) {
            if (this.getObjectiveForSlot(i) == scoreboardobjective) {
                arraylist.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            }
        }

        Iterator iterator = this.getScoresForObjective(scoreboardobjective).iterator();

        while (iterator.hasNext()) {
            ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next();

            arraylist.add(new PacketPlayOutScoreboardScore(scoreboardscore, 0));
        }

        return arraylist;
    }

    public void e(ScoreboardObjective scoreboardobjective) {
        List list = this.getScoreboardScorePacketsForObjective(scoreboardobjective);
        Iterator iterator = this.a.getPlayerList().players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet packet = (Packet) iterator1.next();

                entityplayer.playerConnection.sendPacket(packet);
            }
        }

        this.b.add(scoreboardobjective);
    }

    public List f(ScoreboardObjective scoreboardobjective) {
        ArrayList arraylist = new ArrayList();

        arraylist.add(new PacketPlayOutScoreboardObjective(scoreboardobjective, 1));

        for (int i = 0; i < 3; ++i) {
            if (this.getObjectiveForSlot(i) == scoreboardobjective) {
                arraylist.add(new PacketPlayOutScoreboardDisplayObjective(i, scoreboardobjective));
            }
        }

        return arraylist;
    }

    public void g(ScoreboardObjective scoreboardobjective) {
        List list = this.f(scoreboardobjective);
        Iterator iterator = this.a.getPlayerList().players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                Packet packet = (Packet) iterator1.next();

                entityplayer.playerConnection.sendPacket(packet);
            }
        }

        this.b.remove(scoreboardobjective);
    }

    public int h(ScoreboardObjective scoreboardobjective) {
        int i = 0;

        for (int j = 0; j < 3; ++j) {
            if (this.getObjectiveForSlot(j) == scoreboardobjective) {
                ++i;
            }
        }

        return i;
    }
}
