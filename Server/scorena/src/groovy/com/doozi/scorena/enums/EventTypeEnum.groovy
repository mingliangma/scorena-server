package com.doozi.scorena.enums

public enum EventTypeEnum {
	PREEVENT("pre-event"), 
	MIDEVENT("mid-event"),
	POSTEVENT("post-event"),
	POSTPONED("postponed")
	
    final String value

    EventTypeEnum(String value) { this.value = value }

    String toString() { value } 
    String getKey() { name() }
}
