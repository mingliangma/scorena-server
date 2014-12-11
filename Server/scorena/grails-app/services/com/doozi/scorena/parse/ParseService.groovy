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
		def parseConfig = grailsApplication.config.parse
		
		def resp = rest.get("https://api.parse.com/1/users/me"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header	"X-Parse-Session-Token", sessionToken
		}
		return resp
    }
	
	def validateSessionT3(def rest, def sessionToken) {
		def parseConfig = grailsApplication.config.parse
		
		def resp = rest.get("https://api.parse.com/1/users/me"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId_t3
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey_t3
			header	"X-Parse-Session-Token", sessionToken
		}
		return resp
	}
	
	def createUser(def rest, String usernameInput, String emailInput, String passwordInput, String genderInput, String regionInput, String displayNameInput, String pictureURLInput, 
		String facebookIdInput){
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
		return resp
	}
	
	def loginUser(def rest, String username, String password){
		def parseConfig = grailsApplication.config.parse
		
		def resp = rest.get("https://api.parse.com/1/login?username=${username.encodeAsURL()}&password=${password.encodeAsURL()}"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey			
		}
		return resp
	}
	
	
	def deleteUser(def rest, def sessionToken, def objectId){
		println "https://api.parse.com/1/users/"+objectId
		println "X-Parse-Session-Token: "+ sessionToken
		
		def parseConfig = grailsApplication.config.parse
		def resp = rest.delete("https://api.parse.com/1/users/"+objectId){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header	"X-Parse-Session-Token", sessionToken
		}
		return resp
	}
	
	def retrieveUser(def rest, def objectId){
		def parseConfig = grailsApplication.config.parse
		def resp = rest.get("https://api.parse.com/1/users/"+objectId){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
		}
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

		return userListResult
	}
	
	Map retrieveUserList(Map objectIds){
		List objectIdList = [];
		objectIds.each() { k, v -> objectIdList << k }
		return retrieveUserList(objectIdList)
	}
	
	Map retrieveUserByDisplayName(def displayName){
		def parseConfig = grailsApplication.config.parse
		String whereContraintsString = URLEncoder.encode('{"display_name":"' + displayName + '"}', "UTF-8")
		String url = "https://api.parse.com/1/users?where="+whereContraintsString
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
				
		httpget.addHeader("X-Parse-Application-Id", parseConfig.parseApplicationId);
		httpget.addHeader("X-Parse-REST-API-Key", parseConfig.parseRestApiKey);

		HttpResponse httpResponse = httpclient.execute(httpget);
		JSONObject resultJson = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
		
		return (resultJson as Map)
	}
	
	private String getJSONWhereConstraints(String fieldName, List values){
		JSONArray objectIdArray = new JSONArray()
		JSONObject whereContraintsJson = new JSONObject();

		for (String value: values){
			JSONObject objectIdJson = new JSONObject()
			objectIdJson.put(fieldName, value)			
			objectIdArray.add(objectIdJson)
		}
		
		whereContraintsJson.put('$or',objectIdArray);
		
		String whereContraintsString = URLEncoder.encode(whereContraintsJson.toString(), "UTF-8")
		return whereContraintsString
	}
	
	private String getJSONWhereConstraints(String fieldName, Map values){
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
		return whereContraintsString
	}
	
	def updateUser(def rest, def sessionToken, def objectId, def updateUserDataJSON){
		def parseConfig = grailsApplication.config.parse		
		def resp = rest.put("https://api.parse.com/1/users/"+objectId){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header	"X-Parse-Session-Token", sessionToken
			contentType "application/json"
			json updateUserDataJSON
		}
		return resp
	}
	
	def passwordReset(def rest, def userEmail){
		def parseConfig = grailsApplication.config.parse
		def resp = rest.post("https://api.parse.com/1/requestPasswordReset"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json {
				email=userEmail
			}
		}
		return resp
	}
	
	def uploadImage(def rest, def image, def imageName){
		def parseConfig = grailsApplication.config.parse
		def resp = rest.post("https://api.parse.com/1/files/"+imageName){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header  "Content-Type: image/jpeg"
			body image
		}
		return resp
	}
	
	def associateImageWithUser(def rest, def imageName){
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
		return resp
	}
	
	private Map parseQuery(HttpClient httpclient, String url){
		
		def parseConfig = grailsApplication.config.parse
		
		HttpGet httpget = new HttpGet(url);
				
		httpget.addHeader("X-Parse-Application-Id", parseConfig.parseApplicationId);
		httpget.addHeader("X-Parse-REST-API-Key", parseConfig.parseRestApiKey);

		HttpResponse httpResponse = httpclient.execute(httpget);
		JSONObject resultJson = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
		
		return (resultJson as Map)
	}
	
}
