package com.doozi.scorena.gamedata.team

import grails.transaction.Transactional


class TeamLogoService {

	
	public static final String ALGERIA_TEAMID = "o.fifa.com-t.78"
	public static final String ARGENTINA_TEAMID = "o.fifa.com-t.623"
	public static final String AUSTRALIA_TEAMID = "o.fifa.com-t.313"
	public static final String BELGIUM_TEAMID = "o.fifa.com-t.252"
	public static final String BOSNIA_TEAMID = "o.fifa.com-t.PJ2"
	public static final String BRAZIL_TEAMID = "o.fifa.com-t.550"
	public static final String CAMEROON_TEAMID = "o.fifa.com-t.386"
	public static final String CHILE_TEAMID = "o.fifa.com-t.628"
	public static final String COLOMBIA_TEAMID = "o.fifa.com-t.PJ1"
	public static final String COSTARICA_TEAMID = "o.fifa.com-t.591"
	public static final String COTEDIVIRE_TEAMID = "o.fifa.com-t.717"
	public static final String CROATIA_TEAMID = "o.fifa.com-t.258"
	public static final String ECUADOR_TEAMID = "o.fifa.com-t.626"
	public static final String ENGLAND_TEAMID = "o.fifa.com-t.200"
	public static final String FRANCE_TEAMID = "o.fifa.com-t.248"
	public static final String GERMAN_TEAMID = "o.fifa.com-t.268"
	public static final String GHANA_TEAMID = "o.fifa.com-t.686"
	public static final String GREECE_TEAMID = "o.fifa.com-t.211"
	public static final String HONDURAS_TEAMID = "o.fifa.com-t.1099"
	public static final String IRAN_TEAMID = "o.fifa.com-t.639"
	public static final String ITALY_TEAMID = "o.fifa.com-t.259"
	public static final String JAPAN_TEAMID = "o.fifa.com-t.641"
	public static final String MEXICO_TEAMID = "o.fifa.com-t.545"
	public static final String NETHERLANDS_TEAMID = "o.fifa.com-t.263"
	public static final String NIGERIA_TEAMID = "o.fifa.com-t.1567"
	public static final String PORTUGAL_TEAMID = "o.fifa.com-t.265"
	public static final String RUSSIA_TEAMID = "o.fifa.com-t.276"
	public static final String SOUTHKOREA_TEAMID = "o.fifa.com-t.644"
	public static final String SPAIN_TEAMID = "o.fifa.com-t.249"
	public static final String SWITZERLAND_TEAMID = "o.fifa.com-t.280"
	public static final String USA_TEAMID = "o.fifa.com-t.592"
	public static final String URUGUAY_TEAMID = "o.fifa.com-t.2300"
	
	
	//World Cup Logo URL
	public static final String DEFAULT_LOGO_URL = "https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/default.png"
	public static final String ALGERIA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Algeria.png'
	public static final String ARGENTINA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Argentina.png'
	public static final String AUSTRALIA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Australia.png'
	public static final String BELGIUM_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Belgium.png'
	public static final String BOSNIA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Bosnia.png'
	public static final String BRAZIL_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Brazil.png'
	public static final String CAMEROON_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Cameroon.png'
	public static final String CHILE_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Chile.png'
	public static final String COLOMBIA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Colombia.png'
	public static final String COSTARICA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Costa_Rica.png'
	public static final String COTEDIVIRE_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Cote_Divoire.png'
	public static final String CROATIA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Croatia.png'
	public static final String ECUADOR_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Ecuador.png'
	public static final String ENGLAND_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/England.png'
	public static final String FRANCE_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/France.png'
	public static final String GERMAN_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/German.png'
	public static final String GHANA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Ghana.png'
	public static final String GREECE_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Greece.png'
	public static final String HONDURAS_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Honduras.png'
	public static final String IRAN_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Iran.png'
	public static final String ITALY_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Italy.png'
	public static final String JAPAN_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Japan.png'
	public static final String MEXICO_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Mexico.png'
	public static final String NETHERLANDS_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Netherlands.png'
	public static final String NIGERIA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Nigeria.png'
	public static final String PORTUGAL_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Portugal.png'
	public static final String RUSSIA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Russia.png'
	public static final String SOUTHKOREA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/South_Korea.png'
	public static final String SPAIN_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Spain.png'
	public static final String SWITZERLAND_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Switzerland.png'
	public static final String USA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/USA.png'
	public static final String URUGUAY_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/Uruguay.png'
	
	//EPL LOGO URL
	public static final String ARSENAL_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Arsenal+FC.png'
	public static final String ASTON_VILLA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Aston+Villa+FC.png'
	public static final String BURNLEY_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Burnley_Football_Club.png'
	public static final String CHELSEA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Chelsea+FC.png'
	public static final String CRYSTAL_PALACE_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Crystal+Palace.png'
	public static final String EVERTON_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Everton+FC.png'
	public static final String FULHAM_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Fulham+FC.png'
	public static final String HULL_CITY_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Hull_City_AFC_logo.png'
	public static final String LEICESTER_CITY_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Leicester_City_FC_logo.png'
	public static final String LIVERPOOL_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Liverpool+FC.png'
	public static final String MANCITY_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Manchester+City+FC.png'
	public static final String MANUNITED_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Manchester+United+FC.png'
	public static final String NEWCASTLE_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Newcastle+United+FC.png'
	public static final String QPR_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Queens+Park+Rangers+FC.png'
	public static final String SOUTHAMPTON_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Southampton+FC.png'
	public static final String STOKE_CITY_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Stoke+City+FC.png'
	public static final String SUNDERLAND_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Sunderland+FC.png'
	public static final String SWANSEA_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Swansea+City+AFC.png'
	public static final String TOTTENHAM_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/Tottenham+Hotspur+FC.png'
	public static final String WEST_BROM_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/West+Bromwich+Albion+FC.png'
	public static final String WEST_HAM_LOGO_URL = 'https://s3-us-west-2.amazonaws.com/teamlogo/EPL/West+Ham+United+FC.png'
	
	public static final String ALGERIA_TEAMNAME = "Algeria"
	public static final String ARGENTINA_TEAMNAME = "Argentina"
	public static final String AUSTRALIA_TEAMNAME = "Australia"
	public static final String BELGIUM_TEAMNAME = "Belgium"
	public static final String BOSNIA_TEAMNAME = "Bosnia"
	public static final String BRAZIL_TEAMNAME = "Brazil"
	public static final String CAMEROON_TEAMNAME = "Cameroon"
	public static final String CHILE_TEAMNAME = "Chile"
	public static final String COLOMBIA_TEAMNAME = "Colombia"
	public static final String COSTARICA_TEAMNAME = "Costa Rica"
	public static final String COTEDIVIRE_TEAMNAME = "Ivory Coast"
	public static final String CROATIA_TEAMNAME = "Croatia"
	public static final String ECUADOR_TEAMNAME = "Ecuador"
	public static final String ENGLAND_TEAMNAME = "England"
	public static final String FRANCE_TEAMNAME = "France"
	public static final String GERMAN_TEAMNAME = "German"
	public static final String GHANA_TEAMNAME = "Ghana"
	public static final String GREECE_TEAMNAME = "Greece"
	public static final String HONDURAS_TEAMNAME = "Honduras"
	public static final String IRAN_TEAMNAME = "Iran"
	public static final String ITALY_TEAMNAME = "Italy"
	public static final String JAPAN_TEAMNAME = "Japan"
	public static final String MEXICO_TEAMNAME = "Mexico"
	public static final String NETHERLANDS_TEAMNAME = "Netherlands"
	public static final String NIGERIA_TEAMNAME = "Nigeria"
	public static final String PORTUGAL_TEAMNAME = "Portugal"
	public static final String RUSSIA_TEAMNAME = "Russia"
	public static final String SOUTHKOREA_TEAMNAME = "Korea"
	public static final String SPAIN_TEAMNAME = "Spain"
	public static final String SWITZERLAND_TEAMNAME = "Switzerland"
	public static final String USA_TEAMNAME = "United States"
	public static final String URUGUAY_TEAMNAME = "Uruguay"
	
	public static final String ARSENAL_TEAMNAME = "Arsenal"
	public static final String ASTON_VILLA_TEAMNAME = "Aston Villa"
	public static final String BURNLEY_TEAMNAME = "Burnley"
	public static final String CHELSEA_TEAMNAME = "Chelsea"
	public static final String CRYSTAL_PALACE_TEAMNAME = "Crystal Palace"
	public static final String EVERTON_TEAMNAME = "Everton"
	public static final String FULHAM_TEAMNAME = "Fulham"
	public static final String HULL_CITY_TEAMNAME = "Hull City"
	public static final String LEICESTER_CITY_TEAMNAME = "Leicester City"
	public static final String LIVERPOOL_TEAMNAME = "Liverpool"
	public static final String MANCITY_TEAMNAME = "Manchester City"
	public static final String MANUNITED_TEAMNAME = "Manchester United"
	public static final String NEWCASTLE_TEAMNAME = "Newcastle"
	public static final String QPR_TEAMNAME = "Queens Park Rangers"
	public static final String SOUTHAMPTON_TEAMNAME = "Southampton"
	public static final String STOKE_CITY_TEAMNAME = "Stoke City"
	public static final String SUNDERLAND_TEAMNAME = "Sunderland"
	public static final String SWANSEA_TEAMNAME = "Swansea"
	public static final String TOTTENHAM_TEAMNAME = "Tottenham"
	public static final String WEST_BROM_TEAMNAME = "West Bromwich"
	public static final String WEST_HAM_TEAMNAME = "West Ham"
	
	
    String getTeamLogo(String teamName) {
		
		switch (teamName.toLowerCase()){
			case ALGERIA_TEAMNAME.toLowerCase():
				return ALGERIA_LOGO_URL
			case ARGENTINA_TEAMNAME.toLowerCase():
				return ARGENTINA_LOGO_URL
			case AUSTRALIA_TEAMNAME.toLowerCase():
				return AUSTRALIA_LOGO_URL
			case BELGIUM_TEAMNAME.toLowerCase():
				return BELGIUM_LOGO_URL
			case BOSNIA_TEAMNAME.toLowerCase():
				return BOSNIA_LOGO_URL
			case BRAZIL_TEAMNAME.toLowerCase():
				return BRAZIL_LOGO_URL
			case CAMEROON_TEAMNAME.toLowerCase():
				return CAMEROON_LOGO_URL
			case CHILE_TEAMNAME.toLowerCase():
				return CHILE_LOGO_URL
			case COLOMBIA_TEAMNAME.toLowerCase():
				return COLOMBIA_LOGO_URL
			case COSTARICA_TEAMNAME.toLowerCase():
				return COSTARICA_LOGO_URL
			case COTEDIVIRE_TEAMNAME.toLowerCase():
				return COTEDIVIRE_LOGO_URL
			case CROATIA_TEAMNAME.toLowerCase():
				return CROATIA_LOGO_URL
			case ECUADOR_TEAMNAME.toLowerCase():
				return ECUADOR_LOGO_URL
			case ENGLAND_TEAMNAME.toLowerCase():
				return ENGLAND_LOGO_URL
			case FRANCE_TEAMNAME.toLowerCase():
				return FRANCE_LOGO_URL
			case GERMAN_TEAMNAME.toLowerCase():
				return GERMAN_LOGO_URL
			case GHANA_TEAMNAME.toLowerCase():
				return GHANA_LOGO_URL
			case GREECE_TEAMNAME.toLowerCase():
				return GREECE_LOGO_URL
			case HONDURAS_TEAMNAME.toLowerCase():
				return HONDURAS_LOGO_URL
			case IRAN_TEAMNAME.toLowerCase():
				return IRAN_LOGO_URL
			case ITALY_TEAMNAME.toLowerCase():
				return ITALY_LOGO_URL
			case JAPAN_TEAMNAME.toLowerCase():
				return JAPAN_LOGO_URL
			case MEXICO_TEAMNAME.toLowerCase():
				return MEXICO_LOGO_URL
			case NETHERLANDS_TEAMNAME.toLowerCase():
				return NETHERLANDS_LOGO_URL
			case NIGERIA_TEAMNAME.toLowerCase():
				return NIGERIA_LOGO_URL
			case PORTUGAL_TEAMNAME.toLowerCase():
				return PORTUGAL_LOGO_URL
			case RUSSIA_TEAMNAME.toLowerCase():
				return RUSSIA_LOGO_URL
			case SOUTHKOREA_TEAMNAME.toLowerCase():
				return SOUTHKOREA_LOGO_URL
			case SPAIN_TEAMNAME.toLowerCase():
				return SPAIN_LOGO_URL
			case SWITZERLAND_TEAMNAME.toLowerCase():
				return SWITZERLAND_LOGO_URL
			case USA_TEAMNAME.toLowerCase():
				return USA_LOGO_URL
			case URUGUAY_TEAMNAME.toLowerCase():
				return URUGUAY_LOGO_URL
			case ARSENAL_TEAMNAME.toLowerCase():
				return ARSENAL_LOGO_URL
			case ASTON_VILLA_TEAMNAME.toLowerCase():
				return ASTON_VILLA_LOGO_URL
			case BURNLEY_TEAMNAME.toLowerCase():
				return BURNLEY_LOGO_URL
			case CHELSEA_TEAMNAME.toLowerCase():
				return CHELSEA_LOGO_URL
			case CRYSTAL_PALACE_TEAMNAME.toLowerCase():
				return CRYSTAL_PALACE_LOGO_URL
			case CHELSEA_TEAMNAME.toLowerCase():
				return CHELSEA_LOGO_URL
			case EVERTON_TEAMNAME.toLowerCase():
				return EVERTON_LOGO_URL
			case FULHAM_TEAMNAME.toLowerCase():
				return FULHAM_LOGO_URL
			case HULL_CITY_TEAMNAME.toLowerCase():
				return HULL_CITY_LOGO_URL
			case LEICESTER_CITY_TEAMNAME.toLowerCase():
				return LEICESTER_CITY_LOGO_URL
			case LIVERPOOL_TEAMNAME.toLowerCase():
				return LIVERPOOL_LOGO_URL
			case MANCITY_TEAMNAME.toLowerCase():
				return MANCITY_LOGO_URL
			case MANUNITED_TEAMNAME.toLowerCase():
				return MANUNITED_LOGO_URL
			case NEWCASTLE_TEAMNAME.toLowerCase():
				return NEWCASTLE_LOGO_URL
			case QPR_TEAMNAME.toLowerCase():
				return QPR_LOGO_URL
			case SOUTHAMPTON_TEAMNAME.toLowerCase():
				return SOUTHAMPTON_LOGO_URL
			case STOKE_CITY_TEAMNAME.toLowerCase():
				return STOKE_CITY_LOGO_URL
			case SUNDERLAND_TEAMNAME.toLowerCase():
				return SUNDERLAND_LOGO_URL
			case SWANSEA_TEAMNAME.toLowerCase():
				return SWANSEA_LOGO_URL
			case TOTTENHAM_TEAMNAME.toLowerCase():
				return TOTTENHAM_LOGO_URL
			case WEST_BROM_TEAMNAME.toLowerCase():
				return WEST_BROM_LOGO_URL
			case WEST_HAM_TEAMNAME.toLowerCase():
				return WEST_HAM_LOGO_URL
			default:
				return DEFAULT_LOGO_URL
		}
		

	}
}
