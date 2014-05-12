//
//  FVPromo.h
//  facebet
//
//  Created by Kun on 2013-12-17.
//  Copyright (c) 2013 Doozi Entertainment Company Inc. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface FVPromo : UIView{
    FGame* myPromo;
}



IBElement UILabel* dateLB;
IBElement UILabel* teamA;
IBElement UILabel* teamB;
IBElement UIButton* betBtn;
IBElement UITextView* taunt;

IBElement UILabel* bottomA;
IBElement UILabel* bottomB;

@property(nonatomic,assign) id delegate;

-(void)initWithFeature:(id)feature;
-(IBAction)betClicked:(id)sender;


-(void)initWithGameObj:(FGame*)obj;
-(void)initWithNetworkData:(id)feature;

@end
