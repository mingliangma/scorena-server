/**
 * 
 */
package com.doozi.scorena.gamedata.manager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.doozi.scorena.gamedata.helper.GameDataConstantsXmlSoccer;
import com.doozi.scorena.gamedata.userinput.GameDataInputXmlSoccer;
import com.doozi.scorena.gamedata.useroutput.GameDataOutput;
import com.doozi.scorena.gamedata.useroutput.GameDataSoccer;
import com.doozi.scorena.gamedata.useroutput.GameDataTeam;

/**
 * @author hengkuang
 *
 */
public class GameDataManagerXmlSoccer extends GameDataManager {

	private static final GameDataManagerXmlSoccer _gameDataManagerXmlSoccerInstance = new GameDataManagerXmlSoccer();
	
	/**
	 * 
	 */
	private GameDataManagerXmlSoccer() 
	{
		
	}
	
	public static GameDataManagerXmlSoccer get_gameDataManagerXmlSoccerInstance() 
	{
		return _gameDataManagerXmlSoccerInstance;
	}
	
	public GameDataOutput retrieveGameData(GameDataInputXmlSoccer gameDataInputXmlSoccer) throws Exception
	{
		BufferedReader bufferedReader = null;
		
		try
		{
			final URL apiUrl = new URL(gameDataInputXmlSoccer.get_apiUrl());
			bufferedReader = new BufferedReader(new InputStreamReader(apiUrl.openStream()));
			final StringBuffer stringBuffer = new StringBuffer();
			int read;
			final char[] chars = new char[1024];
			
			while ((read = bufferedReader.read(chars)) != -1)
			{
				stringBuffer.append(chars, 0, read);
			}
			
			String originalGameData = stringBuffer.toString();
			
			System.out.println(originalGameData);
			
			getLeagueScheduleBySeason(originalGameData, gameDataInputXmlSoccer.get_gameType());
			
			if (gameDataInputXmlSoccer.get_apiMethod().equalsIgnoreCase(GameDataConstantsXmlSoccer.apiMethod_GetLeagueStandingsBySeason))
			{
				return getLeagueStandingsBySeason(originalGameData, gameDataInputXmlSoccer.get_gameType());
			}
			
			GameDataOutput gameDataOutput = new GameDataOutput(originalGameData);
			return gameDataOutput;
		}
		finally
		{
			if (bufferedReader != null)
			{
				bufferedReader.close();
			}
		}
	}
	
	public GameDataOutput getLeagueScheduleBySeason(String originalGameData, String gameType) throws Exception 
	{
		GameDataOutput gameDataOutput = new GameDataOutput(originalGameData);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db;
    	Document doc;
    	db = dbf.newDocumentBuilder();
		doc = db.parse(new InputSource(new ByteArrayInputStream(originalGameData.getBytes("utf-8"))));
		NodeList sportsEvents = doc.getElementsByTagName("sports-event");		
				
		for(int i =0; i<sportsEvents.getLength(); i++){
			Node eventNode = sportsEvents.item(i);
			Element eventElement = (Element) eventNode;
			Node eventMetadata = eventElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlteam_xmlTag_EventMetadata).item(0);
			if (eventMetadata.hasAttributes()){
				NamedNodeMap metadataAttr = eventMetadata.getAttributes();
				String eventKey = metadataAttr.getNamedItem("event-key").getNodeValue();
				
			}
			
			//GameDataEvent gameDataEvent = new GameDataEvent();
			
		}
		return gameDataOutput;
	}
	
	public GameDataOutput getLeagueStandingsBySeason(String originalGameData, String gameType) throws Exception
	{
		GameDataOutput gameDataOutput = new GameDataOutput(originalGameData);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db;
    	Document doc;
    	db = dbf.newDocumentBuilder();
		doc = db.parse(new InputSource(new ByteArrayInputStream(originalGameData.getBytes("utf-8"))));
		NodeList teamLeagueStanding = doc.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_TeamLeagueStanding);
		
		for (int i=0; i<teamLeagueStanding.getLength(); i++)
    	{
    		 Node teamLeagueStandingNode = teamLeagueStanding.item(i);
    		 Element teamLeagueStandingElement = (Element) teamLeagueStandingNode;
    		 NodeList teamNameNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_TeamName);
    		 NodeList teamIdNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_TeamId);
    		 NodeList playedNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_Played);
    		 NodeList playedAtHomeNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_PlayedAtHome);
    		 NodeList playedAwayNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_PlayedAway);
    		 NodeList wonNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_Won);
    		 NodeList drawNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_Draw);
    		 NodeList lostNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_Lost);
    		 NodeList pointsNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_Points);
    		 GameDataTeam gameDataTeam = new GameDataTeam();
    		 gameDataTeam.set_teamType(gameType);
    		 gameDataTeam.set_ranking(String.valueOf(i+1));
    		 gameDataTeam.set_teamId(teamIdNodeList.item(0).getTextContent());
    		 gameDataTeam.set_teamName(teamNameNodeList.item(0).getTextContent());
    		 gameDataTeam.set_played(playedNodeList.item(0).getTextContent());
    		 gameDataTeam.set_playedAtHome(playedAtHomeNodeList.item(0).getTextContent());
    		 gameDataTeam.set_playedAway(playedAwayNodeList.item(0).getTextContent());
    		 gameDataTeam.set_won(wonNodeList.item(0).getTextContent());
    		 gameDataTeam.set_draw(drawNodeList.item(0).getTextContent());
    		 gameDataTeam.set_lost(lostNodeList.item(0).getTextContent());
    		 gameDataTeam.set_points(pointsNodeList.item(0).getTextContent());
    		 
    		 if (gameType.equalsIgnoreCase(GameDataConstantsXmlSoccer.gameType_Soccer))
    		 {
    			 NodeList numberOfShotsNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_NumberOfShots);
    			 NodeList yellowCardsNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_YellowCards);
    			 NodeList redCardsNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_RedCards);
    			 NodeList goalsForNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_GoalsFor);
    			 NodeList goalsAgainstNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_GoalsAgainst);
    			 NodeList goalDifferenceNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstantsXmlSoccer.xmlTag_GoalDifference);
    			 GameDataSoccer gameDataSoccer = new GameDataSoccer();
    			 gameDataSoccer.set_numberOfShots(numberOfShotsNodeList.item(0).getTextContent());
    			 gameDataSoccer.set_yellowCards(yellowCardsNodeList.item(0).getTextContent());
    			 gameDataSoccer.set_redCards(redCardsNodeList.item(0).getTextContent());
    			 gameDataSoccer.set_goalsFor(goalsForNodeList.item(0).getTextContent());
    			 gameDataSoccer.set_goalsAgainst(goalsAgainstNodeList.item(0).getTextContent());
    			 gameDataSoccer.set_goalDifference(goalDifferenceNodeList.item(0).getTextContent());
    			 gameDataTeam.set_gameDataSoccer(gameDataSoccer);
    		 }
    		 
    		 gameDataOutput.get_teamList().put(gameDataTeam.get_teamName(), gameDataTeam);
    	}
		
		return gameDataOutput;
	}
}
