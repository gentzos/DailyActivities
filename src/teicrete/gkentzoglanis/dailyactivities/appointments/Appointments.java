package teicrete.gkentzoglanis.dailyactivities.appointments;

// A class for defining the structure of our appointments. Constructors, getters, setters.
public class Appointments {

	private String subject, descr, location, year, month, day, startHour;

	public Appointments() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Appointments(String subject, String descr, String location,
			String year, String month, String day, String startHour) {
		super();
		this.subject = subject;
		this.descr = descr;
		this.location = location;
		this.year = year;
		this.month = month;
		this.day = day;
		this.startHour = startHour;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	public String getStartHour() {
		return startHour;
	}

	public void setStartHour(String startHour) {
		this.startHour = startHour;
	}
}
