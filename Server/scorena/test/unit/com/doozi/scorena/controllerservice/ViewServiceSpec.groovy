package com.doozi.scorena.controllerservice

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(ViewService)
class ViewServiceSpec extends Specification {
	def viewService
    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
		when
			def gameList = viewService.getUpcomingMatches()
			
		then
			gameList.collect().size() > 0
    }	
}
