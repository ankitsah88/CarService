package to;

public class OrderTo {

	// OID VARCHAR(64) NOT NULL,CARID VARCHAR(64) NOT NULL,UID VARCHAR(64)
	// NOT NULL,STARTLOC VARCHAR(64)NOT NULL ,STARTTIMESTAMP BIGINT NOT NULL
	// ,SPLRQST BOOLEAN NOT NULL, PRIMARY KEY (OID)

	String oId;
	String carId;
	String uId;
	String startLoc;
	Long startTimeStamp;
	Boolean splRqst;
	Long endTimeStamp;
	float distance;
	float cost;

	public OrderTo(String oId, String carId, String uId, String startLoc,
			Long startTimeStamp, Boolean splRqst) {
		super();
		this.oId = oId;
		this.carId = carId;
		this.uId = uId;
		this.startLoc = startLoc;
		this.startTimeStamp = startTimeStamp;
		this.splRqst = splRqst;
	}

	public Long getEndTimeStamp() {
		return endTimeStamp;
	}

	public void setEndTimeStamp(Long endTimeStamp) {
		this.endTimeStamp = endTimeStamp;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public String getoId() {
		return oId;
	}

	public void setoId(String oId) {
		this.oId = oId;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getuId() {
		return uId;
	}

	public void setuId(String uId) {
		this.uId = uId;
	}

	public String getStartLoc() {
		return startLoc;
	}

	public void setStartLoc(String startLoc) {
		this.startLoc = startLoc;
	}

	public Long getStartTimeStamp() {
		return startTimeStamp;
	}

	public void setStartTimeStamp(Long startTimeStamp) {
		this.startTimeStamp = startTimeStamp;
	}

	public Boolean getSplRqst() {
		return splRqst;
	}

	public void setSplRqst(Boolean splRqst) {
		this.splRqst = splRqst;
	}

}
