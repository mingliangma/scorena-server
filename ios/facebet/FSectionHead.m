//
//  FSectionHead.m
//  facebet
//
//  Created by Kun on 2013-12-29.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FSectionHead.h"

#define kSectionY 26

@implementation FSectionHead

@synthesize teamA,teamB,aLabel,bLabel,sign,topBar,slash,vsLabel;

@synthesize game,winLose,statLB;

//-(id)initWithFrame:(CGRect)frame teamA:(FTeam*)a teamB:(FTeam*)b{
-(id)initWithFrame:(CGRect)frame game:(FGame *)myGame{
    self = [super initWithFrame:frame];
    if(self){
        self.teamA = [[myGame teamA] teamName];
        self.teamB = [[myGame teamB] teamName];
        self.game = myGame;
        
        topBar = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 3)];
        [topBar setBackgroundColor:[UIColor fLightGrayColor]];
        
        vsLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 20, kSectionY)];
        aLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 90, 35 )];
        bLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 90, 35)];
        sign = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 15, kSectionY)];
        
        //set linenumber
        
        aLabel.lineBreakMode = NSLineBreakByWordWrapping;
        aLabel.numberOfLines = 0;
        
        bLabel.lineBreakMode = NSLineBreakByWordWrapping;
        bLabel.numberOfLines = 0;
        
        [vsLabel setFont:[UIFont fStraightFont:12]];
        
        [aLabel setFont:[UIFont fStraightFont:13]];
        [bLabel setFont:[UIFont fStraightFont:13]];
        
        [vsLabel setTextColor:[UIColor fGreenColor]];
        [aLabel setTextColor:[UIColor fBlueColor]];
        [bLabel setTextColor:[UIColor fBlueColor]];
        [sign setTextColor:[UIColor fDarkGreen]];
        
        [vsLabel setText:@" vs"];
       // [aLabel setText:[[myGame teamA] teamName]];
        //aLabel.numberOfLines=0;
        [aLabel setText:[[[myGame teamA] teamName] uppercaseString]];
       
        [bLabel setText:[[[myGame teamB] teamName] uppercaseString]];
        
        [aLabel setTextAlignment:NSTextAlignmentRight];
        
        [vsLabel shift:152 y:15];
        [aLabel shift:59 y:11];
        [bLabel shift:175 y:11];
        [sign shift:304 y:16];
    
        [self setBackgroundColor:[UIColor whiteColor]];        

        [self addSubview:vsLabel];
        [self addSubview:aLabel];
        [self addSubview:bLabel];
        [self addSubview:sign];
        [self addSubview:topBar];
        
        slash = [[FDateSlash alloc] initWithFrame:CGRectMake(0, 2, 63, 57) game:myGame];
        [self addSubview:slash];
        
        if([myGame placedBet]){
                winLose = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"corner_pick.png"]];
                [winLose shift:0 y:2.5];
                [self addSubview:winLose];
        }
        [self loadStatLabelWithString:[myGame gameStatString]];
        
        [vsLabel setText:[game vsString]];
        [vsLabel setTextColor:[game vsColor]];
        
    }
    return self;
}

-(void)loadStatLabelWithString:(NSString*)str{
    
    statLB = [[UILabel alloc] initWithFrame:CGRectMake(235, 36, 80, 20)];
    [statLB setTextColor:[UIColor fRedColor]];
    [statLB setTextAlignment:NSTextAlignmentRight];
    [statLB setFont:[UIFont systemFontOfSize:8]];
    [statLB setText:str];
    [self addSubview:statLB];
}


-(void)plus{
    [sign setText:@"+"];
}

-(void)minus{
    [sign setText:@"-"];
}

@end
