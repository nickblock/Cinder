 

cmake_minimum_required(VERSION 3.4.1) 
 
add_subdirectory(${CINDER_DIR}/proj/android ${CINDER_DIR}/buildCinderForAndroid) 
 
add_library( LocationManagerApp SHARED 
		../../../src/LocationManagerApp.cpp
    ../../../../../blocks/LocationManager/src/cinder/LocationManagerAndroid.cpp

)

target_include_directories( LocationManagerApp PRIVATE 
    ${CINDER_DIR}/include 
    ../../../include
    ${CINDER_DIR}/blocks/LocationManager/src
)

target_link_libraries( LocationManagerApp 
    cinder 
    ${CINDER_DIR}/lib/android/android-${NDK_PLATFORM}/${ANDROID_ABI}/libboost_system.a 
    ${CINDER_DIR}/lib/android/android-${NDK_PLATFORM}/${ANDROID_ABI}/libboost_filesystem.a 
    log android EGL GLESv3 OpenSLES z atomic 
) 

