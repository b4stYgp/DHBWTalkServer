package models;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Room {

    public int roomNo;
    public String urlString;
    public ArrayList<Connection> active;
    public ArrayList<Connection> pending;

    public Room(String stringUrl, int roomNo) {
        this.roomNo = roomNo;
        this.urlString = stringUrl;
        this.active = new ArrayList<Connection>();
        this.pending = new ArrayList<Connection>();

        // TODO URLConnection conn = url.openConnection(); bei Dennis
    }

    public void addActive(Connection conn){this.active.add(conn);}
    public void addPending(Connection conn){this.pending.add(conn);}
    public void clearRoom(){this.active.clear();this.pending.clear();}
    public int getRoomNo(){return this.roomNo;}
    public Boolean isEmpty(){return this.active.isEmpty() && this.pending.isEmpty();}

    public Boolean checkRooms(Connection conn)
    {
        if (active.contains(conn) || pending.contains(conn)){
            return true;
        }
        return false;
    }

    public void dropConn(Connection conn)
    {
        this.active.remove(conn);
        this.pending.remove(conn);
        if (this.active.size()+this.pending.size() < 2)
        {
            this.clearRoom();
        }
    }

    @Override
    public String toString() {
        return "active: "+this.active+"\npending: "+this.pending+"\nroomNo: "+this.roomNo+"\nurl: "+this.urlString+"\n";
    }

}
