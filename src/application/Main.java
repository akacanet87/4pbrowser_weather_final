package application;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import weather.Weather;

public class Main extends Application {
	
	BorderPane boarderPane = new BorderPane();
	
	@Override
	public void start(Stage primaryStage) {
		
		Weather root = new Weather();
		boarderPane.setCenter(root);
		boarderPane.setPrefWidth(950.0);		//	���ø����̼� ������ ����
		boarderPane.setPrefHeight(550.0);		//	���ø����̼� ������ ����
		Scene scene = new Scene(boarderPane);	//	�ڹ�fx�� scene���� layout�� ����
		//	�ڹ�fx������ scene�� ������ css�� �̷� ������ �ҷ��´�.
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		//	�ڹ�fx������ ���ø����̼� ���� �� �μ��� �Ѱܹ޴� stage�� scene�� ��������� �Ѵ�.
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
