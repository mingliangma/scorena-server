package com.doozi.scorena

import com.doozi.scorena.Game;

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(Game)
class GameSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void "test something"() {
		when:
//		def today = new Date();
//		def weekLater = today + 7;
//		def upcomingGames = Game.findAllByDateBetween(today, weekLater)
//		def games = Game.findAll()
		def gameList = Game.list()
		
		then:
		gameList.taskInstanceList.size() == 0
		.size() > 0
		
    }
}
