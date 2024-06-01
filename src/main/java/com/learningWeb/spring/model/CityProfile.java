package com.learningWeb.spring.model;

public class CityProfile {
	
	public String city_name;
	public int total_population;
	public int under_10;
	public double median_household_income;
	public double employment_rate;
	public int house_units;
	public double poverty_rate;
	public int total_race;
	public int race_asian;
	public int race_white;
	public int race_black;
	
	
	public CityProfile() {}
	
	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public int getTotal_population() {
		return total_population;
	}

	public void setTotal_population(int total_population) {
		this.total_population = total_population;
	}

	public int getUnder_10() {
		return under_10;
	}

	public void setUnder_10(int under_10) {
		this.under_10 = under_10;
	}

	public double getMedian_household_income() {
		return median_household_income;
	}

	public void setMedian_household_income(double median_household_income) {
		this.median_household_income = median_household_income;
	}

	public double getEmployment_rate() {
		return employment_rate;
	}

	public void setEmployment_rate(double employment_rate) {
		this.employment_rate = employment_rate;
	}

	public int getHouse_units() {
		return house_units;
	}

	public void setHouse_units(int house_units) {
		this.house_units = house_units;
	}

	public double getPoverty_rate() {
		return poverty_rate;
	}

	public void setPoverty_rate(double poverty_rate) {
		this.poverty_rate = poverty_rate;
	}

	public int getRace_asian() {
		return race_asian;
	}

	public void setRace_asian(int race_asian) {
		this.race_asian = race_asian;
	}

	public int getRace_white() {
		return race_white;
	}

	public void setRace_white(int race_white) {
		this.race_white = race_white;
	}

	public int getRace_black() {
		return race_black;
	}

	public void setRace_black(int race_black) {
		this.race_black = race_black;
	}
	public int getTotal_race() {
		return total_race;
	}

	public void setTotal_race(int total_race) {
		this.total_race = total_race;
	}
}

