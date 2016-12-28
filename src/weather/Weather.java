/*
 * GridPane span 방법 예시
 * 
 * grid.setRowIndex(lblDesc, 3); grid.setColumnIndex(lblDesc, 2);
 * grid.setRowIndex(tfDesc, 4); grid.setColumnIndex(tfDesc,2);
 * grid.getChildren().addAll(lblDesc, tfDesc);
 * 
 * 
 * 전체 HBox
 * 
 * 
 * 설정한 도시의 위도와 경도를 받아오는 lib 필요
 * 
 * http://fronteer.kr/bbs/view/68 // openweather api 활용 방법
 * 
 * http://seongilman.tistory.com/137 // 도시 입력하여 위도 경도 받아오기
 * 
 *	java로 짠 코드들을 javafx의 형태로 전환 시킴
 * 
 */
package weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

public class Weather extends HBox implements Initializable{
	
	@FXML HBox root;
	
	@FXML ComboBox<String> comboSido;
	@FXML ComboBox<String> comboGungu;
	@FXML ComboBox<String> comboDong;
	
	@FXML Button btnSearch;
	@FXML Button btnRefresh;
	
	@FXML Label lbPresentTime;
	@FXML Label lbPresentWeatherIcon;
	@FXML Label lbPresentTemp;
	
	@FXML Label lbPresentStatusWeatherDetail;
	@FXML Label lbPresentStatusAmountRain;
	@FXML Label lbPresentStatusHumidity;
	@FXML Label lbPresentStatusWind;
	@FXML Label lbPresentStatusClouds;
	
	@FXML HBox hboxDaily;
	
	@FXML HBox hboxHourlyTop;
	@FXML HBox hboxHourlyBottom;

	float lat = 0;
	float lon = 0;
	String location = null;
	String sido = null;
	String gungu = null;
	String dong = null;
	String sidoItem = "도/시";
	String gunguItem = "군/구";
	String dongItem = "동/읍/리";
	
	String filePath = "C:/java_workspace/4pWeatherFinal/zipdb.xlsx";
	String weatherkey = "8df055fe230d118698fd22b2ef6698e1";//"cf8ae2399ef38cef92f916134dd48bba";
	String locKey = "AIzaSyBFAup6WBP98Q190VGT2LQrXSEM6bbkP98";
	
	float kelvin =   -273.15f;

	private final String USER_AGENT = "Mozilla/5.0";	
	
	public Weather() {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("weather.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		getSido();
		comboSido.setOnAction(event->setGunguBox(event));
		comboGungu.setOnAction(event->setDongBox(event));
		comboDong.setOnAction(event->setBtnSearch(event));
		btnSearch.setOnMouseClicked(event->getSearchLoc(event));
		
	}
	
	private void getSearchLoc(MouseEvent event) {
		
		sido = comboSido.getValue();
		gungu = comboGungu.getValue();
		dong = comboDong.getValue();
		
		if(sido.equals("세종")){
		
			location = gungu+" "+dong;
			
		}else{
			
			location = sido+" "+gungu+" "+dong;
			
		}
		
		try {
			
			getLatLon(location);
			getPresentWeather(lat, lon);
			getDailyWeather(lat, lon);
			getHourlyWeather(lat, lon);
			
			
		} catch (Exception e1) {
			
			e1.printStackTrace();
			
		}

	}
	
	private void setGunguBox(ActionEvent event) {

		comboGungu.getItems().clear();
		comboGungu.setPromptText(gunguItem);
		comboDong.getItems().clear();
		comboDong.setPromptText(dongItem);
		getGungu(comboSido.getValue());
		comboGungu.setDisable(false);
		comboDong.setDisable(true);
		btnSearch.setDisable(true);
		
	}
	
	private void setDongBox(ActionEvent event) {
		
		comboDong.getItems().clear();
		comboDong.setPromptText(dongItem);
		getDong(comboGungu.getValue());
		comboGungu.setDisable(false);
		comboDong.setDisable(false);
		
	}
	
	private void setBtnSearch(ActionEvent event){
		
		btnSearch.setDisable(false);
		
	}
	
	public void getSido(){
		
		comboGungu.setDisable(true);
		comboDong.setDisable(true);
		btnSearch.setDisable(true);
		
		File file = new File( filePath );
		
		//	워크북의 역할 - 엑셀파일을 제어하기 위한 객체
		XSSFWorkbook workBook;
		
		try {
			
			workBook = new XSSFWorkbook( file );
			//	열린 파일에 대한 Sheet에 접근하기
			XSSFSheet sheet = workBook.getSheetAt(0);
			
			int total = sheet.getPhysicalNumberOfRows();
			
			//XSSFCell cell = sheet.getRow(1).getCell(1);
			
			for ( int a=1 ; a< total ; a++){
				
				String value = sheet.getRow(a).getCell(0).getStringCellValue();
				comboSido.getItems().add(value);
				
			}
			
		} catch (InvalidFormatException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
		
	}

	public void getGungu( String loc ){
		
		File file = new File( filePath );
		
		//	워크북의 역할 - 엑셀파일을 제어하기 위한 객체
		try {
			XSSFWorkbook workBook = new XSSFWorkbook( file );
			
			//	열린 파일에 대한 Sheet에 접근하기
			XSSFSheet sheet = workBook.getSheetAt(1);
			
			int total = sheet.getPhysicalNumberOfRows();
			
			//XSSFCell cell = sheet.getRow(1).getCell(1);
			
			for ( int a=1 ; a< total ; a++){
				
				if (sheet.getRow(a).getCell(0).getStringCellValue().equals(loc) ){
				
					String value = sheet.getRow(a).getCell(1).getStringCellValue();
					comboGungu.getItems().add(value);
					
				}
				
			}
			
		} catch (InvalidFormatException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void getDong( String loc ){
		
		File file = new File( filePath );
		
		//	워크북의 역할 - 엑셀파일을 제어하기 위한 객체
		try {
			XSSFWorkbook workBook = new XSSFWorkbook( file );
			
			//	열린 파일에 대한 Sheet에 접근하기
			XSSFSheet sheet = workBook.getSheetAt(2);
			
			int total = sheet.getPhysicalNumberOfRows();
			
			//XSSFCell cell = sheet.getRow(1).getCell(1);
			
			for ( int a=1 ; a< total ; a++){
				
				if (sheet.getRow(a).getCell(0).getStringCellValue().equals(loc) ){
				
					String value = sheet.getRow(a).getCell(1).getStringCellValue();
					comboDong.getItems().add(value);
					
				}
				
			}
			
		} catch (InvalidFormatException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	public void getLatLon(String loc) throws Exception {
		
		String newUrl = URLEncoder.encode( loc, "utf-8");

		String locationurl = "http://maps.googleapis.com/maps/api/geocode/json?address="+newUrl+"key="+locKey+"&language=ko";
		// String locationurl =
		// "https://apis.daum.net/local/geo/addr2coord?apikey=daa88b83639c1c74cd56e2f83b3d8e3d&q="+loc+"&output=json";
		//String newUrl = URLDecoder.decode( URLDecoder.decode(locationurl, "8859_1"), "utf-8");
		

		URL obj = new URL(locationurl); HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET 
		
		con.setRequestMethod("GET");

		//add request header 
		con.setRequestProperty("User-Agent",	USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " +
		locationurl); System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new
		InputStreamReader(con.getInputStream())); String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
		response.append(inputLine); } in.close();

		// print result 
		System.out.println(response.toString());

		JSONParser jsonParser = new JSONParser();

		//JSON데이터를 넣어 JSON Object 로 만들어 준다. 
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());

		//weather의 배열을 추출 
		JSONArray locationInfoArray = (JSONArray) jsonObject.get("results");

		for(int i=0; i<locationInfoArray.size(); ++i){

			JSONObject results = (JSONObject)locationInfoArray.get(i); 
			JSONObject location = (JSONObject)results.get("geometry"); 
			JSONObject latlng = (JSONObject)location.get("location");
			//System.out.println(latlng.get("lat")+", "+latlng.get("lng"));
	
			lat = Float.parseFloat( latlng.get("lat").toString()); 
			lon = Float.parseFloat(latlng.get("lng").toString()); 
		
		}

		System.out.println("위도 : "+lat+", 경도 : "+lon);

	}

	public void getPresentWeather(float lat, float lon) throws Exception {

		String weatherurl = "http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" + lon
				+ "&appid="+weatherkey;
		
		String strWeatherMain = null;
		String strWeatherDesc = null;
		String strWeatherIcon = null;
		float fPresentWeatherId = 0;
		float fTemp = 0;
		float fPressure = 0;
		float fHumidity = 0;
		float fSpeed = 0;
		float fClouds = 0;
		float fRain = 0;
		float fSnow = 0;

		URL obj = new URL(weatherurl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + weatherurl);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		//System.out.println(response.toString());

		JSONParser jsonParser = new JSONParser();

		// JSON데이터를 넣어 JSON Object 로 만들어 준다.
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());

		// weather의 배열을 추출
		JSONArray weatherInfoArray = (JSONArray) jsonObject.get("weather");

		for (int a = 0; a < weatherInfoArray.size(); a++) {

			JSONObject weatherObject = (JSONObject) weatherInfoArray.get(a);

			fPresentWeatherId = Float.parseFloat(weatherObject.get("id").toString());
			strWeatherMain = weatherObject.get("main").toString();
			strWeatherDesc = weatherObject.get("description").toString();
			strWeatherIcon = weatherObject.get("icon").toString();

		}
		
		JSONObject main = (JSONObject) jsonObject.get("main");		
		fTemp = Float.parseFloat(main.get("temp").toString()) + kelvin;
		fPressure = Float.parseFloat(main.get("pressure").toString());
		fHumidity = Float.parseFloat(main.get("humidity").toString());
		
		if( jsonObject.containsKey("clouds")==true ){
		
			JSONObject clouds = (JSONObject) jsonObject.get("clouds");		
			fClouds = Float.parseFloat(clouds.get("all").toString());
			
		}
		
		if( jsonObject.containsKey("wind")==true ){
		
			JSONObject wind = (JSONObject) jsonObject.get("wind");		
			fSpeed = Float.parseFloat( wind.get("speed").toString() );
			
		}
		
		if( jsonObject.containsKey("rain")==true ){
			
			JSONObject rain = (JSONObject) jsonObject.get("rain");	
			fRain = Float.parseFloat( rain.get("3h").toString() );
			
		}
		
		if( jsonObject.containsKey("snow")==true ){
		
			JSONObject snow = (JSONObject) jsonObject.get("snow");		
			fSnow = Float.parseFloat( snow.get("3h").toString() );
		
		}
		
		System.out.println("\n===============현재날씨===============\n");
			
		System.out.println("dt : "+(long) jsonObject.get("dt"));
		
		System.out.println(System.currentTimeMillis());

		System.out.println("현재 시간1 : "+getPresent( (long) jsonObject.get("dt")));

		System.out.println(Float.parseFloat(main.get("temp").toString()));
		System.out.println(kelvin);
		System.out.println("현재 온도 : "+fTemp);
		System.out.println("현재 기압 : "+fPressure);
		System.out.println("현재 습도 : "+fHumidity);
		System.out.println("현재 구름 : "+fClouds);
		System.out.println("현재 바람 : "+fSpeed);
		System.out.println("3h 강우량 : "+fRain);
		System.out.println("3h 강설량 : "+fSnow);
		
		System.out.println("날씨 ID : "+fPresentWeatherId);
		System.out.println("날씨 타입 : "+strWeatherMain);
		System.out.println("날씨 세부명 : "+strWeatherDesc);
		System.out.println("날씨 아이콘 : "+strWeatherIcon);
		
		System.out.println("\n====================================\n");

		//lbPresentWeatherIcon.setText(value);
		
		lbPresentTime.setText(getPresent( (long) jsonObject.get("dt")));
		
		//lbPresentWeatherIcon.setText(strWeatherIcon);
	
		root.setStyle("-fx-background-image: url('//res/background/night.jpg');");
		root.setStyle("-fx-padding: 0;");
		root.setStyle("-fx-background-size: 950 550;");
		root.setStyle("fx-background-position: center center;");
		root.setStyle("-fx-background-repeat: no-repeat;");
	   
		
		String iconpath = ((int) fPresentWeatherId)+strWeatherIcon;
		//lbPresentWeatherIcon.setId( ();
		System.out.println(iconpath);
		lbPresentWeatherIcon.setStyle("-fx-background-image: url('//res/weather/"+iconpath+".png');");
		lbPresentTemp.setText(Math.round(fTemp*10)/10.0+"˚");

		lbPresentStatusWeatherDetail.setText(getDetail(Math.round(fPresentWeatherId*100)/100));
		
		if( fRain != 0){
			
			lbPresentStatusAmountRain.setText(fRain+"㎜");
			
		} else {
			
			lbPresentStatusAmountRain.setText(fSnow+"㎜");
			
		}
		
		lbPresentStatusHumidity.setText(fHumidity+"％");
		lbPresentStatusWind.setText(fSpeed+"㎧");
		lbPresentStatusClouds.setText(fClouds+"％");
		
	}
	

	public void getDailyWeather(double lat, double lon) throws Exception {

		String weatherurl = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + lat + "&lon=" + lon
				+ "&cnt=7&appid="+weatherkey;
		
		long time = 0;
		float fPressure = 0;
		float fHumidity = 0;
		float fSpeed = 0;
		float fClouds = 0;
		float fTempMorning = 0;
		float fTempDay = 0;
		float fTempNight = 0;
		float fTempMin = 0;
		float fTempMax = 0;
		String strWeatherMain = null;
		String strWeatherDesc = null;
		float fWeatherId = 0;
		String strWeatherIcon = null;

		URL obj = new URL(weatherurl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + weatherurl);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		//System.out.println(response.toString());

		JSONParser jsonParser = new JSONParser();

		// JSON데이터를 넣어 JSON Object 로 만들어 준다.
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());

		JSONArray listInfoArray = (JSONArray) jsonObject.get("list");
		
		for( int b=1 ; b<6 ; b++ ){
			
			JSONObject listObject = (JSONObject) listInfoArray.get(b);
			
			time = (long) listObject.get("dt");
		
			JSONObject temp = (JSONObject) listObject.get("temp");
			
			fTempMorning = Float.parseFloat(temp.get("morn").toString()) + kelvin;
			fTempDay = Float.parseFloat(temp.get("day").toString()) + kelvin;
			fTempNight = Float.parseFloat(temp.get("night").toString()) + kelvin;
			fTempMin = Float.parseFloat(temp.get("min").toString()) + kelvin;
			fTempMax = Float.parseFloat(temp.get("max").toString()) + kelvin;
			
			// weather의 배열을 추출
			JSONArray weatherInfoArray = (JSONArray) listObject.get("weather");
			
			for (int a = 0; a < weatherInfoArray.size(); a++) {
	
				JSONObject weatherObject = (JSONObject) weatherInfoArray.get(a);
	
				fWeatherId = Float.parseFloat(weatherObject.get("id").toString());
				strWeatherMain = (String) weatherObject.get("main");
				strWeatherDesc = (String) weatherObject.get("description");
				strWeatherIcon = (String) weatherObject.get("icon");
	
			}
			
			fPressure = Float.parseFloat(listObject.get("pressure").toString());
			fHumidity = Float.parseFloat(listObject.get("humidity").toString());
			fSpeed = Float.parseFloat(listObject.get("speed").toString());
			fClouds = Float.parseFloat(listObject.get("clouds").toString());
			
			System.out.println("\n==============="+b+"일 후의 날씨===============\n");
			
			System.out.println("dt : "+time);
			
			System.out.println("예상 시간 : "+getDaily( time ));
			
			System.out.println("예상 기압 : "+fPressure);
			System.out.println("예상 습도 : "+fHumidity);
			System.out.println("예상 구름 : "+fClouds);
			System.out.println("예상 바람 : "+fSpeed);
			
			System.out.println("아침 기온 : "+fTempMorning);
			System.out.println("낮 기온 : "+fTempDay);
			System.out.println("밤 기온 : "+fTempNight);
			System.out.println("최저 온도 : "+fTempMin);
			System.out.println("최고 온도 : "+fTempMax);
			
			System.out.println("날씨 ID : "+fWeatherId);
			System.out.println("날씨 타입 : "+strWeatherMain);
			System.out.println("날씨 세부명 : "+strWeatherDesc);
			System.out.println("날씨 아이콘 : "+strWeatherIcon);
			
			System.out.println("\n====================================\n");
			
			DailyWeather daily = new DailyWeather(getDaily( time ), strWeatherIcon, fWeatherId, getDetail(Math.round(fWeatherId*100)/100), fTempMin, fTempMax );
			
			hboxDaily.getChildren().add(daily);
			
		}
		
	}
	
	public void getHourlyWeather(float lat, float lon) throws Exception {

		String weatherurl = "http://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon
				+ "&appid="+weatherkey;
		
		long time = 0;
		float fTemp = 0;
		float fPressure = 0;
		float fHumidity = 0;
		float fSpeed = 0;
		float fClouds = 0;
		float fRain = 0;
		float fSnow = 0;
		String strWeatherMain = null;
		String strWeatherDesc = null;
		float fWeatherId = 0;
		String strWeatherIcon = null;
		String strTime = null;

		URL obj = new URL(weatherurl);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + weatherurl);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		//System.out.println(response.toString());

		JSONParser jsonParser = new JSONParser();

		// JSON데이터를 넣어 JSON Object 로 만들어 준다.
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());

		JSONArray listInfoArray = (JSONArray) jsonObject.get("list");
		
		for( int b=0 ; b<10 ; b++ ){
			
			JSONObject listObject = (JSONObject) listInfoArray.get(b);
			
			time = (long) listObject.get("dt");
		
			// weather의 배열을 추출
			JSONArray weatherInfoArray = (JSONArray) listObject.get("weather");
			
			for (int a = 0; a < weatherInfoArray.size(); a++) {
	
				JSONObject weatherObject = (JSONObject) weatherInfoArray.get(a);
	
				fWeatherId = Float.parseFloat(weatherObject.get("id").toString());
				strWeatherMain = (String) weatherObject.get("main");
				strWeatherDesc = (String) weatherObject.get("description");
				strWeatherIcon = (String) weatherObject.get("icon");
	
			}
			
			JSONObject main = (JSONObject) listObject.get("main");
			
			fTemp = Float.parseFloat(main.get("temp").toString()) + kelvin;
			fPressure = Float.parseFloat(main.get("pressure").toString());
			fHumidity = Float.parseFloat(main.get("humidity").toString());
			
			JSONObject clouds = (JSONObject) listObject.get("clouds");
			
			fClouds = Float.parseFloat(clouds.get("all").toString());
			
			JSONObject wind = (JSONObject) listObject.get("wind");
			
			fSpeed = Float.parseFloat( wind.get("speed").toString() );
			
			strTime = (String) listObject.get("dt_txt");
			
			if( listObject.containsKey("rain")==true ){
			
				JSONObject rain = (JSONObject) listObject.get("rain");	
				
				if( rain.containsKey("3h")==true ){
					
					fRain = Float.parseFloat( rain.get("3h").toString() );
					
				}
			
			}
			
			if( listObject.containsKey("snow")==true ){
			
				JSONObject snow = (JSONObject) listObject.get("snow");		
				
				if( snow.containsKey("3h")==true ){
				
					fSnow = Float.parseFloat( snow.get("3h").toString() );
				
				}
				
			}
			
			System.out.println("\n==============="+b*3+"시간 후의 날씨===============\n");
			
			System.out.println("dt : "+time);
			
			System.out.println("예상 시간 : "+getHourly( time ));
			System.out.println("예상 시간 : "+strTime );
			
			System.out.println("예상 온도 : "+fTemp);
			System.out.println("예상 기압 : "+fPressure);
			System.out.println("예상 습도 : "+fHumidity);
			System.out.println("예상 구름 : "+fClouds);
			System.out.println("예상 바람 : "+fSpeed);
			System.out.println("3h 강우량 : "+fRain);
			System.out.println("3h 강설량 : "+fSnow);
			
			System.out.println("날씨 ID : "+fWeatherId);
			System.out.println("날씨 타입 : "+strWeatherMain);
			System.out.println("날씨 세부명 : "+strWeatherDesc);
			System.out.println("날씨 아이콘 : "+strWeatherIcon);
			
			System.out.println("\n====================================\n");
			
			HourlyWeather hourly;
			
			if( fRain != 0){
				
				hourly = new HourlyWeather( getHourly( time ), strWeatherIcon, fWeatherId, getDetail(Math.round(fWeatherId*100)/100), fTemp, fRain, fHumidity, fSpeed, fClouds);
				
			} else {
				
				hourly = new HourlyWeather( getHourly( time ), strWeatherIcon, fWeatherId, getDetail(Math.round(fWeatherId*100)/100), fTemp, fSnow, fHumidity, fSpeed, fClouds);
				
			}
	
			if( b<5 ){
				
				hboxHourlyTop.getChildren().add(hourly);
				
			} else if ( b>=5 ){
				
				hboxHourlyBottom.getChildren().add(hourly);
				
			}
			
		}
			
	}

	public String getHourly( long dt ){
		
		long time = dt; 
		
		SimpleDateFormat simpleDateFlag= new SimpleDateFormat("HH");
		
		SimpleDateFormat simpleDateChange = new SimpleDateFormat("M/d HH시");
		
		SimpleDateFormat simpleDateContinue = new SimpleDateFormat("HH시");
		
		if( simpleDateFlag.format(time*1000).equals("00") || simpleDateFlag.format(time*1000).equals("21") ){
			
			return simpleDateChange.format((time*1000));//-32400000);
			
		} else {
			
			return simpleDateContinue.format((time*1000));//-32400000);
			
		}
		
	}
	
	public String getDaily( long dt ){
		
		long time = dt; 
		SimpleDateFormat simpleDate = new SimpleDateFormat("M월 d일");
		
		return simpleDate.format((time*1000));//-32400000);
		
	}
	
	public String getPresent( long dt ){
		
		long time = dt; 
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy년 M월 d일 HH시");
		
		return simpleDate.format((time*1000));//-32400000);
		
	}
	
	public String getDetail( float id ){
		
		String weatherDetail = null;
		
		if ( id==903 ){
			
			weatherDetail = "강추위";
		
		} else if (  id==956 || id==771 || id==781 || id==905 || id==957 || id==958 || id==959 || id==961 || id==960 ){
			
			weatherDetail = "강한바람";
		
		} else if ( id==314 || id==522 ){
			
			weatherDetail = "강한소나기";
		
		} else if ( id==802 || id==803 ){
			
			weatherDetail = "구름조금";
		
		} else if ( id==601 ){
			
			weatherDetail = "눈";
		
		} else if ( id==951 || id==800 || id==801 || id==500 ){
			
			weatherDetail = "맑음";
		
		} else if ( id==804 ){
			
			weatherDetail = "먹구름";
		
		} else if ( id==904 ){
			
			weatherDetail = "무더위";
		
		} else if ( id==954 ){
			
			weatherDetail = "바람";
		
		} else if ( id==302 || id==312 ){
			
			weatherDetail = "부슬비";
		
		} else if ( id==531 || id==502 ){
			
			weatherDetail = "비";
		
		} else if ( id==313 || id==321 || id==521 ){
			
			weatherDetail = "소나기";
		
		} else if ( id==711 ){
			
			weatherDetail = "스모그";
		
		} else if ( id==612 ){
			
			weatherDetail = "싸리눈";
		
		} else if ( id==701 || id==721 || id==741 ){
			
			weatherDetail = "안개";
		
		} else if (id==952 || id==955 || id==953 ){
			
			weatherDetail = "약한바람";
		
		} else if ( id==511 ){
			
			weatherDetail = "우박";
		
		} else if ( id==906 ){
			
			weatherDetail = "장대비";
		
		} else if ( id==221 || id==200 || id==211 || id==212 || id==230 || id==231 ){
			
			weatherDetail = "천둥";
		
		} else if ( id==616 || id==621 || id==622 ){
			
			weatherDetail = "진눈깨비";
		
		} else if ( id==202 || id==232 ){
			
			weatherDetail = "천둥비";
		
		} else if ( id==901 || id==902 ){
			
			weatherDetail = "태풍";
		
		} else if ( id==602 ){
			
			weatherDetail = "폭설";
		
		} else if ( id==503 || id==504 ){
			
			weatherDetail = "폭우";
		
		} else if ( id==902 ){
			
			weatherDetail = "허리케인";
		
		} else if ( id==762 ){
			
			weatherDetail = "화산재";
		
		} else if ( id==731 || id==751 || id==761 ){
			
			weatherDetail = "황사";
		
		} else if ( id==311 || id==501 ){
			
			weatherDetail = "흐리고비";
		
		} else if ( id==210 || id==300 || id==301 || id==520 || id==600 || id==611 || id==615 || id==260 || id==310 ){
			
			weatherDetail = "흐림";
		
		}
		
		return weatherDetail;
		
	}
	

/*	public int getWeatherId( float id ){
		
		int weatherId = ( int )id;
		
	}*/
	
}


