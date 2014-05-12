//
//  FCoinsVC.h
//  facebet
//
//  Created by Kyle on 2014-04-20.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FCoinsVC : UIViewController{
    NSMutableArray* coinArray;
}

@property(nonatomic,strong)  UIScrollView* scroll;

-(void)invite;
-(void)buyOne;
    
-(void)buyTwo;

-(void)buyThree;

-(void)buyFour;
@end
