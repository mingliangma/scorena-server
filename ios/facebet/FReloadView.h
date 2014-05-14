//
//  FReloadView.h
//  facebet
//
//  Created by Kyle on 2014-04-26.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

/**
    The reload spinner when user pulls down
    Can be replaced with UIRefreshControl if no customized style is needed
 */

@interface FReloadView : UIView


@property(nonatomic,strong) UIImageView* spin;

-(void)triggerUpdate;
-(void)stopSpin;

@end
