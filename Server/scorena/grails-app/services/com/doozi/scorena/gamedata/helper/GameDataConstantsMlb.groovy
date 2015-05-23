package com.doozi.scorena.gamedata.helper

class GameDataConstantsMlb {
	public final static String YEAR_PREFIX = "/year_"
	public final static String MONTH_PREFIX = "/month_"
	public final static String DAY_PREFIX = "/day_"
	
	//event status sequence: preview -> pre-game -> inprogress -> final
	public final static String PREVIEW = "Preview"     
	public final static String PREVENT = "Pre-Game"
	public final static String MIDVENT = "In Progress"
	public final static String POSTVENT = "Final"
	
	public static String defaultHost = "http://mlb.mlb.com/";
	public static String defaultUrlPath = "/gdcross/components/game/mlb";
	public static String defaultScoreFileName = "/master_scoreboard.json";
}
