//
//  FHistoryVC.h
//  facebet
//
//  Created by Kun on 2013-12-20.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "FReloadView.h"

/**
    The User Ranking View controller
 
 */


@interface FHistoryVC : UIViewController<UITableViewDataSource,UITableViewDelegate>{
    id rankObj;
    BOOL isWeek;
    BOOL scrollLock;
}

IBElement UIButton* weekButton;
IBElement UIButton* allButton;

@property(nonatomic,strong) UITableView* table;
@property(nonatomic,strong) FReloadView* reloadView;
-(void)refreshRanking;


-(IBAction)weekClicked:(id)sender;
-(IBAction)allClicked:(id)sender;


@end
