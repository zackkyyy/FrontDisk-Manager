package controller;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import javafx.collections.ObservableList;
import model.Guest;
import model.Reservation;
import model.Room;
import model.User;
import model.enums.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;

import static com.mongodb.client.model.Filters.eq;

/**
 * Created by IntelliJ IDEA.
 *
 * @Author: Zacky Kharboutli
 * @Date: 2018-04-18
 * @Project : HotelSystem
 */
public class DBParser {


    //String uri = "mongodb://localhost:27017,localhost:27017/replicaSet=hotelSystem";

    /**
     * Use this Link to change to cloud Data base
     */
     String uri ="mongodb+srv://zacky:group15@hotelmanagerdb-nxz5u.mongodb.net/test";
    MongoClientURI clientURI = new MongoClientURI(uri);
    //  ******uncomment the next line and comment the previous if you want a cloud database******
    MongoClient mongoClient = new MongoClient(clientURI);
    private Document doc;
    private MongoDatabase db = mongoClient.getDatabase("hotelSystem");
    private MongoCursor<Document> cursor;
    private MongoCollection persons = db.getCollection("persons");
    private MongoCollection rooms = db.getCollection("rooms");
    private MongoCollection users = db.getCollection("users");
    private MongoCollection reservations = db.getCollection("reservations");


              /**********************************************/
             /**             Guest Part                   **/
            /**********************************************/


    /**
     * This method compare the password entered by user with the hashed password in the system
     *
     * @param password_String String password entered by user
     * @param hashed_pass     Hashed password stored in system
     * @return
     */
    public static boolean checkPassword(String password_String, String hashed_pass) {
        boolean passwordMatch = false;

        if (!hashed_pass.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("Password is saved in invalid hash form");

        passwordMatch = BCrypt.checkpw(password_String, hashed_pass);
        System.out.println(hashed_pass);
        return (passwordMatch);
    }

    /**
     * This method save the new created guest in the database
     *
     * @param newGuest
     */

    public void createNewGuest(Guest newGuest) {
        try {
            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(LocalDate.now().toString());

            doc = new Document("name", newGuest.getName())
                    .append("last name", newGuest.getLastName())
                    .append("phone nr", newGuest.getPhoneNr())
                    .append("address", newGuest.getAddress())
                    .append("credit card", newGuest.getCreditCard())
                    .append("identity nr", newGuest.getIdentityNr())
                    .append("birthday", date)
                    .append("notes", "");

            persons.insertOne(doc);
            newGuest.setId("" + doc.getObjectId("_id"));

            //  final Node source = (Node) e.getSource();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            //          display the error message
            System.out.println("Failed to save");
        }
        System.out.println("Guest with id " + newGuest.getId() + " is now created");
    }

    /**
     *  This method save the changes made on guest in the database
     *
     * @param guest
     * @throws ParseException
     */
    public void editGuest(Guest guest) throws ParseException {
        ObjectId objectId = new ObjectId(guest.getId());
        java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(guest.getBirthday().toString());
        persons.updateOne(eq("_id", objectId), new Document("$set",
                new Document("last name", guest.getLastName())
                        .append("name", guest.getName())
                        .append("phone nr", guest.getPhoneNr())
                        .append("address", guest.getAddress())
                        .append("credit card", guest.getCreditCard())
                        .append("notes", guest.getNotes())
                        .append("identity nr", guest.getIdentityNr())
                        .append("birthday", date)));
    }

    public ArrayList<Guest> getGuestsInArray() {
        cursor = persons.find().iterator();
        ArrayList<Guest> listOfGuest = new ArrayList<Guest>();

        for (int i = 0; i < persons.count(); i++) {
            doc = cursor.next();

            String name = doc.getString("name");
            String lastName = doc.getString("last name");
            String address = doc.getString("address");
            String phoneNr = doc.getString("phone nr");
            String creditCard = doc.getString("credit card");
            String identity_nr = doc.getString("identity nr");
            Guest gst = new Guest(name, lastName, address, phoneNr, creditCard, identity_nr);
            listOfGuest.add(gst);

        }
        return listOfGuest;
    }

    /**
     * This methods returns a guest by looking in all the guest names
     *
     * @param guestName
     * @return
     */
    public Guest getGuestByName(String guestName) {
        cursor = persons.find().iterator();
        Guest guest = null;
        for (int i = 0; i < persons.count(); i++) {
            doc = cursor.next();
            if ((doc.getString("name") + " " + doc.getString("last name")).equals(guestName)
                    && guestName.length() == doc.getString("name").length() + doc.getString("last name").length() + 1) {
                String id = String.valueOf(doc.getObjectId("_id"));
                String name = doc.getString("name");
                String lastName = doc.getString("last name");
                String address = doc.getString("address");
                String phoneNr = doc.getString("phone nr");
                String creditCard = doc.getString("credit card");
                String identity_nr = doc.getString("identity nr");
                String notes = doc.getString("notes");
                LocalDate birthday = doc.getDate("birthday").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                guest = new Guest(id, name, lastName, address, phoneNr, identity_nr, creditCard, birthday, notes);
            }
        }
        return guest;
    }

      /**********************************************/
     /**                Room Section              **/
    /**********************************************/

    public void deleteGuest(Guest newGuest) throws ParseException {
        java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(newGuest.getBirthday().toString());

        Document doc = new Document("name", newGuest.getName())
                .append("last name", newGuest.getLastName())
                .append("phone nr", newGuest.getPhoneNr())
                .append("address", newGuest.getAddress())
                .append("credit card", newGuest.getCreditCard())
                .append("identity nr", newGuest.getIdentityNr())
                .append("birthday", date)
                .append("notes", newGuest.getNotes());
        persons.deleteOne(doc);

    }


    public void deleteRoom(Room temporaryRoom) {
        Document doc = new Document("room nr", temporaryRoom.getRoomNr())
                .append("room type", temporaryRoom.getRoomType().toString())
                .append("price", temporaryRoom.getPrice())
                .append("city", temporaryRoom.getCity().toString())
                .append("smoking", temporaryRoom.getSmoking().toString())
                .append("adjacent", temporaryRoom.getAdjoined().toString())
                .append("quality", temporaryRoom.getQuality().toString());
        rooms.deleteOne(doc);
    }

    /**
     * This method is called when the user try to look for room in one of the cities (Växjö/Kalmar)
     * The user choose the city from the UI using the menu button
     *
     * @param string The chosen city that is used to filter the rooms
     * @param list   list of rooms from the list view in the UI
     * @return a new list of room after filtering the old list
     */
    public ArrayList<Room> filterByCity(String string, ObservableList<Room> list) {
        ArrayList<Room> listOfRooms = new ArrayList<Room>();

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getCity().toString().equals(string)) {
                listOfRooms.add(list.get(i));
            }
        }
        return listOfRooms;
    }

    /**
     * This method is called when the user try to look for specific type of
     * room from the UI.
     *
     * @param string The room type that is used to filter the rooms
     * @param list   list of rooms from the list view in the UI
     * @return a new list of room after filtering the old list
     */
    public ArrayList<Room> filterByRoomType(String string, ObservableList<Room> list) {
        ArrayList<Room> listOfRooms = new ArrayList<Room>();

        for (int i = 0; i < list.size(); i++) {
            if (string.equals("1")) {
                if (list.get(i).getRoomType().toString().equals("Single")) {
                    listOfRooms.add(list.get(i));
                }
            } else if (string.equals("2")) {
                if (list.get(i).getRoomType().toString().equals("Double")) {
                    listOfRooms.add(list.get(i));
                }
            } else if (string.equals("3")) {
                if (list.get(i).getRoomType().toString().equals("Triple")) {
                    listOfRooms.add(list.get(i));
                }
            } else if (string.equals("4")) {
                if (list.get(i).getRoomType().toString().equals("Apartment")) {
                    listOfRooms.add(list.get(i));
                }
            }
        }
        return listOfRooms;
    }

    /**
     * This method generate an array list of rooms from the database
     *
     * @return list of all the room in array list
     */

    public ArrayList<Room> getAllRoom() {
        cursor = rooms.find().iterator();
        ArrayList<Room> listOfRooms = new ArrayList<Room>();
        for (int i = 0; i < rooms.count(); i++) {
            doc = cursor.next();
            RoomType roomType = RoomType.toEnum(doc.getString("room type"));
            int roomNr = doc.getInteger("room nr");
            Double price = doc.getDouble("price");
            City city = City.toEnum(doc.getString("city"));
            Smoking smoking = Smoking.toEnum(doc.getString("smoking"));
            Adjacent adjacent = Adjacent.toEnum(doc.getString("adjacent"));
            Quality quality = Quality.toEnum(doc.getString("quality"));
            Room room = new Room();
            room.setRoomNr(roomNr);
            room.setRoomType(roomType);
            room.setPrice(price);
            room.setCity(city);
            room.setAdjoined(adjacent);
            room.setSmoking(smoking);
            room.setQuality(quality);
            listOfRooms.add(room);

        }
        return listOfRooms;
    }

    public void refreshRoomStatus(Room room) {
        ObjectId objectId = null;
        cursor = rooms.find().iterator();
        for (int i = 0; i < rooms.count(); i++) {
            doc = cursor.next();
            if ((doc.getInteger("room nr") == room.getRoomNr() && doc.getString("city").equals(room.getCity().toString()))) {
                objectId = doc.getObjectId("_id");
            }
        }
        rooms.updateOne(eq("_id", objectId), new Document("$set",
                new Document("room nr", room.getRoomNr())
                        .append("room type", room.getRoomType().toString())
                        .append("price", room.getPrice())
                        .append("city", room.getCity().toString())));
    }

    public void createNewRoom(Room room) {
        doc = new Document("room nr", room.getRoomNr())
                .append("room type", room.getRoomType().toString())
                .append("price", room.getPrice())
                .append("city", room.getCity().toString())
                .append("smoking", room.getSmoking().toString())
                .append("adjacent", room.getAdjoined().toString())
                .append("quality", room.getQuality().toString());

        rooms.insertOne(doc);
    }



    public Room copyRoomByRoomNumber(int roomNumber) {
        ArrayList<Room> listOfRoom = getAllRoom();
        Room room = null;
        for (int i = 0; i < listOfRoom.size(); i++) {
            if (listOfRoom.get(i).getRoomNr() == roomNumber) {
                room = listOfRoom.get(i);
            }
        }
        return room;
    }
    public ArrayList<Room> copyRoomByRoomNumber(ArrayList<Integer> arrayList) {
        ArrayList<Room> listOfRoom = getAllRoom();
        ArrayList<Room> rooms=new ArrayList<Room>();
        for (int i = 0; i < listOfRoom.size(); i++) {
            for (int j = 0 ; j<arrayList.size() ; j++){
                if (listOfRoom.get(i).getRoomNr()==arrayList.get(j)){

                    rooms.add(listOfRoom.get(i));
                }
            }
        }
        return rooms;
    }

           /**********************************************/
          /**                 User Part                **/
         /**********************************************/

    /**
     * This mehtod return all the names of the users in the database
     *
     * @return
     */
    public Object[] getUserNames() {
        String[] listOfGuest = new String[(int) users.count()];

        MongoCursor<Document> cursor = users.find().iterator();
        for (int i = 0; i < users.count(); i++) {
            Document doc = cursor.next();
            listOfGuest[i] = (doc.getString("name") + " " + doc.getString("last name"));
        }
        return listOfGuest;
    }

    /**
     * This method returns the requested user by checking in the database
     *  if there is a user with the given string
     *
     * @param guestName
     *          String represent the user name to be used to find the user
     * @return
     *      User if found
     */
    public User getUserByName(String guestName) {
        cursor = users.find().iterator();
        User user = null;
        for (int i = 0; i < users.count(); i++) {
            doc = cursor.next();
            if ((doc.getString("name") + " " + doc.getString("last name")).equals(guestName)
                    && guestName.length() == doc.getString("name").length() + doc.getString("last name").length() + 1) {
                String name = doc.getString("name");
                String lastName = doc.getString("last name");
                String password = doc.getString("password");
                String username = doc.getString("username");

                user = new User(name, lastName, username, password);
            }else{
                System.out.println("no user with this name");
            }
        }
        return user;
    }

    /**
     * This method deletes the user document from the database
     *
     * @param user
     */
    public void deleteUser(User user) {
        Document doc = new Document("name", user.getName())
                .append("last name", user.getLastName())
                .append("password", user.getPassword())
                .append("username", user.getUserName());
        users.deleteOne(doc);
    }

    /**
     * This method records all the changes has been made on a user
     *    and update the corresponding user in the database
     *
     * @param selectedItem
     * @param temporaryUser
     */
    public void editUser(Object selectedItem, User temporaryUser) {
        cursor = users.find().iterator();
        ObjectId objectId = null;
        for (int i = 0; i < users.count(); i++) {
            doc = cursor.next();
            if (selectedItem.toString().contains(doc.getString("name")) && selectedItem.toString().contains(doc.getString("last name"))) {
                objectId = doc.getObjectId("_id");
            }
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String hashedPassword = passwordEncoder.encode(temporaryUser.getPassword());
        users.updateOne(eq("_id", objectId), new Document("$set",
                new Document("name", temporaryUser.getName())
                        .append("last name", temporaryUser.getLastName())
                        .append("username", temporaryUser.getUserName())
                        .append("password", hashedPassword)));
    }

    /**
     * This method create a new document for the new created user
     *  It saves the document in the guest table in the database
     *
     * @param newUser
     */
    public void createNewUser(User newUser) {
        doc = new Document();
        try {
            doc = new Document("name", newUser.getName())
                    .append("last name", newUser.getLastName())
                    .append("password", newUser.getPassword())
                    .append("username", newUser.getUserName());

            users.insertOne(doc);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            System.out.println("Failed to save");
        }
    }

    /**
     * This method is a validator that checks if the entered password and user name
     * matches any of the registered users in the DB
     *
     * @param username String that user enter to log in
     * @param password Password as String that user used to log in
     * @return True if there is a match, otherwise false
     */
    public Boolean validateLogging(String username, String password) {
        cursor = users.find().iterator();

        for (int i = 0; i < users.count(); i++) {
            doc = cursor.next();
            if (username.equals(doc.getString("username"))) {
                if (checkPassword(password, doc.getString("password"))) {
                    return true;
                }
            }
        }
        return false;
    }
      /**********************************************/
     /**          Reservation Section             **/
    /**********************************************/



    public void deleteReservationByRoomNumber(ObjectId id) {
        reservations.deleteOne(eq("_id", id));
    }

    /**
     * This method is used to get all the reservation
     *  from the database.
     *
     * @return
     *      return array of all the reservation
     */
    public ArrayList<Reservation> getAllReservations() {
        try {
            cursor = reservations.find().iterator();
        } catch (Exception e) {
            System.err.println(e);
        }
        ArrayList<Reservation> listOfReservation = new ArrayList<Reservation>();
        Reservation reservation;

        for (int i = 0; i < reservations.count(); i++) {
            doc = cursor.next();
            reservation = getReservationFromDB(doc);
            listOfReservation.add(reservation);
        }
        return listOfReservation;
    }

    /**
     * This method save a new created reservation to the database
     *
     * @param reservation
     *              The new created reservation
     */
    public void saveReservationToDB(Reservation reservation) {
        Date ArrivalDate = Date.from(reservation.getArrivalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date departureDate = Date.from(reservation.getDepartureDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

        doc = new Document("guest", reservation.getGuest())
                .append("arrival", ArrivalDate)
                .append("departure", departureDate)
                .append("price", reservation.getPrice())
                .append("is checkedIn", reservation.getCheckedIn())
                .append("rooms",reservation.getRooms());

        reservations.insertOne(doc);
    }
    /**
     * Deletes reservations that has departure date before
     * today's date and unbooks the corresponding rooms
     *
     */
    //
    public void deleteOldReservations() {
        Instant instant = Instant.now().minus(1, ChronoUnit.DAYS);
        Date time = Date.from(instant);
        Bson filter = Filters.lt("departure", time);


        MongoCursor<Document> reservationCursor = reservations.find().iterator();

        // Unbooks the rooms
        for (int i = 0; i < reservations.count(); i++) {
            doc = reservationCursor.next();

            if (doc.getDate("departure").before(time)) {
                ArrayList<Integer> rooms = (ArrayList<Integer>)doc.get("rooms");

              ArrayList  <Room> tempRoom = copyRoomByRoomNumber(rooms);
              for(int j = 0 ;j<tempRoom.size();j++){
                refreshRoomStatus(tempRoom.get(j));
              }
            }
        }

        // Deletes the reservations
        reservations.deleteMany(filter);
    }

    /**
     * Method that copy the information of a reservation from a document in the
     * dataBase and creates a reservation out of it
     *
     * @param doc The document that is used to create the reservation
     * @return Reservation
     */
    private Reservation getReservationFromDB(Document doc) {
        LocalDate arrivalDate = doc.getDate("arrival").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate departureDate = doc.getDate("departure").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Double price = doc.getDouble("price");
        String guestID = doc.getString("guest");
        Boolean checkedIn = doc.getBoolean("is checkedIn");
        Reservation reservation = new Reservation();
        reservation.setDepartureDate(departureDate);
        reservation.setGuest(guestID);
        reservation.setArrivalDate(arrivalDate);
        reservation.setPrice(price);
        reservation.setCheckedIn(checkedIn);
        ObjectId id = doc.getObjectId("_id");
        reservation.setId(id);
        ArrayList<Integer> rooms = (ArrayList<Integer>)doc.get("rooms");
        reservation.setRooms(rooms);
        return reservation;

    }
    /**
     * This method update an existing reservation when the user check
     *  in or check out a reservation
     *
     * @param reservation
     *              The reservation that needed to be updated
     * @return Reservation
     *              The updated version
     */
    public void refreshReservationStatus(Reservation reservation) {

        Date arrivalDate = Date.from(reservation.getArrivalDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date departureDate = Date.from(reservation.getDepartureDate().atStartOfDay(ZoneId.systemDefault()).toInstant());

        System.out.println(reservation.getCheckedIn());

        cursor = reservations.find().iterator();
        ObjectId objectId = null;
        for (int i = 0; i < reservations.count(); i++) {
            doc = cursor.next();
            if (reservation.getId().equals(doc.getObjectId("_id"))) {
                objectId = doc.getObjectId("_id");
                System.out.println(objectId.toString() +" the object id");
            }
        }
        reservations.updateOne(eq("_id", objectId), new Document("$set",
                new Document("guest",reservation.getGuest())
                        .append("arrival",arrivalDate)
                        .append("departure",departureDate)
                        .append("price",reservation.getPrice())
                        .append("is checkedIn",reservation.getCheckedIn())
                        .append("rooms",reservation.getRooms())));



    }
}
