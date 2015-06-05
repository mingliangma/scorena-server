package com.doozi.scorena.sportsdata

import java.util.Date;

import com.doozi.scorena.transaction.LeagueTypeEnum;
import com.doozi.scorena.sportsdata.GameAbstract
import org.springframework.validation.Errors
class GameBaseball extends GameAbstract{
	
	//preevent
	String probablePitcherNameDisplayRoster //away_probable_pitcher.name_display_roster
	String probablePitcherFirstName //away_probable_pitcher.first
	String probablePitcherLastName //away_probable_pitcher.last
	String probablePitcherWins //away_probable_pitcher.wins
	String probablePitcherLosses //away_probable_pitcher.losses
	String probablePitcherERA // away_probable_pitcher.era       (stand for Earned Run Average)
	String probablePitcherId //away_probable_pitcher.id
	String probablePitcherNumber //away_probable_pitcher.number
	String eventKeyDup
	
	//midevent, postevent
	String homeRun 		//linescore.hr
	String errorsCount 		//linescore.e
	String strikeOuts 	//linescore.so
	String runsScored	//linescore.r
	String stolenBases	//linescore.sb
	String hits			//linescore.h
	String innings		//format: 0-1-2-3-0-0-0-2-0
	
	
	static constraints = {		
		probablePitcherNameDisplayRoster nullable: true
		probablePitcherFirstName nullable: true
		probablePitcherLastName nullable: true
		probablePitcherWins nullable: true
		probablePitcherLosses nullable: true
		probablePitcherERA nullable: true
		probablePitcherId nullable: true
		probablePitcherNumber nullable: true		
		homeRun nullable: true
		errorsCount nullable: true
		strikeOuts nullable: true
		runsScored nullable: true
		stolenBases nullable: true
		hits nullable: true
		innings nullable: true
	}
	
	static mapping = {
		datasource 'sportsData'
	}
}
