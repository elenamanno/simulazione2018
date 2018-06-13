package it.polito.tdp.flightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import it.polito.tdp.flightdelays.model.Airline;
import it.polito.tdp.flightdelays.model.Airport;
import it.polito.tdp.flightdelays.model.Flight;
import it.polito.tdp.flightdelays.model.Route;

public class FlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT id, airline from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getString("ID"), rs.getString("airline")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT id, airport, city, state, country, latitude, longitude FROM airports";
		List<Airport> result = new ArrayList<Airport>();
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getString("id"), rs.getString("airport"), rs.getString("city"),
						rs.getString("state"), rs.getString("country"), rs.getDouble("latitude"), rs.getDouble("longitude"));
				result.add(airport);
			}
			
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT id, airline, flight_number, origin_airport_id, destination_airport_id, scheduled_dep_date, "
				+ "arrival_date, departure_delay, arrival_delay, air_time, distance FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("id"), rs.getString("airline"), rs.getInt("flight_number"),
						rs.getString("origin_airport_id"), rs.getString("destination_airport_id"),
						rs.getTimestamp("scheduled_dep_date").toLocalDateTime(),
						rs.getTimestamp("arrival_date").toLocalDateTime(), rs.getInt("departure_delay"),
						rs.getInt("arrival_delay"), rs.getInt("air_time"), rs.getInt("distance"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Flight> loadAirlineFlights(Airline al, Airport ap1, Airport ap2) {
		List<Flight> result = new LinkedList<Flight>();
		String sql = "SELECT DISTINCT f.ID FROM flights as f WHERE AIRLINE=? AND ORIGIN_AIRPORT_ID=? AND DESTINATION_AIRPORT_ID=?";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, al.getId());
			st.setString(2, ap1.getId());
			st.setString(3, ap2.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				int id_query = Integer.parseInt(rs.getString("id"));
				for(Flight f : this.loadAllFlights()) {
					if(f.getId()==id_query) {
						result.add(f);
					}	
				}
				
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public double getAVG(Airline al, Airport ap1, Airport ap2) {
		double avg=0;
		String sql = "SELECT AVG(ARRIVAL_DELAY) as avg FROM flights as f WHERE AIRLINE=? AND ORIGIN_AIRPORT_ID=? AND DESTINATION_AIRPORT_ID=?";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, al.getId());
			st.setString(2, ap1.getId());
			st.setString(3, ap2.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) 
				avg = rs.getDouble("avg");
			System.out.println("La media e "+avg+"\n");
		     

			conn.close();
			return avg;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Route> getRoutes(Airline al) {
		List <Route> risultato= new ArrayList<>();
		String sql = "SELECT AVG(ARRIVAL_DELAY) as avg, ORIGIN_AIRPORT_ID, DESTINATION_AIRPORT_ID FROM flights as f WHERE AIRLINE=? GROUP BY ORIGIN_AIRPORT_ID, DESTINATION_AIRPORT_ID";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, al.getId());
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport ap1 = null;
				Airport ap2 =null;
				for(Airport a : this.loadAllAirports()) {
					if(a.getId().equals(rs.getString("ORIGIN_AIRPORT_ID")))
						ap1=a;
					if(a.getId().equals(rs.getString("DESTINATION_AIRPORT_ID")))
						ap2=a;
				}
				Route r = new Route(ap1, ap2, al, rs.getDouble("avg"));
				risultato.add(r);
			}

			conn.close();
			Collections.sort(risultato);
			return risultato;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
}
