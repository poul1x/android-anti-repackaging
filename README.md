# Android anti-repackaging

**WARNING**: All provided checks can be bypassed. There's no silver bullet to protect your app from being hacked. However, this may protect your app from automated patching tools and low-skilled crackers. To enhance the security level, integrate anti-repackaging techniques with additional security measures such as code obfuscation and encryption, and consider relocating security checks to native code.

## How it works

APK repackaging is a process of taking an existing Android application package file (APK) and modifying it to include malicious code or to perform actions different from the original intent of the application.

The standard method for safeguarding the APK from repackaging involves computing hash sums for the crucial functional components within the APK and verifying their consistency with the originals.

Another approach is to validate that the public key of the APK signature corresponds to the original key.

Finally, it is feasible to compute a hash sum for the entire APK file, but the original hash sum must be obtained from a remote source, particularly an API server or external storage (for educational purposes only).

All techniques mentioned above have been implemented in this repository.

## How to use

### Installation

Install and run application with commands:

```bash
# Download and install APK
curl -LJO https://github.com/poul1x/android-anti-repackaging/releases/download/0.1.0/antrp.apk
adb install antrp.apk

# Download APK hash file and put it in the Documents folder
curl -LJO  https://raw.githubusercontent.com/poul1x/android-anti-repackaging/0.1.0/apk_hash.txt
adb push apk_hash.txt /sdcard/Documents

# Run application
adb shell monkey -p com.example.antrp 1
```

Application must have all integrity checks passed

### Repackaging

Perform APK repackaging with the tool of your choice. If you don't have one, check out [APKLab](https://github.com/APKLab/APKLab)

Reinstall and run repackaged application. Some application integrity checks must fail.
