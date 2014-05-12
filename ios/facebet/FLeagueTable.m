//
//  FLeagueTable.m
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FLeagueTable.h"

#import "FLeagueStat.h"

@implementation FLeagueTable

@synthesize parentVC;

- (id)initWithPoint:(CGPoint)point{
    self = [super initWithFrame:CGRectMake(point.x, point.y, 310, 0+[MUtil extraHeight])];
    [self setBackgroundColor:[UIColor whiteColor]];
    return self;
}

-(void)configureWithLeagueJson:(id)obj{
    NSArray* keys = [obj allKeys];
    
    int i;
    for(i=0;i<[keys count];i++){
        NSString* str = (NSString*)[keys objectAtIndex:i];
        id tmp = [obj objectForKey:str];
        
        FLeagueStat* stat = [[FLeagueStat alloc] initWithPoint:CGPointMake(0, i*60)];
        [stat configureWithJsonObj:tmp title:str];
        [self addSubview:stat];
    }
    
    CGFloat oldHeight = self.frame.size.height;
    CGFloat newHeight = 60.0*i;
    
    [self setFrame:CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.width, oldHeight<newHeight?newHeight:oldHeight)];
}

@end
