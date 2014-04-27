package com.doozi.scorena.sportsdata

import java.io.Serializable;
import java.util.Date;

class ScorenaAllGames  implements Serializable{
	String eventKey
	String fullName
	String teamKey
	String eventStatus
	String alignment
	String score
	Date startDateTime
	Date lastUpdate
	
	private static final String premierLeague = "%premier%"
	private static final String champLeague = "%champion%"
	private static final String brazilSerieA = "%cbf.br.seriea%"	
	private static final String calcioSerieA = "%lega-calcio%"
	private static final String laLiga = "%lfp.es%"	
	private static final String Mls = "%mlsne%"
	
	static mapping = {
		datasource 'sportsData'
		table 'scorena_all_games'
		version false
		eventKey column : "event_key"
		id composite: ['eventKey', 'teamKey']		
	}
	
    static constraints = {
    }
	
	
}
