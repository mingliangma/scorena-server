package com.doozi.scorena.parse

import java.net.URLEncoder;

import org.springframework.transaction.annotation.Transactional
import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject


@Transactional
class ParseService {
	public static final int COMPOUND_QUERY_OBJECT_LIMIT=9
	
	def grailsApplication
	
	
    def validateSession(def rest, def sessionToken) {
		log.info "validateSession(): begins with rest = ${rest}, sessionToken = ${sessionToken}"
		
		def parseConfig = grailsApplication.config.parse
		
		def resp = rest.get("https://api.parse.com/1/users/me"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header	"X-Parse-Session-Token", sessionToken
		}
		
		log.info "validateSession(): ends with resp = ${resp}"
		
		return resp
    }
	
	def validateSessionT3(def rest, def sessionToken) {
		log.info "validateSessionT3(): begins with rest = ${rest}, sessionToken = ${sessionToken}"
		
		def parseConfig = grailsApplication.config.parse
		
		def resp = rest.get("https://api.parse.com/1/users/me"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId_t3
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey_t3
			header	"X-Parse-Session-Token", sessionToken
		}
		
		log.info "validateSessionT3(): ends with resp = ${resp}"
		
		return resp
	}
	
	def createUser(def rest, String usernameInput, String emailInput, String passwordInput, String genderInput, String regionInput, String displayNameInput, String pictureURLInput, 
		String facebookIdInput){
		log.info "createUser(): begins with rest = ${rest}, usernameInput = ${usernameInput}, emailInput = ${emailInput}, passwordInput = ${passwordInput}, genderInput = ${genderInput}" 
		log.info "createUser(): begins with regionInput = ${regionInput}, displayNameInput = ${displayNameInput}, pictureURLInput = ${pictureURLInput}, facebookIdInput = ${facebookIdInput}"
		
		def parseConfig = grailsApplication.config.parse
		def resp = rest.post("https://api.parse.com/1/users"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json {
				username=usernameInput
				password=passwordInput
				email=emailInput
				gender=genderInput
				region=regionInput
				display_name=displayNameInput
				pictureURL=pictureURLInput
				facebookId=facebookIdInput
				lastLoggedIn=new Date()
			}
		}
		
		log.info "createUser(): ends with resp = ${resp}"
		
		return resp
	}
	
	def loginUser(def rest, String username, String password){
		log.info "loginUser(): begins with rest = ${rest}, username = ${username}, password = ${password}"
		
		def parseConfig = grailsApplication.config.parse
		/*
		def resp = rest.get("https://api.parse.com/1/login?username=${username.encodeAsURL()}&password=${password.encodeAsURL()}"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey			
		}
		*/
		String url = "https://api.parse.com/1/login?username=" + username.encodeAsURL() +"&password="+password.encodeAsURL()
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
				
		httpget.addHeader("X-Parse-Application-Id", parseConfig.parseApplicationId);
		httpget.addHeader("X-Parse-REST-API-Key", parseConfig.parseRestApiKey);
		
		HttpResponse httpResponse = httpclient.execute(httpget);
		JSONObject resultJson = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		
		def respMsg = [status:statusCode, json:resultJson]
		
		log.info "loginUser(): ends with resp = ${respMsg}"
	//	log.info "loginUser(): ends with resp = ${resp}"
		
	//	return resp
		return respMsg
	}
	
	
	def deleteUser(def rest, def sessionToken, def objectId){
		log.info "deleteUser(): begins with rest = ${rest}, sessionToke = ${sessionToken}, objectId = ${objectId}"
		
		println "https://api.parse.com/1/users/"+objectId
		println "X-Parse-Session-Token: "+ sessionToken
		
		def parseConfig = grailsApplication.config.parse
		def resp = rest.delete("https://api.parse.com/1/users/"+objectId){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header	"X-Parse-Session-Token", sessionToken
		}
		
		log.info "deleteUser(): ends with resp = ${resp}"
		
		return resp
	}
	
	def retrieveUser(def rest, def objectId){
		log.info "retrieveUser(): begins with rest = ${rest}, objectId = ${objectId}"
		
		def parseConfig = grailsApplication.config.parse
		def resp = rest.get("https://api.parse.com/1/users/"+objectId){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
		}
		
		log.info "retrieveUser(): ends with resp = ${resp}"
		
		return resp
	}	
	
	
// There is an issue by using RestBuilder that pass in a JSON object into the URL parameter.
	 
//	def retreiveUserList(def rest, List objectIds){
//		def parseConfig = grailsApplication.config.parse		
//		JSONArray objectIdsJson = retreiveUserListParamsRender(objectIds)
//		String orOperatorMap = ([$or:objectIdsJson] as JSON).toString()
//
//		String url = "https://api.parse.com/1/users?where=" + URLEncoder.encode(orOperatorMap, "UTF-8");
//
//		def resp = rest.get(url){
//			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
//			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
//			accept "application/json"
//		}
//		println resp
//		return resp
//	}
	
	Map retrieveUserListByFBIds(List facebookIds){
		log.info "retrieveUserListByFBIds(): begins with facebookIds = ${facebookIds}"
		
		def parseConfig = grailsApplication.config.parse
		if (facebookIds.size() == 0){
			return [:]
		}
		
		String url = "https://api.parse.com/1/users?where=" + getJSONWhereConstraints("facebookId", facebookIds);
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
				
		httpget.addHeader("X-Parse-Application-Id", parseConfig.parseApplicationId);
		httpget.addHeader("X-Parse-REST-API-Key", parseConfig.parseRestApiKey);

		HttpResponse httpResponse = httpclient.execute(httpget);
		JSONObject resultJson = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
		
		log.info "retrieveUserListByFBIds(): ends with resultJson = ${resultJson}"
		
		return (resultJson as Map)
	}
	
	/**
	 * There is an issue by using RestBuilder that pass in a JSON object into the URL parameter.
	 * Therefore, The Java httpclient and httpcore library are used for this method
	 * 
	 * @param objectIds: a list of userIds (called objectIds in parse)  
	 * 					
	 * @return a Map object that contains a list of Parse users' profile
	 */
	Map retrieveUserList(List objectIds){
		log.info "retrieveUserList(): begins with objectIds = ${objectIds}"
		
		def parseConfig = grailsApplication.config.parse
		int inputObjectIdsSize = objectIds.size()
		if (inputObjectIdsSize == 0){
			return [:]
		}				
		
		int i = 0
		int querySize = 0
		List queryIdList =[]
		List parseUserList = []
		HttpClient httpclient = new DefaultHttpClient()
		
		for (String objectId: objectIds){
			queryIdList.add(objectId)
			querySize++
			i++
			
			if (i>=inputObjectIdsSize || querySize >=COMPOUND_QUERY_OBJECT_LIMIT){
				String url = "https://api.parse.com/1/users?where=" + getJSONWhereConstraints("objectId", queryIdList)
				Map queryResult = parseQuery(httpclient, url)
				
				if (queryResult.error)
					return queryResult
					
				List parseResult = queryResult.results
				parseUserList = parseUserList+parseResult
				
				querySize=0
				queryIdList=[]
			}

		}
		
		Map userListResult = [results:parseUserList]
		
		log.info "retrieveUserList(): ends with userListResult = ${userListResult}"

		return userListResult
	}
	
	Map retrieveUserList(Map objectIds){
		List objectIdList = [];
		objectIds.each() { k, v -> objectIdList << k }
		return retrieveUserList(objectIdList)
	}
	
	Map retrieveUserByDisplayName(def displayName){
		log.info "retrieveUserByDisplayName(): begins with displayName = ${displayName}"
		
		def parseConfig = grailsApplication.config.parse
		String whereContraintsString = URLEncoder.encode('{"display_name":"' + displayName + '"}', "UTF-8")
		String url = "https://api.parse.com/1/users?where="+whereContraintsString
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
				
		httpget.addHeader("X-Parse-Application-Id", parseConfig.parseApplicationId);
		httpget.addHeader("X-Parse-REST-API-Key", parseConfig.parseRestApiKey);

		HttpResponse httpResponse = httpclient.execute(httpget);
		JSONObject resultJson = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
		
		log.info "retrieveUserByDisplayName(): ends with resultJson = ${resultJson}"
		
		return (resultJson as Map)
	}
	
	private String getJSONWhereConstraints(String fieldName, List values){
		log.info "getJSONWhereConstraints(): begins with fieldName = ${fieldName}, values = ${values}"
		
		JSONArray objectIdArray = new JSONArray()
		JSONObject whereContraintsJson = new JSONObject();

		for (String value: values){
			JSONObject objectIdJson = new JSONObject()
			objectIdJson.put(fieldName, value)			
			objectIdArray.add(objectIdJson)
		}
		
		whereContraintsJson.put('$or',objectIdArray);
		
		String whereContraintsString = URLEncoder.encode(whereContraintsJson.toString(), "UTF-8")
		
		log.info "getJSONWhereConstraints(): ends with whereContraintsString = ${whereContraintsString}"
		
		return whereContraintsString
	}
	
	private String getJSONWhereConstraints(String fieldName, Map values){
		log.info "getJSONWhereConstraints(): begins with fieldName = ${fieldName}, values = ${values}"
		
		JSONArray objectIdArray = new JSONArray()
		JSONObject whereContraintsJson = new JSONObject();
		
		values.each{
			it -> 
				JSONObject objectIdJson = new JSONObject()
				objectIdJson.put(fieldName, it.key)
				objectIdArray.add(objectIdJson)
		}
		
		whereContraintsJson.put('$or',objectIdArray);

		String whereContraintsString = URLEncoder.encode(whereContraintsJson.toString(), "UTF-8")
		
		log.info "getJSONWhereConstraints(): ends with whereContraintsString = ${whereContraintsString}"
		
		return whereContraintsString
	}
	
	def updateUser(def rest, def sessionToken, def objectId, def updateUserDataJSON){
		log.info "updateUser(): begins with rest = ${rest}, sessionToken = ${sessionToken}, objectId = ${objectId}, updateUserDataJSON = ${updateUserDataJSON}"
		
		def parseConfig = grailsApplication.config.parse		
		def resp = rest.put("https://api.parse.com/1/users/"+objectId){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header	"X-Parse-Session-Token", sessionToken
			contentType "application/json"
			json updateUserDataJSON
		}
		
		log.info "updateUser(): ends with resp = ${resp}"
		
		return resp
	}
	
	def passwordReset(def rest, def userEmail){
		log.info "passwordReset(): begins with rest = ${rest}, userEmail = ${userEmail}"
		
		def parseConfig = grailsApplication.config.parse
		def resp = rest.post("https://api.parse.com/1/requestPasswordReset"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json {
				email=userEmail
			}
		}
		
		log.info "passwordReset(): ends with resp = ${resp}"
		
		return resp
	}
	
	def uploadImage(def rest, def image, def imageName){
		log.info "uploadImag(): begins with rest = ${rest}, image = ${image}, imageName = ${imageName}"
		
		def parseConfig = grailsApplication.config.parse
		def resp = rest.post("https://api.parse.com/1/files/"+imageName){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header  "Content-Type: image/jpeg"
			body image
		}
		
		log.info "uploadImage(): ends with resp = ${resp}"
		
		return resp
	}
	
	def associateImageWithUser(def rest, def imageName){
		log.info "associateImageWithUser(): begins with rest = ${rest}, imageName = ${imageName}"
		
		def parseConfig = grailsApplication.config.parse
		def picture = ["name": imageName, "__type":"File"]
		def resp = rest.post("https://api.parse.com/1/classes/user"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json {
				profilePictureMedium= picture				
			}
		}
		
		log.info "associateImageWithUser(): ends with resp = ${resp}"
		
		return resp
	}
	
	private Map parseQuery(HttpClient httpclient, String url){
		log.info "parseQuery(): begins with url = ${url}"
		
		def parseConfig = grailsApplication.config.parse
		
		HttpGet httpget = new HttpGet(url);
				
		httpget.addHeader("X-Parse-Application-Id", parseConfig.parseApplicationId);
		httpget.addHeader("X-Parse-REST-API-Key", parseConfig.parseRestApiKey);

		HttpResponse httpResponse = httpclient.execute(httpget);
		JSONObject resultJson = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
		
		log.info "parseQuery(): ends with resultJson = ${resultJson}"
		
		return (resultJson as Map)
	}
	
}
