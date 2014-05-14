//
//  FBettingVC.m
//  facebet
//
//  Created by Kun on 2013-12-19.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FBettingVC.h"

#define kRowHeight 30

#define kReloadViewHeight 50

@interface FBettingVC ()

@end



@implementation FBettingVC

@synthesize myBet,betScroll;

@synthesize betButtons,teamButtons;

@synthesize payoutTable,payoutBg,floatBet;

@synthesize gameTypeHead,betPanel;
@synthesize dateSlash;
@synthesize detailHead,reloadView;



/*
 2014-04-26 12:05:41.940 facebet[20284:60b] {"sessionToken":"NTxdI74sbNjSeKtOdGPixJOn0","questionId":"34","pick":"1","wager":"15"}
 2014-04-26 12:05:43.999 facebet[20284:60b] Bet Success
 */

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        
            self.navigationItem.backBarButtonItem.tintColor = [UIColor whiteColor];
        [MUtil loadRightBar:self];
        
        // Custom initialization
        mySelect = kBetSelectNone;
        isHomePayout = YES;
        homePayout = nil;
        
        betScroll = [[UIScrollView alloc] initWithFrame:[self scrollRectWithFloatingBet]];
        [betScroll setContentSize:CGSizeMake(310, [self scrollHeight])];
        [betScroll setScrollEnabled:YES];
        [betScroll setShowsVerticalScrollIndicator:NO];
        betScroll.delegate = self;
        [self.view addSubview:betScroll];
        
        
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
        
        [self loadReloadView];
        [self loadDetailHead];
        [self loadBetButtons];
        [self loadGameType];
        [self loadPayoutBackground];
        [self loadBetScroll];
        [self loadBetTables];
        [self loadPayoutTable];
        scrollLock = NO;
        
    }
    return self;
}

#pragma mark - Network callbacks

-(void)detailsLoaded:(id)obj{
//    sfv(obj)

    id stuff = [obj objectForKey:@"betters"];
    homePayout = [stuff objectForKey:@"pick1Betters"];
    awayPayout = [stuff objectForKey:@"pick2Betters"];
    isHomePayout = NO;
    
    //Populate bet panel
    [betPanel populateWithJson:obj past:[myBet placedBet] team:mySelect bet:myBet];
    
    //Update lastUpdate timestamp
    NSString* timeStamp = [obj objectForKey:@"lastUpdate"];
    if(timeStamp && [timeStamp isKindOfClass:[NSString class]]){
        [betPanel configureUpdateTime:[[[obj objectForKey:@"lastUpdate"] dateObjectFromNetwork] updateFormat]];
    }else{
        [betPanel configureUpdateTime:[[NSDate date] updateFormat]];
    }
    [self homePayoutSelected];
    
    //After data population myBet values will change due to empty fields
    [myBet populatePoolData:obj];
}


-(void)gameRefreshed:(id)obj{
//    sfv(obj)
    scrollLock = NO;
    [reloadView stopSpin];
    [betScroll moveToY:10 duration:0.3];

    [detailHead updateGameScoreWithJsonObj:obj];
    [[NSNotificationCenter defaultCenter] postNotificationName:kRefreshUpcomingNotification object:nil userInfo:obj];
}


-(void)loadingFailed:(id)obj{
        scrollLock = NO;
}

-(void)updateBet{
    
    [[FGlobal sharedInstance] reduceBalance:[betPanel wager]];
    
    
    [myBet setUserPick:mySelect==kBetSelectHome?FUserPickHome:FUserPickAway];
    [myBet setPlacedBet:YES];
    [betScroll setFrame:CGRectMake(5, 10, 310, 290+[MUtil extraHeight]+kReloadViewHeight)];
    [self setupContent];
    
    NSDictionary* tt = [NSDictionary dictionaryWithObject:myBet forKey:@"Bet"];
    
    [[NSNotificationCenter defaultCenter] postNotificationName:kRefreshAfterBetNotification object:nil userInfo:tt];
}


-(void)betSucccess:(id)obj{
    
    [self updateBet];
    
    [floatBet setHidden:YES];
    
    FDoneBetVC* fd = [[FDoneBetVC alloc] initWithNibName:@"FDoneBetVC" bundle:[NSBundle mainBundle]];
    
    NSString* tt = mySelect==kBetSelectHome?[myBet pickA]:[myBet pickB];
    
    [fd configBet:[betPanel wager] teamA:[myBet pickA] teamB:[myBet pickB] type:[myBet type] pic:@"shield.jpg" pickTeam:tt];
    [fd setDelegate:self];

    [self.navigationController presentViewController:fd animated:YES completion:nil];
}

-(void)dismissDoneVC{
    [self dismissViewControllerAnimated:NO completion:nil];
    [self.navigationController popViewControllerAnimated:NO];
}

-(void)betFailed:(id)obj{
    NSLog(@"Bet Failed: %@",obj);
    UIAlertView* alert = [[UIAlertView alloc] initWithTitle:@"Error" message:obj delegate:self cancelButtonTitle:@"OK" otherButtonTitles:nil];
    [alert show];
}


#pragma mark - Native UI Delegates


- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    if(!scrollLock &&  scrollView.contentOffset.y<-50){
        scrollLock= YES;
        [reloadView triggerUpdate];
        [betScroll moveToY:50 duration:0];
        [[MNetwork sharedInstance] loadGameDetail:self game:myBet.game_id success:@selector(gameRefreshed:) failure:@selector(loadingFailed:)];
        [[MNetwork sharedInstance] loadPayout:self game:myBet.game_id q_id:myBet.bet_id success:@selector(detailsLoaded:) failure:@selector(loadingFailed:)];
    }
}


#pragma mark - Team Selection API Calls

-(void)noTeamSelected{
    
}

-(void)homeTeamSelected{
    mySelect = kBetSelectHome;
    [betButtons preSetupHome];
    [betPanel homeSelected];
    [teamButtons wagerSelect:teamButtons.homeCrowd];
}

-(void)awayTeamSelected{
    mySelect = kBetSelectAway;
    [betButtons preSetupAway];
    [betPanel awaySelected];
    [teamButtons wagerSelect:teamButtons.awayCrowd];
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

#pragma mark - UI Configuration methods

-(void)setupContent{
    
    [teamButtons setupContent:myBet];
    [betButtons setDelegate:self];
    [betButtons setupGame:myBet past:NO];
    [gameTypeHead setupContent:myBet];
    [detailHead configureWithGame:myBet];
    
    //Configure floating button
    if([myBet getGameStat] == FGameStatusPreEvent){
        [self loadFloatingButton];
    }else{
        [betScroll setFrame:[self scrollRectWithoutBetButton]];
    }
    
    //Configure top left date UI
    dateSlash = [[FDateSlash alloc] initWithFrame:CGRectMake(0, 1.5, 62, 46) game:myBet];
    [self.view addSubview:dateSlash];
    
    [[MNetwork sharedInstance] loadPayout:self game:myBet.game_id q_id:myBet.bet_id success:@selector(detailsLoaded:) failure:@selector(loadingFailed:)];
}

#pragma mark - UI Loading Methods


-(void)loadBetTables{
    teamButtons = [[FTeamButtons alloc] initWithFrame:CGRectMake(0, 305+kReloadViewHeight, 320, 30)];
    [teamButtons setDelegate:self];
    [self.betScroll addSubview:teamButtons];
}


-(void)loadReloadView{
    reloadView = [[FReloadView alloc] initWithFrame:CGRectMake(0, 0, 320, 40)];
    [self.betScroll addSubview:reloadView];
}

-(void)loadDetailHead{
    detailHead = [[FDetailHead alloc] initWithPoint:CGPointMake(0, 0)];
    [self.view addSubview:detailHead];
}


-(void)loadGameType{
    gameTypeHead = [[FUGameType alloc] initWithFrame:CGRectMake(0, 0+kReloadViewHeight, 300, 80)];
    [self.betScroll addSubview:gameTypeHead];
}

-(void)loadBetButtons{
    betButtons = [[FBetButtons alloc] initWithFrame:CGRectMake(0, 40+kReloadViewHeight, 300, 120)];
    [self.betScroll addSubview:betButtons];
}

-(void)loadBetScroll{
    betPanel = [[FBetPanel alloc] initWithFrame:CGRectMake(0, 130+kReloadViewHeight, 300, 180)];
    [self.betScroll addSubview:betPanel];
}

-(void)loadFloatingButton{
    if(![myBet placedBet]){
        floatBet = [[FFloatBet alloc] initWithFrame:CGRectMake(0, 290+[MUtil extraHeight], 320, 60)];
        [floatBet configWithDelegate:self selector:@selector(doBet)];
        [floatBet startTime:[myBet gameTime]];
        [self.view addSubview:floatBet];
    }else{
        [betScroll setFrame:[self scrollRectWithoutBetButton]];
    }
}


-(void)loadPayoutBackground{
    
    payoutBg = [[UIView alloc] initWithFrame:CGRectMake(3, 135+kReloadViewHeight, 303, 230+(isHomePayout?[homePayout count]:[awayPayout count])*kRowHeight)];
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
    payoutTable = [[FPayoutView alloc] initWithFrame:CGRectMake(10, 332+kReloadViewHeight, 280, 150) array:arr];
    [self.betScroll addSubview:payoutTable];
}

#pragma mark - UI Action Methods

-(void)doBet{

    if(![[FGlobal sharedInstance] authenticated]){
        UIAlertView* al = [[UIAlertView alloc] initWithTitle:@"Error" message:@"Please login first" delegate:self cancelButtonTitle:@"ok" otherButtonTitles:nil];
        [al setDelegate:self];
        [al setTag:99];
        [al show];
        return;
    }
    

    if([[FGlobal sharedInstance] balance]<[betPanel wager]){
        [MUtil showAlert:@"You don't have enough coins" del:self];
        return;
    }    
    
    NSString* pick;
    
    if(mySelect==kBetSelectHome){
        pick = @"1";
    }else if(mySelect==kBetSelectAway){
        pick= @"2";
    }else{
        [MUtil showAlert:@"Which side are you betting on?" del:self];
        return;
    }
    
    NSInteger betNumber = [betPanel wager];
    
    NSString* wager = [NSString stringWithFormat:@"%d",betNumber];
    
    NSDictionary* dict = [NSDictionary dictionaryWithObjectsAndKeys:myBet.bet_id,SU_QuestionKey,pick,SU_PickKey,wager,SU_WagerKey,nil];
    
    [[MNetwork sharedInstance] bet:self dict:dict success:@selector(betSucccess:) failure:@selector(betFailed:)];
}

-(void)refreshBetters{
    [self.payoutBg setFrame:CGRectMake(3, 135+kReloadViewHeight, 303, 230+(isHomePayout?[homePayout count]:[awayPayout count])*kRowHeight)];
    [self.betScroll setContentSize:CGSizeMake(310, [self scrollHeight])];
    [self.betScroll setNeedsDisplay];
}

#pragma mark - UI Dimension helper methods

-(CGRect)scrollRectWithFloatingBet{
    return CGRectMake(5, 10, 310, 237+[MUtil extraHeight]+kReloadViewHeight);
}

-(CGRect)scrollRectWithoutBetButton{
    return CGRectMake(5, 10, 310, 290+[MUtil extraHeight]+kReloadViewHeight);
}

-(CGFloat)scrollHeight{
    return 370+(isHomePayout?[homePayout count]:[awayPayout count])*kRowHeight+kReloadViewHeight;
}


#pragma mark - View Delegate Methods

- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex{
    if(alertView.tag==99){
        FAppDelegate* appDelegate = (FAppDelegate*)[[UIApplication sharedApplication] delegate];
        [appDelegate.tabController setSelectedIndex:2];
    }
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
