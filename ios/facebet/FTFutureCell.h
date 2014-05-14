//
//  FTFutureCell.h
//  facebet
//
//  Created by Kun on 2013-12-29.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "FUGameType.h"
#import "FBetButtons.h"

/**
 The Cell that displays an upcoming game where user can choose a team that redirects 
 them to the game detail screen
 */

@interface FTFutureCell : UIView


@property(assign,nonatomic) BOOL isOpen;


@property(assign,nonatomic) FGame* myGame;
@property(strong,nonatomic) FUGameType* gameTypeHead;
@property(strong,nonatomic) UIButton* overBtn;
@property(strong,nonatomic) FBetButtons* betButtons;
@property(strong,nonatomic) NSIndexPath* path;


@property(assign,nonatomic) id parent;
@property(assign,nonatomic) SEL adjustSEL;

-(void)initFutureCell;

-(void)toggleDetail;
-(void)setupGame:(FGame*)game handler:(id<FBetButtonProtocol>)handle;
-(id)init;

@end
