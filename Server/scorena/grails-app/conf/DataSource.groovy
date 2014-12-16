

dataSource 
{
    pooled = true
    driverClassName = "com.mysql.jdbc.Driver"
	dialect = org.hibernate.dialect.MySQL5InnoDBDialect
}

dataSource_sportsData 
{
	pooled = true
	driverClassName = "com.mysql.jdbc.Driver"
	dialect = org.hibernate.dialect.MySQL5InnoDBDialect
}


hibernate 
{
    cache.use_second_level_cache = false
    cache.use_query_cache = false
//    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
}

// environment specific settings
environments 
{
    development 
	{
        dataSource 
		{
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost/scorena_dev?useUnicode=yes&characterEncoding=UTF-8"
			username = "root"
			password = ""
        }
		
		dataSource_sportsData 
		{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			
			url = "jdbc:mysql://xmlinstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/sportsdb?user=dooziadmin&password=dooziadmin"

			properties 
			{
				maxActive = -1
				minEvictableIdleTimeMillis=1800000
				timeBetweenEvictionRunsMillis=1800000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=false
				validationQuery="SELECT 1"
				jdbcInterceptors="ConnectionState"
			 }
		}
    }
	
	awsdev
	{
		dataSource 
		{
						
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
//			url = System.getProperty("JDBC_CONNECTION_STRING")
//			url = "jdbc:mysql://scorenat3.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/scorenaT?user=scorenaadmin&password=scorenaadmin"   
			url = "jdbc:mysql://scorenarealdata.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/scorenaT?useUnicode=yes&characterEncoding=UTF-8"
			username = "dooziadmin"
			password = "dooziadmin"	
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = org.hibernate.dialect.MySQL5InnoDBDialect

			properties 
			{
				maxActive = -1
				minEvictableIdleTimeMillis=1800000
				timeBetweenEvictionRunsMillis=1800000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=false
				validationQuery="SELECT 1"
				jdbcInterceptors="ConnectionState"
			 }
		}
		
		dataSource_sportsData 
		{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''			
			url = "jdbc:mysql://xmlinstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/sportsdb?user=dooziadmin&password=dooziadmin"
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = org.hibernate.dialect.MySQL5InnoDBDialect
			hibernate {
				cache.use_second_level_cache=false
				cache.use_query_cache=false
			}			
			properties 
			{
				maxActive = -1
				minEvictableIdleTimeMillis=1800000
				timeBetweenEvictionRunsMillis=1800000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=false
				validationQuery="SELECT 1"
				jdbcInterceptors="ConnectionState"
			 }
		}
	}
	
	//jdbc:mysql://mydbinstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/scorenaT?user=scorenaadmin&password=scorenaadmin
	
    test 
	{ 
        dataSource 
		{
						
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
//			url = System.getProperty("JDBC_CONNECTION_STRING")
//			url = "jdbc:mysql://productioninstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/scorenaproductiondb?user=scorenauser&password=qeye2tarunA8"
//			url = "jdbc:mysql://scorenat3.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/scorenaT?user=scorenaadmin&password=scorenaadmin"   
			url = "jdbc:mysql://scorenarealdata.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/scorenaT?useUnicode=yes&characterEncoding=UTF-8"
			username = "dooziadmin"
			password = "dooziadmin"	
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = org.hibernate.dialect.MySQL5InnoDBDialect

			properties 
			{
				maxActive = -1
				minEvictableIdleTimeMillis=1800000
				timeBetweenEvictionRunsMillis=1800000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=false
				validationQuery="SELECT 1"
				jdbcInterceptors="ConnectionState"
			 }
		}
		
		dataSource_sportsData 
		{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''			
			url = "jdbc:mysql://xmlinstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/sportsdb?user=dooziadmin&password=dooziadmin"
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = org.hibernate.dialect.MySQL5InnoDBDialect
			
			properties 
			{
				maxActive = -1
				minEvictableIdleTimeMillis=1800000
				timeBetweenEvictionRunsMillis=1800000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=false
				validationQuery="SELECT 1"
				jdbcInterceptors="ConnectionState"
			 }
		}
    }
	
    production 
	{
        dataSource 
		{
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url =System.getProperty("JDBC_CONNECTION_STRING")
			
            properties 
			{
               //max connections is 310
				initialSize=10
		      maxActive=190
		      maxIdle=110
		      minIdle=30
               minEvictableIdleTimeMillis=55000
               timeBetweenEvictionRunsMillis=34000
			   validationQuery="SELECT 1"
			   validationInterval=30000
               testOnBorrow=true
			   removeAbandoned="true"
			   removeAbandonedTimeout=55
			   abandonWhenPercentageFull=100
               jdbcInterceptors="ConnectionState"
            }
        }
		
		dataSource_sportsData
		{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:mysql://xmlinstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/sportsdb?user=dooziadmin&password=dooziadmin"
			pooled = true
			driverClassName = "com.mysql.jdbc.Driver"
			dialect = org.hibernate.dialect.MySQL5InnoDBDialect
			
			properties
			{
			   initialSize=10
			   maxActive=190
			   maxIdle=110
			   minIdle=30
               minEvictableIdleTimeMillis=55000
               timeBetweenEvictionRunsMillis=34000
			   validationQuery="SELECT 1"
			   validationInterval=30000
               testOnBorrow=true
			   removeAbandoned="true"
			   removeAbandonedTimeout=55
			   abandonWhenPercentageFull=100
               jdbcInterceptors="ConnectionState"
			 }
		}
    }
	
	heng 
	{
		dataSource 
		{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			
			/*
			url = "jdbc:mysql://hengmysql.cce59dcpxmml.us-west-2.rds.amazonaws.com/HengDb?useUnicode=yes&characterEncoding=UTF-8"
			username = "HengMySql"
			password = "HengMySql"
			*/
			
			url = "jdbc:mysql://54.186.147.78/sportsdb?useUnicode=yes&characterEncoding=UTF-8"
			username = "scorenaadmin"
			password = "scorenaadmin"
		}
		
		dataSource_sportsData 
		{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:mysql://xmlinstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/sportsdb?user=dooziadmin&password=dooziadmin"
			
			properties 
			{
				maxActive = -1
				minEvictableIdleTimeMillis=1800000
				timeBetweenEvictionRunsMillis=1800000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=false
				validationQuery="SELECT 1"
				jdbcInterceptors="ConnectionState"
			 }
		}
	}
	
	james
	{
		dataSource
		{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:mysql://localhost/jamesdb?useUnicode=yes&characterEncoding=UTF-8"
			username = "root"
			password = "root"
		}
		
		dataSource_sportsData
		{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			
			url = "jdbc:mysql://xmlinstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/sportsdb?user=dooziadmin&password=dooziadmin"

			properties
			{
				maxActive = -1
				minEvictableIdleTimeMillis=1800000
				timeBetweenEvictionRunsMillis=1800000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=false
				validationQuery="SELECT 1"
				jdbcInterceptors="ConnectionState"
			 }
		}
	}
	joel
	{
	dataSource
	{
		dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
		url = "jdbc:mysql://localhost/scorena?useUnicode=yes&characterEncoding=UTF-8"
		username = "root"
		password = ""
	}
	
	dataSource_sportsData
	{
		dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
		
		url = "jdbc:mysql://xmlinstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/sportsdb?user=dooziadmin&password=dooziadmin"

		properties
		{
			maxActive = -1
			minEvictableIdleTimeMillis=1800000
			timeBetweenEvictionRunsMillis=1800000
			numTestsPerEvictionRun=3
			testOnBorrow=true
			testWhileIdle=true
			testOnReturn=false
			validationQuery="SELECT 1"
			jdbcInterceptors="ConnectionState"
		 }
	}
	}
	thomas
	{
		dataSource
		{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = "jdbc:mysql://localhost/scorena_test?useUnicode=yes&characterEncoding=UTF-8"
			username = "scorena_admin"
			password = "scorena_admin"
		}
		
		dataSource_sportsData
		{
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			
			url = "jdbc:mysql://xmlinstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/sportsdb?user=dooziadmin&password=dooziadmin"

			properties
			{
				maxActive = -1
				minEvictableIdleTimeMillis=1800000
				timeBetweenEvictionRunsMillis=1800000
				numTestsPerEvictionRun=3
				testOnBorrow=true
				testWhileIdle=true
				testOnReturn=false
				validationQuery="SELECT 1"
				jdbcInterceptors="ConnectionState"
			 }
		}
	}
}
