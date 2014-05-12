//
//  NSArray+MRandom.m
//  takuya
//
//  Created by Kun on 2013-09-17.
//
//

#import "NSArray+MRandom.h"

@implementation NSArray (MRandom)

-(id)randomObject{
    if([self count]==0){
        return NULL;
    }
    int rand = [self count];
    return [self objectAtIndex:(arc4random() % rand)];
}
@end
