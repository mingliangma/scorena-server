package com.doozi.scorena.status

import grails.converters.JSON

class AppVersionStatusController {

    def getAndroidCurrentVersion() {
		AppVersionStatus clientStatus = AppVersionStatus.findByClientTypeAndVersionStatus(ClientTypeEnum.ANDROID, VersionStatusEnum.ACTIVE);
		render clientStatus as JSON
	}
	
	def setAndroidCurrentVersion(){
		AppVersionStatus v = new AppVersionStatus(currentVersion: "1.1.1", content: "Version 2.0 <p>New features:</p><li>Added feature A</li><li>Added feature B</li><li>Added feature C</li><li>Added feature D</li><li>Added feature E</li><li>Added feature F</li><li>Added feature G</li>", 
			clientType: ClientTypeEnum.ANDROID,  versionStatus: VersionStatusEnum.ACTIVE)
		v.save()
	}
}
