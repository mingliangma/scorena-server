package com.doozi.scorena.utils

import com.doozi.scorena.transaction.LeagueTypeEnum;

import org.springframework.transaction.annotation.Transactional

class SportsDataHelperService {
	
	static transactional = false
	
	final static String PREMIER_LEAGUE = "l.premierlea"
	final static String CHAMP_LEAGUE = "l.uefa.org."
	final static String BRAZIL_SERIES_A = "l.cbf.br.ser"
	final static String CALCIO_SERIES_A = "l.lega-calci"
	final static String LA_LIGA= "l.lfp.es.pri"
	final static String MLS = "l.mlsnet.com"
	final static String WORLD_CUP = "l.fifaworldc"
	final static String NBA = "nba"
	final static String MLB = "mlb"
	final static String CFL = "cfl"
	final static String FRENCHOPEN = "frenchopen"
	final static String NBADRAFT = "nbadraft"
	static final String CUSTOM_TEAM_PREFIX = "customteam-"
	static final String CUSTOM_EVENT_PREFIX = "customevent-"
	
	final static int PREEVENT = 0
	final static int POSTEVENT = 3
	final static int INTERMISSION = 2
	final static int MIDEVENT = 1
	final static int ALLEVENT = 4
	
	
	static int UPCOMING_DATE_RANGE = 14
	static int PAST_DATE_RANGE = 14
	
	public String getLeagueNameFromEventKey(String eventKey){

		if (eventKey.startsWith(PREMIER_LEAGUE))
			return "Premier League"
		else if (eventKey.startsWith(CHAMP_LEAGUE)) 
			return "UEFA Champions League"
		else if (eventKey.startsWith(BRAZIL_SERIES_A))
			return "Brazil league Series A"
		else if (eventKey.startsWith(CALCIO_SERIES_A))
			return "Calcio Series A"
		else if (eventKey.startsWith(LA_LIGA))
			return "La Liga"
		else if (eventKey.startsWith(MLS))
			return "Major League Soccer"
		else if (eventKey.startsWith(WORLD_CUP))
			return "World Cup 2014"
		else if (eventKey.startsWith(NBADRAFT))
			return "NBA Draft"
		else if (eventKey.startsWith(NBA))
			return "NBA"
		else if (eventKey.startsWith(MLB))
			return "MLB"
		else if (eventKey.startsWith(CFL))
			return "CFL"
		else if (eventKey.startsWith(FRENCHOPEN))
			return "French Open"
		else if (eventKey.startsWith(NBADRAFT))
			return "NBA Draft"
		else if (eventKey.startsWith(CUSTOM_EVENT_PREFIX))
			return "Launch Party"
	}
	
	public LeagueTypeEnum getLeagueCodeFromEventKey(String eventKey){
		if (eventKey.startsWith(PREMIER_LEAGUE))
			return LeagueTypeEnum.EPL
		else if (eventKey.startsWith(CHAMP_LEAGUE))
			return LeagueTypeEnum.CHAMP
		else if (eventKey.startsWith(BRAZIL_SERIES_A))
			return LeagueTypeEnum.CBF
		else if (eventKey.startsWith(CALCIO_SERIES_A))
			return LeagueTypeEnum.LEGA
		else if (eventKey.startsWith(LA_LIGA))
			return LeagueTypeEnum.LFP
		else if (eventKey.startsWith(MLS))
			return LeagueTypeEnum.MLS
		else if (eventKey.startsWith(WORLD_CUP))
			return LeagueTypeEnum.WORLDCUP
		else if (eventKey.startsWith(NBADRAFT))
			return LeagueTypeEnum.NBADRAFT
		else if (eventKey.startsWith(NBA))
			return LeagueTypeEnum.NBA
		else if (eventKey.startsWith(MLB))
			return LeagueTypeEnum.MLB
		else if (eventKey.startsWith(CFL))
			return LeagueTypeEnum.CFL
		else if (eventKey.startsWith(FRENCHOPEN))
			return LeagueTypeEnum.FRENCHOPEN		
		else if (eventKey.startsWith(CUSTOM_EVENT_PREFIX))
			return CUSTOM_EVENT_PREFIX
	}
	
	public LeagueTypeEnum getLeagueEnumFromLeagueString(String leagueType){
		if (leagueType.toUpperCase() == LeagueTypeEnum.EPL.toString())
			return LeagueTypeEnum.EPL
		else if (leagueType.toUpperCase() == LeagueTypeEnum.CHAMP.toString())
			return LeagueTypeEnum.CHAMP
		else if (leagueType.toUpperCase() == LeagueTypeEnum.CBF.toString())
			return LeagueTypeEnum.CBF
		else if (leagueType.toUpperCase() == LeagueTypeEnum.LEGA.toString())
			return LeagueTypeEnum.LEGA
		else if (leagueType.toUpperCase() == LeagueTypeEnum.LFP.toString())
			return LeagueTypeEnum.LFP
		else if (leagueType.toUpperCase() == LeagueTypeEnum.MLS.toString())
			return LeagueTypeEnum.MLS
		else if (leagueType.toUpperCase() == LeagueTypeEnum.WORLDCUP.toString())
			return LeagueTypeEnum.WORLDCUP
		else if (leagueType.toUpperCase() == LeagueTypeEnum.NBA.toString())
			return LeagueTypeEnum.NBA
		else if (leagueType.toUpperCase() == LeagueTypeEnum.MLB.toString())
			return LeagueTypeEnum.MLB
		else if (leagueType.toUpperCase() == LeagueTypeEnum.CFL.toString())
			return LeagueTypeEnum.CFL
		else if (leagueType.toUpperCase() == LeagueTypeEnum.FRENCHOPEN.toString())
			return LeagueTypeEnum.FRENCHOPEN
		else if (leagueType.toUpperCase() == LeagueTypeEnum.NBADRAFT.toString())
			return LeagueTypeEnum.NBADRAFT
	}
	
	public String getLeaguePrefixFromLeagueEnum(LeagueTypeEnum leagueTypeEnum){
		if (leagueTypeEnum == LeagueTypeEnum.EPL)
			return PREMIER_LEAGUE
		else if (leagueTypeEnum == LeagueTypeEnum.CHAMP)
			return CHAMP_LEAGUE
		else if (leagueTypeEnum== LeagueTypeEnum.CBF)
			return BRAZIL_SERIES_A
		else if (leagueTypeEnum == LeagueTypeEnum.LEGA)
			return CALCIO_SERIES_A
		else if (leagueTypeEnum == LeagueTypeEnum.LFP)
			return LA_LIGA
		else if (leagueTypeEnum == LeagueTypeEnum.MLS)
			return MLS
		else if (leagueTypeEnum == LeagueTypeEnum.WORLDCUP)
			return WORLD_CUP
		else if (leagueTypeEnum == LeagueTypeEnum.NBA)
			return NBA
		else if (leagueTypeEnum == LeagueTypeEnum.MLB)
			return MLB
		else if (leagueTypeEnum == LeagueTypeEnum.CFL)
			return CFL
		else if (leagueTypeEnum == LeagueTypeEnum.FRENCHOPEN)
			return FRENCHOPEN
		else if (leagueTypeEnum == LeagueTypeEnum.NBADRAFT)
			return NBADRAFT
	}
	
	public isLeagueSupport(String league){
		for (LeagueTypeEnum c : LeagueTypeEnum.values()) {
			if (c.name().equalsIgnoreCase(league)) {
				return true;
			}
		}
	
		return false;
	}
}
