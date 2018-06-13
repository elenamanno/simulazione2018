package it.polito.tdp.flightdelays.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

import it.polito.tdp.flightdelays.db.FlightDelaysDAO;

public class Model {
	private FlightDelaysDAO dao;
	private List<Airline> airlines;
	private List<Airport> airports;
	private List<Route> routes;
	//private List<Flight> flights;
	private Graph <Airport, DefaultWeightedEdge> graph;

	public Model() {
		super();
		this.dao = new FlightDelaysDAO();
		this.airlines=new ArrayList<>(this.dao.loadAllAirlines());
	//	this.flights=new ArrayList<>(this.dao.loadAllFlights());
	}

	public List<Airline> getAllAirlines() {
		return airlines;
	}
	

	/*public List<Flight> getFlights() {
		return flights;
	}*/

	public List<Airport> getAirports() {
		return airports;
	}

	public void createGraph(Airline a) {
		this.airports=new ArrayList<>(this.dao.loadAllAirports());
		this.routes= new ArrayList<>(this.dao.getRoutes(a));
		this.graph=new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		Graphs.addAllVertices(this.graph, this.airports);
		for(Route r : routes) {
			Graphs.addEdge(this.graph, r.getSource(), r.getDestination(), r.getPeso());
		}
	}

	private double calcolaPeso(Airline a, Airport ap1, Airport ap2) {
		// TODO Auto-generated method stub
		double denominatore = LatLngTool.distance(new LatLng(ap1.getLatitude(), ap1.getLongitude()), new LatLng(ap2.getLatitude(), ap2.getLongitude()), LengthUnit.KILOMETER);
		double peso = this.dao.getAVG(a, ap1, ap2) / denominatore;
		return peso;
	}

	private boolean sonoConnessi(Airline a, Airport ap1, Airport ap2) {
		// TODO Auto-generated method stub
		for(Flight f : this.dao.loadAirlineFlights(a, ap1, ap2)) {
			if(f.getOriginAirportId().equals(ap1.getId()) && f.getDestinationAirportId().equals(ap2.getId())) {
				return true;
			}
		}
		return false;
	}

	public List<Route> getRoutes() {
		return routes;
	}

}
