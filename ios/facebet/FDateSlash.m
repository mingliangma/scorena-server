//
//  FDateSlash.m
//  facebet
//
//  Created by Kyle on 2014-04-24.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FDateSlash.h"

@implementation FDateSlash

@synthesize game,slashGray,redTime,monthLabel,yearLabel;


- (id)initWithFrame:(CGRect)frame game:(FGame*)myGame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        self.game = myGame;
        [self loadUI];
    }
    return self;
}



-(void)loadUI{
    
    slashGray = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Slash_Gray.png"]];
    [slashGray setFrame:CGRectMake(0, 0, 64, 52.5)];
    
//    [slashGray moveDownBy:2.5 duration:0];
    
    monthLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 3, 50, 25)];
    [monthLabel setFont:[UIFont fStraightFont:12]];
    [monthLabel setTextColor:[UIColor fBlueColor]];
    
    yearLabel = [[UILabel alloc] initWithFrame:CGRectMake(10, 17, 40, 30)];
    [yearLabel setFont:[UIFont fStraightFont:14]];
    [yearLabel setTextColor:[UIColor fBlueColor]];
    
    redTime = [[UIView alloc] initWithFrame:CGRectMake(10, 23, 34, 1)];
    [redTime setBackgroundColor:[UIColor fRedColor]];
    
    [monthLabel setText:[[game gameTime] dateSlash]];
    [yearLabel setText:[[game gameTime] yearString]];
    
    [self addSubview:slashGray];
    
    [self addSubview:monthLabel];
    [self addSubview:yearLabel];
    [self addSubview:redTime];
    /*
    if([self.game getGameStat] == FGameStatusMidEvent||[self.game getGameStat] ==FGameStatusIntermission||[[self.game gameTime] isToday]){
        timeLB = [[UILabel alloc] initWithFrame:CGRectMake(10, 38, 40, 10)];
        [timeLB setFont:[UIFont systemFontOfSize:7]];
        [timeLB setTextColor:[UIColor fRedColor]];
        [timeLB setText:[[game gameTime] timeString]];
        [self addSubview:timeLB];
    }
     */
}

@end
