//command: -DnoTomcat=true run-script bootstrap-db-script/bootstrapScript.groovy
import com.doozi.User
import com.doozi.Account

def createUser(String _FID, String _displayName, String _email, String _password){
	
	def user = new User(FID: _FID, displayName: _displayName, email: _email, password: _password, account: new Account(currentBalance:0,previousBalance:0))

		if (user.save()){
			System.out.println("user successfully saved")
		}else{
			System.out.println("user save failed")
		}
}

String _FID = "11111111"
String _displayName  = "aaaaaa"
String _email = "aaaaa@gmail.com"
String _password = "11111111"

createUser(_FID, _displayName, _email, _password)

_FID = "22222222"
 _displayName  = "Joey"
 _email = "joey@gmail.com"
 _password = "11111111"

createUser(_FID, _displayName, _email, _password)

 _FID = "333333333"
 _displayName  = "Ming"
 _email = "ming@gmail.com"
 _password = "11111111"

createUser(_FID, _displayName, _email, _password)

 _FID = "444444444"
 _displayName  = "Kyle"
 _email = "kyle@gmail.com"
 _password = "11111111"

createUser(_FID, _displayName, _email, _password)

 _FID = "55555555"
 _displayName  = "Heng"
 _email = "heng@gmail.com"
 _password = "11111111"

createUser(_FID, _displayName, _email, _password)