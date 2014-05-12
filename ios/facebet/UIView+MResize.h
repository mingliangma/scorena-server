//
//  UIView+MResize.h
//  takuya
//
//  Created by Kun on 2013-09-17.
//
//

#import <UIKit/UIKit.h>

@interface UIView (MResize)

-(void)adjustKeyboard:(NSNotification*)aNotification flag:(BOOL)showKeyboard;
-(void)adjustUIForKeyboard:(CGSize)keyboardSize animDuration:(NSTimeInterval)duration flag:(BOOL)showKeyboard;

-(CGRect)frameByStretch:(CGFloat)alpha;
-(void)moveToY:(CGFloat)y duration:(NSTimeInterval)duration;
-(void)moveUpBy:(CGFloat)delta duration:(NSTimeInterval)duration;
-(void)moveDownBy:(CGFloat)delta duration:(NSTimeInterval)duration;
-(void)verticalStretch:(CGFloat)delta duration:(NSTimeInterval)duration botBaseline:(BOOL)freezeBot;

-(void)moveFrameUpBy:(CGFloat)delta;
-(void)moveFrameToY:(CGFloat)y;

-(void)moveRightBy:(CGFloat)delta;
-(void)moveLeftBy:(CGFloat)delta;

-(void)shift:(CGFloat)xDelta y:(CGFloat)yDelta;


-(void)spinForever;
-(void)spinToDegree:(CGFloat)degree;
-(void)stopSpin;

@end
