package com.doozi.scorena.score
import com.doozi.scorena.Question


class QuestionScore extends AbstractScore{
	static belongsTo = [question: Question]
    static constraints = {
    }
}
