package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDatePicker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import model.Reservation;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Created by IntelliJ IDEA.
 *
 * This class is the controller for the check in window. All functions
 * in the check in will be handled here
 *
 * @Author: Zacky Kharboutli
 * @Date: 2018-03-29
 * @Project : HotelSystem
 */
public class checkInController implements Initializable {
    @FXML
    JFXDatePicker date;
    @FXML
    JFXButton prevBut , nextBut;
    @FXML
    private TableView table;
    @FXML
    private TableColumn<Reservation, Integer> arrivalCol,departCol,priceCol,guestCol,roomCol;
    @FXML
    private Text selectionError, checkedInText;
    @FXML
    private JFXButton checkOutButton ,reserveButton, guestButton , logOutBtn;
    private MenuController mu;
    private ObservableList<Reservation> reservations;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        mu = new MenuController();
        date.setValue(LocalDate.now());
        getReservation();

        checkOutButton.setOnAction(event -> {
            try {
                mu.showCheckOutWindow(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        reserveButton.setOnAction(event -> {
            try {
                mu.ShowMainPage(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        logOutBtn.setOnAction(event -> {
            try {
                mu.showLogInWindow(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        guestButton.setOnAction(event -> {
            try {
                mu.showGuestManagement(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void getReservation(){
       DBParser dbParser =new DBParser();
        table.getItems().remove(0,table.getItems().size());
        ObservableList<Reservation> listOfReservations = FXCollections.observableArrayList(dbParser.getReservationByDate(date.getValue()));

        table.setItems(listOfReservations);
        arrivalCol.setCellValueFactory(new PropertyValueFactory<Reservation,Integer>("arrivalDate"));
        departCol.setCellValueFactory(new PropertyValueFactory<Reservation,Integer>("departureDate"));
        priceCol.setCellValueFactory(new PropertyValueFactory<Reservation,Integer>("price"));
        guestCol.setCellValueFactory(new PropertyValueFactory<Reservation,Integer>("guest"));
        roomCol.setCellValueFactory(new PropertyValueFactory<Reservation,Integer>("room"));
    }

    public void nextDay(ActionEvent e){
        date.setValue(date.getValue().plusDays(1));
        getReservation();
    }
    public void prevDay(ActionEvent e){
        date.setValue(date.getValue().minusDays(1));
        getReservation();
    }
    
    public void checkIn() {
		DBParser dbParser = new DBParser();
		reservations = table.getSelectionModel().getSelectedItems();
		checkedInText.setVisible(false);
		
		if(reservations.size() == 0) {
			selectionError.setVisible(true);
		} else {
			selectionError.setVisible(false);
			checkedInText.setVisible(true);
			
			for(int i = 0; i < reservations.size(); i++) {	
				reservations.get(i).setCheckedIn(true);
				dbParser.refreshReservationStatus(reservations.get(i));
			}
		}
		getReservation();
	}
}
