//
//  FPayoutView.m
//  facebet
//
//  Created by Kun on 2013-12-29.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FPayoutView.h"

#define kRefreshCount 5

#define kInitCount 3

#define kStartY 30

@implementation FPayoutView

@synthesize parent,refreshSEL;

@synthesize topBorder,bottomLeft,bottomRight;

- (id)initWithFrame:(CGRect)frame array:(NSArray*)array
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        list = array;
        shown = kInitCount;
        
        [self initSkeleton];
        [self addHeaderRow];
        [self loadPayoutRows];
    }
    return self;
}

-(void)clean{
    NSArray* subviews = [self subviews];
    int i=0;
    
    for(i=0;i<[subviews count];i++){
        [[subviews objectAtIndex:i] removeFromSuperview];
    }
    [self removeFromSuperview];
}

-(void)loadPayoutRows{
    NSUInteger i;
    for(i=0;i<[list count];i++){
       id obj = [list objectAtIndex:i];
        [self addPayoutRow:obj index:i];
    }
}

-(void)initSkeleton{
    topBorder = [[UIView alloc] initWithFrame:CGRectMake(0, 0, 280, 1)];
    [topBorder setBackgroundColor:[UIColor fGreenColor]];
    [topBorder shift:0 y:0];
    [self addSubview:topBorder];
}

-(void)addHeaderRow{
    
    UIImageView* ppl = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"people_color.png"]];
    [ppl shift:50 y:6];
    
    UIImageView* coin = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"coin_color.png"]];
    [coin shift:155 y:5];
    
    UIImageView* meter = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"payout_color.png"]];
    [meter shift:230 y:10];
    
    UILabel* nameLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 80, 30)];
    [self configureLabel:nameLB];
    [nameLB setText:@"Name"];
    [nameLB shift:40 y:10];
    
    UILabel* betLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 70, 30)];
    [self configureLabel:betLB];
    [betLB setText:@"Wager"];
    [betLB shift:147 y:10];
    
    UILabel* payoutLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 80, 30)];
    [self configureLabel:payoutLB];
    [payoutLB setText:@"Payout"];
    [payoutLB shift:220 y:10];
    
    [self addSubview:ppl];
    [self addSubview:coin];
    [self addSubview:meter];
    [self addSubview:nameLB];
    [self addSubview:betLB];
    [self addSubview:payoutLB];
}

-(void)configureLabel:(UILabel*)lb{
    [lb setTextColor:[UIColor fBlueColor]];
    [lb setFont:[UIFont fStraightFont:12]];
}

-(void)addPayoutRow:(id)obj index:(NSUInteger)i{
    
    FWager* wager = [[FWager alloc] initWithJson:obj];
    
    UIImageView* circle = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Circle_Gray.png"]];
    
    [circle shift:7 y:kStartY+7+i*30];
    
    UILabel* nameLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 120, 30)];
    [nameLB setText:[wager name]];
    [nameLB setTextColor:[UIColor fGrayColor]];
    [nameLB setFont:[UIFont fStraightFont:13]];
    [nameLB shift:40 y:kStartY+i*30];
    
    UILabel* betLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 70, 30)];
    [betLB setText:[NSString stringWithFormat:@"$%d",[wager amount]]];
    [betLB setTextColor:[UIColor fGreenColor]];
    [betLB setFont:[UIFont fStraightFont:13]];
    [betLB shift:150 y:kStartY+i*30];
    
    UILabel* payoutLB = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, 80, 30)];
    [payoutLB setText:[NSString stringWithFormat:@"$%d",[wager payout]]];
    [payoutLB setTextColor:[UIColor fGreenColor]];
    [payoutLB setFont:[UIFont fStraightFont:13]];
    [payoutLB shift:220 y:kStartY+i*30];
    
    [self addSubview:circle];
    [self addSubview:nameLB];
    [self addSubview:betLB];
    [self addSubview:payoutLB];
    
    
    DD(@"%@",[wager name])
    if([[wager name] isEqualToString:[[FGlobal sharedInstance] userName]]){
        [nameLB setTextColor:[UIColor fRedColor]];
        [betLB setTextColor:[UIColor fRedColor]];
        [payoutLB setTextColor:[UIColor fRedColor]];
        [circle setImage:[UIImage imageNamed:@"Avatar_icon.png"]];
    }
}

-(BOOL)showReadMore{
    return [list count]>shown;
}

@end
