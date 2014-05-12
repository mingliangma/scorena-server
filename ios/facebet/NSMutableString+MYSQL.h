//
//  NSMutableString+MYSQL.h
//  takuya
//
//  Created by Kun on 13-09-09.
//
//

#import <Foundation/Foundation.h>

@interface NSMutableString (MYSQL)


-(void)appendSqlStr:(NSString*)str;
-(void)appendSqlBool:(NSString*)val;
-(void)appendSqlNumber:(NSString*)num;
-(void)appendSqlStrNoComma:(NSString*)str;

@end
