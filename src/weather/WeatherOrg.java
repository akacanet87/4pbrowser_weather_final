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
 * 자바fx를 이용하기 전에 코드들은 비슷하므로 일단 자바에서 코딩을 하였음
 * 시 도 군 구 동 등의 지역 자료들을 mariadb에 담으려 하였으나 
 * 프로젝트 시간 관계상 파이어 폭스나 크롬 브라우저 처럼 회원 가입을 받는 브라우저를 만들 수가 없어
 * db형태가 아닌 엑셀 워크시트에서 불러오는 형태로 됨
 * 세종시가 구글 지도에 업데이트 되어있지 않으므로 이 어플리케이션은 세종시에 대한 날씨를 지원하지 않음
 * 
 */
package weather;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class WeatherOrg extends JFrame{
	
	float lat = 0;		//	위도 담을 변수
	float lon = 0;		//	경도 담을 변수
	String location = null;		//	시 군 구를 합한 주소값 string 변수
	String sido = null;			//	입력한 시도 값을 담을 string 변수
	String gungu = null;		//	입력한 군구 값을 담을 string 변수
	String dong = null;			//	입력한 동 값을 담을 string 변수
	String sidoItem = "==도,시==";		//	시도 selectBox 맨 앞에 보여질 string 
	String gunguItem = "==군, 구==";		//	군구 selectBox 맨 앞에 보여질 string
	String dongItem = "==동,읍,리==";		//	동읍리 selectBox 맨 앞에 보여질 string
	String filePath = "C:/java_workspace/4pWeatherFinal/zipdb.xlsx";		//	시도 군구 동읍리 가 저장된 엑셀 파일을 불러오는 경로
	
	float kelvin =  (float) -273.15;		//	OpenWeather Api에서 기온을 화씨로 보내기 때문에 변환시킬 kelvin값

	private final String USER_AGENT = "Mozilla/5.0";		//	 브라우저의 지원 형태?

	JPanel pnNorth, pnCenter;			//	디자인을 위한 상단패널, 중앙패널
	Choice chSido, chGungu, chDong;		//	시도, 군구, 동읍리를 위한 choice 박스, 셀렉트 박스에서 변경됨
	JTextField txtfWeather, txtfMinTemp, txtfMaxTemp, txtfPressure, txtfHumidity, txtfSpeed;
	JButton btnExe;

	public WeatherOrg() {
		
		pnNorth = new JPanel();
		pnCenter = new JPanel();
		
		chSido = new Choice();
		chGungu = new Choice();
		chDong = new Choice();
		
		btnExe = new JButton("찾기");
		
		txtfWeather = new JTextField(15);
		
		pnNorth.add(chSido);
		pnNorth.add(chGungu);
		pnNorth.add(chDong);
		pnNorth.add(btnExe);
		pnCenter.add(txtfWeather);
		
		add(pnNorth, BorderLayout.NORTH);
		add(pnCenter);
		
		chSido.addItemListener( new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
			
				if ( !e.getItem().equals(sidoItem) ){
					
					chGungu.removeAll();
					chGungu.add(gunguItem);
					chDong.removeAll();
					chDong.add(dongItem);
					getGungu(chSido.getSelectedItem());
					
				} else {
					
					chGungu.removeAll();
					chGungu.add(gunguItem);
					chDong.removeAll();
					chDong.add(dongItem);
					
				}
			
			}
			
		});
		
		chGungu.addItemListener( new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
			
				if ( !e.getItem().equals(gunguItem) ){
					
					chDong.removeAll();
					chDong.add(dongItem);
					getDong(chGungu.getSelectedItem());
					
				} else {
					
					chDong.removeAll();
					chDong.add(dongItem);

				}
			
			}
			
		});
		
		
		btnExe.addActionListener( new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if( chSido.getSelectedIndex() == 0 || chGungu.getSelectedIndex() == 0 || chDong.getSelectedIndex() == 0 ){
			
					JOptionPane.showMessageDialog(getParent(), "시도동 입력 똑바로");
					return;
					
				} else {
				
					sido = chSido.getSelectedItem();
					gungu = chGungu.getSelectedItem();
					dong = chDong.getSelectedItem();
					
					location = sido+" "+gungu+" "+dong;
					
					try {
						
						getLatLon(location);
						getPresentWeather(lat, lon);
						getHourlyWeather(lat, lon);
						getWeekWeather(lat, lon);
						
						
					} catch (Exception e1) {
						
						e1.printStackTrace();
						
					}
					
				}
			
			}
			
		});

		chSido.add(sidoItem);
		chGungu.add(gunguItem);
		chDong.add(dongItem);
		getSido();
		
		addWindowListener( new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				System.exit(0);
				
			}
			
		});
		setBounds(300, 300, 500, 200);
		setVisible(true);

	}
	
	public void getSido(){
		
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
				chSido.add(value);
				
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
					chGungu.add(value);
					
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
					chDong.add(value);
					
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

		String locationurl = "http://maps.googleapis.com/maps/api/geocode/json?address="+newUrl+"key=AIzaSyBFAup6WBP98Q190VGT2LQrXSEM6bbkP98&language=ko";
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
				+ "&appid=cf8ae2399ef38cef92f916134dd48bba";
		
		String strWeatherMain = null;
		String strWeatherDesc = null;
		String strWeatherIcon = null;
		float fWeatherId = 0;
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

			fWeatherId = Float.parseFloat(weatherObject.get("id").toString());
			strWeatherMain = (String) weatherObject.get("main");
			strWeatherDesc = (String) weatherObject.get("description");
			strWeatherIcon = (String) weatherObject.get("icon");

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

		System.out.println("현재 시간1 : "+getTime( (long) jsonObject.get("dt")));

		System.out.println("현재 온도 : "+fTemp);
		System.out.println("현재 기압 : "+fPressure);
		System.out.println("현재 습도 : "+fHumidity);
		System.out.println("현재 구름 : "+fClouds);
		System.out.println("현재 바람 : "+fSpeed);
		System.out.println("3h 강우량 : "+fRain);
		System.out.println("3h 강설량 : "+fSnow);
		
		System.out.println("날씨 ID : "+fWeatherId);
		System.out.println("날씨 타입 : "+strWeatherMain);
		System.out.println("날씨 세부명 : "+strWeatherDesc);
		System.out.println("날씨 아이콘 : "+strWeatherIcon);
		
		System.out.println("\n====================================\n");
		
		txtfWeather.setText(strWeatherMain);

	}
	
	public void getHourlyWeather(float lat, float lon) throws Exception {

		String weatherurl = "http://api.openweathermap.org/data/2.5/forecast?lat=" + lat + "&lon=" + lon
				+ "&appid=cf8ae2399ef38cef92f916134dd48bba";
		
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
		
		for( int b=0 ; b<listInfoArray.size() ; b++ ){
			
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
			
			System.out.println("예상 시간 : "+getTime( time ));
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
			
		}
			
	}
	
	public void getWeekWeather(double lat, double lon) throws Exception {

		String weatherurl = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=" + lat + "&lon=" + lon
				+ "&cnt=7&appid=cf8ae2399ef38cef92f916134dd48bba";
		
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
		
		for( int b=0 ; b<listInfoArray.size() ; b++ ){
			
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
			
			System.out.println("예상 시간 : "+getTime( time ));
			
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
			
		}
		
	}

	public String getTime( long dt ){
		
		long time = (dt) * 1000 ; 
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss z");
		
		return simpleDate.format(time);
		
	}
	
	public String getDate(long milliSeconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
	    SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}


	public static void main(String[] args) {

		new WeatherOrg();

	}

}




