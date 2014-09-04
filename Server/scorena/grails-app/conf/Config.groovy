// locations to search for config files that get merged into the main config;
// config files can be ConfigSlurper scripts, Java properties files, or classes
// in the classpath in ConfigSlurper format

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if (System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }

grails.project.groupId = appName // change this to alter the default package name and Maven publishing destination

// The ACCEPT header will not be used for content negotiation for user agents containing the following strings (defaults to the 4 major rendering engines)
grails.mime.disable.accept.header.userAgents = ['Gecko', 'WebKit', 'Presto', 'Trident']
grails.mime.types = [
    all:           '*/*',
    atom:          'application/atom+xml',
    css:           'text/css',
    csv:           'text/csv',
    form:          'application/x-www-form-urlencoded',
    html:          ['text/html','application/xhtml+xml'],
    js:            'text/javascript',
    json:          ['application/json', 'text/json'],
    multipartForm: 'multipart/form-data',
    rss:           'application/rss+xml',
    text:          'text/plain',
    hal:           ['application/hal+json','application/hal+xml'],
    xml:           ['text/xml', 'application/xml']
]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// What URL patterns should be processed by the resources plugin
grails.resources.adhoc.patterns = ['/images/*', '/css/*', '/js/*', '/plugins/*']

// Legacy setting for codec used to encode data with ${}
grails.views.default.codec = "html"

// The default scope for controllers. May be prototype, session or singleton.
// If unspecified, controllers are prototype scoped.
grails.controllers.defaultScope = 'singleton'

// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside ${}
                scriptlet = 'html' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}


 
grails.converters.encoding = "UTF-8"
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder = false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []
// whether to disable processing of multi part requests
grails.web.disable.multipart=false

// request parameters to mask when logging exceptions
grails.exceptionresolver.params.exclude = ['password']

// configure auto-caching of queries by default (if false you can cache individual queries with 'cache: true')
grails.hibernate.cache.queries = false



environments {
    development {
        grails.logging.jul.usebridge = true
		parse.parseApplicationId='kEepaeRdIInsSbptLl4lZ6xiZg7nv7bEOV56ym9m'
		parse.parseRestApiKey ='QSBmZXNUjRMtKwWcqB7jLQgtZizCSMBBeoBQHIql'
    }
	
	prodtesting {
		grails.logging.jul.usebridge = true
		parse.parseApplicationId='kEepaeRdIInsSbptLl4lZ6xiZg7nv7bEOV56ym9m'
		parse.parseRestApiKey ='QSBmZXNUjRMtKwWcqB7jLQgtZizCSMBBeoBQHIql'
	}
	
	test{
		grails.logging.jul.usebridge = true
		parse.parseApplicationId=System.getProperty("PARAM1")
		parse.parseRestApiKey =System.getProperty("PARAM2")
		
//		scorena real data/scorenat-env 
//		parse.parseApplicationId='b2cfHtl3JqMwg7rKfkoGJ7sh6LluiZc8RdjWCeQY'
//		parse.parseRestApiKey ='k6ybcRnaDAdfUBbLkSBfqiesECFQinEr6D0Lx8dL'
		
		//scorena test 3
//		parse.parseApplicationId='sxfzjYsgGiSXVwr7pj6vmaFR2f8ok9YGrnXGfx91'
//		parse.parseRestApiKey ='IQX6dOlw7KfsLmNw2tau0cGWsE4I3vBliCw67Ca3'
	}
    production {
        grails.logging.jul.usebridge = false
		parse.parseApplicationId=System.getProperty("PARAM1")
		parse.parseRestApiKey =System.getProperty("PARAM2")
		parse.parseApplicationId_t3=System.getProperty("PARAM3")
		parse.parseRestApiKey_t3 =System.getProperty("PARAM4")
    }
	
	heng {
		grails.logging.jul.usebridge = false
		parse.parseApplicationId='DtljOks86xBy35DigHgP17GoYerqBs0AW9xi0cH7'
		parse.parseRestApiKey ='sonR9xL3BEIsBziYUlY6fw2o7QWfjusjRs9mfeUg'
	}
	
	james {
		grails.logging.jul.usebridge = true
		parse.parseApplicationId='fTRyi6N1Mbiguvr5MnpUgbyfoz0mOUF5YomNHDlj'
		parse.parseRestApiKey ='7vZeox8CEOLh8RSBw7E7SbOtBmdxDIoLjL8e5gus'
	}
}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console appender:
    //
    //appenders {
    //    console name:'stdout', layout:pattern(conversionPattern: '%c{2} %m%n')
    //}

    error  'org.codehaus.groovy.grails.web.servlet',        // controllers
           'org.codehaus.groovy.grails.web.pages',          // GSP
           'org.codehaus.groovy.grails.web.sitemesh',       // layouts
           'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
           'org.codehaus.groovy.grails.web.mapping',        // URL mapping
           'org.codehaus.groovy.grails.commons',            // core / classloading
           'org.codehaus.groovy.grails.plugins',            // plugins
           'org.qcodehaus.groovy.grails.orm.hibernate',      // hibernate integration
           'org.springframework',
           'org.hibernate',
           'net.sf.ehcache.hibernate'
}
