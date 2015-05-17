package com.doozi.scorena.gameengine.custom

import com.doozi.scorena.Pool
import com.doozi.scorena.Question
import com.doozi.scorena.QuestionContent
import com.mysql.jdbc.log.Log;
import com.doozi.scorena.utils.*
import com.doozi.scorena.Account

import grails.plugins.rest.client.RestBuilder
import org.springframework.transaction.annotation.Transactional

@Transactional
class CustomQuestionService {	
	
	def pushService
	def gameService
	def simulateBetService
	
	def createCustomQuestion(String eventId, String content, String pick1, String pick2){
		def rest = new RestBuilder()
		
		log.info "createCustomQuestion(): begins with eventId = ${eventId}, pick1 = ${pick1}, pick2 = ${pick2}"
		
		QuestionContent qc = createCustomQuestionContent(content)
		if (qc == null){
			log.error "createCustomQuestion(): create custom question content failed" 
			return [error: "create custom question content failed"]
		}
		
		Question question= populateQuestions(pick1, pick2, eventId, qc)
		if (question==null){
			log.error "createCustomQuestion(): create question failed"
			return [error: "create question failed"]
		}
		println "quesiton content id="+qc.id
		println "quesiton  id="+question.id
		log.info "createCustomQuestion(): ends"
		def game = gameService.getGame(question.eventKey)
		String status = game.gameStatus
		String parse_channel = eventId.replace(".","_")
		String message = content + " "+pick1 + " or " + pick2
		pushService.customQuestionPush(rest, parse_channel,status, message)
		
		return [questionContentId:qc.id]
	}
	
    def createCustomQuestionContent(String content) {
		log.info "createCustomQuestionContent(): begins with content = ${content}"
		
		def qc = QuestionContent.findByContentAndQuestionType(content, QuestionContent.CUSTOM)
		if (qc){
			println "question content already exists. return existing one"
			log.info "createCustomQuestionContent(): question content already exists. return existing one"
			log.info "createCustomQuestionContent(): ends with qc = ${qc}"
			return qc
		}
		
		def qc1 = new QuestionContent(questionType: QuestionContent.CUSTOM, content:content, sport: "soccer")
		if (qc1.save()){
			System.out.println("Custom Question Content Created successfully saved")
			log.info "createCustomQuestionContent(): Custom Question Content Created successfully saved"
			log.info "createCustomQuestionContent(): ends with qc1 = ${qc1}"
			return qc1
		}else{
			System.out.println("game save failed")
			log.error "createCustomQuestionContent(): game save failed"
			qc1.errors.each{
				println it
				log.info "createCustomQuestionContent(): ${it}"
			}
			return null
		}
    }
	
	def populateQuestions(String pick1, String pick2, String eventId, QuestionContent qc){
		log.info "populateQuestions(): begins with pick1 = ${pick1}, pick2 = ${pick2}, eventId = ${eventId}, qc = ${qc}"

		def q = new Question(eventKey: eventId, pick1: pick1, pick2: pick2, pool: new Pool(minBet: 5))
		qc.addToQuestion(q)
		if (qc.save()){
			System.out.println("game successfully saved")
			log.info "populateQuestions(): game successfully saved"
			log.info "populateQuestions(): ends with q = ${q}"
			return q
		}else{
			System.out.println("game save failed")
			log.error "populateQuestions(): game save failed"
			qc.errors.each{
				println it
				log.info "populateQuestions(): ${it}"
			}
			return null
		}
	}
	
	def simulateBetCustomQuestion(String eventKey, long quesitonContentId){
		log.info "simulateBetCustomQuestion(): begins with quesitonContentId: "+quesitonContentId + " and eventKey = "+eventKey
		Question q = Question.find("from Question as q where eventKey=? and q.questionContent.id=?", [eventKey, quesitonContentId])

		List accounts = Account.findAll("from Account as a where a.accountType=? and a.currentBalance>?", [AccountType.TEST, 100])
		double m = (Math.ceil(accounts.size()/2))
		List pick1Accounts = accounts.subList(0, (int) Math.ceil(accounts.size()/2))
		List pick2Accounts = accounts.subList((int) Math.ceil(accounts.size()/2), accounts.size())

		Random r = new Random()
		simulateBetService.simulateBetOnAQuestion(q, pick1Accounts, r, 1, true)
		simulateBetService.simulateBetOnAQuestion(q, pick2Accounts, r, 2, true)
		log.info "simulateBetCustomQuestion(): ends"
	}
	
		
}
