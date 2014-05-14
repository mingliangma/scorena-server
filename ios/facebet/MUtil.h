//
//  MUtil.h
//  takuya
//
//  Created by Kun on 13-02-17.
//
//

#import <Foundation/Foundation.h>


/**
 Utility static class that provides convenience methods for screen size detection, re-used UI component loading code etc.
 */

@interface MUtil : NSObject


+(NSString *)appDocumentsDir:(NSString*)file;
+(NSString*)tauntFromType:(NSString*)type;
+(CGFloat)screenHeight;
+(CGFloat)extraHeight;
+(kDeviceScreenType)screenType;
+(NSArray*)wagerArray;

+(NSString*)gameTypeString:(NSString*)type;
+(void)loadRightBar:(UIViewController*)vc;

+(void)showAlert:(NSString*)msg del:(id)del;
+(void)showAlert:(NSString*)msg title:(NSString*)title del:(id)del;
@end
