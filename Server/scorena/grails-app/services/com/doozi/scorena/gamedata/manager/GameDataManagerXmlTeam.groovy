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

import com.doozi.scorena.gamedata.helper.GameDataConstantsXmlTeam;
import com.doozi.scorena.gamedata.userinput.GameDataInputXmlTeam;
import com.doozi.scorena.gamedata.useroutput.GameDataEventXmlTeam;
import com.doozi.scorena.gamedata.useroutput.GameDataOutput;

/**
 * @author Heng
 *
 */
public class GameDataManagerXmlTeam implements IGameDataManager {

	private static final GameDataManagerXmlTeam _gameDataManagerXmlTeamInstance = new GameDataManagerXmlTeam();
	
	/**
	 * 
	 */
	private GameDataManagerXmlTeam() 
	{
		
	}
	
	public static GameDataManagerXmlTeam get_gameDataManagerXmlTeamInstance() 
	{
		return _gameDataManagerXmlTeamInstance;
	}
	
	public GameDataOutput retrieveGameData(GameDataInputXmlTeam gameDataInputXmlTeam) throws Exception
	{
		BufferedReader bufferedReader = null;
		
		try
		{
			final URL apiUrl = new URL(gameDataInputXmlTeam.get_apiUrl());
			bufferedReader = new BufferedReader(new InputStreamReader(apiUrl.openStream()));
			final StringBuffer stringBuffer = new StringBuffer();
			int read;
			final char[] chars = new char[1024];
			
			while ((read = bufferedReader.read(chars)) != -1)
			{
				stringBuffer.append(chars, 0, read);
			}
			
			String originalGameData = stringBuffer.toString();
			
			if (gameDataInputXmlTeam.get_apiMethod().equalsIgnoreCase(GameDataConstantsXmlTeam.apiMethod_GetLeagueScheduleBySeason))
			{
				return getLeagueScheduleBySeason(originalGameData, gameDataInputXmlTeam.get_gameType());
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
		NodeList sportsEvents = doc.getElementsByTagName(GameDataConstantsXmlTeam.xmlTag_sportsEvent);		
				
		for(int i =0; i<sportsEvents.getLength(); i++)
		{
			Node eventNode = sportsEvents.item(i);
			Element eventElement = (Element) eventNode;
			GameDataEventXmlTeam gameDataEventXmlTeam = new GameDataEventXmlTeam();
			
			Node eventMetadata = 
					eventElement.getElementsByTagName(GameDataConstantsXmlTeam.xmlTag_EventMetadata).item(0);
			
			if (eventMetadata.hasAttributes())
			{
				NamedNodeMap metadataAttr = eventMetadata.getAttributes();
				gameDataEventXmlTeam.setGameEventId(metadataAttr.getNamedItem("event-key").getNodeValue());
			}
		}
		
		return gameDataOutput;
	}
}
