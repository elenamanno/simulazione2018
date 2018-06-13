package it.polito.tdp.flightdelays.model;

import com.javadocmd.simplelatlng.LatLng;
import com.javadocmd.simplelatlng.LatLngTool;
import com.javadocmd.simplelatlng.util.LengthUnit;

public class Route implements Comparable<Route>{
	private Airport source;
	private Airport destination;
	private Airline airline;
	private double avg;
	private double peso;
	
	public Route(Airport source, Airport destination, Airline airline, double avg) {
		super();
		this.source = source;
		this.destination = destination;
		this.airline = airline;
		this.avg=avg;
		double denominatore = LatLngTool.distance(new LatLng(source.getLatitude(), source.getLongitude()), new LatLng(destination.getLatitude(), destination.getLongitude()), LengthUnit.KILOMETER);
		double peso = this.avg / denominatore;
	}

	public Airport getSource() {
		return source;
	}

	public Airport getDestination() {
		return destination;
	}

	public double getPeso() {
		return peso;
	}

	@Override
	public String toString() {
		return "\nSource: " + source + ", destination: " + destination + ", airline: " + airline + ", peso: " + peso
				+ "\n";
	}

	@Override
	public int compareTo(Route r) {
		// TODO Auto-generated method stub
		return (int) (r.peso-this.getPeso()); //decrescente
	}
	
	
	
}
