package dbconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import to.CarTo;
import to.OrderTo;

public class IntializeDataBaseWithData {

	static Connection conn;
	private static IntializeDataBaseWithData instance;

	private IntializeDataBaseWithData() {
		String driver = "org.apache.derby.jdbc.EmbeddedDriver";
		String connectionURL = "jdbc:derby:myDatabase3;create=true";
		try {
			Class.forName(driver);
		} catch (java.lang.ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			conn = DriverManager.getConnection(connectionURL);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static IntializeDataBaseWithData getInstance() {
		if (null == instance) {
			instance = new IntializeDataBaseWithData();
			init();
		}
		return instance;
	}

	public OrderTo bookCar(Map<String, Object> data) {

		CarTo to = getCar(data);
		if (to == null) {
			return null;
		}
		String id = UUID.randomUUID().toString().replaceAll("-", "");
		Boolean spl = false;
		if ((data.get("spl") != null && data.get("spl").equals(true))
				|| (data.get("color") != null && data.get("color").equals(
						"pink"))) {
			spl = true;
		}
		String insertQuery = "insert into Operation values ('" + id + "','"
				+ to.getCarId() + "','" + data.get("userId") + "','"
				+ data.get("location").toString() + "'," + 0 + " ," + spl + ")";
		String updateQuery = "update Cars set  AVAILABLE =" + false
				+ " where CARID='" + to.getCarId() + "'";
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(insertQuery);
			stmt.executeUpdate(updateQuery);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OrderTo to1 = new OrderTo(id, to.getCarId(), data.get("userId")
				.toString(), data.get("location").toString(), null, spl);
		return to1;
	}

	private long getCurrentTimeStamp() throws ParseException {
		Date date = new Date();
		SimpleDateFormat converter = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
		converter.setTimeZone(TimeZone.getTimeZone("UTC"));
		String UTCDateInString = converter.format(date);
		return converter.parse(UTCDateInString).getTime();
	}

	private OrderTo transformOrderModel(ResultSet rs) throws SQLException {
		OrderTo to = new OrderTo(rs.getString("OID"), rs.getString("CARID"),
				rs.getString("UID"), rs.getString("STARTLOC"),
				rs.getLong("STARTTIMESTAMP"), rs.getBoolean("SPLRQST"));
		return to;
	}

	public OrderTo endRide(Map<String, Object> data) {
		long UTCDateInString = 0;
		try {
			UTCDateInString = getCurrentTimeStamp();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		OrderTo to = null;
		Statement stmt2;
		try {
			stmt2 = conn.createStatement();

			String query = "select * from Operation where OID='"
					+ data.get("tId") + "'";
			ResultSet rs = stmt2.executeQuery(query);

			while (rs.next()) {
				to = transformOrderModel(rs);
			}
			int d = Math.round(getDistance(getXAxis(to.getStartLoc()),
					getYAxis(to.getStartLoc()), getXAxis(data.get("location")
							.toString()), getYAxis(data.get("location")
							.toString())));
			Long time = ((UTCDateInString) - to.getStartTimeStamp()) / (60000);
			long cost = (d * 2) + (time * 1);
			if (to.getSplRqst()) {
				cost = cost + 5;
			}
			to.setCost(cost);
			to.setEndTimeStamp((UTCDateInString));
			to.setDistance(d);
			String insertQuery = "insert into CTRANSACTION values ('"
					+ UUID.randomUUID().toString().replaceAll("-", "") + "','"
					+ to.getCarId() + "','" + to.getuId() + "','"
					+ to.getStartLoc() + "'," + to.getStartTimeStamp() + ",'"
					+ data.get("location") + "'," + (UTCDateInString) + ","
					+ cost + ")";

			String deleteQuery = "delete from Operation where OID='"
					+ data.get("tId") + "'";
			String updateQuery = "update Cars set  AVAILABLE =" + true
					+ " where CARID='" + to.getCarId() + "'";
			Statement stmt = conn.createStatement();
			stmt.execute(insertQuery);
			stmt.executeUpdate(deleteQuery);
			stmt.executeUpdate(updateQuery);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return to;

	}

	public Boolean startRide(String tId) {
		long UTCDateInString = 0;
		try {
			UTCDateInString = getCurrentTimeStamp();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String insertQuery = "update Operation set  STARTTIMESTAMP ="
				+ (UTCDateInString) + " where OID='" + tId + "'";
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate(insertQuery);
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			return false;
		}
		return true;
	}

	public CarTo getCar(Map<String, Object> data) {
		String location = data.get("location").toString();
		float xAxis = getXAxis(location);
		float yAxis = getYAxis(location);
		float distance = 1000;
		float dis;
		CarTo to = null;
		List<CarTo> list = getAllCar(data);
		for (int i = 0; i < list.size(); i++) {
			dis = getDistance(xAxis, yAxis,
					getXAxis(list.get(i).getLocation()), getYAxis(list.get(i)
							.getLocation()));
			to = dis <= distance ? list.get(i) : to;

		}
		return to;
	}

	private Float getXAxis(String location) {
		return Float.parseFloat(location.substring(0, location.indexOf("-")));
	}

	private Float getYAxis(String location) {
		return Float.parseFloat(location.substring(location.indexOf("-") + 1));
	}

	private Float getDistance(float x1, float y1, float x2, float y2) {

		if (x1 <= x2) {
			x1 = x1 + x2;
			x2 = x1 - x2;
			x1 = x1 - x2;
		}

		if (y1 <= y2) {
			y1 = y1 + y2;
			y2 = y1 - y2;
			y1 = y1 - y2;
		}
		return (float) Math.sqrt((Math.pow((x1 - x2), 2) + Math.pow((y1 - y2),
				2)));
	}

	public List<CarTo> getAllCar(Map<String, Object> data) {

		String query = "select * from Cars where AVAILABLE=true";
		if (data != null) {
			Iterator entries = data.entrySet().iterator();
			while (entries.hasNext()) {
				Map.Entry entry = (Map.Entry) entries.next();
				if (entry.getKey().equals("color")) {
					query = query + " and COLOR = '" + entry.getValue() + "'";
				}
			}
		}
		List<CarTo> list = new ArrayList<CarTo>();
		try {
			Statement stmt2 = conn.createStatement();
			ResultSet rs = stmt2.executeQuery(query);
			while (rs.next()) {
				list.add(transformModel(rs));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

	private CarTo transformModel(ResultSet rs) throws SQLException {
		CarTo to = new CarTo(rs.getString("CARID"), 0,
				rs.getString("LOCATION"), rs.getString("COLOR"),
				rs.getBoolean("AVAILABLE"));
		return to;
	}

	public void cleanUpDataBase() {
		Statement stmt2;
		try {
			stmt2 = conn.createStatement();

			String sti = "drop table Cars";
			String sti2 = "drop table Operation";
			String sti3 = "drop table CTRANSACTION";

			stmt2.execute(sti);
			stmt2.execute(sti2);
			stmt2.execute(sti3);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void init() {

		String createString = "CREATE TABLE Cars (CARID VARCHAR(64) NOT NULL,COLOR VARCHAR(64)NOT NULL ,LOCATION VARCHAR(64) ,AVAILABLE BOOLEAN NOT NULL,ISSPL BOOLEAN NOT NULL, PRIMARY KEY (CARID))";
		String createString1 = "CREATE TABLE Operation (OID VARCHAR(64) NOT NULL,CARID VARCHAR(64) NOT NULL,UID VARCHAR(64) NOT NULL,STARTLOC VARCHAR(64)NOT NULL ,STARTTIMESTAMP BIGINT ,SPLRQST BOOLEAN NOT NULL, PRIMARY KEY (OID))";
		String createString2 = "CREATE TABLE CTRANSACTION (TID VARCHAR(64) NOT NULL,CARID VARCHAR(64) NOT NULL,UID VARCHAR(64) NOT NULL,STARTLOC VARCHAR(64)NOT NULL ,STARTTIMESTAMP BIGINT NOT NULL , ENDLOC VARCHAR(64)NOT NULL ,ENDTIMESTAMP BIGINT NOT NULL ,BILL INT NOT NULL, PRIMARY KEY (TID))";
		Statement stmt = null;
		// String id = UUID.randomUUID().toString().replaceAll("-", "");
		String insStmnt = "insert into Cars values ('1','pink','13-14',true,true)";
		String insStmnt1 = "insert into Cars values ('2','red','15-10',true,true)";
		String insStmnt2 = "insert into Cars values ('3','pink','1-4',true,true)";
		String insStmnt3 = "insert into Cars values ('4','red','2-4',true,true)";
		String insStmnt4 = "insert into Cars values ('5','pink','122-124',true,true)";
		String insStmnt5 = "insert into Cars values ('6','black','121-140',true,true)";
		String insStmnt6 = "insert into Cars values ('7','pink','121-140',true,true)";
		String insStmnt7 = "insert into Cars values ('8','black','122-140',true,true)";
		String insStmnt8 = "insert into Cars values ('9','pink','18-1',true,true)";
		String insStmnt9 = "insert into Cars values ('10','pink','19-14',true,true)";
		String insStmnt10 = "insert into Cars values ('11','black','20-14',true,true)";
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(createString);
			stmt.executeUpdate(createString1);
			stmt.executeUpdate(createString2);
			stmt.execute(insStmnt);
			stmt.execute(insStmnt1);
			stmt.execute(insStmnt2);
			stmt.execute(insStmnt3);
			stmt.execute(insStmnt4);
			stmt.execute(insStmnt5);
			stmt.execute(insStmnt6);
			stmt.execute(insStmnt7);
			stmt.execute(insStmnt8);
			stmt.execute(insStmnt9);
			stmt.execute(insStmnt10);
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Statement stmt2;
			try {
				stmt2 = conn.createStatement();

				ResultSet rs = stmt2.executeQuery("select * from Cars");
				int num = 0;
				while (rs.next()) {
					System.out.println(++num + ": ID: " + rs.getString(1)
							+ " color " + rs.getString(2) + " loc "
							+ rs.getString(3) + " avl " + rs.getString(4)
							+ " loc " + rs.getString(5));
				}
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}