//
//  MDb.h
//  takuya
//
//  Created by Kun on 13-02-17.
//
//

#import <Foundation/Foundation.h>
#import "sqlite3.h"

@interface MDb : NSObject{
    sqlite3* _db;
}

-(void)close;

-(BOOL)iterateWithSql:(NSString*)sqlStatement handleBlock:(void (^)(sqlite3_stmt* compiledStatement,BOOL result))handleBlock;

-(NSMutableArray*)loadSqlData:(NSString*)sqlStatement handleBlock:(void (^)(sqlite3_stmt* compiledStatement,NSMutableArray* result))handleBlock;
-(id)loadSingleRecord:(NSString*)sqlStatement ofClass:(NSString*)myclass handleBlock:(void (^)(sqlite3_stmt* compiledStatement,id result))handleBlock;
-(BOOL)executeSql:(NSString*)sql;
-(BOOL)checkExistenceIn:(NSString*)table column1:(NSString*)col1 value1:(NSString*)val1 column2:(NSString*)col2 value2:(NSString*)val2;
-(BOOL)updateSingleField:(NSString*)table value:(NSString*)value key:(NSString*)key lookupField:(NSString*)field lookupValue:(NSString*)lookupVal;
-(int)loadCountWithSql:(NSString*)sql;
@end
