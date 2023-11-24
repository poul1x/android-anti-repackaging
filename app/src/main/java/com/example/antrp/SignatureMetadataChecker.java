package com.example.antrp;

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
    private static final String DN = "CN=|You Can't|,OU=|Hack|,O=|My|,L=|App|,ST=|I'm number|,C=|01|";
    private boolean mPassed = false;

    @Override
    public String name() {
        return TAG;
    }

    public boolean passed() {
        return mPassed;
    }

    SignatureMetadataChecker() {
        checkSubjectDN();
    }

    private static X509Certificate getX509Certificate(Signature signature) {
        try {
            byte[] signatureBytes = signature.toByteArray();
            CertificateFactory certFactory = java.security.cert.CertificateFactory.getInstance("X.509");
            return (X509Certificate) certFactory.generateCertificate(new ByteArrayInputStream(signatureBytes));
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void checkSubjectDN() {
        try {
            String packageName = MyApplication.context.getPackageName();
            PackageManager packageManager = MyApplication.context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

            Signature[] signatures = packageInfo.signatures;
            for (Signature signature : signatures) {
                X509Certificate cert = getX509Certificate(signature);
                if (cert == null) {
                    continue;
                }

                String subjectDN = cert.getSubjectDN().getName();
                Log.d(TAG, String.format("getSubjectDN(): %s", subjectDN));

                if (subjectDN.equals(DN)) {
                    mPassed = true;
                    break;
                }
            }

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, String.format("Failed to get signatures. Reason - %s", e.toString()));
        }
    }

}
