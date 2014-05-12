//command: -DnoTomcat=true run-script bootstrap-db-script/bootstrapScript.groovy
import com.doozi.scorena.Game
import com.doozi.scorena.GameEvent
import com.doozi.scorena.GameResult
import com.doozi.scorena.Pool
import com.doozi.scorena.Question
import com.doozi.scorena.QuestionContent
import com.doozi.scorena.Account
import com.doozi.scorena.controllerservice.GameService
import com.doozi.scorena.PoolTransaction
import com.doozi.scorena.BetResult
import com.doozi.scorena.User

import grails.converters.JSON
import grails.web.JSONBuilder
import groovy.util.slurpersupport.GPathResult

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import grails.plugins.rest.client.RestBuilder

import java.util.Date;



//bootstrapQuestionContent()
createQuestions()
//createUsers()
//simulateBetUpcoming()
//simulateBetPast()

println "create transactions ended"

def bootstrapQuestionContent(){
	def qc1 = new QuestionContent(questionType: QuestionContent.WHOWIN, content: "Who will win", sport: "soccer")
	
	String qc2Indicator = 2.5
	String qc2Content = "will total score be more than "+qc2Indicator+" goals"
	def qc2 = new QuestionContent(questionType: QuestionContent.SCOREGREATERTHAN, content: qc2Content, sport: "soccer", indicator1: qc2Indicator)
	
	qc2Indicator = 4.5
	qc2Content = "will total score be more than "+qc2Indicator+" goals"
	def qc3 = new QuestionContent(questionType: QuestionContent.SCOREGREATERTHAN, content: qc2Content, sport: "soccer", indicator1: qc2Indicator)
	
	if (qc1.save()){
		System.out.println("game successfully saved")
	}else{
		System.out.println("game save failed")
		qc1.errors.each{
			println it
		}
	}
	
	if (qc2.save()){
		System.out.println("game successfully saved")
	}else{
		System.out.println("game save failed")
		qc2.errors.each{
			println it
		}
	}
	
	if (qc3.save()){
		System.out.println("game successfully saved")
	}else{
		System.out.println("game save failed")
		qc2.errors.each{
			println it
		}
	}
}

def createQuestions(){
	println "create quesitons starts"
	def gameService = ctx.getBean("gameService")
	List upcomingGames = gameService.listUpcomingGames()
	List pastGames = gameService.listPastGames()
	
	println "upcomingGames: "+upcomingGames.size()
	println "pastGames: "+pastGames.size()
	
	for (int i=0; i < upcomingGames.size(); i++){
		def game = upcomingGames.get(i)
		println "game id: "+game.gameId
		if (Question.findByEventKey(game.gameId) == null){
			populateQuestions(game.away.teamname, game.home.teamname, game.gameId)
		}
	}
	
	for (int i=0; i < pastGames.size(); i++){
		def game = pastGames.get(i)
		println "game id: "+game.gameId
		if (Question.findByEventKey(game.gameId) == null){
			populateQuestions(game.away.teamname, game.home.teamname, game.gameId)
		}
	}
}

def populateQuestions(String away, String home, String eventId){

	def questionContent2 = QuestionContent.findAllByQuestionType("truefalse-0")
	for (QuestionContent qc: questionContent2){
		def q = new Question(eventKey: eventId, pick1: "Yes", pick2: "No", pool: new Pool(minBet: 5))
		qc.addToQuestion(q)
		if (qc.save()){
			System.out.println("game successfully saved")
		}else{
			System.out.println("game save failed")
			qc.errors.each{
				println it
			}
		}
	}
	
	def questionContent1 = QuestionContent.findAllByQuestionType("team-0")
	for (QuestionContent qc: questionContent1){
		def q = new Question(eventKey: eventId, pick1: home, pick2: away, pool: new Pool(minBet: 5))
		qc.addToQuestion(q)
		if (qc.save()){
			System.out.println("game successfully saved")
		}else{
			System.out.println("game save failed")
			qc.errors.each{
				println it
			}
		}
	}	
}




def simulateBetUpcoming(){


	def gameService = ctx.getBean("gameService")
	def betService = ctx.getBean("betService")
	
	
	Random random = new Random()
	def accounts = Account.findAll()
	def upcomingGames = gameService.listUpcomingGames()
	for (int i=0; i < upcomingGames.size(); i++){
		def upcomingGame = upcomingGames.get(i)				
		System.out.println("game away: "+upcomingGame.away.teamname + "   VS   game home: "+upcomingGame.home.teamname + "----- StartDate: "+upcomingGame.date)
		
		def questions = Question.findAllByEventKey(upcomingGame.gameId)
		
		
		for (Question q: questions){
			
			def questionId = q.id
			for (Account account: accounts){
				if (random.nextInt(2) == 1){
					continue
				}
				System.out.println("user name: "+account.username)
				int _wager =  (random.nextInt(6)+1)*5
				Date _time = new Date()
				int _pick
				
				if (random.nextInt(2)==0){
					_pick=1
				}else{
					_pick=2
				}
				
				
				betService.saveBetTrans(_wager, _time,_pick, account.userId, q.id)
			}
		}
	}
}

def simulateBetPast(){
	
	
		def gameService = ctx.getBean("gameService")
		def betService = ctx.getBean("betService")
		
		
		Random random = new Random()
		def accounts = Account.findAll()
		def upcomingGames = gameService.listPastGames()
		for (int i=0; i < upcomingGames.size(); i++){
			def upcomingGame = upcomingGames.get(i)
			System.out.println("game away: "+upcomingGame.away.teamname + "   VS   game home: "+upcomingGame.home.teamname + "----- StartDate: "+upcomingGame.date)
			
			def questions = Question.findAllByEventKey(upcomingGame.gameId)
			
			
			for (Question q: questions){
				if (random.nextInt(2) == 1){
					continue
				}
				def questionId = q.id
				Date _time = new Date() - (random.nextInt(6) + 18)
				for (Account account: accounts){
					if (random.nextInt(3) == 1){
						continue
					}
					
					System.out.println("user name: "+account.username)
					int _wager =  (random.nextInt(6)+1)*5
					_time = _time + random.nextInt(2)
					int _pick
					
					if (random.nextInt(2)==0){
						_pick=1
					}else{
						_pick=2
					}
					
					
					betService.saveBetTrans(_wager, _time,_pick, account.userId, q.id)
				}
			}
		}
	}
	

def createUser(String _username, String _email, String _password, String _gender, String _region){
	
	def userService = ctx.getBean("userService")
    def resp = userService.createUser(_username, _email, _password, _gender, _region)
	println resp
	
}

def createUsers(){
	Random random = new Random()
	String _displayName
	String _emai
	String _password
	String _gender
	String _region
	
	for (int i=0; i<10; i++){
		def num = random.nextInt(10000)
		 _displayName  = "scorena"+num.toString()
		 _email = _displayName+"@gmail.com"
		 _password = "12345"
		 _gender = "male"
		 _region = "Japan"	
		createUser(_displayName, _email, _password, _gender, _region)
	}
	println "create users ended"
}


def createGame(String _league, String _away, String _home, String _type, String _country, Date _theDate, int _awayScore, int _homeScore, int _pick1Amount,
	int _pick1NumPeople, int _pick2Amount, int _pick2NumPeople){
			
			println "create game starts..."
			Random random = new Random()
			
			//def newdate = new Date().parse("d/M/yyyy H:m:s", _theDate)
			String _awayTeamId = random.nextInt(10000).toString()
			String _homeTeamId = random.nextInt(10000).toString()
			String _gameEventId = random.nextInt(10000).toString()
			String _siteKey = random.nextInt(1000).toString()
			Date currentDate = new Date()
			
			def game = new Game(gameEventId: _gameEventId, league: _league, homeTeamId:_homeTeamId, homeTeamNameFirst:_home, awayTeamId: _awayTeamId, awayTeamNameFirst: _away,
				city: "London", siteKey:_siteKey, siteName: "DW Stadium",type:_type, country:_country, startDate:_theDate, createdAt: currentDate)
			System.out.println("game: "+game.id)
			def q1 = new Question(pick1: _home, pick2: _away, content:"who will win between the two",
				pool: new Pool(minBet: 5))
			def q2 = new Question(pick1: _home, pick2: _away, content:"who will score the first goal",
				pool: new Pool(minBet: 5))
			
			game.addToQuestion(q1);
			game.addToQuestion(q2);

		
			if (game.save()){
				System.out.println("game successfully saved")
			}else{
				System.out.println("game save failed")
				game.errors.each{
					println it
				}
			}
}




				