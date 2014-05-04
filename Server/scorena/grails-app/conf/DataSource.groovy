

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
    cache.use_second_level_cache = true
    cache.use_query_cache = true
    cache.region.factory_class = 'net.sf.ehcache.hibernate.EhCacheRegionFactory' // Hibernate 3
    //cache.region.factory_class = 'org.hibernate.cache.ehcache.EhCacheRegionFactory' // Hibernate 4
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
	
	//jdbc:mysql://mydbinstance.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/scorenaT?user=scorenaadmin&password=scorenaadmin
	
    test 
	{ 
        dataSource 
		{
						
			dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
			url = System.getProperty("JDBC_CONNECTION_STRING")
//			url = "jdbc:mysql://scorenat3.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/scorenaT?user=scorenaadmin&password=scorenaadmin"   
//			url = "jdbc:mysql://scorenarealdata.cce59dcpxmml.us-west-2.rds.amazonaws.com:3306/scorenaT?useUnicode=yes&characterEncoding=UTF-8"
//			username = "dooziadmin"
//			password = "dooziadmin"	
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
			//url = "jdbc:mysql://aa1cnaol294hlvt.cce59dcpxmml.us-west-2.rds.amazonaws.com/ebdb?useUnicode=yes&characterEncoding=UTF-8"
			//username = "scorenaadmin"
			//password = "scorenaadmin"    
			
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
}
