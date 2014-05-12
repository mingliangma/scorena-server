//
//  NSString+MSQL.m
//  takuya
//
//  Created by Kun on 13-02-17.
//
//

#import "NSString+MSQL.h"


@implementation NSString (MSQL)

+(NSString*)fromSQL:(sqlite3_stmt*)stmt col:(int)col{
    NSString* result =[NSString stringWithUTF8String:(char*)sqlite3_column_text(stmt, col)];
    if(result && [result length]>0){
        return result;
    }else{
        return @"";
    }
}


-(NSString*)sqlEscape{
    return [self stringByReplacingOccurrencesOfString:@"'" withString:@"''"];
}


@end
