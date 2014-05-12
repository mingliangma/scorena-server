//
//  FRankCell.m
//  facebet
//
//  Created by Kyle on 2014-05-03.
//  Copyright (c) 2014 Doozi Entertainment Company Inc. All rights reserved.
//

#import "FRankCell.h"

@implementation FRankCell

@synthesize nameLB,scoreLB,rankLB;

-(id)init{
    self = [[[NSBundle mainBundle] loadNibNamed:@"FRankCell" owner:nil options:nil] firstObject];
    return self;
}

- (void)awakeFromNib
{
    // Initialization code
}

- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
