//
//  FReloadView.h
//  facebet
//
//  Created by Kyle on 2014-04-26.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FReloadView : UIView


@property(nonatomic,strong) UIImageView* spin;

-(void)triggerUpdate;
-(void)stopSpin;

@end
