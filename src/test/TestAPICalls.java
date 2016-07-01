package test;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

import to.CarTo;
import to.OrderTo;
import dbconnection.IntializeDataBaseWithData;

@PrepareForTest({ IntializeDataBaseWithData.class })
public class TestAPICalls {

	static IntializeDataBaseWithData intializeDataBaseObject;

	@BeforeClass
	public static void setUpTest() throws Exception {

		intializeDataBaseObject = IntializeDataBaseWithData.getInstance();

	}

	@AfterClass
	public static void removeSetUpTest(){
		intializeDataBaseObject.cleanUpDataBase();
	}

	// find all car
	@Test
	public void testForGetAllCars() {
		List<CarTo> toResults = intializeDataBaseObject.getAllCar(null);
		Assert.assertTrue(toResults.size() == 11);
	}

	// find nearest car
	@Test
	public void testFindNearestCar() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("location", "12-32");
		CarTo toResult = intializeDataBaseObject.getCar(data);
		Assert.assertTrue(toResult.getCarId().equals("11"));
	}

	// book a car and get the cost
	@Test
	public void testBookCar() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("location", "12-32");
		data.put("userId", "123e2");
		data.put("isBook", true);
		OrderTo toResult = intializeDataBaseObject.bookCar(data);
		Boolean isRideStarted = intializeDataBaseObject.startRide(toResult
				.getoId());
		data = new HashMap<String, Object>();
		data.put("location", "16-34");
		data.put("tId", toResult.getoId());
		OrderTo billTo1 = intializeDataBaseObject.endRide(data);
		Assert.assertTrue(isRideStarted == true);
		Assert.assertTrue(billTo1.getDistance() == 4.0);
		Assert.assertTrue(billTo1.getCost() == 8.0);

	}

	// book a special car and get the cost
	@Test
	public void testBookSplCar() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("location", "12-32");
		data.put("userId", "123e2");
		data.put("isBook", true);
		data.put("color", "pink");
		OrderTo toResult = intializeDataBaseObject.bookCar(data);
		Boolean isRideStarted = intializeDataBaseObject.startRide(toResult
				.getoId());
		data = new HashMap<String, Object>();
		data.put("location", "16-34");
		data.put("tId", toResult.getoId());
		OrderTo billTo1 = intializeDataBaseObject.endRide(data);
		Assert.assertTrue(isRideStarted == true);
		Assert.assertTrue(billTo1.getDistance() == 4.0);
		Assert.assertTrue(billTo1.getCost() == 13.0);
	}

	// book a car and number of car should be less by 1 and when end should be
	// available
	@Test
	public void testBookNumberCar() {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("location", "12-32");
		data.put("userId", "123e2");
		data.put("isBook", true);
		data.put("color", "pink");
		OrderTo toResult = intializeDataBaseObject.bookCar(data);
		Boolean isRideStarted = intializeDataBaseObject.startRide(toResult
				.getoId());
		data = new HashMap<String, Object>();
		data.put("location", "16-34");
		data.put("tId", toResult.getoId());

		Assert.assertTrue(intializeDataBaseObject.getAllCar(null).size() == 10);
		OrderTo billTo1 = intializeDataBaseObject.endRide(data);
		Assert.assertTrue(intializeDataBaseObject.getAllCar(null).size() == 11);
		Assert.assertTrue(isRideStarted == true);
		Assert.assertTrue(billTo1.getDistance() == 4.0);
		Assert.assertTrue(billTo1.getCost() == 13.0);
	}

	// no car avaliable
	@Test
	public void testNoCarAvailable() throws SQLException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("location", "12-32");
		data.put("isBook", true);
		data.put("color", "pink");
		for (int i = 0; i < 11; i++) {
			data.put("userId", "123e2" + i);
			OrderTo toResult = intializeDataBaseObject.bookCar(data);
			data = new HashMap<String, Object>();
			data.put("location", "16-34");
			data.put("tId", toResult.getoId());
		}
		Assert.assertTrue(intializeDataBaseObject.getAllCar(null).size() == 0);
		intializeDataBaseObject.cleanUpDataBase();
		intializeDataBaseObject.init();
	}

}
