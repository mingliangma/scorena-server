//
//  FCharts.h
//  facebet
//
//  Created by Kyle on 2014-01-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XYPieChart.h"

/**
    Used in v1, can be deleted since we used pointers instead
 */

@interface FCharts : UIView <XYPieChartDataSource>

@property(nonatomic,strong) XYPieChart* amountChart;
@property(nonatomic,strong) XYPieChart* percentChart;

@property(nonatomic,strong) UIImageView* amountDonut;
@property(nonatomic,strong) UIImageView* percentDonut;

@property(nonatomic,strong) UILabel* winLabel;
@property(nonatomic,strong) UILabel* payoutLabel;

@property(nonatomic,strong) UILabel* winText;
@property(nonatomic,strong) UILabel* payoutText;

-(void)reload;

@end
