package com.example.registrasi.nim0420230007.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class CaptchaService {

    private static final String CAPTCHA_CHARS = "ABCDEFGHJKMNPQRSTUVWXYZ23456789";
    private static final int CAPTCHA_LENGTH = 6;

    private final Random random = new Random();

    /**
     * Generate kode captcha baru secara acak.
     *
     * @return string captcha sepanjang CAPTCHA_LENGTH karakter
     */
    public String generateCaptcha() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            sb.append(CAPTCHA_CHARS.charAt(random.nextInt(CAPTCHA_CHARS.length())));
        }
        return sb.toString();
    }

    /**
     * Validasi input captcha dari user dengan captcha yang tersimpan di session.
     * Perbandingan case-insensitive.
     *
     * @param input          input dari user
     * @param sessionCaptcha captcha yang tersimpan di session
     * @return true jika cocok
     */
    public boolean validate(String input, String sessionCaptcha) {
        if (input == null || sessionCaptcha == null)
            return false;
        return input.trim().equalsIgnoreCase(sessionCaptcha.trim());
    }
}
