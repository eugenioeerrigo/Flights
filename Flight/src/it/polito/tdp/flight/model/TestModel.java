package it.polito.tdp.flight.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TestModel {

	public static void main(String[] args) {

		Model m = new Model();
		
		Airport a  = m.getAirports().get(0);
		System.out.println("Primo aeroporto della lista: "+a);
		System.out.println("Rotte relative ad esso: "+a.getRoutes()+"\n");
		
		m.createGraph();
		
		Set<Airport> biggestSCC = m.printStats();
		System.out.println("Dimensione componente connessa più grossa (strong): "+biggestSCC.size());
		
		try {                                                    //try/catch perchè ho fatto throw nel model (posso generare exception e devo gestirla)
			
			//System.out.println(m.getShortestPath(8591, 1525));    //test con nyc e bgy (NY e Bergamo) => NO CAMMINO 
			
			List<Airport> airportList = new ArrayList<Airport>(biggestSCC);
			System.out.println("Fermate intermedie: "+m.getShortestPath(airportList.get(0).getAirportId(), airportList.get(15).getAirportId()));  
			//test con due aeroporti della comp connessa più grossa 
			
		} catch (RuntimeException e) {
			System.out.println("Airport code error.");
		}
	}

}
