package it.polito.tdp.itunes.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.itunes.db.ItunesDAO;

public class Model {
	ItunesDAO dao;
	SimpleGraph<Track, DefaultEdge> graph;
	ArrayList<Track> best;
	int secondi = 0;
	Set<Track> maggiore;
	
	public Model() {
		this.dao = new ItunesDAO();
		this.best = new ArrayList<>();
	}
	
	public ArrayList<Genre> getGenre(){
		return dao.getAllGenres();
	}
	public String creaGrafo(String genere, int min, int max) {
		graph = new SimpleGraph<>(DefaultEdge.class);
		ArrayList<Track> canzoni = dao.getTracks(genere, min*1000, max*1000);
		Graphs.addAllVertices(graph, canzoni);
		
		ArrayList<Edge> coppie = dao.getCoppie(genere, min * 1000, max*1000);
		
		for(Edge c: coppie) {
			graph.addEdge(c.getT1(), c.getT2());
		}
		System.out.println("il grafo ha " + graph.vertexSet().size() + " vertici e " + graph.edgeSet().size() + " archi");
		
		String result = "";
		
		ConnectivityInspector<Track, DefaultEdge> insp = new ConnectivityInspector<>(this.graph);
		
		List<Set<Track>> componenti = insp.connectedSets();
		maggiore = new TreeSet<>();
		int mag = 0;
		for(Set<Track> t : componenti) {
			if(t.size() > mag) {
				maggiore = t;
				mag = t.size();
			}
		}
		
		for(Set<Track> t : componenti) {
			int nP = -1;
			for(Track tr : t) {
				for(Edge e: coppie) {
					if(e.getT1().equals(tr) || e.getT2().equals(tr))
						nP = e.num;
				}
			}
			result += "Componente con " + t.size() + " archi " + "inseriti in " + nP +" playlist \n";
		}
		
		return result;
	}
	
	public ArrayList<Track> trovaMaggiore(int DTot){
		int ms = DTot*60*1000;
		ArrayList<Track> parziale = new ArrayList<>();
		int sec = 0;
		cerca(parziale, sec, ms);
		return best;
	}

	private void cerca(ArrayList<Track> parziale, int sec, int ms) {
		if(parziale.size() > best.size()) {
			best = new ArrayList<>(parziale);
		}
		for(Track t: maggiore) {
			if(!parziale.contains(t) && (sec+t.getMilliseconds()) < ms) {
				parziale.add(t);
				sec += t.getMilliseconds();
				cerca(parziale, sec, ms);
				parziale.remove(t);
				sec -=t.getMilliseconds();
			}
		}
	}
	public TreeMap<Integer, Track> map(){
		return dao.map();
	}
	
	
}
