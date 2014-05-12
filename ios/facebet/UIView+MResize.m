//
//  UIView+MResize.m
//  takuya
//
//  Created by Kun on 2013-09-17.
//
//

#import "UIView+MResize.h"

@implementation UIView (MResize)


-(void)adjustKeyboard:(NSNotification*)aNotification flag:(BOOL)showKeyboard{
    NSDictionary* info = [aNotification userInfo];
    CGRect kbFrameEndFrame = [[info objectForKey:UIKeyboardFrameEndUserInfoKey] CGRectValue];
    NSTimeInterval animDuration = [[info objectForKey:UIKeyboardAnimationDurationUserInfoKey] doubleValue];
    [self adjustUIForKeyboard:kbFrameEndFrame.size animDuration:animDuration flag:showKeyboard];
}

- (void)adjustUIForKeyboard:(CGSize)keyboardSize animDuration:(NSTimeInterval)duration flag:(BOOL)showKeyboard{
    
    CGFloat delta=215;
    if(UI_USER_INTERFACE_IDIOM() == UIUserInterfaceIdiomPhone)
    {
        CGSize result = [[UIScreen mainScreen] bounds].size;
        if(result.height == 480)
        {
            // iPhone Classic
            delta = 205;
        }
        if(result.height == 568)
        {
            delta = 245;
            // iPhone 5
        }
    }
    if (showKeyboard) {
        [self moveUpBy:keyboardSize.height>delta?keyboardSize.height-delta:0 duration:duration];
    } else {
        [self moveDownBy:keyboardSize.height>delta?keyboardSize.height-delta:0 duration:duration];
    }
}



-(CGRect)frameByStretch:(CGFloat)alpha{
    CGRect myFrame = [self frame];
 //   CGPoint center = [self center];
    
    return CGRectMake(myFrame.origin.x-alpha, myFrame.origin.y-alpha, myFrame.size.width+alpha*2, myFrame.size.height+alpha*2);
}

-(void)moveToY:(CGFloat)y duration:(NSTimeInterval)duration{
    if(y!=self.frame.origin.y){
        CGRect oldFrame = self.frame;
        CGRect newFrame = CGRectMake(oldFrame.origin.x, y, oldFrame.size.width, oldFrame.size.height);
        [self changeToFrame:newFrame duration:duration];
    }
}

-(void)moveFrameToY:(CGFloat)y{
    if(y!=self.frame.origin.y){
        CGRect oldFrame = self.frame;
        CGRect newFrame = CGRectMake(oldFrame.origin.x, y, oldFrame.size.width, oldFrame.size.height);
        [self setFrame:newFrame];
    }
}


-(void)moveFrameUpBy:(CGFloat)delta{
    CGRect oldFrame = self.frame;
    CGRect newFrame = CGRectMake(oldFrame.origin.x, oldFrame.origin.y-delta, oldFrame.size.width, oldFrame.size.height);
    [self setFrame:newFrame];
}

-(void)moveRightBy:(CGFloat)delta{
    CGRect oldFrame = self.frame;
    CGRect newFrame = CGRectMake(oldFrame.origin.x+delta, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
    [self setFrame:newFrame];
}

-(void)moveLeftBy:(CGFloat)delta{
    CGRect oldFrame = self.frame;
    CGRect newFrame = CGRectMake(oldFrame.origin.x-delta, oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height);
    [self setFrame:newFrame];
}

-(void)moveUpBy:(CGFloat)delta duration:(NSTimeInterval)duration{
    CGRect oldFrame = self.frame;
    CGRect newFrame = CGRectMake(oldFrame.origin.x, oldFrame.origin.y-delta, oldFrame.size.width, oldFrame.size.height);
    [self changeToFrame:newFrame duration:duration];
}

-(void)moveDownBy:(CGFloat)delta duration:(NSTimeInterval)duration{
    CGRect oldFrame = self.frame;
    CGRect newFrame = CGRectMake(oldFrame.origin.x, oldFrame.origin.y+delta, oldFrame.size.width, oldFrame.size.height);
    [self changeToFrame:newFrame duration:duration];
}

-(void)shift:(CGFloat)xDelta y:(CGFloat)yDelta{
    CGRect oldFrame = self.frame;
    CGRect newFrame = CGRectMake(oldFrame.origin.x+xDelta, oldFrame.origin.y+yDelta, oldFrame.size.width, oldFrame.size.height);
    [self changeToFrame:newFrame duration:0.0];
}

-(void)verticalStretch:(CGFloat)delta duration:(NSTimeInterval)duration botBaseline:(BOOL)freezeBot{
    CGRect oldFrame = self.frame;
    CGRect newFrame = CGRectMake(oldFrame.origin.x, freezeBot?oldFrame.origin.y-delta:oldFrame.origin.y, oldFrame.size.width, oldFrame.size.height+delta);
    [self changeToFrame:newFrame duration:duration];
}

-(void)changeToFrame:(CGRect)newFrame duration:(NSTimeInterval)duration{
    [UIView animateWithDuration:duration
                     animations:^(void) {
                         self.frame = newFrame;
                     }
                     completion:NULL];
}

-(void)spinForever{
    CABasicAnimation* rotationAnimation;
    rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    rotationAnimation.toValue = [NSNumber numberWithFloat: M_PI * 2.0 /* full rotation*/ * 1 * 1.0 ];
    rotationAnimation.duration = 1.0;
    rotationAnimation.cumulative = YES;
    rotationAnimation.repeatCount = HUGE_VALF;
    [self.layer addAnimation:rotationAnimation forKey:@"rotationAnimation"];
    [self setNeedsDisplay];
}

-(void)spinToDegree:(CGFloat)degree{
    CABasicAnimation* rotationAnimation;
    rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
    rotationAnimation.fromValue = 0;
    rotationAnimation.toValue = [NSNumber numberWithFloat: degree* M_PI/180.0 ];
    rotationAnimation.duration = 0.5;
    rotationAnimation.repeatCount = 1;
    rotationAnimation.cumulative = NO;
    rotationAnimation.removedOnCompletion = NO;
    rotationAnimation.fillMode = kCAFillModeForwards;
    [self.layer addAnimation:rotationAnimation forKey:@"rotationAnimation"];
}


-(void)stopSpin{
    CABasicAnimation* rotationAnimation;
    rotationAnimation = [CABasicAnimation animationWithKeyPath:@"transform.rotation.z"];
//    rotationAnimation.fromValue = 0;
    rotationAnimation.toValue = 0;
    rotationAnimation.cumulative = YES;
    rotationAnimation.repeatCount = 1;
    [self.layer addAnimation:rotationAnimation forKey:@"rotationAnimation"];
}

@end
