package weather;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

//	매일 받아오는 날씨를 VBox 형태로 정의 
public class DailyWeather extends VBox{
	
	Label lbDailyDate;
	Label lbDailyWeather;
	Label lbDailyDetail;
	Label lbDailyLowHigh;
	
	//	생성자에서 값들을 받아옴
	public DailyWeather( String time, String icon, float id, String desc, float tempMin, float tempMax) {
		
		//	각 값들을 담기 위한 Label
		lbDailyDate = new Label();
		lbDailyWeather = new Label();
		lbDailyDetail = new Label();
		lbDailyLowHigh = new Label();
	
		//	각 라벨들의 크기 명시
		lbDailyDate.setPrefSize(70.0, 40.0);
		lbDailyWeather.setPrefSize(70.0, 70.0);
		lbDailyDetail.setPrefSize(70.0, 40.0);
		lbDailyLowHigh.setPrefSize(70.0, 40.0);
		
		//	각 라벨에 넘겨받은 인수들을 넣어줌
		lbDailyDate.setText( time );
		lbDailyWeather.setStyle("-fx-background-image: url('//res/weather/"+((int) id)+icon+".png');");
		lbDailyDetail.setText( desc );
		lbDailyLowHigh.setText( Math.round(tempMin)+"˚/"+Math.round(tempMax)+"˚");
		
		//	라벨 내의 텍스트 가운데 정렬
		lbDailyDate.setAlignment(Pos.CENTER);
		lbDailyWeather.setAlignment(Pos.CENTER);
		lbDailyDetail.setAlignment(Pos.CENTER);
		lbDailyLowHigh.setAlignment(Pos.CENTER);
		
		//	메인의 자식요소에 각 라벨들을 붙임
		getChildren().add(lbDailyDate);
		getChildren().add(lbDailyWeather);
		getChildren().add(lbDailyDetail);
		getChildren().add(lbDailyLowHigh);
		
		setPrefSize(70.0, 200.0);
	
	}

}
