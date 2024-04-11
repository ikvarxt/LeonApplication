#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_leonapplication_nativedemo_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT void JNICALL
Java_com_example_leonapplication_nativedemo_NativeLib_allocMemory(
        JNIEnv *env,
        jobject,
        jlong memorySize) {

    long reqMemorySize = static_cast<long>(memorySize);
    void *memory = malloc(reqMemorySize);

    if (memory == nullptr) {
        return;
    }
    memset(memory, 0, reqMemorySize);
    return;
}
