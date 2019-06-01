#include <jni.h>
#include <string>

//
// Created by Manuel Chris-Ogar on 6/1/2019.
//

extern "C" JNIEXPORT jstring JNICALL
Java_ng_riby_androidtest_MainActivity_getNativeKey(
        JNIEnv *env,
        jobject /* this */) {
    std::string mNativeKey="QUl6YVN5Q29BNnZMMHQyaXM1TGlXMVcxQzhIdmprRlBPalQ0TU80";
    return env->NewStringUTF(mNativeKey.c_str());
}



