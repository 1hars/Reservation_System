package Hotel_Reservation_System;
import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {
    private static final String password = "root";
    private static final String user_Name = "root";
    private static final String url = "jdbc:mysql://localhost:3306/hotel_db";

    public static void main(String[] args){
        Scanner scn = new Scanner(System.in);
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch(ClassNotFoundException ee){
            System.out.println(ee.getMessage());
        }
        try{
            Connection connection = DriverManager.getConnection(url,user_Name,password);
            while(true){
                System.out.println();
                System.out.println("HOTEL MANAGEMENT SYSTEM");
                System.out.println("1. Reserve a room");
                System.out.println("2. View Reservation");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");
                System.out.println("Choose an option: ");
                int choice = scn.nextInt();
                switch (choice){
                    case 1:
                        reserveRoom(connection, scn);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection, scn);
                        break;
                    case 4:
                        updateReservation(connection,scn);
                        break;
                    case 5:
                        deleteReservation(connection,scn);
                        break;
                    case 0:
                        exit(); //Inter
                        scn.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }
        }
        catch(SQLException | InterruptedException ee){
            System.out.println(ee.getMessage());
        }
    }
    public static void reserveRoom(Connection connection, Scanner scn){
        String query = "Insert into reservations(guest_name, room_number,contact_number) Values (?,?,?)";
        try{
            System.out.println("Enter guest Name: ");
            String guestname = scn.next();
            scn.nextLine();
            System.out.println("Enter room number: ");
            int roomNumber = scn.nextInt();
            System.out.println("Enter contact number: ");
            String contactNumber = scn.next();
             try(PreparedStatement statement = connection.prepareStatement(query)){
                 statement.setString(1,guestname);
                 statement.setInt(2,roomNumber);
                 statement.setString(3,contactNumber);
                 int effectrow = statement.executeUpdate();
                 if(effectrow > 0){
                     System.out.println("Reservation Successfully");
                 }else{
                     System.out.println("Reservation failed");
                 }
             }
        }
        catch (Exception ee){
            System.out.println();
        }
    }
    public static void viewReservation(Connection connection){
        String sql = "Select * from reservations";
        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){
            System.out.println("Current reservation:");
            System.out.println("+----------------+----------------+--------------+-------------------+----------------------------+");
            System.out.println("| Reservation ID | Guest          | Room Number  | Contact Number    |    Reservation Date        |");
            System.out.println("+----------------+----------------+--------------+-------------------+-----------------------------+");
            while(resultSet.next()){
                int id = resultSet.getInt("reservation_id");
                String name = resultSet.getString("guest_name");
                int roomNumber = resultSet.getInt("room_number");
                String contact = resultSet.getString("contact_number");
                String date = resultSet.getTimestamp("reservation_date").toString();
                System.out.printf("| %-14d | %-15s | %-13d | %-20s | %-19s  |\n",id,name,roomNumber,contact,date);
            }
            System.out.println("+----------------+----------------+--------------+-------------------+------------------------------+");
        }
        catch (SQLException ee){
            System.out.println(ee.getMessage());
        }

    }
    public static void getRoomNumber(Connection connection, Scanner scn){
        try {
            System.out.print("Enter reservation ID: ");
            int reservationId = scn.nextInt();
            System.out.print("Enter guest name: ");
            String guestName = scn.next();

            String sql = "SELECT room_number FROM reservations " +
                    "WHERE reservation_id = " + reservationId +
                    " AND guest_name = '" + guestName + "'";

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                if (resultSet.next()) {
                    int roomNumber = resultSet.getInt("room_number");
                    System.out.println("Room number for Reservation ID " + reservationId +
                            " and Guest " + guestName + " is: " + roomNumber);
                } else {
                    System.out.println("Reservation not found for the given ID and guest name.");
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public static void updateReservation(Connection connection, Scanner scn){
        try{
            System.out.println("Enter reservation id to update: ");
            int id = scn.nextInt();
            scn.nextLine();
            if(!reservationexist(connection, id)){
                System.out.println("reservation does't exist");
                return;
            }
            System.out.println("Enter new guest name: ");
            String name = scn.nextLine();
            System.out.println("Enter new room number: ");
            int newRoomNumber = scn.nextInt();
            System.out.println("Enter new contact number: ");
            String newContactNumber = scn.next();
            String sql = "Update reservations SET guest_name = '"+name+ "', "+
                    "room_number =  "+newRoomNumber + "," +
                    "contact_number = ' "+ newContactNumber + "'"+
                    "Where reservation_id = "+id;
            try(Statement statement = connection.createStatement()){
                int roweffect = statement.executeUpdate(sql);
                if(roweffect > 0){
                    System.out.println("Updated reservation");
                }else{
                    System.out.println("Reservation Updated failed");
                }
            }
        }
        catch(SQLException ee){
            System.out.println(ee.getMessage());

        }

    }
    public static void deleteReservation(Connection connection, Scanner scn){
        try{
            System.out.println("Enter reservation ID: ");
            int id = scn.nextInt();
            if(!reservationexist(connection, id)){
                System.out.println("reservation does't exist");
                return;
            }

            String sql = "DELETE From reservations where reservation_id = "+id;
            try(Statement statement = connection.createStatement()){
                int roweffect = statement.executeUpdate(sql);
                if(roweffect > 0){
                    System.out.println("Reservation delete successfully !");
                }else{
                    System.out.println("Reservation Delete failed");
                }
            }
        }
        catch (SQLException ee){
            System.out.println(ee.getMessage());
        }

    }
    public static boolean reservationexist(Connection connection,int reservationId){
        try{
            String sql = "Select reservation_id from reservations  where reservation_id = "+ reservationId;
            try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){
                return resultSet.next();
            }
        }
        catch (SQLException ee){
            System.out.println(ee.getMessage());
            return false;
        }
    }
    public static void exit() throws InterruptedException{
        System.out.print("Exiting System");
        int i = 5;
        while(i != 0){
            System.out.print(".");
            Thread.sleep(450);
            i--;
        }
        System.out.println();
        System.out.println("Thanku for using Hotel Reservation System!!!");
    }

}
