//
//  FWagerScroll.h
//  facebet
//
//  Created by Kun on 2013-12-28.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

/**
    The reuable scroll view UI where users can click on different wager amount chips
    - Used in FBettingVC, made clickable etc.
    - Used in FResultVC, disabled to just show how much users have bet
 */

@interface FWagerScroll : UIScrollView

@property(assign,nonatomic) NSInteger price;

@property(assign,nonatomic) id del;
@property(assign,nonatomic) SEL sel;

-(void)buttonPressed:(id)sender;

-(void)configDelegate:(id)delegate selector:(SEL)selector;

-(void)selectAlreadyPlacedWager:(NSInteger)wager;
-(void)selectNewBetWager:(NSInteger)wager;
@end
