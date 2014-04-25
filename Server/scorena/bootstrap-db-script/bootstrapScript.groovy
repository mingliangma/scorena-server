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



bootstrapQuestionContent()
createQuestions()
createUsers()
simulateBetUpcoming()
simulateBetPast()

println "create transactions ended"

def bootstrapQuestionContent(){
	def qc1 = new QuestionContent(questionType: QuestionContent.WHOWIN, content: "Who will win", sport: "soccer")
	
	String qc2Indicator = 2.5
	String qc2Content = "will total score be more than "+qc2Indicator+" goals"
	def qc2 = new QuestionContent(questionType: QuestionContent.SCOREGREATERTHAN, content: qc2Content, sport: "soccer", indicator1: qc2Indicator)
	
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
}

def createQuestions(){
	println "create quesitons starts"
	def gameService = ctx.getBean("gameService")
	List upcomingGames = gameService.listUpcomingGames()
	List pastGames = gameService.listPastGames()
	
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

	def q1 = new Question(eventKey: eventId, pick1: home, pick2: away, pool: new Pool(minBet: 5))
	def q2 = new Question(eventKey: eventId, pick1: home, pick2: away, pool: new Pool(minBet: 5))
	
	def questionContent1 = QuestionContent.findByQuestionType("team-0")
	def questionContent2 = QuestionContent.findByQuestionType("truefalse-0")
	questionContent1.addToQuestion(q1)
	questionContent2.addToQuestion(q2)
	
	if (questionContent1.save()){
		System.out.println("game successfully saved")
	}else{
		System.out.println("game save failed")
		questionContent1.errors.each{
			println it
		}
	}
	
	if (questionContent2.save()){
		System.out.println("game successfully saved")
	}else{
		System.out.println("game save failed")
		questionContent2.errors.each{
			println it
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
				
				def questionId = q.id
				Date _time = new Date() - (random.nextInt(6) + 10)
				for (Account account: accounts){
					
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
	
def simulateBet(){
	
	
	def gameService = ctx.getBean("gameService")
	def betService = ctx.getBean("betService")
	
	
	Random random = new Random()
	def accounts = Account.findAll()
	
	
	
	def upcomingGames = gameService.getUpcomingGameObjects()
	for (Game upcomingGame: upcomingGames){
		System.out.println("game away: "+upcomingGame.awayTeamNameFirst + "   VS   game home: "+upcomingGame.homeTeamNameFirst + "----- StartDate: "+upcomingGame.startDate)
		System.out.println("quesiton : "+upcomingGame.question)
		for (Question q: upcomingGame.question){
			System.out.println("question: "+q.content)
			def questionId = q.id
			for (Account account: accounts){
				
				System.out.println("user name: "+account.username)
				int _wager =  (random.nextInt(6)+1)*5
				Date _time = new Date()
				int _pick
				
				if (random.nextInt(2)==0){
					_pick=1
				}else{
					_pick=2
				}
				
				
				betService.saveBetTrans(_wager, _time,_pick, account.userId, q.id, upcomingGame.id)
			}
		}
	}
}

def createUser(String _username, String _email, String _password, String _gender, String _region){
	
	def userService = ctx.getBean("userService")
    def resp = userService.createUser(_username, _email, _password, _gender, _region)
	println resp
	
}

def createPastGames(){
	String _league = "EPL"
	String _away = "Liverpool FC"
	String _home ="Man United"
	String _type ="soccer"
	String _country = "england"
	Date _theDate =new Date() -2
	int _awayScore =0
	int _homeScore =0
	int _pick1Amount =0
	int _pick1NumPeople =0
	int _pick2Amount =0
	int _pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
	
	_league = "EPL"
	_away = "Liverpool FC"
	_home ="Stoke City"
	_type ="soccer"
	_country = "england"
	_theDate =new Date() - 5
	_awayScore =0
	_homeScore =0
	_pick1Amount =0
	_pick1NumPeople =0
	_pick2Amount =0
	_pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
}

def createGames(){
	Random random = new Random()

	String _league = "EPL"
	String _away = "Liverpool FC"
	String _home ="Man United"
	String _type ="soccer"
	String _country = "england"
	Date _theDate =new Date() + random.nextInt(7)
	int _awayScore =0
	int _homeScore =0
	int _pick1Amount =0
	int _pick1NumPeople =0
	int _pick2Amount =0
	int _pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
	
	_league = "EPL"
	_away = "Liverpool FC"
	_home ="Stoke City"
	_type ="soccer"
	_country = "england"
	_theDate =new Date() + random.nextInt(7)
	_awayScore =0
	_homeScore =0
	_pick1Amount =0
	_pick1NumPeople =0
	_pick2Amount =0
	_pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
	
	_league = "EPL"
	_away = "Arsenal FC"
	_home ="Aston Villa"
	_type ="soccer"
	_country = "england"
	_theDate =new Date() + random.nextInt(7)
	_awayScore =0
	_homeScore =0
	_pick1Amount =0
	_pick1NumPeople =0
	_pick2Amount =0
	_pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
	
	_league = "EPL"
	_away = "Norwich City"
	_home ="Everton FC"
	_type ="soccer"
	_country = "england"
	_theDate =new Date() + random.nextInt(7)
	_awayScore =0
	_homeScore =0
	_pick1Amount =0
	_pick1NumPeople =0
	_pick2Amount =0
	_pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
	
	_league = "EPL"
	_away = "Sunderland AFC"
	_home ="Fulham FC"
	_type ="soccer"
	_country = "england"
	_theDate =new Date() + random.nextInt(7)
	_awayScore =0
	_homeScore =0
	_pick1Amount =0
	_pick1NumPeople =0
	_pick2Amount =0
	_pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)

	_league = "EPL"
	_away = "Liverpool FC"
	_home ="Stoke City"
	_type ="soccer"
	_country = "england"
	_theDate =new Date() + random.nextInt(7)
	_awayScore =0
	_homeScore =0
	_pick1Amount =0
	_pick1NumPeople =0
	_pick2Amount =0
	_pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
	
	_league = "EPL"
	_away = "Arsenal FC"
	_home ="Aston Villa"
	_type ="soccer"
	_country = "england"
	_theDate =new Date() + random.nextInt(7)
	_awayScore =0
	_homeScore =0
	_pick1Amount =0
	_pick1NumPeople =0
	_pick2Amount =0
	_pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
	
	_league = "EPL"
	_away = "Norwich City"
	_home ="Everton FC"
	_type ="soccer"
	_country = "england"
	_theDate =new Date() + random.nextInt(7)
	_awayScore =0
	_homeScore =0
	_pick1Amount =0
	_pick1NumPeople =0
	_pick2Amount =0
	_pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
	
	_league = "EPL"
	_away = "Sunderland AFC"
	_home ="Fulham FC"
	_type ="soccer"
	_country = "england"
	_theDate =new Date() + random.nextInt(7)
	_awayScore =0
	_homeScore =0
	_pick1Amount =0
	_pick1NumPeople =0
	_pick2Amount =0
	_pick2NumPeople =0
	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)

	println "create game ended"
}

def createUsers(){
	
	String _displayName  = "michealLiu1"
	String _email = "micheal1@gmail.com"
	String _password = "11111111"
	String _gender = "male"
	String _region = "Japan"
	
	createUser(_displayName, _email, _password, _gender, _region)
	
	 _displayName  = "Joey"
	 _email = "joey@gmail.com"
	 _password = "11111111"
	_gender = "male"
	_region = "Toronto"
	 
	 createUser(_displayName, _email, _password, _gender, _region)
	
	 _displayName  = "Ming"
	 _email = "ming@gmail.com"
	 _password = "11111111"
	_gender = "male"
	 _region = "Toronto"
	 
	 createUser(_displayName, _email, _password, _gender, _region)

	
	 _displayName  = "Kyle"
	 _email = "kyle@gmail.com"
	 _password = "11111111"
	 _gender = "male"
	 _region = "Toronto"
	 
	 createUser(_displayName, _email, _password, _gender, _region)
	
	 _displayName  = "Heng"
	 _email = "heng@gmail.com"
	 _password = "11111111"
	
	 _gender = "female"
	 _region = "Vancouver"
	 
	 createUser(_displayName, _email, _password, _gender, _region)
	
	_displayName  = "Heng1"
	_email = "heng1@gmail.com"
	_password = "11111111"
	
	_gender = "female"
	_region = "Toronto"
	
	createUser(_displayName, _email, _password, _gender, _region)
	
	_displayName  = "Heng2"
	_email = "heng2@gmail.com"
	_password = "11111111"
	
	_gender = "male"
	_region = "Montreal"
	 
	 createUser(_displayName, _email, _password, _gender, _region)
	
	_displayName  = "Heng3"
	_email = "heng3@gmail.com"
	_password = "11111111"
	
	_gender = "female"
	_region = "Toronto"
	
	createUser(_displayName, _email, _password, _gender, _region)
	
	_displayName  = "Heng4"
	_email = "heng4@gmail.com"
	_password = "11111111"
	
	 _gender = "female"
	 _region = "Montreal"
	
	createUser(_displayName, _email, _password, _gender, _region)
	
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




				