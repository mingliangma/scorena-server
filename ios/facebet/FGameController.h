//
//  FGameController.h
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FTFutureCell.h"
#import "FTPastCell.h"
#import "FReloadView.h"

@interface FGameController : UIViewController <UITableViewDataSource,UITableViewDelegate,FBetButtonProtocol,UIScrollViewDelegate>{
    
    NSInteger pastSection;
    NSInteger pastRow;
    
    NSInteger futureSection;
    NSInteger futureRow;
    
    BOOL isPast;
    
    BOOL scrollLock;
}

@property(nonatomic,strong) FReloadView* futureReloadView;
@property(nonatomic,strong) FReloadView* pastReloadView;

IBElement UITableView* futureTable;
IBElement UITableView* pastTable;

@property(nonatomic,strong) NSMutableArray* futureGames;
@property(nonatomic,strong) NSMutableArray* pastGames;

@property(nonatomic,strong) NSMutableArray* futureDetails;
@property(nonatomic,strong) NSMutableArray* pastDetails;

@property(nonatomic,strong) UIButton* upcoming;
@property(nonatomic,strong) UIButton* past;

-(void)toggleRow:(NSIndexPath*)path;
-(void)goPastGame;
-(void)noTeamSelected;


@end
