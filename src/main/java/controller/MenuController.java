package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * Controller for the menu in all the pages to navigate between all the pages
 *
 * @Author: Zacky Kharboutli
 * @Date: 2018-05-02
 * @Project : HotelSystem
 */
public class MenuController {

	/**
	 * This method runs when ever the user press on log out option in the header
	 *
	 * @param event log out button pressed
	 * @throws IOException
	 */
	public void showLogInWindow(ActionEvent event) throws IOException {
		Parent LogInWindow = FXMLLoader.load(getClass().getResource(String.valueOf("/Untitled.fxml")));
		Scene mainWindowScene = new Scene(LogInWindow);
		Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		app_stage.setScene(mainWindowScene);
		app_stage.show();
	}

	/**
	 * This method runs when ever the user press on the check in option in the header
	 *
	 * @param event check in page requested
	 * @throws IOException
	 */
	public void showCheckInPage(ActionEvent event) throws IOException {
		Parent CheckInPage = FXMLLoader.load(getClass().getResource(String.valueOf("/CheckInPage.fxml")));
		Scene CheckInScene = new Scene(CheckInPage);
		Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		app_stage.setScene(CheckInScene);
		app_stage.show();
	}

	/**
	 * This method runs when ever the user press on the check out option in the header
	 *
	 * @param event check out page requested
	 * @throws IOException
	 */
	public void showCheckOutWindow(ActionEvent event) throws IOException {
		Parent CheckInPage = FXMLLoader.load(getClass().getResource(String.valueOf("/CheckOutPage.fxml")));
		Scene mainWindowScene = new Scene(CheckInPage);
		Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		app_stage.setScene(mainWindowScene);
		app_stage.show();
	}

	/**
	 * This method runs when ever the user press on the Guest management option in the header
	 *
	 * @param event Guest management page requested
	 * @throws IOException
	 */
	public void showGuestManagement(ActionEvent event) throws IOException {
		Parent checkInPage = FXMLLoader.load(getClass().getResource(String.valueOf("/GuestManagerPage.fxml")));
		Scene CheckInScene = new Scene(checkInPage);
		Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		app_stage.setScene(CheckInScene);
		app_stage.show();
	}


	/**
	 * This method runs when ever the user press on the Guest management option in the header
	 *
	 * @param event Main window page requested
	 * @throws IOException
	 */
	public void showMainPage(ActionEvent event) throws IOException {
		Parent mainWindow = FXMLLoader.load(getClass().getResource(String.valueOf("/MainWindow.fxml")));
		Scene mainWindowScene = new Scene(mainWindow);
		Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		app_stage.setScene(mainWindowScene);
		app_stage.show();
	}
}
