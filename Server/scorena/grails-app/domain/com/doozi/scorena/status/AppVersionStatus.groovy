package com.doozi.scorena.status

class AppVersionStatus {
	ClientTypeEnum clientType
	String versionName
	int versionCode
	String content
	VersionStatusEnum versionStatus
	
    static constraints = {
		versionCode unique: true
    }
}
