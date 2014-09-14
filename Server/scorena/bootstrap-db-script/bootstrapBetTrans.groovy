import java.util.Date;

import com.doozi.scorena.User
import com.doozi.scorena.Account
import com.doozi.scorena.gameengine.GameService
import com.doozi.scorena.Question
import com.doozi.scorena.Game
import com.doozi.scorena.PoolTransaction
import com.doozi.scorena.BetResult

//def createBetTrans(int _wager, Date _time, boolean _pick, User user, Question q, Game game){
//	
//	def bet = new BetTransaction(wager: _wager, time: _time, pick: _pick, betResult: new BetResult(gameResult: "", payOut:0))
//	System.out.println("bet: "+bet.id)
//	
////	if (bet.save()){
////		System.out.println("---------------bet successfully saved")
////	}else{
////		System.out.println("---------------bet save failed")
////	}
//	
//	user.addToBet(bet)
//	q.addToBet(bet)
//	game.addToBet(bet)
//	
//	
//	if (user.save()){
//		System.out.println("---------------user successfully saved")
//	}else{
//		System.out.println("---------------user save failed")
//	}
//	
//	if (q.save()){
//		System.out.println("---------------q successfully saved")
//	}else{
//		System.out.println("---------------q save failed")
//	}
//	
//	if (game.save()){
//		System.out.println("---------------game successfully saved")
//	}else{
//		System.out.println("---------------game save failed")
//	}
//}

def simulateBet(){

	def gameService = ctx.getBean("gameService")
	def betService = ctx.getBean("betService")
	
	
	Random random = new Random()
	def accounts = Account.findAll()
	def upcomingGames = gameService.listUpcomingGames()
	for (int j=1; j < upcomingGames.size(); j++){
		def upcomingGame = upcomingGames.get(j)				
		System.out.println("game away: "+upcomingGame.away.teamname + "   VS   game home: "+upcomingGame.home.teamname + "----- StartDate: "+upcomingGame.date)
		
		def questions = Question.findAllByEventKey(upcomingGame.gameId)
		
		for (int i=0; i < questions.size(); i++){
			Question q = questions.get(i)
			def questionId = q.id
			for (Account account: accounts){
				int _wager =  (random.nextInt(4)+1)*20
				if (account.currentBalance <= _wager){
					continue
				}
				if (random.nextInt(5) == 1){
					
					
					Date _time = new Date()
					int _pick
					
					if (random.nextInt(2)==0){
						_pick=1
					}else{
						_pick=2
					}
					System.out.println("user name: "+account.username + " wager: "+_wager + " balance: "+account.currentBalance)
					betService.saveBetTrans(_wager, _time,_pick, account.userId, q.id)
				}
			}
		}
	}
}

simulateBet()

