package weather;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

//	���� �޾ƿ��� ������ VBox ���·� ���� 
public class DailyWeather extends VBox{
	
	Label lbDailyDate;
	Label lbDailyWeather;
	Label lbDailyDetail;
	Label lbDailyLowHigh;
	
	//	�����ڿ��� ������ �޾ƿ�
	public DailyWeather( String time, String icon, float id, String desc, float tempMin, float tempMax) {
		
		//	�� ������ ��� ���� Label
		lbDailyDate = new Label();
		lbDailyWeather = new Label();
		lbDailyDetail = new Label();
		lbDailyLowHigh = new Label();
	
		//	�� �󺧵��� ũ�� ���
		lbDailyDate.setPrefSize(70.0, 40.0);
		lbDailyWeather.setPrefSize(70.0, 70.0);
		lbDailyDetail.setPrefSize(70.0, 40.0);
		lbDailyLowHigh.setPrefSize(70.0, 40.0);
		
		//	�� �󺧿� �Ѱܹ��� �μ����� �־���
		lbDailyDate.setText( time );
		lbDailyWeather.setStyle("-fx-background-image: url('//res/weather/"+((int) id)+icon+".png');");
		lbDailyDetail.setText( desc );
		lbDailyLowHigh.setText( Math.round(tempMin)+"��/"+Math.round(tempMax)+"��");
		
		//	�� ���� �ؽ�Ʈ ��� ����
		lbDailyDate.setAlignment(Pos.CENTER);
		lbDailyWeather.setAlignment(Pos.CENTER);
		lbDailyDetail.setAlignment(Pos.CENTER);
		lbDailyLowHigh.setAlignment(Pos.CENTER);
		
		//	������ �ڽĿ�ҿ� �� �󺧵��� ����
		getChildren().add(lbDailyDate);
		getChildren().add(lbDailyWeather);
		getChildren().add(lbDailyDetail);
		getChildren().add(lbDailyLowHigh);
		
		setPrefSize(70.0, 200.0);
	
	}

}
