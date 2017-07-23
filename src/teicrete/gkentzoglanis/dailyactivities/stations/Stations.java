package teicrete.gkentzoglanis.dailyactivities.stations;

//A class for defining the structure of our appointments. Constructors, getters, setters.
public class Stations {

	private String company, strtAddress, strtNumber, city, postcode, state,
			country, opening, closing, imgUrl, latitude, longitude;

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getStrtAddress() {
		return strtAddress;
	}

	public void setStrtAddress(String strtAddress) {
		this.strtAddress = strtAddress;
	}

	public String getStrtNumber() {
		return strtNumber;
	}

	public void setStrtNumber(String strtNumber) {
		this.strtNumber = strtNumber;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getOpening() {
		return opening;
	}

	public void setOpening(String opening) {
		this.opening = opening;
	}

	public String getClosing() {
		return closing;
	}

	public void setClosing(String closing) {
		this.closing = closing;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	@Override
	public String toString() {
		return "Stations [company=" + company + ", strtAddress=" + strtAddress
				+ ", strtNumber=" + strtNumber + ", city=" + city
				+ ", postcode=" + postcode + ", state=" + state + ", country="
				+ country + ", opening=" + opening + ", closing=" + closing
				+ ", imgUrl=" + imgUrl + ", latitude=" + latitude
				+ ", longitude=" + longitude + "]";
	}
}
