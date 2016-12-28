/*
 * GridPane span ��� ����
 * 
 * grid.setRowIndex(lblDesc, 3); grid.setColumnIndex(lblDesc, 2);
 * grid.setRowIndex(tfDesc, 4); grid.setColumnIndex(tfDesc,2);
 * grid.getChildren().addAll(lblDesc, tfDesc);
 * 
 * 
 * ��ü HBox
 * 
 * 
 * ������ ������ ������ �浵�� �޾ƿ��� lib �ʿ�
 * 
 * http://fronteer.kr/bbs/view/68 // openweather api Ȱ�� ���
 * 
 * http://seongilman.tistory.com/137 // ���� �Է��Ͽ� ���� �浵 �޾ƿ���
 * 
 * �ڹ�fx�� �̿��ϱ� ���� �ڵ���� ����ϹǷ� �ϴ� �ڹٿ��� �ڵ��� �Ͽ���
 * �� �� �� �� �� ���� ���� �ڷ���� mariadb�� ������ �Ͽ����� 
 * ������Ʈ �ð� ����� ���̾� ������ ũ�� ������ ó�� ȸ�� ������ �޴� �������� ���� ���� ����
 * db���°� �ƴ� ���� ��ũ��Ʈ���� �ҷ����� ���·� ��
 * �����ð� ���� ������ ������Ʈ �Ǿ����� �����Ƿ� �� ���ø����̼��� �����ÿ� ���� ������ �������� ����
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
	
	float lat = 0;		//	���� ���� ����
	float lon = 0;		//	�浵 ���� ����
	String location = null;		//	�� �� ���� ���� �ּҰ� string ����
	String sido = null;			//	�Է��� �õ� ���� ���� string ����
	String gungu = null;		//	�Է��� ���� ���� ���� string ����
	String dong = null;			//	�Է��� �� ���� ���� string ����
	String sidoItem = "==��,��==";		//	�õ� selectBox �� �տ� ������ string 
	String gunguItem = "==��, ��==";		//	���� selectBox �� �տ� ������ string
	String dongItem = "==��,��,��==";		//	������ selectBox �� �տ� ������ string
	String filePath = "C:/java_workspace/4pWeatherFinal/zipdb.xlsx";		//	�õ� ���� ������ �� ����� ���� ������ �ҷ����� ���
	
	float kelvin =  (float) -273.15;		//	OpenWeather Api���� ����� ȭ���� ������ ������ ��ȯ��ų kelvin��

	private final String USER_AGENT = "Mozilla/5.0";		//	 �������� ���� ����?

	JPanel pnNorth, pnCenter;			//	�������� ���� ����г�, �߾��г�
	Choice chSido, chGungu, chDong;		//	�õ�, ����, �������� ���� choice �ڽ�, ����Ʈ �ڽ����� �����
	JTextField txtfWeather, txtfMinTemp, txtfMaxTemp, txtfPressure, txtfHumidity, txtfSpeed;
	JButton btnExe;

	public WeatherOrg() {
		
		pnNorth = new JPanel();
		pnCenter = new JPanel();
		
		chSido = new Choice();
		chGungu = new Choice();
		chDong = new Choice();
		
		btnExe = new JButton("ã��");
		
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
			
					JOptionPane.showMessageDialog(getParent(), "�õ��� �Է� �ȹٷ�");
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
		
		//	��ũ���� ���� - ���������� �����ϱ� ���� ��ü
		XSSFWorkbook workBook;
		
		try {
			
			workBook = new XSSFWorkbook( file );
			//	���� ���Ͽ� ���� Sheet�� �����ϱ�
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
		
		//	��ũ���� ���� - ���������� �����ϱ� ���� ��ü
		try {
			XSSFWorkbook workBook = new XSSFWorkbook( file );
			
			//	���� ���Ͽ� ���� Sheet�� �����ϱ�
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
		
		//	��ũ���� ���� - ���������� �����ϱ� ���� ��ü
		try {
			XSSFWorkbook workBook = new XSSFWorkbook( file );
			
			//	���� ���Ͽ� ���� Sheet�� �����ϱ�
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

		//JSON�����͸� �־� JSON Object �� ����� �ش�. 
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());

		//weather�� �迭�� ���� 
		JSONArray locationInfoArray = (JSONArray) jsonObject.get("results");

		for(int i=0; i<locationInfoArray.size(); ++i){

			JSONObject results = (JSONObject)locationInfoArray.get(i); 
			JSONObject location = (JSONObject)results.get("geometry"); 
			JSONObject latlng = (JSONObject)location.get("location");
			//System.out.println(latlng.get("lat")+", "+latlng.get("lng"));
	
			lat = Float.parseFloat( latlng.get("lat").toString()); 
			lon = Float.parseFloat(latlng.get("lng").toString()); 
		
		}

		System.out.println("���� : "+lat+", �浵 : "+lon);

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

		// JSON�����͸� �־� JSON Object �� ����� �ش�.
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());

		// weather�� �迭�� ����
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
		
		System.out.println("\n===============���糯��===============\n");
			
		System.out.println("dt : "+(long) jsonObject.get("dt"));
		
		System.out.println(System.currentTimeMillis());

		System.out.println("���� �ð�1 : "+getTime( (long) jsonObject.get("dt")));

		System.out.println("���� �µ� : "+fTemp);
		System.out.println("���� ��� : "+fPressure);
		System.out.println("���� ���� : "+fHumidity);
		System.out.println("���� ���� : "+fClouds);
		System.out.println("���� �ٶ� : "+fSpeed);
		System.out.println("3h ���췮 : "+fRain);
		System.out.println("3h ������ : "+fSnow);
		
		System.out.println("���� ID : "+fWeatherId);
		System.out.println("���� Ÿ�� : "+strWeatherMain);
		System.out.println("���� ���θ� : "+strWeatherDesc);
		System.out.println("���� ������ : "+strWeatherIcon);
		
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

		// JSON�����͸� �־� JSON Object �� ����� �ش�.
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());

		JSONArray listInfoArray = (JSONArray) jsonObject.get("list");
		
		for( int b=0 ; b<listInfoArray.size() ; b++ ){
			
			JSONObject listObject = (JSONObject) listInfoArray.get(b);
			
			time = (long) listObject.get("dt");
		
			// weather�� �迭�� ����
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
			
			System.out.println("\n==============="+b*3+"�ð� ���� ����===============\n");
			
			System.out.println("dt : "+time);
			
			System.out.println("���� �ð� : "+getTime( time ));
			System.out.println("���� �ð� : "+strTime );
			
			System.out.println("���� �µ� : "+fTemp);
			System.out.println("���� ��� : "+fPressure);
			System.out.println("���� ���� : "+fHumidity);
			System.out.println("���� ���� : "+fClouds);
			System.out.println("���� �ٶ� : "+fSpeed);
			System.out.println("3h ���췮 : "+fRain);
			System.out.println("3h ������ : "+fSnow);
			
			System.out.println("���� ID : "+fWeatherId);
			System.out.println("���� Ÿ�� : "+strWeatherMain);
			System.out.println("���� ���θ� : "+strWeatherDesc);
			System.out.println("���� ������ : "+strWeatherIcon);
			
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

		// JSON�����͸� �־� JSON Object �� ����� �ش�.
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
			
			// weather�� �迭�� ����
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
			
			System.out.println("\n==============="+b+"�� ���� ����===============\n");
			
			System.out.println("dt : "+time);
			
			System.out.println("���� �ð� : "+getTime( time ));
			
			System.out.println("���� ��� : "+fPressure);
			System.out.println("���� ���� : "+fHumidity);
			System.out.println("���� ���� : "+fClouds);
			System.out.println("���� �ٶ� : "+fSpeed);
			
			System.out.println("��ħ ��� : "+fTempMorning);
			System.out.println("�� ��� : "+fTempDay);
			System.out.println("�� ��� : "+fTempNight);
			System.out.println("���� �µ� : "+fTempMin);
			System.out.println("�ְ� �µ� : "+fTempMax);
			
			System.out.println("���� ID : "+fWeatherId);
			System.out.println("���� Ÿ�� : "+strWeatherMain);
			System.out.println("���� ���θ� : "+strWeatherDesc);
			System.out.println("���� ������ : "+strWeatherIcon);
			
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




