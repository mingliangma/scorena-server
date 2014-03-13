//command: -DnoTomcat=true run-script bootstrap-db-script/bootstrapScript.groovy
import com.doozi.Game
import com.doozi.GameEvent
import com.doozi.GameResult
import com.doozi.Pool
import com.doozi.Question

def createGame(String _league, String _away, String _home, String _type, String _country, String _theDate, int _awayScore, int _homeScore, int _pick1Amount,
	int _pick1NumPeople, int _pick2Amount, int _pick2NumPeople){
	
			println "create game starts..."
			def newdate = new Date().parse("d/M/yyyy H:m:s", _theDate)
			def game = new Game(league: _league, home:_home, away: _away, type:_type, country:_country, date:newdate,
				gameResult:new GameResult(awayScore:_awayScore, homeScore:_homeScore), gameEvent:new GameEvent(awayScore:_awayScore, homeScore:_homeScore))
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

	
String _league = "EPL"
String _away = "Chelsea"
String _home ="Man Unitied"
String _type ="soccer"
String _country = "england"
String _theDate ="12/03/2014 16:00:00"
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
_theDate ="14/03/2014 16:00:00"
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
_theDate ="14/03/2014 20:00:00"
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
_theDate ="18/03/2014 16:00:00"
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
_theDate ="24/03/2014 16:00:00"
_awayScore =0
_homeScore =0
_pick1Amount =0
_pick1NumPeople =0
_pick2Amount =0
_pick2NumPeople =0
createGame( _league,  _away,  _home,  _type,  _country,  _theDate,  _awayScore,  _homeScore,  _pick1Amount, _pick1NumPeople,  _pick2Amount, _pick2NumPeople)


println "create game ended"

				