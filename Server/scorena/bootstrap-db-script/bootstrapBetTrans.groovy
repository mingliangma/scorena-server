import java.util.Date;

import com.doozi.User
import com.doozi.Account
import com.doozi.GameService
import com.doozi.Question
import com.doozi.Game
import com.doozi.BetTransaction
import com.doozi.BetResult

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

simulateBet()

