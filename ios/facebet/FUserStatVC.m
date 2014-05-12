//
//  FUserStatVC.m
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FUserStatVC.h"

#define kUserReloadHeight 44

@interface FUserStatVC ()

@end

@implementation FUserStatVC

@synthesize nameLB,genderLocationLB,userImage,historyBtn,scroll,userCircles;
@synthesize whiteBg,reloadView,rangeButtons,leagueTable,parentVC;

#pragma mark - Initialization
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
        
        [self.navigationItem setHidesBackButton:YES];
        scrollLock = NO;
        
        statType = kStatTimeRangeWeek;
        // Custom initialization
        [self loadScroll];
        [self loadWhiteBackground];
        [self loadTimeRangeButtons];
        [self loadUserCircles];
        [self loadReloadView];
        [self loadLeagueTable];

        [self setTitle:@"Profile"];
    }
    return self;
}

#pragma mark - UI Loading
-(void)loadScroll{
    scroll = [[UIScrollView alloc] initWithFrame:CGRectMake(0, 40, 320, 300+[MUtil extraHeight])];

    [scroll setScrollEnabled:YES];
    [scroll setShowsVerticalScrollIndicator:NO];
    scroll.delegate = self;
    [self.view addSubview:scroll];
    [self.view sendSubviewToBack:scroll];
}

-(void)loadLeagueTable{
    leagueTable = [[FLeagueTable alloc] initWithPoint:CGPointMake(5, 265)];
    [self.scroll addSubview:leagueTable];
    [scroll setContentSize:CGSizeMake(310, [self scrollHeight])];
}

-(void)loadReloadView{
  //  reloadView = [[FReloadView alloc] initWithFrame:CGRectMake(0, 0, 320, 40)];
  //  [self.scroll addSubview:reloadView];
}

-(void)loadUserCircles{
    userCircles = [[FUserStatCircles alloc] initWithPoint:CGPointMake(5, kUserReloadHeight+57)];
    [self.scroll addSubview:userCircles];
}

-(void)loadWhiteBackground{
    whiteBg = [[UIView alloc] initWithFrame:CGRectMake(5, kUserReloadHeight+5, 310, 55+[MUtil extraHeight])];
    [whiteBg setBackgroundColor:[UIColor whiteColor]];
    [self.scroll addSubview:whiteBg];
}

-(void)loadTimeRangeButtons{
    rangeButtons = [[FRangeButtons alloc] initWithPoint:CGPointMake(5, 15+kUserReloadHeight)];
    [rangeButtons setParentVC:self];
    [self.scroll addSubview:rangeButtons];
    [rangeButtons weekClicked:nil];
}

#pragma mark - External Calls

-(void)loadWeek{
    statType = kStatTimeRangeWeek;
    [self configureWithUserProfileJson:profile];
}

-(void)loadMonth{
    statType = kStatTimeRangeMonth;
    [self configureWithUserProfileJson:profile];
}

-(void)loadAll{
    statType = kStatTimeRangeAll;
    [self configureWithUserProfileJson:profile];    
}


-(void)refreshUserProfile{
    [[MNetwork sharedInstance] loadUserDetails:self user:[[[FGlobal sharedInstance] credential] objectForKey:@"userId"] success:@selector(userDetailsLoaded:) failure:@selector(userDetailLoadFailed:)];
    
   
}

#pragma mark - Refresh UI With Data

-(void)configureWithUserProfileJson:(id)obj{
    
    profile = nil;
    profile = obj;
    
    
    //User name & reagion
    [nameLB setText:[obj objectForKey:@"username"]];
   // [genderLocationLB setText:[NSString stringWithFormat:@"%@/%@",[obj objectForKey:@"gender"],[obj objectForKey:@"region"]]];
    
    //Use dummy data now
    [genderLocationLB setText:[[NSString stringWithFormat:@"L%@- %@",[obj objectForKey:@"level"],[obj objectForKey:@"levelName"]] uppercaseString] ];
    
    NSString* abc;
    
    if(statType==kStatTimeRangeWeek){
        abc = @"weekly";
    }else if(statType == kStatTimeRangeMonth){
        abc = @"monthly";
    }else{
        abc = @"all";
    }
    
    [userCircles configureWithTimeRange:[[obj objectForKey:@"userStats"] objectForKey:abc] balance:[[FGlobal sharedInstance] balance]];
    
    [leagueTable configureWithLeagueJson:[[[obj objectForKey:@"userStats"] objectForKey:abc] objectForKey:@"leagues"]];
    [scroll setContentSize:CGSizeMake(310, [self scrollHeight])];
    
}

#pragma mark - UIScrollViewDelegate


- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{    
    if(!scrollLock &&  scrollView.contentOffset.y<-30){
        scrollLock= YES;
        [reloadView triggerUpdate];
        [scroll moveToY:80 duration:0];
        [self refreshUserProfile];
    }
}

#pragma mark - Network Callback

-(void)userDetailsLoaded:(id)obj{
    
    [reloadView stopSpin];
    [scroll moveToY:40 duration:0];
    
    scrollLock = NO;
    [self configureWithUserProfileJson:obj];
}

-(void)userDetailLoadFailed:(id)obj{
    scrollLock = NO;
    
}

#pragma mark - Action Hooks

-(IBAction)editClicked:(id)sender{
    [parentVC performSelector:NSSelectorFromString(@"switchToProfile:") withObject:nil];
    
}

#pragma mark - UI Helper
-(CGFloat)scrollHeight{
    return 270+self.leagueTable.frame.size.height;
}


#pragma mark - UIViewController

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view from its nib.
    [userImage.layer setBorderColor:[UIColor whiteColor].CGColor];
    [userImage.layer setBorderWidth:2.0];
  
   //  [self refreshBalance];

}



- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    
    // Dispose of any resources that can be recreated.
}

/*
 {"createdAt":"2014-04-05T19:39:46.678Z",
 "username":"Heng4",
 "currentBalance":2000,
 "updatedAt":"2014-04-05T19:39:46.678Z",
 "userId":"k7Q00ai9j4",
 "gender":"female",
 "region":"Montreal",
 "email":heng4@gmail.com
 "userStats" : {
 "weekly" : {
 "losses" : 3,"netGain" : 70, "wins" : 4, "ties" : 1
 "premier" : {"losses" : 0, "netLose" : 0,"netGain" : 0,"wins" : 0,"ties" : 0},
 "brazil" : {"losses" : 0, "netLose" : 0,"netGain" : 0,"wins" : 0,"ties" : 0},
 "champ" : {"losses" : 0, "netLose" : 0,"netGain" : 0,"wins" : 0,"ties" : 0},
 },
 "monthly" : {
 "losses" : 3,"netGain" : 70, "wins" : 4, "ties" : 1
 "premier" : {"losses" : 0, "netLose" : 0,"netGain" : 0,"wins" : 0,"ties" : 0},
 "brazil" : {"losses" : 0, "netLose" : 0,"netGain" : 0,"wins" : 0,"ties" : 0},
 "champ" : {"losses" : 0, "netLose" : 0,"netGain" : 0,"wins" : 0,"ties" : 0},
 },
 "all" : {
 "losses" : 3,"netGain" : 70, "wins" : 4, "ties" : 1
 "premier" : {"losses" : 0, "netLose" : 0,"netGain" : 0,"wins" : 0,"ties" : 0},
 "brazil" : {"losses" : 0, "netLose" : 0,"netGain" : 0,"wins" : 0,"ties" : 0},
 "champ" : {"losses" : 0, "netLose" : 0,"netGain" : 0,"wins" : 0,"ties" : 0},
 },
 }
 */

@end
