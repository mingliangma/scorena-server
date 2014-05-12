//
//  NSMutableString+MYSQL.m
//  takuya
//
//  Created by Kun on 13-09-09.
//
//

#import "NSMutableString+MYSQL.h"

@implementation NSMutableString (MYSQL)


-(void)appendSqlStr:(NSString*)str{
    
    if(!str||[str length]<1){
        [self appendString:@",''"];
    }else{
        [self appendFormat:@",\'%@\'",[self getEscapedString:str]];
    }
}

-(NSString*)getEscapedString:(NSString*)str{
    return [str stringByReplacingOccurrencesOfString:@"'" withString:@"''"];
}

-(void)appendSqlStrNoComma:(NSString*)str{
    [self appendFormat:@"\'%@\'",[self getEscapedString:str]];
}

-(void)appendSqlBool:(NSString*)val{
    [self appendFormat:@",%d",[val boolValue]];
}

-(void)appendSqlNumber:(NSString*)num{
    [self appendFormat:@",%d",[num intValue]];
}


@end
