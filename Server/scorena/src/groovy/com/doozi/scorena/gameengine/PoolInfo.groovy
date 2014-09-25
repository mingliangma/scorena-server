package com.doozi.scorena.gameengine

class PoolInfo {
	int pick1Amount
	int pick2Amount
	int pick1NumPeople
	int pick2NumPeople
	Date lastUpdate

	public int getPick1Amount() {
		return pick1Amount;
	}
	public void setPick1Amount(int pick1Amount) {
		this.pick1Amount = pick1Amount;
	}
	public int getPick2Amount() {
		return pick2Amount;
	}
	public void setPick2Amount(int pick2Amount) {
		this.pick2Amount = pick2Amount;
	}
	public int getPick1NumPeople() {
		return pick1NumPeople;
	}
	public void setPick1NumPeople(int pick1NumPeople) {
		this.pick1NumPeople = pick1NumPeople;
	}
	public int getPick2NumPeople() {
		return pick2NumPeople;
	}
	public void setPick2NumPeople(int pick2NumPeople) {
		this.pick2NumPeople = pick2NumPeople;
	}
	public Date getLastUpdate() {
		return lastUpdate;
	}
	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}}
