//
//  FDoneBetVC.h
//  facebet
//
//  Created by Kyle on 2014-01-03.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

typedef enum{
    FDoneTypeBet,
    FDoneTypeWin,
    FDoneTypeLose
} FDoneType;

@interface FDoneBetVC : UIViewController{
    NSString* amountText;
    NSString* teamAText;
    NSString* teamBText;
    NSString* teamPic;
    NSString* typeText;
    NSString* pickTeam;
}

@property(nonatomic,assign) FDoneType type;

@property(nonatomic,assign) IBOutlet UITextView* typeView;
IBElement UILabel* congratLB;
IBElement UILabel* amountLB;
IBElement UILabel* teamA;
IBElement UILabel* teamB;
IBElement UILabel* teamLB;
IBElement UILabel* message;
IBElement UIImageView* bgImage;
IBElement UIImageView* teamImage;


@property(nonatomic,assign) id delegate;

-(void)configBet:(NSInteger)bet teamA:(NSString*)teamA teamB:(NSString*)teamB type:(NSString*)type pic:(NSString*)pic pickTeam:(NSString*)myPickTeam;


-(IBAction)goBack:(id)sender;

@end
