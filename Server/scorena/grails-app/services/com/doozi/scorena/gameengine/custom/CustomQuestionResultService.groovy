package com.doozi.scorena.gameengine.custom

import grails.transaction.Transactional
import com.doozi.scorena.*
import com.doozi.scorena.processengine.CustomQuestionResult
import com.doozi.scorena.sportsdata.*

@Transactional
class CustomQuestionResultService {

    def getCustomQuestionResult(questionId) {
		return CustomQuestionResult.find("from CustomQuestionResult as cq where cq.questionId=?",[questionId.toInteger()])
    }
	
	def recordExist(questionId){
		def record = CustomQuestionResult.find("from CustomQuestionResult as cq where cq.questionId=?",[questionId.toInteger()])
		if (!record)
			return false
		else
			return true
	}
	
	def addCustomQuestionResult(int winnerPick,int questionId, String eventKey){
		if (winnerPick!=0 && winnerPick!=1 && winnerPick!=2)
			return [error: "INcorrect questionResult. Result can only be 0, 1, and 2"]
		
		def question = Question.findById(questionId)
		if (!question)
			return [error: "There is no question with given question ID"]
		
		if (question.eventKey != eventKey)	
			return [error: "The event does not contain the given question ID"]
			
			
		def customQuestionResult = new CustomQuestionResult(eventKey:eventKey, winnerPick:winnerPick, questionId:questionId )
		
		if (!customQuestionResult.save(failOnError:true)){
			return [error: "The custom question result failed to be saved"]
		}
		
		return [gameId: eventKey, question:[questionId: question.id,quesitonContent:question.questionContent.content, pick1:question.pick1, pick2:question.pick2],
			winnerPick: winnerPick]
		
	}
}
