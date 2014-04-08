/**
 * 
 */
package com.doozi.scorena.gamedata.userinput;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.doozi.scorena.gamedata.helper.GameDataConstantsXmlSoccer;
import com.doozi.scorena.gamedata.useroutput.GameDataOutput;

/**
 * @author Heng
 *
 */
public class GameDataInputXmlTeam implements IGameDataInput {

	/**
	 * 
	 */
	public GameDataInputXmlTeam() 
	{
		
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

}
