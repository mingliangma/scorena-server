//
//  FPickPanel.m
//  facebet
//
//  Created by Kyle on 2014-05-06.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FPickPanel.h"

@implementation FPickPanel

@synthesize decideLB,decideLeft,decideMiddle,decideRight,pick;

- (id)initWithPoint:(CGPoint)point{
    self = [super initWithFrame:CGRectMake(point.x, point.y, 300, 105)];
    [self loadUI];
    return self;
}

-(void)loadUI{
    pick = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 180, 30)];
    [pick setTextColor:[UIColor fRedColor]];
    [decideLB setFont:[UIFont fItalicFont:16]];
    [pick setTextAlignment:NSTextAlignmentCenter];
    
    decideLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 140, 30)];
    [decideLB setText:@"Your pick"];
    [decideLB setTextAlignment:NSTextAlignmentCenter];
    [decideLB setFont:[UIFont fItalicFont:16]];
    [decideLB setTextColor:[UIColor fGreenColor]];
    
    decideLeft = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 70, 1)];
    [decideLeft setBackgroundColor:[UIColor fGreenColor]];
    
    decideRight = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 70, 1)];
    [decideRight setBackgroundColor:[UIColor fGreenColor]];
    
    decideMiddle = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 280, 1)];
    [decideMiddle setBackgroundColor:[UIColor fGreenColor]];
    
    [pick shift:70 y:25];
    [decideLeft shift:15 y:20];
    [decideMiddle shift:15 y:100];
    [decideLB shift:90 y:5];
    [decideRight shift:225 y:20];
    
    [self addSubview:decideRight];
    [self addSubview:decideLeft];
    [self addSubview:decideMiddle];
    
    [self addSubview:pick];
    [self addSubview:decideLB];
}

-(void)configureWithPick:(NSString*)myPick{
    
    [pick setText:[myPick copy]];

}


-(void)setupGrayUIWhenBetPlaced{
    [pick setTextColor:[UIColor fLightGrayColor]];
    [decideLB setTextColor:[UIColor fLightGrayColor]];
    [decideLeft setBackgroundColor:[UIColor fLightGrayColor]];
    [decideRight setBackgroundColor:[UIColor fLightGrayColor]];
    [decideMiddle setBackgroundColor:[UIColor fLightGrayColor]];
}

@end
