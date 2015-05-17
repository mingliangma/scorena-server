package com.doozi.scorena.controllerservice

import java.util.Date;

import grails.test.mixin.TestFor
import grails.test.mixin.Mock
import spock.lang.Specification

import com.doozi.scorena.Question
import com.doozi.scorena.QuestionContent
import com.doozi.scorena.gamedata.dboutput.SportsDataService;
import com.doozi.scorena.gameengine.GameService;
import com.doozi.scorena.gameengine.QuestionService;
import com.doozi.scorena.sportsdata.ScorenaAllGames
import com.doozi.scorena.utils.HelperService;

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(QuestionService)
@Mock([Question, QuestionContent, ScorenaAllGames, SportsDataService, HelperService])
class QuestionServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

	
    void "when creating creating questions the questions are associated with gameId and questionContent"() {

		given:
		def gameMock = mockFor(GameService)
		Map upcomingGame1 = [
			    gameId : "l.mlsnet.com-2013-e.1861732",
			    away : [
			      score : null,
			      teamname : "San Jose Earthquakes"
			    ],
			    leagueName : "Major League Soccer",
			    gameStatus : "pre-event",
			    date : "2014-06-07 20:00:00.0 UTC",
			    home : [
			      score : null,
			      teamname : "Toronto FC "
			    ],
			    type : "soccer",
			    leagueCode : "l.mlsnet.com"
			]
		List upcomingGamesMock = []
		upcomingGamesMock.add(upcomingGame1)
		upcomingGamesMock.add(upcomingGame1)
		gameMock.demand.listUpcomingNonCustomGames{[list: {upcomingGame1}]}	
		gameMock.demand.static.logResults { List results ->  }
		service.gameService = gameMock.createMock()
		 
		
		when:
			questionContentInstance.save()
			def upcomingGames = service.createQuestions()

		then:
			upcomingGames == "hi"
			
		where:					
			questionContentInstance = new QuestionContent(content: "Who will win?", questionType: "team-0", sport:"soccer")
		


    }
}
