package com.doozi.scorena.useraccount

class UserLeagueStats {
	private long accountId
	private String league
	private int playResult
	private long netGain
	private long numGames
	
	def sportsDataService
	
	UserLeagueStats(long accountId, String league, int playResult, long netGain, long numGames){
		this.accountId = accountId
		this.league = league
		this.playResult = playResult
		this.netGain = netGain
		this.numGames = numGames
	}
	public long getAccountId() {
		return accountId;
	}
	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}
	public String getLeague() {
		return league;
	}
	public void setLeague(String league) {
		this.league = league;
	}
	public int getPlayResult() {
		return playResult;
	}
	public void setPlayResult(int playResult) {
		this.playResult = playResult;
	}
	public long getNetGain() {
		return netGain;
	}
	public void setNetGain(long netGain) {
		this.netGain = netGain;
	}
	public long getNumGames() {
		return numGames;
	}
	public void setNumGames(long numGames) {
		this.numGames = numGames;
	}
	public java.lang.Object getSportsDataService() {
		return sportsDataService;
	}
	public void setSportsDataService(java.lang.Object sportsDataService) {
		this.sportsDataService = sportsDataService;
	}
	
	
	}
