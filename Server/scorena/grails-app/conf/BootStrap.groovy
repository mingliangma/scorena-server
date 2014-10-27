import com.doozi.scorena.*
import com.doozi.scorena.sportsdata.ScorenaAllGames
import com.doozi.scorena.utils.AccountType;

import java.util.Date;

import grails.util.Environment

class BootStrap {

    def init = { servletContext ->
		println "bootstrap starts..."
		Environment.executeForCurrentEnvironment {
			development {
				
				Thread.sleep(5000)
		 		if (!QuestionContent.count()) {
					bootstrapQuestionContent()
				}
				 
				if (!Question.count()) {
					createQuestions()					
				}
				
//				if (!Account.countByAccountType(AccountType.TEST)){
//					createTestUsers()
//				}
				
				if (!Account.count()){
					createUsers()
					simulateBetUpcoming()
					simulateBetPast()
				}
				
				
				
				println "bootstrap ended"
			}
			awsdev{					
				Thread.sleep(5000)
		 		if (!QuestionContent.count()) {
					bootstrapQuestionContent()
				}
				 
				if (!Question.count()) {
					createQuestions()					
				}

				if (!Account.count()){
					createUsers()
					simulateBetUpcoming()
					simulateBetPast()
				}
				
				if (!Account.countByAccountType(AccountType.TEST)){
					createTestUsers()
				}
				
				println "bootstrap ended"
				}
			
			joel {
				
				Thread.sleep(5000)
				 if (!QuestionContent.count()) {
					bootstrapQuestionContent()
				}
				 
				if (!Question.count()) {
					createQuestions()
				}

				if (!Account.count()){
					createUsers()
					simulateBetUpcoming()
					simulateBetPast()
				}
				
				println "bootstrap ended"
			}
			
			production{
				Thread.sleep(5000)
				 if (!QuestionContent.count()) {
					bootstrapQuestionContent()
				}
				 
				if (!Question.count()) {
					createQuestions()
				}
				
				if (!Account.countByAccountType(AccountType.TEST)){
					createTestUsers()
				}
				
				println "bootstrap ended"
				}
		  }		
    }
    def destroy = {
    }
	
	def gameService
	def betTransactionService
	def userService
	def customQuestionService
	def questionService
	
	def createCustomQuestions(){
		List pastGames = gameService.listPastGames()
		println "createCustomQuestions()::pastGames: "+pastGames.size()
		for (def game: pastGames){
			customQuestionService.createCustomQuestion(game.gameId, "Joseph will be naked at the party?", "hell yes", "of course")
		}
	}
	
	def bootstrapQuestionContent(){
		def qc1 = new QuestionContent(questionType: QuestionContent.WHOWIN, content: "Who will win this match?", sport: "soccer")
		
		String qc2Indicator = 2.5
		String qc2Content = "What will the total score be?"
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
		
		List upcomingGames = gameService.listUpcomingGames()
		List pastGames = gameService.listPastGames()
		
		println "upcomingGames: "+upcomingGames.size()
		println "pastGames: "+pastGames.size()
		
		for (int i=0; i < upcomingGames.size(); i++){
			def game = upcomingGames.get(i)
			println "game id: "+game.gameId
			if (Question.findByEventKey(game.gameId) == null){
				questionService.populateQuestions(game.away.teamname, game.home.teamname, game.gameId)
			}
		}
		
		for (int i=0; i < pastGames.size(); i++){
			def game = pastGames.get(i)
			println "game id: "+game.gameId
			if (Question.findByEventKey(game.gameId) == null){
				questionService.populateQuestions(game.away.teamname, game.home.teamname, game.gameId)
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
			if (random.nextInt(2) == 1){
				continue
			}
			
			def upcomingGame = upcomingGames.get(i)
			System.out.println("game away: "+upcomingGame.away.teamname + "   VS   game home: "+upcomingGame.home.teamname + "----- StartDate: "+upcomingGame.date)
			
			def questions = Question.findAllByEventKey(upcomingGame.gameId)
			
			
			for (Question q: questions){
//				if (random.nextInt(3) == 1){
//					continue
//				}
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
					
					
					betTransactionService.createBetTrans(_wager,_pick, account.userId, questionId)
				}
			}
		}
	}
	
	def simulateBetPast(){
		
		
//			def gameService = ctx.getBean("gameService")
//			def betService = ctx.getBean("betService")
			
			println "============simulateBetPast() starts==============="
			Random random = new Random()
			def accounts = Account.findAll()
			def pastGames = gameService.listPastGames()
			for (int i=0; i < pastGames.size(); i++){
				
				if (random.nextInt(2) == 1){
					continue
				}
				
				def upcomingGame = pastGames.get(i)
				
				System.out.println("game away: "+upcomingGame.away.teamname + "   VS   game home: "+upcomingGame.home.teamname + "----- StartDate: "+upcomingGame.date)
				
				def questions = Question.findAllByEventKey(upcomingGame.gameId)
				
				
				for (Question q: questions){
//					if (random.nextInt(2) == 1){
//						continue
//					}
					def questionId = q.id
					Date _time = new Date() - (random.nextInt(6) + 18)
					for (Account account: accounts){
						if (random.nextInt(15) == 1){
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
						
						
						betTransactionService.createBetTrans(_wager,_pick, account.userId, questionId, _time, false)
					}
				}
			}
		}
		
	
	def createUser(String _username, String _email, String _password, String _gender, String _region){
		
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
	
	boolean testUsersExist(){
		def acc = Account.findAllByAccountType(AccountType.TEST)
		if (acc)
			true
		else
			false
	}
	
	def createTestUsers(){
		List _displayNames = [
		"UncleSam",
		"SecretAgents",
		"Aiden3321",
		"Liam",
		"GotTheRuns",
		"Noah",
		"Masonsportsmaster",
		"Jayden",
		"Ethan",
		"Jacob",
		"ElectrolyteJunkies",
		"New-Balls",
		"bigjoe",
		"KissMyAce",
		"Hard_Balls",
		"Caleb",
		"Ryan",
		"Alexander",
		"Elijah",
		"James",
		"William",
		"Oliver",
		"Boom",
		"Matthew",
		"Daniel",
		"Blondie",
		"Brayden",
		"Jayce",
		"Henry",
		"Carter",
		"DylanDamon",
		"GabrielVespasian",
		"Joshua",
		"Nicholas",
		"DizzyIsaac",
		"Owen",
		"Nathan",
		"Grayson",
		"EliBaker",
		"RedLandon",
		"Andrew888",
		"Max_Ling",
		"Nenete",
		"Losob",
		"Lujabi",
		"BigNastyOnes",
		"Faceskull",
		"Smash Girls",
		"Beefdoof",
		"Meatcorn",
		"Clotwimp",
		"Wipelunk",
		"JosephLumpcheese",
		"Bumpface",
		"godfather",
		"Fingerthimble",
		"JohnAbrams",
		"Poofcheese",
		"Alpha_Bandits",
		"SpinMasters",
		"torontowild",
		"ToxicSmokinMonkeys",
		"Adam",
		"Isaiah",
		"Alex",
		"Aaron",
		"Parker",
		"Cooper",
		"Miles",
		"Chase",
		"Muhammad"]
		
		List pictureURLs = [
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/000profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/001profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/002profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/003profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/004profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/005profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/006profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/007profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/008profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/009profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/010profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/011profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/012profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/013profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/014profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/015profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/016profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/017profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/018profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/019profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/020profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/021profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/022profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/023profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/024profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/025profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/026profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/027profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/028profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/029profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/030profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/031profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/032profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/033profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/034profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/035profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/036profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/037profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/038profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/039profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/040profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/041profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/042profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/043profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/044profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/045profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/046profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/047profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/048profile.png",
			"https://s3-us-west-2.amazonaws.com/userprofilepickture/049profile.png",
			]
		
		
		
		
		String _password = "12345"
		String _gender = "male"
		String _region = "Japan"
		Random random = new Random()
		for(int i = 0; i<_displayNames.size(); i++){
			String _email = _displayNames[i]+random.nextInt(100000)+"@scorena.ca"
			String pictureURL = ""
			if (i<pictureURLs.size()){
				pictureURL = pictureURLs[i]
			}
			def resp = userService.createTestUser(_displayNames[i], _email, _password, _gender, _region, pictureURL)
		}
		
	}
}
