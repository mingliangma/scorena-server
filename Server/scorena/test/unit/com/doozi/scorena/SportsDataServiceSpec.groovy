package com.doozi.scorena

import com.doozi.scorena.controllerservice.SportsDataService;

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(SportsDataService)
class SportsDataServiceSpec extends Specification {
	def sportsDataService
    def setup() {
    }

    def cleanup() {
    }

    void "test upcoming games"() {
		when:
			println "test starts"
			def gameList = service.getAllUpcomingGames()
			
		then:
			gameList.collect().size() == 0
    }
	
	void "testPastGame"(){
		when:
			def pastgamelist = service.getAllPastGames()
			
		then:
			pastgamelist.size() > 0
	}	
}
