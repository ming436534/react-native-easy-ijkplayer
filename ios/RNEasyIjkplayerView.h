//
//  RNEasyIjkplayerView.h
//  RNEasyIjkplayer
//
//  Created by lj on 2019/11/10.
//  Copyright © 2019 Facebook. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "React/RCTComponent.h"

NS_ASSUME_NONNULL_BEGIN

@interface RNEasyIjkplayerView : UIView

    @property(nonatomic,strong, readwrite) NSURL *url;
    @property(nonatomic,strong, readwrite) NSNumber *autoPlay;
    @property(nonatomic,strong, readwrite) NSNumber *duration;
    @property(nonatomic,strong, readwrite) NSDictionary *size;

    @property(nonatomic, copy) RCTDirectEventBlock onPrepared;
    @property(nonatomic, copy) RCTDirectEventBlock onProgressUpdate;
    @property(nonatomic, copy) RCTDirectEventBlock onLoadProgressUpdate;
    @property(nonatomic, copy) RCTDirectEventBlock onInfo;
    @property(nonatomic, copy) RCTDirectEventBlock onError;
    @property(nonatomic, copy) RCTDirectEventBlock onComplete;

    @property (nonatomic, strong) NSThread *progressUpdateThread;
    @property (nonatomic, strong) NSTimer *progressUpdateTimer;

    -(instancetype)init;
    -(void)play;
    -(void)pause;
    -(void)stop;
    -(void)seekTo:(NSInteger)time;

@end

NS_ASSUME_NONNULL_END
