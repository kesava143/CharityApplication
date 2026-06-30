package com.example.controller;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLEncoder;
import java.util.List;
import java.util.stream.Collectors;

public class FastWhatsAppSender {

    public static void main(String[] args) {
        try {

            // ✅ Clickable UPI HTTPS link (IMPORTANT)
            String upiLink = "https://pay.google.com/gp/vpa/pay?pa=helpinghearts@upi&pn=HelpingHearts&cu=INR";

            // ✅ Professional Message
            String message = """
Helping Hearts & Supporting Hands

Hello Everyone,

We are continuing our mission to support the needy through:
- Food Distribution Programs
- Blood Donation Camps
- Medical Assistance Camps
- Emergency Support Initiatives

We invite you to contribute towards our monthly charity efforts.

Donation Details:
Phone / UPI: 9885795766
UPI ID: helpinghearts@upi

Click to Donate:
""" + upiLink + """

(Works with PhonePe, GPay, Paytm)

You can also scan our QR code (shared separately)

Website:
www.hhash.co.in

Thank you 
""";

            // ✅ Encode message
            String encodedMessage = URLEncoder.encode(message, "UTF-8");

            // ✅ Read numbers from file (same folder or full path)
            //List<String> numbers = Files.readAllLines(Paths.get("numbers.txt"));
			/*
			 * InputStream numbers = FastWhatsAppSender.class .getClassLoader()
			 * .getResourceAsStream("numbers.txt");
			 */
            
         // ✅ Read numbers.txt from resources
            InputStream numberStream = FastWhatsAppSender.class
                    .getClassLoader()
                    .getResourceAsStream("numbers.txt");

            if (numberStream == null) {
                throw new RuntimeException("numbers.txt not found in resources!");
            }

            List<String> numbers = new BufferedReader(new InputStreamReader(numberStream))
                    .lines()
                    .collect(Collectors.toList());

            for (String number : numbers) {
                if (number.trim().isEmpty()) continue;

                String url = "https://wa.me/" + number.trim() + "?text=" + encodedMessage;

                Desktop.getDesktop().browse(new URI(url));

                // ⏱️ Delay between messages (adjust if needed)
                Thread.sleep(6000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}