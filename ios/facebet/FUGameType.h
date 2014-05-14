//
//  FUGameType.h
//  facebet
//
//  Created by Kun on 2013-12-30.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>


/**
    The UI component for a question, eg:"who will win?"
    - Used in FBettingVC, FResultVC & FGameController
 */


@interface FUGameType : UIView

@property(strong,nonatomic) UILabel* gameTypeLB;
@property(strong,nonatomic) UIImageView* whiteBar;
@property(nonatomic,strong) UIImageView* ppl;
@property(strong,nonatomic) UIImageView* arrow;
@property(nonatomic,strong) UILabel* crowdLB;
@property(nonatomic,strong) UIImageView* pickView;



-(void)setupContent:(id)obj;
-(NSString*)totalNumberString:(FGame*)game;

@end
