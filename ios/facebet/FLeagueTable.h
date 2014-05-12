//
//  FLeagueTable.h
//  facebet
//
//  Created by Kyle on 2014-05-02.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FLeagueTable : UIView

@property(nonatomic,assign) id parentVC;

- (id)initWithPoint:(CGPoint)point;
-(void)configureWithLeagueJson:(id)obj;

@end
