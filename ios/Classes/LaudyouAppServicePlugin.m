#import "LaudyouAppServicePlugin.h"
#if __has_include(<laudyou_app_service/laudyou_app_service-Swift.h>)
#import <laudyou_app_service/laudyou_app_service-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "laudyou_app_service-Swift.h"
#endif

@implementation LaudyouAppServicePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftLaudyouAppServicePlugin registerWithRegistrar:registrar];
}
@end
