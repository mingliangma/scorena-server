//
//  FUGameType.m
//  facebet
//
//  Created by Kun on 2013-12-30.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FUGameType.h"

@implementation FUGameType

@synthesize gameTypeLB,whiteBar,arrow,ppl,crowdLB,pickView;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self loadUI];
    }
    return self;
}

-(void)setupContent:(id)obj{
    FGame* myBet = (FGame*)obj;
    [gameTypeLB setText:[MUtil gameTypeString:[myBet type]]];
    [crowdLB setText:[self totalNumberString:myBet]];
    
    if(![obj placedBet]){
        [pickView setHidden:YES];
    }else{
        if([obj winnerPick] == FWinnerPickNone){
            [pickView setHidden:NO];
        }else if([obj userPickStatus] == FUserPickStatusWin){
            [pickView setImage:[UIImage imageNamed:@"corner_win.png"]];
        }else if([obj userPickStatus] == FUserPickStatusLose){
            [pickView setImage:[UIImage imageNamed:@"corner_loss.png"]];
        }else if([obj userPickStatus] == FUserPickStatusTie){
            [pickView setImage:[UIImage imageNamed:@"corner_tie.png"]];
        }
    }
}

-(NSString*)totalNumberString:(FGame*)game{
    NSInteger pick1 = [[[game pool] objectForKey:@"pick1NumPeople"] integerValue];
    NSInteger pick2 = [[[game pool] objectForKey:@"pick2NumPeople"] integerValue];
    NSInteger total = pick1+pick2;
    
    return [NSString stringWithFormat:@"%d",total];
}


-(void)loadUI{
    
    whiteBar = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Bar_Shadow.png"]];

    gameTypeLB = [[UILabel alloc] initWithFrame:CGRectMake(50, 8, 280, 40)];
    [gameTypeLB setFont:[UIFont fStraightFont:14]];
    [gameTypeLB setTextColor:[UIColor fGrayColor]];
    
    ppl = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ppl_green.png"]];
    
    crowdLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 30, 25, 20)];
    [crowdLB setFont:[UIFont fStraightFont:10]];
    [crowdLB setTextColor:[UIColor fGreenColor]];
    [crowdLB setTextAlignment:NSTextAlignmentRight];
    
    [ppl shift:15 y:12];
    
    [self addSubview:whiteBar];
    [self addSubview:gameTypeLB];
    [self addSubview:ppl];
    [self addSubview:crowdLB];
    
    pickView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"corner_pick.png"]];
    [pickView shift:2.5 y:2.5];
    [self addSubview:pickView];
    
    
}

@end
