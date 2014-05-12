//
//  FMoreButton.h
//  facebet
//
//  Created by Kyle on 2014-01-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FMoreButton : UIView

@property(nonatomic,strong) UIButton* overlay;

@property(nonatomic,strong) UILabel* titleLB;
@property(nonatomic,strong) UIImageView* circle;
@property(nonatomic,strong) UILabel* arrow;

- (id)initWithFrame:(CGRect)frame;
-(void)initWithTitle:(NSString*)title del:(id)del sel:(SEL)sel;

@end
