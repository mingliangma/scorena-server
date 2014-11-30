package com.doozi.scorena.admin

import com.doozi.scorena.Account
import com.doozi.scorena.Question
import com.doozi.scorena.utils.AccountType
import grails.transaction.Transactional

@Transactional
class SimulateCommentService {
	def commentService
	
	List messageList = [
		"I went digging through my garage the other day. My aim was to fish out all the old sports equipment I could find.",
		"Hamilton Tiger-Cats slotback Andy Fantuz of Chatham smiles after catching a ball during Wednesday's practice for the Grey Cup in Vancouver.",
		"Lauren Hill's Basketball Debut",
		"Kitchener Lady Rangers Peewee B player Tanis Hall, centre, drives for the net to attempt a shot on Grand River Mustangs goalie Jacobi",
		"It strikes me that RBC Sports Day in Canada is a chance to recapture, indeed celebrate, the sweet magic that the items I found again in my garage, and those in so many similar garages and basements around the nation, have the potential to conjure up.",
		"There may not be excessive worrying about winning and losing. Contracts and ticket prices will most definitely not come into play. The tendency will be to participate and not just to watch. Overbearing coaches and demanding parents just might, for once, be in the minority. "+
		"Concussions and cheating could finally be relegated to the back burner.Perhaps RBC Sports Day in Canada will get closer to what this is really meant to represent.The basic and all too obvious objects of our fascination with athletic games can often linger, hidden away in storage"+
		"cupboards far from the actual playing fields. They can be underused and sometimes taken for granted if not forgotten altogether. It's true they are symbols of healthy living, community spirit and the acquired skills which make us better teammates and leaders."
		]
    def simulateComment() {
		println "simulateComment() started"
		//get a random user
		Random random = new Random()
		List<Account> accounts = Account.findAllByAccountType(AccountType.TEST)
		String userId = accounts[random.nextInt(accounts.size())].userId
		List<Question> questionList = Question.executeQuery('from Question order by rand()', [max: 10])
		
		for (Question q :questionList ){
			commentService.writeComments(userId,messageList[random.nextInt(messageList.size())], q.id)
		}
		
		println "simulateComment() ended"
    }
}
