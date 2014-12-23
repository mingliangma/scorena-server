package com.doozi.scorena.gameengine.custom

import com.doozi.scorena.Pool
import com.doozi.scorena.Question
import com.doozi.scorena.QuestionContent
import com.mysql.jdbc.log.Log;

import org.springframework.transaction.annotation.Transactional

@Transactional
class CustomQuestionService {	
	
	def createCustomQuestion(String eventId, String content, String pick1, String pick2){
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
		
		log.info "createCustomQuestion(): ends"
		
		return [:]
	}
	
    def createCustomQuestionContent(String content) {
		log.info "createCustomQuestionContent(): begins with content = ${content}"
		
		def qc = QuestionContent.findByContent(content)
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
	
	
	
		
}
