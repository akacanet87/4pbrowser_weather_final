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
		boarderPane.setPrefWidth(950.0);		//	어플리케이션 가동시 넓이
		boarderPane.setPrefHeight(550.0);		//	어플리케이션 가동시 높이
		Scene scene = new Scene(boarderPane);	//	자바fx는 scene에다 layout을 설정
		//	자바fx에서는 scene에 적용할 css를 이런 식으로 불러온다.
		scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		//	자바fx에서는 어플리케이션 가동 시 인수로 넘겨받는 stage에 scene을 설정해줘야 한다.
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
