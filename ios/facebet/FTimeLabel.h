//
//  FTimeLabel.h
//  facebet
//
//  Created by Kyle on 2014-01-03.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FTimeLabel : UILabel{
    NSTimer* myTimer;
}

@property(nonatomic,strong) NSDate* myDate;

-(void)startWithTime:(NSDate*)date;

@end
