//
//  FCharts.m
//  facebet
//
//  Created by Kyle on 2014-01-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FCharts.h"

@implementation FCharts

@synthesize amountChart,percentChart,amountDonut,percentDonut;

@synthesize winLabel,winText,payoutLabel,payoutText;

- (id)initWithFrame:(CGRect)frame
{
    self = [super initWithFrame:frame];
    if (self) {
        // Initialization code
        
        [self loadUI];
    }
    return self;
}

-(void)loadUI{
    
    amountDonut = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Donut.png"]];
    [amountDonut setCenter:CGPointMake(80, 80)];
    [self addSubview:amountDonut];
    
    percentDonut = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"Donut.png"]];
    [percentDonut setCenter:CGPointMake(230, 80)];
    [self addSubview:percentDonut];
    
    amountChart = [[XYPieChart alloc] initWithFrame:CGRectMake(0, 0, 90, 90) Center:CGPointMake(80, 80) Radius:45];
    [amountChart setTag:100];
    [amountChart setShowPercentage:NO];
    [amountChart setLabelFont:[UIFont fStraightFont:12]];
    [amountChart setDataSource:self];
    [self addSubview:amountChart];
    
    percentChart = [[XYPieChart alloc] initWithFrame:CGRectMake(0, 0, 90, 90) Center:CGPointMake(230, 80) Radius:45];
    [percentChart setTag:200];
    [percentChart setShowPercentage:NO];
    [percentChart setLabelFont:[UIFont fStraightFont:12]];
    [percentChart setDataSource:self];
    [self addSubview:percentChart];
    
    winLabel = [[UILabel alloc] initWithFrame:CGRectMake(40, 120, 140, 30)];
    [self configLB:winLabel];
    [winLabel setText:@"TOTAL WINNING"];
    [winLabel setTextColor:[UIColor fGreenColor]];
    
    payoutLabel = [[UILabel alloc] initWithFrame:CGRectMake(180, 120, 140, 30)];
    [self configLB:payoutLabel];
    [payoutLabel setText:@"AVERAGE PAYOUT"];
    [payoutLabel setTextColor:[UIColor fGreenColor]];
    
    winText = [[UILabel alloc] initWithFrame:CGRectMake(10, 132, 140, 30)];
    [self configLB:winText];
    [winText setTextAlignment:NSTextAlignmentCenter];
    [winText setText:@"$2600"];
    [winText setTextColor:[UIColor fBlueColor]];
    
    payoutText = [[UILabel alloc] initWithFrame:CGRectMake(160, 132, 140, 30)];
    [self configLB:payoutText];
    [payoutText setTextAlignment:NSTextAlignmentCenter];
    [payoutText setText:@"+65%"];
    [payoutText setTextColor:[UIColor fBlueColor]];
    
    [self addSubview:winLabel];
    [self addSubview:payoutLabel];
    [self addSubview:winText];
    [self addSubview:payoutText];
}

-(void)configLB:(UILabel*)lb{
    [lb setFont:[UIFont fStraightFont:10]];
    [lb setBackgroundColor:[UIColor clearColor]];
}

-(void)reload{
    [percentChart reloadData];
    [amountChart reloadData];
}

- (NSUInteger)numberOfSlicesInPieChart:(XYPieChart *)pieChart{
    return 2;
}

- (CGFloat)pieChart:(XYPieChart *)pieChart valueForSliceAtIndex:(NSUInteger)index{
    if(index ==0){
        return 35;
    }else{
        return 65;
    }
}
- (UIColor *)pieChart:(XYPieChart *)pieChart colorForSliceAtIndex:(NSUInteger)index{
    if(index==0){
        return [UIColor clearColor];
    }else{
        return [UIColor fOrangeColor];
    }
}

- (NSString *)pieChart:(XYPieChart *)pieChart textForSliceAtIndex:(NSUInteger)index{
    
    
    if(pieChart.tag == 100){
        
        return index==1?@"$2.6k":@"";
    }else if(pieChart.tag == 200){
        return index==1?@"65%":@"";
    }
    return @"";
}


@end
