package com.example.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.example.dto.DonationDto;

@Service
public class DonationReceiptImageGenerator {

    /*----------------------------------------------------------
     * Template
     *---------------------------------------------------------*/

    private static final String TEMPLATE_PATH =
            "src/main/resources/static/images/donation-template.png";

    /*----------------------------------------------------------
     * Output Folder
     *---------------------------------------------------------*/

    private static final String OUTPUT_FOLDER =
            "generated/donation-receipts/";

    private static final String JPG_FOLDER =
            OUTPUT_FOLDER + "jpg/";

    private static final String PNG_FOLDER =
            OUTPUT_FOLDER + "png/";

    /*----------------------------------------------------------
     * Fonts
     *---------------------------------------------------------*/
    private static final Font VALUE_FONT =
            new Font("Times New Roman", Font.BOLD, 60);

    /*----------------------------------------------------------
     * Colors
     *---------------------------------------------------------*/

    private static final Color TEXT_COLOR =
            Color.BLACK;

    /*----------------------------------------------------------
     * Coordinates
     *---------------------------------------------------------*/

    private static final int NAME_X = 1200;
    private static final int NAME_Y = 1480;

    private static final int MOBILE_X = 1250;
    private static final int MOBILE_Y = 1700;

    private static final int AMOUNT_X = 1350;
    private static final int AMOUNT_Y = 1950;

    private static final int PAYMENT_RECEIVED_DATE_X = 1350;
    private static final int PAYMENT_RECEIVED_DATE_Y = 2200;
    

    private static final int DONATION_DATE_X = 3500;
    private static final int DONATION_DATE_Y = 1480;

    private static final int PAYMENT_MODE_X = 3500;
    private static final int PAYMENT_MODE_Y = 1700;

    private static final int PURPOSE_X = 3500;
    private static final int PURPOSE_Y = 1900;

    private static final int REMARKS_X = 3500;
    private static final int REMARKS_Y = 2150;
    
    
    
    private static final int RECEIPT_NO_X = 300;
    private static final int RECEIPT_NO_Y = 1250;

    /*----------------------------------------------------------
     * Constructor
     *---------------------------------------------------------*/

    public DonationReceiptImageGenerator() {

        createFolders();

    }

    /*----------------------------------------------------------
     * Folder Creation
     *---------------------------------------------------------*/

    private void createFolders() {

        try {

            Files.createDirectories(Paths.get(JPG_FOLDER));

            Files.createDirectories(Paths.get(PNG_FOLDER));

        } catch (Exception e) {

            throw new RuntimeException(
                    "Unable to create receipt folders.",
                    e);

        }

    }

    /*----------------------------------------------------------
     * Generate Image
     *---------------------------------------------------------*/

    public File generateImage(
            DonationDto donation,
            String format)
            throws Exception {

        BufferedImage template = loadTemplate();

        Graphics2D g =
                template.createGraphics();

        prepareGraphics(g);

        drawFields(g, donation);

        g.dispose();

        File generatedFile = saveImage(
                template,
                donation,
                format);

        return generatedFile;

    }
    
    private File saveImage(
            BufferedImage image,
            DonationDto donation,
            String format) throws Exception {

        String extension =
                format.equalsIgnoreCase("PNG") ? "png" : "jpg";

        String folder =
                format.equalsIgnoreCase("PNG")
                        ? PNG_FOLDER
                        : JPG_FOLDER;

        File output = new File(
                folder + sanitize(donation.getName()) + "." + extension);

        ImageIO.write(image, extension, output);

        donation.setImageFile(output);

        return output;
    }

    /*----------------------------------------------------------
     * Load Template
     *---------------------------------------------------------*/

    private BufferedImage loadTemplate()
            throws Exception {

        File template =
                new File(TEMPLATE_PATH);

        if (!template.exists()) {

            throw new RuntimeException(
                    "Donation Template Not Found : "
                            + template.getAbsolutePath());

        }

        return ImageIO.read(template);

    }

    /*----------------------------------------------------------
     * Graphics Settings
     *---------------------------------------------------------*/

    private void prepareGraphics(
            Graphics2D g) {

        g.setColor(TEXT_COLOR);

        g.setFont(VALUE_FONT);

        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        g.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g.setRenderingHint(
                RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);

    }

    /*----------------------------------------------------------
     * File Name
     *---------------------------------------------------------*/

    private String sanitize(
            String name) {

        if (name == null
                || name.isBlank()) {

            return "Receipt";

        }

        return name.replaceAll(
                "[\\\\/:*?\"<>|]",
                "_");

    }
    
    /*----------------------------------------------------------
     * Draw All Dynamic Fields
     *---------------------------------------------------------*/

    private void drawFields(
            Graphics2D g,
            DonationDto donation) {

    	drawField(
    	        g,
    	        donation.getName(),
    	        NAME_X,
    	        NAME_Y);

        drawField(g,
                donation.getMobile(),
                MOBILE_X,
                MOBILE_Y);

        drawField(g,
                formatAmount(donation.getAmount()),
                AMOUNT_X,
                AMOUNT_Y);

        drawField(g,
                donation.getPaymentReceivedDate().toString(),
                PAYMENT_RECEIVED_DATE_X,
                PAYMENT_RECEIVED_DATE_Y);

        drawField(g,
                donation.getDonationDate().toString(),
                DONATION_DATE_X,
                DONATION_DATE_Y);

        drawField(g,
                donation.getPaymentMode(),
                PAYMENT_MODE_X,
                PAYMENT_MODE_Y);
        
        drawField(
                g,
                "Receipt No : "+donation.getReceiptNo(),
                RECEIPT_NO_X,
                RECEIPT_NO_Y);

        drawWrappedText(g,
                donation.getPurpose(),
                PURPOSE_X,
                PURPOSE_Y,
                650);

        drawWrappedText(g,
                donation.getRemarks(),
                REMARKS_X,
                REMARKS_Y,
                650);
        
        

    }

    /*----------------------------------------------------------
     * Draw Single Field
     *---------------------------------------------------------*/

    private void drawField(
            Graphics2D g,
            String value,
            int x,
            int y) {

        if (value == null) {
            value = "";
        }

        g.setFont(VALUE_FONT);
        g.setColor(TEXT_COLOR);

        g.drawString(value, x, y);

    }

    /*----------------------------------------------------------
     * Draw Wrapped Text
     *---------------------------------------------------------*/

    private void drawWrappedText(
            Graphics2D g,
            String text,
            int x,
            int y,
            int maxWidth) {

        if (text == null || text.isBlank()) {
            return;
        }

        g.setFont(VALUE_FONT);

        int lineHeight = g.getFontMetrics().getHeight();

        String[] words = text.split("\\s+");

        StringBuilder line = new StringBuilder();

        int currentY = y;

        for (String word : words) {

            String testLine =
                    line.length() == 0
                            ? word
                            : line + " " + word;

            int width =
                    g.getFontMetrics()
                            .stringWidth(testLine);

            if (width > maxWidth) {

                g.drawString(
                        line.toString(),
                        x,
                        currentY);

                line = new StringBuilder(word);

                currentY += lineHeight;

            } else {

                line = new StringBuilder(testLine);

            }

        }

        if (!line.isEmpty()) {

            g.drawString(
                    line.toString(),
                    x,
                    currentY);

        }

    }

    /*----------------------------------------------------------
     * Amount
     *---------------------------------------------------------*/

    private String formatAmount(
            Double amount) {

        if (amount == null) {

            return "₹ 0.00";

        }

        return String.format(
                "₹ %,.2f",
                amount);

    }
}