#ifndef ANDROID_FOPEN_H
#define ANDROID_FOPEN_H

#include <stdio.h>
#include <android/asset_manager.h>
#include <android/log.h>

#ifdef __cplusplus
extern "C" {
#endif




/* hijack fopen and route it through the android asset system so that
   we can pull things out of our packagesk APK */


FILE* android_fopen(const char* fname, const char* mode);

#define fopen(name, mode) android_fopen(name, mode)

#define printf(...) __android_log_print(ANDROID_LOG_DEBUG, "LOG_D", __VA_ARGS__)

#ifdef __cplusplus
}
#endif

#endif