package models;

import java.util.ArrayList;

public class Room {

    public int roomNo;
    public String urlString;
    public ArrayList<ConnectionModel> active;
    public ArrayList<ConnectionModel> pending;

    public Room(String stringUrl, int roomNo) {
        this.roomNo = roomNo;
        this.urlString = stringUrl;
        this.active = new ArrayList<ConnectionModel>();
        this.pending = new ArrayList<ConnectionModel>();

        // TODO URLConnection conn = url.openConnection(); bei Dennis
    }

    public void addActive(ConnectionModel conn){this.active.add(conn);}
    public void addPending(ConnectionModel conn){this.pending.add(conn);}
    public void clearRoom(){this.active.clear();this.pending.clear();}
    public int getRoomNo(){return this.roomNo;}
    public Boolean isEmpty(){return this.active.isEmpty() && this.pending.isEmpty();}

    public Boolean checkRooms(ConnectionModel conn)
    {
        if (active.contains(conn) || pending.contains(conn)){
            return true;
        }
        return false;
    }

    public void dropConn(ConnectionModel conn)
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
