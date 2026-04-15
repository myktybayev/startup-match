package kz.diplomka.startupmatch.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import kz.diplomka.startupmatch.R;

/**
 * WhatsApp чатын ашу (халықаралық формат: тек цифрлар, ел кодымен).
 */
public final class WhatsAppUtils {

    private WhatsAppUtils() {
    }

    /**
     * @return true егер intent іске қосылды
     */
    public static boolean openChat(
            @NonNull Context context,
            @Nullable String phoneRaw,
            @NonNull String message
    ) {
        String digits = digitsForWhatsApp(phoneRaw);
        if (digits.isEmpty()) {
            return false;
        }
        String encoded;
        try {
            encoded = URLEncoder.encode(message, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encoded = "";
        }
        Uri uri = Uri.parse("https://api.whatsapp.com/send?phone=" + digits + "&text=" + encoded);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            intent.setPackage("com.whatsapp");
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException ignored) {
        }
        try {
            intent.setPackage("com.whatsapp.w4b");
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException ignored) {
        }
        intent.setPackage(null);
        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.incoming_whatsapp_failed, Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    /**
     * Тек цифрлар қалдырады; KZ үшін 8XXXXXXXXXX → 7XXXXXXXXXX.
     */
    @NonNull
    public static String digitsForWhatsApp(@Nullable String raw) {
        if (raw == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < raw.length(); i++) {
            char ch = raw.charAt(i);
            if (ch >= '0' && ch <= '9') {
                sb.append(ch);
            }
        }
        String d = sb.toString();
        if (d.length() == 11 && d.charAt(0) == '8') {
            return '7' + d.substring(1);
        }
        return d;
    }

    public static void openChatOrToast(
            @NonNull Context context,
            @Nullable String phoneRaw,
            @NonNull String message
    ) {
        if (TextUtils.isEmpty(digitsForWhatsApp(phoneRaw))) {
            Toast.makeText(context, R.string.incoming_pitch_no_phone, Toast.LENGTH_SHORT).show();
            return;
        }
        openChat(context, phoneRaw, message);
    }
}
