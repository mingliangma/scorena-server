//
//  FDetailHead.m
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FDetailHead.h"

@implementation FDetailHead

@synthesize statusLB,teamA,teamB,vsLB;

- (id)initWithPoint:(CGPoint)point{
    self = [[[NSBundle mainBundle] loadNibNamed:@"FDetailHead" owner:nil options:nil] objectAtIndex:0];
    return self;
}

-(void)configureWithGame:(FGame*)game{
 //   NSString *uppercaseString = [[game teamA].teamName uppercaseString];
    
   [teamA setText:[game teamA].teamName] ;
   // [teamA setText:[[game teamA].teamName uppercaseString]];
    [teamB setText:[game teamB].teamName];
    [statusLB setText:[game gameStatString]];
    [vsLB setText:[game vsString]];
    [vsLB setTextColor:[game vsColor]];
}

-(void)updateGameScoreWithJsonObj:(id)obj{
//    sfv(obj)
    [vsLB setText:[FGame vsStringFromJson:obj]];
 
 
}

@end
