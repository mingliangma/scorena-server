//
//  FResultVC.m
//  facebet
//
//  Created by Kyle on 2014-04-26.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FResultVC.h"

#define kRowHeight 30

#define kWagerPickHeight 115

@interface FResultVC ()

@end

@implementation FResultVC

@synthesize myBet,betButtons,teamButtons,pastCircles;
@synthesize payoutTable,payoutBg,wagerScroll;
@synthesize gameTypeHead,dateSlash,betScroll,detailHead,pickPanel;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
                    self.navigationItem.backBarButtonItem.tintColor = [UIColor whiteColor];
        // Custom initialization
        [MUtil loadRightBar:self];
        
        isHomePayout = YES;
        homePayout = nil;
        
        betScroll = [[UIScrollView alloc] initWithFrame:CGRectMake(5, 60, 310, 290+[MUtil extraHeight])];
        [betScroll setContentSize:CGSizeMake(310, [self scrollHeight])];
        [betScroll setScrollEnabled:YES];
        [betScroll setShowsVerticalScrollIndicator:NO];
        [self.view addSubview:betScroll];
    
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
        
        [self loadDetailHead];
        [self loadBetButtons];
        [self loadGameType];
        [self loadPayoutBackground];
        [self loadWagerScroll];
        [self loadBetTables];
        [self loadPayoutTable];
        [self loadPastCircles];
        

    }
    return self;
}

-(void)loadPastCircles{
    pastCircles = [[FPastCircles alloc] initWithPoint:CGPointMake(0, 130+kWagerPickHeight)];
    [self.betScroll addSubview:pastCircles];
}

-(void)homePayoutSelected{
    if(!isHomePayout){
        isHomePayout = YES;
        [self loadPayoutTable];
        [self refreshBetters];
    }
}

-(void)awayPayoutSelected{
    if(isHomePayout){
        isHomePayout = NO;
        [self loadPayoutTable];
        [self refreshBetters];
    }
}

-(void)goPastGame{
    //Dummy method to prevent crash, Do not remove
}


-(void)detailsLoaded:(id)obj{
    
    sfv(obj)
    
    id stuff = [obj objectForKey:@"betters"];
    homePayout = [stuff objectForKey:@"pick1Betters"];
    awayPayout = [stuff objectForKey:@"pick2Betters"];
    isHomePayout = NO;
    [myBet populateAdditionalData:obj];
    
    [self refreshCirclesWithJsonData:obj];
    [self homePayoutSelected];
    [self refreshPickAndWagerWithData:obj];
}

-(void)refreshPickAndWagerWithData:(id)obj{
    if([[[obj objectForKey:@"userInfo"] objectForKey:@"userPick"] integerValue]==1){
        [pickPanel configureWithPick:[obj objectForKey:@"pick1"]];
    }else{
        [pickPanel configureWithPick:[obj objectForKey:@"pick2"]];
    }
    
    NSInteger userWager = [[[obj objectForKey:@"userInfo"] objectForKey:@"userWager"] integerValue];
    
    [wagerScroll selectAlreadyPlacedWager:userWager];
    [pickPanel setupGrayUIWhenBetPlaced];
}

-(void)refreshCirclesWithJsonData:(id)obj{
    NSInteger picked = [[[obj objectForKey:@"userInfo"] objectForKey:@"userPick"] integerValue];
    NSInteger winner = [[obj objectForKey:@"winnerPick"] integerValue];
    
    if(winner==0){
        [pastCircles configureWithType:FUserPickStatusTie money:0 percent:0];
    }else{
        CGFloat pct = [[[obj objectForKey:@"userInfo"] objectForKey:@"userPayoutPercent"] floatValue];
        CGFloat amt = [[[obj objectForKey:@"userInfo"] objectForKey:@"userWinningAmount"] floatValue];
        if(picked==winner){
            [pastCircles configureWithType:FUserPickStatusWin money:amt percent:pct];
        }else{
            [pastCircles configureWithType:FUserPickStatusLose money:amt percent:pct];
        }
    }
}

-(void)loadingFailed:(id)obj{

}

-(void)setupContent{
    
    [teamButtons setupContent:myBet];
    [betButtons setDelegate:self];
    [betButtons setupGame:myBet past:YES];
    [gameTypeHead setupContent:myBet];

    [detailHead configureWithGame:myBet];
    
    dateSlash = [[FDateSlash alloc] initWithFrame:CGRectMake(0, 1.5, 62, 46) game:myBet];
    [self.view addSubview:dateSlash];
    
    [[MNetwork sharedInstance] loadPayout:self game:myBet.game_id q_id:myBet.bet_id success:@selector(detailsLoaded:) failure:@selector(loadingFailed:)];
}

-(void)homeTeamSelected{
//    dummy to avoid crash
}

-(void)awayTeamSelected{
//    dummy to avoid crash
}

-(void)loadGameType{
    gameTypeHead = [[FUGameType alloc] initWithFrame:CGRectMake(0, 0, 300, 80)];
    [self.betScroll addSubview:gameTypeHead];
}

-(void)loadBetButtons{
    betButtons = [[FBetButtons alloc] initWithFrame:CGRectMake(0, 40, 300, 120)];
    [self.betScroll addSubview:betButtons];
}

-(NSString*)oddString{
    
    CGFloat odd1 = [[[myBet pool] objectForKey:@"pick1PayoutPercent"] floatValue];
    CGFloat odd2 = [[[myBet pool] objectForKey:@"pick2PayoutPercent"] floatValue];
    
    CGFloat result;
    NSString* resultOdds;
    if(odd1>odd2){
        result = odd1/odd2;
        resultOdds = [NSString stringWithFormat:@"%.02f : 1",result];
    }else{
        result = odd2/odd1;
        resultOdds = [NSString stringWithFormat:@"1 : %.02f",result];
    }
    
    return [NSString stringWithFormat:@"%@ / %@ = %@",[myBet teamA].teamName,[myBet teamB].teamName,resultOdds];
}


-(void)loadBetTables{
    teamButtons = [[FTeamButtons alloc] initWithFrame:CGRectMake(0, 305+kWagerPickHeight, 320, 30)];
    [teamButtons setDelegate:self];
    [self.betScroll addSubview:teamButtons];
}

-(CGFloat)scrollHeight{
    return 370+kWagerPickHeight+(isHomePayout?[homePayout count]:[awayPayout count])*kRowHeight;
}

-(void)refreshBetters{
    [self.payoutBg setFrame:[self payoutBGRect]];
    [self.betScroll setContentSize:CGSizeMake(310, [self scrollHeight])];
    [self.betScroll setNeedsDisplay];
}

-(void)loadDetailHead{
    detailHead = [[FDetailHead alloc] initWithPoint:CGPointMake(0, 0)];
    [self.view addSubview:detailHead];
}

-(CGRect)payoutBGRect{
    return CGRectMake(3, 135, 303, 230+kWagerPickHeight+(isHomePayout?[homePayout count]:[awayPayout count])*kRowHeight);
}

-(void)loadPayoutBackground{
    payoutBg = [[UIView alloc] initWithFrame:[self payoutBGRect]];
    [payoutBg setBackgroundColor:[UIColor whiteColor]];
    [[payoutBg layer] fShadowSetup];
    
    [self.betScroll addSubview:payoutBg];
}

-(void)loadPayoutTable{
    NSArray* arr;
    if(isHomePayout){
        arr = homePayout;
    }else{
        arr = awayPayout;
    }
    
    if(payoutTable && [payoutTable respondsToSelector:@selector(clean)]){
        [payoutTable clean];
        payoutTable = nil;
    }
    payoutTable = [[FPayoutView alloc] initWithFrame:CGRectMake(10, 332+kWagerPickHeight, 280, 150) array:arr];
    [self.betScroll addSubview:payoutTable];
}


-(void)loadWagerScroll{
    
    pickPanel = [[FPickPanel alloc] initWithPoint:CGPointMake(0, 140)];
    [self.betScroll addSubview:pickPanel];
    
    wagerScroll = [[FWagerScroll alloc] initWithFrame:CGRectMake(0, 190, 260, 48)];
    [self.betScroll addSubview:wagerScroll];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
