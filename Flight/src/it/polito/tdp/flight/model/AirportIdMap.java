package it.polito.tdp.flight.model;

import java.util.*;

public class AirportIdMap {
	
	private Map<Integer, Airport> map;
	
	public AirportIdMap() {
		map = new HashMap<>();
	}
	
	public Airport get(int id) {
		return map.get(id);
	}

	public Airport get(Airport airport) {
		Airport old = map.get(airport.getAirportId());
		if(old==null) {
			map.put(airport.getAirportId(), airport);
			return airport;
		}
		return old;
	}
	
	public void put(Airport airport, int airportID) {
		map.put(airportID, airport);
	}

}
