//
//  FMoreButton.m
//  facebet
//
//  Created by Kyle on 2014-01-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FMoreButton.h"

@implementation FMoreButton

@synthesize overlay,titleLB,arrow,circle;


- (id)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        [self setBackgroundColor:[UIColor whiteColor]];
        [self loadUI];
    }
    return self;
}

-(void)loadUI{
    
    titleLB = [[UILabel alloc] initWithFrame:CGRectMake(85, 13, 180, 30)];
    [titleLB setFont:[UIFont fStraightFont:15]];
    [titleLB setTextColor:[UIColor fGrayColor]];
    [self addSubview:titleLB];
    
    circle = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Circle_More.png"]];
    [circle setCenter:CGPointMake(40, 28)];
    [self addSubview:circle];
    
    arrow = [[UILabel alloc] initWithFrame:CGRectMake(290, 13, 30, 30)];
    [arrow setText:@">"];
    [arrow setTextColor:[UIColor fBlueColor]];
    [arrow setFont:[UIFont fStraightFont:20]];
    
    [self addSubview:arrow];
    
    overlay = [[UIButton alloc] initWithFrame:CGRectMake(0, 0, 320, 65)];
    [self addSubview:overlay];
}

-(void)initWithTitle:(NSString*)title del:(id)del sel:(SEL)sel{
    
    [titleLB setText:title];
    [overlay addTarget:del action:sel forControlEvents:UIControlEventTouchUpInside];
}

@end
