//
//  FBetButtons.m
//  facebet
//
//  Created by Kun on 2013-12-30.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FBetButtons.h"

#define kBBY 35
#define kBCY 72

@implementation FBetButtons

@synthesize greenStripe,slashGreen,homeButton,homeTeam,awayButton,awayTeam,homeWhite,awayWhite;

@synthesize homeMoney,awayCrowd,homeCrowd,awayMoney,homePeople,awayPeople;

@synthesize delegate,chooseSel,dimLayer,correctLB,meterHome,meterAway,homeOdds,awayOdds;

@synthesize homeSmoke,awaySmoke,overlay,myGame;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        [self loadUI];
    }
    return self;
}


-(void)addWinText{
    correctLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 120, 25)];
    [correctLB setText:@"THE WINNER IS"];
    [correctLB setTextColor:[UIColor whiteColor]];
    [correctLB setFont:[UIFont fStraightFont:10]];
}

-(void)setupHomeWin{
    [greenStripe setBackgroundColor:[UIColor fBlueColor]];
    [slashGreen setImage:[UIImage imageNamed:@"Orange_Bar.png"]];
    [self addWinText];
    [correctLB shift:30 y:11];
}

-(void)setupAwayWin{
    [greenStripe setBackgroundColor:[UIColor fOrangeColor]];
    [slashGreen setImage:[UIImage imageNamed:@"Blue_Bar.png"]];
    [self addWinText];
    [correctLB shift:190 y:11];
}

//Past game
-(void)setupPast{
    
    if([myGame winnerPick]==FWinnerPickHome){
        [self setupHomeWin];
    }else if([myGame winnerPick]==FWinnerPickAway){
        [self setupAwayWin];
    }
    [self addSubview:correctLB];
    
    //User has placed bet, display winner yellow, loser blue and text
    if([myGame placedBet]){
        if([myGame userPick]==FUserPickHome){
            [homeButton setBackgroundColor:[UIColor fRedColor]];
            [homeSmoke setHidden:YES];
        }else{
            [awayButton setBackgroundColor:[UIColor fRedColor]];
            [awaySmoke setHidden:YES];
        }
        [overlay addTarget:delegate action:NSSelectorFromString(@"goPastGame") forControlEvents:UIControlEventTouchUpInside];
    //User
    }else{
        [homeButton setEnabled:NO];
        [awayButton setEnabled:NO];
        
    }
  }


-(void)overlayClicked{
    [[FGlobal sharedInstance] setCurrent:myGame];
    if([myGame userPick]==FUserPickHome){
        [delegate homeTeamSelected];
    }else if([myGame userPick] == FUserPickAway){
        [delegate awayTeamSelected];
    }else{
     //   [delegate performSelector:NSSelectorFromString(@"noTeamSelected") withObject:nil];
    }
}

-(void)setupFuture{
    
    if([myGame placedBet]){
        [homeButton setEnabled:NO];
        [awayButton setEnabled:NO];
        
        if([myGame userPick]==FUserPickHome){
            [homeButton setBackgroundColor:[UIColor fRedColor]];
            [homeSmoke setHidden:YES];
        }else if([myGame userPick] == FUserPickAway){
            [awayButton setBackgroundColor:[UIColor fRedColor]];
            [awaySmoke setHidden:YES];
        }
        
    }else{
        if([myGame getGameStat] == FGameStatusPreEvent){
            [awaySmoke setHidden:YES];
            [homeSmoke setHidden:YES];
            [overlay setHidden:YES];
        }else{
                [homeButton setBackgroundColor:[UIColor fBlueColor]];
                [awayButton setBackgroundColor:[UIColor fBlueColor]];
        }
    }

}


-(void)setupGame:(FGame*)game past:(BOOL)isPast{
    
    myGame = game;
    
    [homeButton setTitle:[game pickA] forState:UIControlStateNormal];
    [awayButton setTitle:[game pickB] forState:UIControlStateNormal];
    homeButton.titleLabel.lineBreakMode = NSLineBreakByWordWrapping;
    homeButton.titleLabel.numberOfLines = 0;
    awayButton.titleLabel.lineBreakMode = NSLineBreakByWordWrapping;
    awayButton.titleLabel.numberOfLines = 0;
    [self setupCrowd:[game pool]];
    if(isPast){
        [self setupPast];
    }else{
        [self setupFuture];
    }
    
}

-(void)setupCrowd:(id)crowd{
    [homeCrowd setText:[NSString stringWithFormat:@"%@",[crowd objectForKey:@"pick1NumPeople"]]];
    [awayCrowd setText:[NSString stringWithFormat:@"%@",[crowd objectForKey:@"pick2NumPeople"]]];
    
    [homeMoney setText:[NSString stringWithFormat:@"$ %@ ",[crowd objectForKey:@"pick1Amount"]]];
    [awayMoney setText:[NSString stringWithFormat:@"$ %@ ",[crowd objectForKey:@"pick2Amount"]]];
    
    [homeOdds setText:[NSString stringWithFormat:@"%@ ",[crowd objectForKey:@"pick1odds"]]];
    [awayOdds setText:[NSString stringWithFormat:@"%@ ",[crowd objectForKey:@"pick2odds"]]];
}

-(void)loadUI{
    
    greenStripe = [[UIView alloc] initWithFrame:CGRectMake(2.5, 0, 300, 95)];
    [greenStripe setBackgroundColor:[UIColor fMiddleGreen]];
    [[greenStripe layer] fShadowSetup];
    slashGreen = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Slash.png"]];
    [slashGreen setFrame:CGRectMake(0, 0, 162.5, 82.5)];
    
    [self addSubview:greenStripe];
    [self addSubview:slashGreen];
    
    homeButton = [[UIButton alloc] initWithFrame:CGRectMake(50, kBBY, 90, 38)];
    [homeButton setBackgroundColor:[UIColor fGreenColor]];
    [[homeButton titleLabel] setFont:[UIFont fItalicFont:14]];
    [[homeButton titleLabel] setLineBreakMode:NSLineBreakByTruncatingTail];
    [[homeButton layer] fShadowSetup];
    [homeButton setTag:17];
    
    awayButton = [[UIButton alloc] initWithFrame:CGRectMake(205, kBBY, 90, 38)];
    [awayButton setBackgroundColor:[UIColor fGreenColor]];
    [[awayButton titleLabel] setFont:[UIFont fItalicFont:14]];
    [[awayButton titleLabel] setLineBreakMode:NSLineBreakByTruncatingTail];
    [[awayButton layer] fShadowSetup];
    [awayButton setTag:19];
    
    [homeButton addTarget:self action:@selector(teamSelect:) forControlEvents:UIControlEventTouchUpInside];
    [awayButton addTarget:self action:@selector(teamSelect:) forControlEvents:UIControlEventTouchUpInside];
    
    [self addSubview:homeButton];
    [self addSubview:awayButton];
    
    homeWhite = [[UIView alloc] initWithFrame:CGRectMake(12, kBBY, 38, 38)];
    [homeWhite setBackgroundColor:[UIColor whiteColor]];
  //  [[homeWhite layer] fVerticalShadow];
    
    awayWhite = [[UIView alloc] initWithFrame:CGRectMake(167, kBBY, 38, 38)];
    [awayWhite setBackgroundColor:[UIColor whiteColor]];
 //   [[awayWhite layer] fVerticalShadow];
    
    //Adding generic team logo
    homeTeam = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Shield_blue.png"]];
    [homeTeam setFrame:CGRectMake(14+2, kBBY+2, 30, 32)];
  //  [[homeTeam layer] fVerticalShadow];
     awayTeam = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Shield_green.png"]];
    [awayTeam setFrame:CGRectMake(169+2, kBBY+2, 30, 32)];
   // [[awayTeam layer] fVerticalShadow];
    [self addSubview:homeWhite];
    [self addSubview:homeTeam];
    [self addSubview:awayWhite];
    [self addSubview:awayTeam];
    
    homeMoney = [[UILabel alloc] initWithFrame:CGRectMake(17, kBCY, 60, 25)];
    [self configLB:homeMoney orient:NSTextAlignmentLeft];
    
    homeCrowd = [[UILabel alloc] initWithFrame:CGRectMake(70, kBCY, 60, 25)];
    [self configLB:homeCrowd orient:NSTextAlignmentLeft];
    
    awayMoney = [[UILabel alloc] initWithFrame:CGRectMake(172, kBCY, 60, 25)];
    [self configLB:awayMoney orient:NSTextAlignmentLeft];
    
    awayCrowd = [[UILabel alloc] initWithFrame:CGRectMake(230, kBCY, 60, 25)];
    [self configLB:awayCrowd orient:NSTextAlignmentLeft];
    
    [self addSubview:homeCrowd];
    [self addSubview:awayCrowd];
    
    [self addSubview:awayMoney];
    [self addSubview:homeMoney];
    
    
    homePeople = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Icon_People_1.png"]];
    [homePeople setCenter:CGPointMake(65, kBCY+13)];
    
    awayPeople = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Icon_People_1.png"]];
    [awayPeople setCenter:CGPointMake(225, kBCY+13)];
    
    [self addSubview:homePeople];
    [self addSubview:awayPeople];
    
    
    [greenStripe shift:2.5 y:0];
    [slashGreen shift:2.5 y:13];
    
    meterHome = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"meter_white.png"]];
    [meterHome setCenter:CGPointMake(100, kBCY+13)];
    
    meterAway =[[UIImageView alloc] initWithImage:[UIImage imageNamed:@"meter_white.png"]];
    [meterAway setCenter:CGPointMake(260, kBCY+13)];
    
    [self addSubview:meterHome];
    [self addSubview:meterAway];
    
    homeOdds = [[UILabel alloc] initWithFrame:CGRectMake(110, kBCY, 40, 25)];
    [self configLB:homeOdds orient:NSTextAlignmentLeft];
    
    awayOdds = [[UILabel alloc] initWithFrame:CGRectMake(270, kBCY, 40, 25)];
    [self configLB:awayOdds orient:NSTextAlignmentLeft];
    
    [self addSubview:homeOdds];
    [self addSubview:awayOdds];
    
    homeSmoke = [[UIImageView alloc] initWithFrame:CGRectMake(2.5, 13, 162.5, 82.5)];
    [homeSmoke setImage:[UIImage imageNamed:@"White_Bar.png"]];
    [homeSmoke setAlpha:0.4];
    
    awaySmoke = [[UIImageView alloc] initWithFrame:CGRectMake(133.5, 13, 170.5, 82.5)];
    [awaySmoke setImage:[UIImage imageNamed:@"White_Bar_Flip.png"]];
    [awaySmoke setAlpha:0.4];
    
    [self addSubview:homeSmoke];
    [self addSubview:awaySmoke];
    
    overlay = [[UIButton alloc] initWithFrame:CGRectMake(2.5, 15, 303, 80)];
    [overlay setBackgroundColor:[UIColor clearColor]];
    [overlay addTarget:self action:@selector(overlayClicked) forControlEvents:UIControlEventTouchUpInside];
    [self addSubview:overlay];
}

-(void)configLB:(UILabel*)lb orient:(NSTextAlignment)align{
    lb.textAlignment = align;
    [lb setBackgroundColor:[UIColor clearColor]];
    [lb setTextColor:[UIColor whiteColor]];
    [lb setFont:[UIFont fStraightFont:11]];
}

-(void)preSetupAway{
    [awayButton setBackgroundColor:[UIColor fRedColor]];
    [homeButton setBackgroundColor:[UIColor fBlueColor]];
}

-(void)preSetupHome{
    [homeButton setBackgroundColor:[UIColor fRedColor]];
    [awayButton setBackgroundColor:[UIColor fBlueColor]];
}

-(void)teamSelect:(id)sender{
    UIButton* btn = (UIButton*)sender;
    NSInteger tag = [btn tag];
    
    if(tag == 17){
        [homeButton setBackgroundColor:[UIColor fRedColor]];
        [awayButton setBackgroundColor:[UIColor fGreenColor]];
        [[FGlobal sharedInstance] setCurrent:myGame];
        [delegate homeTeamSelected];
        
    }else if(tag == 19){
        [awayButton setBackgroundColor:[UIColor fRedColor]];
        [homeButton setBackgroundColor:[UIColor fGreenColor]];
        [[FGlobal sharedInstance] setCurrent:myGame];
        [delegate awayTeamSelected];
    }
}


@end
