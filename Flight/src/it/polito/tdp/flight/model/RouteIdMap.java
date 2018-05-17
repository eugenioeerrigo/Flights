package it.polito.tdp.flight.model;

import java.util.*;

public class RouteIdMap {
	
	private Map<Integer, Route> map;
	
	public RouteIdMap() {
		map = new HashMap<>();
	}
	
	public Route get(String id) {
		return map.get(id);
	}

	public Route get(Route route) {
		Route old = map.get(route.getRouteId());
		if(old==null) {
			map.put(route.getRouteId(), route);
			return route;
		}
		return old;
	}
	
	public void put(Route route, int routeID) {
		map.put(routeID, route);
	}

}
