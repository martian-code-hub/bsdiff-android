#include <jni.h>
#include <cstdlib>


extern "C" {
int bsdiff_main(int argc, const char *argv[]);

int bspatch_main(int argc, const char *argv[]);
}

extern "C"
JNIEXPORT jint JNICALL
Java_com_martian_bsdiff_BsdiffUtils_diff__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2(JNIEnv *env, jclass clazz, jstring old_file,
                                                                                                    jstring new_file, jstring patch_file) {
    // for cpp
    const char *oldFile = env->GetStringUTFChars(old_file, 0);
    const char *patchFile = env->GetStringUTFChars(patch_file, 0);
    const char *newFile = env->GetStringUTFChars(new_file, 0);

    const char *argv[] = {"", oldFile, newFile, patchFile};

    int result = bsdiff_main(4, argv);

    env->ReleaseStringUTFChars(old_file, oldFile);
    env->ReleaseStringUTFChars(patch_file, patchFile);
    env->ReleaseStringUTFChars(new_file, newFile);

    return result;
}
extern "C"
JNIEXPORT jint JNICALL
Java_com_martian_bsdiff_BsdiffUtils_patch__Ljava_lang_String_2Ljava_lang_String_2Ljava_lang_String_2(JNIEnv *env, jclass clazz, jstring old_file,
                                                                                                     jstring patch_file, jstring new_file) {
    // for cpp
    const char *oldFile = env->GetStringUTFChars(old_file, 0);
    const char *patchFile = env->GetStringUTFChars(patch_file, 0);
    const char *newFile = env->GetStringUTFChars(new_file, 0);

    const char *argv[] = {"", oldFile, newFile, patchFile};

    int result = bspatch_main(4, argv);

    env->ReleaseStringUTFChars(old_file, oldFile);
    env->ReleaseStringUTFChars(new_file, newFile);
    env->ReleaseStringUTFChars(patch_file, patchFile);

    return result;
}