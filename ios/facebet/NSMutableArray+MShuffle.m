//
//  NSMutableArray+MShuffle.m
//  takuya
//
//  Created by Kun on 13-02-17.
//
//

#import "NSMutableArray+MShuffle.h"

@implementation NSMutableArray (MShuffle)


-(void)shuffle{
    
    for(NSUInteger i = [self count]; i > 1; i--) {
        NSUInteger j = arc4random() % i;
        [self exchangeObjectAtIndex:i-1 withObjectAtIndex:j];
    }
    
}

-(void)fillWithEmptyArray:(NSInteger)num{
    for(int i=0;i<num;i++){
        NSMutableArray* tmp = [[NSMutableArray alloc] init];
        [self addObject:tmp];
    }
}

@end
