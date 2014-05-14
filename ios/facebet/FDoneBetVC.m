//
//  FDoneBetVC.m
//  facebet
//
//  Created by Kyle on 2014-01-03.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//



/**
    The View Controller that's displayed after a user has successfully placed a bet 
 */

#import "FDoneBetVC.h"

@interface FDoneBetVC ()

@end

@implementation FDoneBetVC

@synthesize congratLB,amountLB,teamLB,delegate,title,teamA,teamB,message,bgImage,teamImage,typeView;


- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void)viewDidLoad{
    [super viewDidLoad];
    [amountLB setText:amountText];
//    [typeLB setText:typeText];
    [typeView setText:typeText];
    [teamA setText:teamAText];
    [teamB setText:teamBText];
    [teamLB setText:pickTeam];
    [teamImage setImage:[UIImage imageNamed:teamPic]];
    [typeView setTextColor:[UIColor whiteColor]];
    [typeView setFont:[UIFont boldSystemFontOfSize:18.0]];
    [typeView setTextAlignment:NSTextAlignmentCenter];
}

-(void)configBet:(NSInteger)bet teamA:(NSString*)myteamA teamB:(NSString*)myteamB type:(NSString*)type pic:(NSString*)pic pickTeam:(NSString*)myPickTeam{
    amountText = [NSString stringWithFormat:@"%d",bet];
    teamAText = myteamA;
    teamBText = myteamB;
    typeText = type;
    teamPic = pic;
    pickTeam = myPickTeam;
}

-(IBAction)goBack:(id)sender{
//    if([delegate respondsToSelector:@selector(dismissViewControllerAnimated:completion:)]){
    if([delegate respondsToSelector:NSSelectorFromString(@"dismissDoneVC")]){
//          [delegate dismissViewControllerAnimated:YES completion:nil];
        [delegate performSelector:NSSelectorFromString(@"dismissDoneVC") withObject:nil afterDelay:0];
    }else{
        NSLog(@"ERROR:%s",__PRETTY_FUNCTION__);
    }
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
