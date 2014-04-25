package com.doozi.scorena.controllerservice

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(SportsDataService)
class ViewServiceSpec extends Specification {
	def sportsDataService
    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
		when
			def gameList = sportsDataService.getUpcomingMatches()
			
		then
			gameList.collect().size() > 0
    }	
}
