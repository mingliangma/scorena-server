package com.doozi.scorena.gameengine.custom

import com.doozi.scorena.Pool
import com.doozi.scorena.Question
import com.doozi.scorena.QuestionContent

import org.springframework.transaction.annotation.Transactional

@Transactional
class CustomQuestionService {	
	
	def createCustomQuestion(String eventId, String content, String pick1, String pick2){
		QuestionContent qc = createCustomQuestionContent(content)
		if (qc == null)
			return [error: "create custom question content failed"]
		
		Question question= populateQuestions(pick1, pick2, eventId, qc)
		if (question==null){
			return [error: "create question failed"]
		}
		return [:]
	}
	
    def createCustomQuestionContent(String content) {
		
		def qc = QuestionContent.findByContent(content)
		if (qc){
			println "question content already exists. return existing one"
			return qc
		}
		
		def qc1 = new QuestionContent(questionType: QuestionContent.CUSTOM, content:content, sport: "soccer")
		if (qc1.save()){
			System.out.println("Custom Question Content Created successfully saved")
			return qc1
		}else{
			System.out.println("game save failed")
			qc1.errors.each{
				println it
			}
			return null
		}
    }
	
	def populateQuestions(String pick1, String pick2, String eventId, QuestionContent qc){

		def q = new Question(eventKey: eventId, pick1: pick1, pick2: pick2, pool: new Pool(minBet: 5))
		qc.addToQuestion(q)
		if (qc.save()){
			System.out.println("game successfully saved")
			return q
		}else{
			System.out.println("game save failed")
			qc.errors.each{
				println it
			}
			return null
		}
	}
	
	
	
		
}
