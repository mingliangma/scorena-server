//
//  MDb.m
//
//
//  Created by Kun on 13-02-17.
//
//

#import "MDb.h"

@implementation MDb



-(id)init{
    if(self == [super init]){
        
        NSFileManager* manager = [NSFileManager defaultManager];
        NSString* dbFile = [MUtil appDocumentsDir:@"Entrapment.epub"];
        BOOL exists = [manager fileExistsAtPath:dbFile];
        
        NSString* dbResource = [[NSBundle mainBundle] pathForResource:@"Entrapment" ofType:@"epub" inDirectory:nil];
        NSError* error;
        
        if(!exists){
            if ( [[NSFileManager defaultManager] copyItemAtPath:dbResource toPath:dbFile error:&error]){
                [self loadDBFromFile:dbFile];
            }
        }else{
            [self loadDBFromFile:dbFile];
        }
        
        DD(@"[Database] Exists: %@",exists?@"YES":@"NO");
    }
    return self;
}

-(void)loadDBFromFile:(NSString*)dbFile{
    int result = sqlite3_open([dbFile UTF8String], &_db);
    
    if (sqlite3_exec(_db, (const char*) "SELECT count(*) FROM sqlite_master;", NULL, NULL, NULL) == SQLITE_OK) {
        // password is correct, or, database has been initialized
        DD(@"Password is correct");
    } else {
        DD(@"Incorrect password");
        // incorrect password!
    }
     
    if(!result==SQLITE_OK){
        DD(@"[Database] ERROR: Opening DB: %d :%@",result,dbFile);
    }
}

-(void)close{
	sqlite3_close(_db);
}


#pragma mark - Database API Methods


-(BOOL)iterateWithSql:(NSString*)sqlStatement handleBlock:(void (^)(sqlite3_stmt* compiledStatement,BOOL result))handleBlock{
    
    sqlite3_stmt* compiledStatement;
    //NSMutableArray* result = [[NSMutableArray alloc] init];
    
    BOOL result= YES;
    
    int stat = sqlite3_prepare_v2(_db, [sqlStatement UTF8String], -1, &compiledStatement, NULL);
    if( stat == SQLITE_OK){
        while(sqlite3_step(compiledStatement) == SQLITE_ROW){
            handleBlock(compiledStatement,result);
        }
    }else{
        DD(@"[Database] ERROR: %d : %@",stat,sqlStatement);
    }
    
    sqlite3_finalize(compiledStatement);
    return result;
}


/**
 *  Helper method for retrieving a list of data from a single sql statement
 *
 *  @param sqlStatement The sql statement to execute for the data load
 *  @param handleBlock The block to execute for the processing for each of the row being returned by the sql statement
 *
 *  @result result The resulting NSMutableArray object containing processed data
 */
-(NSMutableArray*)loadSqlData:(NSString*)sqlStatement handleBlock:(void (^)(sqlite3_stmt* compiledStatement,NSMutableArray* result))handleBlock{
    
    sqlite3_stmt* compiledStatement;
    NSMutableArray* result = [[NSMutableArray alloc] init];
    
    int stat = sqlite3_prepare_v2(_db, [sqlStatement UTF8String], -1, &compiledStatement, NULL);
    if( stat == SQLITE_OK){
        while(sqlite3_step(compiledStatement) == SQLITE_ROW){
            handleBlock(compiledStatement,result);
        }
    }else{
        DD(@"[Database] ERROR: %d : %@",stat,sqlStatement);
    }
    
    sqlite3_finalize(compiledStatement);
    return result;
}


-(id)loadSingleRecord:(NSString*)sqlStatement ofClass:(NSString*)class handleBlock:(void (^)(sqlite3_stmt* compiledStatement,id result))handleBlock{
    
    sqlite3_stmt* compiledStatement;
    id result = [[NSClassFromString(class) alloc] init];
    
    int stat = sqlite3_prepare_v2(_db, [sqlStatement UTF8String], -1, &compiledStatement, NULL);
    if( stat == SQLITE_OK){
        while(sqlite3_step(compiledStatement) == SQLITE_ROW){
            handleBlock(compiledStatement,result);
        }
    }else{
        DD(@"[Database] ERROR: %d : Loading Single Record of Class: %@",stat,class);
    }
    
    sqlite3_finalize(compiledStatement);
    return result;
}

-(BOOL)executeSql:(NSString*)sql{
    
    sqlite3_stmt* compiledStatement;
    
    int stat = sqlite3_prepare_v2(_db, [sql UTF8String], -1, &compiledStatement, NULL);
    if( stat == SQLITE_OK){
        while(sqlite3_step(compiledStatement) == SQLITE_ROW){
            DD(@"[Database] Success: %@",sql);
        }
    }else{
        DD(@"[Database] ERROR: %d : %@",stat,sql);
    }
    sqlite3_finalize(compiledStatement);
    
    return stat==SQLITE_OK;
}

-(BOOL)checkExistenceIn:(NSString*)table column1:(NSString*)col1 value1:(NSString*)val1 column2:(NSString*)col2 value2:(NSString*)val2{
    
    NSString* sql = [NSString stringWithFormat:@"select count(1) from %@ where %@ =='%@' and %@ =='%@'",table,col1,[val1 sqlEscape],col2,[val2 sqlEscape]];
    
    sqlite3_stmt* compiledStatement;
    
    int stat = sqlite3_prepare_v2(_db,[sql UTF8String],-1,&compiledStatement,NULL);
    if(stat == SQLITE_OK){
        while(sqlite3_step(compiledStatement) == SQLITE_ROW){
            int result = sqlite3_column_int(compiledStatement, 0);
            if(result>0){
                return YES;
            }
        }
    }else{
        DD(@"[Database] ERROR: %d : %@",stat,sql);
    }
    
    return NO;
}

-(int)loadCountWithSql:(NSString*)sql{

    sqlite3_stmt* compiledStatement;
    int result=0;
    
    int stat = sqlite3_prepare_v2(_db, [sql UTF8String], -1, &compiledStatement, NULL);
    if( stat == SQLITE_OK){
        while(sqlite3_step(compiledStatement) == SQLITE_ROW){
            result = sqlite3_column_int(compiledStatement,0);
        }
    }else{
        DD(@"[Database] ERROR: %d : Loading Count With Sql: %@",stat,sql);
    }
    
    sqlite3_finalize(compiledStatement);
    return result;
}


-(BOOL)updateSingleField:(NSString*)table value:(NSString*)value key:(NSString*)key lookupField:(NSString*)field lookupValue:(NSString*)lookupVal{
    NSString* sql = [NSString stringWithFormat:@"update %@ set %@=%@ where %@==%@",table,key,value,field,lookupVal];
    
    if([self executeSql:sql]){
        DD(@"[Database] Success: %@",sql)
    }else{
        return NO;
    }
    return YES;
}


@end
