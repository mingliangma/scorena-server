//
//  FGameController.m
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FGameController.h"
#import "FSectionHead.h"
#import "FBettingVC.h"
#import "FResultVC.h"

@interface FGameController ()

@end

@implementation FGameController

@synthesize upcoming,past;

@synthesize futureTable,pastTable;
@synthesize futureGames,pastGames;

@synthesize futureDetails,pastDetails,pastReloadView,futureReloadView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {        
        
        [MUtil loadRightBar:self];
        
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
        
        self.title = @"Games";
        
        pastSection = 0;
        pastRow = -1;
        
        futureSection = 0;
        futureRow = -1;
        
        isPast = NO;        
        scrollLock = NO;
        
    }
    return self;
}

- (void)viewDidLoad{
    [super viewDidLoad];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshFutureGames:) name:kRefreshUpcomingNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(refreshAfterBet:) name:kRefreshAfterBetNotification object:nil];
    
    [self loadSegments];
    [self loadUpcoming];
    
}

- (void)didReceiveMemoryWarning{
    [super didReceiveMemoryWarning];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void)refreshAfterBet:(NSNotification*)obj{
    FGame* tmpGame = [obj.userInfo objectForKey:@"Bet"];
    [self searchAndReplaceWithObject:tmpGame];
}

-(void)refreshFutureGames:(NSNotification*)obj{
    [self searchAndReplaceWithJson:obj.userInfo];
}

-(void)searchAndReplaceWithObject:(FGame*)obj{
    for(int i=0;i<[futureGames count];i++){
        FGame* tmp = (FGame*)[futureGames objectAtIndex:i];
        if([[tmp game_id] isEqualToString:obj.game_id]){
            [futureGames replaceObjectAtIndex:i withObject:obj];
            [[MNetwork sharedInstance] loadQuestions:self forGame:obj.game_id index:futureSection past:NO success:@selector(questionsLoaded:) failure:@selector(questionLoadFailed:)];

        }
    }
}

-(void)searchAndReplaceWithJson:(id)obj{
    for(int i=0;i<[futureGames count];i++){
        FGame* tmp = (FGame*)[futureGames objectAtIndex:i];
        if([[tmp game_id] isEqualToString:[obj objectForKey:@"gameId"]]){
            [tmp updateWithGameDetailJson:obj];
            [futureGames replaceObjectAtIndex:i withObject:tmp];
            break;
            [futureTable reloadData];
        }
    }
}

#pragma mark - API Data Return

-(void)pastGamesLoaded:(id)obj{

    [pastReloadView stopSpin];
    [pastTable moveToY:5 duration:0.3];
    
    if(!pastGames || scrollLock){
        [pastGames removeAllObjects];
        pastGames = [[NSMutableArray alloc] init];
        for(int i=0;i<[obj count];i++){
            FGame* game = [[FGame alloc] initWithJson:[obj objectAtIndex:i]];
            [pastGames addObject:game];
        }
        
        if(pastGames && [pastGames count]>0){
            [pastDetails removeAllObjects];
            pastDetails = [[NSMutableArray alloc] initWithCapacity:[pastGames count]];
            [pastDetails fillWithEmptyArray:[pastGames count]];
        }
        
        [pastTable reloadData];
        
        scrollLock = NO;
    }
    
}

-(void)upcomingLoaded:(id)obj{
//       sfv(obj)
    
    [futureReloadView stopSpin];
    [futureTable moveToY:5 duration:0.3];
    
    if(!futureGames || scrollLock){
        
        [futureGames removeAllObjects];
        futureGames = [[NSMutableArray alloc] init];
        for(int i=0;i<[obj count];i++){
            FGame* game = [[FGame alloc] initWithJson:[obj objectAtIndex:i]];
            [futureGames addObject:game];
        }
        
        if(futureGames && [futureGames count]>0){
            [futureDetails removeAllObjects];
            futureDetails = [[NSMutableArray alloc] initWithCapacity:[futureGames count]];
            [futureDetails fillWithEmptyArray:[futureGames count]];
        }
        [futureTable reloadData];
        scrollLock = NO;
    }
}

-(void)requestFailed:(id)obj{
    
    if(![[FGlobal sharedInstance] authenticated]){
        [MUtil showAlert:@"Please login to view data!" del:self];        
    }else{
        [MUtil showAlert:@"Error loading data, please try again later" del:self];
    }
}


-(void)questionsLoaded:(id)obj{
//    sfv(obj)
    id json = [obj objectForKey:@"json"];
    NSInteger index = [[obj objectForKey:@"index"] integerValue];
    BOOL myPast = [[obj objectForKey:@"past"] boolValue];
    
    if(myPast){
        [pastDetails replaceObjectAtIndex:index withObject:json];
    }else{
        [futureDetails replaceObjectAtIndex:index withObject:json];
    }
    
    if(myPast){
        [pastTable reloadData];
    }else{
        [futureTable reloadData];
    }
    
    if(json && [json count]==0){
        [MUtil showAlert:@"There're no questions for this game" del:self];
    }
}


-(void)updateQuestionPicks:(id)obj{
    
    
    
}

-(void)questionLoadFailed:(id)obj{
    
    DD(@"Question Loading Failed:%@",obj);
}

/*Upcoming and Past Game Tab can be updated in here */
-(void)loadSegments{
    
    UIView* topGray = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, 4)];
    [topGray setBackgroundColor:[UIColor fLightGrayColor]];
    [self.view addSubview:topGray];
    
    upcoming = [[UIButton alloc] initWithFrame:CGRectMake(0, 4, 160, 41)];
    [upcoming setTitle:@"Upcoming" forState:UIControlStateNormal];
    [[upcoming titleLabel] setFont:[UIFont fBoldItalicFont:13]];
    [upcoming addTarget:self action:@selector(loadUpcoming) forControlEvents:UIControlEventTouchUpInside];
    
    past = [[UIButton alloc] initWithFrame:CGRectMake(160, 4, 160, 41)];
    [past setTitle:@"Past" forState:UIControlStateNormal];
    [[past titleLabel] setFont:[UIFont fBoldItalicFont:13]];
    [past addTarget:self action:@selector(loadPast) forControlEvents:UIControlEventTouchUpInside];
    
    [self refreshButtons];
    
    [self.view addSubview:upcoming];
    [self.view addSubview:past];
}

-(void)refreshButtons{
    [upcoming setTitleColor:isPast?[UIColor fBlueColor]:[UIColor whiteColor] forState:UIControlStateNormal];
    [upcoming setBackgroundColor:isPast?[UIColor whiteColor]:[UIColor fBlueColor]];
    
    [past setTitleColor:isPast?[UIColor whiteColor]:[UIColor fBlueColor] forState:UIControlStateNormal];
    [past setBackgroundColor:isPast?[UIColor fBlueColor]:[UIColor whiteColor]];
    
    
    pastSection = -1;
    pastRow = -1;
    
    futureSection = -1;
    futureRow = -1;
}

-(void)loadPast{
    [futureTable setHidden:YES];
    [pastTable setHidden:NO];
    isPast = YES;
    [self refreshButtons];
    [[MNetwork sharedInstance] loadPastGames:self success:@selector(pastGamesLoaded:) failure:@selector(requestFailed:)];
}

-(void)loadUpcoming{
    [pastTable setHidden:YES];
    [futureTable setHidden:NO];
    isPast = NO;
    [self refreshButtons];
    [[MNetwork sharedInstance] loadUpcoming:self success:@selector(upcomingLoaded:) failure:@selector(requestFailed:)];
}



- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    
    if(!scrollLock &&  scrollView.contentOffset.y<-40){
    
        scrollLock= YES;
    
        if(scrollLock){
            if(isPast){
                [pastReloadView triggerUpdate];
                [pastTable moveToY:45 duration:0];
                [[MNetwork sharedInstance] loadPastGames:self success:@selector(pastGamesLoaded:) failure:@selector(requestFailed:)];
            }else{
                [futureReloadView triggerUpdate];
                [futureTable moveToY:45 duration:0];
                [[MNetwork sharedInstance] loadUpcoming:self success:@selector(upcomingLoaded:) failure:@selector(requestFailed:)];
            }
        }
    }
    
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    id obj;
    FGame* game;
    
    if(tableView.tag==200){
        obj = [futureGames objectAtIndex:section];
        game = (FGame*)[futureGames objectAtIndex:section];
    }else{
        obj = [pastGames objectAtIndex:section];
        game = (FGame*)[pastGames objectAtIndex:section];
    }
    
    UIView* topBB = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 320, section==0?88:48)];
    FSectionHead* head = [[FSectionHead alloc] initWithFrame:CGRectMake(0, section==0?40:0, 320, 48) game:game];
    [topBB addSubview:head];
    
    if(section==0){
        
        if(tableView.tag == 200) {
            if(!futureReloadView){
                futureReloadView = [[FReloadView alloc] initWithFrame:CGRectMake(0, -80, 320, 120)];
               // futureReloadView = [[FReloadView alloc] initWithFrame:CGRectMake(0, 0, 320, 44)];
            }
            [topBB addSubview:futureReloadView];
        }else{
            if(!pastReloadView){
                pastReloadView = [[FReloadView alloc] initWithFrame:CGRectMake(0, -80, 320, 120)];
            }
            [topBB addSubview:pastReloadView];
        }
    }
    
    if(tableView.tag == 200){
        if(section == futureSection){
            [head minus];
        }else{
            [head plus];
        }
    }else{
        if(section == pastSection){
            [head minus];
        }else{
            [head plus];
        }
    }
    
    
    UIButton* btn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 48)];
   // UIButton* btn = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 52)];
    [btn setTag:700+section];
    [btn addTarget:self action:@selector(toggleSection:) forControlEvents:UIControlEventTouchUpInside];
    [head addSubview:btn];
    
    return topBB;
}
- (void)tableView:(UITableView *)tableView
  willDisplayCell:(UITableViewCell *)cell
forRowAtIndexPath:(NSIndexPath *)indexPath{
    [cell setBackgroundColor:[UIColor fGreenColor]];
}

-(void)toggleSection:(UIButton*)sender{
    NSInteger section = ([sender tag] - 700);
    
    
    
    if(isPast){
        if(pastSection!=section){
            pastSection = section;
            pastRow = -1;
            
            NSArray* tmpArray = [pastDetails objectAtIndex:section];
            if(!tmpArray || [tmpArray count]==0){
                
                FGame* obj = [pastGames objectAtIndex:section];
                
                NSString* game_id = obj.game_id;                
                
                [[MNetwork sharedInstance] loadQuestions:self forGame:game_id index:section past:YES success:@selector(questionsLoaded:) failure:@selector(questionLoadFailed:)];
            }else{
                [pastTable reloadData];
            }
            
        }else{
            pastRow=-1;
            pastSection = -1;
            [pastTable reloadData];
        }
    }else{
        
        if(futureSection!=section){
            futureSection = section;
            futureRow = -1;
            
            
            NSArray* tmpArray = [futureDetails objectAtIndex:section];
            if(!tmpArray || [tmpArray count]==0){
                
                FGame* obj = [futureGames objectAtIndex:section];
                
                NSString* game_id = obj.game_id;
                
                [[MNetwork sharedInstance] loadQuestions:self forGame:game_id index:section past:NO success:@selector(questionsLoaded:) failure:@selector(questionLoadFailed:)];
                
            }else{
                [futureTable reloadData];
            }
        }else{
            
            futureRow=-1;
            futureSection = -1;
            [futureTable reloadData];
        }
    }
    
}

-(void)toggleRow:(NSIndexPath*)path{
    NSInteger sec = path.section;
    
    if(isPast){
        if(pastSection==sec){
            if(pastRow == path.row){
                pastRow = -1;
            }else{
                pastRow = path.row;
            }
            [pastTable reloadData];
        }else{
            DD(@"ERROR: Past Section Mismatch");
        }
    }else{
        if(futureSection==sec){
            if(futureRow == path.row){
                futureRow = -1;
            }else{
                futureRow = path.row;
            }
            [futureTable reloadData];
        }else{
            DD(@"ERROR: Future Section Mismatch");
        }
    }
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    if(tableView.tag == 200){
        return [futureGames count];
    }else if(tableView.tag==300){
        return [pastGames count];
    }
    return 0;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    if(tableView.tag == 200){
        return (indexPath.row==futureRow)?140:65;
    }else{
        return (indexPath.row==pastRow)?140:65;
    }
}

-(void)goGame:(kBetSelectType)betSel{
    
    FBettingVC* vc = [[FBettingVC alloc] initWithNibName:@"FBettingVC" bundle:[NSBundle mainBundle]];
    [vc setMyBet:[[FGlobal sharedInstance] current]];
    [vc setupContent];
    if(betSel == kBetSelectHome){
        [vc homeTeamSelected];
    }else if(betSel == kBetSelectAway){
        [vc awayTeamSelected];
    }
    [[self navigationController] pushViewController:vc animated:YES];
}

-(void)goPastGame{
    FResultVC* vc = [[FResultVC alloc] initWithNibName:@"FResultVC" bundle:[NSBundle mainBundle]];
    [vc setMyBet:[[FGlobal sharedInstance] current]];
    [vc setupContent];
    [[self navigationController] pushViewController:vc animated:YES];
    
}

-(void)homeTeamSelected{
    [self goGame:kBetSelectHome];
}

-(void)awayTeamSelected{
    [self goGame:kBetSelectAway];
}

-(void)noTeamSelected{
    [self goGame:kBetSelectNone];
}


- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    return section==0?94:54;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if(tableView.tag==200){
        if(futureSection!=section){
            return 0;
        }
        
        if(futureDetails&& [futureDetails count]>0){
            NSMutableArray* future = (NSMutableArray*)[futureDetails objectAtIndex:section];
            return (future&&[future count]>0)?[future count]:0;
        }else{
            return 0;
        }
    }else{
        if(pastSection!=section){
            return 0;
        }
        
        if(pastDetails&&[pastDetails count]>0){
            NSMutableArray* pastArray = (NSMutableArray*)[pastDetails objectAtIndex:section];
            return (pastArray&&[pastArray count]>0)?[pastArray count]:0;
        }else{
            return 0;
        }
    }
    
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if (cell == nil) {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    [cell setAccessoryView:nil];
    
    if(isPast){
        
        FTPastCell* acc = [[FTPastCell alloc] init];
        [acc setPath:indexPath];
        [acc setParent:self];
        [acc setAdjustSEL:@selector(toggleRow:)];
        [acc setIsOpen:(indexPath.section==pastSection && indexPath.row == pastRow)];
        [acc initPastCell];
        
        FGame* game = [self gameFromIndexPath:indexPath isPast:YES];
        [acc setupGame:game handler:acc];
        
        [cell setAccessoryView:acc];
        
    }else{
        
        FTFutureCell* acc = [[FTFutureCell alloc] init];
        [acc setPath:indexPath];
        [acc setParent:self];
        [acc setAdjustSEL:@selector(toggleRow:)];
        [acc setIsOpen:(indexPath.section == futureSection && indexPath.row == futureRow)];
        [acc initFutureCell];
        
        FGame* game = [self gameFromIndexPath:indexPath isPast:NO];
        [acc setupGame:game handler:self];
        [cell setAccessoryView:acc];
    }
    [cell setSelectionStyle:UITableViewCellSelectionStyleNone];
    return cell;
}

/**
 Convert json to game object here
 */

-(FGame*)gameFromIndexPath:(NSIndexPath*)indexPath isPast:(BOOL)loadPast{
    
    id typeObj = loadPast?[pastDetails objectAtIndex:indexPath.section]:[futureDetails objectAtIndex:indexPath.section];
    
    NSDictionary* typeDict = (NSDictionary*)[typeObj objectAtIndex:indexPath.row];
    NSString* type = [typeDict objectForKey:@"content"];
    
    FGame* game = loadPast?[pastGames objectAtIndex:indexPath.section]:[futureGames objectAtIndex:indexPath.section];
    
    FGame* result = [game copy];
    
    [result setType:type];
    [result populateAdditionalData:typeDict];
    
    return result;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    
}


@end
