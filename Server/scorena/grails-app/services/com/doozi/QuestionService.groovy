package com.doozi

import grails.transaction.Transactional

@Transactional
class QuestionService {

	def listQuestions(gameId) {
		def Games = Game.findAllById(gameId)
		
		def all = Games.collect {Game game ->
			[	
				question: game.question.collect{ Question q ->
					[
						id: q.id,
						content: q.content,
						pick1: q.pick1,
						pick2: q.pick2,
						pool: [
							pick1Amount: q.pool.pick1Amount,
							pick1NumPeople: q.pool.pick1NumPeople,
							pick2Amount: q.pool.pick2Amount,							
							pick2NumPeople: q.pool.pick1NumPeople
						]					
						]
				}
			]
		  }
		return all
	}
}

