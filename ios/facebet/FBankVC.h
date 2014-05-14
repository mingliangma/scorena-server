//
//  FBankVC.h
//  facebet
//
//  Created by Kun on 2013-12-20.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

/**
    The user's bank balance
 */

@interface FBankVC : UIViewController

IBElement UILabel* balanceLB;
IBElement UIButton* getBtn;


-(void)configureBalance:(NSInteger)balance;

-(IBAction)getCoins:(id)sender;

@end
