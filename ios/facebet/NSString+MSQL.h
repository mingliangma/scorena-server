//
//  NSString+MSQL.h
//  takuya
//
//  Created by Kun on 13-02-17.
//
//

#import <Foundation/Foundation.h>

@interface NSString (MSQL)

+(NSString*)fromSQL:(sqlite3_stmt*)stmt col:(int)col;

-(NSString*)sqlEscape;

@end
