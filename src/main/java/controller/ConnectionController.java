package controller;

import models.ConnectionModel;
import models.Room;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
class ConnectionController {

  Boolean VERBOSE = true;
  List<ConnectionModel> listConnection = new ArrayList<ConnectionModel>();
  List<Room> listRoom = new ArrayList<Room>(){{
    add(new Room("https://bbb.dhbw-heidenheim.de/?M=xzNW8jI0M2U3Mu862pnC", 0));
    add(new Room("https://bbb.dhbw-heidenheim.de/?M=eBtgjI0NDNlMushX31Zj", 1));
    add(new Room("https://bbb.dhbw-heidenheim.de/?M=MwIJjI0MmQwMgkoqcKIf", 2));}};

  ConnectionController() {Cleaner();}

  private void Cleaner(){
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        if (!listConnection.isEmpty())
        {
          Iterator<ConnectionModel> connectionIterator = listConnection.iterator();
          while (connectionIterator.hasNext())
          {
            ConnectionModel conn = connectionIterator.next();
            if (new Date().getTime() - conn.getTimestamp().getTime() >= 1000000)
            {
              System.out.println("User timed out: " + conn);
              connectionIterator.remove();
              Iterator<Room> roomsIterator = listRoom.iterator();
              while (roomsIterator.hasNext())
              {
                Room room = roomsIterator.next(); room.dropConn(conn);
              }
            }
          }
        }
      }
    }, 0, 1000000);}

  @GetMapping("/heartbeat")
  public ConnectionModel updateConnection(@RequestHeader("username") String username,
                                     @RequestHeader("authorization") String id)
  {

    // TODO check if user exists

    for(ConnectionModel conn : listConnection)
    {
      if (Objects.equals(conn.getId(), id))
      {
        conn.setTimestamp();
        if(VERBOSE){System.out.println("\n/heartbeat: thunk " + username + "\n");}
        return conn;
      }
    }
    ConnectionModel newConnection = new ConnectionModel(id, username);
    listConnection.add(newConnection);
    
    if(VERBOSE){System.out.println("\n/heartbeat: created " + username);}
    
    return newConnection;
  }

  @GetMapping("/rooms")
  public String checkPending(@RequestHeader("username") String requestingUser,
                             @RequestHeader("authorization") String id)
  {
    ConnectionModel checkingConn = listConnection.stream()
            .filter(conn -> requestingUser.equals(conn.getUsername()))
            .findAny()
            .orElse(null);

    String myRooms = "";
    Iterator<Room> roomsIterator = listRoom.iterator();
    while (roomsIterator.hasNext())
    {
      Room room = roomsIterator.next();
      if (room.checkRooms(checkingConn))
      {
        myRooms += ("{" + room.getRoomNo() + ":" + room.active + "}");
      };
    }
    if(VERBOSE){System.out.println("/rooms: {" + myRooms + "}");}

    return "{" + myRooms + "}";
  }

  @GetMapping("/url")
  public String getURL(@RequestHeader("username") String username,
                              @RequestHeader("authorization") String id)
  {
    ConnectionModel askingConn = listConnection.stream()
            .filter(conn -> username.equals(conn.getUsername()))
            .findAny()
            .orElse(null);
    return askingConn.getRoom().urlString;
  }


  @GetMapping("/leave")
  public ConnectionModel leaveRoom(@RequestHeader("username") String username,
                       @RequestHeader("authorization") String id)
  {
    ConnectionModel leavingCon = listConnection.stream()
            .filter(conn -> username.equals(conn.getUsername()))
            .findAny()
            .orElse(null);
    leavingCon = new ConnectionModel(id, username);
    return leavingCon;
  }

  @GetMapping("/join/{roomNo}")
  public void joinRoom(@RequestHeader("username") String requestingUser,
                       @RequestHeader("authorization") String id,
                       @PathVariable("roomNo") int roomNo)
  {
    ConnectionModel joiningConn = listConnection.stream()
            .filter(conn -> requestingUser.equals(conn.getUsername()))
            .findAny()
            .orElse(null);
    Room room = listRoom.get(roomNo);

    if (!(room.pending.contains(joiningConn) || room.active.contains(joiningConn)))
    {
      if(VERBOSE){System.out.println("/join/" + roomNo + ": failed. not in pending nor in active");}
      return;
    }
    joiningConn.setRoom(room);
    joiningConn.setState(2);
  }

  @GetMapping("/info")
  public String getInfo()
  {
    String infoString = "";
    for(Room room:listRoom){infoString += room+"\n";}
    for(ConnectionModel conn:listConnection){infoString += conn+"\n";}

    if(VERBOSE){System.out.println("/info\n" + infoString);}

    return infoString;
  }

  @GetMapping("/add/{username}")
  public void callConnection(@RequestHeader("username") String requestingUser,
                             @RequestHeader("authorization") String id,
                             @PathVariable("username") String acceptingUser)
  {
    if (requestingUser == acceptingUser)
    {
      if(VERBOSE){System.out.println("/add/" + acceptingUser + ": failed. cant connect to yourself");}
      return;
    }

    ConnectionModel requestingConn = listConnection.stream()
            .filter(conn -> requestingUser.equals(conn.getUsername()))
            .findAny()
            .orElse(null);

    ConnectionModel acceptingConn = listConnection.stream()
            .filter(conn -> acceptingUser.equals(conn.getUsername()))
            .findAny()
            .orElse(null);

    if (requestingUser == null || acceptingUser == null)
    {
      if(VERBOSE){System.out.println("/add/" + acceptingUser + ": failed. users do not exist");}
      return;
    }

    switch (((requestingConn.getState()==0) ? 0 : 1) + "" +
                    ((requestingConn.getState()==0) ? 0 : 1)
    )

    {
      case "00":
        Room emptyRoom = listRoom.stream()
                .filter(room -> room.isEmpty().equals(true))
                .findAny()
                .orElse(null);

        if (emptyRoom == null)
        {
          if(VERBOSE){System.out.println("/add/" + acceptingUser + ": failed. No empty rooms");}
          return;
        }

        requestingConn.setState(2);
        requestingConn.setRoom(emptyRoom);
        acceptingConn.setState(1);
        acceptingConn.setRoom(emptyRoom);

        emptyRoom.addActive(requestingConn);
        emptyRoom.addPending(acceptingConn);
        if(VERBOSE){System.out.println("/add/" + acceptingUser + ": success");}
        break; // both idle
      case "10":
        acceptingConn.setState(1);
        requestingConn.getRoom().addPending(acceptingConn);
        if(VERBOSE){System.out.println("/add/" + acceptingUser + ": success");}
        break; // req. to add to room
      case "01":
      case "11":
        if(VERBOSE){System.out.println("/add/" + acceptingUser + ": user bsy");}
        break; // user busy
    }
  }
}



