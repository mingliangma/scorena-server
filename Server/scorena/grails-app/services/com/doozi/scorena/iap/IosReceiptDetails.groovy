package com.doozi.scorena.iap

class IosReceiptDetails {

			// bundle id of product 
			public String bundle_id;
			
			// app version being used to purchase 
			public String application_version;
			
			// original app version 
			public String original_application_version;
			
			// list object of in app receipt fields  
			public List<IosInapp> in_app;

		
    static constraints = {
    }
}
