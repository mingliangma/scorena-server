//
//  FHistoryVC.m
//  facebet
//
//  Created by Kun on 2013-12-20.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FHistoryVC.h"
#import "FRankCell.h"
#import "FDummyHead.h"

@interface FHistoryVC ()

@end

static NSString* rankCellIdentifier = @"rankCell";

@implementation FHistoryVC

@synthesize weekButton,allButton,table,reloadView;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        
                    self.navigationItem.backBarButtonItem.tintColor = [UIColor whiteColor];
        // Custom initialization
        if([self respondsToSelector:@selector(setEdgesForExtendedLayout:)]){
            self.edgesForExtendedLayout = UIRectEdgeNone;
        }
        self.title = @"My Ranking";
        isWeek = YES;
        [self loadTable];
        [self refreshRanking];
    }
    return self;
}

#pragma mark - UI Loading

-(void)loadTable{
    table = [[UITableView alloc] initWithFrame:CGRectMake(5, 0, 310, 340+[MUtil extraHeight]) style:UITableViewStylePlain];
    [table setDelegate:self];
    [table setDataSource:self];
    [table setBackgroundColor:[UIColor clearColor]];

    [self.view addSubview:table];
    [self.view sendSubviewToBack:table];
}

#pragma mark - Configuration

-(IBAction)weekClicked:(id)sender{
    [allButton setBackgroundColor:[UIColor whiteColor]];
    [allButton setTitleColor:[UIColor fBlueColor] forState:UIControlStateNormal];
    
    [weekButton setBackgroundColor:[UIColor fBlueColor]];
    [weekButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    
    isWeek=YES;
    [table reloadData];
}

-(IBAction)allClicked:(id)sender{
    
    [weekButton setBackgroundColor:[UIColor whiteColor]];
    [weekButton setTitleColor:[UIColor fBlueColor] forState:UIControlStateNormal];
    
    [allButton setBackgroundColor:[UIColor fBlueColor]];
    [allButton setTitleColor:[UIColor whiteColor] forState:UIControlStateNormal];
    
    isWeek=NO;
    [table reloadData];
}

#pragma mark - External Hooks

-(void)refreshRanking{    
    [[MNetwork sharedInstance] loadRanking:self success:@selector(rankLoaded:) failure:@selector(rankLoadFailed:)];
}

#pragma mark - Network Callbacks

-(void)rankLoaded:(id)obj{
    scrollLock = NO;
    [reloadView stopSpin];
    
    [table moveToY:5 duration:0.3];
    rankObj = NULL;
    rankObj = obj;
    [table reloadData];
}

-(void)rankLoadFailed:(id)obj{
    
}

#pragma mark - UIScrollView

- (void)scrollViewDidEndDragging:(UIScrollView *)scrollView willDecelerate:(BOOL)decelerate{
    if(!scrollLock &&  table.contentOffset.y<-30){
        scrollLock= YES;
        [reloadView triggerUpdate];
        [table moveToY:80 duration:0];
        [self refreshRanking];
    }
}

#pragma mark - UITableViewDelegate & DataSource

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section{
    
    UIView* head = [[UIView alloc] initWithFrame:CGRectMake(0, 30, 310, 88)];
    [head setBackgroundColor:[UIColor colorWithRed:0.0 green:174/255.0 blue:174/255.0 alpha:1.0]];
    
    FDummyHead* dummy = [[FDummyHead alloc] initWithPoint:CGPointMake(0, 44)];
    [dummy setBackgroundColor:[UIColor whiteColor]];
    [head addSubview:dummy];
    
 //   reloadView = [[FReloadView alloc] initWithFrame:CGRectMake(0, 0, 310, 40)];
  //  [head addSubview:reloadView];
    
    return head;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    return isWeek?[[rankObj objectForKey:@"weekly"] count]:[[rankObj objectForKey:@"all"] count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    FRankCell* cell = [tableView dequeueReusableCellWithIdentifier:rankCellIdentifier];
    if(!cell){
        cell = [[FRankCell alloc] init];
    }
    
    id tmp;
    
    if(isWeek){
        tmp = [[rankObj objectForKey:@"weekly"] objectAtIndex:indexPath.row];
    }else{
        tmp = [[rankObj objectForKey:@"all"] objectAtIndex:indexPath.row];
    }

    [cell.nameLB setText:[tmp objectForKey:@"username"]];
    [cell.scoreLB setText:[NSString numStringForKey:@"gain" obj:tmp]];
    [cell.rankLB setText:[NSString numStringForKey:@"rank" obj:tmp]];
    
    if([[tmp objectForKey:@"username"] isEqualToString:[[FGlobal sharedInstance] userName]]){
        [cell.nameLB setTextColor:[UIColor fRedColor]];
        [cell.scoreLB setTextColor:[UIColor fRedColor]];
        [cell.rankLB setTextColor:[UIColor fRedColor]];
    }
    
    return cell;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    
    return 44;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section{
    
    return section==0?88:0;
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
