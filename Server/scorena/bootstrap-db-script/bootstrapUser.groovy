//command: -DnoTomcat=true run-script bootstrap-db-script/bootstrapScript.groovy
import com.doozi.User
import com.doozi.Account

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

//def createUser(String _FID, String _displayName, String _email, String _password){
//	
//	def user = new User(FID: _FID, displayName: _displayName, email: _email, password: _password, account: new Account(currentBalance:0,previousBalance:0))
//
//		if (user.save()){
//			System.out.println("user successfully saved")
//		}else{
//			System.out.println("user save failed")
//		}
//}

def createUser(String _username, String _email, String _password){
	def userService = ctx.getBean("userService")
	userService.createUser(_username, _email, _password)
	
}

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
