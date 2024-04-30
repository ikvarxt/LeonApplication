#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_leonapplication_nativedemo_NativeLib_stringFromJNI(
        JNIEnv *env,
        jobject obj) {
    std::string hello = "Hello from C++";
    jclass cls = env->GetObjectClass(obj);
    jmethodID jvmString = env->GetMethodID(cls, "stringFromJvm", "()Ljava/lang/String;");
    jobject res = env->CallObjectMethod(obj, jvmString);
    const char *resStr = env->GetStringUTFChars((jstring) res, nullptr);
    hello.append(resStr);
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
