//
//  MGeo.h
//  movie
//
//  Created by Kun on 13-07-10.
//
//

#import <Foundation/Foundation.h>
#import "MUtil.h"

/**
 *  The geo mapping coordinates start from the top left corner of the phone,
 *  So point {0,0} on iPhone 4 would be ccp(0,480)
 *  Point {0,0} on iPhone 5 would be ccp(0,568)
 */

@interface MGeo : NSObject{
    NSDictionary* geoMap;
    kDeviceScreenType screenType;
    
    NSDictionary* pointMap;
    NSDictionary* sizeMap;
    NSDictionary* lengthMap;
}

+(MGeo*) shared;

-(CGPoint)ppk:(NSString*)key;
-(CGSize)psk:(NSString*)key;
-(CGFloat)plk:(NSString*)key;

@end

