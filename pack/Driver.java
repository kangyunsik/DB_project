package pack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Driver {
	static Scanner sc = new Scanner(System.in);

	static Connection conn = null;
	static Statement st = null;
	static ResultSet rs = null;

	static StringBuilder urlBuilder;
	static URL v_url;
	static HttpURLConnection htcon;
	static Document document;
	static DocumentBuilderFactory builderFactory;
	static DocumentBuilder builder;
	static NodeList nList;
	static Node node;
	
	static String url = "jdbc:postgresql://localhost/dbpro";
	static String user = "test";
	static String password = "test";
	
	static int user_sid = 0;
	static String user_id=null;
	static String user_pw=null;
	static String user_name = null;
	static Scanner scan = new Scanner(System.in);
	
	public static void main(String[] args) throws IOException, SQLException, Exception {
		String func;
		
		System.out.println("데이터베이스 팀 프로젝트");
		System.out.println("----------------------------");
		System.out.println("데이터베이스에 연결중입니다...");
		
		try {
			conn = DriverManager.getConnection(url, user, password);
			st = conn.createStatement();
			
			if(conn!=null) {
				System.out.println("데이터베이스와 연결이 성공하였습니다");
			}
			else
			{
				System.out.println("데이터베이스와 연결이 실패하였습니다. 프로그램을 종료합니다");
				System.exit(0);
			}
			}catch(Exception e) {
			e.printStackTrace(); 
		}
		
		
		while (true)
		{
			System.out.println("1. 로그인");
			System.out.println("2. 회원가입");
			System.out.println("3. 종료");
			System.out.println("원하시는 기능을 선택해주세요: ");
			func = scan.nextLine();
			if (func.equals("1")) {
				user_sid = DB_Login();
				startDB();
			}
			else if (func.equals("2")) {
				DB_Register();
			}
			else if(func.equals("3")){
				System.out.println("----------------------------");
				System.out.println(" 이용해 주셔서 감사합니다. 안녕히가세요  ");
				System.out.println("----------------------------");
				System.exit(0);
			}
			else
				System.out.println("잘못된 입력입니다");
		}
	}
	
	public static void DB_Register() throws SQLException {
		int count=0;
		int sid_max=0;
		System.out.println("이름을 입력해주세요 : ");
		user_name = scan.nextLine();
		while(true) {
			System.out.println("아이디를 입력해주세요 : ");
			user_id = scan.nextLine();
			//System.out.println("select count(*) from Account where uID = '"+user_id+"'");
			rs = st.executeQuery("select count(*) from Account where uID = \'"+user_id+"\';");
			if(rs.next()) {
				count = rs.getInt(1);
			}
			if(count>0){
				System.out.println("중복된 아이디 입니다");
			} 
			else
				break;
		}
		System.out.println("비밀번호를 입력해주세요 : ");
		user_pw = scan.nextLine();
		
		rs = st.executeQuery("select max(sID) from Account");
		if(rs.next()) {
		sid_max = rs.getInt(1);
		}
		sid_max++;
		System.out.println("1!");
		st.executeUpdate("Insert into Account values("+sid_max+", '"+user_name+"', '"+user_id+"', '"+user_pw+"');");
		System.out.println(user_name+"님 회원가입이 완료되었습니다");
	}
	
	public static int DB_Login() throws SQLException {
		String user_id;
		String user_pw;
		int get_sid;
		while(true) {
			System.out.println("아이디 입력 : ");
			user_id = scan.nextLine();
			System.out.println("비밀번호 입력 : ");
			user_pw = scan.nextLine();
			rs = st.executeQuery("select * from Account where uID = '"+user_id+"' and hashed_password = '"+user_pw+"';");
			if(rs.next()) {
				if(rs.getString(3).equals(user_id)&&rs.getString(4).equals(user_pw)) {
					get_sid = rs.getInt(1);
					break;
				}
			}
			else
				System.out.println("아이디 또는 비밀번호가 올바르지 않습니다");
		}
		System.out.println(rs.getString(2)+"님 로그인에 성공하였습니다");
		return get_sid;
	}
	
	public static void Setting_Account() throws SQLException {
		String type;
		while(true) {
			System.out.println("1. 비밀번호 변경");
			System.out.println("2. 아이디 삭제");
			System.out.println("3. 뒤로가기");
			System.out.println("원하시는 기능을 선택해 주세요 : ");
			type = scan.nextLine();
			if(type.equals("1")) {
				System.out.println("기존의 비밀번호를 입력해 주세요 : ");
				type = scan.nextLine();
				if(type.equals(user_pw)) {
					System.out.println("변경하고자 하는 비밀번호를 입력해 주세요 : ");
					user_pw = scan.nextLine();
					st.executeUpdate("update Account set hashed_password = '"+user_pw+"' where sid = "+user_sid+";");
					System.out.println("비밀번호가  "+user_pw+"로 변경되었습니다");
			
				}
				else
					System.out.println("비밀번호가 일치하지 않습니다 메뉴로 돌아갑니다");
			}
			else if(type.equals("2")) {
				System.out.println("기존의 비밀번호를 입력해 주세요 : ");
				type = scan.nextLine();
				if(type.equals(user_pw)) {
					System.out.println("아이디를 삭제하시겠습니까? 삭제하시려면 1을 입력하세요");
					type= scan.nextLine();
					if(type.equals("1")) {
						st.executeUpdate("delete from Account where sid = "+user_sid+";");
						user_sid = 0;
						System.out.println(user_name+"님 계정삭제가 완료되었습니다");
						break;
					}
					else
						System.out.println("잘못된 입력입니다 메뉴로 돌아갑니다");
				}
				else
					System.out.println("비밀번호가 일치하지 않습니다 메뉴로 돌아갑니다");
			}
			else if(type.equals("3")) {
				break;
			}
			else
				System.out.println("잘못된 입력입니다");
		}
	}
	
	public static void startDB() throws SQLException, Exception {
		String type;
		rs = st.executeQuery("select sname, uid, hashed_password from Account where sid = "+user_sid+";");
		if(rs.next()) {
			user_name = rs.getString(1);
			user_id = rs.getString(2);
			user_pw = rs.getString(3);
		}
		
		
		
		while(true) {
			System.out.println("----------------------------");
			System.out.println("1. 개인정보 수정");
			System.out.println("2. 보유 재료 추가");
			System.out.println("3. 보유한 재료 목록 확인");
			System.out.println("4. 유통기한 갱신");
			System.out.println("5. 조리가능한 음식 목록 출력");
			System.out.println("6. 음식 레시피 검색");
			System.out.println("7. 로그아웃");
			System.out.println("8. 레시피 재료테이블 업데이트");
			System.out.println("9. 종료");
			System.out.println("원하시는 기능을 선택해 주세요 : ");
			type = scan.nextLine();
			if(type.equals("1")) {
				System.out.println("개인정보 수정을 선택하셨습니다");
				Setting_Account();
				if(user_sid==0)
					break;
			}
			else if(type.equals("2")) {
				
			}
			else if(type.equals("3")) {
				
			}
			else if(type.equals("4")) {
				
			}
			else if(type.equals("5")) {
				
			}
			else if(type.equals("6")) {
				
			}
			else if(type.equals("7")) {
				System.out.println("----------------------------");
				System.out.println("               로그아웃 되었습니다          ");
				System.out.println("----------------------------");
				break;
			}

			else if(type.equals("8")){
				System.out.println("----------------------------");
				System.out.println(" 레시피 재료목록 업데이트를 진행합니다.  ");
				System.out.println("----------------------------");
				
				Update_database();
				//Update_foodDB();
			}
			else if(type.equals("9")){
				System.out.println("----------------------------");
				System.out.println(" 이용해 주셔서 감사합니다. 안녕히가세요  ");
				System.out.println("----------------------------");
				System.exit(0);
			}
			else
				System.out.println("잘못된 입력입니다");
			
			
			
			
			
		}
	}
	
	private static void Update_database() {
		builderFactory = DocumentBuilderFactory.newInstance();
		try {
			builder = builderFactory.newDocumentBuilder();

			System.out.println("레시피 정보 테이블을 업데이트중입니다.");
			set_RecipeInfo_make();						// recipeinfo 테이블에 tuple : rid, rname, cookingtime 넣기.
			System.out.println("레시피 정보 테이블 조리 과정을 업데이트중입니다.");
			set_RecipeInfo_update_cookingprocess();		// recipeinfo 테이블 각각 tuple에 cookingprocess 추가.
			System.out.println("재료 테이블을 업데이트중입니다.");
			Update_foodDB();
			System.out.println("업데이트가 완료되었습니다.");
			
		}catch(Exception e) {
			e.getStackTrace();
		}
	}
	
	private static void set_RecipeInfo_make() throws SQLException, IOException, ParserConfigurationException, SAXException {
		urlBuilder = new StringBuilder(
				"http://211.237.50.150:7080/openapi/d68c8082a8eef4b3573701caf72dcb0626a3bb238896f37c3aa3adf55d0a1509/xml/Grid_20150827000000000226_1/1/1000");

		rs = st.executeQuery("select count(*) from pg_tables where tablename = 'recipeinfo'");
		rs.next();
		
		if(rs.getInt(1) == 0) {
			st.execute("create table recipeinfo(rID int, rName text, Num int, CookingTime int, CookingProcess text)");
		}
		
		
		v_url = new URL(urlBuilder.toString());
		htcon = (HttpURLConnection) v_url.openConnection();
		htcon.setRequestMethod("GET");
		htcon.setRequestProperty("Content-type", "application/json");
		BufferedReader rd;
		if (htcon.getResponseCode() >= 200 && htcon.getResponseCode() <= 300) {
			rd = new BufferedReader(new InputStreamReader(htcon.getInputStream()));
		} else {
			rd = new BufferedReader(new InputStreamReader(htcon.getErrorStream()));
		}
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}

		
		
		document = builder.parse(v_url.toString());
		document.getDocumentElement().normalize();

		nList = document.getElementsByTagName("Grid_20150827000000000226_1");
		node = nList.item(0);

		nList = document.getElementsByTagName("row");
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);

			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				String recipeinfo_update;
				int rID;
				
				String check = String.format("select count(*) from recipeinfo where rID = %d",Integer.parseInt(getTagValue("RECIPE_ID", eElement)));
				rs = st.executeQuery(check);
				rs.next();
				
				rID = rs.getInt(1);
				if(rID > 0) {	// rID가 이미 존재하면, update 수행하는 쿼리 작성
					recipeinfo_update = String.format("update recipeinfo set "
							+ "rName = '%s',"
							+ "CookingTime = %d "
							+ "where rID = %d",
							getTagValue("RECIPE_NM_KO", eElement),
							Integer.parseInt(getTagValue("COOKING_TIME", eElement).split("분")[0]),
							Integer.parseInt(getTagValue("RECIPE_ID", eElement)));
					
				}else {			// rID가 존재하지 않으면, insert 수행하는 쿼리 작성
					recipeinfo_update = String.format("insert into recipeinfo values(%d,'%s',null,%d,null)",
							Integer.parseInt(getTagValue("RECIPE_ID", eElement)),
							getTagValue("RECIPE_NM_KO", eElement),
							Integer.parseInt(getTagValue("COOKING_TIME", eElement).split("분")[0]));
				}
				
				st.executeUpdate(recipeinfo_update);	// 쿼리 수행.
			}
		}
	}
	private static void set_RecipeInfo_update_cookingprocess() throws IOException, SAXException, SQLException {
		for (int t = 0; t < 4; t++) {				// 4 * 1000개. 최대 4000개 update 가능.
			urlBuilder = new StringBuilder(
					"http://211.237.50.150:7080/openapi/d68c8082a8eef4b3573701caf72dcb0626a3bb238896f37c3aa3adf55d0a1509/xml/Grid_20150827000000000228_1/1/1000");
			v_url = new URL(urlBuilder.toString());
			htcon = (HttpURLConnection)v_url.openConnection();

			document = builder.parse(v_url.toString());
			document.getDocumentElement().normalize();

			nList = document.getElementsByTagName("Grid_20150827000000000228_1");
			node = nList.item(0);

			nList = document.getElementsByTagName("row");
			for (int i = 0; i < nList.getLength(); i++) {
				Node nNode = nList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String recipeinfo_update = String.format(
							"update recipeinfo set cookingprocess = '%s' where rid = %d",
							getTagValue("COOKING_DC", eElement),
							Integer.parseInt(getTagValue("RECIPE_ID", eElement)));

					st.executeUpdate(recipeinfo_update);
				}
			}
		}
	}

	private static void Update_foodDB() throws Exception {
		
		rs = st.executeQuery("select count(*) from pg_tables where tablename = 'recipe'");
		rs.next();
		if(rs.getInt(1) == 0) {
			String Create_RecipeTable = "create table recipe(rID int, fName text, requiredNumber int)";
			st.execute(Create_RecipeTable);
		}
		
		int foodCnt = 99999;
		
		for(int start = 1, end = 1000; start <= foodCnt ;start = start + 1000, end = end + 1000) {
			StringBuilder urlBuilder = new StringBuilder("http://211.237.50.150:7080/openapi/15461637ad1e73e3c432ddec0ec105cf559634437fc1d16b3d610b100ea2fb6c/xml/Grid_20150827000000000227_1");
			urlBuilder.append("/" + URLEncoder.encode(Integer.toString(start), "UTF-8"));
			urlBuilder.append("/" + URLEncoder.encode(Integer.toString(end), "UTF-8"));
	        URL url = new URL(urlBuilder.toString());
	        
	        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = builderFactory.newDocumentBuilder();
	        Document document = builder.parse(url.toString());
	        document.getDocumentElement().normalize();
	        
	        if(foodCnt == 99999) {
		        NodeList nList = document.getElementsByTagName("Grid_20150827000000000227_1");
		        Node node = nList.item(0);
		        foodCnt = Integer.parseInt(getTagValue("totalCnt", (Element)node));
	        }
	        NodeList nList = document.getElementsByTagName("row");
	        for(int i=0;i<nList.getLength(); i++) {
	        	Node nNode = nList.item(i);
	        	if(nNode.getNodeType() == Node.ELEMENT_NODE) {
	        		int rID;
	        		Element eElement = (Element)nNode;
	        		
	        		String amount = getTagValue("IRDNT_CPCTY", eElement);
					int Get_amount = getAmount(amount);
					String fname = getTagValue("IRDNT_NM", eElement);
					int Get_rID = Integer.parseInt(getTagValue("RECIPE_ID", eElement));
					
	        		String check = String.format("select count(*) from recipe where rID = %d",Get_rID);
					rs = st.executeQuery(check);
					rs.next();
					
					rID = rs.getInt(1);
	        		
					if(rID > 0) {	// rID가 이미 존재하면, update 수행.
						String update_recipe = String.format("update recipe set "
								+ "fName = '%s', "
								+ "requiredNumber = '%d' "
								+ "where rID = %d",
								fname,
								Get_amount,
								rID);
						st.executeUpdate(update_recipe);
					} else {		// rID가 존재하지 않으면, insert 수행.
						st.executeUpdate("insert into recipe values (" + getTagValue("RECIPE_ID", eElement) + ",'"
								+ fname + "', " + Get_amount + ");");
						
					}
	        	}
	        }
        }
	}
	
	private static int getAmount(String str) {
		String intStr = "";
		boolean flag = false;
		if(str == null) return 0;
		for (int i = 0; i < str.length(); i++) {
		    char ch = str.charAt(i);
		    if (48 <= ch && ch <= 57) {
		        intStr += ch;
		        flag = true;
		    }
		    else {
		    	if(flag) break;
		    }
		}
		if(intStr == "") return 1;
		return Integer.parseInt(intStr);
	}
	
	private static String getTagValue(String tag, Element e) {
		NodeList nList = e.getElementsByTagName(tag).item(0).getChildNodes();
		Node nValue = (Node) nList.item(0);
		if(nValue == null) return null;
		return nValue.getNodeValue();
	}
}