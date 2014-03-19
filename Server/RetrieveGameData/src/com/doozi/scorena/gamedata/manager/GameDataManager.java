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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.doozi.scorena.gamedata.helper.GameDataConstants;
import com.doozi.scorena.gamedata.userinput.GameDataInput;
import com.doozi.scorena.gamedata.useroutput.GameDataOutput;
import com.doozi.scorena.gamedata.useroutput.GameDataSoccer;
import com.doozi.scorena.gamedata.useroutput.GameDataTeam;

/**
 * @author hengkuang
 *
 */
public class GameDataManager {

	private static final GameDataManager _gameDataManagerInstance = new GameDataManager();
	
	/**
	 * 
	 */
	private GameDataManager() 
	{
		
	}
	
	public static GameDataManager get_gameDataManagerInstance() 
	{
		return _gameDataManagerInstance;
	}
	
	public GameDataOutput retrieveGameData(GameDataInput gameDataInput) throws Exception
	{
		BufferedReader bufferedReader = null;
		
		try
		{
			final URL apiUrl = new URL(gameDataInput.get_apiUrl());
			bufferedReader = new BufferedReader(new InputStreamReader(apiUrl.openStream()));
			final StringBuffer stringBuffer = new StringBuffer();
			int read;
			final char[] chars = new char[1024];
			
			while ((read = bufferedReader.read(chars)) != -1)
			{
				stringBuffer.append(chars, 0, read);
			}
			
			String originalGameData = stringBuffer.toString();
			
			if (gameDataInput.get_apiMethod().equalsIgnoreCase(GameDataConstants.apiMethod_GetLeagueStandingsBySeason))
			{
				return getLeagueStandingsBySeason(originalGameData, gameDataInput.get_gameType());
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
	
	public GameDataOutput getLeagueStandingsBySeason(String originalGameData, String gameType) throws Exception
	{
		GameDataOutput gameDataOutput = new GameDataOutput(originalGameData);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	DocumentBuilder db;
    	Document doc;
    	db = dbf.newDocumentBuilder();
		doc = db.parse(new InputSource(new ByteArrayInputStream(originalGameData.getBytes("utf-8"))));
		NodeList teamLeagueStanding = doc.getElementsByTagName(GameDataConstants.xmlTag_TeamLeagueStanding);
		
		for (int i=0; i<teamLeagueStanding.getLength(); i++)
    	{
    		 Node teamLeagueStandingNode = teamLeagueStanding.item(i);
    		 Element teamLeagueStandingElement = (Element) teamLeagueStandingNode;
    		 NodeList teamNameNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_TeamName);
    		 NodeList teamIdNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_TeamId);
    		 NodeList playedNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_Played);
    		 NodeList playedAtHomeNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_PlayedAtHome);
    		 NodeList playedAwayNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_PlayedAway);
    		 NodeList wonNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_Won);
    		 NodeList drawNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_Draw);
    		 NodeList lostNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_Lost);
    		 NodeList pointsNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_Points);
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
    		 
    		 if (gameType.equalsIgnoreCase(GameDataConstants.gameType_Soccer))
    		 {
    			 NodeList numberOfShotsNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_NumberOfShots);
    			 NodeList yellowCardsNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_YellowCards);
    			 NodeList redCardsNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_RedCards);
    			 NodeList goalsForNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_GoalsFor);
    			 NodeList goalsAgainstNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_GoalsAgainst);
    			 NodeList goalDifferenceNodeList = teamLeagueStandingElement.getElementsByTagName(GameDataConstants.xmlTag_GoalDifference);
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
