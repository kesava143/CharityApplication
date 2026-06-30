package com.example.service;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.example.entity.CertificateContactEntity;

@Service
public class CertificateService {

    private static final String TEMPLATE =
            "static/certificates/certificate-template.jpg";

    private static final String OUTPUT_FOLDER =
            "generated/certificates/";

    @Autowired
    private PdfService pdfService;

    @Autowired
    private CertificateProgressService progressService;

    @Autowired
    private WhatsAppService whatsAppService;

    /**
     * Constructor
     */
    public CertificateService() {

        createFolders();

    }

    /**
     * Create Output Folder
     */
    private void createFolders() {

        File folder = new File(OUTPUT_FOLDER);

        if (!folder.exists()) {

            folder.mkdirs();

        }

    }

    /**
     * Load Master Certificate
     */
    private BufferedImage loadTemplate()
            throws IOException {

        return ImageIO.read(

                new ClassPathResource(
                        TEMPLATE).getInputStream());

    }

    /**
     * Generate One Certificate Image
     */
    public File generateCertificateImage(

            CertificateContactEntity contact,

            String certificateDate)

            throws Exception {

        BufferedImage template = loadTemplate();

        Graphics2D g = template.createGraphics();

        g.setRenderingHint(

                RenderingHints.KEY_ANTIALIASING,

                RenderingHints.VALUE_ANTIALIAS_ON);

        g.setRenderingHint(

                RenderingHints.KEY_TEXT_ANTIALIASING,

                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        drawName(

                g,

                contact.getName(),

                template.getWidth());

        drawDate(

                g,

                certificateDate);

        g.dispose();

        String fileName =
                contact.getName()

                        .replaceAll("\\s+", "_")

                        + ".jpg";

        File output =

                new File(

                        OUTPUT_FOLDER +

                                fileName);

        ImageIO.write(

                template,

                "jpg",

                output);

        return output;

    }

    /**
     * Draw Name
     */
    private void drawName(

            Graphics2D g,

            String name,

            int imageWidth) {

        g.setColor(Color.BLACK);

        Font font = new Font(

                "Times New Roman",

                Font.BOLD,

                38);

        g.setFont(font);

        FontMetrics metrics =

                g.getFontMetrics(font);

        int width =

                metrics.stringWidth(

                        name.toUpperCase());

        /*
         * Adjust these coordinates
         * according to your certificate
         */

        int x =

                (imageWidth - width) / 2;

        int y = 635;

        g.drawString(

                name.toUpperCase(),

                x,

                y);

    }

    /**
     * Draw Date
     */
    private void drawDate(

            Graphics2D g,

            String date) {

        g.setColor(Color.BLACK);

        Font font =

                new Font(

                        "Arial",

                        Font.PLAIN,

                        24);

        g.setFont(font);

        /*
         * Adjust X & Y
         */

        int x = 1250;

        int y = 1015;

        g.drawString(

                date,

                x,

                y);

    }
    
    /**
     * Generate PDF for a single contact
     */
    public File generateCertificatePdf(
            CertificateContactEntity contact,
            String certificateDate) throws Exception {

        File imageFile =
                generateCertificateImage(contact, certificateDate);

        File pdfFile =
                pdfService.convertImageToPdf(imageFile);

        contact.setCertificateFile(
                pdfFile.getAbsolutePath());

        return pdfFile;

    }

    /**
     * Generate Certificates Only
     */
    public void generateCertificates(
            List<CertificateContactEntity> contacts,
            String certificateDate) {

        progressService.start(contacts.size());

        for (CertificateContactEntity contact : contacts) {

            try {

                progressService.setCurrentName(contact.getName());

                progressService.setCurrentDate(certificateDate);

                progressService.setStatus("Generating Certificate");

                File pdf =
                        generateCertificatePdf(
                                contact,
                                certificateDate);

                progressService.setCurrentPdf(
                        pdf.getName());

                contact.setStatus("GENERATED");

                progressService.markSuccess();

            } catch (Exception e) {

                contact.setStatus("FAILED");

                progressService.markFailed();

                e.printStackTrace();

            }

        }

        progressService.setStatus("Certificates Generated");

    }

    /**
     * Generate Certificates and Send WhatsApp
     */
    public void generateAndSend(
            List<CertificateContactEntity> contacts,
            String certificateDate,
            String message) {

        progressService.start(contacts.size());

        for (CertificateContactEntity contact : contacts) {

            try {

                progressService.setCurrentName(contact.getName());

                progressService.setCurrentDate(certificateDate);
                
                progressService.setCurrentMobile(
                        contact.getMobile());

                progressService.setStatus("Generating Certificate");

                File pdf =
                        generateCertificatePdf(
                                contact,
                                certificateDate);

                progressService.setCurrentPdf(pdf.getName());

                String personalizedMessage = message
                        .replace("{NAME}",
                                contact.getName())
                        .replace("{DATE}",
                                certificateDate);

                progressService.setStatus("Sending WhatsApp");

                boolean sent =
                        whatsAppService.sendMessageWithAttachment(
                                contact.getMobile(),
                                personalizedMessage,
                                pdf);

                if (sent) {

                    contact.setStatus("SENT");

                    progressService.markSuccess();

                } else {

                    contact.setStatus("FAILED");

                    progressService.markFailed();

                }

            } catch (Exception e) {

                contact.setStatus("FAILED");

                progressService.markFailed();

                e.printStackTrace();

            }

        }

        progressService.setStatus("Completed");

    }
    
    /**
     * Generate Certificates ZIP
     */
    public File getGeneratedZip() throws Exception {

        File sourceFolder = new File(OUTPUT_FOLDER);

        File zipFile = new File("generated/Certificates.zip");

        zipFolder(sourceFolder, zipFile);

        return zipFile;

    }

    /**
     * Zip Complete Folder
     */
    private void zipFolder(File sourceFolder,
                           File zipFile) throws Exception {

        try (java.io.FileOutputStream fos =
                     new java.io.FileOutputStream(zipFile);

             java.util.zip.ZipOutputStream zos =
                     new java.util.zip.ZipOutputStream(fos)) {

            File[] files = sourceFolder.listFiles();

            if (files == null) {
                return;
            }

            for (File file : files) {

                if (!file.isFile()) {
                    continue;
                }

                java.util.zip.ZipEntry entry =
                        new java.util.zip.ZipEntry(file.getName());

                zos.putNextEntry(entry);

                java.nio.file.Files.copy(
                        file.toPath(),
                        zos);

                zos.closeEntry();

            }

        }

    }

    /**
     * Delete Generated Files
     */
    public void deleteGeneratedFiles() {

        File folder = new File(OUTPUT_FOLDER);

        File[] files = folder.listFiles();

        if (files == null) {
            return;
        }

        for (File file : files) {

            try {

                file.delete();

            } catch (Exception ignored) {

            }

        }

    }

    /**
     * Get Generated Folder
     */
    public File getGeneratedFolder() {

        return new File(OUTPUT_FOLDER);

    }

    /**
     * Get Total Generated Files
     */
    public int getGeneratedCount() {

        File folder = new File(OUTPUT_FOLDER);

        File[] files = folder.listFiles();

        return files == null ? 0 : files.length;

    }

    /**
     * Check Template Exists
     */
    public boolean templateExists() {

        try {

            return new ClassPathResource(TEMPLATE).exists();

        } catch (Exception e) {

            return false;

        }

    }

    /**
     * Format Date
     */
    public String formatDate(String date) {

        try {

            LocalDate d = LocalDate.parse(date);

            return d.format(
                    DateTimeFormatter.ofPattern("dd-MMM-yyyy"));

        } catch (Exception e) {

            return date;

        }

    }

    /**
     * Build Personalized Message
     */
    public String buildMessage(
            String template,
            CertificateContactEntity contact,
            String date) {

        return template

                .replace("{NAME}",
                        contact.getName())

                .replace("{DATE}",
                        date);

    }

    /**
     * Get PDF Name
     */
    public String getPdfName(
            CertificateContactEntity contact) {

        return contact.getName()

                .replaceAll("\\s+", "_")

                + ".pdf";

    }

}
