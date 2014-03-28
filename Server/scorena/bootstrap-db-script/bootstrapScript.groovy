//command: -DnoTomcat=true run-script bootstrap-db-script/bootstrapScript.groovy
import com.doozi.Game
import com.doozi.GameEvent
import com.doozi.GameResult
import com.doozi.Pool
import com.doozi.Question
import com.doozi.Account
import com.doozi.GameService
import com.doozi.BetTransaction
import com.doozi.BetResult
import com.doozi.User

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


def createGame(String _league, String _away, String _home, String _type, String _country, String _theDate, int _awayScore, int _homeScore, int _pick1Amount,
	int _pick1NumPeople, int _pick2Amount, int _pick2NumPeople){
	
			println "create game starts..."
			def newdate = new Date().parse("d/M/yyyy H:m:s", _theDate)
			def game = new Game(league: _league, home:_home, away: _away, type:_type, country:_country, date:newdate,
				gameResult:new GameResult(awayScore:_awayScore, homeScore:_homeScore), gameEvent:new GameEvent(awayScore:_awayScore, homeScore:_homeScore))
			System.out.println("game: "+game.id)
			def q1 = new Question(pick1: _home, pick2: _away, content:"who will win between the two", 
				pool: new Pool(pick1Amount:_pick1Amount, pick1NumPeople:_pick1NumPeople, pick2Amount:_pick2Amount, pick2NumPeople:_pick2NumPeople))
			def q2 = new Question(pick1: _home, pick2: _away, content:"who will score the first goal",
				pool: new Pool(pick1Amount:_pick1Amount, pick1NumPeople:_pick1NumPeople, pick2Amount:_pick2Amount, pick2NumPeople:_pick2NumPeople))
			
			game.addToQuestion(q1);
			game.addToQuestion(q2);

		
			if (game.save()){
				System.out.println("game successfully saved")
			}else{
				System.out.println("game save failed")
			}
			
}
	
def simulateBet(){
	
	
	def gameService = ctx.getBean("gameService")
	def betService = ctx.getBean("betService")
	
	
	Random random = new Random()
	def accounts = Account.findAll()
	
	
	
	def upcomingGames = gameService.getUpcomingGameObjects()
	for (Game upcomingGame: upcomingGames){
		System.out.println("game away: "+upcomingGame.away + "   VS   game home: "+upcomingGame.home)
		for (Question q: upcomingGame.question){
			System.out.println("question: "+q.content)
			for (Account account: accounts){
				
				System.out.println("user name: "+account.username)
				int _wager =  (random.nextInt(6)+1)*5
				Date _time = new Date() - random.nextInt(7)
				int _pick
				
				if (random.nextInt(1)==0){
					_pick=0
				}else{
					_pick=1
				}
				betService.saveBetTrans(_wager, _time,_pick, account, q, upcomingGame)
			}
		}
	}
}

def createUser(String _username, String _email, String _password){
	
	def userService = ctx.getBean("userService")
    def resp = userService.createUser(_username, _email, _password)
	println resp
	
}
	
//	String _league = "EPL"
//	String _away = "Chelsea"
//	String _home ="Man United"
//	String _type ="soccer"
//	String _country = "england"
//	String _theDate ="01/04/2014 12:00:00"
//	int _awayScore =0
//	int _homeScore =0
//	int _pick1Amount =0
//	int _pick1NumPeople =0
//	int _pick2Amount =0
//	int _pick2NumPeople =0
//	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
//	
//	_league = "EPL"
//	_away = "Liverpool FC"
//	_home ="Stoke City"
//	_type ="soccer"
//	_country = "england"
//	_theDate ="28/03/2014 16:00:00"
//	_awayScore =0
//	_homeScore =0
//	_pick1Amount =0
//	_pick1NumPeople =0
//	_pick2Amount =0
//	_pick2NumPeople =0
//	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
//	
//	_league = "EPL"
//	_away = "Arsenal FC"
//	_home ="Aston Villa"
//	_type ="soccer"
//	_country = "england"
//	_theDate ="29/03/2014 20:00:00"
//	_awayScore =0
//	_homeScore =0
//	_pick1Amount =0
//	_pick1NumPeople =0
//	_pick2Amount =0
//	_pick2NumPeople =0
//	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
//	
//	_league = "EPL"
//	_away = "Norwich City"
//	_home ="Everton FC"
//	_type ="soccer"
//	_country = "england"
//	_theDate ="30/03/2014 16:00:00"
//	_awayScore =0
//	_homeScore =0
//	_pick1Amount =0
//	_pick1NumPeople =0
//	_pick2Amount =0
//	_pick2NumPeople =0
//	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
//	
//	_league = "EPL"
//	_away = "Sunderland AFC"
//	_home ="Fulham FC"
//	_type ="soccer"
//	_country = "england"
//	_theDate ="24/04/2014 16:00:00"
//	_awayScore =0
//	_homeScore =0
//	_pick1Amount =0
//	_pick1NumPeople =0
//	_pick2Amount =0
//	_pick2NumPeople =0
//	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
//
//	_league = "EPL"
//	_away = "Liverpool FC"
//	_home ="Stoke City"
//	_type ="soccer"
//	_country = "england"
//	_theDate ="14/03/2014 16:00:00"
//	_awayScore =0
//	_homeScore =0
//	_pick1Amount =0
//	_pick1NumPeople =0
//	_pick2Amount =0
//	_pick2NumPeople =0
//	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
//	
//	_league = "EPL"
//	_away = "Arsenal FC"
//	_home ="Aston Villa"
//	_type ="soccer"
//	_country = "england"
//	_theDate ="14/03/2014 20:00:00"
//	_awayScore =0
//	_homeScore =0
//	_pick1Amount =0
//	_pick1NumPeople =0
//	_pick2Amount =0
//	_pick2NumPeople =0
//	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
//	
//	_league = "EPL"
//	_away = "Norwich City"
//	_home ="Everton FC"
//	_type ="soccer"
//	_country = "england"
//	_theDate ="18/03/2014 16:00:00"
//	_awayScore =0
//	_homeScore =0
//	_pick1Amount =0
//	_pick1NumPeople =0
//	_pick2Amount =0
//	_pick2NumPeople =0
//	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)
//	
//	_league = "EPL"
//	_away = "Sunderland AFC"
//	_home ="Fulham FC"
//	_type ="soccer"
//	_country = "england"
//	_theDate ="24/03/2014 16:00:00"
//	_awayScore =0
//	_homeScore =0
//	_pick1Amount =0
//	_pick1NumPeople =0
//	_pick2Amount =0
//	_pick2NumPeople =0
//	createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)

	println "create game ended"
	
	String _displayName  = "michealLiu"
	String _email = "micheal@gmail.com"
	String _password = "11111111"
	
	createUser(_displayName, _email, _password)
	
	 _displayName  = "Joey"
	 _email = "joey@gmail.com"
	 _password = "11111111"
	
	createUser(_displayName, _email, _password)
	
	 _displayName  = "Ming"
	 _email = "ming@gmail.com"
	 _password = "11111111"
	
	createUser(_displayName, _email, _password)
	
	 _displayName  = "Kyle"
	 _email = "kyle@gmail.com"
	 _password = "11111111"
	
	createUser(_displayName, _email, _password)
	
	 _displayName  = "Heng"
	 _email = "heng@gmail.com"
	 _password = "11111111"
	
	createUser(_displayName, _email, _password)
	
	_displayName  = "Heng1"
	_email = "heng1@gmail.com"
	_password = "11111111"
	
	createUser(_displayName, _email, _password)
	
	_displayName  = "Heng2"
	_email = "heng2@gmail.com"
	_password = "11111111"
	
	createUser(_displayName, _email, _password)
	
	_displayName  = "Heng3"
	_email = "heng3@gmail.com"
	_password = "11111111"
	
	createUser(_displayName, _email, _password)
	
	_displayName  = "Heng4"
	_email = "heng4@gmail.com"
	_password = "11111111"
	
	createUser(_displayName, _email, _password)
	
	println "create users ended"
	simulateBet()
	
	println "create transactions ended"



				