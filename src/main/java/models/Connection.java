package models;
import java.util.Date;
import java.util.Objects;


public class Connection {

  private String id;
  private Date timestamp;
  private int state;
  private Room room;
  public String username;

  public Connection(String id, String username)
  {
    this.id = id;
    this.timestamp = new Date();
    this.state = 0; // 0,1,2,3= idle, pending or active
    this.username = username;
  }

  public String getId() {return this.id;}
  public Date getTimestamp(){return this.timestamp;}
  public String getUsername(){return this.username;}
  public void setTimestamp(){this.timestamp = new Date();}
  public int getState(){return this.state;}
  public Room getRoom(){return this.room;}
  public void setRoom(Room room){this.room = room;}
  public void setState(int state){this.state = state;}

  @Override
  public String toString() {
    return "name: "+this.username+"\nid: "+this.id+"\ntimestamp: "+this.timestamp+"\nstate: "+this.state+"\n";
  }

}

