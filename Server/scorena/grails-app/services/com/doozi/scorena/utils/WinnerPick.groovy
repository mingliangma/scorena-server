package com.doozi.scorena.utils

//  winnerPick = -1, the game has not finished yet, the winnerPick is not available
//  winnerPick = 0, the game is tied
//  winnerPick = 1, pick 1 won the game
//  winnerPick = 2, pick 2 won the game


class WinnerPick {
	public static final int GAME_NOT_FINISHED = -1
	public static final int PICK_TIE = 0
	public static final int PICK1_WON= Pick.PICK1
	public static final int PICK2_WON = Pick.PICK2
}
