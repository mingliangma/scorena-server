package com.doozi.scorena.status

class AppVersionStatus {
	ClientTypeEnum clientType
	String currentVersion
	String content
	VersionStatusEnum versionStatus
	
    static constraints = {
		currentVersion unique: true
    }
}
