package com.youtube;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java -jar <jar-file> <size-in-mb> (e.g. 47.7 or 48)");
            return;
        }
        // Accept decimal MB, allow comma as decimal separator
        final String rawArg = args[0].trim();
        final String raw = rawArg.replace(',', '.');
        double mbDouble;
        try {
            mbDouble = Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number: " + args[0]);
            return;
        }

        // If the user passed an integer (no dot/comma) treat it as decimal MB so Explorer shows that value when rounded.
        boolean integerInput = !(rawArg.contains(".") || rawArg.contains(","));
        long targetBytes;
        String targetMode;
        if (integerInput) {
            targetBytes = Math.round(mbDouble * 1_000_000.0);
            targetMode = "decimal (Explorer)";
        } else {
            targetBytes = Math.round(mbDouble * 1024.0 * 1024.0);
            targetMode = "binary (MiB)";
        }
        File out = new File("dummy.pdf");

        // Create a minimal PDF once (small content)
        createMinimalPdf(out);

        long current = out.length();
        if (current > targetBytes) {
            System.out.println("Generated PDF is already larger (" + current + " bytes) than target (" + targetBytes + " bytes). Try smaller MB.");
            System.out.println("Current size: " + current + " bytes");
            return;
        }

        long remaining = targetBytes - current;
        if (remaining > 0) {
            // Append zeros in chunks to reach exact size
            try (FileOutputStream fos = new FileOutputStream(out, true)) {
                byte[] buf = new byte[1024 * 1024]; // 1 MB buffer
                while (remaining > 0) {
                    int toWrite = (int) Math.min(buf.length, remaining);
                    fos.write(buf, 0, toWrite);
                    remaining -= toWrite;
                }
            }
        }

    long finalSize = out.length();
    // Rename output file to include its final size in decimal MB (two decimals), e.g. dummy-48.86MB.pdf
    String fileName;
    try {
        long bytes = finalSize;
        double mbFinal = bytes / 1_000_000.0; // decimal MB
        String newName = String.format(Locale.US, "dummy-%.2fMB.pdf", mbFinal);
        File renamed = new File(out.getParentFile(), newName);
        if (out.renameTo(renamed)) {
            fileName = renamed.getName();
            out = renamed;
        } else {
            fileName = out.getName();
        }
    } catch (Exception e) {
        fileName = out.getName();
    }
    long bytes = finalSize;
    double mib = bytes / 1048576.0;
    double mb = bytes / 1_000_000.0;
    long rounded = Math.round(mb);

    String separator = "============================================================";

    System.out.println(separator);
    System.out.println("📄 " + fileName + " — Final Size");
    System.out.println("──────────────────────────────────────────────");

    System.out.printf("• Binary (MiB):         %.2f MiB   (1 MiB = 1,048,576 bytes)%n", mib);
    System.out.println();

    System.out.printf("• Bytes:                %,d%n", bytes);
    System.out.println();

    System.out.printf("• Decimal (MB):         %.2f MB    (1 MB  = 1,000,000 bytes)%n", mb);
    System.out.println();

    System.out.printf("• Explorer/OS actual size display:  %d MB (rounded)%n", rounded);

    System.out.println(separator);
    System.out.println("(Target mode used: " + targetMode + ")");
    }

    private static void createMinimalPdf(File file) throws FileNotFoundException {
        WriterProperties props = new WriterProperties();
        props.setCompressionLevel(0);
        props.setFullCompressionMode(false);
        PdfWriter writer = new PdfWriter(file.getAbsolutePath(), props);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);
        doc.add(new Paragraph("Dummy PDF generated for exact-size padding."));
        doc.close();
    }
}