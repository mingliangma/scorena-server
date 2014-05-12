//
//  FRangeButtons.h
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FRangeButtons : UIView

IBElement UIButton* weekBtn;
IBElement UIButton* monthBtn;
IBElement UIButton* allBtn;

@property(nonatomic,assign) id parentVC;

-(id)initWithPoint:(CGPoint)point;

-(IBAction)weekClicked:(id)sender;
-(IBAction)monthClicked:(id)sender;
-(IBAction)allClicked:(id)sender;

@end
