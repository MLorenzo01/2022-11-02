package it.polito.tdp.itunes.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import it.polito.tdp.itunes.model.Album;
import it.polito.tdp.itunes.model.Artist;
import it.polito.tdp.itunes.model.Edge;
import it.polito.tdp.itunes.model.Genre;
import it.polito.tdp.itunes.model.MediaType;
import it.polito.tdp.itunes.model.Playlist;
import it.polito.tdp.itunes.model.Track;

public class ItunesDAO {
	private TreeMap<Integer, Track> idMap;
	
	public TreeMap<Integer, Track> map(){
		return idMap;
	}
	
	public List<Album> getAllAlbums(){
		final String sql = "SELECT * FROM Album";
		List<Album> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Album(res.getInt("AlbumId"), res.getString("Title")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Artist> getAllArtists(){
		final String sql = "SELECT * FROM Artist";
		List<Artist> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Artist(res.getInt("ArtistId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Playlist> getAllPlaylists(){
		final String sql = "SELECT * FROM Playlist";
		List<Playlist> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Playlist(res.getInt("PlaylistId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<Track> getAllTracks(){
		final String sql = "SELECT * FROM Track";
		List<Track> result = new ArrayList<Track>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Track(res.getInt("TrackId"), res.getString("Name"), 
						res.getString("Composer"), res.getInt("Milliseconds"), 
						res.getInt("Bytes"),res.getDouble("UnitPrice")));
			
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public ArrayList<Track> getTracks(String genere, int min, int max){
		idMap = new TreeMap<>();
		final String sql = "SELECT t.TrackId, t.Name, t.Composer, t.Milliseconds, t.Bytes, t.UnitPrice "
				+ "FROM genre g, track t "
				+ "WHERE g.Name = ? AND t.Milliseconds > ? AND t.Milliseconds < ? AND g.GenreId = t.GenreId";
		ArrayList<Track> result = new ArrayList<Track>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			st.setInt(2, min);
			st.setInt(3, max);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				Track track = new Track(res.getInt("TrackId"), res.getString("Name"), 
						res.getString("Composer"), res.getInt("Milliseconds"), 
						res.getInt("Bytes"),res.getDouble("UnitPrice"));
				result.add(track);
				idMap.put(track.getTrackId(), track);
				
			
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public ArrayList<Edge> getCoppie(String genere, int min, int max){
		final String sql = "SELECT DISTINCT t.TrackId as t1, t2.TrackId AS t2, t.num1 as num1, t2.num2 "
				+ "FROM (SELECT t1.TrackId, COUNT(pt1.PlaylistId) AS num1 "
				+ "		FROM track t1, genre g, playlisttrack pt1 "
				+ "		WHERE g.Name = ? AND t1.Milliseconds > ? AND t1.Milliseconds < ? AND g.GenreId = t1.GenreId AND pt1.TrackId = t1.TrackId "
				+ "		GROUP BY t1.TrackId) t, "
				+ "		(SELECT t3.TrackId, COUNT(pt2.PlaylistId) AS num2 "
				+ "		FROM track t3, genre g, playlisttrack pt2 "
				+ "		WHERE g.Name = ? AND t3.Milliseconds > ? AND t3.Milliseconds < ? AND g.GenreId = t3.GenreId AND pt2.TrackId = t3.TrackId "
				+ "		GROUP BY t3.TrackId) t2 "
				+ "WHERE t.TrackId < t2.TrackId AND t.num1 = t2.num2";
		ArrayList<Edge> result = new ArrayList<Edge>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, genere);
			st.setInt(2, min);
			st.setInt(3, max);
			st.setString(4, genere);
			st.setInt(5, min);
			st.setInt(6, max);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Edge(idMap.get(res.getInt("t1")),idMap.get(res.getInt("t2")), res.getInt("num1")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public ArrayList<Genre> getAllGenres(){
		final String sql = "SELECT * FROM Genre";
		ArrayList<Genre> result = new ArrayList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new Genre(res.getInt("GenreId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}
	
	public List<MediaType> getAllMediaTypes(){
		final String sql = "SELECT * FROM MediaType";
		List<MediaType> result = new LinkedList<>();
		
		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();

			while (res.next()) {
				result.add(new MediaType(res.getInt("MediaTypeId"), res.getString("Name")));
			}
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("SQL Error");
		}
		return result;
	}

	
	
}
