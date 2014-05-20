import com.doozi.scorena.*
import com.doozi.scorena.sportsdata.ScorenaAllGames
import java.util.Date;

class BootStrap {

    def init = { servletContext ->
		println "bootstrap starts..."
		
		if (!QuestionContent.count()) {
//			bootstrapQuestionContent()
		}
		if (!Question.count()) {
//			createQuestions()
//			createUsers()
//			simulateBetUpcoming()
//			simulateBetPast()
		}
		println "bootstrap ended"
    }
    def destroy = {
    }
	
	def gameService
	def betService
	def userService
	def customQuestionService
	
	def createCustomQuestions(){
		List pastGames = gameService.listPastGames()
		println "createCustomQuestions()::pastGames: "+pastGames.size()
		for (def game: pastGames){
			customQuestionService.createCustomQuestion(game.gameId, "Joseph will be naked at the party?", "hell yes", "of course")
		}
	}
	
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
	
	
//		def gameService = ctx.getBean("gameService")
//		def betService = ctx.getBean("betService")
		
		
		Random random = new Random()
		def accounts = Account.findAll()
		def upcomingGames = gameService.listUpcomingGames()
		for (int i=0; i < upcomingGames.size(); i++){
			def upcomingGame = upcomingGames.get(i)
			System.out.println("game away: "+upcomingGame.away.teamname + "   VS   game home: "+upcomingGame.home.teamname + "----- StartDate: "+upcomingGame.date)
			
			def questions = Question.findAllByEventKey(upcomingGame.gameId)
			
			
			for (Question q: questions){
				if (random.nextInt(3) == 1){
					continue
				}
				def questionId = q.id
				for (Account account: accounts){
					if (random.nextInt(2) == 1){
						continue
					}
//					System.out.println("user name: "+account.username)
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
		
		
//			def gameService = ctx.getBean("gameService")
//			def betService = ctx.getBean("betService")
			
			
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
						
//						System.out.println("user name: "+account.username)
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
		
//		def userService = ctx.getBean("userService")
		def resp = userService.createUser(_username, _email, _password, _gender, _region)
		println resp
		
	}
	
	def createUsers(){
		Random random = new Random()
		String _displayName
		String _email
		String _password
		String _gender
		String _region
		
		for (int i=0; i<10; i++){
			def num = random.nextInt(100000)
			 _displayName  = "scorena"+num.toString()
			 _email = _displayName+"@gmail.com"
			 _password = "12345"
			 _gender = "male"
			 _region = "Japan"
			createUser(_displayName, _email, _password, _gender, _region)
		}
		println "create users ended"
	}
}
