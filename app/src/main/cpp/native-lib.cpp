//
// Created by Manuel Chris-Ogar on 6/1/2019.
//

#include <jni.h>
#include <string>

extern "C" JNIEXXPORT jstring

JNICALL
ng.riby.androidtest_MainActivity_getNativeKey(JNIEnv *env, jobject) {


    std::string mNativeKey="TmF0aXZlNWVjcmV0UEBzc3cwcmQx";
 return env->NewStringUTF(mNativeKey.c_str());
}

