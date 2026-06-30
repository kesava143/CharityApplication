package com.example.service;

import java.io.File;

import org.springframework.stereotype.Service;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;

@Service
public class PdfService {

    /**
     * Convert JPG/PNG Certificate to PDF
     */
    public File convertImageToPdf(File imageFile) throws Exception {

        if (imageFile == null || !imageFile.exists()) {
            throw new Exception("Certificate image not found.");
        }

        String pdfPath = imageFile.getAbsolutePath()
                .replace(".jpg", ".pdf")
                .replace(".jpeg", ".pdf")
                .replace(".png", ".pdf");

        File pdfFile = new File(pdfPath);

        PdfWriter writer = new PdfWriter(pdfFile);

        PdfDocument pdfDocument = new PdfDocument(writer);

        PageSize pageSize = PageSize.A4.rotate();

        Document document = new Document(pdfDocument, pageSize);

        document.setMargins(0, 0, 0, 0);

        ImageData imageData =
                ImageDataFactory.create(imageFile.getAbsolutePath());

        Image image = new Image(imageData);

        float pageWidth =
                pageSize.getWidth();

        float pageHeight =
                pageSize.getHeight();

        image.scaleToFit(pageWidth, pageHeight);

        float x =
                (pageWidth - image.getImageScaledWidth()) / 2;

        float y =
                (pageHeight - image.getImageScaledHeight()) / 2;

        image.setFixedPosition(x, y);

        document.add(image);

        document.close();

        pdfDocument.close();

        writer.close();

        return pdfFile;

    }

}