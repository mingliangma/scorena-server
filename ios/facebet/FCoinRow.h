//
//  FCoinRow.h
//  facebet
//
//  Created by Kyle on 2014-04-25.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FCoinRow : UIView

IBElement UILabel* coinLB;
IBElement UILabel* priceLB;
IBElement UIButton* buyBtn;
IBElement UITextView* descBtn;
IBElement UIImageView* coinImg;
@property(nonatomic,assign) SEL sel;
@property(nonatomic,assign) id delegate;

-(id)initWithPoint:(CGPoint)point;
-(void)configureWithObj:(id)obj del:(id)del;

@end
