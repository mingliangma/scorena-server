//
//  FFeatureController.h
//  facebet
//
//  Created by Kun on 2013-12-03.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

#import "FVPromo.h"

@interface FFeatureController : UIViewController<UIScrollViewDelegate>{
    NSMutableArray* games;
}

@property (strong, nonatomic) UIScrollView* scrollView;
@property (assign, nonatomic) IBOutlet UIPageControl* pageControl;

@property (strong, nonatomic) NSArray* features;

-(void)loadBetScreen:(id)bet;

-(void)refreshWithDataObject:(id)obj;

@end
