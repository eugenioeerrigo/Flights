package it.polito.tdp.flight.model;

import java.util.*;

import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flight.db.FlightDAO;

public class Model {
	
	//Implementazione del pattern ORM
	private FlightDAO fdao;
	private List<Airport> airports;
	private List<Airline> airlines;
	private List<Route> routes;
	
	private AirlineIdMap idmapline;
	private AirportIdMap idmapair;
	private RouteIdMap idmaproute;
	
	private SimpleDirectedWeightedGraph<Airport, DefaultWeightedEdge> graph;
	
	public Model(){
		fdao = new FlightDAO();
		
		idmapline = new AirlineIdMap();
		idmapair = new AirportIdMap();
		idmaproute = new RouteIdMap();
		
		airlines = fdao.getAllAirlines(idmapline);
		System.out.println("Lista linee: "+airlines.size());

		airports = fdao.getAllAirports(idmapair);
		System.out.println("Lista aeroporti: "+airports.size());

		routes = fdao.getAllRoutes(idmapline, idmapair, idmaproute);
		System.out.println("Lista rotte: "+routes.size()+"\n");
		
		
	}

	public List<Airport> getAirports() {
		if(this.airports==null)                         //evita errori sui controlli modello ORM
			return new ArrayList<>();
		return airports;
	}

	//PUNTO 1 - grafo
	public void createGraph() {
		graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.graph, this.airports);
		
		for(Route r : routes) {
			Airport source = r.getSourceAirport();
			Airport dest = r.getDestinationAirport();
			
			if(!source.equals(dest)) {                            //Evito loop di rotte con sorgente=destinazione
			double weight = LatLngTool.distance(new LatLng(source.getLatitude(), source.getLongitude()),
									new LatLng(dest.getLatitude(), dest.getLongitude()), LengthUnit.KILOMETER);
			
			Graphs.addEdge(this.graph, source, dest, weight);
			}
		}
		
		System.out.println("GRAFO\nVertex: "+graph.vertexSet().size());
		System.out.println("Edges: "+graph.edgeSet().size()+"\n");
	}
	
	public Set<Airport> printStats() {
		if(graph==null)
			this.createGraph();
		
		ConnectivityInspector<Airport, DefaultWeightedEdge> c = new ConnectivityInspector<>(graph);
		System.out.println("Tot componenti connesse: "+c.connectedSets().size());                               //tutti i set relativi alle varie componenti connesse del grafo
		
		//Cerco set più grosso
		Set<Airport> bestSet = null;
		int bestSize = 0;
		for(Set<Airport> s : c.connectedSets()) {
			if(s.size() > bestSize) {
				bestSize = s.size();
				bestSet = new HashSet<>(s);
			}
		}
		
		return bestSet;
	}

	//RICERCA CAMMINO MINIMO con Dijkstra
	public List<Airport> getShortestPath(int i, int j) {
		
		Airport a1 = idmapair.get(i);
		Airport a2 = idmapair.get(j);
		
		if(a1==null || a2==null)
			throw new RuntimeException("Gli aeroporti selezionati non sono presenti in memoria");
		
		System.out.println("\nAeroporto di partenza: "+a1.getName());
		System.out.println("Aeroporto di arrivo: "+a2.getName());
		
		ShortestPathAlgorithm<Airport, DefaultWeightedEdge> spa = new DijkstraShortestPath<>(graph); 
		
		double weight = spa.getPathWeight(a1, a2);                                     //peso del cammino 
		GraphPath<Airport, DefaultWeightedEdge> gp = spa.getPath(a1, a2);              //grafo del cammino
		
		System.out.println("Peso cammino : "+weight);                                  
		//SE weight= "Infinity" vuol dire che gli aeroporti non si trovano nella stessa comp. connessa 
		
		return gp.getVertexList();
	}
	
	
}
