package com.example.antrp;

import static com.example.antrp.Util.loadHashFile;
import static com.example.antrp.Util.toHexString;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class SignatureMetadataChecker extends IntegrityChecker {

    private static final String TAG = SignatureMetadataChecker.class.getSimpleName();
    private static final String KEY_FILE = "public_key.txt";
    private boolean mPassed = false;

    @Override
    public String name() {
        return TAG;
    }

    public boolean passed() {
        return mPassed;
    }

    SignatureMetadataChecker() {
        checkPublicKey();
    }


    private static X509Certificate getX509Certificate(Signature signature) {
        try {
            byte[] signatureBytes = signature.toByteArray();
            CertificateFactory certFactory = java.security.cert.CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signatureBytes));
        } catch (CertificateException e) {
            Log.e(TAG, String.format("Failed to get X509Certificate. Reason - %s", e.toString()));
            return null;
        }
    }

    private void checkPublicKey() {
        String origPublicKeyString = loadHashFile(KEY_FILE);
        if (origPublicKeyString == null) {
            return;
        }

        try {
            String packageName = MyApplication.context.getPackageName();
            PackageManager packageManager = MyApplication.context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            for (Signature signature : packageInfo.signatures) {
                X509Certificate cert = getX509Certificate(signature);
                if (cert == null) {
                    continue;
                }

                String publicKeyString = toHexString(cert.getPublicKey().getEncoded());
                Log.d(TAG, String.format("Found public key %s", publicKeyString));

                if (publicKeyString.equals(origPublicKeyString)) {
                    mPassed = true;
                    break;
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, String.format("Failed to get signatures. Reason - %s", e.toString()));
        }
    }

}
