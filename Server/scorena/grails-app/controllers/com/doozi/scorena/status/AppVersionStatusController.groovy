package com.doozi.scorena.status

import grails.converters.JSON

class AppVersionStatusController {

    def getAndroidCurrentVersion() {
		AppVersionStatus clientStatus = AppVersionStatus.findByClientTypeAndVersionStatus(ClientTypeEnum.ANDROID, VersionStatusEnum.ACTIVE);
		Map result = [version_code: clientStatus.versionCode, content: clientStatus.content]
		render result as JSON
	}
	
	def setAndroidCurrentVersion(){
		AppVersionStatus v = new AppVersionStatus(versionName: "1.1.2", versionCode: 20, content: "Version 2.0 ", 
			clientType: ClientTypeEnum.ANDROID,  versionStatus: VersionStatusEnum.ACTIVE)
		v.save()
	}
}
