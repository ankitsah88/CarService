package to;

public class CarTo {

	public String carId;
	public int timeTaken;
	public String location;
	public String color;
	public Boolean isSpecial;

	public CarTo(String carId, int timeTaken, String location, String color,
			Boolean isSpecial) {
		super();
		this.carId = carId;
		this.timeTaken = timeTaken;
		this.location = location;
		this.color = color;
		this.isSpecial = isSpecial;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public Boolean getIsSpecial() {
		return isSpecial;
	}

	public void setIsSpecial(Boolean isSpecial) {
		this.isSpecial = isSpecial;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public int getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(int timeTaken) {
		this.timeTaken = timeTaken;
	}

}
