package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        try {

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("admin@hhash.co.in");
            helper.setTo(toEmail);
            helper.setSubject("🔐 OTP Verification - Kesava Helping Hearts");

            String htmlContent = buildOtpTemplate(otp);

            helper.setText(htmlContent, true); // 🔥 HTML enabled

            mailSender.send(message);

            System.out.println("✅ EMAIL SENT SUCCESS");

        } catch (Exception e) {
            System.out.println("❌ EMAIL FAILED");
            e.printStackTrace();
        }
    }
    
    private String buildOtpTemplate(String otp) {


        // Split OTP into digits
        char[] digits = otp.toCharArray();

        StringBuilder otpBoxes = new StringBuilder();

        for (char d : digits) {
            otpBoxes.append("""
                <span style="
                    display:inline-block;
                    width:45px;
                    height:55px;
                    line-height:55px;
                    margin:5px;
                    font-size:22px;
                    font-weight:bold;
                    background:#f4f4f4;
                    border-radius:8px;
                    text-align:center;
                    border:1px solid #ddd;
                ">""" + d + "</span>");
        }

        return """
        <html>
        <body style="margin:0;padding:0;background:#eef2f7;font-family:Segoe UI,Arial">

            <div style="max-width:520px;margin:40px auto;background:#fff;border-radius:14px;
                        overflow:hidden;box-shadow:0 15px 50px rgba(0,0,0,0.2)">

                <!-- HEADER -->
                <div style="background:linear-gradient(135deg,#1d4ed8,#ff416c);
                            padding:25px;text-align:center;color:white">

                   

                    <h2 style="margin-top:10px;">Kesava Helping Hearts</h2>
                </div>

                <!-- CONTENT -->
                <div style="padding:30px;text-align:center;color:#333">

                    <h3 style="margin-bottom:10px;">🔐 Verify Your Login</h3>

                    <p style="font-size:14px;color:#666;">
                        Use the OTP below to complete your login.
                    </p>

                    <!-- OTP BOXES -->
                    <div style="margin:25px 0;">
                        """ + otpBoxes.toString() + """
                    </div>

                    <p style="font-size:13px;color:#888;">
                        This OTP will expire in <b>5 minutes</b>.
                    </p>

                    <!-- BUTTON (VISUAL ONLY) -->
                    <div style="margin-top:25px;">
                        <span style="
                            display:inline-block;
                            padding:12px 25px;
                            background:#ff416c;
                            color:white;
                            border-radius:8px;
                            font-size:14px;
                            font-weight:bold;
                        ">
                            Secure Login Verification
                        </span>
                    </div>

                </div>

                <!-- FOOTER -->
                <div style="text-align:center;font-size:12px;color:#aaa;padding:15px">
                    © Helping Hearts And Supporting Hands Charitable Trust
                </div>

            </div>

        </body>
        </html>
        """;
    }
}
